/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.javausagesnavigator;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jmi.reflect.RefObject;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.ClassMember;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.java.JavaEditor;
import org.netbeans.modules.javacore.ClassIndex;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.netbeans.modules.javacore.jmiimpl.javamodel.UsageFinder;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.text.PositionBounds;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 * Navigator panel which shows usages of class members 
 *
 * @author Tim Boudreau
 */
public final class UsagesTreePanel implements NavigatorPanel, LookupListener {
    
    /** UI of this navigator panel */ 
    private UsagesTreePanelUI panelUI;
    /** model actually containing content of this panel */ 
    private UsagesRoot curModel;
    /** current context to work on */
    private Lookup.Result curContext;
    /** actual data */
    private JavaDataObject curData;

    public String getDisplayName () {
        return NbBundle.getBundle(UsagesTreePanel.class).getString("LBL_Usages"); //NOI18N
    }
    
    public String getDisplayHint () {
        // XXX - TBD
        return null;
    }
    
    public JComponent getComponent () {
        return getPanelUI();
    }
    
    private static final Lookup.Template tpl = new Lookup.Template (JavaDataObject.class);
    /** Creates and activates InheritanceTreeModel model for given context.
     */
    public void panelActivated (Lookup context) {
        curContext = context.lookup(tpl);
        curContext.addLookupListener(this);
        curData = (JavaDataObject)(((List)curContext.allInstances()).get(0));
        setNewContent(curData);
        panelUI.componentActivated();
    }
    
    /** Deactivates inheritance tree model.
     */
    public void panelDeactivated () {
        curContext.removeLookupListener(this);
        curContext = null;
//        curModel.removeNotify();
        curModel = null;
        curData = null;
        panelUI.componentDeactivated();
    }

    /** Impl of LookupListener, reacts to changes of context */
    public void resultChanged (LookupEvent ev) {
        Collection data = ((Lookup.Result)ev.getSource()).allInstances();
        if (!data.isEmpty()) {
            JavaDataObject jdo = (JavaDataObject)data.iterator().next();
            if (!jdo.equals(curData)) {
                curData = jdo;
                setNewContent(jdo);
            }
        }
    }
    
    /** Default activated Node strategy is enough for now */
    public Lookup getLookup () {
        return null;
    }
    
    /************ non public stuff **********/
    
    private void setNewContent (JavaDataObject jdo) {
        curModel = new UsagesRoot(jdo);
        getPanelUI().setContext(curModel);
//        curModel.addNotify();
    }
    
    /** Accessor for instance handling UI */
    private UsagesTreePanelUI getPanelUI () {
        if (panelUI == null) {
            panelUI = new UsagesTreePanelUI();
        }
        return panelUI;
    }
    
    private static class UsagesTreePanelUI extends JPanel implements ExplorerManager.Provider {
        private final ExplorerManager manager = new ExplorerManager();
        private final BeanTreeView view = new BeanTreeView();
        public UsagesTreePanelUI() {
            // Or however you want your GUI to look:
            setLayout (new BorderLayout());
            add(view, BorderLayout.CENTER);

            view.setRootVisible(false);
            // Can set up initial selected nodes too:
            manager.setRootContext(new WaitNode());
            // Probably boilerplate (depends on what you are doing):
            ActionMap map = getActionMap();
            map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
            map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
            map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
            // This one is sometimes changed to say "false":
            map.put("delete", ExplorerUtils.actionDelete(manager, true));
            // Boilerplate:
        }
        // This is optional:
        public boolean requestFocusInWindow() {
            super.requestFocusInWindow();
            // You will need to pick a view to focus:
            return view.requestFocusInWindow();
        }
        // The rest is boilerplate.
        public ExplorerManager getExplorerManager() {
            return manager;
        }
        protected void componentActivated() {
            ExplorerUtils.activateActions(manager, true);
        }
        protected void componentDeactivated() {
            ExplorerUtils.activateActions(manager, false);
        } 
        
        public void setContext (Node n) {
            manager.setRootContext(n);
        }
    }
    
    private static final RequestProcessor rp = new RequestProcessor();
    
    private static class UsagesRoot extends AbstractNode {
        final JavaDataObject jdo;
        UsagesRoot (JavaDataObject jdo) {
            super (new UC (jdo));
            this.jdo = jdo;
            setDisplayName (jdo.getName());
            setIconBaseWithExtension (iconFor(jdo));
        }

        UsagesRoot (ClassDefinition clazz, JavaDataObject jdo) {
            super (new UC (clazz, jdo));
            this.jdo = jdo;
            setDisplayName (clazz instanceof JavaClass ? ((JavaClass) clazz).getSimpleName() : clazz.getName());
            setIconBaseWithExtension (iconFor(clazz));
        }
    }
    
    private static class UC extends Children.Keys implements Runnable {
        private final JavaDataObject jdo;
        public UC (JavaDataObject jdo) {
            this.jdo = jdo;
        }
        
        private ClassDefinition clazz;
        public UC (ClassDefinition clazz, JavaDataObject jdo) {
            this(jdo);
            this.clazz = clazz;
        }
        
        Task task = null;
        
        public void addNotify() {
            setKeys (Collections.singleton(new WaitNode()));
            synchronized (this) {
                task = rp.post(this);
            }
        }
        
        public void removeNotify() {
            if (!initialized) {
                synchronized (this) {
                    if (task != null) {
                        task.cancel();
                    }
                }
            }
        }

        protected Node[] createNodes(Object key) {
            if (key instanceof WaitNode) {
                return new Node[] { (WaitNode) key };
            } else if (key instanceof ClassDefinition) {
                return new Node[] { new UsagesRoot ((ClassDefinition) key, jdo) };
            } else {
                ClassMember mem = (ClassMember) key;
                return new Node[] {new MemberNode (mem, jdo)};
            }
        }
        
        private volatile boolean initialized = false;
        public void run() {
            try {
                ClassDefinition[] cl = clazz == null ? classFor (jdo) : new ClassDefinition[] { clazz };
                List keys;
                if (cl.length == 1) {
                    List l = cl[0].getChildren();
                    keys = new ArrayList(l.size());
                    for (Iterator i=l.iterator();i.hasNext();) {
                        Object o = i.next();
                        if (o instanceof Method || o instanceof Field || o instanceof ClassDefinition) {
                            keys.add (o);
                        }
                    }
                } else if (cl.length > 1) {
                    //JDK 1.0 style sources - will get multiple
                    //class nodes as children.  Not nice, but neither
                    //are JDK 1.0 style sources.
                    keys = (Arrays.asList((cl)));
                } else {
                    keys = Collections.EMPTY_LIST;
                }
                setKeys (keys);
            } finally {
                synchronized (this) {
                    task = null;
                    initialized = true;
                }
            }
        }
    }
    
    private static class MemberNode extends AbstractNode {
        private final ClassMember member;
        public MemberNode (ClassMember member, JavaDataObject dob) {
            super (new MemberChildren (member, dob));
            this.member = member;
            setDisplayName(displayNameFor (member, dob));
            setName (member.getName());
            setIconBaseWithExtension (iconFor(member));
        }
        
        public Action[] getActions(boolean context) {
            return new Action[] {
                new OC(member)
            };
        }
        
        public Action getPreferredAction() {
            return getActions(false)[0];
        }
    } 
    
    private static String displayNameFor (ClassMember member, JavaDataObject dob) {
        String base = member instanceof Constructor ? ((JavaClass)member.getDeclaringClass()).getSimpleName() : member.getName();
        return getDataObject(member) == dob ? base : ((ClassDefinition) member.refImmediateComposite()).getName() + "." + base;
    }
        
    private static final class OC extends AbstractAction {
        private ClassMember member;
        public OC (ClassMember member) {
            this.member = member;
            putValue (Action.NAME, "Go To Source"); //XXX I18N
        }
    
        public void actionPerformed(ActionEvent ae) {
            try {
                DataObject d = getDataObject(member);
                if (d != null) {
                    PositionBounds bounds = JavaMetamodel.getManager().getElementPosition(member);
                    if (bounds != null) {
                        JavaEditor ed = ((JavaEditor) d.getCookie(JavaEditor.class));
                        ed.openAtPosition(bounds.getBegin());
                        return;
                    }
                }
            } catch (javax.jmi.reflect.InvalidObjectException e) {
            }
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private static final DataObject getDataObject(ClassMember member) {
        Resource r = member.getResource();
        FileObject fob = JavaModel.getFileObject(r);
        if (fob != null && fob.isValid()) {
            try {
                return DataObject.find (fob);
            } catch (DataObjectNotFoundException donfe) {
                //do nothing
            }
        }
        return null;
    }
    
    
    
    private static class MemberChildren extends Children.Keys implements Runnable {
        private final ClassMember member;
        private final JavaDataObject editing;
        public MemberChildren (ClassMember member, JavaDataObject jdo) {
            this.member = member;
            editing = jdo;
        }
        
        private volatile boolean active = false;
        private Task task = null;
        public void addNotify() {
            setKeys (Collections.singleton (new WaitNode()));
            synchronized (this) {
                Task task = rp.post (this);
            }
            active = true;
        }
        
        public void removeNotify() {
            synchronized (this) {
                if (task != null) {
                    task.cancel();
                }
            }
            active = false;
        }
        
        protected Node[] createNodes(Object key) {
            if (key instanceof WaitNode) {
                return new Node[] { (Node) key };
            } else {
                return new Node[] { new MemberNode ((ClassMember) key, editing) };
            }
        }

        public void run() {
            try {
                Collection c = findDependencies(member, true, false, false);
                Set l = new HashSet(c.size());
                for (Iterator it = c.iterator(); it.hasNext();) {
                    Object elem = (Object) it.next();
                    elem = findClassMember (elem);
                    if (elem != null && (elem instanceof Method || elem instanceof Field || elem instanceof Constructor)) {
                        l.add (elem);
                    }
                }
                setKeys (l);
            } finally {
                synchronized (this) {
                    task = null;
                }
            }
        }
    }
    
    private static class WaitNode extends AbstractNode {
        public WaitNode () {
            super (Children.LEAF);
            setDisplayName("Please wait..."); //XXX I18N
            setIconBaseWithExtension(iconFor(this));
        }
    }
    
    private static ClassDefinition[] classFor (JavaDataObject dob) {
        Resource res = getResourceForDataObject (dob);
        if (res == null) {
            return null;
        }
        ArrayList al = new ArrayList(1);
        for (Iterator i=res.getClassifiers().iterator(); i.hasNext();) {
            //XXX deal with JDK 1.0 sources
//            return (JavaClass) i.next();
            al.add (i.next());
        }
        
        return (ClassDefinition[]) al.toArray(new ClassDefinition[al.size()]);
    }
    
    //******************
    //Everything from here copied from java/navigation/JUtils
    
    public static Resource getResourceForDataObject (JavaDataObject dob) {
        return dob.isValid () ?
                JavaModel.getResource ( dob.getPrimaryFile () ) :
                null;
    }       
    
   /**
     * Given an element representing some part of a method body or such, find the nearest enclosing ClassMember, or at
     * least try to.
     */
    public static Object findClassMember (Object o) {
        int ct = 0;
        //Limit it so we don't send this thread into an endless loop
        while ( notAClassMember ( o ) && ct < 10 ) {
            o = findOuter ( o );
            if (o == null) {
                break;
            }
            ct++;
        }
        return o;
    }

    private static Object findOuter (Object o) {
        while (!(o instanceof ClassMember) && o != null) {
          o = ((RefObject) o).refImmediateComposite();
        }
        return o;
    }

    private static boolean notAClassMember (Object o) {
        return !( o instanceof ClassMember );
    }    
    
    public static String iconFor (Object o) {
        String key = "";
        boolean washOut = false;

        if ( o instanceof Method ) {
            Method m = (Method) o;
            boolean stat = ( m.getModifiers () & Modifier.STATIC ) != 0;

            if ( ( m.getModifiers () & Modifier.PUBLIC ) != 0 ) {
                key = stat ? "methodStPublic" : "methodPublic"; //NOI18N
            } else if ( ( m.getModifiers () & Modifier.PROTECTED ) != 0 ) {
                key = stat ? "methodStProtected" : "methodProtected"; //NOI18N
            } else if ( ( m.getModifiers () & Modifier.PRIVATE ) != 0 ) {
                key = stat ? "methodStPrivate" : "methodPrivate"; //NOI18N
            } else {
                key = stat ? "methodStPackage" : "methodPackage"; //NOI18N
            }
        } else if ( o instanceof Constructor ) {
            Constructor c = (Constructor) o;

            if ( ( c.getModifiers () & Modifier.PUBLIC ) != 0 ) {
                key = "constructorPublic"; //NOI18N
            } else if ( ( c.getModifiers () & Modifier.PROTECTED ) != 0 ) {
                key = "constructorProtected"; //NOI18N
            } else if ( ( c.getModifiers () & Modifier.PRIVATE ) != 0 ) {
                key = "constructorPrivate"; //NOI18N
            } else {
                key = "constructorPackage"; //NOI18N
            }
        } else if ( o instanceof Field ) {
            Field f = (Field) o;
            boolean stat = ( f.getModifiers () & Modifier.STATIC ) != 0;

            if ( ( f.getModifiers () & Modifier.PUBLIC ) != 0 ) {
                key = stat ? "variableStPublic" : "variablePublic"; //NOI18N
            } else if ( ( f.getModifiers () & Modifier.PROTECTED ) != 0 ) {
                key = stat ? "variableStProtected" : "variableProtected"; //NOI18N
            } else if ( ( f.getModifiers () & Modifier.PRIVATE ) != 0 ) {
                key = stat ? "variableStPrivate" : "variablePrivate"; //NOI18N
            } else {
                key = stat ? "variableStPackage" : "variablePackage"; //NOI18N
            }
        } else if ( o instanceof JavaClass ) {
            JavaClass jc = (JavaClass) o;

            if ( jc.isInterface () ) {
                key = "interface";
            } else {
                key = "class2";
            }
        } else if ( o instanceof DataObject ) {
            key = "class";
        } else if ( o instanceof WaitNode ) {
            key = "wait";
        }

        return "org/netbeans/modules/javausagesnavigator/resources/" + key + //NOI18N
                    ".gif"; //NOI18N
    }  
    
    //XXX need this?
    private static final Map icons = new HashMap();
    
    /**
     * Generate a lightened version of an image
     */
    private static Image washOutImage (Image i) {
        Image result = toBufferedImage ( i );
        Graphics2D g = (Graphics2D) result.getGraphics ();
        g.setComposite ( AlphaComposite.getInstance ( AlphaComposite.SRC_ATOP, 0.40f ) );

        g.fillRect ( 0, 0, 16, 16 );
        return result;
    }


    /**
     * Image management junk stolen from IconManager
     */
    private static final Image toBufferedImage (Image img) {
        new javax.swing.ImageIcon ( img );
        java.awt.image.BufferedImage rep =
                createBufferedImage ( img.getWidth ( null ), img.getHeight ( null ) );

        java.awt.Graphics g = rep.createGraphics ();
        g.drawImage ( img, 0, 0, null );
        g.dispose ();
        img.flush ();
        return rep;
    }


    /**
     * More image management junk stolen from IconManager
     */
    private static final BufferedImage createBufferedImage (int width, int height) {

        ColorModel model = GraphicsEnvironment.getLocalGraphicsEnvironment ().
                getDefaultScreenDevice ().
                getDefaultConfiguration ().
                getColorModel ( java.awt.Transparency.BITMASK );

        BufferedImage result = new java.awt.image.BufferedImage ( model,
                model.createCompatibleWritableRaster ( width, height ),
                model.isAlphaPremultiplied (), null );

        return result;
    }   
    
    //******************
    //Everything from here is copied from CallableFeatureImpl
    
    public static Collection findDependencies(ClassMember member, boolean findUsages, boolean fromBaseClass, boolean findOverridingMethods) {
        Resource[] res = findReferencedResources(member, true);
        
        if (!fromBaseClass || (fromBaseClass && member instanceof CallableFeature && !isOverriden((CallableFeature)member))) {
            Element cd = member.getDeclaringClass();
            boolean isPrivate = false;
            while (cd != null && !(cd instanceof Resource)) {
                if (cd instanceof JavaClass) {
                    int m = ((JavaClass) cd).getModifiers();
                    if (!Modifier.isPublic(m) && !Modifier.isProtected(m)) {
                        isPrivate = true;
                        break;
                    }
                }
                cd = (Element) cd.refImmediateComposite();
            }
            if (isPrivate) {
                res = filterResourcesFromThisPackage(member, res);
            }
        }

        UsageFinder finder = member instanceof CallableFeature ? 
            new UsageFinder((CallableFeature) member, findUsages, fromBaseClass, findOverridingMethods) :
            new UsageFinder (member);
        return finder.getUsers(res);
    }  
    
    private static final Resource[] findReferencedResources(ClassMember member, boolean includeLibraries) {
        int modifiers=member.getModifiers();
        String name;
        Resource[] res;
        ClassDefinition clazz = member instanceof ClassDefinition ?
            (ClassDefinition) member : (ClassDefinition) member.refImmediateComposite();

        if (Modifier.isPrivate(modifiers)) {
            return new Resource[] {member.getResource()};
        }
        if (member instanceof JavaClass) {
            name=((JavaClass)clazz).getSimpleName();
        } else if (member instanceof Constructor) {
            name=((JavaClass)member.getDeclaringClass()).getSimpleName();
        } else {
            name=member.getName();
        }
        res=ClassIndex.findResourcesForIdentifier(name, includeLibraries);
        
        if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
            if (member instanceof Field) {
                Element cd = member.getDeclaringClass();
                boolean isPrivate = false;
                boolean isPackagePrivate = false;
                while (cd != null && !(cd instanceof Resource)) {
                    if (cd instanceof JavaClass) {
                        int m = ((JavaClass) cd).getModifiers();
                        if (Modifier.isPrivate(m)) {
                            isPrivate = true;
                            break;
                        } else if (!isPackagePrivate && !Modifier.isPublic(m) && !Modifier.isProtected(m)) {
                            isPackagePrivate = true;
                        }
                    }
                    cd = (Element) cd.refImmediateComposite();
                }
                if (isPrivate) {
                    return new Resource[] {member.getResource()};
                } else if (isPackagePrivate) {
                    return filterResourcesFromThisPackage(member, res);
                }
            }
            return res;
        }
        return filterResourcesFromThisPackage(member, res);
    }
    
    private static final Resource[] filterResourcesFromThisPackage(ClassMember member, Resource[] res) {
        List filteredResources=new ArrayList(res.length);
        String packageName=member.getResource().getPackageName();
        for (int i=0;i<res.length;i++) {
            Resource r=res[i];

            if (r.getPackageName().equals(packageName))
                filteredResources.add(r);
        }
        return (Resource[])filteredResources.toArray(new Resource[filteredResources.size()]);
    }    
    
    private static final boolean isOverriden(CallableFeature member) {
        if (!(member instanceof Method))
            return false;
        
        ClassDefinition declaringClass = member.getDeclaringClass();
        
        List params = new ArrayList();
        for (Iterator i = member.getParameters().iterator(); i.hasNext(); params.add(((Parameter)i.next()).getType()));
        
        ClassDefinition parent = declaringClass.getSuperClass();
        Method m = parent.getMethod(member.getName(), params, true);
        if (m!=null) {
            return true;
        }
        Iterator i = declaringClass.getInterfaces().iterator();
        while (i.hasNext()) {
            ClassDefinition jc = (ClassDefinition) i.next();
            m = jc.getMethod(member.getName(), params, true);
            if (m!=null) {
                return true;
            }
        }
        return false;
    }   
}

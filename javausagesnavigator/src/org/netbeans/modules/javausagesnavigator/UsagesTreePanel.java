/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import javax.jmi.reflect.InvalidObjectException;
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
import org.openide.ErrorManager;
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
            super (new UC (jdo), jdo.getNodeDelegate().getLookup());
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
        private boolean top;
        public UC (JavaDataObject jdo) {
            this.jdo = jdo;
            top = true;
        }
        
        private ClassDefinition clazz;
        public UC (ClassDefinition clazz, JavaDataObject jdo) {
            this(jdo);
            this.clazz = clazz;
            top = false;
        }
        
        Task task = null;
        
        public void addNotify() {
            setKeys (Collections.singleton(new WaitNode()));
            synchronized (this) {
                if (task == null) {
                    task = rp.post(this);
                }
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
                return new Node[] {new MemberNode (mem, clazz, jdo, top)};
            }
        }
        
        private volatile boolean initialized = false;
        public void run() {
            try {
                ClassDefinition[] cl = clazz == null ? classFor (jdo) : new ClassDefinition[] { clazz };
                List keys;
                if (cl.length == 1) {
                    clazz = cl[0];
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
        public MemberNode (ClassMember member, ClassDefinition clazz, JavaDataObject dob, boolean top) {
            super (new MemberChildren (member, clazz, dob, top), dob.getNodeDelegate().getLookup());
            this.member = member;
            try {
                setDisplayName(displayNameFor (member, clazz, dob));
                setName (member.getName());
                setIconBaseWithExtension (iconFor(member));
            } catch (InvalidObjectException exe) {
                setDisplayName ("Invalid...");
            }

            Children kids = getChildren();
            if (kids instanceof MemberChildren) {
                //Theoretically possible for the thread to be started from
                //member children constructor and already be set to something
                //else here.  But pretty unlikely.
                ((MemberChildren) kids).node = this;
            }
        }
        
        public Action[] getActions(boolean context) {
            return new Action[] {
                new OC(member)
            };
        }
        
        public Action getPreferredAction() {
            return getActions(false)[0];
        }
        
        boolean knownLeaf = false;
        void becomeLeaf () {
            setChildren (Children.LEAF);
            knownLeaf = true;
            String s = getDisplayName();
            fireDisplayNameChange(getDisplayName(), "<font color='gray'>" + getDisplayName());
        }
        
        public String getHtmlDisplayName() {
            return knownLeaf ?
                "<font color=\"!controlShadow\">" + getDisplayName() : null;
        }
    }
    
    private static RequestProcessor rp2 = new RequestProcessor ();
    private static void enqueue (MemberChildren children) {
        synchronized (children) {
            if (children.task == null) {
                children.setQueueTask (rp2.post (children));
            }
        }
    }
    
    private static class MemberChildren extends Children.Keys implements Runnable {
        private final ClassMember member;
        private final JavaDataObject editing;
        private final ClassDefinition on;
        public MemberChildren (ClassMember member, ClassDefinition clazz, JavaDataObject jdo, boolean top) {
            this.member = member;
            editing = jdo;
            on = clazz;
            if (top) {
                enqueue (this);
            }
        }
        MemberNode node = null;
        
        private Task queueTask = null;
        synchronized void setQueueTask (Task task) {
            queueTask = task;
        }

        private volatile boolean active = false;
        private Task task = null;
        public void addNotify() {
            setKeys (Collections.singleton (new WaitNode()));
            if (cachedKeys != null) {
                setKeys (cachedKeys);
            } else {
                if (!active) {
                    synchronized (this) {
                        if (task == null) {
                            task = rp.post (this);
                        }
                    }
                }
            }
        }

        private volatile boolean alive = false;
        public void removeNotify() {
            active = false;
            alive = false;
            synchronized (this) {
                if (task != null) {
                    task.cancel();
                    task = null;
                }
                if (queueTask != null) {
                    queueTask.cancel();
                    queueTask = null;
                }
            }
        }

        protected Node[] createNodes(Object key) {
            if (key instanceof WaitNode) {
                return new Node[] { (Node) key };
            } else {
                return new Node[] { new MemberNode ((ClassMember) key, on, editing, true) };
            }
        }

        private Collection cachedKeys = null;
        public void run() {
            try {
                alive = true;
                synchronized (this) {
                    if (rp.isRequestProcessorThread() && queueTask != null) {
                        queueTask.cancel();
                        queueTask = null;
                    }
                }
                if (!alive) {
                    return;
                }
                //XXX cannot use the transaction lock here, it will deadlock
                //with the editor
//                JavaMetamodel.getDefaultRepository().beginTrans(false);
                Set l = new HashSet();
                try {
                    //Getting some InvalidObjectExceptions despite the transaction lock,
                    //from the usagesfinder iterator.  Try to reduce by pre-cooking a new
                    //collection
                    Collection c;
                    try {
                        c = new ArrayList (findDependencies(member, true, false, false));
                    } catch (InvalidObjectException ioe) {
                        try {
                            //UsagesFinder bug - give it another shot
                            c = new ArrayList (findDependencies(member, true, false, false));
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
                        } catch (InvalidObjectException ioe2) {
                            c = Collections.EMPTY_LIST;
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe2);
                        }
                    }
                    for (Iterator it = c.iterator(); it.hasNext();) {
                        Object elem = (Object) it.next();
                        elem = findClassMember (elem);
                        if (elem != null && (elem instanceof Method || elem instanceof Field || elem instanceof Constructor)) {
                            l.add (elem);
                        }
                        if (!alive) {
                            return;
                        }
                    }
                } finally {
//                    JavaMetamodel.getDefaultRepository().endTrans();
                }
                if (l.isEmpty() && node != null) {
                    node.becomeLeaf ();
                } else {
                    cachedKeys = l;
                    setKeys (l);
                }
            } finally {
                active = true;
                synchronized (this) {
                    task = null;
                    queueTask = null;
                }
            }
        }
    }
    
    private static void recurseDisplayNameFor (JavaClass def, ClassDefinition last, StringBuffer s) {
        if (def == null) {
            return;
        }
        String cname = def.getSimpleName();
        
        s.insert (0, cname + ".");
        if (def.getDeclaringClass() != null) {
            JavaClass owner = (JavaClass) def.getDeclaringClass();
            if (owner != last) {
                recurseDisplayNameFor (owner, last, s);
            }
        }
    }
    
    private static String displayNameFor (ClassMember member, ClassDefinition expectedOn, JavaDataObject dob) {
        if (member instanceof ClassDefinition || !(member.getDeclaringClass() instanceof JavaClass)) {
            //What is a ClassDefinition$Impl, anyway?
            return member.getName();
        }
        JavaClass declaring = (JavaClass) member.getDeclaringClass();
        String base = member instanceof Constructor ? (
                (JavaClass)member.getDeclaringClass()).getSimpleName() : 
                member.getName();
        
        boolean differentFile = dob != getDataObject(member);
        if (differentFile || declaring != expectedOn) {
            StringBuffer sb = new StringBuffer(base);
            recurseDisplayNameFor (declaring, expectedOn, sb);
            return sb.toString();
        }
        return base;
    }
    
    private static String minimize (String name) {
        if (name == null) {
            return "?";
        }
        if (name.length() <= 4) {
            return name;
        }
        char[] c = name.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < c.length; i++) {
            if (Character.isUpperCase(c[i])) {
                sb.append (c[i]);
            }
        }
        if (sb.length() == 0) {
            return name;
        }
        return sb.toString();
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

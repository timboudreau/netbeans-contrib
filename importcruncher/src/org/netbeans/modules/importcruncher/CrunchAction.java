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
package org.netbeans.modules.importcruncher;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.ArrayReference;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Import;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.JavaModelPackage;
import org.netbeans.jmi.javamodel.MultipartId;
import org.netbeans.jmi.javamodel.NamedElement;
import org.netbeans.jmi.javamodel.PrimitiveType;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.UnresolvedClass;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.netbeans.modules.javacore.jmiimpl.javamodel.DiffElement;
import org.netbeans.modules.javacore.jmiimpl.javamodel.ResourceImpl;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;


public final class CrunchAction extends CookieAction implements Comparator {

    protected void performAction(Node[] n) {
        Prefs prefs = new Prefs();
        if (prefs.isShowDialog()) {
            boolean proceed = prefs.showDialog(true);
            if (proceed) {
                breakUp = prefs.isBreakup();
                eliminateFQNs = prefs.isEliminateFqns();
                eliminateWildcards = prefs.isEliminateWildcards();
                sort = prefs.isSort();
            } else {
                return;
            }
        }
        ProgressHandle handle = ProgressHandleFactory.createHandle("Crunching imports...");
        handle.start(100);
        assert n.length == 1;
        try {
            handle.progress(1);
            Document d = ((EditorCookie) n[0].getCookie(EditorCookie.class)).getDocument();
            process (d, handle);
        } finally {
            handle.finish();
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return new Prefs().isShowDialog() ?
            NbBundle.getMessage(CrunchAction.class, "LBL_Action_Dlg") : //NOI18N
            NbBundle.getMessage(CrunchAction.class, "LBL_Action"); //NOI18N
    }

    public String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean isEnabled() {
        if (super.isEnabled()) {
            if (JavaMetamodel.getManager().isScanInProgress()) {
                return false;
            }
            Node[] nn = TopComponent.getRegistry().getActivatedNodes();
            if (nn.length != 1) {
                return false;
            }
            Node n = nn[0];
            JEditorPane[] panes = 
                    ((EditorCookie) n.getCookie(EditorCookie.class)).getOpenedPanes();
            if (panes != null && panes.length > 0) {
                DataObject dob = (DataObject) n.getCookie(DataObject.class);
                if (dob == null || !dob.isValid() || !dob.getPrimaryFile().canWrite()) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    protected Class[] cookieClasses() {
        return new Class[] {
            JavaDataObject.class,
            EditorCookie.class
        };
    }

    protected boolean asynchronous() {
        return true;
    }
    
    private boolean eliminateWildcards = true;
    private boolean eliminateFQNs = true;
    private boolean sort = true;
    private boolean breakUp = true;


    private void process(final Document d, final ProgressHandle ph) {
        if (!(d instanceof BaseDocument)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        JMIUtils utils = JMIUtils.get((BaseDocument)d);
        final Resource resource = utils.getResource();

        if (resource == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        process (resource, d, ph);
    }
    
    private void process(Resource r, Document d, ProgressHandle ph) {
        ImportProcessor ip = new ImportProcessor (r, d, ph);
        ph.progress(1);
        if (eliminateWildcards) {
            JavaMetamodel.getDefaultRepository().beginTrans(true);
            ip.setMode (ImportProcessor.ELIMINATE_WILDCARDS);
            try {
                ((BaseDocument) d).runAtomic(ip);
            } finally {
                JavaMetamodel.getDefaultRepository().endTrans();
            }
        }
        ph.progress (20);
        if (eliminateFQNs) {
            ip.setMode(ImportProcessor.ELIMINATE_FQNS);
            JavaMetamodel.getDefaultRepository().beginTrans(true);
            try {
                ((BaseDocument) d).runAtomic(ip);
            } finally {
                JavaMetamodel.getDefaultRepository().endTrans();
            }
            ph.progress (40);
            ip.setMode(ImportProcessor.REPLACE_IMPORTS);
            JavaMetamodel.getDefaultRepository().beginTrans(true);
            try {
                ((BaseDocument) d).runAtomic(ip);
            } finally {
                JavaMetamodel.getDefaultRepository().endTrans();
            }
        }
        ph.progress(80);
        if (sort) {
            sort (r, d, ph);
        }
        ph.progress(90);
        if (breakUp) {
            breakup (r, d, ph);
        }
        ph.finish();
    }
    
    private class ImportProcessor implements Runnable {
        private Data[] dtas;
        private final Resource r;
        private final BaseDocument d;
        private final ProgressHandle ph;
        
        public ImportProcessor (Resource r, Document d, ProgressHandle ph) {
            this.ph = ph;
            this.r = r;
            this.d = (BaseDocument) d;
        }
        
        private Set wildcardImports = null;
        private Set explicitImports = null;
        private Set elementsImportedByWildcard = null;
        private Set referencedClassesInSource = null;
        private Set fqnsInSource = new HashSet();
        private void buildInfo() {
            if (wildcardImports != null) {
                return;
            }
            List imps = r.getImports();
            explicitImports = new HashSet();
            wildcardImports = new HashSet();
            elementsImportedByWildcard = new HashSet();
            referencedClassesInSource = getAllReferencedClasses (r, d, fqnsInSource);
            for (Iterator it = imps.iterator(); it.hasNext();) {
                Import elem = (Import) it.next();
                if (!elem.isStatic()) {
                    if (elem.getImportedNamespace() != null) { //no static imports here
                        if (!elem.isOnDemand()) {
                            explicitImports.add (elem.getImportedNamespace().getName());
                        } else {
                            wildcardImports.add (elem.getImportedNamespace().getName());
                            elementsImportedByWildcard.addAll(elem.getImportedElements());
                        }
                    }
                }
            }
        }
        public static final int ELIMINATE_WILDCARDS = 1;
        public static final int REPLACE_IMPORTS = 2;
        public static final int ELIMINATE_FQNS = 3;
        private int mode = 0;
        public void setMode (int mode) {
            switch (mode) {
                case ELIMINATE_WILDCARDS:
                case REPLACE_IMPORTS:
                case ELIMINATE_FQNS:
                    break;
                default:
                    throw new IllegalArgumentException (Integer.toString(mode));
            }
            this.mode = mode;
        }
        
        public void run() {
            JavaModel.setClassPath(r);
            buildInfo();
            switch (mode) {
                case REPLACE_IMPORTS :
                    replaceImports();
                    break;
                case ELIMINATE_FQNS :
                    eliminateFqns();
                    break;
                case ELIMINATE_WILDCARDS:
                    eliminateWildcards();
                    break;
                default :
                    throw new IllegalArgumentException (Integer.toString(mode));
            }
        }
        
        private JavaClass outermost (JavaClass cd) {
            while (cd != null && cd.isInner()) {
                //Parent may be a method if it's a class defined in a method
                Element el = (Element) cd.refImmediateComposite();
                while (el != null && !(el instanceof JavaClass)) {
                    el = (Element) el.refImmediateComposite();
                }
                cd = (JavaClass) el;
            }
            return cd;
        }
        
        public void eliminateWildcards() {
            if (!wildcardImports.isEmpty()) {
                List imps = r.getImports();
                List classesInSource = r.getClassifiers();
                JavaModelPackage pkg = (JavaModelPackage) r.refImmediatePackage();
                for (Iterator i=referencedClassesInSource.iterator(); i.hasNext();) {
                    Object o = i.next();
                    if (o instanceof JavaClass) {
                        JavaClass cd = (JavaClass) o;
                        String importString;
                        if (!pkg.equals(cd.refImmediatePackage())) {
                            importString = cd.getName();
                        } else {
                            importString = null;
                        }
                        if (classesInSource.contains(cd) || (cd.isInner() && classesInSource.contains(outermost(cd)))) {
                            importString = null;
                        }
                        if (importString != null && !explicitImports.contains(importString)) {
                            if (importString.startsWith("java.lang.") && importString.lastIndexOf(".") == "java.lang.".lastIndexOf(".")) {
                                continue;
                            }
                            explicitImports.add(importString);
                            Import imp=pkg.getImport().createImport(importString, null, false, false);
                            imps.add (imp);
                        }
                    }
                }
                for (Iterator i=imps.iterator();i.hasNext();) {
                    Import imp = (Import) i.next();
                    if (imp.isOnDemand() && !imp.isStatic()) {
                        wildcardImports.remove(imp.getImportedNamespace().getName());
                        i.remove();
                    }
                }
            }
        }
        
        public void replaceImports() {
            JavaModelPackage pkg = (JavaModelPackage) r.refImmediatePackage();
            if (dtas == null) {
                return;
            }
            for (int i=0; i < dtas.length; i++) {
                JavaClass type = dtas[i].type;
                String typeName = type.getName();
                String pkgName;
                if (typeName != type.getSimpleName()) {
                    pkgName = typeName.substring(0, typeName.length() - type.getSimpleName().length()-1);
                } else {
                    pkgName = ""; //name == simplename : default package
                }
                    
                String typeStr = type.getName();
                if (typeStr.startsWith("java.lang.") && typeStr.lastIndexOf(".") == "java.lang.".lastIndexOf(".")) {
                    continue;
                }
                if (!explicitImports.contains(typeStr) && !wildcardImports.contains(pkgName) && !ambiguous (type)) {
                    Import imp = pkg.getImport().createImport(typeStr, null, 
                            false, false);
                    r.addImport(imp);
                    explicitImports.add(typeStr);
                }
            }
        }
        
       public void eliminateFqns() {
           buildInfo();
           List changes = new ArrayList(20);
           JavaModelPackage jpkg = (JavaModelPackage) r.refImmediatePackage();
           for (Iterator i=fqnsInSource.iterator(); i.hasNext();) {
               MultipartId id = (MultipartId) i.next();
               if (id.getType() instanceof JavaClass) {  //Skip unresolved classes
                   JavaClass type = (JavaClass) id.getType();
                   if (!ambiguous(type)) {
                       MultipartId startId = id;
                       while (startId.getParent() != null) {
                           MultipartId old = startId;
                           startId = startId.getParent();
                           old.setParent(null);
                       }
                       if (startId != id) {
                           startId.setName(type.getSimpleName());
                           Data dta = new Data (0, 0, type.getSimpleName(), (JavaClass) id.getType());
                           changes.add (dta);
                           i.remove();
                       }
                   }
               }
           }
           if (!changes.isEmpty()) {
               dtas = (Data[]) changes.toArray(new Data[0]);
               Arrays.sort(dtas);
           }
        }  

        private boolean ambiguous (JavaClass clazz) {
            //XXX handle static imports too
            String simple = clazz.getSimpleName();
            for (Iterator i=elementsImportedByWildcard.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o instanceof JavaClass) { //May be a method or field if static import
                    JavaClass curr = (JavaClass) o;
                    if (clazz != curr && simple.equals(curr.getSimpleName())) {
                        return true;
                    }
                }
            }
            for (Iterator i = explicitImports.iterator(); i.hasNext();) {
                String fqn = (String) i.next();
                if (!clazz.getName().equals(fqn) && fqn.endsWith('.' + simple) && fqn.length() > simple.length()) {
                    return true;
                }
            }
            return false;
        }       
    }
   
    private static final class Data implements Comparable {
        final int start, end;
        final String nue;
        final JavaClass type;
        public Data (int start, int end, String nue, JavaClass type) {
            this.start = start;
            this.end = end;
            this.nue = nue;
            this.type = type;
        }

        public int compareTo(Object o) {
            Data dta = (Data) o;
            //reverse sort
            return dta.start - start;
        }
        
        public String toString() {
            return type.getName() + " " + start + ":" + end + " -> '" + nue + "'";
        }
    }
    
    private String nameOf (Import im) {
        NamedElement nm = im.getImportedNamespace();
        if (nm != null) {
            return nm.getName();
        }
        return null;
    }

    public int compare(Object o1, Object o2) {
        Import a = (Import) o1;
        Import b = (Import) o2;
        NamedElement an = a.getImportedNamespace();
        NamedElement bn = b.getImportedNamespace();
        int amt = 0;
        //Documented somewhere that it can be null, I think for
        //wildcard static imports
        if (an != null && bn != null) {
            String na = an.getName();
            String nb = bn.getName();
            amt = na.compareTo(nb);
        }
        if (a.isStatic() && !b.isStatic()) {
            amt += -20000; 
        } else if (!a.isStatic() && b.isStatic()) {
            amt += 20000;
        }
        return amt;
    }
    private Set getAllReferencedClasses (Resource r, Document d, Set fqns) {
        HashSet unresolved = new HashSet();
        HashSet resolved = new HashSet();
        findPotentialClassNames(r, unresolved, resolved, fqns, d);
        HashSet result = new HashSet();
        result.addAll(resolved);
//        result.addAll(unresolved);
        return result;
    }
    
    private static boolean isFQN (MultipartId id) {
        return id.getType() != null && 
                id.getName() != null && 
                !(id.getType() instanceof PrimitiveType) && 
                !id.getType().getName().equals(id.getName());
    }
    
    private void findPotentialClassNames(Set set, Set resolved, Set fqns, Element elem, Set checkedElements, int level, Document doc) {
        //borrowed from JavaFixAllImports
        Iterator iterator;
        level++;
        if (elem instanceof ArrayReference) {
            elem = ((ArrayReference)elem).getParent();
        }
        if (elem instanceof MultipartId) {
            List typeArgs = new ArrayList();
            MultipartId id = (MultipartId)elem;
            typeArgs.addAll(id.getTypeArguments());

            if (isFQN (id)) {
                fqns.add(id);
            }
            while (id.getParent() != null) {
                id = id.getParent();
                typeArgs.addAll(id.getTypeArguments());
            }
            NamedElement namedElem = id.getElement();
            if (namedElem instanceof UnresolvedClass) {
                set.add(idToName(id));
            } else if (namedElem instanceof JavaClass) {
                resolved.add(namedElem);
            }
            iterator = typeArgs.iterator();
        } else {
            iterator = elem.getChildren().iterator();
        }
        for (; iterator.hasNext();) {
            Element usedElem = (Element)iterator.next();
            if (!checkedElements.contains(usedElem)) {
                checkedElements.add(usedElem);
                findPotentialClassNames(set, resolved, fqns, usedElem, checkedElements, level, doc);
            }
        }
    }
    
    private static String idStr (Document doc, MultipartId id) {
        try {
            int start = id.getStartOffset();
            int end = id.getEndOffset();
            return doc.getText (start, end-start);
        } catch (Exception e) {}
        return "";
    }
    
    private void findPotentialClassNames(Resource resource, Set unresolved, Set resolved, Set fqns, Document doc) {
        //borrowed from JavaFixAllImports
        HashSet checkedElements = new HashSet();
        Iterator iter = resource.getClassifiers().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Element elem = (Element) iter.next();
            count += elem.getChildren().size();
        }
        iter = resource.getClassifiers().iterator();
        while (iter.hasNext()) {
            findPotentialClassNames(unresolved, resolved, fqns, (Element)iter.next(), 
                    checkedElements, 0, doc);
        }
    }    
    
    private static void idToName(StringBuffer buffer, MultipartId id) {
        MultipartId parent = id.getParent();
        if (parent != null) {
            idToName(buffer, parent);
            buffer.append('.');
        }
        buffer.append(id.getName());
    }
    
    private static String idToName(MultipartId id) {
        StringBuffer buffer = new StringBuffer();
        idToName(buffer, id);
        return buffer.toString();
    }    

    private void sort(Resource r, Document d, ProgressHandle ph) {
        final List l = r.getImports();
        if (l.size() == 0) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        final Import[] imps = (Import[]) l.toArray(new Import[l.size()]);
        final boolean[] b = new boolean [imps.length];
        Arrays.sort (imps, this);
        final ArrayList copy = new ArrayList (l);
        Runnable run = new Runnable() {
            public void run() {
                try {
                    l.clear();
                    l.addAll (Arrays.asList(imps));
                } catch (Exception e) {
                    if (!new HashSet(l).equals(new HashSet(copy))) {
                        l.clear();
                        l.addAll(copy);
                        ErrorManager.getDefault().notify(e);
                        return;
                    }
                } finally {
                }
            }
        };
        JavaMetamodel.getDefaultRepository().beginTrans(true);
        try {
            ((BaseDocument) d).runAtomic(run);
        } finally {
            JavaMetamodel.getDefaultRepository().endTrans();
        }
    }

    private void breakup(final Resource r, final Document d, final ProgressHandle ph) {
        
        //XXX how else to do this?
        final ArrayList positions = new ArrayList();
        Runnable run = new Runnable() {
        public void run() {            
            List nue = r.getImports();
            int max = nue.size()-2;
            if (max <= 1) {
                return;
            }
            Import prev = (Import) nue.get(max + 1);
            //First iterate the imports backward and build a
            //list of all imports where the next import's first
            //package block does not match the previous
            for (int i=max; i >= 0; i--) {
                Import curr = (Import) nue.get (i);

                String as = nameOf (prev);
                String bs = nameOf (curr);
                if (as == null || bs == null) {
                    continue;
                }
                StringTokenizer tk1 = new StringTokenizer(as, ".");
                StringTokenizer tk2 = new StringTokenizer(bs, ".");
                int ct = Math.min (tk1.countTokens(), tk2.countTokens());
                boolean nuline = false;
                ct = Math.min (ct, 1);
                for (int j=0; j < ct; j++) {
                    String tok1 = tk1.nextToken();
                    String tok2 = tk2.nextToken();
                    nuline = !tok1.equals(tok2);
                    break;
                }
                if (nuline) {
                    try {
                        int newlineCount;

                        int end = curr.getEndOffset();
                        String s = d.getText(end-1, Math.min (d.getLength(), end + 25));
                        //Make sure there's only 1 \n before the next line
                        //We don't want to double the blank spaces each
                        //time
                        if (s.indexOf("\n") != -1) { //NOI18N
                            newlineCount = 1;
                            char[] c = s.toCharArray();
                            for (int j=s.indexOf("\n") + 1; j < c.length; j++) {
                                if (c[j] == '\n') {
                                    newlineCount++;
                                    if (newlineCount >= 2) {
                                        break;
                                    }
                                } else if (!Character.isWhitespace(c[j])) {
                                    break;
                                }
                            }
                            if (newlineCount <= 1) {
                                positions.add (new Integer(end));
                            }
                        }
                    } catch (BadLocationException ble) {
                        //Harmless, just one line we won't modify.  But
                        //should never happen
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ble);
                    }
                }
                prev = curr;
            }

            //Now iterate the list backward, inserting \n's as needed
            Integer[] ints = new Integer[positions.size()];
            ints = (Integer[]) positions.toArray(ints);
            Arrays.sort (ints);
            //positions array is already in reverse order
//            for (int i=ints.length-1; i >= 0; i--) {
             for (int i=0; i < ints.length; i++) {
                 int pos = ints[i].intValue();
                 DiffElement diff = new DiffElement (pos, pos, "\n");
                 ((ResourceImpl) r).addExtDiff(diff);
            }
        }
    };
    JavaMetamodel.getDefaultRepository().beginTrans(true);
    try {
        ((BaseDocument) d).runAtomic(run);
    } finally {
            JavaMetamodel.getDefaultRepository().endTrans();
    }
    }
}


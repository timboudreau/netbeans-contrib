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

package org.netbeans.modules.importcruncher;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.ArrayReference;
import org.netbeans.jmi.javamodel.ClassDefinition;
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
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;

public final class CrunchAction extends CookieAction implements Comparator {

    protected void performAction(final Node[] n) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                // XXX I18N
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
        });
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(CrunchAction.class, "LBL_Action");
    }

    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean enable(Node[] nn) {
        if (!super.enable(nn)) {
            return false;
        }
        if (JavaMetamodel.getManager().isScanInProgress()) {
            return false;
        }
        Node n = nn[0];
        EditorCookie ec = (EditorCookie) n.getCookie(EditorCookie.class);
        if (ec == null) {
            return false;
        }
        JEditorPane[] panes = ec.getOpenedPanes();
        if (ec.getOpenedPanes() == null) {
            return false;
        }
        DataObject dob = (DataObject) n.getCookie(DataObject.class);
        return dob != null && dob.isValid() && dob.getPrimaryFile().canWrite();
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            JavaDataObject.class,
        };
    }

    protected boolean asynchronous() {
        return false;
    }

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
    
    private void process (final Resource r, final Document d, final ProgressHandle ph) {
        final ImportProcessor ip = new ImportProcessor (r, d, ph);
        JavaMetamodel.getDefaultRepository().beginTrans(true);
        RuntimeException exc = null;
        try {
            ((BaseDocument) d).runAtomicAsUser(ip);
        } catch (RuntimeException e) {
            exc = e;
            ErrorManager.getDefault().notify(exc);
        } finally {
            JavaMetamodel.getDefaultRepository().endTrans(exc != null);
        }
        //breakup() must run in its own transaction - we have moved things 
        //around, so the model no longer has file positions for imports.
        //Ending the transaction and starting another triggers a reparse,
        //so this info will be there.

        //Unfortunately no way to do all this as one undoable action - 
        //the document lock must come after the repository is locked,
        //so we can't wrap all of this in one runAtomicAsUser() :-(
        try {
            if (exc == null && Prefs.get(Prefs.SORT) && Prefs.get(Prefs.BREAKUP)) {
                JavaMetamodel.getDefaultRepository().beginTrans(true);
                try {
                    breakup (r, d, ph);
                } catch (RuntimeException e2) {
                    exc = e2;
                    ErrorManager.getDefault().notify(exc);
                } finally {
                    JavaMetamodel.getDefaultRepository().endTrans(exc != null);
                }
            }
        } finally {
            ph.finish();
        }
    }
    
    private class ImportProcessor implements Runnable {
//        private Data[] dtas;
        private final Resource r;
        private final BaseDocument d;
        private final ProgressHandle ph;
        
        public ImportProcessor (Resource r, Document d, ProgressHandle ph) {
            this.ph = ph;
            this.r = r;
            this.d = (BaseDocument) d;
        }
        
        public void run() {
            ph.progress(10);
            JavaModel.setClassPath(r);
            buildInfo();
            ph.progress(20);
            if (Prefs.get(Prefs.NO_WILDCARDS)) {
                createExplicitImportsForWildcardImports();
                ph.progress(30);
                deleteAllWildcardImports();
            }
            ph.progress(40);
            if (Prefs.get(Prefs.NO_FQNS)) {
                List /* <JavaClass> */ toImport = eliminateFqns();
                ph.progress(60);
                createExplicitImports(toImport);
            }
            ph.progress(80);
            if (Prefs.get(Prefs.SORT)) {
                sort(r, d, ph);
            }
            ph.progress(90);
        }        
        
        private Set wildcardImports = null;
        private Set explicitImports = null;
        private Set elementsImportedByWildcard = null;
        private Set referencedClassesInSource = null;
        private Set /* <MultipartId> */ qualifiedNamesInSource = new HashSet();
        private Set /* <MultipartId> */ unqualifiedNamesInSource = new HashSet();
        private Set innerNamesInSource = new HashSet();
        private void buildInfo() {
            if (wildcardImports != null) {
                return;
            }
            List imps = r.getImports();
            explicitImports = new HashSet();
            wildcardImports = new HashSet();
            elementsImportedByWildcard = new HashSet();
            referencedClassesInSource = getAllReferencedClasses (r, d, qualifiedNamesInSource, unqualifiedNamesInSource);
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
            
            for (Iterator i=r.getClassifiers().iterator(); i.hasNext();) {
                ClassDefinition def = (ClassDefinition) i.next();
                buildListOfInnerClassNamesForAmbiguityCheck (def);
            }
        }
        
        private void buildListOfInnerClassNamesForAmbiguityCheck (ClassDefinition def) {
            for (Iterator j=def.getFeatures().iterator(); j.hasNext();) {
                Object o = j.next();
                if (o instanceof ClassDefinition) {
                    buildListOfInnerClassNamesForAmbiguityCheck ((ClassDefinition) o);
                }
            }
            innerNamesInSource.add (getSimpleName(def));
        }
        
        private String getSimpleName (ClassDefinition def) {
            if (def instanceof JavaClass) {
                return ((JavaClass) def).getSimpleName();
            } else {
                //Handle UnresolvedClass as best as possible
                String nm = def.getName();
                int ix = nm.lastIndexOf(".");
                return nm.substring(ix+1);
            }
        }
        
        private String getPackageName (MultipartId id) {
            if (!(id.getType() instanceof JavaClass)) {
                return null;
            }
            JavaClass jc = (JavaClass) id.getType();
            JavaClass out = getOutermostClass (jc);
            String nm = out.getName();
            int minus = out.getSimpleName().length() + 1;
            if (nm.length() - minus <= 0) {
                return "";
            } else {
                return nm.substring (0, nm.length() - minus);
            }
        }
        
        private JavaClass getOutermostClass (JavaClass cd) {
            while (cd != null && cd.isInner()) {
                //Parent may be a method if it's a class defined in a method
                //We shouldn't ever try to import one, but we can get one.
                Element el = (Element) cd.refImmediateComposite();
                while (el != null && !(el instanceof JavaClass)) {
                    el = (Element) el.refImmediateComposite();
                }
                cd = (JavaClass) el;
            }
            return cd;
        }
        
        private boolean isJavaLang (ClassDefinition cd) {
            return cd.getName().startsWith("java.lang") && cd.getName().lastIndexOf('.') == "java.lang.".lastIndexOf('.');
        }
        
        private Import createImportFor (JavaClass cd, JavaModelPackage pkg, List outerClassesInSource) {
            if (
                !pkg.equals(cd.refImmediatePackage()) && //It is not in the same package as the file we're chewing on
                !outerClassesInSource.contains(cd) &&  //It is not a class defined in this file
                !outerClassesInSource.contains(getOutermostClass(cd)) &&  //It is not a child of a class defined in this file
                !isJavaLang(cd)) { //it is not in the java.lang.* namespace
                return pkg.getImport().createImport(cd.getName(), null, false, false);
            } else {
                return null;
            }
        }        
        
        public void createExplicitImportsForWildcardImports() {
            if (!wildcardImports.isEmpty()) {
                List imps = r.getImports();
                List classesInSource = r.getClassifiers();
                JavaModelPackage pkg = (JavaModelPackage) r.refImmediatePackage();
                for (Iterator i=referencedClassesInSource.iterator(); i.hasNext();) {
                    Object o = i.next();
                    if (o instanceof JavaClass) {
                        JavaClass cd = (JavaClass) o;
                        Import imp = createImportFor (cd, pkg, classesInSource);
                        if (imp != null && !explicitImports.contains(imp.getName())) {
                            String importString = imp.getName();
                            explicitImports.add(importString);
                            imps.add(imp);
                        }
                    }
                }
            }
        }
        
        public void deleteAllWildcardImports() {
            if (!wildcardImports.isEmpty()) {
                List imps = r.getImports();
                for (Iterator i=imps.iterator();i.hasNext();) {
                    Import imp = (Import) i.next();
                    if (imp.isOnDemand() && !imp.isStatic()) {
                        wildcardImports.remove(imp.getImportedNamespace().getName());
                        i.remove();
                    }
                }
            }
        }
        
        public void createExplicitImports(List /* <JavaClass> */ classesToExplicitlyImport) {
            JavaModelPackage pkg = (JavaModelPackage) r.refImmediatePackage();
            if (classesToExplicitlyImport == null) {
                return;
            }
            for (Iterator i=classesToExplicitlyImport.iterator(); i.hasNext();) {
                JavaClass type = (JavaClass) i.next();
                String typeName = type.getName();
                String pkgName;
                if (typeName != type.getSimpleName()) {
                    pkgName = typeName.substring(0, typeName.length() - type.getSimpleName().length()-1);
                } else {
                    pkgName = ""; //default package
                }
                if (type.refImmediatePackage().equals(r.refImmediatePackage()) && (!type.isInner() || !Prefs.get(Prefs.IMPORT_NESTED_CLASSES))) {
                    continue;
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
        
       /**
        * Get a qualified name for an inner class, or the simple name for an
        * outer class. 
        */
       private String getQualifiedNameWithoutPackage (JavaClass type) {
           StringBuffer sb = new StringBuffer (type.getSimpleName());
           JavaClass par = (JavaClass) type.getDeclaringClass();
           while (par != null) {
               sb.insert(0, '.');
               sb.insert(0, par.getSimpleName());
               par = (JavaClass) par.getDeclaringClass();
           }
           return sb.toString();
       }
       
       private boolean isSiblingOfResource (JavaClass type) {
           //XXX handle case of inner siblings?  Don't see a need, but might be a corner case.
           return type.refImmediatePackage().equals(r.refImmediatePackage()) && !type.isInner();
       }
        
       public List /* <JavaClass> */ eliminateFqns() {
           buildInfo();
           List changes = new ArrayList(20);
           JavaModelPackage jpkg = (JavaModelPackage) r.refImmediatePackage();
           for (Iterator i=qualifiedNamesInSource.iterator(); i.hasNext();) {
               MultipartId id = (MultipartId) i.next();
               if (id.getType() instanceof JavaClass) {  //Skip unresolved classes
                    JavaClass type = (JavaClass) id.getType();
                    if (isSiblingOfResource(type)) {
                        convertToUnqualifiedId (id, type);
                        //Don't import from same package - skip adding this to the list of things to
                        //import
                        continue;
                    }
                    if (!ambiguous(type)) {
                       Element context = (Element) id.refImmediateComposite();
                       if (id.getParent() != null) {  //Probably an FQN
                            JavaClass addToList = null;
                            if (!Prefs.get(Prefs.IMPORT_NESTED_CLASSES)) {
                                JavaClass toImport = getOutermostClass(type);
                                if (type.isInner()) {  //Not an FQN, a QN of an inner class Outer.Inner
                                    MultipartId myId = convertToQualifiedIdForInnerClass(id, type);
                                    if (myId != id) {
                                        context.replaceChild(myId, id);
                                    }
                                } else {  //We're just stripping package names
                                    convertToUnqualifiedId(id, type);
                                }
                                String replaceWith = getQualifiedNameWithoutPackage(type);
                                addToList = toImport;
                            } else {
                               if (isAlreadyUnqualifiedId(id, type)) {
                                   //Nothing to do, save some time here
                                   continue;
                               }
                               convertToUnqualifiedId(id, type);
                               addToList = type;
                            }
                           changes.add (addToList);
                           i.remove();
                       }
                   }
                }
            }
           if (!changes.isEmpty()) {
               return changes;
           } else {
               return null;
           }
        }  
       
        private boolean isAlreadyUnqualifiedId (MultipartId id, JavaClass type) {
            return idToName(id).equals(type.getSimpleName()) && !type.isInner();
        }

        private void convertToUnqualifiedId(final MultipartId id, final JavaClass type) {
            stripParents (id);
            id.setName(type.getSimpleName());
        }

        private MultipartId convertToQualifiedIdForInnerClass(final MultipartId id, final JavaClass type) {
            MultipartId myId = id;
            //Isolate it
            for (Iterator j=id.getChildren().iterator(); j.hasNext();) {
                MultipartId mid = (MultipartId) j.next();
                mid.setParent(null);
            }
            myId = stripParents(myId);
            //Set its name to the QN it should use.
            id.setName (getQualifiedNameWithoutPackage(type));
            return myId;
        }

        private MultipartId stripParents(MultipartId myId) {
            while (myId.getParent() != null) {
                MultipartId old = myId;
                myId = myId.getParent();
                old.setParent(null);
            }
            return myId;
        }

        private boolean ambiguous (JavaClass clazz) {
            //XXX handle static imports too
            String simple = clazz.getSimpleName();
            if (innerNamesInSource.contains(simple)) {
                return true;
            }
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
    
    private Set getAllReferencedClasses (Resource r, Document d, Set fqns, Set nonFqns) {
        HashSet unresolved = new HashSet();
        HashSet resolved = new HashSet();
        findPotentialClassNames(r, unresolved, resolved, fqns, nonFqns, d);
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
    
    private void findPotentialClassNames(Set set, Set resolved, Set fqns, Set nonFqns, Element elem, Set checkedElements, int level, Document doc) {
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
            } else {
                nonFqns.add(id);
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
                findPotentialClassNames(set, resolved, fqns, nonFqns, usedElem, checkedElements, level, doc);
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
    
    private void findPotentialClassNames(Resource resource, Set unresolved, Set resolved, Set fqns, Set nonFqns, Document doc) {
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
            findPotentialClassNames(unresolved, resolved, fqns, nonFqns, (Element)iter.next(), 
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
            String src = r.getSourceText();
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
                    int newlineCount;
                    String s;
                    int pos = curr.getEndOffset();
                    int end = Math.min (src.length()-1, pos + 30);
                    try {
                        s = src.substring(pos-1, end);
                    } catch (StringIndexOutOfBoundsException sioobe) {
                        IllegalStateException e = new IllegalStateException(
                                "Bad offsets length " + src.length() + " start " 
                                + pos + " end " + end);
                        throw e;
                    }
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
                            positions.add (new Integer(pos));
                        }
                    }
                }
                prev = curr;
            }

            //Now iterate the list backward, inserting \n's as needed
            Integer[] ints = new Integer[positions.size()];
            ints = (Integer[]) positions.toArray(ints);
            Arrays.sort (ints);
             for (int i=0; i < ints.length; i++) {
                 int pos = ints[i].intValue();
                 DiffElement diff = new DiffElement (pos, pos, "\n");
                 ((ResourceImpl) r).addExtDiff(diff);
            }
        }
    };
    JavaMetamodel.getDefaultRepository().beginTrans(true);
    try {
        ((BaseDocument) d).runAtomicAsUser(run);
    } finally {
        JavaMetamodel.getDefaultRepository().endTrans();
    }
    }
}

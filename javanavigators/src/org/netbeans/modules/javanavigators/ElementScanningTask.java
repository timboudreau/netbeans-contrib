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
package org.netbeans.modules.javanavigators;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementScanner6;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.UiUtils;

/** XXX Remove the ElementScanner class from here it should be wenough to
 * consult the Elements class. It should also permit for showing inherited members.
 *
 * @author phrebejk
 */
public class ElementScanningTask implements CancellableTask<CompilationInfo>{
    
    private GenerifiedListModel <Description> ui;
    private FindChildrenElementVisitor scanner;
    private volatile boolean canceled;
    
    public ElementScanningTask( GenerifiedListModel <Description> ui ) {
        this.ui = ui;
    }
    
    public void cancel() {
        canceled = true;
        if ( scanner != null ) {
            scanner.cancel();
        }
    }

    public void run(CompilationInfo info) throws Exception {
        
        canceled = false; // Task shared for one file needs reset first

        Description rootDescription = new Description( );
        rootDescription.fileObject = info.getFileObject();
        rootDescription.subs = new ArrayList<Description>();
        
        // Get all outerclasses in the Compilation unit
        CompilationUnitTree cuTree = info.getCompilationUnit();
        List<? extends Tree> typeDecls = cuTree.getTypeDecls();
        List<Element> elements = new ArrayList<Element>( typeDecls.size() );
        TreePath cuPath = new TreePath( cuTree );
        for( Tree t : typeDecls ) {
            TreePath p = new TreePath( cuPath, t );
            Element e = info.getTrees().getElement( p );
            if ( e != null ) {
                elements.add( e );
            }
        }
        
        if ( !canceled ) {
            scanner = new FindChildrenElementVisitor(info);                      
            for (Element element : elements) {
                scanner.scan(element, rootDescription);
            }
        }
        
        List <Description> contents = new ArrayList <Description> (100);
        
        //leave out root
        for (Description d : rootDescription.subs) {
            find (d, contents);
        }
        
        if ( !canceled ) {
            ui.setContents( contents, false );
        }
    }
    
    private void find (Description d, List <Description> descs) {
        //PENDING:  Better way to weed out error nodes?
        if (d.toString().indexOf("<error>") < 0) { //NOI18N        
            //Dont show top level class elements
            if (d.elementHandle.getKind() != ElementKind.CLASS || d.inner) {
                descs.add (d);
            }
            if (d.subs != null) {
                for (Description dd : d.subs) {
                    find (dd, descs);
                }
            }
        }
    }
     
    private static class FindChildrenElementVisitor extends ElementScanner6<Void, Description> {
        
        private CompilationInfo info;
        
        private volatile boolean canceled = false;
        
        public FindChildrenElementVisitor(CompilationInfo info) {
            this.info = info;
        }
        
        void cancel() {
            canceled = true;
        }
        
        public Void visitPackage(PackageElement e, Description p) {
            return null; // No interest in packages here
        }
        
        public Void visitType(TypeElement e, Description p) {
            if ( !canceled  && !info.getElementUtilities().isSynthetic(e) ) {            
                Description d = new Description();
                d.parent = p;
                d.fileObject = p.fileObject;
                d.kind = e.getKind();
                d.modifiers = e.getModifiers();
                d.elementHandle = ElementHandle.create(e);
                d.name = e.getSimpleName().toString();
                d.subs = new ArrayList<Description>();
                d.pos = getPosition( e );
                d.htmlHeader = createHtmlHeader( e, 
                        info.getElements().isDeprecated(e) );                
                d.javadoc = splitUp (info.getElements().getDocComment(e));
                d.icon = UiUtils.getElementIcon(e.getKind(), e.getModifiers());
                d.inner = e.getNestingKind().isNested();
                
                if ( d.pos == -1 ) {
                    return null;
                }
                super.visitType(e, d);
                p.subs.add(d);
            }
            return null;
        }
        
        public Void visitVariable(VariableElement e, Description p) {
            if ( !canceled && !info.getElementUtilities().isSynthetic(e) && 
                ( e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.ENUM_CONSTANT ) ) {
                Description d = new Description();
                d.parent = p;
                d.fileObject = p.fileObject;
                d.kind = e.getKind();
                d.modifiers = e.getModifiers();
                d.elementHandle = ElementHandle.create(e);
                d.name = e.getSimpleName().toString();
                d.pos = getPosition( e );
                d.htmlHeader = createHtmlHeader( e, info.getElements().isDeprecated(e) ); 
                d.icon = UiUtils.getElementIcon(e.getKind(), e.getModifiers());
                d.javadoc = splitUp (info.getElements().getDocComment(e));
                if ( d.pos == -1 ) {
                    return null;
                }
                super.visitVariable(e,d);
                p.subs.add(d);            
            }
            return null;
        }
        
        public Void visitExecutable(ExecutableElement e, Description p) {
            if ( !canceled  && !info.getElementUtilities().isSynthetic(e) ) {
                Description d = new Description();                
                d.parent = p;
                d.fileObject = p.fileObject;
                d.kind = e.getKind();
                d.modifiers = e.getModifiers();
                d.elementHandle = ElementHandle.create(e);
                d.name = e.getSimpleName().toString();
                d.pos = getPosition( e );
                d.javadoc = splitUp (info.getElements().getDocComment(e));
                if ( d.pos == -1 ) {
                    return null;
                }
                d.htmlHeader = createHtmlHeader(e, info.getElements().isDeprecated(e));
                d.icon = UiUtils.getElementIcon(e.getKind(), e.getModifiers());
                super.visitExecutable(e, d);
                p.subs.add(d);            
            }
            return null;
        }
        
        private String splitUp (String s) {
            return s == null ? null : "<html>" + s.replace("\n\n", "<P>").replace (
                    "\n", "<br>");
        }
        
        
        public Void visitTypeParameter(TypeParameterElement e, Description p) {
            return null;
        }
        
        private long getPosition( Element e ) {
             Trees trees = info.getTrees();
             CompilationUnitTree cut = info.getCompilationUnit();
             Tree t = trees.getTree(e);
             
             if ( t == null ) {
                 // Methods like values in Enums - should noty be shown in navigator
                 return -1;
             }
             
             SourcePositions sourcePositions = trees.getSourcePositions();
             
             return sourcePositions.getStartPosition(cut, t );
        }
        
                
        /** Creates HTML display name of the Executable element */
        private String createHtmlHeader( ExecutableElement e, boolean isDeprecated ) {
            
            StringBuilder sb = new StringBuilder();
            if (info.getElementUtilities().overridesMethod(e)) {
                sb.append ("<b>");
            }
                        
            Set <Modifier> mods = e.getModifiers();
//            boolean statik = mods.contains(Modifier.STATIC);
//            if (statik) {
//                sb.append ("<u>");
//            }
            boolean phynal = mods.contains(Modifier.FINAL);
            if (phynal) {
                sb.append ("<i>"); //FIXME
            }
            
            if ( isDeprecated ) {
                sb.append("<s>"); // NOI18N
            }
            if ( e.getKind() == ElementKind.CONSTRUCTOR ) {
                sb.append(e.getEnclosingElement().getSimpleName());
            }
            else {
                sb.append(e.getSimpleName());
            }
            if ( isDeprecated ) {
                sb.append("</s>"); // NOI18N
            }
            
            sb.append("("); // NOI18N
            
            List<? extends VariableElement> params = e.getParameters();
            for( Iterator<? extends VariableElement> it = params.iterator(); it.hasNext(); ) {
                VariableElement param = it.next();                
                sb.append(print( param.asType()));
                sb.append(" "); // NOI18N
                sb.append(param.getSimpleName());
                if ( it.hasNext() ) {
                    sb.append(", "); // NOI18N
                }
            }
            
            
            sb.append(")"); // NOI18N
            
            if ( e.getKind() != ElementKind.CONSTRUCTOR ) {
                TypeMirror rt = e.getReturnType();
                if ( rt.getKind() != TypeKind.VOID ) {                               
                    sb.append(" : "); // NOI18N                                
                    sb.append(print(e.getReturnType()));
                }
            }
                
            return sb.toString();
        }
        
        private String createHtmlHeader( VariableElement e, boolean isDeprecated ) {
            
            StringBuilder sb = new StringBuilder();
            
            if ( isDeprecated ) {
                sb.append("<s>"); // NOI18N
            }
            
            Set <Modifier> mods = e.getModifiers();
//            boolean statik = mods.contains(Modifier.STATIC);
//            if (statik) {
//                sb.append ("<u>");
//            }
            boolean phynal = mods.contains(Modifier.FINAL);
            if (phynal) {
                sb.append ("<i>"); //FIXME
            }
            
            sb.append(e.getSimpleName());
            if ( isDeprecated ) {
                sb.append("</s>"); // NOI18N
            }
            
            if ( e.getKind() != ElementKind.ENUM_CONSTANT ) {
                sb.append( " : " ); // NOI18N
                sb.append(print(e.asType()));
            }
                        
            return sb.toString();            
        }
        
        private String createHtmlHeader( TypeElement e, boolean isDeprecated ) {
            
            StringBuilder sb = new StringBuilder();            
            if ( isDeprecated ) {
                sb.append("<s>"); // NOI18N
            }
            
            Set <Modifier> mods = e.getModifiers();
            boolean statik = mods.contains(Modifier.STATIC);
            if (statik) {
                sb.append ("<font color=#2222C0>");
            }
            boolean phynal = mods.contains(Modifier.FINAL);
            if (phynal) {
                sb.append ("<i>"); //FIXME
            }
            
            sb.append(e.getSimpleName());
            if ( isDeprecated ) {
                sb.append("</s>"); // NOI18N
            }
            // sb.append(print(e.asType()));            
            List<? extends TypeParameterElement> typeParams = e.getTypeParameters();
            
            if ( typeParams != null && !typeParams.isEmpty() ) {
                sb.append("&lt;"); // NOI18N
                
                for( Iterator<? extends TypeParameterElement> it = typeParams.iterator(); it.hasNext(); ) {
                    TypeParameterElement tp = it.next();
                    sb.append( tp.getSimpleName() );                    
                    try { // XXX Verry ugly -> file a bug against Javac?
                        List<? extends TypeMirror> bounds = tp.getBounds();
                        if ( !bounds.isEmpty() ) {
                            sb.append(printBounds(bounds));
                        }
                    }
                    catch ( NullPointerException npe ) {
                        npe.printStackTrace();
                    }                    
                    if ( it.hasNext() ) {
                        sb.append(", "); // NOI18N
                    }
                }
                
                sb.append("&gt;"); // NOI18N
            }
            return sb.toString();            
        }
        
        private String printBounds( List<? extends TypeMirror> bounds ) {
            if ( bounds.size() == 1 && "java.lang.Object".equals( bounds.get(0).toString() ) ) {
                return "";
            }
            
            StringBuilder sb = new StringBuilder();
            
            sb.append( " extends " ); // NOI18N
            
            for (Iterator<? extends TypeMirror> it = bounds.iterator(); it.hasNext();) {
                TypeMirror bound = it.next();
                sb.append(print(bound));
                if ( it.hasNext() ) {
                    sb.append(" & " ); // NOI18N
                }
               
            }

            return sb.toString();
        }
        
        private String print( TypeMirror tm ) {
            StringBuilder sb;
            
            switch ( tm.getKind() ) {
                case DECLARED:
                    DeclaredType dt = (DeclaredType)tm;
                    sb = new StringBuilder( dt.asElement().getSimpleName().toString() );
                    List<? extends TypeMirror> typeArgs = dt.getTypeArguments();
                    if ( !typeArgs.isEmpty() ) {
                        sb.append("&lt;");
                        
                        for (Iterator<? extends TypeMirror> it = typeArgs.iterator(); it.hasNext();) {
                            TypeMirror ta = it.next();
                            sb.append(print(ta));
                            if ( it.hasNext() ) {
                                sb.append(", ");
                            }
                        }
                        sb.append("&gt;");
                    }
                    
                    return sb.toString(); 
                case TYPEVAR:
                    TypeVariable tv = (TypeVariable)tm;  
                    sb = new StringBuilder( tv.asElement().getSimpleName().toString() );
                    return sb.toString();
                case ARRAY:
                    ArrayType at = (ArrayType)tm;
                    sb = new StringBuilder( print(at.getComponentType()) );
                    sb.append("[]");
                    return sb.toString();
                case WILDCARD:
                    WildcardType wt = (WildcardType)tm;
                    sb = new StringBuilder("?");
                    if ( wt.getExtendsBound() != null ) {
                        sb.append(" extends "); // NOI18N
                        sb.append(print(wt.getExtendsBound()));
                    }
                    if ( wt.getSuperBound() != null ) {
                        sb.append(" super "); // NOI18N
                        sb.append(print(wt.getSuperBound()));
                    }
                    return sb.toString();
                default:
                    return tm.toString();
            }
        }
    }
}

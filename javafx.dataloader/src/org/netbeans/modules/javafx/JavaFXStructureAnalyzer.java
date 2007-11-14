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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.javafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.ElementHandle;
import org.netbeans.api.gsf.ElementKind;
import org.netbeans.api.gsf.HtmlFormatter;
import org.netbeans.api.gsf.Modifier;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.gsf.StructureItem;
import org.netbeans.api.gsf.StructureScanner;
import org.netbeans.modules.javafx.parser.FXParserResult;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.javafx.parser.JavaFXElement;

;

/**
 *
 * @author answer
 */
public class JavaFXStructureAnalyzer implements StructureScanner{

    private HtmlFormatter formatter;
    private FXParserResult result;
    
    public JavaFXStructureAnalyzer() {
    }

    public List<? extends StructureItem> scan(CompilationInfo info, HtmlFormatter formatter) {
        this.result = (FXParserResult)info.getParserResult();
        this.formatter = formatter;
        
        List<JavaFXElement> elements = result.getElementsList();
        List<StructureItem> structure = new ArrayList<StructureItem>(elements.size());
        
        for (JavaFXElement element: elements) {
            if (element.getKind() != ElementKind.OTHER) {
                JavaFXStructureItem structureItem = new JavaFXStructureItem(info.getFileObject(), element);
                for (JavaFXElement nestedElement: element.getNested(element)) {
                    JavaFXStructureItem nestedStructureItem = new JavaFXStructureItem(info.getFileObject(), nestedElement);
                    structureItem.addNested(nestedStructureItem);
                }
                structure.add(structureItem);
            }
        }
        return structure;
    }

/*    
    public List<OffsetRange> folds(CompilationInfo info) {
        List<OffsetRange> folds = new  ArrayList<OffsetRange>();
        return folds;
    }
*/    
    public Map<String, List<OffsetRange>> folds(CompilationInfo info) {
        Map<String,List<OffsetRange>> folds = new HashMap<String,List<OffsetRange>>();
        List<OffsetRange> codefolds = new ArrayList<OffsetRange>();
        List<OffsetRange> importfolds = new ArrayList<OffsetRange>();
        folds.put("codeblocks", codefolds); // NOI18N
        folds.put("imports", importfolds); // NOI18N

        this.result = (FXParserResult)info.getParserResult();
        
        List<JavaFXElement> elements = result.getElementsList();
        List<StructureItem> structure = new ArrayList<StructureItem>(elements.size());
        
        for (JavaFXElement element: elements) {
            if (element.getKind() == ElementKind.OTHER)
                if (element.getName().contentEquals("IMPORT_FOLD")) {
                    importfolds.add(element.getOffsetRange());
                } else {
                    codefolds.add(element.getOffsetRange());
                }
        }

        return folds;
    }
    
    private class JavaFXStructureItem implements StructureItem {
        private JavaFXElement element = null;
        private List<StructureItem> nestedList = new ArrayList<StructureItem>(10);
        private JavaFXElementHandleImpl   handle = null;
        public JavaFXStructureItem(FileObject fileObject, JavaFXElement element) {
            this.element = element;
            this.handle = new JavaFXElementHandleImpl(fileObject);
            this.handle.setElement(element);
        }
        public String getName() {
            return element.getName().substring(element.getName().lastIndexOf(".") + 1);
        }
        public String getHtml() {
            return element.getName().substring(element.getName().lastIndexOf(".") + 1);
        }
        public ElementHandle<? extends Element> getElementHandle() {
            return (ElementHandle<? extends Element>)(handle);
        }
        public ElementKind getKind() {
            return element.getKind();
        }
        public Set<Modifier> getModifiers() {
            return element.getModifiers();
        }
        public boolean isLeaf() {
            switch (element.getKind()) {
                case CLASS:
                    return false;
                case ATTRIBUTE:
                    return true;
                default:
                    return true;
            }
        }
        public List<StructureItem> getNestedItems() {
            return nestedList;
        }
        public void addNested(StructureItem item) {
            nestedList.add(item);
        }
        public long getPosition() {
            return 2;
        }

        public long getEndPosition() {
            return 3;
        }
    }

}

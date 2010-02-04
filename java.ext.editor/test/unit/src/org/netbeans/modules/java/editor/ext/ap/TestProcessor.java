/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.java.editor.ext.ap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lahvac
 */
@ServiceProvider(service=Processor.class)
@SupportedAnnotationTypes("org.netbeans.modules.java.editor.ext.ap.TestAnnotation")
public class TestProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) return true;
        
        final TypeElement ann = processingEnv.getElementUtils().getTypeElement("org.netbeans.modules.java.editor.ext.ap.TestAnnotation");

        for (Element e : roundEnv.getElementsAnnotatedWith(ann)) {
            for (AnnotationMirror am : e.getAnnotationMirrors()) {
                if (ann.equals(am.getAnnotationType().asElement())) {
                    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                        if (entry.getKey().getSimpleName().contentEquals("error1")) {
                            processingEnv.getMessager().printMessage(Kind.ERROR, (String) entry.getValue().getValue(), e);
                        }
                        if (entry.getKey().getSimpleName().contentEquals("errorNoElement")) {
                            processingEnv.getMessager().printMessage(Kind.ERROR, (String) entry.getValue().getValue());
                        }
                        if (entry.getKey().getSimpleName().contentEquals("errorNoteNoElement")) {
                            processingEnv.getMessager().printMessage(Kind.NOTE, (String) entry.getValue().getValue());
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        TypeElement ann = (TypeElement) annotation.getAnnotationType().asElement();

        if (!"org.netbeans.modules.java.editor.ext.ap.TestAnnotation".contentEquals(ann.getQualifiedName())) {
            throw new IllegalStateException(ann.getQualifiedName().toString());
        }

        List<Completion> result = new LinkedList<Completion>();

        if (member == null) {
            return result;
        }
        
        String attribute = member.getSimpleName().toString();

        if ("clazz1".equals(attribute)) {
            result.add(new CompletionImpl("java.util.List", ""));
        }

        if ("clazz2".equals(attribute)) {
            result.add(new CompletionImpl("java.util.List.class", ""));
        }

        if ("string".equals(attribute) || "value".equals(attribute)) {
            result.add(new CompletionImpl("test", ""));
        }

        if ("multiString".equals(attribute)) {
            result.add(new CompletionImpl("test", ""));
            result.add(new CompletionImpl("aa", ""));
        }
        
        if ("multiType".equals(attribute)) {
            result.add(new CompletionImpl("java.util.LinkedList", ""));
            result.add(new CompletionImpl("java.awt.List", ""));
        }

        return result;
    }

    private static final class CompletionImpl implements Completion {

        private final String value;
        private final String message;

        public CompletionImpl(String value, String message) {
            this.value = value;
            this.message = message;
        }

        public String getValue() {
            return value;
        }

        public String getMessage() {
            return message;
        }
        
    }

}

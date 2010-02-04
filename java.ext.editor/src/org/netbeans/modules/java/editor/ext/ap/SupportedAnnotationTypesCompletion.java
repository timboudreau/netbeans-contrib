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

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;

/**
 *
 * @author lahvac
 */
@SupportedAnnotationTypes("javax.annotation.processing.SupportedAnnotationTypes")
public class SupportedAnnotationTypesCompletion extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        throw new UnsupportedOperationException("Should not be called.");
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        if (!(processingEnv instanceof ProcessingEnvironmentImpl))
            return Collections.emptyList();

        CompilationInfo info = ((ProcessingEnvironmentImpl) processingEnv).getInfo();
        ClassIndex ci = info.getClasspathInfo().getClassIndex();

        if (ci == null)
            return Collections.emptyList();

        TypeElement annotationObj = processingEnv.getElementUtils().getTypeElement("java.lang.annotation.Annotation");

        if (annotationObj == null)
            return Collections.emptyList();

        List<Completion> result = new LinkedList<Completion>();

//        for (ElementHandle<TypeElement> eh : ci.getElements(ElementHandle.create(annotationObj), EnumSet.of(SearchKind.IMPLEMENTORS), EnumSet.of(SearchScope.DEPENDENCIES, SearchScope.SOURCE))) {
//            result.add(new CompletionImpl(eh.getQualifiedName()));
//        }

        for (ElementHandle<TypeElement> eh : ci.getDeclaredTypes("", ClassIndex.NameKind.PREFIX, EnumSet.of(SearchScope.DEPENDENCIES, SearchScope.SOURCE))) {
            if (eh.getKind() != ElementKind.ANNOTATION_TYPE) continue;
            
            result.add(new CompletionImpl(eh.getQualifiedName()));
        }

        return result;
    }



}

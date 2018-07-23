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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.fisheye.hacks;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.errorstripe.AnnotationViewFactory;
import org.netbeans.modules.fisheye.FiMark;
import org.netbeans.modules.fisheye.FisheyeHandler;
import org.netbeans.modules.fisheye.FisheyeTrigger;
import org.openide.util.ImageUtilities;

/**
 * Overrides the standard factory to provide a fisheye view
 *
 * @author Tim Boudreau
 */
public class FisheyeAnnotationViewFactory extends AnnotationViewFactory {

    @Override
    public JComponent createSideBar(JTextComponent target) {
        JComponent result = super.createSideBar(target);
        H h = new H();
        FisheyeTrigger trig = new FisheyeTrigger(target, result, h);
        h.trig = trig;
        return result;
    }

    private static class H implements FisheyeHandler {

        private FisheyeTrigger trig;

        public Integer viewToModel(int yCoord, JComponent trigger) {
            return trig.getLocusElement();
        }

        public List<FiMark> getMarks(Document doc) {
            if (!(doc instanceof BaseDocument)) {
                return Collections.<FiMark>emptyList();
            }
            BaseDocument bd = (BaseDocument) doc;
            Annotations annos = bd.getAnnotations();
            List<FiMark> result = new ArrayList<FiMark>();
            for (int line = annos.getNextLineWithAnnotation(-1); line != -1; line = annos.getNextLineWithAnnotation(line + 1)) {
                AnnotationDesc anno = annos.getActiveAnnotation(line);
                FiMark mark = new MarkImpl(anno);
                AnnotationDesc[] passive = annos.getPasiveAnnotations(line);
                if (passive != null) {
                    for (int i = 0; i < passive.length; i++) {
                        AnnotationDesc annotationDesc = passive[i];
                        result.add(new MarkImpl(annotationDesc));
                    }
                }
                result.add(mark);
            }
            return result;
        }
    }

    private static class MarkImpl extends FiMark {

        private final AnnotationDesc anno;

        private MarkImpl(AnnotationDesc anno) {
            this.anno = anno;
        }

        @Override
        public Color getColor() {
            //Try to find something meaningful
            Color result = anno.getColoring().getBackColor();
            if (result == null) {
                result = anno.getColoring().getWaveUnderlineColor();
                if (result == null) {
                    result = anno.getAnnotationTypeInstance().getCustomSidebarColor();
                    if (result == null) {
                        result = anno.getAnnotationTypeInstance().getWaveUnderlineColor();
                        if (result == null) {
                            result = new Color (130, 130, 245);
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public String getDescription() {
            return anno.getShortDescription();
        }

        @Override
        public Icon getIcon() {
            Image img = anno.getGlyph();
            return img == null ? null : ImageUtilities.image2Icon(img);
        }

        @Override
        public int getLine() {
            return anno.getLine();
        }

        @Override
        public int getEndLine() {
            return anno.getLength() + anno.getLine();
        }
    }
}

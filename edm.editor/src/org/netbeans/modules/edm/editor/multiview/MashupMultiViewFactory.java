/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */

package org.netbeans.modules.edm.editor.multiview;

import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

import org.netbeans.modules.edm.editor.dataobject.MashupDataEditorSupport;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

/**
 *
 * @author Jeri Lockhart
 */
public class MashupMultiViewFactory {
    /**
     * Creates a new instance of MashupMultiViewFactory
     */
    public MashupMultiViewFactory() {
    }

    public static CloneableTopComponent createMultiView(MashupDataObject obj) {
        MultiViewDescription views[] = new MultiViewDescription[2];

        views[0] = getAspectSourceMultiviewDesc(obj);
        views[1] = getAspectGraphMultiViewDesc(obj);

        CloneableTopComponent multiview =
                MultiViewFactory.createCloneableMultiView(
                views,
                views[1], // views[1],
                new MashupDataEditorSupport.CloseHandler(obj));

        //IZ 84440 - show file name with extension
        String name = obj.getNodeDelegate().getDisplayName();
        multiview.setDisplayName(name);
        multiview.setName(name);
        
        return multiview;
    }


    private static MultiViewDescription getAspectGraphMultiViewDesc(MashupDataObject obj) {
        return new MashupGraphMultiViewDesc(obj);
    }

    private static MultiViewDescription getAspectSourceMultiviewDesc(MashupDataObject obj) {
        return new MashupSourceMultiViewDesc(obj);
    }

    /**
     * Shows the desired multiview element. Must be called after the editor
     * has been opened (i.e. WSDLEditorSupport.open()) so the TopComponent
     * will be the active one in the registry.
     *
     * @param  id      identifier of the multiview element.
     */
    public static void requestMultiviewActive(String id) {
        TopComponent activeTC = TopComponent.getRegistry().getActivated();
        MultiViewHandler handler = MultiViews.findMultiViewHandler(activeTC);
        if (handler != null) {
            MultiViewPerspective[] perspectives = handler.getPerspectives();
            for (MultiViewPerspective perspective : perspectives) {
                if (perspective.preferredID().equals(id)) {
                    handler.requestActive(perspective);
                }
            }
        }
    }
}

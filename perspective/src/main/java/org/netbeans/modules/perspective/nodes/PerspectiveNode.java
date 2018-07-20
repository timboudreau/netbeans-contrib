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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.perspective.nodes;

import java.awt.Image;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha
 */
public class PerspectiveNode extends AbstractNode {

    private PerspectiveImpl perspectiveImpl;
    private AbstractAction defaultAction;

    public PerspectiveNode(PerspectiveImpl perspectiveImpl) {
        super(Children.LEAF);
        this.perspectiveImpl = perspectiveImpl;
    }

    public PerspectiveNode(PerspectiveImpl perspectiveImpl, AbstractAction defaultAction) {
        this(perspectiveImpl);
        this.defaultAction = defaultAction;
    }

    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage(perspectiveImpl.getImagePath(), true);
    }

    @Override
    public String getDisplayName() {
        return perspectiveImpl.getAlias();
    }

    @Override
    public String getShortDescription() {
        return perspectiveImpl.getDescription();
    }

    public PerspectiveImpl getPerspectiveImpl() {
        return perspectiveImpl;
    }

    @Override
    public Action getPreferredAction() {
        if (defaultAction!=null) {
            return defaultAction;
        } else {
            return super.getPreferredAction();
        }
    }
}

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.tools.navigation;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import static javax.lang.model.util.ElementFilter.*;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public final class ShowJavaStructureAction extends AbstractNavigationAction {
    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = (DataObject) activatedNodes[0].getLookup()
                                                              .lookup(DataObject.class);

        if (dataObject != null) {
            final FileObject fileObject = dataObject.getPrimaryFile();

            if (fileObject != null) {
                JavaStructure.show(fileObject);

                return;
            }
        }

        beep();
    }

    public String getName() {
        return NbBundle.getMessage(ShowJavaStructureAction.class,
            "CTL_ShowJavaStructureAction");
    }

    protected Class[] cookieClasses() {
        return new Class[] { DataObject.class };
    }

    protected String iconResource() {
        return "org/netbeans/modules/java/tools/navigation/resources/structure.gif";
    }
}

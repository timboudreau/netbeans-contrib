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
package org.netbeans.bluej.ui.window;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.netbeans.bluej.api.BluejOpenCloseCallback;
import org.openide.windows.WindowManager;

/**
 *
 * @author mkleint
 */
public class OpenCloseImpl implements BluejOpenCloseCallback {

    /** Creates a new instance of OpenCloseImpl */
    public OpenCloseImpl() {
    }

    public void projectOpened(final org.netbeans.api.project.Project project) {
        // very ugly, needs to be like this because the component listens on 
        // opened project changes and needs to be opened to do so..
        // a better solution would be to start listening on opened project changes right at the start of IDE
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    if (WindowManager.getDefault().getMainWindow().isVisible()) {
                        BluejViewTopComponent tc = BluejViewTopComponent.findInstance();
                        if (!tc.isOpened()) {
                            //TODO
                            tc.open();
                        }
                    }
                }
            });
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void projectClosed(final org.netbeans.api.project.Project project) {
        //TODO possibly close when empty??
    }
    
}

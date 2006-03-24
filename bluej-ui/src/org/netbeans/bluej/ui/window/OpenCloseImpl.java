/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.bluej.ui.window;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.bluej.api.BluejLogicalViewProvider;
import org.netbeans.bluej.api.BluejOpenCloseCallback;
import org.openide.util.RequestProcessor;
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

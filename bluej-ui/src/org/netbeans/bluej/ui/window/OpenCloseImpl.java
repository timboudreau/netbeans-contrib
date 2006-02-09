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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BluejLogicalViewProvider provider = (BluejLogicalViewProvider) project.getLookup().lookup(BluejLogicalViewProvider.class);
                final BluejViewTopComponent tc = BluejViewTopComponent.findInstance();
                tc.setRootNode(provider.getBigIconRootNode());

                if (WindowManager.getDefault().getMainWindow().isVisible()) {
                    RequestProcessor.getDefault().post(new Runnable() {
                        public void run() {
                            // hack - get to perform after the default projects view.
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    tc.open();
                                    tc.requestActive();
                                }
                            });
                        }
                    }, 300);
                }
            }

        });
    }
    
    public void projectClosed(final org.netbeans.api.project.Project project) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BluejLogicalViewProvider provider = (BluejLogicalViewProvider) project.getLookup().lookup(BluejLogicalViewProvider.class);
                final BluejViewTopComponent tc = BluejViewTopComponent.findInstance();
                tc.close();
            }
        });
    }
    
    public org.netbeans.api.project.Project getCurrentOpenedProject() {
        //TODO
        return null;
    }
    
}

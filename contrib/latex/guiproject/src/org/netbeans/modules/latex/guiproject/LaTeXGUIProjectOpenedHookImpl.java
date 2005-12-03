/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectOpenedHookImpl extends ProjectOpenedHook {
    
    private LaTeXGUIProject project;
    
    /** Creates a new instance of LaTeXGUIProjectOpenedHookImpl */
    public LaTeXGUIProjectOpenedHookImpl(LaTeXGUIProject project) {
        this.project = project;
    }

    protected void projectOpened() {
        LaTeXGUIProjectUpgrader.getUpgrader().upgrade(project);
        assureParsed();
    }

    protected void projectClosed() {
    }
    
    private void assureParsed() {
        RequestProcessor.getDefault().postRequest(new Runnable() {
	    public void run() {
                LaTeXSource      source = project.getSource();
	        LaTeXSource.Lock lock = null;
		
//                System.err.println("source=" + source);
		try {
//                    System.err.println("LaTeXGUIProject.assureParsed trying to obtain lock");
		    lock = source.lock(true);
//                    System.err.println("LaTeXGUIProject.assureParsed trying lock obtained=" + lock);
		} finally {
                    if (lock != null) {
//                        System.err.println("LaTeXGUIProject.assureParsed unlock the lock");
                        source.unlock(lock);
//                        System.err.println("LaTeXGUIProject.assureParsed unlocking done");
                    } else {
//                        System.err.println("LaTeXGUIProject.assureParsed no unlocking (lock == null)");
                    }
		}
	    }
	});
    }
    
}

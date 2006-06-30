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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
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
        RequestProcessor.getDefault().post(new Runnable() {
	    public void run() {
                //an attempt to prevent deadlock between TexKit.<clinit> and TexSettingsInitializer:
                try {
                    ClassLoader cl = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
                    
                    cl.loadClass("org.netbeans.modules.latex.editor.TexKit");
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
                
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

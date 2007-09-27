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
 * Contributor(s):
 */
package org.netbeans.modules.java.closurenavigator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.SourceCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.InputOutput;

public final class ViewClosure extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        JavaDataObject javaDataObject = (JavaDataObject) 
                activatedNodes[0].getLookup().lookup(JavaDataObject.class);
        Project p = FileOwnerQuery.getOwner(
                javaDataObject.getPrimaryFile());
        if (p != null) {
            ClassPathProvider prov=(ClassPathProvider) p
                    .getLookup().lookup (ClassPathProvider.class);
            if (prov != null) {
                ClassPath cp = prov.findClassPath(
                        javaDataObject.getPrimaryFile(),
                        ClassPath.EXECUTE);
                //expedient - really shouldn't be using sourceCookie, but the
                //alternatives are equally bad these days...
                SourceCookie src = (SourceCookie) javaDataObject.getCookie(
                        SourceCookie.class);
                if (src != null) {
                    String nm = 
                        src.getSource().getClasses()[0].getName().getFullName();
                    try {
                        Closure closure = new Closure (cp, nm);
                        InputOutput io = closure.dumpClosure();
                        ClassPath srcPath = prov.findClassPath(
                                javaDataObject.getPrimaryFile(),
                                ClassPath.SOURCE);
                        Collection <DataObject> obs = closure.getSources(
                                srcPath);
//                        for (DataObject dob : obs) {
//                            Transferable t = dob.getNodeDelegate().clipboardCopy();
//                            //xxx and do what?  How do I programmaticaly
                              //multi-select-and-copy?
//                        }
                        String[] missing = closure.getMissingSources();
                        if (missing.length > 0) {
                            io.getOut().println("-----------------------" +
                                    "------------------------");
                            io.getOut().println("MISSING SOURCES FOR");
                            for (int i = 0; i < missing.length; i++) {
                                io.getOut().println ("  " + missing[i]);
                            }
                        }
                        
                        io.getOut().println("*****************************" +
                                "********************\n" + obs.size() + 
                                " SOURCES FOUND:\n");
                        List <String> names = new ArrayList <String> (obs.size());
                        for (DataObject dob : obs) {
                            names.add (dob.getPrimaryFile().getPath());
                        }
                        Collections.sort (names);
                        for (String s : names) {
                            io.getOut().println(s);
                        }
                        io.getOut().close();
                        return;
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify (ioe);
                        return;
                    }
                }
            }
        }
        StatusDisplayer.getDefault().setStatusText("Could not find " +
                ".class file for " + 
                javaDataObject.getNodeDelegate().getDisplayName() +
                " to compute closure.  Maybe it is not compiled?");
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ViewClosure.class, "CTL_ViewClosure");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            JavaDataObject.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return true;
    }
}


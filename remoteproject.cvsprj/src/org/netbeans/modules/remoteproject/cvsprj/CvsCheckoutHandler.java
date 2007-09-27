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
 *
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.remoteproject.cvsprj;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.versioning.system.cvss.executor.CheckoutExecutor;
import org.openide.filesystems.FileUtil;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.remoteproject.CheckoutHandler;
import org.netbeans.modules.versioning.system.cvss.ExecutorGroup;
import org.netbeans.modules.versioning.system.cvss.ui.actions.checkout.CheckoutAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Tim Boudreau
 */
public class CvsCheckoutHandler implements CheckoutHandler {
    
    /** Creates a new instance of CvsCheckoutHandler */
    public CvsCheckoutHandler() {
    }
    
    public boolean canCheckout(FileObject template) {
        return "cvs".equals (template.getAttribute("system")); //NOI18N
    }
    
    private String processRoot (String cvsroot, String username) {
        Pattern p = Pattern.compile("(:.*?:)(.*)(@.*)"); //NOI18N                    
        Matcher m = p.matcher(cvsroot);
        if (m.matches()) {
            String templateUsername = m.group(2);
            if (!templateUsername.equals(username)) {
                cvsroot = m.group(1) + username + m.group(3);
                System.err.println("Rewrote cvsroot to " + cvsroot);
            }
        }
        return cvsroot;
    }

    public String checkout(FileObject template, FileObject dest,
                           final ProgressHandle progress, String username) {
        try {
        assert template != null;
        assert dest != null;
        assert dest.isFolder();
       CheckoutAction a = (CheckoutAction) SystemAction.get(CheckoutAction.class);
       
       String root = (String) template.getAttribute("cvsroot"); //NOI18N
       String modules = (String) template.getAttribute("what"); //NOI18N
       String tag = (String) template.getAttribute("tag"); //NOI18N
       if (tag == null) tag = "HEAD"; //NOI18N

       root = processRoot (root, username);
       
       File f = FileUtil.toFile (dest);
       assert f != null : "Not a normal file: " + dest; //NOI18N
       String workingDir = f.getPath();
       
       System.err.println("Creating a group");
       ExecutorGroup group = new ExecutorGroup(NbBundle.getMessage(
               CvsCheckoutHandler.class, "MSG_Checkout", template.getName()), //NOI18N
               true);
       
       System.err.println("Group " + group);

       //XXX authentication
       
       CheckoutExecutor ce = a.checkout(root, modules, tag, workingDir, true,
                                         group);
       
       
       try {
           Field fld = ExecutorGroup.class.getDeclaredField("progressHandle"); //NOI18N
           fld.setAccessible (true);
           fld.set(group, progress);
       } catch (Exception e) {
           Exceptions.printStackTrace(e);
           e.printStackTrace();
       }
       progress.start();
       group.execute();
        } catch (RuntimeException re) {
            Exceptions.printStackTrace(re);
        }
       return null;
    }

    public String getUserName(FileObject template) {
        String root = (String) template.getAttribute("cvsroot"); //NOI18N
        if (root != null) {
            Pattern p = Pattern.compile(":.*?:(.*)@.*"); //NOI18N                    
            Matcher m = p.matcher(root);
            if (m.matches()) {
                return m.group(1);
            }
        }
        return null;
    }

    public File[] getCreatedDirs(FileObject template, File destFolder) {
       String modules = (String) template.getAttribute("what"); //NOI18N
       File[] result;
       if (modules != null) {
           String[] s = modules.split(" ");
           List <File> l = new ArrayList<File>(s.length);
           for (int i = 0; i < s.length; i++) {
               File f = new File (destFolder, s[i]);
               if (f.exists() && f.isDirectory()) {
                   l.add (f);
               }
           }
           result = l.toArray(new File[l.size()]);           
       } else {
           result = new File[0];           
       }
       return result;
    }
}

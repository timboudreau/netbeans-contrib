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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.primitivesettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Random;
import org.netbeans.api.convertor.Convertors;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.xml.sax.SAXException;

public final class TestAction extends CallableSystemAction {

    public void performAction() {
        try {
            performActionInternal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return NbBundle.getMessage(TestAction.class, "CTL_TestAction");
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
        return false;
    }

    private void performActionInternal() throws IOException {
        {
            // test reading
            FileObject foAll = Repository.getDefault().getDefaultFileSystem().findResource("Tmp/All");
            FileObject [] all = foAll.getChildren();
            for (FileObject fo : all) {
                String settingFileName = "All/" + fo.getNameExt();
                dumpFileContents(settingFileName);
            }
            for (FileObject fo : all) {
                String settingFileName = "All/" + fo.getNameExt();
                Object setting = readSetting(settingFileName);
                if (setting == null) {
                    System.out.println("The " + settingFileName + " setting is: null");
                } else {
                    System.out.println("The " + settingFileName + " setting is: " + setting.getClass().getName() + "[" + setting);
                }
            }
        }

        {
            // test writing boolean
            String fileName = "test-boolean-" + System.currentTimeMillis();
            Boolean booleanValue = Boolean.valueOf(new Random().nextInt(100) > 50);

            writeSetting(fileName, booleanValue);
            System.out.println("Writing new bolean setting in file " + fileName + " value = " + booleanValue);
            
            dumpFileContents(fileName);
            
            Object booleanSetting = readSetting(fileName);
            System.out.println("The new boolean setting in file " + fileName + " is: " + booleanSetting);
        }
        
        {
            // test writing integer
            String fileName = "test-integer-" + System.currentTimeMillis();
            Integer integerValue = new Random().nextInt();

            writeSetting(fileName, integerValue);
            System.out.println("Writing new integer setting in file " + fileName + " value = " + integerValue);
            
            dumpFileContents(fileName);
            
            Object integerSetting = readSetting(fileName);
            System.out.println("The new integer setting in file " + fileName + " is: " + integerSetting);
        }
    }
    
    private void dumpFileContents(String fileName) throws IOException {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Tmp/" + fileName);
        if (fo != null) {
            BufferedReader r = new BufferedReader(new InputStreamReader(fo.getInputStream()));
            try {
                String line;

                System.out.println("---- The contents of " + fo.getPath() + ": ------------------");
                while (null != (line = r.readLine())) {
                    System.out.println(line);
                }
                System.out.println("---- End of the Contents of " + fo.getPath() + ": ------------------");

            } finally {
                r.close();
            }
        } else {
            throw new IOException("Can't find file: Tmp/" + fileName);
        }
    }
    
    private Object readSetting(String settingFileName) throws IOException {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Tmp/" + settingFileName);
        if (fo != null) {
            InputStream is = fo.getInputStream();
            try {
                return Convertors.read(is);
            } catch (SAXException saxe) {
                IOException ioe = new IOException("Can't convert the file contenets.");
                ioe.initCause(saxe);
                throw ioe;
            } finally {
                is.close();
            }
        } else {
            throw new IOException("Can't find setting file: Tmp/" + settingFileName);
        }
    }
    
    private void writeSetting(String settingFileName, Object setting) throws IOException {
        FileObject foTmp = Repository.getDefault().getDefaultFileSystem().findResource("Tmp");
        if (foTmp != null) {
            FileObject fo = foTmp.createData(settingFileName);
            OutputStream os = fo.getOutputStream();
            try {
                Convertors.write(os, setting);
            } finally {
                os.close();
            }
        } else {
            throw new IOException("Can't find folder: Tmp/");
        }
    }
}

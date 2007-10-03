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

package org.netbeans.modules.vcs.profiles.testprofiles.commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import org.netbeans.modules.vcscore.Variables;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;

/**
 *
 * @author  Martin Entlicher
 */
public class CheckOutFromXML extends Object implements VcsAdditionalCommand {
    
    /** Creates a new instance of CheckOutFromXML */
    public CheckOutFromXML() {
    }
    
    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        String path = Variables.expand(vars, "$[? MODULE] [${MODULE}${PS}][]${DIR}", false); // NOI18N
        String xml = (String) vars.get("XML_FS");
        XMLFileSystem xmlFS;
        try {
            xmlFS = new XMLFileSystem(xml);
        } catch (SAXException sex) {
            stderrListener.outputLine(sex.getLocalizedMessage());
            return false;
        }
        //System.out.println("path = "+path);
        FileObject pathFO = xmlFS.findResource(path);
        if (pathFO == null) {
            stderrListener.outputLine("Folder '"+path+"' not found on XML FS.");
            return false;
        }
        String dir;
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        dir = (String) vars.get("DIR"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) dir += File.separator + module;
        } else {
            if (module == null)
                dir=rootDir+File.separator+dir;
            else
                dir=rootDir+File.separator+module+File.separator+dir;
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar) {
            dir = dir.substring(0, dir.length() - 1);
        }

        return checkout(new File(dir), pathFO, stdoutListener, stderrListener);
    }
    
    private static boolean checkout(File dir, FileObject folder,
                                    CommandOutputListener stdoutListener,
                                    CommandOutputListener stderrListener) {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                stderrListener.outputLine("Can not create directory '"+dir.getAbsolutePath()+"'");
                return false;
            }
        }
        FileObject[] children = folder.getChildren();
        for (int i = 0; i < children.length; i++) {
            FileObject ch = children[i];
            String name = ch.getNameExt();
            File file = new File(dir, name);
            if (ch.isFolder()) {
                if (!checkout(file, ch, stdoutListener, stderrListener)) {
                    return false;
                }
            } else {
                try {
                    file.createNewFile();
                    String time = (String) ch.getAttribute("time");
                    String date = (String) ch.getAttribute("date");
                    if (date != null) {
                        if (time != null) {
                            date = date + ", " + time;
                            //try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
                                ParsePosition pp = new ParsePosition(0);
                                Date lmdate = sdf.parse(date, pp);
                                //Date lmdate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).parse(date);
                                if (lmdate == null) {
                                    stderrListener.outputLine("Can not parse date '"+date+"', error at "+pp.getErrorIndex());
                                }
                                file.setLastModified(lmdate.getTime());
                            //} catch (ParseException pex) {
                            //    stderrListener.outputLine(pex.getLocalizedMessage());
                            //}
                        }
                    }
                } catch (IOException ioex) {
                    stderrListener.outputLine(ioex.getLocalizedMessage());
                    return false;
                }
            }
        }
        return true;
    }
}

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

package org.netbeans.modules.vcs.profiles.testprofiles.list;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsListCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;

//import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

public class ListFromXMLFS extends VcsListCommand {
    
    private static XMLFileSystem lastXMLFS;
    private static String lastXML;
    private static long lastXMLModif;
    
    public ListFromXMLFS() {
    }
    
    private static synchronized XMLFileSystem getXMLFileSystem(String xml) throws SAXException {
        if (!xml.equals(lastXML) || !(lastXMLModif == new File(xml).lastModified())) {
            lastXMLModif = new File(xml).lastModified();
            lastXML = xml;
            lastXMLFS = new XMLFileSystem(xml);
        }
        return lastXMLFS;
    }
    
    /**
     * List files of CVS Repository.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * filesByName listing of files with status attributes
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        String path = args.length > 0 ? args[0] : "";
        String xml = (String) vars.get("XML_FS");
        XMLFileSystem xmlFS;
        try {
            xmlFS = getXMLFileSystem(xml);
        } catch (SAXException sex) {
            stderrNRListener.outputLine(sex.getLocalizedMessage());
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
        
        File dirFile = new File(dir);

        //System.out.println("path = "+path);
        FileObject pathFO = xmlFS.findResource(path);
        if (pathFO == null) {
            stderrNRListener.outputLine("Folder '"+path+"' not found on XML FS.");
            return false;
        }
        FileObject[] children = pathFO.getChildren();
        for (int i = 0; i < children.length; i++) {
            FileObject ch = children[i];
            String name = ch.getNameExt();
            //String status;// = (String) ch.getAttribute("status");
            String locker = (String) ch.getAttribute("locker");
            String revision = (String) ch.getAttribute("revision");
            String sticky = (String) ch.getAttribute("sticky");
            String time = (String) ch.getAttribute("time");
            String date = (String) ch.getAttribute("date");
            String size = (String) ch.getAttribute("size");
            String attr = (String) ch.getAttribute("attr");
            File file = new File(dirFile, name);
            String status = getRealStatus(file, ch, stderrNRListener);
            if (ch.isFolder()) name += '/';
            String[] elements = new String[] { name, status, locker, revision, sticky, time, date, size, attr };
            stdoutListener.outputData(elements);
            filesByName.put(name, elements);
        }
        return true;
    }
    
    public static final String getRealStatus(File file, FileObject fo) {
        return getRealStatus(file, fo, null);
    }
    
    private static final String getRealStatus(File file, FileObject fo, CommandOutputListener stderrNRListener) {
        String status;
        if (!file.exists()) {
            if (fo.isData()) {
                status = "Needs Update";
            } else {
                status = "Missing";
            }
        } else {
            if (file.isFile()) {
                String time = (String) fo.getAttribute("time");
                String date = (String) fo.getAttribute("date");
                long lastModified = file.lastModified();
                long reposModified = 0;
                String dateStr;
                if (date != null) {
                    if (time != null) {
                        dateStr = date + ", " + time;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
                        ParsePosition pp = new ParsePosition(0);
                        Date lmdate = sdf.parse(dateStr, pp);
                        //Date lmdate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).parse(date);
                        if (lmdate == null) {
                            stderrNRListener.outputLine("Can not parse date '"+date+"', error at "+pp.getErrorIndex());
                        } else {
                            reposModified = lmdate.getTime();
                        }
                    }
                }
                if (lastModified == reposModified) {
                    status = "Up-to-date";
                } else {
                    if (lastModified < reposModified) {
                        status = "Needs Patch";
                    } else {
                        status = "Locally Modified";
                    }
                }
            } else {
                status = "";
            }
        }
        return status;
    }
    
    public boolean list_XMLOnly(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        String path = args.length > 0 ? args[0] : "";
        String xml = (String) vars.get("XML_FS");
        XMLFileSystem xmlFS;
        try {
            xmlFS = new XMLFileSystem(xml);
        } catch (SAXException sex) {
            stderrNRListener.outputLine(sex.getLocalizedMessage());
            return false;
        }
        //System.out.println("path = "+path);
        FileObject pathFO = xmlFS.findResource(path);
        if (pathFO == null) {
            stderrNRListener.outputLine("Folder '"+path+"' not found on XML FS.");
            return false;
        }
        FileObject[] children = pathFO.getChildren();
        for (int i = 0; i < children.length; i++) {
            FileObject ch = children[i];
            String name = ch.getNameExt();
            if (ch.isFolder()) name += '/';
            String status = (String) ch.getAttribute("status");
            String locker = (String) ch.getAttribute("locker");
            String revision = (String) ch.getAttribute("revision");
            String sticky = (String) ch.getAttribute("sticky");
            String time = (String) ch.getAttribute("time");
            String date = (String) ch.getAttribute("date");
            String size = (String) ch.getAttribute("size");
            String attr = (String) ch.getAttribute("attr");
            String[] elements = new String[] { name, status, locker, revision, sticky, time, date, size, attr };
            stdoutListener.outputData(elements);
            filesByName.put(name, elements);
        }
        return true;
    }
    
}
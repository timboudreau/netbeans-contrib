/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.testprofiles.list;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.CacheStatuses;
import org.netbeans.modules.vcscore.cmdline.VcsListCommand;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;

//import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

public class ListFromXMLFS extends VcsListCommand {
    
    public ListFromXMLFS() {
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
            xmlFS = new XMLFileSystem(xml);
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
            String status;// = (String) ch.getAttribute("status");
            String locker = (String) ch.getAttribute("locker");
            String revision = (String) ch.getAttribute("revision");
            String sticky = (String) ch.getAttribute("sticky");
            String time = (String) ch.getAttribute("time");
            String date = (String) ch.getAttribute("date");
            String size = (String) ch.getAttribute("size");
            String attr = (String) ch.getAttribute("attr");
            File file = new File(dirFile, name);
            if (!file.exists()) {
                if (ch.isData()) {
                    status = "Needs Update";
                } else {
                    status = "Missing";
                }
            } else {
                if (file.isFile()) {
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
                            /*
                            try {
                                Date lmdate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).parse(dateStr);
                                reposModified = lmdate.getTime();
                            } catch (ParseException pex) {
                                stderrNRListener.outputLine(pex.getLocalizedMessage());
                            }
                             */
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
            if (ch.isFolder()) name += '/';
            String[] elements = new String[] { name, status, locker, revision, sticky, time, date, size, attr };
            stdoutListener.outputData(elements);
            filesByName.put(name, elements);
        }
        return true;
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
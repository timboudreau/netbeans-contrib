/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.genericnavigator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Utilities;

/**
 * Represents either a constructed or registered-on-disk regular expression
 * associated with a particular mime type.  These are installed by adding files
 * and subfolders to the "GenericNavigator/" folder in the system filesystem
 * via module layers.  So to add a pattern for HTML matching, you would provide
 * a file in "GenericNavigator/text/html".  To localize the display name of a
 * pattern, set the file name's display name by setting the file's
 * SystemFileSystem.localizingBundle attribute to point to the appropriate
 * resource bundle wherein there will be a key such as
 * "GenericNavigator/text/html/MyItem=My Item".
 * <p>
 * The file format is simple - each file is two lines of text.  The first line
 * is the regular expression to use, and the second is a comma-delimited list
 * of flags to pass to the constructor of java.util.regex.Pattern.  Example
 * (matches html headers):
 *<pre>
 *&lt;[Hh][1-6]&gt;(.*?)&lt;/[Hh][1-6]&gt;
 *MULTILINE,CASE_INSENSITIVE,DOTALL
 *</pre>
 *Such files are expected to be encoded in UTF-8.
 * @author Tim Boudreau
 */
final class PatternItem {
    public String displayName;
    private Pattern pattern;
    private final DataObject ob;
    private int flags = -1;
    private String patternString;

    private static final int[] FLAGS = new int[] {
        Pattern.CANON_EQ, Pattern.CASE_INSENSITIVE, Pattern.COMMENTS,
        Pattern.LITERAL, Pattern.MULTILINE, Pattern.UNICODE_CASE,
        Pattern.UNIX_LINES, Pattern.DOTALL,
    };

    private static final String[] FLAG_STRINGS = new String[] {
        "CANON_EQ", "CASE_INSENSITIVE", "COMMENTS", //NOI18N
        "LITERAL", "MULTILINE", "UNICODE_CASE", //NOI18N
        "UNIX_LINES", "DOTALL", //NOI18N
    };

    public static PatternItem DEFAULT = new PatternItem ("Headers", //NOI18N
            "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)", //NOI18N
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE); //NOI18N

    private static Map patternItems = new HashMap();
    private static final String FOLDER = "GenericNavigator/"; //NOI18N
    private static final String NAVIGATOR_REG_FLD = "Navigator/Panels";
    private static Set mimeTypes = null;

    private int[] includeGroups;

    //Unit tests will overwrite this:
    static FileObject rootfolder = Repository.getDefault().getDefaultFileSystem().findResource(
            FOLDER);

    PatternItem (DataObject ob) {
        this.ob = ob;
        if (ob == null) {
            throw new NullPointerException ("DataObject may not be null"); //NOI18N
        }
    }

    PatternItem (String displayName, String pattern, int flags) {
        this (displayName, pattern, flags, new int[] { 0 }, false);
    }

    PatternItem (String displayName, String pattern, int flags, int[] includeGroups, boolean stripHtml) {
        this.displayName = displayName;
        this.patternString = pattern;
        this.stripHtml = stripHtml;
        this.ob = null;
        if (includeGroups == null) {
            includeGroups = new int[] { 0 };
        }
        Arrays.sort (includeGroups);
        this.includeGroups = includeGroups;
        if (displayName == null) {
            throw new NullPointerException ("Display name null"); //NOI18N
        }
        if (pattern == null) {
            throw new NullPointerException ("Pattern name null"); //NOI18N
        }
        if (flags <= 0) {
            flags = 0;
        }
        this.flags = flags;
    }

    PatternItem (PatternItem other) {
        this.displayName = other.displayName;
        this.patternString = other.patternString;
        this.ob = other.ob;
        this.pattern = other.pattern;
        this.flags = other.flags;
    }

    public boolean differs (PatternItem other) {
        if (patternString == null) {
            getPatternString();
        }
        if (other == this) {
            return false;
        } else {
            boolean result = !other.getDisplayName().equals(getDisplayName());
            if (!result) {
                result = !other.getPatternString().equals(getPatternString());
            }
            if (!result) {
                result = other.getFlags() != getFlags();
            }
            if (!result) {
                result = other.isStripHtml() != isStripHtml();
            }
            if (!result) {
                result = !Arrays.equals (other.getIncludeGroups(), getIncludeGroups());
                if (result) {
                    System.err.println("OTHER: " + str (other.getIncludeGroups()) + " MINE: " +
                            str (getIncludeGroups()));
                }
            }
            return result;
        }
    }

    private String str(int[] x) {
        StringBuffer result = new StringBuffer (x.length * 3);
        result.append ('[');
        for (int i = 0; i < x.length; i++) {
            result.append (x[i]);
            if (i != x.length -1) {
                result.append (',');
            }
        }
        result.append (']');
        return result.toString();
    }

    public boolean equals (Object o) {
        boolean result = o instanceof PatternItem;
        if (result) {
            PatternItem pi = (PatternItem) o;
            String s = getPatternString();
            String other = pi.getPatternString();
            result = s == null && other == null || s != null && s.equals(other);
        }
        return result;
    }

    public int hashCode() {
        return getPatternString() == null ? -1 :
            getPatternString().hashCode() * 17;
    }

    public int getFlags() {
        if (patternString == null) {
            getPatternString();
        }
        return flags;
    }

    public String getDisplayName() {
        if (displayName == null) {
            assert ob != null;
            displayName = ob.getNodeDelegate().getDisplayName();
        }
        return displayName;
    }

    private boolean failed = false;
    public Pattern getPattern() {
        if (pattern == null && !failed) {
            pattern = createPattern();
            failed = pattern == null;
        }
        return pattern;
    }

    public String getPatternString() {
        if (patternString == null) {
            try {
                patternString = readPattern();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
                patternString =
                        "could not parse " + ob.getPrimaryFile().getPath(); //NOI18N
            }
        }
        return patternString;
    }

    public int[] getIncludeGroups() {
        if (patternString == null) {
            getPatternString();
        }
        if (includeGroups == null) {
            includeGroups = new int[] { 0 };
        }
        int[] result = (int[]) includeGroups.clone();
        return result;
    }

    public String toString() {
        return getDisplayName();
    }

    private String getOutputString() {
        StringBuffer result = new StringBuffer();
        result.append (getPatternString());
        result.append ('\n'); //NOI18N
        result.append (getFlagsString(flags));
        result.append ('\n'); //NOI18N
        result.append (getGroupsString(includeGroups));
        result.append ('\n'); //NOI18N
        if (stripHtml) {
            result.append (STRIP_HTML);
            result.append ('\n');
        }
        return result.toString();
    }

    public static String getGroupsString (int[] groups) {
        StringBuffer result = new StringBuffer();
        if (groups.length > 0) {
            for (int i = 0; i < groups.length; i++) {
                result.append (groups[i]);
                if (i != groups.length - 1) {
                    result.append (',');
                }
            }
        } else {
            result.append ("0"); //NOI18N
        }
        return result.toString();
    }

    public static String getFlagsString (int flags) {
        StringBuffer result = new StringBuffer();
        result.append ('\n'); //NOI18N
        for (int i = 0; i < FLAGS.length; i++) {
            if ((flags & FLAGS[i]) != 0) {
                result.append (FLAG_STRINGS[i]);
                result.append (',');
            }
        }
        int end = result.length() - 1;
        if (result.charAt(end) == ',') {
            result.delete(end, end);
        }
        return result.toString();
    }

    public void delete(String mimetype) throws IOException {
        if (ob != null) {
            //ensure things are cached so object can live on a little while
            getPattern();
            getDisplayName();
            ob.delete();
            maybeUnregisterNavPanelForMimeType(mimetype);
        } else {
            throw new IOException ("Not backed by a file: " + this);
        }
    }

    public void save (String mimetype, String displayName) throws IOException {
        save (mimetype, displayName, rootfolder);
    }

    public void save (String mimetype, String displayName, FileObject fob) throws IOException {
        //Clear the cache
        patternItems.remove(mimetype);
        FileObject fld = fob.getFileObject(mimetype);
        if (fld == null) {
            StringTokenizer tok = new StringTokenizer (mimetype, "/"); //NOI18N
            fld = fob;
            while (tok.hasMoreTokens()) {
                String dir = tok.nextToken();
                if (fld.getFileObject(dir) == null) {
                    fld = fld.createFolder(dir);
                } else {
                    fld = fld.getFileObject(dir);
                }
            }
        }
        FileObject ob = fld.getFileObject(displayName);
        if (ob == null) {
            ob = fld.createData(displayName);
        }
        FileLock lock = ob.lock();
        try {
            OutputStream out = new BufferedOutputStream (ob.getOutputStream(lock));
            try {
                String data = getOutputString();
                out.write(data.getBytes("UTF-8")); //NOI18N
            } finally {
                out.close();
            }
        registerNavPanelForMimeType(mimetype);
        } finally {
            lock.releaseLock();
        }
    }

    private Pattern createPattern() {
        try {
            return Pattern.compile(getPatternString());
        } catch (PatternSyntaxException e) {
            ErrorManager.getDefault().notify (e);
            return null;
        }
    }

    private String readPattern() throws IOException {
        assert ob != null;
        int read = 0;
        int sz = (int) ob.getPrimaryFile().getSize();
        byte[] bytes = new byte [ sz ];
        InputStream is = new BufferedInputStream (
                ob.getPrimaryFile().getInputStream());
        int pos = 0;
        StringBuffer data = new StringBuffer();
        try {
            int off = 0;
            while ((read = is.read(bytes)) != -1) {
                off += read;
                data.append (new String (bytes, "UTF-8"));
            }
        } finally {
            is.close();
        }
        if (data.length() == 0) {
            return null;
        } else {
            return parse (data.toString());
        }
    }

    public static final String STRIP_HTML = "stripMarkup";
    private boolean stripHtml = false;

    public boolean isStripHtml() {
        if (patternString == null) {
            getPatternString();
        }
        return stripHtml;
    }

    private String parse (String data) {
        StringTokenizer tok = new StringTokenizer (data, "\n"); //NOI18N
        String result = null;
outer:  for (int i=0; tok.hasMoreTokens(); i++) {
            String s = tok.nextToken().trim();
            switch (i) {
                case 0:
                    result = s;
                    break;
                case 1:
                    flags = parseFlags (s);
                    break;
                case 2:
                    includeGroups = parseIncludeGroups (s);
                    break;
                case 3:
                    stripHtml = s.toUpperCase().indexOf(STRIP_HTML.toUpperCase()) >= 0;
                    break;
                default :
                    break outer;
            }
        }
        if (flags == -1) {
            flags = 0;
        }
        return result;
    }

    static final int[] parseIncludeGroups (String line) {
        StringTokenizer tok = new StringTokenizer (line, ",");
        List ints = new ArrayList (tok.countTokens());
        int i=0;
        for (; tok.hasMoreTokens();) {
            String curr = tok.nextToken().trim();
            if (curr.length() > 0) {
                try {
                    ints.add (new Integer (curr));
                } catch (NumberFormatException nfe) {
                    ErrorManager.getDefault().notify (ErrorManager.INFORMATIONAL, nfe);
//                    return new int[] { 0 };
                }
                i++;
            }
        }
        Integer[] arr = (Integer[]) ints.toArray(new Integer[ints.size()]);
        int[] result = (int[]) Utilities.toPrimitiveArray(arr);
        Arrays.sort (result);
        return result;
    }

    static final int parseFlags (String flagsLine) {
        StringTokenizer tok = new StringTokenizer (flagsLine, ","); //NOI18N
        int result = 0;
        while (tok.hasMoreTokens()) {
            String item = tok.nextToken().trim();
            int ix = Arrays.asList (FLAG_STRINGS).indexOf(item);
            if (ix != -1) {
                result |= FLAGS[ix];
            }
        }
        return result;
    }

    static void changed() {
        patternItems.clear();
        GenericNavPanel.refreshAll();
    }

    public static PatternItem[] getDefaultItems(String mimetype) {
        assert mimetype != null;
        List result = (List) patternItems.get(mimetype);
        if (result == null) {
            DataObject[] obs;
            try {
                obs = getItemDataObjects(mimetype);
                result = new ArrayList(obs.length);
                for (int i = 0; i < obs.length; i++) {
                    result.add(new PatternItem (obs[i]));
                }
            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
                return new PatternItem[0];
            }
        }
        PatternItem[] r = (PatternItem[]) result.toArray(new PatternItem[result.size()]);
        return r;
    }

    private static DataObject[] getItemDataObjects(String mimetype) throws IOException {
        FileObject fob = Repository.getDefault().getDefaultFileSystem().findResource(
                FOLDER + mimetype);
        if (fob != null) {
            DataFolder fld = DataFolder.findFolder(fob);
            return fld.getChildren();
        } else {
            return new DataObject[0];
        }
    }

    public static FileObject getConfigRoot() {
//        FileObject root =
//                Repository.getDefault().getDefaultFileSystem().
//                getRoot().getFileObject(FOLDER);
//        return root;
        return rootfolder;
    }

    public static Set getSupportedMimeTypes() {
        if (mimeTypes == null) {
            mimeTypes = new HashSet();
            FileObject fob = getConfigRoot();

            FileObject[] kids = fob.getChildren();
            for (int i = 0; i < kids.length; i++) {
                if (kids[i].isFolder()) {
                    String first = kids[i].getName();
                    FileObject[] subs = kids[i].getChildren();
                    for (int j = 0; j < subs.length; j++) {
                        if (subs[j].isFolder()) {
                            String second = subs[j].getName();
                            mimeTypes.add (first + '/' + second); //NOI18N
                        }
                    }
                }
            }
        }
        return mimeTypes;
    }

    public static void registerNavPanelForMimeType (String mimetype) throws IOException {
        String path = NAVIGATOR_REG_FLD + '/' + mimetype;
        String fname = "org-netbeans-modules-genericnavigator-GenericNavPanel.instance";
        FileObject fob = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject (path);
        if (fob == null || fob.getFileObject(fname) == null) {
            FileObject curr = Repository.getDefault().getDefaultFileSystem().getRoot();
            for (StringTokenizer tok = new StringTokenizer (path, "/");tok.hasMoreTokens();) {
                String fld = tok.nextToken();

                if (curr.getFileObject(fld) == null) {
                    curr = curr.createFolder(fld);
                } else {
                    curr = curr.getFileObject(fld);
                }
            }
            fob = curr;
            FileObject theFile = fob.getFileObject (fname);
            if (theFile == null) {
                theFile = fob.createData(fname);
            }
        }
    }

    public static void maybeUnregisterNavPanelForMimeType (String mimetype) throws IOException {
        FileObject ourMimeRegistryFolder= Repository.getDefault().getDefaultFileSystem().findResource(
                FOLDER + '/' + mimetype);
        if (ourMimeRegistryFolder == null || ourMimeRegistryFolder.getChildren().length == 0) {
            String path = FOLDER + mimetype;
            FileObject fob = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject (path);
            if (fob != null) {
                String fname = "org-netbeans-modules-genericnavigator-GenericNavPanel.instance";
                FileObject ob = fob.getFileObject(fname);
                if (ob != null) {
                    ob.delete();
                }
            }
        }
    }
}

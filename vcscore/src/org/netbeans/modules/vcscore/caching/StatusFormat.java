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
package org.netbeans.modules.vcscore.caching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.Statuses;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.io.File;

/**
 * Defines status format as communicated from REFRESH command
 * and supports disk cache entries formatting.
 *
 * TODO move to more perspective package, probably commands or turbo
 *
 * @author Petr Kuzel caching independent code extracted from RefreshCommandSupport code by
 * @author Martin Entlicher
 */
public final class StatusFormat {
    /**
     * The index of file name in the cache.
     */
    public static final int ELEMENT_INDEX_FILE_NAME = 0;
    /**
     * The index of file status in the cache.
     */
    public static final int ELEMENT_INDEX_STATUS = 1;
    /**
     * The index of file locker in the cache.
     */
    public static final int ELEMENT_INDEX_LOCKER = 2;
    /**
     * The index of file revision in the cache.
     */
    public static final int ELEMENT_INDEX_REVISION = 3;
    /**
     * The index of file sticky information in the cache (i.e. the current branch)
     */
    public static final int ELEMENT_INDEX_STICKY = 4;
    /**
     * The index of file additional attribute in the cache.
     */
    public static final int ELEMENT_INDEX_ATTR = 5;
    /**
     * The index of file size in the cache.
     */
    public static final int ELEMENT_INDEX_SIZE = 6;
    /**
     * The index of file date in the cache.
     */
    public static final int ELEMENT_INDEX_DATE = 7;
    /**
     * The index of file time in the cache.
     */
    public static final int ELEMENT_INDEX_TIME = 8;
    /**
     * The number of file attributes in the cache.
     */
    public static final int NUM_ELEMENTS = 9;

    private static final String DIRECTORY_CACHE_ID = "D"; // NOI18N
    /**
     * The annotation pattern of a file name.
     */
    public static final String ANNOTATION_PATTERN_FILE_NAME = "fileName"; // NOI18N
    /**
     * The annotation pattern of a file status.
     */
    public static final String ANNOTATION_PATTERN_STATUS = "status"; // NOI18N
    /**
     * The annotation pattern of a file locker.
     */
    public static final String ANNOTATION_PATTERN_LOCKER = "locker"; // NOI18N
    /**
     * The annotation pattern of a file revision.
     */
    public static final String ANNOTATION_PATTERN_REVISION = "revision"; // NOI18N
    /**
     * The annotation pattern of a file sticky info.
     */
    public static final String ANNOTATION_PATTERN_STICKY = "sticky"; // NOI18N
    /**
     * The annotation pattern of a file additional attribute.
     */
    public static final String ANNOTATION_PATTERN_ATTR = "attribute"; // NOI18N
    /**
     * The annotation pattern of a file size.
     */
    public static final String ANNOTATION_PATTERN_SIZE = "size"; // NOI18N
    /**
     * The annotation pattern of a file date.
     */
    public static final String ANNOTATION_PATTERN_DATE = "date"; // NOI18N
    /**
     * The annotation pattern of a file time.
     */
    public static final String ANNOTATION_PATTERN_TIME = "time"; // NOI18N
    /**
     * When the attributes of the set of files differ, do not show them.
     */
    public static final int MULTI_FILES_ANNOTATION_EMPTY = 0;
    /**
     * When the attributes of the set of files differ, show them as a list.
     */
    public static final int MULTI_FILES_ANNOTATION_LIST = 1;
    /**
     * When the attributes of the set of files differ, show a "Not In Synch" status instead.
     */
    public static final int MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR = 2;
    /* ${fileName} $[? status] [[${status}$[? revision] [, revision] []]] []$[? revision] [revision] []$[? locker] [(${locker})] [] */
    public static final String DEFAULT_ANNOTATION_PATTERN = "${"+ANNOTATION_PATTERN_FILE_NAME+"}"+ // NOI18N
    "$[? "+ANNOTATION_PATTERN_STATUS+"] "+ // NOI18N
        "[ [${"+ANNOTATION_PATTERN_STATUS+"}$[? "+ANNOTATION_PATTERN_REVISION+"] [; ${"+ANNOTATION_PATTERN_REVISION+"}] []]] "+ // NOI18N
        "["+"$[? "+ANNOTATION_PATTERN_REVISION+"] [ ${"+ANNOTATION_PATTERN_REVISION+"}] []"+"]"+ // NOI18N
    "$[? "+ANNOTATION_PATTERN_LOCKER+"][ (${"+ANNOTATION_PATTERN_LOCKER+"})] []" + // NOI18N
    "$[? "+ANNOTATION_PATTERN_STICKY+"][ (${"+ANNOTATION_PATTERN_STICKY+"})] []"; // NOI18N
    public static final int[] DEFAULT_MULTI_FILES_ANNOTATION_TYPES = { MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR, MULTI_FILES_ANNOTATION_LIST, MULTI_FILES_ANNOTATION_EMPTY,
                                                                       MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY,
                                                                       MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY };
    public static final String DEFAULT_MULTI_FILES_ANNOTATION_DELIMETER = ", "; // NOI18N

    /**
     * Extract the file name from the array of elements.
     */
    public static String getFileName(String[] elements) {
        if (elements.length < NUM_ELEMENTS) return null;
        return elements[ELEMENT_INDEX_FILE_NAME];
    }

    /** Decodes raw data from string encoded by {@link #getLineFromElements(String[])}. */
    public static String[] getElementsFromLine(String line) {
        boolean dir = false;
        int index = 0;
        if (line.charAt(index) == '/') index++;
        String[] elements = new String[NUM_ELEMENTS];
        for(int i = 0; index < line.length() && i < NUM_ELEMENTS; i++) {
            int next = line.indexOf('/', index);
            if (next < 0) break;
            String element;
            if (next > 0 && line.substring(next - 1).startsWith(" // ")) {
                StringBuffer adjusted = new StringBuffer();
                int begin = index;
                int end = next - 1;
                while (true) {
                    if (begin < end) adjusted.append(line.substring(begin, end));
                    adjusted.append('/');
                    int nextSl = line.indexOf('/', end + " // ".length());
                    int nextMySep = line.indexOf(" // ", end + " // ".length());
                    begin = end + " // ".length();
                    if (nextMySep >= 0 && nextMySep < nextSl) {
                        end = nextMySep;
                    } else {
                        end = (nextSl > 0) ? nextSl : line.length();
                        adjusted.append(line.substring(begin, end));
                        break;
                    }
                }
                element = adjusted.toString();
                next = end;
            } else {
                element = new String(line.substring(index, next)); // I want to have a completely new object so that line can be G.C.
            }

            if ("#NULL#".equals(element)) element = null;  // NOI18N

            index = next + 1;
            if (i == 0 && DIRECTORY_CACHE_ID.equals(element)) {
                dir = true;
                i--;
                continue;
            }
            elements[i] = element;
        }
        if (dir) elements[ELEMENT_INDEX_FILE_NAME] += "/"; // NOI18N
        return elements;
    }

    /** Encodes raw data to string decodable by {@link #getElementsFromLine(String)}. */
    public static String getLineFromElements(String[] elements) {
        if (elements.length < NUM_ELEMENTS) return null;
        boolean dir = elements[ELEMENT_INDEX_FILE_NAME].endsWith("/"); // NOI18N
        String firstElement = (dir)
            ? elements[ELEMENT_INDEX_FILE_NAME].substring(0, elements[ELEMENT_INDEX_FILE_NAME].length() - 1)
            : elements[ELEMENT_INDEX_FILE_NAME];
        StringBuffer line = new StringBuffer();
        if (dir) line.append(DIRECTORY_CACHE_ID);
        line.append('/'); // NOI18N
        line.append(escapeElement(firstElement));
        line.append('/'); // NOI18N
        for(int i = 1; i < NUM_ELEMENTS; i++) {
            line.append(escapeElement(elements[i]));
            line.append('/'); // NOI18N
        }
        return line.toString();
    }

    /** escape '/' separators */
    private static String escapeElement(String element) {
        if (element == null) return "#NULL#";  // NOI18N
        int index = element.indexOf('/');
        if (index < 0) return element;
        StringBuffer adjusted = new StringBuffer();
        int begin;
        for (begin = 0; index >= 0 && index < element.length(); begin = index + 1, index = element.indexOf('/', begin)) {
            adjusted.append(element.substring(begin, index));
            adjusted.append(" // ");
        }
        if (begin < element.length()) {
            adjusted.append(element.substring(begin));
        }
        return adjusted.toString();
    }
    
    /** Adjusts "${" and "$[" strings so that they are not interpreted by the expansion method. */
    private static String adjustVarRef(String str) {
        str = org.openide.util.Utilities.replaceString(str, "${", "\\${"); // NOI18N
        str = org.openide.util.Utilities.replaceString(str, "$[", "\\$["); // NOI18N
        return str;
    }

    // TODO Following methods are called only by FS. Move closer to FS to avoid public contact with side efects

    /**
     * Get the annotation line for a file, it takes last known status
     * from memory and if unknown it schedules background fetching
     * (pretty complicated contract).
     *
     * @param name the name of the node which is annotated
     * @param fo fileobject to annotate
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param annotationStyle defines what varibles values are provided into pattern
     * input {@link org.netbeans.modules.vcscore.settings.GeneralVcsSettings}
     * @return the annotation pattern filled up with proper attributes
     */
    public static String getStatusAnnotation(String name, FileObject fo, String annotationPattern,
                                             int annotationStyle, Map possibleFileStatusInfoMap) {
        return getStatusAnnotation(name, fo, annotationPattern, annotationStyle, null, possibleFileStatusInfoMap);
    }

    /**
     * Get the annotation line for a file, it takes last known status
     * from memory and if unknown it schedules background fetching
     * (pretty complicated contract).
     */
    public static String getStatusAnnotation(String name, FileObject fileObject,
                                             String pattern, int annotationStyle, Map extraVars,
                                             Map possibleFileStatusInfoMap) {
        Hashtable vars = new Hashtable();
        if (extraVars != null) vars.putAll(extraVars);
        vars.put(StatusFormat.ANNOTATION_PATTERN_FILE_NAME, adjustVarRef(name));

        String status;
        FileProperties fprops = null;
        FileObject vfo = (FileObject) fileObject.getAttribute(VcsAttributes.VCS_NATIVE_FILEOBJECT);
        if (vfo != null && vfo.isRoot()) {
            // in favourites view mark versioned workspace roots
            status = NbBundle.getMessage(StatusFormat.class, "vfs_root");
        } else {
            Turbo.prepareMeta(fileObject);
            fprops = Turbo.getMemoryMeta(fileObject);
            status = FileProperties.getStatus(fprops);
            FileStatusInfo statusInfo = (FileStatusInfo) possibleFileStatusInfoMap.get(status);
            if (statusInfo != null) {
                if (annotationStyle == GeneralVcsSettings.FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY
                    && (statusInfo.represents(FileStatusInfo.UP_TO_DATE)
                       || statusInfo.represents(Statuses.createIgnoredFileInfo())
                    )) {
                    return name;
                }
                status = statusInfo.getDisplayName();
            }
        }
        return substitute(pattern, status, fprops, vars);

    }

    /**
     * Get the annotation line for a list of files.
     * @param name the object file name
     * @param files the FileObjects to annotate
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param multiFilesAnnotationTypes the annotation types for individual attributes.
     *        Values of this files are <code>MULTI_FILES_ANNOTATION_*<code> constants.
     * @return the annotation pattern filled up with proper attributes
     */
    public static String getStatusAnnotation(String name, Collection files, String pattern,
                                             int annotationStyle, //Map extraVars,
                                             Map possibleFileStatusInfoMap,
                                             int[] multiFilesAnnotationTypes) {
        Hashtable vars = new Hashtable();
        //if (extraVars != null) vars.putAll(extraVars);
        vars.put(StatusFormat.ANNOTATION_PATTERN_FILE_NAME, adjustVarRef(name));

        List statuses = new ArrayList(files.size());
        FileProperties[] properties = new FileProperties[files.size()];
        boolean isModified = (annotationStyle != GeneralVcsSettings.FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY);
        int i = 0;
        for (Iterator it = files.iterator(); it.hasNext(); i++) {
            FileObject fileObject = (FileObject) it.next();
            FileProperties fprops = Turbo.getMemoryMeta(fileObject);
            String status = FileProperties.getStatus(fprops);
            FileStatusInfo statusInfo = (FileStatusInfo) possibleFileStatusInfoMap.get(status);
            if (statusInfo != null) {
                if (!isModified
                    && !(statusInfo.represents(FileStatusInfo.UP_TO_DATE)
                       || statusInfo.represents(Statuses.createIgnoredFileInfo())
                    )) {
                    isModified = true;
                }
                status = statusInfo.getDisplayName();
            }
            if (!statuses.contains(status)) statuses.add(status);
            properties[i] = fprops;
        }
        if (annotationStyle == GeneralVcsSettings.FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY && !isModified) {
            return name;
        }
        String nSynch = (String) possibleFileStatusInfoMap.get("GENERIC_STATUS_NOT_IN_SYNCH"); // NOI18N
        if (nSynch == null) nSynch = ""; // NOI18N
        
        String sharedStatus = mergeAttributes((String[]) statuses.toArray(new String[0]),
                                              multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_STATUS],
                                              nSynch);
        FileProperties sharedProps = mergeProperties(properties, multiFilesAnnotationTypes);
        
        return substitute(pattern, sharedStatus, sharedProps, vars);
    }
    
    private static String mergeAttributes(String[] values, int annotationType,
                                          String nSynch) {
        if (values.length == 0) return ""; // NOI18N
        String first = values[0];
        int n = values.length;
        int i = 1;
        for (i = 1; i < n; i++) {
            if (first == null && values[i] != null || first != null && !first.equals(values[i])) {
                break;
            }
        }
        boolean differ = i < n;
        switch (annotationType) {
            case MULTI_FILES_ANNOTATION_EMPTY:
                if (differ) return ""; // NOI18N
                else {
                    return first;
                }
            case MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR:
                if (differ) return nSynch;
                else {
                    return first;
                }
            case MULTI_FILES_ANNOTATION_LIST:
                if (!differ) return first;
                StringBuffer buf = new StringBuffer(first);
                for (int j = 1; j < n; j++) {
                    if (values[j] != null) {
                        buf.append(DEFAULT_MULTI_FILES_ANNOTATION_DELIMETER + values[j]);
                    }
                }
                return buf.toString();
            default: return ""; // NOI18N
        }
    }
    
    private static FileProperties mergeProperties(FileProperties[] properties,
                                                  int[] multiFilesAnnotationTypes) {
        FileProperties fProps = new FileProperties();
        int n = properties.length;
        String[] values = new String[n];
        for (int i = 0; i < n; i++) {
            if (properties[i] != null) {
                values[i] = properties[i].getLocker();
            } else {
                values[i] = null;
            }
        }
        fProps.setLocker(mergeAttributes(values, multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_LOCKER], ""));
        for (int i = 0; i < n; i++) {
            if (properties[i] != null) {
                values[i] = properties[i].getRevision();
            } else {
                values[i] = null;
            }
        }
        fProps.setRevision(mergeAttributes(values, multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_REVISION], ""));
        for (int i = 0; i < n; i++) {
            if (properties[i] != null) {
                values[i] = properties[i].getSticky();
            } else {
                values[i] = null;
            }
        }
        fProps.setSticky(mergeAttributes(values, multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_STICKY], ""));
        for (int i = 0; i < n; i++) {
            if (properties[i] != null) {
                values[i] = properties[i].getAttr();
            } else {
                values[i] = null;
            }
        }
        fProps.setAttr(mergeAttributes(values, multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_ATTR], ""));
        for (int i = 0; i < n; i++) {
            if (properties[i] != null) {
                values[i] = properties[i].getDate();
            } else {
                values[i] = null;
            }
        }
        fProps.setDate(mergeAttributes(values, multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_DATE], ""));
        for (int i = 0; i < n; i++) {
            if (properties[i] != null) {
                values[i] = properties[i].getTime();
            } else {
                values[i] = null;
            }
        }
        fProps.setTime(mergeAttributes(values, multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_TIME], ""));
        return fProps;
    }
    
    private static String annotationFontColor;
    private static String getAnnotationFontColor() {
        if (annotationFontColor == null) {
            String fontColor = "<font color='!controlShadow'>";
            if(javax.swing.UIManager.getDefaults().getColor("Tree.selectionBackground").equals(javax.swing.UIManager.getDefaults().getColor("controlShadow"))){
                fontColor = "<font color='!Tree.selectionBorderColor'>";
            }
            annotationFontColor = fontColor;
        }
        return annotationFontColor;
    }

    private static String escapeSpecialHTMLCharacters(String str) {
        str = org.openide.util.Utilities.replaceString(str, "&", "&amp;");
        str = org.openide.util.Utilities.replaceString(str, "<", "&lt;");
        str = org.openide.util.Utilities.replaceString(str, ">", "&gt;");
        str = org.openide.util.Utilities.replaceString(str, "\"", "&quot;");
        return str;
    }

    /**
     * Get the annotation line in a HTML format for a file.
     * It takes last known status
     * from memory and if unknown it schedules background fetching
     * (pretty complicated contract).
     *
     * @param name the name of the node which is annotated
     * @param fo fileobject to annotate
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param annotationStyle defines what varibles values are provided into pattern
     * input {@link org.netbeans.modules.vcscore.settings.GeneralVcsSettings}
     * @return the annotation pattern filled up with proper attributes
     */
    public static String getHtmlStatusAnnotation(String name, FileObject fo, final String annotationPattern,
                                                 int annotationStyle, Map possibleFileStatusInfoMap) {

        Hashtable vars = new Hashtable();
        name = escapeSpecialHTMLCharacters(name);
        // Special "light gray" color for the file annotation
        name += getAnnotationFontColor();
        vars.put(StatusFormat.ANNOTATION_PATTERN_FILE_NAME, adjustVarRef(name));
        if ("${fileName}".equals(annotationPattern)) { // NOI18N
            return Variables.expand(vars, annotationPattern, false);
        }

        String status;
        FileProperties fprops = null;
        FileObject vfo = (FileObject) fo.getAttribute(VcsAttributes.VCS_NATIVE_FILEOBJECT);
        if (vfo != null && vfo.isRoot()) {
            // in favourites view mark versioned workspace roots
            status = NbBundle.getMessage(StatusFormat.class, "vfs_root");
            status = escapeSpecialHTMLCharacters(status);
        } else {
            Turbo.prepareMeta(fo);
            fprops = Turbo.getMemoryMeta(fo);
            status = FileProperties.getStatus(fprops);

            FileStatusInfo statusInfo = (FileStatusInfo) possibleFileStatusInfoMap.get(status);
            if (statusInfo != null) {
                if (annotationStyle == GeneralVcsSettings.FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY
                    && (statusInfo.represents(FileStatusInfo.UP_TO_DATE)
                       || statusInfo.represents(Statuses.createIgnoredFileInfo())
                    )) {
                    return name;
                }
                status = statusInfo.getDisplayName();
                status = escapeSpecialHTMLCharacters(status);
                status = colorifyStatus(statusInfo, status);
            } else {
                status = escapeSpecialHTMLCharacters(status);
            }
        }

        return substitute(annotationPattern, status, fprops, vars);
    }
    
    private static String colorifyStatus(FileStatusInfo statusInfo, String status) {
        if (statusInfo instanceof javax.swing.colorchooser.ColorSelectionModel) {
            java.awt.Color c = ((javax.swing.colorchooser.ColorSelectionModel) statusInfo).getSelectedColor();
            if (c != null) {
                String r = Integer.toHexString(c.getRed());
                if (r.length() == 1) r = "0"+r;
                String g = Integer.toHexString(c.getGreen());
                if (g.length() == 1) g = "0"+g;
                String b = Integer.toHexString(c.getBlue());
                if (b.length() == 1) b = "0"+b;
                status = "<font color=#"+r+g+b+">" + status + "</font>"; //NOI18N
            }
        }
        return status;
    }

    /**
     * Get the annotation line in a HTML format for a file.
     * It takes last known status
     * from memory and if unknown it schedules background fetching
     *
     * @param name the name of the node which is annotated
     * @param fo fileobject to annotate
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param annotationStyle defines what varibles values are provided into pattern
     * input {@link org.netbeans.modules.vcscore.settings.GeneralVcsSettings}
     * @return the annotation pattern filled up with proper attributes
     */
    public static String getHtmlStatusAnnotation(String name, Collection files, String pattern,
                                                 int annotationStyle,
                                                 Map possibleFileStatusInfoMap,
                                                 int[] multiFilesAnnotationTypes) {
        
        //assert name.indexOf(File.separatorChar) == -1 : "#51577 trap " + name;  // NOI18N
        //The name *can* have a slash in it's name - e.g. "I/O APIs" - display name of a project node.

        Hashtable vars = new Hashtable();
        name = escapeSpecialHTMLCharacters(name);
        // Special "light gray" color for the file annotation
        name += getAnnotationFontColor();
        vars.put(StatusFormat.ANNOTATION_PATTERN_FILE_NAME, adjustVarRef(name));
        if ("${fileName}".equals(pattern)) { // NOI18N
            return Variables.expand(vars, pattern, false);
        }
        
        List statuses = new ArrayList(files.size());
        FileProperties[] properties = new FileProperties[files.size()];
        boolean isModified = (annotationStyle != GeneralVcsSettings.FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY);
        int i = 0;
        for (Iterator it = files.iterator(); it.hasNext(); i++) {
            FileObject fileObject = (FileObject) it.next();
            FileProperties fprops = Turbo.getMemoryMeta(fileObject);
            String status = FileProperties.getStatus(fprops);
            FileStatusInfo statusInfo = (FileStatusInfo) possibleFileStatusInfoMap.get(status);
            if (statusInfo != null) {
                if (!isModified
                    && !(statusInfo.represents(FileStatusInfo.UP_TO_DATE)
                       || statusInfo.represents(Statuses.createIgnoredFileInfo())
                    )) {
                    isModified = true;
                }
                status = statusInfo.getDisplayName();
                status = escapeSpecialHTMLCharacters(status);
                status = colorifyStatus(statusInfo, status);
            } else {
                status = escapeSpecialHTMLCharacters(status);
            }
            if (!statuses.contains(status)) statuses.add(status);
            properties[i] = fprops;
        }
        if (annotationStyle == GeneralVcsSettings.FILE_ANNOTATION_FULL_FOR_MODIFIED_ONLY && !isModified) {
            return name;
        }
        String nSynch = (String) possibleFileStatusInfoMap.get("GENERIC_STATUS_NOT_IN_SYNCH"); // NOI18N
        if (nSynch == null) nSynch = ""; // NOI18N
        
        String sharedStatus = mergeAttributes((String[]) statuses.toArray(new String[0]),
                                              multiFilesAnnotationTypes[StatusFormat.ELEMENT_INDEX_STATUS],
                                              nSynch);
        FileProperties sharedProps = mergeProperties(properties, multiFilesAnnotationTypes);
        
        return substitute(pattern, sharedStatus, sharedProps, vars);
    }

    private static String substitute(String annotationPattern, String s, FileProperties fprops, Map vars) {
        if (s != null) vars.put(ANNOTATION_PATTERN_STATUS, s);
        if (fprops != null) {
            s = fprops.getLocker();
            if (s != null) vars.put(ANNOTATION_PATTERN_LOCKER, s);
            s = fprops.getRevision();
            if (s != null) vars.put(ANNOTATION_PATTERN_REVISION, s);
            s = fprops.getSticky();
            if (s != null) vars.put(ANNOTATION_PATTERN_STICKY, s);
            s = fprops.getSizeAsString();
            if (s != null) vars.put(ANNOTATION_PATTERN_SIZE, s);
            s = fprops.getAttr();
            if (s != null) vars.put(ANNOTATION_PATTERN_ATTR, s);
            s = fprops.getDate();
            if (s != null) vars.put(ANNOTATION_PATTERN_DATE, s);
            s = fprops.getTime();
            if (s != null) vars.put(ANNOTATION_PATTERN_TIME, s);
        }
        return Variables.expand(vars, annotationPattern, false);

    }

    /** Verifies user entered pattern. */
    public static boolean isValidAnnotationPattern(String pattern) {

        // TODO convert to RefreshCommandSupport independent implementation

        final String[] testStatus = { "" };
        FileStatusProvider testProvider = new FileStatusProvider() {
            public Set getPossibleFileStatusInfos() {
                return java.util.Collections.EMPTY_SET;
            }
            public String getNotInSynchStatus() { return "NSynch"; } // NOI18N
            public String getFileStatus(String fullName) {
                return testStatus[0];
            }
            public FileStatusInfo getFileStatusInfo(String fullName) { return null; }
            public String getFileLocker(String fullName) {
                return testStatus[0];
            }
            public String getFileRevision(String fullName) {
                return testStatus[0];
            }
            public String getFileSticky(String fullName) {
                return testStatus[0];
            }
            public String getFileAttribute(String fullName) {
                return testStatus[0];
            }
            public String getFileSize(String fullName) {
                return testStatus[0];
            }
            public String getFileDate(String fullName) {
                return testStatus[0];
            }
            public String getFileTime(String fullName) {
                return testStatus[0];
            }
            public void setFileStatus(String path, String status) {}
            public void setFileModified(String path) {}
            public String getLocalFileStatus() { return "Local"; } // NOI18N
            public void refreshDir(String path) {}
            public void refreshDirRecursive(String path) {}
        };
        String annot = getHtmlStatusAnnotation("name", "full/name", pattern, testProvider, new Hashtable()); // NOI18N
        java.awt.image.BufferedImage bimage =
            new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_3BYTE_BGR);
        try {
            org.openide.awt.HtmlRenderer.renderHTML(annot, bimage.getGraphics(), 0, 0,
                0, 0, bimage.getGraphics().getFont(), bimage.getGraphics().getColor(), 0, true);
        } catch (Exception ex) {
            //ex.printStackTrace();
            //System.out.println("INVALID HTML");
            return false;
        }
        testStatus[0] = "test"; // NOI18N
        annot = getHtmlStatusAnnotation("name", "full/name", pattern, testProvider, new Hashtable()); // NOI18N
        try {
            org.openide.awt.HtmlRenderer.renderHTML(annot, bimage.getGraphics(), 0, 0,
                0, 0, bimage.getGraphics().getFont(), bimage.getGraphics().getColor(), 0, true);
        } catch (Exception ex) {
            //ex.printStackTrace();
            //System.out.println("INVALID HTML");
            return false;
        }
        //System.out.println("VALID HTML");
        return true;
    }

    /**
     * Get the annotation line in a HTML format for a file.
     * @param name the object file name
     * @param fullName the full path of the file with respect to the filesystem root
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param statusProvider the provider of the status attributes information
     * @return the annotation pattern filled up with proper attributes
     */
    public static String getHtmlStatusAnnotation(String name, String fullName, String annotationPattern,
                                                 FileStatusProvider statusProvider, Hashtable additionalVars) {
        Hashtable vars = new Hashtable();
        if (additionalVars != null) vars.putAll(additionalVars);
        name = escapeSpecialHTMLCharacters(name);
        // Special "light gray" color for the file annotation
        name += getAnnotationFontColor();
        vars.put(ANNOTATION_PATTERN_FILE_NAME, adjustVarRef(name));
        if ("${fileName}".equals(annotationPattern)) { // NOI18N
            return Variables.expand(vars, annotationPattern, false);
        }
        //String status = statusProvider.getFileStatus(fullName);
        FileStatusInfo statusInfo = statusProvider.getFileStatusInfo(fullName);
        String status;
        if (statusInfo != null) {
            status = statusInfo.getDisplayName();
            status = escapeSpecialHTMLCharacters(status);
            if (statusInfo instanceof javax.swing.colorchooser.ColorSelectionModel) {
                java.awt.Color c = ((javax.swing.colorchooser.ColorSelectionModel) statusInfo).getSelectedColor();
                if (c != null) {
                    String r = Integer.toHexString(c.getRed());
                    if (r.length() == 1) r = "0"+r;
                    String g = Integer.toHexString(c.getGreen());
                    if (g.length() == 1) g = "0"+g;
                    String b = Integer.toHexString(c.getBlue());
                    if (b.length() == 1) b = "0"+b;
                    status = "<font color=#"+r+g+b+">" + status + "</font>"; //NOI18N
                }
            }
        } else {
            status = statusProvider.getFileStatus(fullName);
            status = escapeSpecialHTMLCharacters(status);
        }
        return createStatusAnnotation(status, vars, fullName, annotationPattern, statusProvider, true);
    }

    private static String createStatusAnnotation(String status, Hashtable vars,
                                                 String fullName, String annotationPattern,
                                                 FileStatusProvider statusProvider, boolean escapeHTML) {
        if (status != null) vars.put(ANNOTATION_PATTERN_STATUS, status);
        status = statusProvider.getFileLocker(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_LOCKER, escapeSpecialHTMLCharacters(status));
        status = statusProvider.getFileRevision(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_REVISION, escapeSpecialHTMLCharacters(status));
        status = statusProvider.getFileSticky(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_STICKY, escapeSpecialHTMLCharacters(status));
        status = statusProvider.getFileSize(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_SIZE, escapeSpecialHTMLCharacters(status));
        status = statusProvider.getFileAttribute(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_ATTR, escapeSpecialHTMLCharacters(status));
        status = statusProvider.getFileDate(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_DATE, escapeSpecialHTMLCharacters(status));
        status = statusProvider.getFileTime(fullName);
        if (status != null) vars.put(ANNOTATION_PATTERN_TIME, escapeSpecialHTMLCharacters(status));
        //System.out.println("vars = "+vars+",\npattern = "+annotationPattern+",\nexpansion = "+Variables.expandFast(vars, annotationPattern, false));
        return Variables.expand(vars, annotationPattern, false);
    }

}

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
package org.netbeans.modules.vcscore.caching;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.FileObject;

import java.util.Hashtable;
import java.util.Map;

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

}

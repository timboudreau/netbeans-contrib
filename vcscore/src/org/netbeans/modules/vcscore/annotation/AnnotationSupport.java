/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.annotation;

import java.util.*;
import java.io.File;

import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.cache.*;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * This class provides means of creating a mutable annotation of the data object.
 * 
 *
 * @author Milos Kleint
 */
public class AnnotationSupport extends Object {

    
    /**
     * The annotation pattern of a file name.
     */
    public static final String ANNOTATION_PATTERN_FILE_NAME = "fileName";
    /**
     * The annotation pattern of a file status.
     */
    public static final String ANNOTATION_PATTERN_STATUS = "status";
    /**
     * The annotation pattern of a file locker.
     */
    public static final String ANNOTATION_PATTERN_LOCKER = "locker";
    /**
     * The annotation pattern of a file revision.
     */
    public static final String ANNOTATION_PATTERN_REVISION = "revision";
    /**
     * The annotation pattern of a file sticky info.
     */
    public static final String ANNOTATION_PATTERN_STICKY = "sticky";
    /**
     * The annotation pattern of a file additional attribute.
     */
    public static final String ANNOTATION_PATTERN_ATTR = "attribute";
    /**
     * The annotation pattern of a file size.
     */
    public static final String ANNOTATION_PATTERN_SIZE = "size";
    /**
     * The annotation pattern of a file date.
     */
    public static final String ANNOTATION_PATTERN_DATE = "date";
    /**
     * The annotation pattern of a file time.
     */
    public static final String ANNOTATION_PATTERN_TIME = "time";

    
    /**
     * When the attributes of the set of files differ, do not show them.
     */
    public static final int MULTI_FILES_ANNOTATION_EMPTY = 0;
    private static final Integer MULTI_FILES_PATT_ANN_EMPTY = new Integer(MULTI_FILES_ANNOTATION_EMPTY);
    
    /**
     * When the attributes of the set of files differ, show them as a list.
     */
    public static final int MULTI_FILES_ANNOTATION_LIST = 1;
    private static final Integer MULTI_FILES_PATT_ANN_LIST = new Integer(MULTI_FILES_ANNOTATION_LIST);
    /**
     * When the attributes of the set of files differ, show a "Not In Synch" status instead.
     */
    public static final int MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR = 2;
    private static final Integer MULTI_FILES_PATT_ANN_NOT_SYNCH = new Integer(MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR);

    /* ${fileName} $[? status] [[${status}$[? revision] [, revision] []]] []$[? revision] [revision] []$[? locker] [(${locker})] [] */
/*    public static final String DEFAULT_ANNOTATION_PATTERN = "${"+ANNOTATION_PATTERN_FILE_NAME+"}"+
    "$[? "+ANNOTATION_PATTERN_STATUS+"] "+
        "[ [${"+ANNOTATION_PATTERN_STATUS+"}$[? "+ANNOTATION_PATTERN_REVISION+"] [; ${"+ANNOTATION_PATTERN_REVISION+"}] []]] "+
        "["+"$[? "+ANNOTATION_PATTERN_REVISION+"] [ ${"+ANNOTATION_PATTERN_REVISION+"}] []"+"]"+
    "$[? "+ANNOTATION_PATTERN_LOCKER+"][ (${"+ANNOTATION_PATTERN_LOCKER+"})] []" +
    "$[? "+ANNOTATION_PATTERN_STICKY+"][ (${"+ANNOTATION_PATTERN_STICKY+"})] []";
    public static final int[] DEFAULT_MULTI_FILES_ANNOTATION_TYPES = { MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR, MULTI_FILES_ANNOTATION_LIST, MULTI_FILES_ANNOTATION_EMPTY,
                                                                       MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY,
                                                                       MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY, MULTI_FILES_ANNOTATION_EMPTY };
 */
//    public static final String DEFAULT_MULTI_FILES_ANNOTATION_DELIMETER = ", ";
    
    
    private HashMap annotationPatterns;
    
    private static AnnotationSupport instance = null;
    
    /** Creates new RefreshCommandSupport */
    private AnnotationSupport() {
        annotationPatterns = new HashMap();
    }

    public static AnnotationSupport getInstance() {
        if (instance == null) {
            instance = new AnnotationSupport();
        }
        return instance;
    }
    
    public AnnotationSupport.PatternType registerPatternType(String id) {
        PatternType pattType = (PatternType)annotationPatterns.get(id);
        if (pattType == null) {
            pattType = new PatternType(id);
            annotationPatterns.put(id, pattType);
        }
        return pattType;
    }

    public void unregisterPatternType(String id) {
        Object obj = annotationPatterns.remove(id);
    }
    
    /**
     * Extract the file name from the array of elements.
     */
/**    public static String getFileName(String[] elements) {
        if (elements.length < NUM_ELEMENTS) return null;
        return elements[ELEMENT_INDEX_FILE_NAME];
    }

    
    
    public static String getLineFromElements(String[] elements) {
        if (elements.length < NUM_ELEMENTS) return null;
        boolean dir = elements[ELEMENT_INDEX_FILE_NAME].endsWith("/");
        StringBuffer line = new StringBuffer();
        if (dir) line.append(DIRECTORY_CACHE_ID);
        line.append("/");
        for(int i = 0; i < NUM_ELEMENTS; i++) {
            line.append(elements[i]);
            if (i != 0 || !dir) line.append("/");
        }
        return line.toString();
    }
   */ 
    /**
     * Get the annotation line for a file.
     * @param name the object file name
     * @param fullName the full path of the file with respect to the filesystem root
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param statusProvider the provider of the status attributes information
     * @return the annotation pattern filled up with proper attributes
     */
    public String getStatusAnnotation(String name, String fullName, 
                                             AnnotationProvider statusProvider, String annTypeId) {
        return getStatusAnnotation(name, fullName, statusProvider, null, annTypeId);
    }
    
    /**
     * Get the annotation line for a file.
     * @param name the object file name
     * @param fullName the full path of the file with respect to the filesystem root
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param statusProvider the provider of the status attributes information
     * @return the annotation pattern filled up with proper attributes
     */
    public  String getStatusAnnotation(String name, String fullName,
                       AnnotationProvider statusProvider, Hashtable additionalVars, String annTypeId) {
        AnnotationSupport.PatternType patternType = (AnnotationSupport.PatternType)this.annotationPatterns.get(annTypeId);
        if (patternType == null) {
            System.out.println("error.");
            return name;
        }
        String annotationPattern = patternType.getStringPattern();
        Hashtable vars = new Hashtable();
        if (additionalVars != null) vars.putAll(additionalVars);
        vars.put(ANNOTATION_PATTERN_FILE_NAME, name);
        String status;
        String attr;
        String[] pattArray = patternType.getPatternsAsArray();
        
        for(int j = 0 ; j < pattArray.length; j++) {
            String pat = pattArray[j];
            if (pat.equals(ANNOTATION_PATTERN_FILE_NAME)) continue;
            int patIndex = patternType.getPatternIndex(pat);
            String delimOrSynch = patternType.getMultiFileDelimiterAt(patIndex);
            int multiFileAnn = patternType.getMultiFileAnnotationAt(patIndex);
            status = statusProvider.getAttributeValue(fullName, pat);
            if (status != null) vars.put(pat, status);
        }
        
        
        //System.out.println("vars = "+vars+",\npattern = "+annotationPattern+",\nexpansion = "+Variables.expandFast(vars, annotationPattern, false));
        return Variables.expand(vars, annotationPattern, false);
    }
    
    /**
     * Get the annotation line for a list of files.
     * @param name the object file name
     * @param fullName the full path of the file with respect to the filesystem root
     * @param annotationPattern the pattern how the annotation should be displayed
     * @param statusProvider the provider of the status attributes information
     * @param multiFilesAnnotationTypes the annotation types for individual attributes.
     *        Values of this files are <code>MULTI_FILES_ANNOTATION_*<code> constants.
     * @return the annotation pattern filled up with proper attributes
     */
    public String getStatusAnnotation(String name, ArrayList files, 
                                             AnnotationProvider statusProvider, String annTypeId) {
        AnnotationSupport.PatternType patternType = (AnnotationSupport.PatternType)this.annotationPatterns.get(annTypeId);
        if (patternType == null) {
            System.out.println("error.");
            return name;
        }
        String annotationPattern = patternType.getStringPattern();
        Hashtable vars = new Hashtable();
        ArrayList attributes = new ArrayList();
        int n = files.size();
        if (n == 0) return name;
        vars.put(ANNOTATION_PATTERN_FILE_NAME, name);
        String trans = null;
        String attr;
        String[] pattArray = patternType.getPatternsAsArray();
        for(int j = 0 ; j < pattArray.length; j++) {
            attributes.clear();
            String pat = pattArray[j];
            if (pat.equals(ANNOTATION_PATTERN_FILE_NAME)) continue;
            int patIndex = patternType.getPatternIndex(pat);
            String delimOrSynch = patternType.getMultiFileDelimiterAt(patIndex);
            int multiFileAnn = patternType.getMultiFileAnnotationAt(patIndex);
            for(int i = 0 ; i < n; i++) {
                String value = statusProvider.getAttributeValue(((String) files.get(i)), pat);
                attributes.add(value);
            }
            attr = getAttribute(attributes, multiFileAnn, delimOrSynch);
            if (attr != null) vars.put(pat, attr);
        }
        return Variables.expand(vars, annotationPattern, false);
    }
    

    
    private static String getAttribute(ArrayList attributes, int annotationType, String nSynch) {
        boolean differ;
        Object firstObj = attributes.get(0);
        if (firstObj == null) return null;
        String first = (String) firstObj;
        int n = attributes.size();
        int i = 1;
        for (i = 1; i < n; i++) {
            if (!firstObj.equals(attributes.get(i))) {
                //System.out.println("first = "+firstObj+" != "+attributes.get(i));
                break;
            }
        }
        differ = i < n;
        switch (annotationType) {
            case MULTI_FILES_ANNOTATION_EMPTY:
                if (differ) return "";
                else return first;
            case MULTI_FILES_ANNOTATION_NOT_SYNCH_ATTR:
                if (differ) return nSynch;
                else return first;
            case MULTI_FILES_ANNOTATION_LIST:
                if (!differ) return first;
                StringBuffer buf = new StringBuffer(first);
                for (int j = 1; j < n; j++) {
                    buf.append(nSynch + (String) attributes.get(j));
                }
                return buf.toString();
            default: return "";
        }
    }
    
    
    public class PatternType {

        
        /** Holds value of property stringPattern. */
        private String stringPattern;
        
        /** Holds value of property id. */
        private String id;
        
        private List patternList;
        private List multiFileList;
        private List multiDelimitList;
        
        PatternType(String id) {
            this.id = id;
            patternList = new ArrayList();
            multiFileList = new ArrayList();
            multiDelimitList = new ArrayList();
        }
        
        /** Getter for property stringPattern.
         * @return Value of property stringPattern.
         */
        public String getStringPattern() {
            return this.stringPattern;
        }
        
        /** Setter for property stringPattern.
         * @param stringPattern New value of property stringPattern.
         */
        public void setStringPattern(String stringPattern) {
            this.stringPattern = stringPattern;
        }
        
        public synchronized void addMultiEmptyPattern(String patternName) {
            patternList.add(patternName);
            multiFileList.add(AnnotationSupport.MULTI_FILES_PATT_ANN_EMPTY);
            multiDelimitList.add("");
        }
        
        public synchronized void addMultiListPattern(String patternName, String multiPatternDelimiter) {
            patternList.add(patternName);
            multiFileList.add(AnnotationSupport.MULTI_FILES_PATT_ANN_LIST);
            multiDelimitList.add(multiPatternDelimiter);
            
        }

        public synchronized void addMultiSynchPattern(String patternName, String synchLabel) {
            patternList.add(patternName);
            multiFileList.add(AnnotationSupport.MULTI_FILES_PATT_ANN_NOT_SYNCH);
            multiDelimitList.add(synchLabel);
        }
        
        public synchronized void removePattern(String patternName) {
            int count = patternList.size();
            for (int i = 0; i < count; i++) {
                String patt = patternList.get(i).toString();
                if (patt.equals(patternName)) {
                    patternList.remove(i);
                    multiFileList.remove(i);
                    multiDelimitList.remove(i);
                    return;
                }
            }
        }
        
        public synchronized boolean hasPattern(String patternName) {
            return true; //TODO
            
        }
        
        public String[] getPatternsAsArray() {
            String[] arr = new String[patternList.size()];
            arr = (String[])patternList.toArray(arr);
            return arr;
        }
        
        int getPatternIndex(String pattern) {
            return patternList.indexOf(pattern);
        }
        
        String getMultiFileDelimiterAt(int index) {
            return (String)multiDelimitList.get(index);
        }
        
        int getMultiFileAnnotationAt(int index) {
            return ((Integer)multiFileList.get(index)).intValue();
        }
        /** Getter for property id.
         * @return Value of property id.
         */
        public String getId() {
            return id;
        }
        
        
    }
}

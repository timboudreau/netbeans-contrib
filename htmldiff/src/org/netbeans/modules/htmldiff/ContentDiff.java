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

package org.netbeans.modules.htmldiff;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

/** Computes differencies for two sets of files.
 *
 * @author  Jaroslav Tulach
 */
public final class ContentDiff extends Object {
    /** names of all pages to compare */
    private Set allFiles;
    /** source to read or null */
    private Source source;
    /** map of dependencies between pages */
    private Map deps = new HashMap ();
    /** clusters of pages */
    private Cluster[] clusters;
    
    /** no instances outside */
    private ContentDiff (Source source, Set allFiles) {
        this.source = source;
        this.allFiles = allFiles;
    }
    

    /** The description of page clusters in this diff.
     * @return array of clusters sorted by dependencies
     */
    public Cluster[] getClusters () {
        if (clusters != null) {
            return clusters;
        }
        
        try {
            List sort = org.openide.util.Utilities.topologicalSort (allFiles, deps);
            
            // ok, no cycles in between pages
            clusters = new Cluster[sort.size ()];
            for (int i = 0; i < clusters.length; i++) {
                clusters[i] = new Cluster (Collections.singleton (sort.get (i)));
            }
        } catch (org.openide.util.TopologicalSortException ex) {
            // ok, there are cycles
            
            Set[] sets = ex.topologicalSets();
            clusters = new Cluster[sets.length];
            for (int i = 0; i < clusters.length; i++) {
                clusters[i] = new Cluster (sets[i]);
            }
        }
        
        // references <String -> Cluster>
        HashMap refs = new HashMap ();
        for (int i = 0; i < clusters.length; i++) {
            java.util.Iterator it = clusters[i].getPages ().iterator ();
            while (it.hasNext()) {
                refs.put (it.next (), clusters[i]);
            }
        }
        
        for (int i = 0; i < clusters.length; i++) {
            Set references = new HashSet ();
            java.util.Iterator it = clusters[i].getPages ().iterator ();
            while (it.hasNext()) {
                Collection c = (Collection)deps.get (it.next ());
                if (c != null) {
                    Iterator d = c.iterator();
                    while (d.hasNext()) {
                        references.add (refs.get (d.next ()));
                    }
                }
            }
            // prevent self references
            references.remove (clusters[i]);
            clusters[i].references = (Cluster[])references.toArray (new Cluster[0]);
        }
        
        return clusters;
    }
    
    
    
    /** @param removed 
     */
    private void pageAddedRemoved (String filename, boolean removed) {
    }
    
    /** Parses the page for URL dependencies */
    private void parseHRefs (URL page, String name, URL base) throws IOException {
        Reader r = source.getReader (page);
        URL[] refs = ParseURLs.parse (r, page);
        
        String b = base.toExternalForm();
        for (int i = 0; i < refs.length; i++) {
            String e = refs[i].toExternalForm ();
            if (e.startsWith (b)) {
                String s = e.substring (b.length ());
                Collection c = (Collection)deps.get (name);
                if (c == null) {
                    c = new ArrayList ();
                    deps.put (name, c);
                }
                c.add (s);
            }
        }
    }
    
    /** Parses given set of files and creates groups of files 
     * refering sorted topologically with identification of how much
     * each page changed.
     *
     * @param base1 url to base search for the first set of files
     * @param files1 set of filenames <String> to parse
     * @param base2 url to base search for the second set of files
     * @param files2 set of filenames <String> to parse
     * @return result of comparation
     */
    public static ContentDiff diff (URL base1, Set files1, URL base2, Set files2, Source source) throws IOException {
        Set allFiles = new HashSet (files1);
        allFiles.addAll (files2);
        
        ContentDiff result = new ContentDiff (source, allFiles);
        
        Iterator names = allFiles.iterator();
        while (names.hasNext ()) {
            String fileName = (String)names.next ();
            URL f1 = new URL (base1, fileName);
            URL f2 = new URL (base2, fileName);
            
            boolean isIn1 = files1.contains (fileName);
            if (isIn1 != files2.contains (fileName)) {
                // page is either missing or added
                result.pageAddedRemoved (fileName, isIn1);
                if (isIn1) {
                    result.parseHRefs (f1, fileName, base1);
                } else {
                    result.parseHRefs (f2, fileName, base2);
                }
            } else {
                // parse the diffs
                result.parseHRefs (f1, fileName, base1);
                result.parseHRefs (f2, fileName, base2);
                /*
                Reader r1 = source.getReader (f1);
                Reader r2 = source.getReader (f2);
                
                r1.close ();
                r2.close ();
                 */
            }
        }
            
            
        return result;
    }
   
    
    
    /** Mapping from URLs to content. Useful for testing.
     */
    interface Source {
        /** Finds a reader for a given file name.
         */
        public java.io.Reader getReader (URL url) throws java.io.IOException;
    } // end of Source

    
    /** Describes a set of pages that seem to be related. E.g. refer to each 
     * other.
     */
    public static final class Cluster extends Object {
        /** set of <String> */
        private Set pages;
        /** reference clusters */
        Cluster[] references;
        
        /** create instances just in this class */
        Cluster (Set pages) {
            this.pages = pages;
        }
        
        /** Names of pages that are the in the cluster.
         * @return set of <String>
         */
        public Set getPages () {
            return pages;
        }
        
        /** Gets an array of clusters this one depends on
         */
        public Cluster[] getReferences () {
            return references;
        }
    } // end of Cluster
}

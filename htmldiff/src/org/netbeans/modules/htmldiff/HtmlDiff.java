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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Computes HTML based diff.
 *
 * @author  Jaroslav Tulach
 */
public final class HtmlDiff extends Object {
    private String oldText;
    private String newText;

    /** same text
     */
    private HtmlDiff (String same) {
        this (same, same);
    }
    
    /** Creates a new instance of HtmlDiff */
    private HtmlDiff(String o, String n) {
        this.oldText = o;
        this.newText = n;
    }
    
    
    /** Checks if this is a difference or just the same area.
     */
    public boolean isDifference () {
        return oldText != newText;
    }
    
    /** Gets the old text.
     */
    public String getOld () {
        return oldText;
    }
    
    /** Gets the new one.
     */
    public String getNew () {
        return newText;
    }
    
    
    /** Computes the differences for old and new streams.
     * @param old old stream of HTML document
     * @param new new stream of HTML document
     * @exception IOException if there is I/O problem
     */
    public static HtmlDiff[] diff (Reader old, Reader current) throws IOException {
        List oldArr = wordize (old);
        List newArr = wordize (current);
        
        org.netbeans.spi.diff.DiffProvider diff = (org.netbeans.spi.diff.DiffProvider)org.openide.util.Lookup.getDefault().lookup(org.netbeans.spi.diff.DiffProvider.class);
        if (diff == null) {
            diff = new org.netbeans.modules.diff.builtin.provider.BuiltInDiffProvider ();
        }
        org.netbeans.api.diff.Difference[] arr = diff.computeDiff(toStream (oldArr), toStream (newArr));

        
        ArrayList res = new ArrayList ();

        int[] oldArrIndex = { 0 };
        int[] newArrIndex = { 0 };
        for (int i = 0; i < arr.length; i++) {
            int os = arr[i].getFirstStart();
            int ns = arr[i].getSecondStart();

            if (arr[i].getType() == org.netbeans.api.diff.Difference.ADD) {
                ns--;
            } else if (arr[i].getType() == org.netbeans.api.diff.Difference.CHANGE) {
                ns--;
                os--;
            }
            
            // same text
            String oldText = toHTML (oldArr, oldArrIndex, os, true);
            String newText = toHTML (newArr, newArrIndex, ns, true);
            /*
            if (!oldText.equals (newText)) {
                throw new IllegalStateException ("Should be the same: " + oldText + " new: " + newText);
            }
             */
            
            res.add (new HtmlDiff (newText));
            
            // process the difference
            int oe = arr[i].getFirstEnd ();
            int ne = arr[i].getSecondEnd ();
            
            oldText = toHTML (oldArr, oldArrIndex, oe, false);
            newText = toHTML (newArr, newArrIndex, ne, false);
            
            res.add (new HtmlDiff (oldText, newText));
        }
        
        // same text
        String oldText = toHTML (oldArr, oldArrIndex, Integer.MAX_VALUE, true);
        String newText = toHTML (newArr, newArrIndex, Integer.MAX_VALUE, true);
        
        if (newText.length() != 0) {
/*
        if (!oldText.equals (newText)) {
            throw new IllegalStateException ("Should be the same: " + oldText + " new: " + newText);
        }
*/
            res.add (new HtmlDiff (newText));
        }
        
        return (HtmlDiff[])res.toArray (new HtmlDiff[0]);
    }
    
    
    /** Converts the stream to pieces.
     * @param r strea of characters
     * @return List<Item> of items
     *
     */
    private static List wordize (Reader r) throws IOException {
        BufferedReader buf = new BufferedReader (r);
        
        ArrayList arr = new ArrayList ();
        int state = 0;
        StringBuffer word = null;
        for (;;) {
            int ch = buf.read ();
            if (ch == -1) break;

            switch (state) {
            case 1: // character
                if (Character.isLetter ((char)ch)) {
                    word.append ((char)ch);
                    break;
                } else {
                    arr.add (newWord (word.toString()));
                    word = null;
                    state = 0;
                    // fall thru
                }
            case 0: // white line
                if (Character.isLetter((char)ch)) {
                    word = new StringBuffer ();
                    word.append ((char)ch);
                    state = 1;
                }
                if (ch == '<') {
                    state = 2;
                    word = new StringBuffer ();
                    word.append ('<');
                }
                if (ch == '\n') {
                    arr.add (newLine ());
                }
                break;
            case 2: // search for end of comment
                word.append ((char)ch);
                if (ch == '>') {
                    arr.add (newTag (word.toString()));
                    word = null;
                    state = 0;
                    break;
                }
                break;
            }
            
        }
        
        return arr;
    }
    
    /** Converts the text into a stream.
     */
    private static Reader toStream (List items) throws IOException {
        StringWriter to = new StringWriter ();
        
//        System.out.println("begin");
        Iterator it = items.iterator();
        int[] counter = { 0 };
  //          PrintWriter pw = new PrintWriter (System.out);
        while (it.hasNext()) {
            Item i = (Item)it.next ();
    //        i.printDiff (pw, new int[1]);
            i.printDiff (to, counter);
        }
      //  pw.flush ();
        //System.out.println("konec");
        
        return new StringReader (to.toString ());
    }
    
    /** Gets HTML.
     */
    private static String toHTML (List items, int[] fromPosition, int toIndex, boolean eager) throws IOException {
        StringWriter w = new StringWriter ();
        boolean addSpace = false;
        boolean firstOrEager = true;
        while (fromPosition[0] < items.size ()) {
            Item item = (Item)items.get (fromPosition[0]);
            if (firstOrEager && item.index >= toIndex) {
                return w.toString ();
            }
            firstOrEager = eager;
            
            addSpace = item.printHTML (w, addSpace);
                
            fromPosition[0]++;
            
            if (!eager && item.index + 1 >= toIndex) {
                return w.toString ();
            }
        }
        return w.toString ();
    }   
    
    /** In list of items finds the one with given index.
     */
    private static int findIndex (List where, int which, int from) {
        int i = from;
        while (where.size () > i) {
            Item item = (Item)where.get (i);
            if (item.index == which) {
                return i;
            }
            i++;
        }
        return from + 1;
    }
    
    private static Item newLine () {
        return new Item (0, null);
    }
    private static Item newWord (String string) {
        return new Item (1, string);
    }
    private static Item newTag (String string) {
        return new Item (2, string);
    }
    
    /** Item in HTML document
     */
    private static class Item extends Object {
        private int type;
        private String value;
        private int index = -1;
        
        public Item (int type, String value) {
            this.type = type;
            this.value = value;
        }
        
        public String toString () {
            return type + " value: " + value;
        }

        /** Prints to diff file
         */
        public void printDiff (Writer w, int[] counter) throws IOException {
            switch (type) {
                case 0: break; // new line
                case 1: 
                    w.write (value); 
                    w.write ('\n'); 
                    index = counter[0]++;
                    break; // word
                case 2: break; // no tags
                default: 
                    throw new IllegalArgumentException ();
            }
        }
        
        /** Prints to Html 
         * @return true whether there should be a space
         */
        public boolean printHTML (Writer w, boolean needsSpace) throws IOException {
            switch (type) {
                case 0: 
                    w.write ('\n');
                    needsSpace = false;
                    break; // new line
                case 1: 
                    if (needsSpace) {
                        w.write (' ');
                    }
                    w.write (value); 
                    needsSpace = true;
                    break; // word
                case 2: 
                    w.write (value);
                    needsSpace = false;
                    break; // no tags
                default: 
                    throw new IllegalArgumentException ();
            }
            return needsSpace;
        }
    } // end of Item
}

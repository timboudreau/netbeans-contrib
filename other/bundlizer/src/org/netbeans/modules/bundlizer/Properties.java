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
/*
 * CommentPreservingProperties.java
 *
 * Created on April 30, 2004, 7:23 PM
 */

package org.netbeans.modules.bundlizer;

import java.awt.Frame;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * A Properties like map which 
 *
 * @author  Tim Boudreau
 */
public class Properties {
    private List items = new ArrayList();
    private int size = 0;
    boolean sorted = true;
    
    /** Creates a new instance of CommentPreservingProperties */
    public Properties() {
        
    }
    
    public Properties (Properties copy) {
        items = new ArrayList (copy.items);
        size = copy.size;
        sorted = copy.sorted;
    }
    
    public Properties (Properties copy, Collection keysToRetain) {
        for (Iterator i=keysToRetain.iterator(); i.hasNext();) {
            String key = (String) i.next();
            int idx = copy.indexOf (key);
            if (idx == -1) {
                throw new IllegalStateException ("Key " + key + " missing");
            }
            items.add (copy.items.get(idx));
            size++;
        }
    }
    
    public void load (File f) throws IOException {
        FileInputStream fis = new FileInputStream (f);
        ByteBuffer bb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());

        byte[] b = new byte[(int) f.length()];
        bb.get (b);
        String s = new String (b);
        parseFile (s);
    }
    
    public Collection keySet () {
        Vector l = new Vector();
        for (Iterator i=items.iterator(); i.hasNext();) {
            Item it = (Item) i.next();
            String key = it.getKey();
            if (key != null) {
                l.add (key);
            }
        }
        return l;
    }
    
    private void parseFile (String s) throws IOException {
        StringTokenizer tok = new StringTokenizer (s, "\n");
        StringBuffer currComments = new StringBuffer();
        boolean first = true;
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            if (isKeyValuePair(line)) {
                if (first && currComments.length() != 0) {
                    //Treat first comments as a header that will be in their
                    //own item.  Note this means we may delete any comment above
                    //the very first key value pair.  Do better loose header line
                    //matching at some point.
                    put (null, null, currComments.toString());
                    currComments = new StringBuffer();
                    first = false;
                }
                String comments = currComments.length() > 0 ? currComments.toString() : null;
                if (comments != null) {
                    currComments = new StringBuffer();
                }
                String keyValuePair = line;
                int equals = keyValuePair.indexOf ("=");
                assert equals != -1;
                
                String key = keyValuePair.substring (0, equals).trim();
                String value;
                if (equals == keyValuePair.length() - 1) {
                    value = "";
                } else {
                    value = keyValuePair.substring (equals + 1);
                }
                while (value.trim().endsWith("\\") && tok.hasMoreTokens()) {
                    value += "\n" + tok.nextToken();
                }
                put (key, value, comments);
            } else {
                currComments.append (line);
                currComments.append ("\n");
            }
        }
        if (currComments.length() > 0) {
            //there's a trailing comment at the end of the file
            put (null, null, currComments.toString());
        }
    }
    
    public int size() {
        return size;
    }
    
    public void store (FileOutputStream fos, String header) throws IOException {
        ArrayList items = new ArrayList (this.items);
        boolean deleteFirst = false;
        if (header != null) {
            Item first = (Item) items.get(0);
            String comment = first.getComments();
            if (comment != null) {
                StringTokenizer tok = new StringTokenizer (comment, "\n");
                StringTokenizer hed = new StringTokenizer (header, "\n");
                String headFirstLine = hed.nextToken().trim().toUpperCase();
                String ffirstLine = tok.nextToken().trim().toUpperCase();
                String fsecondLine = tok.hasMoreElements() ? tok.nextToken().trim().toUpperCase() : null;
                deleteFirst = headFirstLine.equals(ffirstLine) || headFirstLine.equals(fsecondLine);
            }
        }
        if (deleteFirst) {
            System.err.println("Header match, deleting original header: " + items.get(0));
            items.remove(0);
        }
        
        
        
        Item[] its = new Item[items.size()];
        its = (Item[]) items.toArray(its);
        if (sorted) {
            Arrays.sort(its);
        }
        
        StringBuffer sb = new StringBuffer ((header != null ? header.length() : 0) + its.length * 30);
        if (header != null) {
            sb.append (header);
        }
        if (!header.endsWith("\n")) {
            sb.append ("\n");
        }
        
        boolean lastWasComment = header != null;
        for (int i=0; i < its.length; i++) {
            if (!lastWasComment && its[i].getComments() != null) {
                sb.append ("\n");
            }
            sb.append (its[i]);
            lastWasComment = its[i].getKey() == null;
        }
        
        byte[] b = sb.toString().getBytes();
        fos.write(b);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i=items.iterator(); i.hasNext();) {
            sb.append (i.next());
        }
        sb.append ("\n");
        return sb.toString();
    }
    
    private boolean isKeyValuePair (String line) {
        int equals = line.indexOf("=");
        int cmt = line.indexOf ("#");
        return (cmt > 0 && equals < cmt) || (cmt < 0 && equals >= 0);
    }
    
    private int indexOfLineComment (String line) {
        return line.indexOf ("#");
    }
    
    public Enumeration keys() {
        return ((Vector) keySet()).elements();
    }
    
    public void put (String key, String value) {
        int i = indexOf(key);
        if (i != -1) {
            Item item = (Item) items.get(i);
            if (item.getValue() != null &&item.getValue().trim().equals(value.trim())) {
                System.err.println("Pruned duplicate identical entry " + key + "=" + value);
                return;
            }
            if (!askRemove(i, key, value)) {
                items.remove(i);
                size--;
            }
        }
        items.add (new Item (key, value));
        if (key != null) {
            size++;
        }
    }
    
    public void put (String key, String value, String comment) {
        if (key != null) {
            int i = indexOf(key);
            if (i != -1) {
                Item item = (Item) items.get(i);
                if (item.getValue() != null &&item.getValue().trim().equals(value.trim())) {
                    System.err.println("Pruned duplicate identical entry " + key + "=" + value);
                    return;
                }
                if (!askRemove(i, key, value)) {
                    items.remove(i);
                    size--;
                }
            }
        }

        items.add (new Item (key, value, comment));
        if (key != null) {
            size++;
        }
    }
    
    public boolean askRemove (int index, String key, String value) {
        Item item = (Item) items.get(index);
        if (item.getValue() == null) {
            return false;
        }
        JPanel jp = new JPanel();
        JRadioButton first = new JRadioButton (value);
        JRadioButton second = new JRadioButton (item.getValue());
        jp.setLayout (new BoxLayout(jp, BoxLayout.Y_AXIS));
        JLabel jl = new JLabel ("Keep which value?");
        jp.add (jl);
        jp.add (first);
        jp.add (second);
        first.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(first);
        bg.add(second);
        
        JOptionPane.showMessageDialog(Frame.getFrames()[0], jp, "Duplicate key: " + key, JOptionPane.OK_OPTION);
        return first.isSelected();
    }
    
    public int indexOf (Object key) {
        int idx = 0;
        for (Iterator i = items.iterator(); i.hasNext();) {
            Item it = (Item) i.next();
            if (key.equals(it.getKey())) {
                return idx;
            }
            idx++;
        }
        return -1;
    }
    
    public void remove (Object key) {
        for (Iterator i = items.iterator(); i.hasNext();) {
            Item it = (Item) i.next();
            if (key.equals(it.getKey())) {
                i.remove();
                size--;
                return;
            }
        }
        throw new IllegalArgumentException ("No item " + key);
    }
    
    public String get (Object key) {
        for (Iterator i = items.iterator(); i.hasNext();) {
            Item item = (Item) i.next();
            if (key.equals(item.getKey())) {
                return item.getValue();
            }
        }
        return null;
    }
    
    private class Item implements Comparable {
        private String key;
        private String value;
        private String comments = null;
        public Item (String key, String value) {
            this (key, value, null);
        }
        
        public Item (String key, String value, String comments) {
            this.key = key;
            this.value = value;
            this.comments = comments;
            if (comments != null && !comments.endsWith("\n")) {
                comments += "\n";
            }
            if (value == null && key != null) {
                throw new IllegalArgumentException ("Null value");
            }
            if (value == null && key == null && comments == null) {
                throw new IllegalArgumentException ("Don't be silly.");
            }
        }
        
        public String getComments() {
            return comments;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            if (comments != null) {
                sb.append (comments);
            }
            if (getKey() != null && getValue() != null) {
                sb.append (getKey() + "=" + getValue());
            }
            sb.append ("\n");
            return sb.toString();
        }
        
        public int compareTo(Object obj) {
            if (obj instanceof Item) {
                return getKey().compareTo(((Item) obj).getKey());
            }
            throw new ClassCastException (obj + " is not an instance of Item");
        }
        
    }
    
}

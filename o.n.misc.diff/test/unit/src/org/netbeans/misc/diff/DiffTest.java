/*
 * DiffTest.java
 *
 * Created on February 27, 2007, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.misc.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Tim Boudreau
 */
public class DiffTest extends TestCase {
    
    /** Creates a new instance of DiffTest */
    public DiffTest(String x) {
        super(x);
    }
    
    public void testIterDiff() {
        System.out.println("testIterDiff");
        List <String> a = Arrays.<String>asList (new String[] {
            "hello", "this", "is", "a", "list", "of", "things"
        });
        
        List <String> b = Arrays.<String>asList (new String[] {
            "hello", "is", "a", "list", "of", "other", "things"
        });
        
        Diff <String> diff = org.netbeans.misc.diff.Diff.<String>create (a, b,
                Diff.Algorithm.ITERATIVE);
 
        System.err.println("Iter diff: " + diff);
        
        List <String> list = new ArrayList<String>(diff.getOld());
        List <String> target = diff.getNew();
        List <Change> changes = diff.getChanges();
        for (Iterator <Change> iter=changes.iterator(); iter.hasNext();) {
            Change change = iter.next();
            int start = change.getStart();
            int end = change.getEnd();
            switch (change.getType()) {
            case Change.CHANGE :
                for (int i=start; i <= end; i++) {
                    list.set(i, target.get(i));
                }
                break;
            case Change.INSERT :
                for (int i=end; i >= start; i--) {
                    Object o = target.get(i);
                    list.add(start, (String) o);
                }
                break;
            case Change.DELETE :
                for (int i=end; i >= start; i--) {
                    list.remove(i);
                }
                break;
            }
        }
        int max = b.size();
        assertEquals (max, list.size());
        for (int i=0; i < max; i++) {
            assertEquals (b.get(i), list.get(i));
        }
    }
    
    public void testLcsDiff() {
        System.out.println("testLcsDiff");
        List <String> a = Arrays.<String>asList (new String[] {
            "hello", "this", "is", "a", "list", "of", "things"
        });
        
        List <String> b = Arrays.<String>asList (new String[] {
            "hello", "is", "a", "list", "of", "other", "things"
        });
        
        Diff <String> diff = org.netbeans.misc.diff.Diff.<String>create (a, b,
                Diff.Algorithm.LONGEST_COMMON_SEQUENCE);
        
        System.err.println("Lcs diff: " + diff);
        
        List <String> list = new ArrayList<String>(diff.getOld());
        List <String> target = b;
        List <Change> changes = diff.getChanges();
        for (Iterator <Change> iter=changes.iterator(); iter.hasNext();) {
            Change change = iter.next();
            int start = change.getStart();
            int end = change.getEnd();
            switch (change.getType()) {
            case Change.CHANGE :
                for (int i=start; i <= end; i++) {
                    String s = target.get(i);
                    System.err.println("Change '" + list.get(i) + "' to '" + s + "' at " + i);
                    list.set(i, s);
                }
                break;
            case Change.INSERT :
                for (int i=end; i >= start; i--) {
                    String o = target.get(i);
                    System.err.println("Add '" + o +"' at " + i);
                    list.add(start, (String) o);
                }
                break;
            case Change.DELETE :
                for (int i=end; i >= start; i--) {
                    String s = list.remove(i);
                    System.err.println("Remove '" + s +"' at " + i);
                }
                break;
            }
        }
        System.err.println("Got " + list + " should be " + target);
        int max = b.size();
        for (int i=0; i < max; i++) {
            assertEquals (b.get(i), list.get(i));
        }
        assertEquals (max, list.size());
    }
}

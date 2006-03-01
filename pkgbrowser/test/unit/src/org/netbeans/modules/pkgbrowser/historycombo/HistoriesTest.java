/*
 * HistoriesTest.java
 * JUnit based test
 *
 * Created on February 27, 2006, 12:12 PM
 */

package org.netbeans.modules.pkgbrowser.historycombo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import junit.framework.TestCase;
import junit.framework.*;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;

/**
 *
 * @author Tim Boudreau
 */
public class HistoriesTest extends TestCase {
    
    public HistoriesTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(HistoriesTest.class);
        
        return suite;
    }

    public void testCreateComboBox() {
        JComboBox box = Histories.createComboBox("foo");
        assert box.getModel() instanceof CompletionComboBoxModel;
        assert box.getEditor() instanceof HistoryComboBoxEditor;
    }

    public void testPutAndGet() {
        String[] contents = new String[] {
            "aaHello", "aaGoodbye", "aaAardvark", "abcd", "foodbar", "foodbag",
            "bubble", "boom" };
        
        List l = Arrays.asList(contents);
        for (int i=0; i < contents.length; i++) {
            Histories.put ("foo", contents[i]);
        }
        Set s = new HashSet (l);
        Set s1 = new HashSet (Histories.get("foo"));
        assertTrue (s.containsAll(s1));
        assertTrue ("Contains all fails - got " + s1 + " expected " + s,
                s1.containsAll(s));

        List list = Histories.getMatchList("foo", "aa");
        s = new HashSet();
        for (int i=0; i < contents.length; i++) {
            if (contents[i].startsWith("aa")) {
                assertTrue ("List is missing " + contents[i] + ": " + list,
                        list.contains(contents[i]));
                s.add (contents[i]);
            }
        }
        assertEquals ("Wrong match list contents for 'aa' " + list, 3, list.size());
        assertEquals (s, new HashSet(list));

        list = Histories.getMatchList("foo", "a");
        s = new HashSet();
        for (int i=0; i < contents.length; i++) {
            if (contents[i].startsWith("a")) {
                assertTrue (list.contains(contents[i]));
                s.add (contents[i]);
            }
        }
        assertEquals ("Wrong match list contents for 'a' " + list, 4, list.size());
        assertEquals (s, new HashSet(list));

        List l2 = Histories.getMatchList("foo", "");
        List l1 = new ArrayList(Arrays.asList(contents));
        Collections.sort (l1);
        Collections.sort(l2);
        assertEquals (l1, l2);
    }
}

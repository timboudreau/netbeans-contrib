/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editor.bracesmacthing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.bracesmatching.MasterMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;

import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;

/**
 *
 * @author vita
 */
public class MasterMatcherTest extends NbTestCase {

    public MasterMatcherTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testContext() throws Exception {
        MockServices.setServices(MockMimeLookup.class);
        MockMimeLookup.setInstances(MimePath.EMPTY, new TestMatcher());
        
        AttributeSet EAS = SimpleAttributeSet.EMPTY;
        PlainDocument d = new PlainDocument();
        OffsetsBag bag = new OffsetsBag(d);
        d.insertString(0, "text text { text } text", null);

        MasterMatcher.get(d).highlight(7, "forward", bag, EAS, EAS);
        Thread.sleep(300);
        {
        TestMatcher tm = TestMatcher.lastMatcher;
        assertNotNull("No matcher created", tm);
        assertNotNull("No context passed to the matcher", tm.context);
        assertSame("Wrong document", d, tm.context.getDocument());
        assertEquals("Wrong caret offset", 7, tm.context.getCaretOffset());
        assertFalse("Wrong search direction", tm.context.isSearchingBackward());
        }        
        
        TestMatcher.lastMatcher = null;
        MasterMatcher.get(d).highlight(11, "backward", bag, EAS, EAS);
        Thread.sleep(300);
        {
        TestMatcher tm = TestMatcher.lastMatcher;
        assertNotNull("No matcher created", tm);
        assertNotNull("No context passed to the matcher", tm.context);
        assertSame("Wrong document", d, tm.context.getDocument());
        assertEquals("Wrong caret offset", 11, tm.context.getCaretOffset());
        assertTrue("Wrong search direction", tm.context.isSearchingBackward());
        }        
    }
    
    public void testAreas() throws Exception {
        MockServices.setServices(MockMimeLookup.class);
        MockMimeLookup.setInstances(MimePath.EMPTY, new TestMatcher());
        
        AttributeSet EAS = SimpleAttributeSet.EMPTY;
        PlainDocument d = new PlainDocument();
        OffsetsBag bag = new OffsetsBag(d);
        d.insertString(0, "text text { text } text", null);

        TestMatcher.origin = new int [] { 2, 3 };
        TestMatcher.matches = new int [] { 10, 11 };
        
        MasterMatcher.get(d).highlight(7, "backward", bag, EAS, EAS);
        Thread.sleep(300);
        {
        TestMatcher tm = TestMatcher.lastMatcher;
        assertNotNull("No matcher created", tm);
        
        HighlightsSequence hs = bag.getHighlights(0, Integer.MAX_VALUE);
        assertTrue("Wrong number of highlighted areas", hs.moveNext());
        assertEquals("Wrong origin startOfset", 2, hs.getStartOffset());
        assertEquals("Wrong origin endOfset", 3, hs.getEndOffset());
        
        assertTrue("Wrong number of highlighted areas", hs.moveNext());
        assertEquals("Wrong match startOfset", 10, hs.getStartOffset());
        assertEquals("Wrong match endOfset", 11, hs.getEndOffset());
        }        
    }
    
    public void testBlockingByForLoop() throws Exception {
        MockServices.setServices(MockMimeLookup.class);
        MockMimeLookup.setInstances(MimePath.EMPTY, new BlockingMatcher());
        
        AttributeSet EAS = SimpleAttributeSet.EMPTY;
        PlainDocument d = new PlainDocument();
        OffsetsBag bag = new OffsetsBag(d);
        d.insertString(0, "text text { text } text", null);

        BlockingMatcher.blockByForLoop = true;
        BlockingMatcher.origin = new int [] { 2, 3 };
        BlockingMatcher.matches = new int [] { 10, 11 };

        {
        BlockingMatcher.blockInFindOrigin = true;
        
        MasterMatcher.get(d).highlight(7, "backward", bag, EAS, EAS);
        Thread.sleep(300);
        BlockingMatcher first = BlockingMatcher.lastMatcher;
        assertNotNull("No first matcher", first);
        assertTrue("Should be blocking", first.blocking);
        
        MasterMatcher.get(d).highlight(8, "backward", bag, EAS, EAS);
        Thread.sleep(2000);
        BlockingMatcher second = BlockingMatcher.lastMatcher;
        assertNotNull("No second matcher", second);
        
        assertFalse("First blocking matcher was not interrupted", first.blocking);
        assertFalse("There should be no highlights", bag.getHighlights(0, Integer.MAX_VALUE).moveNext());
        }
        
        {
        BlockingMatcher.blockInFindOrigin = false;
        
        MasterMatcher.get(d).highlight(7, "backward", bag, EAS, EAS);
        Thread.sleep(300);
        BlockingMatcher first = BlockingMatcher.lastMatcher;
        assertNotNull("No first matcher", first);
        assertTrue("First matcher should be blocking", first.blocking);
        
        MasterMatcher.get(d).highlight(8, "backward", bag, EAS, EAS);
        Thread.sleep(2000);
        BlockingMatcher second = BlockingMatcher.lastMatcher;
        assertNotNull("No second matcher", second);
        assertTrue("Second matcher should be blocking", second.blocking);
        
        assertFalse("First blocking matcher was not interrupted", first.blocking);
        assertFalse("There should be no highlights", bag.getHighlights(0, Integer.MAX_VALUE).moveNext());
        }
    }
    
    public void testBlockingByThreadSleep() throws Exception {
        MockServices.setServices(MockMimeLookup.class);
        MockMimeLookup.setInstances(MimePath.EMPTY, new BlockingMatcher());
        
        AttributeSet EAS = SimpleAttributeSet.EMPTY;
        PlainDocument d = new PlainDocument();
        OffsetsBag bag = new OffsetsBag(d);
        d.insertString(0, "text text { text } text", null);

        BlockingMatcher.blockByForLoop = false;
        BlockingMatcher.origin = new int [] { 2, 3 };
        BlockingMatcher.matches = new int [] { 10, 11 };

        {
        BlockingMatcher.blockInFindOrigin = true;
        
        MasterMatcher.get(d).highlight(7, "backward", bag, EAS, EAS);
        Thread.sleep(300);
        BlockingMatcher first = BlockingMatcher.lastMatcher;
        assertNotNull("No first matcher", first);
        assertTrue("Should be blocking", first.blocking);
        
        MasterMatcher.get(d).highlight(8, "backward", bag, EAS, EAS);
        Thread.sleep(2000);
        BlockingMatcher second = BlockingMatcher.lastMatcher;
        assertNotNull("No second matcher", second);
        
        assertFalse("First blocking matcher was not interrupted", first.blocking);
        assertFalse("There should be no highlights", bag.getHighlights(0, Integer.MAX_VALUE).moveNext());
        }
        
        {
        BlockingMatcher.blockInFindOrigin = false;
        
        MasterMatcher.get(d).highlight(7, "backward", bag, EAS, EAS);
        Thread.sleep(300);
        BlockingMatcher first = BlockingMatcher.lastMatcher;
        assertNotNull("No first matcher", first);
        assertTrue("First matcher should be blocking", first.blocking);
        
        MasterMatcher.get(d).highlight(8, "backward", bag, EAS, EAS);
        Thread.sleep(2000);
        BlockingMatcher second = BlockingMatcher.lastMatcher;
        assertNotNull("No second matcher", second);
        assertTrue("Second matcher should be blocking", second.blocking);
        
        assertFalse("First blocking matcher was not interrupted", first.blocking);
        assertFalse("There should be no highlights", bag.getHighlights(0, Integer.MAX_VALUE).moveNext());
        }
    }
    
    private static final class TestMatcher implements BracesMatcher, BracesMatcherFactory {

        public static TestMatcher lastMatcher = null; 
        public static int [] origin = null;
        public static int [] matches = null;
        
        public final MatcherContext context;
        
        public TestMatcher() {
            this(null);
        }
        
        private TestMatcher(MatcherContext context) {
            this.context = context;
        }
        
        public int[] findOrigin() throws InterruptedException, BadLocationException {
            return origin;
        }

        public int[] findMatches() throws InterruptedException, BadLocationException {
            return matches;
        }

        public BracesMatcher createMatcher(MatcherContext context) {
            lastMatcher = new TestMatcher(context);
            return lastMatcher;
        }
    }

    private static final class BlockingMatcher implements BracesMatcher, BracesMatcherFactory {

        public static BlockingMatcher lastMatcher = null; 
        
        public static boolean blockInFindOrigin = false;
        public static boolean blockByForLoop = false;
        
        public static int [] origin = null;
        public static int [] matches = null;
        
        public final MatcherContext context;
        
        public volatile boolean blocking;
        
        public BlockingMatcher() {
            this(null);
        }
        
        private BlockingMatcher(MatcherContext context) {
            this.context = context;
        }
        
        public int[] findOrigin() throws InterruptedException, BadLocationException {
            if (blockInFindOrigin) {
                block();
            }
            return origin;
        }

        public int[] findMatches() throws InterruptedException, BadLocationException {
            if (!blockInFindOrigin) {
                block();
            }
            return matches;
        }

        private void block() throws InterruptedException {
            blocking = true;
            try {
                //System.out.println("!!! Blocking: " + this + ", offset = " + context.getCaretOffset());
                if (blockByForLoop) {
                    for( ; ; ) {
                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }
                    }
                } else {
                    Thread.sleep(999999999999999L);
                }
            } finally {
                //System.out.println("!!! Not Blocking: " + this + ", offset = " + context.getCaretOffset());
                blocking = false;
            }
        }
        
        public BracesMatcher createMatcher(MatcherContext context) {
            lastMatcher = new BlockingMatcher(context);
            return lastMatcher;
        }
    }
}

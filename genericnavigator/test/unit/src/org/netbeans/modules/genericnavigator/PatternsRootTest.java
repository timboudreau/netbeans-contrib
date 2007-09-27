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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import junit.framework.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.LocalFileSystem;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;

/**
 * Comprehensive test of all evil that can be done on a root node for the
 * patterns folder.
 *
 * @author Tim Boudreau
 */
public class PatternsRootTest extends TestCase {

    public PatternsRootTest(String testName) {
        super(testName);
    }

    File dataDir = new File (new File(System.getProperty("java.io.tmpdir")), "pattersRootTest");
    private void cleanUpAfterOldTests() {
        if (dataDir.exists()) {
            List <File> l = new ArrayList <File>();
            recurseDelete (dataDir,l);
            for (Iterator <File> i = l.iterator(); i.hasNext();) {
                File file = i.next();
                file.delete();
            }
        }
    }

    private void recurseDelete (File dataDir, List <File> l) {
        l.add (0, dataDir);
        if (dataDir.isDirectory()) {
            File[] kids = dataDir.listFiles();
            for (int i = 0; i < kids.length; i++) {
                File file = kids[i];
                recurseDelete (file, l);
            }
        }
    }

    private void createBaseData() throws IOException {
        dataDir.mkdir();
        File f = new File (dataDir, "text");
        f.mkdir();
        f = new File (f, "html");
        f.mkdir();
        String toWrite = "<\\s*[Ll][Ii]\\s*>(.*?)<\\s*/[lL][iI]\\s*>\n" +
                         "CASE_INSENSITIVE, DOTALL, MULTILINE,\\n";

        writeFile (f, "listitems", toWrite);

        toWrite = "(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)\n" +
                "CASE_INSENSITIVE, DOTALL,MULTILINE,\n";

        writeFile (f, "headers", toWrite);
    }

    private void writeFile (File dir, String name, String data) throws IOException {
        File f = new File (dir, name);

        f.createNewFile();

        f.deleteOnExit();
        FileOutputStream fos = new FileOutputStream (f);
        FileChannel ch = fos.getChannel();
        ch.write (ByteBuffer.wrap(data.getBytes()));
        ch.close();
    }

    protected void tearDown() throws Exception {
        cleanUpAfterOldTests();
    }

    PatternsRoot root;
    LocalFileSystem lfs;
    protected void setUp() throws Exception {
        PatternsRoot.log = false;
        cleanUpAfterOldTests();
        createBaseData();
        lfs = new LocalFileSystem ();
        lfs.setRootDirectory(dataDir);
        PatternItem.rootfolder = lfs.getRoot();
        root = new PatternsRoot (lfs.getRoot());
        initChildren (root);
    }

    private void initChildren (Node root) {
        System.err.println("Init Children " + root);
        Node[] n = root.getChildren().getNodes(true);
        for (int i = 0; i < n.length; i++) {
            initChildren (n[i]);
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PatternsRootTest.class);
        return suite;
    }

    private void outTree (Node n) {
        StringBuffer sb = new StringBuffer();
        outNode (n, 0, sb);
        System.err.println(sb);
    }

    private void outNode (Node n, int depth, StringBuffer sb) {
        char[] c = new char[depth * 2];
        Arrays.fill (c, ' ');
        sb.append (new String(c));
        sb.append ('-');
        sb.append (n);
        sb.append ('\n');
        Node[] nn = n.getChildren().getNodes(true);
        for (int i = 0; i < nn.length; i++) {
            Node node = nn[i];
            outNode (nn[i], depth + 1, sb);
        }
    }


    public void testHierarchy() throws Exception {
        System.out.println("testHierarchy");
        Node[] ch = root.getChildren().getNodes(true);
        assertEquals (1, ch.length);
        Node n = ch[0];
        assertEquals ("text/html", n.getName());
        assertEquals ("text/html", n.getDisplayName());

        NL nl = new NL();
        n.addNodeListener(nl);

        Node[] kk = n.getChildren().getNodes(true);
        assertEquals(2, kk.length);
        assertEquals("headers", kk[0].getName());
        assertEquals("listitems", kk[1].getName());
        assertEquals("headers", kk[0].getDisplayName());
        assertEquals("listitems", kk[1].getDisplayName());
        PatternItemProvider pip1 = (PatternItemProvider )
            kk[0].getLookup().lookup (PatternItemProvider.class);

        PatternItemProvider pip2 = (PatternItemProvider )
            kk[1].getLookup().lookup (PatternItemProvider.class);

        assertNotNull (pip1);
        assertNotNull (pip2);

        assertEquals ("headers", pip1.getDisplayName());
        assertEquals ("listitems", pip2.getDisplayName());

        assertEquals ("text/html", pip1.getMimeType());
        assertEquals ("text/html", pip2.getMimeType());

        PatternItem item1 = pip1.getPatternItem();
        PatternItem item2 = pip2.getPatternItem();

        assertEquals ("(<\\s*[Hh][1-6]\\s*>)(.*?)(</\\s*[Hh][1-6]\\s*>)", item1.getPatternString());
        assertEquals ("<\\s*[Ll][Ii]\\s*>(.*?)<\\s*/[lL][iI]\\s*>", item2.getPatternString());


        Pattern p1 = item1.getPattern();
        Pattern p2 = item2.getPattern();
//        String check = "<html><body><h1>Heading 1</h2>Some text\n<ul>\n<li>Item 1</li>\n<li>Item 2</li>\n</ul></body></html>";
//        assertTrue (p1.matcher(check).matches());
//        assertTrue (p2.matcher(check).matches());

        nl.assertNotRemoved();
        nl.assertNotAdded();
        root.remove(item1, "text/html");
        Node[] nueKids = n.getChildren().getNodes(true);

        nl.assertRemoved();
        nl.assertNotAdded();
        assertEquals (1, nueKids.length);

        Node only = nueKids[0];
        assertEquals ("listitems", only.getName());
        PatternItemProvider pip = (PatternItemProvider) only.getLookup().lookup(PatternItemProvider.class);
        assertEquals ("<\\s*[Ll][Ii]\\s*>(.*?)<\\s*/[lL][iI]\\s*>", pip.getPatternItem().getPatternString());
        assertEquals ("listitems", pip.getDisplayName());

        PatternItem nueItem = new PatternItem ("aaa", ".*?<p>.*\\", 0);
        PatternItem otherNueItem = new PatternItem ("aaa", ".*?<p>.*\\", 0);
        PatternsRoot.PIP prov1 = new PatternsRoot.PIP ("text/foo", nueItem);
        PatternsRoot.PIP prov2 = new PatternsRoot.PIP ("text/foo", otherNueItem);
        assertEquals (prov1, prov2);

        root.add (nueItem, "text/html");
        nueKids = n.getChildren().getNodes(true);

        assertEquals (2, nueKids.length);
        assertEquals ("aaa", nueKids[0].getName());

        nl.assertAdded();

        pip = (PatternItemProvider)
                nueKids[0].getLookup().lookup(PatternItemProvider.class);

        PatternItem pi = pip.getPatternItem();
        assertEquals (".*?<p>.*\\", pi.getPatternString());

        PatternItemProvider pip3 = (PatternItemProvider)
                nueKids[1].getLookup().lookup(PatternItemProvider.class);

        assertEquals ("listitems", pip3.getDisplayName());

        PatternItem replacesHeaders = new PatternItem ("headers", "foo", 0);
        nl.assertNotAdded();
//        nl.assertNotRemoved();
        PatternsRoot.log = true;
        root.add (replacesHeaders, "text/html");

        nl.assertAdded();
        PatternsRoot.log=false;

        nueKids = n.getChildren().getNodes(true);
        assertEquals (3, nueKids.length);

        assertEquals ("aaa", nueKids[0].getName());
        assertEquals ("headers", nueKids[1].getName());
        assertEquals ("listitems", nueKids[2].getName());

        pip = (PatternItemProvider)
                nueKids[1].getLookup().lookup(PatternItemProvider.class);
        assertEquals ("foo", pip.getPatternItem().getPatternString());
        System.err.println("\n\n****************");
        System.err.println("AS IT SHOULD BE:");
        outTree (root);

        System.err.println("---------------------");
        root.store();
        System.err.println("---------------------");

        FileObject fob = lfs.getRoot().getFileObject("text/html/aaa");
        assertNotNull (fob);
        fob = lfs.getRoot().getFileObject("text/html/headers");
        assertNotNull (fob);
        fob = lfs.getRoot().getFileObject("text/html/listitems");
        assertNotNull (fob);


        PatternsRoot root = new PatternsRoot (lfs.getRoot());

        System.err.println("AS IT IS: ");
        System.err.println("**********************\n\n");
        outTree (root);

        nueKids = root.getChildren().getNodes(true);
        assertEquals (1, nueKids.length);

        nueKids = nueKids[0].getChildren().getNodes(true);
        assertEquals (3, nueKids.length);

        assertEquals ("aaa", nueKids[0].getName());
        assertEquals ("headers", nueKids[1].getName());
        assertEquals ("listitems", nueKids[2].getName());

        PatternItemProvider[] pp3 = new PatternItemProvider[3];
        for (int i = 0; i < pp3.length; i++) {
            pp3[i] = (PatternItemProvider) nueKids[i].getLookup().lookup(PatternItemProvider.class);
            assertFalse (pp3[i].isVirtual());
        }

        PatternItem another = new PatternItem ("another", "goo", 0);
        NL l = new NL();
//        l.log=true;
        root.addNodeListener(l);
        l.assertNotAdded();

        root.add(another, "text/foo");

        nueKids = root.getChildren().getNodes(true);
        l.assertAdded();
        assertEquals ("Should be two elements, not " + Arrays.asList(nueKids), 2, nueKids.length);

        assertEquals ("text/html", nueKids[1].getName());
        assertEquals ("text/foo", nueKids[0].getName());

        nueKids = nueKids[0].getChildren().getNodes(true);
        assertEquals (1, nueKids.length);

        assertEquals ("another", nueKids[0].getName());

        pip = (PatternItemProvider)
                nueKids[0].getLookup().lookup(PatternItemProvider.class);

        assertEquals ("another", pip.getDisplayName());
        assertSame (another, pip.getPatternItem());

        root.remove (another, "text/foo");

        assertEquals (1, root.getChildren().getNodes(true).length);

        root.add (another, "text/foo");
        nueKids = root.getChildren().getNodes(true);
        l.assertAdded();
        assertEquals ("Should be two elements, not " + Arrays.asList(nueKids), 2, nueKids.length);

        System.err.println("Get Children For " + nueKids[0].getName());
        String type = nueKids[0].getName();
        nueKids=nueKids[0].getChildren().getNodes(true);

        assertEquals ("Should be two elements for "+type+", not " + Arrays.asList(nueKids), 1, nueKids.length);

        nueKids = root.getChildren().getNodes(true);
        nueKids=nueKids[1].getChildren().getNodes(true);

        assertEquals (3, nueKids.length);
        outTree (root);

        pip = (PatternItemProvider)
                nueKids[0].getLookup().lookup(PatternItemProvider.class);

        PatternItemProvider yetAnother = (PatternItemProvider)
                nueKids[1].getLookup().lookup(PatternItemProvider.class);

        PatternItemProvider stillAnother = (PatternItemProvider)
                nueKids[2].getLookup().lookup(PatternItemProvider.class);

        type = "text/html";
        root.remove (pip.getPatternItem(), type);
        assertEquals (2, root.getChildren().getNodes(true).length);

        root.remove (yetAnother.getPatternItem(), type);
        root.remove (stillAnother.getPatternItem(), type);

        outTree(root);

        assertEquals (1, root.getChildren().getNodes(true).length);

        n = root.getChildren().getNodes(true)[0];
        type = n.getName();
        assertEquals (1, n.getChildren().getNodes(true).length);
        n = n.getChildren().getNodes()[0];
        pip = (PatternItemProvider)
                n.getLookup().lookup(PatternItemProvider.class);

        root.remove (pip.getPatternItem(), type);

        assertEquals (0, n.getChildren().getNodes(true).length);

        outTree (root);
    }

    public void testRemove() throws Exception {
        System.out.println("testRemove");
        Node[] ch = root.getChildren().getNodes(true);
        assertEquals (1, ch.length);

        Node[] chch = root.getChildren().getNodes(true) [0].getChildren().getNodes();
        PatternItem[] pi = new PatternItem[chch.length];
        assertEquals (2, chch.length);

        for (int i = 0; i < chch.length; i++) {
            Node node = chch[i];
            PatternItemProvider prov = (PatternItemProvider)
                node.getLookup().lookup(PatternItemProvider.class);
            assertNotNull (prov);
            pi[i] = prov.getPatternItem();
        }

        for (int i = 0; i < pi.length; i++) {
            PatternItem patternItem = pi[i];
            root.remove(patternItem, "text/html");
        }

        ch = root.getChildren().getNodes(true);
        assertEquals (0, ch.length);

        PatternItem item1 = new PatternItem ("a", "a", 0);
        PatternItem item2 = new PatternItem ("b", "a", 0);
        PatternItem item3 = new PatternItem ("c", "a", 0);

        root.add (item1, "application/x-foo");
        root.add (item2, "application/x-foo");
        root.add (item3, "application/x-foo");

        ch = root.getChildren().getNodes(true);
        assertEquals (1, ch.length);
        chch = ch[0].getChildren().getNodes(true);
        assertEquals (3, chch.length);

        root.store();

        root = new PatternsRoot (lfs.getRoot());
        ch = root.getChildren().getNodes(true);
        outTree (root);

        assertEquals (1, ch.length);
        chch = ch[0].getChildren().getNodes(true);
        assertEquals (3, chch.length);
        pi = new PatternItem[3];
        int ix = 0;
        for (char c = 'a'; c < 'd'; c++) {
            Node node = chch[ix];
            PatternItemProvider prov = (PatternItemProvider)
                node.getLookup().lookup(PatternItemProvider.class);
            System.err.println("GOT " + c + " :" + prov);

            assertNotNull (prov);
            pi[ix] = prov.getPatternItem();
            assertEquals (new String(new char[] {c}), prov.getDisplayName());
            assertEquals (new String(new char[] {c}), pi[ix].getDisplayName());
            assertEquals ("application/x-foo", prov.getMimeType());
            ix++;
        }

        for (int i = 0; i < pi.length; i++) {
            System.err.println("REMOVE " + i + ":" + pi[i]);
            root.remove (pi[i], "application/x-foo");
        }
        ch = root.getChildren().getNodes(true);
        outTree(root);
        assertEquals (0, ch.length);

        root.store();

//        root = new PatternsRoot (lfs.getRoot());
//        ch = root.getChildren().getNodes(true);
//        System.err.println("\n\n-----------------------------");
//        outTree(root);
//        assertEquals (1, ch.length);
//        chch = root.getChildren().getNodes(true);
//        assertEquals (2, ch.length);
//
//        PatternItem[] ppp = new PatternItem [2];
//        for (int i = 0; i < chch.length; i++) {
//            PatternItemProvider pippp = (PatternItemProvider)
//                chch[i].getLookup().lookup(PatternItemProvider.class);
//            ppp[i] = pippp.getPatternItem();
//            root.remove(ppp[i], "text/html");
//        }
//        outTree (root);
//        assertEquals (0, root.getChildren().getNodes(true).length);
//
//        root.store();
        outTree (root);
        root = new PatternsRoot (lfs.getRoot());
        ch = root.getChildren().getNodes(true);
        assertEquals (0, ch.length);
    }

    public void testRemoveByProvider() throws Exception {
        System.out.println("testRemoveByProvider");
        System.out.println("testHierarchy");
        Node[] ch = root.getChildren().getNodes(true);
        Node[] chch = ch[0].getChildren().getNodes(true);
        assertEquals(2, chch.length);

        PatternItemProvider[] provs = new PatternItemProvider[chch.length];
        for (int i = 0; i < chch.length; i++) {
            provs[i] = (PatternItemProvider) chch[i].getLookup().lookup(
                    PatternItemProvider.class);
        }

        root.log = true;
        root.remove (provs[1]);
        chch = ch[0].getChildren().getNodes(true);
        assertEquals (1, chch.length);

    }


    private class NL implements NodeListener {
        private NodeMemberEvent removed;
        private NodeMemberEvent added;

        private void assertNotAdded () {
            assertNull (added);
        }

        private void assertNotRemoved() {
            assertNull (removed);
        }

        private void assertAdded () {
            NodeMemberEvent old = added;
            added = null;
            assertNotNull (old);
        }

        private void assertRemoved () {
            NodeMemberEvent old = removed;
            added = null;
            assertNotNull (old);
        }

        private void clear() {
            added = null;
            removed = null;
        }

        public void childrenAdded(org.openide.nodes.NodeMemberEvent x0) {
            System.err.println("Children added " + Arrays.asList(x0.getDelta()));
            added = x0;
        }

        boolean log = false;

        public void childrenRemoved(org.openide.nodes.NodeMemberEvent x0) {
            System.err.println("Children removed " + Arrays.asList(x0.getDelta()));
            if (log) Thread.dumpStack();
            removed = x0;
        }

        public void childrenReordered(org.openide.nodes.NodeReorderEvent x0) {
        }

        public void nodeDestroyed(org.openide.nodes.NodeEvent x0) {
        }

        public void propertyChange(java.beans.PropertyChangeEvent x0) {
        }
    }
}

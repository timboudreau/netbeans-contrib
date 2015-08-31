package org.netbeans.lib.callgraph;

import org.netbeans.lib.callgraph.CallgraphControl;
import org.netbeans.lib.callgraph.Arguments;
import org.netbeans.lib.callgraph.Callgraph;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests argument parsing.
 *
 * @author Tim Boudreau
 */
public class ArgumentsTest {

    @Test
    public void testBooleanSwitches() {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        String tmp = tmpdir.getAbsolutePath();

        Arguments args = new Arguments("-s", tmp);
        assertFalse(args.isMaven());
        assertTrue(args.isShortNames());
        assertTrue(args.isSelfReferences());
        assertTrue(args.folders().contains(new File(tmp)));

        args = new Arguments("-n", tmp);
        assertFalse(args.isShortNames());
        assertFalse(args.isSelfReferences());
        assertTrue(args.folders().contains(new File(tmp)));

        args = new Arguments("-n", "-g", tmp + File.separator + "out.txt", 
                "--exclude", "foo.bar,foo.baz", System.getProperty("java.io.tmpdir"));
        assertFalse(args.isShortNames());
        assertFalse(args.isSelfReferences());
        assertTrue(args.folders().contains(new File(tmp)));
        assertNotNull(args.methodGraphFile());
        assertEquals(new File(tmpdir, "out.txt"), args.methodGraphFile());
        assertFalse(args.excludePrefixes().isEmpty());
        assertTrue(args.excludePrefixes().contains("foo.bar"));
        assertTrue(args.excludePrefixes().contains("foo.baz"));
    }

    @Test
    public void testBuilder() {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        String tmp = tmpdir.getAbsolutePath();
        CallgraphControl ctrl = Callgraph.configure().classGraphOutput(new File(tmpdir, "classes.txt"))
                .excludePrefix("foo.bar").excludePrefix("baz.quux")
                .ignoreSelfReferences().quiet().methodGraphOutput(new File(tmpdir, "methods.txt"))
                .addSourceParent(tmpdir).packageGraphOutput(new File(tmpdir, "pkgs.txt")).build();
    }

    @Test(expected = Arguments.InvalidArgumentsException.class)
    public void testNoArguments() {
        new Arguments();
    }

    @Test(expected = Arguments.InvalidArgumentsException.class)
    public void testQuietAndNoOuput() {
        new Arguments("-q", System.getProperty("java.io.tmpdir"));
    }

    @Test(expected = Arguments.InvalidArgumentsException.class)
    public void testNonExistentFolder() {
        new Arguments("/" + System.currentTimeMillis());
    }

    @Test
    public void testMavenScan() throws IOException {
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File dir = new File(tmp, ArgumentsTest.class.getSimpleName() + "_" + System.currentTimeMillis());
        File project1 = new File(dir, "prj");
        File project2 = new File(dir, "prj2");
        File pom1 = new File(project1, "pom.xml");
        File pom2 = new File(project2, "pom.xml");

        File src1 = new File(project1, "src");
        File main1 = new File(src1, "main");
        File java1 = new File(main1, "java");

        File src2 = new File(project2, "src");
        File main2 = new File(src2, "main");
        File java2 = new File(main2, "java");

        assertTrue(java1.mkdirs());
        assertTrue(java2.mkdirs());
        pom1.createNewFile();
        pom2.createNewFile();
        try {

            Arguments args = new Arguments("--maven", "-s", dir.getAbsolutePath());
            assertTrue(args.isMaven());
            assertTrue(args.isShortNames());
            assertTrue(args.isSelfReferences());
            assertTrue(args.folders().contains(java1));
            assertTrue(args.folders().contains(java2));
        } finally {
            // Clean up
            assertTrue(pom1.delete());
            assertTrue(pom2.delete());
            assertTrue(java1.delete());
            assertTrue(java2.delete());
            assertTrue(main1.delete());
            assertTrue(main2.delete());
            assertTrue(src1.delete());
            assertTrue(src2.delete());
            assertTrue(project1.delete());
            assertTrue(project2.delete());
            assertTrue(dir.delete());
        }
    }

}

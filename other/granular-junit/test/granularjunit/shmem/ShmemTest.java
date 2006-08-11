package granularjunit.shmem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Tim Boudreau
 */
public class ShmemTest extends TestCase {
    
    public ShmemTest(String testName) {
        super(testName);
    }
    
    
    Shmem one = null;
    Shmem two = null;
    A a;
    A b;
    protected void setUp() throws Exception {
        byte[] b = new byte[2048];
        String spec = "testfile" + System.currentTimeMillis();
        File f = new File (new File (System.getProperty("java.io.tmpdir")), spec);
        if (!f.createNewFile()) {
            fail ("Couldn't create " + f.getPath());
        }
        
        FileOutputStream str = new FileOutputStream (f);
        byte[] bytes = (Shmem.MAGIC_SEQ + f.getPath() + '\n').getBytes("UTF-8");
        System.arraycopy(bytes, 0, b, 0, bytes.length);
        str.write(b);
        str.close();
        
        one = new Shmem (new FileInputStream(f));
        two = new Shmem(new FileInputStream(one.getFile()));
        a = new A(one);
        this.b = new A(two);
    }
    
    private void sleep() throws Exception {
        Thread.currentThread().sleep (1000);
    }
    
    public void testSanity() {
        System.out.println("testSanity");
        assertFalse (one.getUID() == two.getUID());
    }

    public void testWrite() throws Exception {
        System.out.println("testWrite");
        a.postWrite(5, "Hello");
        synchronized (a) {
            a.wait(3000);
        }
        Command c = two.getNextCommand(2000);
        assertNotNull (c);
        assertEquals (5, c.getCommand());
        assertEquals (one.getUID(), c.getSourceUid());
        assertEquals ("Hello", c.getContent());
    }
    
    public void testGetNextCommand() throws Exception {
        System.out.println("testGetNextCommand");
        a.postWrite(2, "Whee");
        synchronized (a) {
            a.wait(2000);
        }
        
        b.assertHasDataWaiting();
        a.assertOwnsData();
        Command c = two.getNextCommand(2000);
        assertNotNull (c);
        assertEquals ("Whee", c.getContent());
        assertEquals (c.getSourceUid(), one.getUID());
        assertEquals (2, c.getCommand());
    }
    
    public void testAltConstructors() throws Exception {
        String spec = "testfile" + (System.currentTimeMillis() * System.identityHashCode(this));
        File f = new File (new File (System.getProperty("java.io.tmpdir")), spec);
        while (f.exists()) {
            spec = "testfile" + (System.currentTimeMillis() + 
                    new Random (System.identityHashCode(this)).nextLong());
            f = new File (new File (System.getProperty("java.io.tmpdir")), spec);
        }
        if (f.exists()) {
            if (!f.delete()) {
                fail ("Couldn't delete " + f);
            }
        }
        if (!f.createNewFile()) {
            fail ("Couldn't create " + f.getPath());
        }
        
        System.out.println("testAltConstructors");
        OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
        Shmem first = new Shmem(fos);
        fos.close();
        
        InputStream in = new BufferedInputStream(new FileInputStream (f));
        System.err.println("AVAIL BYTES " + in.available());
        System.err.println("FILE SIZE " + f.length());
        Shmem second = new Shmem (in);
        in.close();
        assertEquals (first.getFile(), second.getFile());
    }
    
    private class A {
        private Shmem mem;
        A (Shmem mem) {
            this.mem = mem;
        }
        
        public void write (int cmd, String content)  throws Exception {
            mem.send(cmd, content);
        }
        
        public Command read()  throws Exception {
            Command result = mem.getNextCommand(3000);
            System.err.println("READ CMD " + result);
            return result;
        }
        
        public void assertHasDataWaiting()  throws Exception {
            assertTrue (mem.hasDataWaiting());
        }
        
        public void assertOwnsData() throws Exception {
            assertTrue (mem.ownsBufferData());
        }
        
        public void postWrite (final int cmd, final String content) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        fail();
                    }
                    try {
                        write (cmd, content);
                        synchronized (A.this) {
                            A.this.notifyAll();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException (e);
                    }
                }
            };
            new Thread(r).start();
        }
        
        public void postRead (final Command[] c) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().sleep (500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        fail();
                    }
                    try {
                        c[0] = read ();
                        System.err.println("READ " + c[0]);
                        synchronized (A.this) {
                            A.this.notifyAll();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException (e);
                    }
                }
            };
            new Thread(r).start();
        }
    }
}

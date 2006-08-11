package granularjunit.shmem;

import junit.framework.TestCase;
import junit.framework.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class CommandTest extends TestCase {    
    ByteBuffer buf;
    long uid = 0;
    public CommandTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        buf = ByteBuffer.allocate (2048);
        uid = System.currentTimeMillis();
        buf.asLongBuffer().put(uid);
    }

    public void testGetUid() {
        System.out.println("testGetUid");
        assertFalse (Command.isUID(-1, buf));
        assertTrue (Command.isUID(uid, buf));
    }

    public void testGetCommand() throws Exception {
        System.out.println("testGetCommand");
        Command cmd = new Command (233, 1, "Hello world");
        cmd.write(buf, false);
        Command c = Command.read(buf);
        assertEquals (cmd, c);
    }

    public void testGetContent() {
        System.out.println("testGetContent");
        String s = "This is a command";
        Command cmd = new Command (233, 1, s);
        cmd.write(buf, false);
        Command c = Command.read(buf);
        assertEquals (s, c.getContent());
    }

    public void testEquals() {
        System.out.println("testEquals");
        Command a = new Command (122, 3, "Whoopee");
        Command b = new Command (122, 3, "Whoopee");
        Command c = new Command (123, 3, "Whoopee");
        Command d = new Command (122, 4, "Whoopee");
        Command e = new Command (122, 3, "Whoopee!!");
        assertEquals (a, b);
        assertFalse(a.equals(c));
        assertFalse(a.equals(d));
        assertFalse(a.equals(e));
    }

    public void testHashCode() {
        System.out.println("testHashCode");
        Command a = new Command (122, 3, "Whoopee");
        Command b = new Command (122, 3, "Whoopee");
        Command c = new Command (123, 3, "Whoopee");
        assertEquals (a.hashCode(), b.hashCode());
        assertFalse (a.hashCode() == c.hashCode());
    }

    public void testWaitForAcknowledgement() throws Exception {
        System.out.println("testWaitForAcknowledgement");
        final Command a = new Command (122, 3, "Whoopee");
        a.write(buf, false);
        Runnable r = new Runnable() {
            public void run() {
                System.err.println(" running");
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException ex) {
                    fail ("interrupted");
                }
                System.err.println("Delay finished");
                Command.setAcknowledged(buf);
                
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        new Thread (r).start();
//        synchronized (r) {
//            r.wait(3000);
//        }
        a.setAcknowledged(buf);
        boolean ack = a.waitForAcknowledgement(buf);
        assertTrue (Command.isAcknowledge(122, buf));
        assertTrue (ack);
    }

     public void testWaitForCommand() throws Exception {
        System.out.println("testWaitForCommand");
        System.err.println("okay...");
        final Command[] cmds = new Command[1];
        final boolean[] ack = new boolean[] { false };
        Runnable r = new Runnable() {
            public void run() {
                try {
                    System.err.println("Thread running");
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException ex) {
                    fail ("interrupted");
                }
                cmds[0] = new Command (302, 5, "Data is here");
                System.err.println(" write command " + cmds[0]);
                cmds[0].write(buf, false);
                assertEquals (cmds[0], Command.read(buf));
                System.err.println(" command written, acknowledged ");
                try {
                    System.err.println("Start wait for ack " + System.currentTimeMillis());
                    ack[0] = Command.waitForAcknowledgement(302, -1, buf);
                    System.err.println("Done wait for ack " + System.currentTimeMillis());
                    ack[0] = Command.isAcknowledge(302, buf);
                    assertTrue (ack[0]);
                    synchronized (this) {
                        notifyAll();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    fail();
                }
                System.err.println("Acknowledged? " + ack[0]);
            }
        };
        byte[] zeros = new byte[2000];
        buf.position(0);
        buf.put(zeros);
        System.err.println("Starting thread...");
        new Thread (r).start();
        Thread.currentThread().sleep(100);
        Command.waitForCommand(122, 3000, buf);
        System.err.println("Finished wait for command");
        Command c = Command.read(buf);
        assertEquals (c, cmds[0]);
        assertFalse (ack[0]);
        assertFalse (Command.isAcknowledge(302, buf));
        Command.setAcknowledged(buf);
        assertTrue (Command.isAcknowledge(302, buf));
        synchronized (r) {
            r.wait(5000);
        }
        assertTrue (Command.isAcknowledge(302, buf));
//        Thread.currentThread().sleep (3000);
//        assertTrue (ack[0]);
    }

    public void testIsAcknowledge() {
        System.out.println("testIsAcknowledge");
        Command c = new Command (3204, 17, "testIsAcknowledge");
        c.write(buf, false);
        assertFalse (Command.isAcknowledge(3204, buf));
        Command.setAcknowledged(buf);
        assertTrue (Command.isAcknowledge(3204, buf));
    }
    public void testIsCommand() {
        System.out.println("testIsCommand");
        byte[] zeros = new byte[2000];
        buf.put (zeros);
        assertFalse (Command.isAcknowledge(123, buf));
        assertFalse (Command.isCommand(3204, buf));
        Command c = new Command (3204, 17, "testIsAcknowledge");
        c.write(buf, false);
        assertTrue (Command.isCommand(17, buf));
        assertFalse (Command.isCommand(0, buf));
        Command.setAcknowledged(buf);
        assertTrue (Command.isCommand(Command.ACK, buf));
    }
    public void testIsUID() {
        System.out.println("testIsUID");
        Command c = new Command (3204, 17, "testIsAcknowledge");
        c.write(buf, false);
        assertTrue (Command.isUID(3204, buf));
        assertFalse (Command.isUID(32093, buf));
        c = new Command (32093, 17, "testIsAcknowledge");
        c.write(buf, false);
        assertTrue (Command.isUID(32093, buf));
        assertFalse (Command.isUID(3204, buf));
    }

    public void testIsDataWaiting() {
        System.out.println("testIsDataWaiting");
        Command c = new Command (3204, 17, "testIsAcknowledge");
        c.write(buf, false);
        assertTrue (Command.isDataWaiting(123, buf));
        assertFalse (Command.isDataWaiting(3204, buf));
        Command.setAcknowledged(buf);
        assertFalse (Command.isDataWaiting(3204, buf));
    }
}

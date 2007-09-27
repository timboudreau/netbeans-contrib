/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

/**
 * Tests for the search worker.
 *
 * XXX - the main thread starts a search worker and waits for a few files
 * to be added before calling the tested method. When the files are added the
 * main threas is waken up and the search thread waits for this to happen.
 * All the waiting is done using Object.wait(). Maybe this interferes 
 * with the search thread too much.
 *
 * Another approach would be to just poll the search result in the main thread,
 * something like:
 *
 * while (result.getSize() < 5); 
 * 
 * @author Andrei Badea
 */
public class SearchWorkerTest extends NbTestCase {
    
    private RequestProcessor rp;
    
    public SearchWorkerTest(String testName) {
        super(testName);
    }
    
    public void setUp() {
        rp = new RequestProcessor("SearchWorkerTest", 1, true);
    }
    
    public void tearDown() {
        rp = null;
    }
    
    public void testSetFilterAndStart() throws Exception {
        FileSearchResult result = new FileSearchResult();
        SearchWorker s = new SearchWorker(rp, new FileObject[] { Utils.createSearchRoot(getWorkDir()) }, result);
        s.setFilter(new PrefixSearchFilter("foobar", true));
        
        s.start();
        s.waitFinished();
        
        Utils.checkResult(result, 10, "foobar");
    }
    
    public void testCancel() throws Exception {
        NotifyingFileSearchResult result = new NotifyingFileSearchResult(5);
        FileObject root = Utils.createSearchRoot(getWorkDir());
        SearchWorker s = new SearchWorker(rp, new FileObject[] { root }, result);
        s.setFilter(new PrefixSearchFilter("", true));
        s.start();
        
        synchronized (this) {
            while (!result.isThresholdExceeded()) {
                wait();
            }
        }
        result.wakeUp();
        
        s.cancel();
        
        int size = result.getSize();
        // XXX we hope here that the thread will really be interrupted before
        // processing all the children of the search root
        assertTrue(size < Utils.getFileCount(root));

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {}
        
        // no items have been added since the last check
        assertEquals(size, result.getSize());
        
        System.out.println("Canceled after having added " + result.getAdded());
    }
    
    public void testRestart() throws Exception {
        NotifyingFileSearchResult result = new NotifyingFileSearchResult(5);
        FileObject root = Utils.createSearchRoot(getWorkDir());
        final SearchWorker s = new SearchWorker(rp, new FileObject[] { root }, result);
        s.setFilter(new PrefixSearchFilter("", true));
        s.start();
        
        // wait
        synchronized (this) {
            while (!result.isThresholdExceeded()) {
                wait();
            }
        }
        result.wakeUp();
        
        // restart
        s.start();
        s.waitFinished();
        
        int count = Utils.getFileCount(root);
        assertTrue(result.getAdded() >= result.getThreshold() + Utils.getFileCount(root));
        
        System.out.println("Restarted after having added " + (result.getAdded() - count));
    }
    
    public void testSetFilterDuringSearch() throws Exception {
        class MyFileSearchResult extends NotifyingFileSearchResult {
            
            volatile SearchFilter filter;
            
            public MyFileSearchResult(int threshold) {
                super(threshold);
            }
            
            public void add(FileObject fo) {
                if (filter != null) {
                    assertTrue("Trying to add a file not accepted by the filter", filter.accept(fo));
                }
                super.add(fo);
            }
        }
        
        MyFileSearchResult result = new MyFileSearchResult(5);
        FileObject root = Utils.createSearchRoot(getWorkDir());
        SearchWorker s = new SearchWorker(rp, new FileObject[] { root }, result);
        s.setFilter(new PrefixSearchFilter("foo", true));
        s.start();
        
        synchronized (this) {
            while (!result.isThresholdExceeded()) {
                wait();
            }
        }
        result.wakeUp();
        
        result.filter = new PrefixSearchFilter("foobar", true);
        s.setFilter(result.filter);
        s.waitFinished();
    }
    
    public void testSetFilterAfterSearch() throws Exception {
        FileObject root = Utils.createSearchRoot(getWorkDir());
        NotifyingFileSearchResult result = new NotifyingFileSearchResult(Utils.getFileCount(root) - 1);
        SearchWorker s = new SearchWorker(rp, new FileObject[] { root }, result);
        s.setFilter(new PrefixSearchFilter("", true));
        s.start();
        
        synchronized (this) {
            while (!result.isThresholdExceeded()) {
                wait();
            }
        }
        result.wakeUp();
        
        SearchFilter newFilter = new PrefixSearchFilter("foobar", true);
        s.setFilter(newFilter);
        
        assertSame(newFilter, s.getFilter());
    }
    
    /**
     * Tests that setFilter() after cancel() doesn't wait indefinitely.
     * This would happen if we forgot to set searchTask to null during cancel().
     * Well, yes, it did happen, that's why there's a test for it :-)
     */
    public void testSetFilterAfterCancel() throws Exception {
        NotifyingFileSearchResult result = new NotifyingFileSearchResult(5);
        FileObject root = Utils.createSearchRoot(getWorkDir());
        SearchWorker s = new SearchWorker(rp, new FileObject[] { root }, result);
        s.setFilter(new PrefixSearchFilter("", true));
        s.start();
        
        synchronized (this) {
            while (!result.isThresholdExceeded()) {
                wait();
            }
        }
        result.wakeUp();
        
        s.cancel();
        
        // XXX should create a thread here which would interrupt
        // the main thread if it waited too long
        s.setFilter(new PrefixSearchFilter("foo", true));
    }
    
    /**
     * Calls notifyAll() on the test class when the threshold-th file is added
     */ 
    class NotifyingFileSearchResult extends FileSearchResult {
        
        private Object sync = new Object();

        private int threshold;
        private boolean thresholdExceeded;
        private boolean wait = true;
        
        /**
         * the number of times the add() method was called 
         * can be greater than getSize(), since we can restart the search
         */
        private volatile int added;
        
        public NotifyingFileSearchResult(int threshold) {
            this.threshold = threshold;
        }
        
        public int getThreshold() {
            return threshold;
        }
        
        public boolean isThresholdExceeded() {
            return thresholdExceeded;
        }
        
        public int getAdded() {
            return added;
        }

        public void add(FileObject fo) {
            added++;
            // wake up the main thread if the threshold is reached
            if (getSize() + 1 == threshold) {
                synchronized (SearchWorkerTest.this) {
                    thresholdExceeded = true;
                    SearchWorkerTest.this.notifyAll();
                }
                // wait for the main thread to wake up
                // this is necessary as sometimes the main thread takes so long
                // to wake up that the search would finish
                synchronized (sync) {
                    while (wait) {
                        try {
                            sync.wait();
                        } catch (InterruptedException e) {}
                    }
                }
            }
            super.add(fo);
        }
        
        public void wakeUp() {
            synchronized (sync) {
                wait = false;
                sync.notifyAll();
            }
        }
    }
}

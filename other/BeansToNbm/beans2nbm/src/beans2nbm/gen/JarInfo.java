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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package beans2nbm.gen;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.SwingUtilities;

/**
 *
 * @author Tim Boudreau
 */
public class JarInfo {
    private File f;
    /** Creates a new instance of JarInfo */
    public JarInfo(String filespec) {
        if (filespec == null || filespec.trim().length() == 0) {
            throw new NullPointerException();
        }
        f = new File (filespec);
    }
    
    public String getFileName() {
        return f.getPath();
    }
    
    private String problem = null;
    public String getProblem() {
        return problem;
    }
    
    private Checker checker = null;
    public void scan (ScanObserver observer) {
        if (initialized) {
            observer.done();
            return;
        }
        synchronized (this) {
            if (checker != null) {
                checker.abort();
            }
            checker = new Checker(observer);
        }
        observer.start();
        Thread t = new Thread (new Checker(observer), "Jar scan thread " + (ct++));
        t.start();
    }
    private static int ct = 0;
    
    public static interface ScanObserver {
        public void start();
        public void progress (int progress);
        public void fail (String msg);
        public void done ();
    }
    
    public List getBeans() {
        return initialized ? beans : Collections.EMPTY_LIST;
    }
    
    public List getEntries() {
        return initialized ? entries : Collections.EMPTY_LIST;
    }
    
    private boolean initialized = false;
    
    private List beans = null;
    private JarFile jar = null;
    private boolean init (Checker checker) {
        try {
            jar = new JarFile (f, true);
        } catch (IOException ioe) {
            problem = ioe.getMessage();
            checker.enqueueNotify(Integer.MIN_VALUE, problem);
            return false;
        }
        return true;
    }
    
    private void go (Checker checker) {
        if (init(checker)) {
            checker.enqueueNotify(25, null);
            if (findBeans((checker))) {
                checker.enqueueNotify(50, null);
                if (findEntries(checker)) {
                    checker.enqueueNotify(100, null);
                    initialized = true;
                }
            }
        }
    }
    
    private boolean findBeans (Checker checker) {
        try {
            beans = new ArrayList();
            Manifest man = jar.getManifest();
            if (man == null) {
                checker.enqueueNotify(Integer.MIN_VALUE, "Manifest missing");
                return false;
            }
            Map m = man.getEntries();
            for (Iterator i = m.keySet().iterator(); i.hasNext();) {
                String name = (String) i.next();
                if (name.endsWith(".class")) {
                    Attributes atts = (Attributes) m.get(name);
                    String val = atts.getValue("Java-Bean");
                    if (val != null && "true".equalsIgnoreCase(val)) {
                        beans.add (name);
                    }
                }
            }
        } catch (IOException ioe) {
            problem = ioe.getMessage();
            checker.enqueueNotify(Integer.MIN_VALUE, problem);
            return false;
        }
        return true;
    }
    
    private List entries = null;
    private boolean findEntries (Checker checker) {
//        assert jar != null;
        if (jar == null) throw new NullPointerException();
        entries = new ArrayList();
        try {
            int ix = 0;
            for (Enumeration en=jar.entries(); en.hasMoreElements();) {
                JarEntry entry = (JarEntry) en.nextElement();
                ix++;
                if (ix == 30) {
                    //fudge the scroll bar on a magic number...
                    checker.enqueueNotify(75, null);
                }
                if (entry.getName().endsWith(".class") && entry.getName().indexOf('$') < 0) {
                    entries.add (entry);
                }
            }
        } catch (RuntimeException ioe) {
            ioe.printStackTrace();
            problem = ioe.getMessage();
            checker.enqueueNotify(Integer.MIN_VALUE, problem);
            return false;
        }
        return true;
    }
    
    private class Checker implements Runnable {
        private final ScanObserver observer;
        public Checker (ScanObserver observer) {
            this.observer = observer;
        }
        
        private boolean aborted = false;
        public void abort() {
            aborted = true;
        }
        private boolean done = false;
        
        public boolean isDone() {
            return done;
        }

        public void run() {
            try {
                go(this);
                synchronized (observer) {
                    observer.notifyAll();
                }
            } finally {
                done = true;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        observer.done();
                    }
                });
            }
        }
        
        void enqueueNotify (int progress, String msg) {
            synchronized (lock) {
                if (msg != null) {
                    if (notify != null) {
                        notify.fail(msg);
                    } else {
                        notify = new Notify(Integer.MIN_VALUE);
                        notify.msg = msg;
                        SwingUtilities.invokeLater (notify);
                    }
                } else if (notify != null) {
                    notify.set (progress);
                } else {
                    SwingUtilities.invokeLater(new Notify(progress));
                }
            }
        }
        
        private Object lock = new Object();
        private Notify notify = null;
        
        private class Notify implements Runnable {
            private int val;
            public Notify (int val) {
                this.val = val;
            }
            
            public void set(int val) {
                synchronized (lock) {
                    this.val = val;
                }
            }
            
            String msg = null;

            public void fail(String msg) {
                this.msg = msg;
                set (Integer.MIN_VALUE);
            }
            
            public void run() {
                int currval;
                synchronized (lock) {
                    notify = null;
                    currval = val;
                }
                if (currval == Integer.MAX_VALUE) {
                    observer.done();
                } else if (currval == Integer.MIN_VALUE) {
                    observer.fail(msg);
                } else {
                    observer.progress(currval);
                }
            }
        }
    }
}

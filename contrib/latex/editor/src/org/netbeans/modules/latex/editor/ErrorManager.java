/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

/**
 *
 * @author Jan Lahoda
 */
public abstract class ErrorManager {
    
    private static ErrorManager instance = null;
    
    /** Creates a new instance of ErrorManager */
    protected ErrorManager() {
    }
    
    public synchronized static ErrorManager getDefault() {
        if (instance == null) {
            try {
                Class.forName("org.openide.ErrorManager");
                instance = new IDEErrorManager();
            } catch (Throwable t) {
                if (t instanceof ThreadDeath)
                    throw (ThreadDeath) t;
                
                instance = new SAErrorManager();
            }
        }
        
        return instance;
    }

    public abstract Throwable annotate(Throwable t, String annotation);
    public abstract Throwable annotate(Throwable t, Throwable th);
    public abstract void notify(Throwable t);
    public abstract void notifyInformational(Throwable t);
    
    private static class SAErrorManager extends ErrorManager {
        
        protected SAErrorManager() {
        }
        
        public Throwable annotate(Throwable t, String annotation) {
            System.err.println("About to annotate: ");
            t.printStackTrace(System.err);
            System.err.println("With: \"" + annotation + "\".");
            
            return t;
        }
    
        public void notify(Throwable t) {
            t.printStackTrace(System.err);
        }
        
        public Throwable annotate(Throwable t, Throwable th) {
            System.err.println("About to annotate: ");
            t.printStackTrace(System.err);
            System.err.println("With: ");
            th.printStackTrace(System.err);
            
            return t;
        }

        public void notifyInformational(Throwable t) {
            notify(t);
        }
        
    }
    
    private static class IDEErrorManager extends ErrorManager {
        
        public Throwable annotate(Throwable t, String annotation) {
            return org.openide.ErrorManager.getDefault().annotate(t, annotation);
        }
        
        public void notify(Throwable t) {
            org.openide.ErrorManager.getDefault().notify(t);
        }
        
        public Throwable annotate(Throwable t, Throwable th) {
            return org.openide.ErrorManager.getDefault().annotate(t, th);
        }

        public void notifyInformational(Throwable t) {
            org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, t);
        }
        
    }
}

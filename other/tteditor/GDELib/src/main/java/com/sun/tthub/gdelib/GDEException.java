
/*
 * GDEException.java
 *
 * Created on September 21, 2005, 12:30 PM
 *
 */

package com.sun.tthub.gdelib;

/**
 * The root exception thrown by any of the GDE methods.
 *
 * @author Hareesh Ravindran
 */
public class GDEException extends Exception {
    
    /** Creates a new instance of GDEException */
    public GDEException() {}
    public GDEException(String msg) { super(msg); }
    public GDEException(Throwable th) { super(th); }    
    public GDEException(String msg, Throwable th) { super(msg, th); }
}

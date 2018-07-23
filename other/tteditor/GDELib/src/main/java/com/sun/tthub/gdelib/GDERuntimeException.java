/*
 * GDERuntimeException.java
 *
 * Created on September 28, 2005, 2:35 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.sun.tthub.gdelib;

/**
 *
 * @author Hareesh Ravindran
 */
public class GDERuntimeException extends RuntimeException {
    
    /** Creates a new instance of GDERuntimeException */
    public GDERuntimeException() {}
    
    public GDERuntimeException(String msg) { super(msg); }

    public GDERuntimeException(Throwable th) { super(th); }

    public GDERuntimeException(String msg, Throwable th) { super(msg, th); }        
    
}


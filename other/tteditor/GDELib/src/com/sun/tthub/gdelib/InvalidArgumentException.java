/*
 * InvalidArgumentException.java
 *
 * Created on September 28, 2005, 2:33 PM
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
public class InvalidArgumentException extends GDERuntimeException {
    
    /** Creates a new instance of InvalidArgumentException */
    public InvalidArgumentException() {}
        
    public InvalidArgumentException(String msg) { super(msg); }

    public InvalidArgumentException(Throwable th) { super(th); }

    public InvalidArgumentException(String msg, Throwable th) { super(msg, th); }   
    
}


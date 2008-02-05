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

package com.sun.portal.admin.common;

    /**
     * A generic PS MBean error.
     */
public class PSMBeanException extends Exception {

    private String errorKey;
    private Object[] tokens = null;
    /**
     * Constructs a new PS MBean exception with the specified error
     * key and <code>null</code> as its detail message.  The cause is
     * not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     */
    public PSMBeanException(String errorKey) {
	super();
        this.errorKey = errorKey;
    }
    /**
     * Constructs a new PS MBean exception with the specified error
     * key and detail message . The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  message  the detail message (which is saved for later
     *                  retrieval by the {@link #getMessage()} method).
     */
    public PSMBeanException(String errorKey, String message) {
	    super(message);
        this.errorKey = errorKey;
    }

    /**
     * Constructs a new PS MBean exception with the specified error
     * key and object array of tokens and <code>null</code> as its detail message.  The cause is
     * not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @param  errorKey the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  tokens  the tokens to be inserted in the localized message (which is saved for later
     *                  retrieval by the {@link #getTokens()} method).
     */
    public PSMBeanException(String errorKey, Object[] tokens ) {
	super();
        this.errorKey = errorKey;
        this.tokens = tokens;
    }

    /**
     * Constructs a new PS MBean exception with the specified error
     * key, cause ,detail message of <tt>((cause == null) ?
     * null : cause.toString())</tt> (which typically contains the
     * class and detail message of <tt>cause</tt>)
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  cause  the cause (which is saved for later retrieval by
     *                the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates
     *                that the cause is nonexistent or unknown.)
     */
    public PSMBeanException(String errorKey, Throwable cause) {
        super(cause);
        this.errorKey = errorKey;
    }
    /**
     * Constructs a new PS MBean exception with the specified error
     * key , detail message and an objecty array of tokens  The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  message  the detail message (which is saved for later
     *                  retrieval by the {@link #getMessage()} method).
     * @param  tokens  the tokens to be inserted in the localized message (which is saved for later
     *                  retrieval by the {@link #getTokens()} method).
     */
    public PSMBeanException(String errorKey, String message, Object[] tokens) {
	    super(message);
        this.errorKey = errorKey;
        this.tokens = tokens ;
    }

     /**
     * Constructs a new PS MBean exception with the specified error
     * key, detail message , cause .  <p>Note that the detail
     * message associated with <code>cause</code> is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  message  the detail message (which is saved for later
     *                  retrieval by the {@link #getMessage()} method).
     * @param  cause  the cause (which is saved for later retrieval by
     *                the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates
     *                that the cause is nonexistent or unknown.)

     */
    public PSMBeanException(String errorKey, String message, Throwable cause) {
        super(message, cause);
        this.errorKey = errorKey;
    }
    /**
     * Constructs a new PS MBean exception with the specified error
     * key, detail message , cause and object array of tokens.  <p>Note that the detail
     * message associated with <code>cause</code> is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  message  the detail message (which is saved for later
     *                  retrieval by the {@link #getMessage()} method).
     * @param  cause  the cause (which is saved for later retrieval by
     *                the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates
     *                that the cause is nonexistent or unknown.)
     * @param  tokens  the tokens to be inserted in the localized message (which is saved for later
     *                  retrieval by the {@link #getTokens()} method).
     */
    public PSMBeanException(String errorKey, String message, Throwable cause, Object[] tokens) {
        super(message, cause);
        this.errorKey = errorKey;
        this.tokens = tokens;
    }


    /**
     * Constructs a new PS MBean exception with the specified error
     * key, cause ,detail message of <tt>((cause == null) ?
     * null : cause.toString())</tt> (which typically contains the
     * class and detail message of <tt>cause</tt>) and  object array of tokens
     *
     * @param  errorKey  the error key (which is saved for later
     *                    retrieval by the {@link #getErrorKey()} method).
     * @param  cause  the cause (which is saved for later retrieval by
     *                the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates
     *                that the cause is nonexistent or unknown.)
     *  @param  tokens  the tokens to be inserted in the localized message (which is saved for later
     *                  retrieval by the {@link #getTokens()} method).
     */
    public PSMBeanException(String errorKey, Throwable cause, Object[] tokens) {
        super(cause);
        this.errorKey = errorKey;
        this.tokens = tokens;
    }

    /**
     * Returns the error key of this PS MBean exception.
     *
     * @return the error key of this PS MBean exception.
     */
    public String getErrorKey() {
        return errorKey;
    }

     /**
     * Returns the object array of tokens which can be later used to insert in
     * localzed messages
     *
     * @return the tokens as an object array.
     */
    public Object[] getTokens(){
        return tokens;
    }


}

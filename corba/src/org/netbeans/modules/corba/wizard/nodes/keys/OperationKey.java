/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.nodes.keys;

/** 
 *
 * @author  root
 * @version 
 */
public class OperationKey extends NamedKey {

    private String ret;
    private String params;
    private String except;
    private String ctx;
    private boolean oneway;
  
    /** Creates new OperationKey */
    public OperationKey (int kind, String name, String ret, String params, String except, String ctx, boolean oneway) {
        super (kind, name);
        this.ret = ret;
        this.params = params;
        this.except = except;
        this.ctx = ctx;
        this.oneway = oneway;
    }
  
    public String getReturnType () {
        return this.ret;
    }
  
    public String getParameters () {
        return this.params;
    }
  
    public String getExceptions () {
        return this.except;
    }
  
    public String getContext () {
        return this.ctx;
    }
  
    public boolean isOneway () {
        return this.oneway;
    }
  
}

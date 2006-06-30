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
    
    public void setReturnType (String ret) {
        this.ret = ret;
    }
  
    public String getParameters () {
        return this.params;
    }
    
    public void setParameters (String params) {
        this.params = params;
    }
  
    public String getExceptions () {
        return this.except;
    }
    
    public void setExceptions (String except) {
        this.except = except;
    }
  
    public String getContext () {
        return this.ctx;
    }
    
    public void setContext (String ctx) {
        this.ctx = ctx;
    }
  
    public boolean isOneway () {
        return this.oneway;
    }
    
    public void setOneway (boolean ow) {
        this.oneway = ow;
    }
  
}

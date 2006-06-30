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

package org.netbeans.modules.corba.browser.ir;

import org.omg.CORBA.*;

import java.util.Vector;

import java.net.*;
import java.io.*;

/*
 * @author Karel Gardas
 */

public class Repository implements Serializable {

    String name;
    transient private org.omg.CORBA.Container repository;
    String url;
    String ior;
    transient boolean failed;

    static final long serialVersionUID=9175839955806475950L;

    public Repository (String _name, org.omg.CORBA.Container repo, String _url, String _ior) {
        name = _name;
        repository = repo;
        url = _url;
        ior = _ior;
        failed = false;
    }

    public Repository (String _name, org.omg.CORBA.Container repo, String _url, String _ior, boolean failed){
        this.name = name;
        this.repository = repo;
        this.url = url;
        this.ior = ior;
        this.failed = failed;
    }

    public String getName () {
        return name;
    }

    public org.omg.CORBA.Container getRepository () {
        return repository;
    }
    
    void setRepository (org.omg.CORBA.Container repository) {
        this.repository = repository;
    }

    public String getURL () {
        return url;
    }

    public String getIOR () {
        return ior;
    }

    public boolean failed(){
        return this.failed;
    }

    public void setFailed (boolean failed){
        this.failed = failed;
    }

}

/*
 * $Log
 * $
 */




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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */


package com.sun.tthub.data;
import java.util.Map;
/**
 *
 * @author choonyin
 */
public abstract class TempPageState implements java.io.Serializable {
    
    protected Map tempFieldsList=null;
    protected String editPath=null;
    /** Creates a new instance of TempPageState */
    public TempPageState(String editPath,Map tempFieldsList) {
        this.editPath=editPath;
        this.tempFieldsList=tempFieldsList;
    }
    public String getEditPath(){
        return null;
    }
    
    public Map getFieldList(){
        return tempFieldsList;
    }
     public String toString(){
        StringBuffer buffer=new StringBuffer();
          buffer.append("[StandardTempPageState]");
          buffer.append("EditPath: [").append(editPath).append("], ");
          buffer.append("TempFieldsList: [").append(tempFieldsList).append("]");
          return buffer.toString();
        
    }
}

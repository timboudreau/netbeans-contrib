
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


import com.sun.tthub.util.CommonUtil;
import com.sun.tthub.util.DataConstants;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;

import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.fields.ComplexEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.UIComponentType;

/**
 * A standard page consists of standard/user-defined controls. So, the
 * TempPageState object for the standard page should hold the state of each
 * control in the page. Once a request from a standard page is encountered, the
 * StandardPageStateHandler will perform the following tasks for each UI control
 * representing a field in the edit path of the TTValueImpl object:
 *
 * - Locate the FieldStateHandler object associated with the control
 * - Extract the value from the UI control using the FieldStateHandler for the
 *  control. The value of the UI control is represented by a 'TempFieldState' object.
 * - Store the TempFieldState object against the field name so that it can be
 *  retrieved any time using the field name.
 *
 * @author choonyin
 */
public class StandardPageStateHandler extends PageStateHandler{
    
    /** Creates a new instance of StandardPageStateHandler */
    public StandardPageStateHandler(ActionRequest request) {
        super(request);
    }
/*
will be associated with a 'PageStateHandler' instance. The PageStateHandler
object interprets the http request (i.e. extracts the values of UI components
from the http request) and creates a 'TempPageState' object which represents the
current state of the page.
 */
    public TempPageState extractTempPageState(String curEditPath)throws IllegalServerException{
        
        System.out.println("[StandardPageStateHandler extractTempPageState Entry]");
        PortletSession session= request.getPortletSession();
        TTValueDisplayInfo displayInfo=CommonUtil.retrieveDisplayInfo(session);
        ///PersonSearch/Responsible
        
        String[] pathList= curEditPath.split("/");
        Map fieldsList = CommonUtil.getDisplayInfoFieldListForEditPath(displayInfo, pathList);
        
        // Create TemporaryFieldValue for each field
        Collection coll = fieldsList.entrySet();
        Map <String,TempFieldState> tempFieldsList = new HashMap<String,TempFieldState>(coll.size());
        
        //Populate tempFieldsList in TempPageState
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            FieldInfo info =
                    (FieldInfo) entry.getValue();
            TempFieldState newFieldState=null;
            
            String fieldName= info.getFieldMetaData().getFieldName();
            UIComponentType uiComponentType= info.getFieldDisplayInfo().getUIComponentType();
            
            if (uiComponentType.equals(uiComponentType.CONTROL_COMPLEX_ENTRY)){
                    //to add control for complex object
                String[] values=new String[1];
                values[0]=DataConstants.COMPLEX_DEFAULT_STATE;
                newFieldState=new DefaultFieldState(fieldName,uiComponentType,values);
                
            }else{
                newFieldState= new NonCmplxFieldStateHandler(request).extractTempFieldState(uiComponentType,fieldName);
            }
            
            tempFieldsList.put(fieldName,newFieldState);
        }
        StandardTempPageState tempPageState=new StandardTempPageState(curEditPath,tempFieldsList);
        System.out.println("[StandardPageStateHandler tempPageState]"+tempPageState);
        //Store TempPageState in session
        HashMap tempPageMap=(HashMap)session.getAttribute(DataConstants.TEMP_PAGE_STATE_MAP);
        if (tempPageMap==null)
            tempPageMap=new HashMap();
        tempPageMap.put(curEditPath,tempPageState);
        session.setAttribute(DataConstants.TEMP_PAGE_STATE_MAP,tempPageMap);
        System.out.println("[StandardPageStateHandler tempPageMap]"+tempPageMap.toString());
        return tempPageState;
        
    }

}


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


package com.sun.tthub.tags;


import java.util.Map;
import java.io.IOException;
import javax.portlet.*;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.*;
import javax.servlet.http.HttpServletRequest;


import com.sun.tthub.util.DataConstants;
import com.sun.tthub.data.ReqProcessingResult;
import com.sun.tthub.data.FieldError;
import com.sun.tthub.util.CommonUtil;

import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;

/**
 *
 * @author  choonyin
 * @version
 */

public class CheckBoxSetTag extends TagSupport {
    
    
    protected String fieldName=null;
    public void setfieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public int doStartTag()
    throws JspException {
        
        System.out.println("[CheckBoxSetTag Entry]");
        System.out.println("[CheckBoxSetTag-fieldName]-"+fieldName);
        FieldError fieldError=null;
        String[] fields= new String[]{};
        Object[] selRange = new Object[]{};
        Object[] defList = new Object[]{};
        try{
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            RenderRequest renderRequest =(RenderRequest)request.getAttribute(TTHubTagConstants.RENDER_REQUEST_ATTRIBUTENAME);
            PortletSession portletSession=renderRequest.getPortletSession();
            
            //Retrieve Fieldvalue from Session
            
            String curEditPath= (String)renderRequest.getParameter("returnEditPath");
            
            // Check for Field Errors. If field Errors present, display field from tempPageState,else from TTValueImplObj
            ReqProcessingResult processingResult= (ReqProcessingResult)portletSession.getAttribute(DataConstants.REQPROCESSINGRESULT);
            
            Map fieldErrors=null;
            if (processingResult!=null)
                fieldErrors= processingResult.getFieldErrors();
            
            
            if (fieldErrors==null || fieldErrors.size()==0) {
                // retrieve fieldValue from TValueImplObj
                fields= TTHubTagUtil.retrieveFieldFromTTValue(fieldName,portletSession, curEditPath);
                
                
            }else{
                // retrieve fieldValue from tempPageState
                fields = TTHubTagUtil.retrieveFieldFromTempPage(fieldName,portletSession, curEditPath);
                
                if (fieldErrors.size()!=0)
                    fieldError=(FieldError)fieldErrors.get(fieldName);
            }
            
            
            // Retrieve Option values
            TTValueDisplayInfo displayInfo=CommonUtil.retrieveDisplayInfo(portletSession);
            
            String[] pathList=new String[]{};
            if (curEditPath!=null)
                pathList=curEditPath.split("/");
            
            Map fieldList= CommonUtil.getDisplayInfoFieldListForEditPath(displayInfo,pathList);
            
            FieldInfo fieldInfo=(FieldInfo)fieldList.get(fieldName);
            
            System.out.println("[CheckBoxSetTag-fieldList]-"+fieldList);
            
            
            
            
            SelectionFieldDisplayInfo fieldDisplayInfo =
                    (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
            selRange = fieldDisplayInfo.getSelectionRange();
            defList = fieldDisplayInfo.getDefaultSelection();
        }catch(Exception e){
            e.printStackTrace();
            fieldError= new FieldError(fieldName,e.getMessage());
        }
        
        //Print check box set
        try{
            StringBuffer buffer=new StringBuffer();
            buffer.append("<table border=\"0\" width=\"100%\">");
            
            for(int i = 0; i < selRange.length; ++i) {
                buffer.append("<tr><td><input type=\"checkbox\" name=\"");
                buffer.append(fieldName);
                buffer.append("\" value=\"");
                buffer.append(selRange[i].toString());
                buffer.append("\"");
                if (fields==null){
                    buffer.append(isInDefValueList(selRange[i], defList) ?
                        "checked />" : "/>");
                }else{
                    buffer.append(isInDefValueList(selRange[i], fields) ?
                        "checked />" : "/>");
                    
                }
                buffer.append(selRange[i].toString());
                buffer.append("</td></tr>");
            }
            // Print Error Message
            if (fieldError!=null){
                buffer.append("<tr><td>Error:"+fieldError.getErrorMessage()+"</td></tr>");
            }
            buffer.append("</table>");
            
            
            pageContext.getOut().print(buffer.toString());
        } catch(Exception e){
            e.printStackTrace();
            throw new JspTagException("Error: IOException while writing");
        }
        
        return SKIP_BODY;
    }
    
    private boolean isInDefValueList(Object obj, Object[] defList) {
        for(int i = 0; i < defList.length; ++i) {
            if(obj.equals(defList[i]))
                return true;
        }
        return false;
    }
    
    /**Called by the container to invoke this tag.
     * The implementation of this method is provided by the tag library developer,
     * and handles all tag processing, body iteration, etc.
     */
    /*
    public void doTag() throws JspException {
     
        JspWriter out=getJspContext().getOut();
     
        try {
            // TODO: insert code to write html before writing the body content.
            // e.g.:
            //
            // out.println("<strong>" + attribute_1 + "</strong>");
            // out.println("    <blockquote>");
     
            JspFragment f=getJspBody();
            if (f != null) f.invoke(out);
     
            // TODO: insert code to write html after writing the body content.
            // e.g.:
            //
            // out.println("    </blockquote>");
     
        } catch (java.io.IOException ex) {
            throw new JspException(ex.getMessage());
        }
     
    }
     */
}

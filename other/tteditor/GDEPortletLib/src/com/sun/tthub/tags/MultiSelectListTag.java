
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder. *
 */


package com.sun.tthub.tags;

import java.util.Map;
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

public class MultiSelectListTag extends TagSupport {
    protected String fieldName=null;
    public void setfieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public int doStartTag()
    throws JspException {
        
        System.out.println("[MultiSelectListTag Entry]");
        System.out.println("[MultiSelectListTag-fieldName]-"+fieldName);
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
            
            System.out.println("[MultiSelectListTag-fieldList]-"+fieldList);
            
            
            SelectionFieldDisplayInfo fieldDisplayInfo =
                    (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
            selRange = fieldDisplayInfo.getSelectionRange();
            defList = fieldDisplayInfo.getDefaultSelection();
            
        }catch(Exception e){
            e.printStackTrace();
            fieldError= new FieldError(fieldName,e.getMessage());
        }
        //Print MultiSelectList Control
        try{
            StringBuffer buffer = new StringBuffer();
            
            buffer.append("<select name=\"");
            buffer.append(fieldName);
            buffer.append("\" size=\"5\" multiple>");
            for(int i = 0; i < selRange.length; ++i) {
                if(selRange[i] == null)
                    continue;
                buffer.append("<option value=\"");
                buffer.append(selRange[i].toString());
                buffer.append("\" ");
                if(fields==null){
                    buffer.append(isInDefValueList(selRange[i], defList) ?
                        "selected >" : ">");
                }else{
                    buffer.append(isInDefValueList(selRange[i], fields) ?
                        "selected >" : ">");
                }
                
                buffer.append(selRange[i].toString());
                buffer.append("</option>");
            }
            buffer.append("</select>");  // close the select tag.
            // Print Error Message
            if (fieldError!=null){
                buffer.append("Error:").append(fieldError.getErrorMessage());
            }
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

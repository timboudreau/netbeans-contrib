
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

package com.sun.tthub.processor;

import com.sun.tthub.util.CommonUtil;
import com.sun.tthub.util.DataConstants;
import com.sun.tthub.util.TThubNamespacePrefixMapperImpl;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;


import java.lang.reflect.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;




import com.sun.tthub.data.*;

//import gde.generated.TroubleTicketValue;
import com.sun.tthub.gdelib.fields.UIComponentType;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
/**
 *
 * @author choonyin
 */

/*
 * get the edit path to be saved. (from request)
 * transfer the field values  to the temporary page state object
      (tmppagestateobjmap - update)
 * apply the null checks, conversions and validations to each field in the
      edit path as mentioned above. (tmppagestateobjmap - access)
 * transfer the contents of the temporary page state object to the
      ttvalue impl object (ttvalueimplobj - update)
 * return the logical string based on the processing result. (success/error)
 **/
public class SaveEditPathReqProcessor {
    
    /** Creates a new instance of SaveEditPathReqProcessor */
    public SaveEditPathReqProcessor() {
    }
    public String processRequest(AppRequest appRequest,ActionRequest request){
        
        System.out.println("[SaveEditPathReqProcessor processRequest Entry]");
        
        String tempdir = System.getProperty("java.io.tmpdir");
        String fileName= tempdir+"/"+appRequest.getUsecaseId()+".xml";
        
        PortletSession session= request.getPortletSession();
        //TroubleTicketValue ttValImplObj=(TroubleTicketValue)session.getAttribute(DataConstants.TTVALUEIMPLOBJECT);
        Object ttValImplObj=(Object)session.getAttribute(DataConstants.TTVALUEIMPLOBJECT);
        
        // identify the current edit path from the request.
        String curEditPath=appRequest.getCurEditPath();
        System.out.println("[SaveEditPathReqProcessor-curEditPath]-"+curEditPath);
        //locate the current edit path in the TTValue Impl object stored in the portlet session
        String[] pathList= null;
        if (curEditPath!=null)
            pathList=curEditPath.split("/");
        
        
        // Initialize a collection for Field Errors and Generic Errors to store
        // field specific and other general errors while processing the edit path.
        HashMap <String,FieldError>fieldErrors = new HashMap<String,FieldError>();
        ArrayList <GlobalError>globalErrors = new ArrayList<GlobalError>();
        boolean clearTTValImplObj=false;
        
        
        try{
            TTValueDisplayInfo displayInfo=CommonUtil.retrieveDisplayInfo(session);
            
            //When the first request is submitted, the ttValueImplObject is not present in the session.
            //Thus, create a new instance.
            if (ttValImplObj ==null){
                ttValImplObj = CommonUtil.createTTValueJAXBObject(displayInfo.getExtTTValueImplClass());
                
            }
            
            
            Class fieldClass = ttValImplObj.getClass();
            Object fieldObj=ttValImplObj;
            Object fieldValue=ttValImplObj;
            System.out.println("[SaveEditPathReqProcessor-pathList length]-"+pathList.length);
            if (pathList.length!=0){
                //Complex Object
                
                
                for(int i=1;i<pathList.length;i++){
                    
                    String fieldName=pathList[i];
                    
                    System.out.println("[SaveEditPathReqProcessor-fieldName -pathList["+i+"]]-"+fieldName);
                    fieldValue=CommonUtil.getFieldValue(fieldName,fieldClass,fieldObj);
                    if (fieldValue==null){
                        
                        //If the specified edit path is null in the TTValueImpl object, create the
                        //required edit paths as mentioned in 'UNAVAILABLE/NULL EDIT PATHS IN
                        //THE SAVE EDIT PATH REQUEST'
                        String fieldType= CommonUtil.getFieldType(fieldName,fieldClass);
                        fieldValue= CommonUtil.createObject(fieldType);
                        Class[] parameterTypes = new Class[] {fieldValue.getClass()};
                        String methodName="set"+ fieldName.substring(0,1).toUpperCase()+ fieldName.substring(1);
                        Method fieldMethod=fieldClass.getMethod(methodName,parameterTypes);
                        Object[] arguments = new Object[] {fieldValue};
                        Object updatedField=fieldMethod.invoke(fieldObj,arguments);
                        
                    }
                    fieldClass= fieldValue.getClass();
                    fieldObj=fieldValue;
                }
            }
            
            //Create the TemporaryPageState object using the TemporaryPageHandler as
            //described in the above section.
            StandardPageStateHandler pageHandler=new StandardPageStateHandler(request);
            StandardTempPageState tempPageState= (StandardTempPageState)pageHandler.extractTempPageState(curEditPath);
            
            
            
            //Process each field in the temporaryPageState variable
            Map tempFieldsList= tempPageState.getFieldList();
            Collection tempValueList=(Collection) tempFieldsList.values();
            for(Iterator it = tempValueList.iterator(); it.hasNext(); ){
                DefaultFieldState tempFieldState= (DefaultFieldState)it.next();
                String[] tempFieldValue=tempFieldState.getFieldValue();
                
                String tempFieldName=tempFieldState.getFieldName();
                // Check if temp Field is null
                if (tempFieldValue==null){
                    
                    FieldError fieldError= new FieldError(tempFieldName,"Field Value cannot be empty");
                    fieldErrors.put(tempFieldName,fieldError);
                    // set default value?
                    continue;
                }
                //check if Displaytype is non-editable complex entry control.
                UIComponentType uiComponentType=tempFieldState.getUIComponentType();
                if (uiComponentType.equals(uiComponentType.CONTROL_COMPLEX_ENTRY)){
                    //skip complex entry field
                    continue;
                }
                
                // execute Field Conversion and update ttvalueImplObj field value.
                Class curFieldClass = fieldObj.getClass();
                //Field childField=curFieldClass.getDeclaredField(tempFieldName);
                //Class childFieldType= childField.getType();
                String childFieldType=CommonUtil.getFieldType(tempFieldName,curFieldClass);
                try{
                    Object childFieldValue= NonComplxFieldConverter.convert(tempFieldValue[0],childFieldType);
                    // Maynot be able to getFieldValue/SetFieldValue
                    
                    //childField.set(fieldObj,childFieldValue);
                    CommonUtil.setFieldValue(tempFieldName,curFieldClass,fieldObj,childFieldValue);
                }catch(ConversionException e){
                    e.printStackTrace();
                    FieldError fieldError= new FieldError(tempFieldName,e.getMessage());
                    fieldErrors.put(tempFieldName,fieldError);
                    continue;
                }
            }
            //Save TTValueImplObj in session.
            session.setAttribute(DataConstants.TTVALUEIMPLOBJECT,ttValImplObj);
            System.out.println("[SaveEditPathReqProcessor-ttValImplObj]-"+ttValImplObj.toString());
            
            // if root edit path, generate xml file for ossj backend
         
            if (pathList.length==0){
                //
                
                // Generated Request
                Object operationObj=CommonUtil.createOperationJAXBObject(CommonUtil.convertFirstLettertoUpperCase(appRequest.getUsecaseId()),ttValImplObj.toString());
                CommonUtil.setFieldValue(ttValImplObj.getClass().getSimpleName(),operationObj.getClass(),operationObj,ttValImplObj);
                
                CommonUtil.saveOutputFile(session, operationObj,fileName);

                clearTTValImplObj=true;
            }

            
        }catch(Exception e){
            e.printStackTrace();
            GlobalError globalError=new GlobalError(e.getMessage());
            globalErrors.add(globalError);
        }
        
        
        //Set returnpath/ status message
        String returnEditPath="";
        String statusMessage="";
        if( globalErrors.size()!=0 || fieldErrors.size()!=0){
            returnEditPath=curEditPath;
            statusMessage=DataConstants.FAILED_MESSAGE+":Unable to save " +curEditPath +" Page.";
            if (fieldErrors.size()!=0)
                System.out.println("[SaveEditPathReqProcessor-fieldErrors]-"+fieldErrors.toString());
        }else{
            returnEditPath=new String();
            if (pathList.length==0){
                returnEditPath=curEditPath;
            }else{
                //go back to parent path
                int index=curEditPath.lastIndexOf("/");
                System.out.println("[SaveEditPathReqProcessor-index]-"+index);
                if (index==0)
                    //parent is Root path
                    returnEditPath="/";
                else 
                    returnEditPath=curEditPath.substring(0,index);
            }
            if (curEditPath.equals("/")){
              statusMessage=DataConstants.SUCCESS_MESSAGE+":The request is saved to "+fileName+".";
                
            }else{
                statusMessage=DataConstants.SUCCESS_MESSAGE+": "+curEditPath+" is saved.";
            }
            
            
            
        }
        //Set Processing Result
        ReqProcessingResult result=new ReqProcessingResult();
        result.setFieldErrors(fieldErrors);
        result.setGlobalErrors(globalErrors);
        result.setStatusMessage(statusMessage);
        result.setClearTTValueFlag(clearTTValImplObj);
        session.setAttribute(DataConstants.REQPROCESSINGRESULT,result);
        
         System.out.println("[SaveEditPathReqProcessor-returnEditPath]-"+returnEditPath);
        
        return returnEditPath;
    }

  
    
    
}

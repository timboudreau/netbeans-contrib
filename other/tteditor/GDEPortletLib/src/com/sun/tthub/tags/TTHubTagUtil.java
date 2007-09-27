
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

import com.sun.tthub.util.CommonUtil;
import com.sun.tthub.util.DataConstants;
import java.util.Map;

import java.lang.reflect.*;
import javax.portlet.*;

import com.sun.tthub.data.*;
//import gde.generated.TroubleTicketValue;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
/**
 *
 * @author choonyin
 */
public class TTHubTagUtil {
    
    /** Creates a new instance of TagUtil */
    public TTHubTagUtil() {
    }
    
    public static String[] retrieveFieldFromTempPage(String fieldName,PortletSession portletSession, String curEditPath) {
        
        String[] field;
        
        Map tempPageStateMap= (Map)portletSession.getAttribute(DataConstants.TEMP_PAGE_STATE_MAP);
        StandardTempPageState tempPageState=(StandardTempPageState)tempPageStateMap.get(curEditPath);
        DefaultFieldState fieldState=(DefaultFieldState)tempPageState.getTempFieldState(fieldName);
        field= fieldState.getFieldValue();
        System.out.println("[TTHubTagUtil.retrieveFieldFromTempPage-field-]"+field);
        return field;
    }
    
    public static String[] retrieveFieldFromTTValue(String fieldName,PortletSession portletSession, String curEditPath )
    throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchMethodException,InvocationTargetException,IllegalServerException {
        
        // retrieve fieldValue from TValueImplObj
        String[] field=new String[1];
        //TroubleTicketValue ttValImplObj=(TroubleTicketValue)portletSession.getAttribute(DataConstants.TTVALUEIMPLOBJECT);
        Object ttValImplObj=(Object)portletSession.getAttribute(DataConstants.TTVALUEIMPLOBJECT);
        
        String[] pathList= null;
        
        if (curEditPath!=null)
            pathList=curEditPath.split("/");
        
        if (ttValImplObj ==null){
            // Retrieve datatype and display defaultValue
            field[0]= retrieveDefaultValue(fieldName,pathList,portletSession);
            
        }else{
            
            
            Class fieldClass = ttValImplObj.getClass();
            Object fieldObj=ttValImplObj;
            Object fieldValue=ttValImplObj;
            
            if (pathList!=null && pathList.length!=0){
                //Complex Object
                for(int i=1;i<pathList.length;i++){
                    
                    String curfieldName=pathList[i];
                    fieldValue=CommonUtil.getFieldValue(curfieldName,fieldClass,fieldObj);
                    
                    
                    if (fieldValue==null){
                        field[0]= retrieveDefaultValue(fieldName,pathList,portletSession);
                        break;
                    }
                    fieldClass= fieldValue.getClass();
                    fieldObj=fieldValue;
                }
            }
            
            if( fieldValue!=null){
                
                // retrieve ttmplvalue
                Class curFieldClass = fieldObj.getClass();
                Object fieldValueObj=CommonUtil.getFieldValue(fieldName,curFieldClass,fieldObj);
                if (fieldValueObj!=null)
                    field[0]=(String)fieldValueObj.toString();
                else
                    field[0]=retrieveDefaultValue(fieldName,pathList,portletSession);
            }
            
        }
        System.out.println("[TTHubTagUtil.retrieveFieldFromTTValue-field[0]"+field[0]);
        return field;
        
    }
    
    public static String retrieveDefaultValue(String fieldName,String[] pathList,PortletSession session)throws IllegalServerException{
        
        System.out.println("[TTHubTagUtil.retrieveDefaultValue]-fieldName-"+fieldName);
        
        TTValueDisplayInfo ttValueDisplayInfo=CommonUtil.retrieveDisplayInfo(session);
        
        Map fieldList= CommonUtil.getDisplayInfoFieldListForEditPath(ttValueDisplayInfo,pathList);
        System.out.println("[TTHubTagUtil.retrieveDefaultValue]-fieldList-"+fieldList);
        
        FieldInfo fieldInfo=(FieldInfo)fieldList.get(fieldName);
        System.out.println("[TTHubTagUtil.retrieveDefaultValue]-fieldInfo-"+fieldInfo);
        String fieldValue=null;
        if (fieldInfo!=null){
            String fieldType=fieldInfo.getFieldMetaData().getFieldDataType();
            fieldValue=getDefaultValueByType(fieldType);
        }
        System.out.println("[TTHubTagUtil.retrieveDefaultValue]-fieldValue-"+fieldValue);
        return fieldValue;
    }
    public static String getDefaultValueByType(String fieldType){
        String defaultValue="";
        if (fieldType.equals(Integer.class.getName())
        || fieldType.equals(Long.class.getName())
        || fieldType.equals(Short.class.getName())
        || fieldType.equals(Byte.class.getName())) {
            defaultValue="0";
        }else if(fieldType.equals(Float.class.getName())
        || fieldType.equals(Double.class.getName())){
            defaultValue="0.0";
        }
        //String/Date-null-blank
        return defaultValue;
    }
}


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

package com.sun.tthub.util;

import com.sun.tthub.data.*;
import java.lang.reflect.*;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import com.sun.tthub.gdelib.logic.*;
import com.sun.tthub.gdelib.fields.*;

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletContext;



import javax.xml.bind.*;
import javax.xml.namespace.QName;

/**
 *
 * @author choonyin
 */
public class CommonUtil {
    
    /** Creates a new instance of ObjectUtil */
    public CommonUtil() {
    }
    
    
    public static Object getFieldValue( String fieldName,Class fieldClass,Object fieldObj)
    throws NoSuchMethodException,IllegalAccessException,InvocationTargetException{
        
        
        
        Class[] parameterTypes = new Class[] {};
        Object[] arguments = new Object[] {};
        Method fieldMethod= fieldClass.getDeclaredMethod("get"+fieldName,parameterTypes);
        Object fieldValue= fieldMethod.invoke(fieldObj,arguments);
        
        System.out.println("[CommonUtil.getFieldValue- fieldName]-"+fieldName);
        System.out.println("[CommonUtil.getFieldValue- fieldClass]-"+fieldClass.getName());
        //Field field= fieldClass.getDeclaredField(fieldName);
        //Object fieldValue= field.get(fieldObj);
        return fieldValue;
    }
    
    public static String getFieldType( String fieldName,Class fieldClass) throws NoSuchMethodException{
        Class[] parameterTypes = new Class[] {};
        Object[] arguments = new Object[] {};
        Method fieldMethod= fieldClass.getDeclaredMethod("get"+fieldName,parameterTypes);
        String fieldType=fieldMethod.getReturnType().getName();
        //Field field= fieldClass.getDeclaredField(fieldName);
        //String fieldType= field.getType().getName();
        return fieldType;
    }
    
    public static void setFieldValue(String fieldName,Class fieldClass,Object fieldObj,Object fieldValue)throws NoSuchMethodException,IllegalAccessException,InvocationTargetException{
        Class[] parameterTypes = new Class[1];
        Object[] arguments = new Object[1];
        parameterTypes[0]=fieldValue.getClass();
        arguments[0]=fieldValue;
        Method fieldMethod= fieldClass.getDeclaredMethod("set"+fieldName,parameterTypes);
        Object newfieldValue= fieldMethod.invoke(fieldObj,arguments);
        
    }
    public static Object createObject(String className)
    throws NoSuchMethodException,InvocationTargetException,InstantiationException,IllegalAccessException,ClassNotFoundException {
        Object object = null;
        
        //Get classname/packagename
        
        int point= className.lastIndexOf(".");
        String cName= className.substring(point+1);
        String cPackage= className.substring(0,point);
        System.out.println("[CommonUtil.createObject- point]-"+point);
        System.out.println("[CommonUtil.createObject- className]-"+className);
        System.out.println("[CommonUtil.createObject- cName]-"+cName);
        System.out.println("[CommonUtil.createObject- cPackage]-"+cPackage);
        
        Class classDefinition = Class.forName(className);
        object = classDefinition.newInstance();
        
        return object;
    }
    
    public static Object createTTValueJAXBObject(String className)
    throws NoSuchMethodException,InvocationTargetException,InstantiationException,IllegalAccessException,ClassNotFoundException {
        
        Object object = null;
        
        int point= className.lastIndexOf(".");
        
        String cName= className.substring(point+1);
        String cPackage= className.substring(0,point);
        
        // check if is class/interface import used default jaxb
        if (cPackage.equalsIgnoreCase("gde.generated")){
            return createJAXBObject(cName,false);
        } else{
            return createJAXBObject("TroubleTicketValue",true);
        }
        
    }
    
    
    public static Object createOperationJAXBObject(String className,String ttValueClassName)
    throws NoSuchMethodException,InvocationTargetException,InstantiationException,IllegalAccessException,ClassNotFoundException {
        
        Object object = null;
        
        int point= ttValueClassName.lastIndexOf(".");
        
        String cPackage= ttValueClassName.substring(0,point);
        
        // check if is class/interface import used default jaxb
        if (cPackage.equalsIgnoreCase("gde.generated")){
            return createJAXBObject(className,false);
        } else{
            return createJAXBObject(className,true);
        }
        
    }
    
    public static Object createJAXBObject(String className, boolean isDefault)
    throws NoSuchMethodException,InvocationTargetException,InstantiationException,IllegalAccessException,ClassNotFoundException {
        Object object = null;
        // Create Jaxb Object factory instance
        System.out.println("[CommonUtil.createJAXBObject- entry]");
        String packageName="gde.generated";
        
        if (isDefault==true){
            packageName="gde.generated.base";
        }
        Class classDefinition = Class.forName(packageName+".ObjectFactory");
        Object objectFactory = classDefinition.newInstance();
        
        // Call Factory create method to create jaxb object instance
        Class[] parameterTypes = new Class[]{};
        Object[] arguments = new Object[]{};
        Method createMethod= classDefinition.getDeclaredMethod("create"+className,parameterTypes);
        Object objectValue= createMethod.invoke(objectFactory,arguments);
        
        return objectValue;
    }
    public static Map getDisplayInfoFieldListForEditPath(TTValueDisplayInfo displayInfo, String[] pathList) {
        
        // Get Field List for current Edit Path;
        
        Map fieldsList=null;
        if (pathList==null || pathList.length==0){
            // EditPath=/
            System.out.println("CommonUtil getDisplayInfoFieldListForEditPath-root path");
            fieldsList=(Map)displayInfo.getExtFieldInfoMap();
        }else{
            FieldInfo fieldInfo=null;
            for(int i=1;i<pathList.length;i++){
                
                System.out.println("CommonUtil getDisplayInfoFieldListForEditPath-Retrieve path "+pathList[i]);
                fieldInfo=getFieldInfo(pathList[i],displayInfo, fieldInfo);
            }
            fieldsList=(Map)((ComplexEntryFieldDisplayInfo)fieldInfo.getFieldDisplayInfo()).getFieldInfoMap();
        }
        return fieldsList;
    }
    
    public static FieldInfo getFieldInfo(String fieldName,TTValueDisplayInfo displayInfo, FieldInfo fieldInfo){
        FieldInfo newInfo=null;
        if (fieldInfo==null){
            newInfo=(TTValueFieldInfo)(displayInfo.getFieldInfo(fieldName));
        }else{
            newInfo= (FieldInfo)((ComplexEntryFieldDisplayInfo)fieldInfo.getFieldDisplayInfo()).getFieldInfoMap().get(fieldName);
        }
        return newInfo;
    }
    
    public static TTValueDisplayInfo retrieveDisplayInfo(PortletSession session)throws IllegalServerException{
        try{
            TTValueDisplayInfo displayInfo = (TTValueDisplayInfo)session.getAttribute(DataConstants.TTVALUEDISPLAYINFO);
            if (displayInfo==null){
                //create new TTValueDisplayInfo object and save in session
                
                PortletContext context= session.getPortletContext();
                
                
                InputStream displayInfoStream= context.getResourceAsStream("/WEB-INF/ttValueDisplayInfo.xml");
                
                java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(
                        new java.io.BufferedInputStream(displayInfoStream));
                
                displayInfo =(TTValueDisplayInfo) decoder.readObject();
                decoder.close();
                session.setAttribute(DataConstants.TTVALUEDISPLAYINFO,displayInfo);
                
            }
            return displayInfo;
        }catch(Exception e){
            e.printStackTrace();
            throw new IllegalServerException("Error retrieving TTValueDisplayInfo.xml");
        }
        
    }
    
    public static String  convertFirstLettertoUpperCase(String word){
        String firstletter=String.valueOf(word.charAt(0));
        String newUpperCaseLetter=firstletter.toUpperCase();
        return word.replaceFirst(firstletter,newUpperCaseLetter);
        
    }
    
    
    public static Object saveOutputFile(PortletSession session, Object operationObj,String fileName) throws IOException,PropertyException, JAXBException,FileNotFoundException {
        
        String operationClassName=operationObj.getClass().getName();
        int point=operationClassName.lastIndexOf(".");
        
        String cPackage= operationClassName.substring(0,point);
        
        JAXBContext jc = JAXBContext.newInstance(cPackage);
        
        Marshaller m = jc.createMarshaller();
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        
        m.setProperty("com.sun.xml.bind.namespacePrefixMapper",new TThubNamespacePrefixMapperImpl());
        
        m.marshal( operationObj, os );
        //m.marshal(new JAXBElement(new QName("http://java.sun.com/products/oss/xml/TroubleTicket",appRequest.getUsecaseId(),"tt"),operationObj.getClass(),operationObj),os);
        
        session.setAttribute(DataConstants.TTVALUEIMPLXML,os);
        //Test outputfile
        java.io.FileOutputStream file=new FileOutputStream(fileName);
        file.write(os.toByteArray());
        
        return null;
    }
    
}

/*
 * GenerateWSDL.java
 *
 * Created on Apr 30, 2007, 9:27:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.wsdlextensions.ldap.ldif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import org.netbeans.modules.wsdlextensions.ldap.impl.ResultSetAttribute;
import org.netbeans.modules.wsdlextensions.ldap.impl.SearchFilterAttribute;
import org.netbeans.modules.wsdlextensions.ldap.impl.UpdateSetAttribute;
import org.openide.util.Exceptions;

/**
 *
 * @author tianlize
 */
public class GenerateXSD {

    private File mDir;
    private Map mSelectedObjectMap;
    private String mFunction;
    private String mFileName;
    private String mBaseDN = "";
    private String mMainAttrInAdd="";
    private String mSearchFilterTypeElements = "";
    private String mResponseTypeElements = "";
    private String mAddAttributesType = "";
//    private GenerateSearchFilter mLDAPSearchFilter;
    public GenerateXSD() {

    }

    public GenerateXSD(File dir, Map selectedObject, String function, String fileName, String baseDN, String mainAttr) {
        mDir = dir;
        mSelectedObjectMap = selectedObject;
        mFunction = function;
        mFileName = fileName;
        mBaseDN = baseDN;
        mMainAttrInAdd=mainAttr;
//        mLDAPSearchFilter=new GenerateSearchFilter(selectedObject);
    }

    private String generateXMLHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }

    private String upInitial(String str) {
        String ret = str.substring(0, 1).toUpperCase();
        ret += str.substring(1);
        return ret;
    }

    private String generateSchemaHead(String objectClassName) {
        String type = upInitial(objectClassName) + mFunction;
        String ret = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + "\n";
        ret += "\t\ttargetNamespace=\"http://xml.netbeans.org/schema/" + type + "\"" + "\n";
        ret += "\t\txmlns:tns=\"http://xml.netbeans.org/schema/" + type + "\"" + "\n";
        ret += "\t\telementFormDefault=\"qualified\"" + "\n";
        ret += "\t\txmlns:ldap=\"http://schemas.sun.com/jbi/wsdl-extensions/ldap/\">" + "\n";
        ret += "\n";
        ret += getTab(1) + "<xsd:import schemaLocation=\"LdapBase.xsd\" namespace=\"http://schemas.sun.com/jbi/wsdl-extensions/ldap/\"/>" + "\n";
        ret += "\n";

        return ret;
    }

    private String generateSchemaTail() {
        return "</xsd:schema>";
    }

    private String getTab(int level) {
        String ret = "";
        for (int i = 0; i < level; i++) {
            ret += "    ";
        }
        return ret;
    }

    private String generateElement(String tag, String type, String defaultvalue, int level) {
        String ret = "";
        ret += getTab(level);
        if (defaultvalue != null) {
            ret += "<xsd:element name=\"" + tag + "\" type=\"" + type + "\" default=\"" + defaultvalue + "\"></xsd:element>" + "\n";
        } else {
            ret += "<xsd:element name=\"" + tag + "\" type=\"" + type + "\"></xsd:element>" + "\n";
        }
        return ret;
    }

    private String generateElement(String tag, String type, String defaultvalue, boolean repeat, int level) {
        String ret = "";
        ret += getTab(level);
        if (!repeat) {
            return generateElement(tag, type, defaultvalue, level);
        } else {
            if (defaultvalue != null) {
                ret += "<xsd:element name=\"" + tag + "\" type=\"" + type + "\" default=\"" + defaultvalue + "\" maxOccurs=\"unbounded\"></xsd:element>" + "\n";
            } else {
                ret += "<xsd:element name=\"" + tag + "\" type=\"" + type + "\" maxOccurs=\"unbounded\"></xsd:element>" + "\n";
            }
        }

        return ret;
    }

    private String generateSearchFilterElements(LdifObjectClass mLdif, int level) {
        String ret = "";
        List selected = mLdif.getSelected();
        Iterator it = selected.iterator();
        String objName = mLdif.getName();
        while (it.hasNext()) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) it.next();
            ret += getTab(level) + "<xsd:element name=\"" + objName + "." + sfa.getAttributeName() + "\" >\n";

            ret += getTab(level + 1) + "<xsd:complexType>" + "\n";
            ret += getTab(level + 2) + "<xsd:sequence>" + "\n";
            ret += getTab(level + 3) + "<xsd:element name=\"value\" type=\"xsd:string\"/>" + "\n";
            ret += getTab(level + 2) + "</xsd:sequence>" + "\n";
            ret += generateAttribute("positionIndex", "optional", "xsd:int", String.valueOf(sfa.getPositionIndex()), level + 2);
            ret += generateAttribute("bracketDepth", "optional", "xsd:int", String.valueOf(sfa.getBracketDepth()), level + 2);
            ret += generateAttribute("bracketBeginDepth", "optional", "xsd:int", String.valueOf(sfa.getBracketBeginDepth()), level + 2);
            ret += generateAttribute("bracketEndDepth", "optional", "xsd:int", String.valueOf(sfa.getBracketEndDepth()), level + 2);
            ret += generateAttribute("logicOp", "optional", "xsd:string", sfa.getLogicOp(), level + 2);
            ret += generateAttribute("compareOp", "optional", "xsd:string", sfa.getCompareOp(), level + 2);
            ret += getTab(level + 1) + "</xsd:complexType>" + "\n";
            ret += getTab(level) + "</xsd:element>" + "\n";

            ret += "\n";
        }
        mSearchFilterTypeElements += ret;
        DocumentBuilder build;

        return ret;
    }

    private String generateAddAttributesTypeEles(LdifObjectClass mLdif, int level) {
        String ret = "";
        List selected = mLdif.getResultSet();
        Iterator it = selected.iterator();
        String objName = mLdif.getName();
        while (it.hasNext()) {
            String attrName=(String)it.next();
            String str=objName + "." + attrName;
            if(!mMainAttrInAdd.equals(str)){
                ret += generateElement(objName + "." + attrName, "xsd:string", null, level);
            }            
        }
        mAddAttributesType += ret;
        return ret;
    }

    private String generateRequestType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"RequestType\">" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += getTab(level + 2) + "<xsd:element name=\"property\" type=\"tns:RequestPropertyType\"></xsd:element>" + "\n";
        if (mFunction.equals("Search")) {
            ret += getTab(level + 2) + "<xsd:element name=\"attributes\" type=\"tns:SearchFilterType\"></xsd:element>" + "\n";
        } else if (mFunction.equals("Update")) {
            ret += getTab(level + 2) + "<xsd:element name=\"attributes\" type=\"tns:UpdateType\"></xsd:element>" + "\n";
        } else if (mFunction.equals("Add")) {
            ret += getTab(level + 2) + "<xsd:element name=\"attributes\" type=\"tns:AddType\"></xsd:element>" + "\n";
        } else if (mFunction.equals("Delete")) {
            ret += getTab(level + 2) + "<xsd:element name=\"attributes\" type=\"tns:SearchFilterType\"></xsd:element>" + "\n";
        } 
        ret += getTab(level + 2) + "<xsd:element name=\"connection\" type=\"ldap:ConnectionType\"></xsd:element>" + "\n";
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";
        return ret;
    }

    private String generateRequestPropertyType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"RequestPropertyType\">" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += generateElement("requestId", "xsd:string", null, level + 2);
        ret += generateElement("dn", "xsd:string", mBaseDN, level + 2);
        ret += generateElement("scope", "xsd:int", "2", level + 2);
        ret += generateElement("size", "xsd:int", "0", level + 2);
        ret += generateElement("timeout", "xsd:int", "0", level + 2);
        ret += generateElement("deref", "xsd:boolean", "true", level + 2);
        ret += generateElement("referral", "xsd:string", "follow", level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";
        return ret;
    }

    private String generateSearchFilterType(LdifObjectClass mLdif, int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"SearchFilterType\" >" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += generateSearchFilterElements(mLdif, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";

        return ret;
    }

    private String generateMainSearchFilterType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"SearchFilterType\" >" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += mSearchFilterTypeElements;
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";
        return ret;
    }

    private String generateResponseType(int level) {
        String ret = "";

        ret += getTab(level) + "<xsd:complexType name=\"ResponseType\" >" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += getTab(level + 2) + "<xsd:element name=\"property\" type=\"tns:ResponsePropertyType\"/>" + "\n";
        if (mFunction.equals("Search")) {
            ret += getTab(level + 2) + "<xsd:element name=\"ResponseElements\" maxOccurs=\"unbounded\" type=\"tns:ResponseAttributeType\"/>" + "\n";
        } else {
            ret += getTab(level + 2) + "<xsd:element name=\"OperationResult\" maxOccurs=\"unbounded\" type=\"xsd:string\"/>" + "\n";
        }
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";
        return ret;
    }

    private String generateResponseAttributeType(LdifObjectClass mLdif, int level) {
        String ret = "";

        ret += getTab(level) + "<xsd:complexType name=\"ResponseAttributeType\" >" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += generateResponseElement(mLdif, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";

        return ret;
    }

    private String generateMainResponseAttributeType(int level) {
        String ret = "";

        ret += getTab(level) + "<xsd:complexType name=\"ResponseAttributeType\" >" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += mResponseTypeElements;
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";

        ret += "\n";
        return ret;
    }

    private String generateMainUpdateElementsType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"UpdateElementsType\" >" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += mResponseTypeElements;
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        ret += "\n";
        return ret;
    }

    private String generateResponseElement(LdifObjectClass mLdif, int level) {
        String ret = "";

        List selected = mLdif.getResultSet();
        Iterator it = selected.iterator();
        while (it.hasNext()) {
            ResultSetAttribute sra = (ResultSetAttribute) it.next();
            ret += generateElement(sra.getObjName() + "." + sra.getAttributeName(), "xsd:string", null, true, level);
            sra = null;
        }
        selected = null;
        it = null;
        mResponseTypeElements += ret;
        return ret;
    }

    private String generateUpdateTypeAddEle(UpdateSetAttribute usa, int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:element name=\"" + usa.getObjName() + "." + usa.getAttrName() + "\">\n";
        ret += getTab(level + 1) + "<xsd:complexType>\n";
        ret += getTab(level + 2) + "<xsd:sequence>\n";
        ret += generateElement("AddValue", "xsd:string", null, level + 3);
        ret += getTab(level + 2) + "</xsd:sequence>\n";
        ret += generateAttribute("opType", "optional", "xsd:string", "Add", level + 2);
        ret += getTab(level + 1) + "</xsd:complexType>\n";
        ret += getTab(level) + "</xsd:element>\n";
        return ret;
    }

    private String generateUpdateTypeReplaceEle(UpdateSetAttribute usa, int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:element name=\"" + usa.getObjName() + "." + usa.getAttrName() + "\">\n";
        ret += getTab(level + 1) + "<xsd:complexType>\n";
        ret += getTab(level + 2) + "<xsd:sequence>\n";
//        ret+=generateElement("primaryValue", "xsd:string", null, level+3);
        ret += generateElement("newValue", "xsd:string", null, level + 3);
        ret += getTab(level + 2) + "</xsd:sequence>\n";
        ret += generateAttribute("opType", "optional", "xsd:string", "Replace", level + 2);
        ret += getTab(level + 1) + "</xsd:complexType>\n";
        ret += getTab(level) + "</xsd:element>\n";
        return ret;
    }

    private String generateUpdateTypeRemoveEle(UpdateSetAttribute usa, int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:element name=\"" + usa.getObjName() + "." + usa.getAttrName() + "\">\n";
        ret += getTab(level + 1) + "<xsd:complexType>\n";
        ret += getTab(level + 2) + "<xsd:sequence>\n";
        ret += generateElement("removeValue", "xsd:string", null, level + 3);
        ret += getTab(level + 2) + "</xsd:sequence>\n";
        ret += generateAttribute("opType", "optional", "xsd:string", "Remove", level + 2);
        ret += getTab(level + 1) + "</xsd:complexType>\n";
        ret += getTab(level) + "</xsd:element>\n";
        return ret;
    }

    private String generateUpdateTypeRemoveAllEle(UpdateSetAttribute usa, int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:element name=\"" + usa.getObjName() + "." + usa.getAttrName() + "\">\n";
        ret += getTab(level + 1) + "<xsd:complexType>\n";
        ret += generateAttribute("opType", "optional", "xsd:string", "RemoveAll", level + 2);
        ret += getTab(level + 1) + "</xsd:complexType>\n";
        ret += getTab(level) + "</xsd:element>\n";
        return ret;
    }

    private String generateUpdateTypeElements(LdifObjectClass mLdif, int level) {
        String ret = "";

        List selected = mLdif.getResultSet();
        Iterator it = selected.iterator();
        while (it.hasNext()) {
            UpdateSetAttribute usa = (UpdateSetAttribute) it.next();
            String opType = usa.getOpType();
            if ("Add".equals(opType)) {
                ret += generateUpdateTypeAddEle(usa, level);
            } else if ("Replace".equals(opType)) {
                ret += generateUpdateTypeReplaceEle(usa, level);
            } else if ("Remove".equals(opType)) {
                ret += generateUpdateTypeRemoveEle(usa, level);
            } else {
                ret += generateUpdateTypeRemoveAllEle(usa, level);
            }
            usa = null;
        }
        selected = null;
        it = null;
        mResponseTypeElements += ret;
        return ret;
    }

    private String generateGlobalElements(int level) {
        String ret = "";
        ret += generateElement("Request", "tns:RequestType", null, level);
        ret += generateElement("Response", "tns:ResponseType", null, level);
        ret += generateElement("Fault", "ldap:FaultType", null, level);
        return ret;
    }
    
    private String generateResponsePropertyType(int level){
        String ret = "";
        ret+=getTab(level)+"<xsd:complexType name=\"ResponsePropertyType\" >\n";
        ret+=getTab(level+1)+"<xsd:sequence>\n";
        ret+=generateElement("code", "xsd:string", null, level+2);
        ret+=generateElement("requestId", "xsd:string", null, level+2);
        ret+=getTab(level+1)+"</xsd:sequence>\n";
        ret+=getTab(level)+"</xsd:complexType>\n\n";    
        return ret;
    }

    private String generateAttribute(String tag, String use, String type, String fixed, int level) {
        String ret = "";
        ret += getTab(level);
        if (fixed != null) {
            ret += "<xsd:attribute name=\"" + tag + "\" use=\"" + use + "\" type=\"" + type + "\" fixed=\"" + fixed + "\" ></xsd:attribute>" + "\n";
        } else {
            ret += "<xsd:attribute name=\"" + tag + "\" use=\"" + use + "\" type=\"" + type + "\" ></xsd:attribute>" + "\n";
        }
        return ret;
    }

    private String generateUpdateType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"UpdateType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        ret += generateElement("SearchFilter", "tns:SearchFilterType", null, level + 2);
        ret += generateElement("UpdateElements", "tns:UpdateElementsType", null, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }

    private String generateAddType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"AddType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        ret += generateElement("Attributes", "tns:AttributesType", null, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }

    private String generateAddTypeMain(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"AddType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        ret += generateElement("MainAttribute", "tns:MainAttributeType", null, level + 2);
        ret += generateElement("Attributes", "tns:AttributesType", null, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }

    private String generateAddMainAttributeType(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"MainAttributeType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        if(mMainAttrInAdd!=null & (!mMainAttrInAdd.equals(""))){
            ret += generateElement(mMainAttrInAdd, "xsd:string", null, level + 2);
        }        
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }

    private String generateAddAttributesType(LdifObjectClass mLdif,int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"AttributesType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        ret += generateAddAttributesTypeEles(mLdif,level+2);
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }
    
    private String generateAddAttributesTypeMain(int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"AttributesType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        ret += mAddAttributesType;
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }

    private String generateUpdateElementsType(LdifObjectClass mLdif, int level) {
        String ret = "";
        ret += getTab(level) + "<xsd:complexType name=\"UpdateElementsType\">\n";
        ret += getTab(level + 1) + "<xsd:sequence>\n";
        ret += generateUpdateTypeElements(mLdif, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>\n";
        ret += getTab(level) + "</xsd:complexType>\n";
        return ret;
    }

    private String generateSearchSchema(LdifObjectClass mLdif) {

        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mLdif.getName());
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateSearchFilterType(mLdif, 1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateResponseAttributeType(mLdif, 1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        return ret;
    }

    private String generateUpdateSchema(LdifObjectClass mLdif) {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mLdif.getName());
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateUpdateType(1);
        ret += this.generateSearchFilterType(mLdif, 1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateUpdateElementsType(mLdif, 1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        return ret;
    }

    private String generateAddSchema(LdifObjectClass mLdif) {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mLdif.getName());
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateAddType(1);
        ret += this.generateAddAttributesType(mLdif, 1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        return ret;
    }

    private String generateAddMainSchema() {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mFileName);
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateAddTypeMain(1);
        ret += this.generateAddMainAttributeType(1);
        ret += this.generateAddAttributesTypeMain(1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        return ret;
    }
    
    private String generateDeleteSchema(LdifObjectClass mLdif) {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mLdif.getName());
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateSearchFilterType(mLdif, 1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail(); 
        return ret;
    }
    
    private String generateDeleteMainSchema() {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mFileName);
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateMainSearchFilterType(1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        return ret;
    }

    private String generateSchema(LdifObjectClass mLdif) {
        if (mFunction.equals("Search")) {
            return generateSearchSchema(mLdif);
        } else if (mFunction.equals("Add")) {
            return generateAddSchema(mLdif);
        } else if (mFunction.equals("Update")){
            return generateUpdateSchema(mLdif);
        } else {
            return generateDeleteSchema(mLdif);
        }
    }

    private String generateSearchMainSchema() {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mFileName);
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateMainSearchFilterType(1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateMainResponseAttributeType(1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();

        return ret;
    }

    private String generateUpdateMainSchema() {
        String ret = "";
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead(mFileName);
        ret += this.generateRequestType(1);
        ret += this.generateRequestPropertyType(1);
        ret += this.generateUpdateType(1);
        ret += this.generateMainSearchFilterType(1);
        ret += this.generateResponseType(1);
        ret += this.generateResponsePropertyType(1);
        ret += this.generateMainUpdateElementsType(1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        return ret;
    }

    private String generateMainSchema() {
        if (mFunction.equals("Search")) {
            return generateSearchMainSchema();
        } else if (mFunction.equals("Add")) {
            return generateAddMainSchema();
        } else if (mFunction.equals("Update")){ 
            return generateUpdateMainSchema();
        } else {
            return generateDeleteMainSchema();
        }

    }

    public void generate() throws IOException {
        if (mSelectedObjectMap != null & mSelectedObjectMap.size() > 0) {
            Iterator it = mSelectedObjectMap.values().iterator();
            while (it.hasNext()) {
                LdifObjectClass loc = (LdifObjectClass) it.next();
                File outputFile = new File(mDir.getAbsolutePath() + File.separator + upInitial(loc.getName()) + mFunction + ".xsd");
                FileOutputStream fos = new FileOutputStream(outputFile);
                String schema = generateSchema(loc);
                fos.write(schema.getBytes());
                fos.close();
                loc = null;
                schema = null;
                fos = null;
            }
            it = null;
            File outputFile = new File(mDir.getAbsolutePath() + File.separator + upInitial(mFileName) + mFunction + ".xsd");
            FileOutputStream mainFos = new FileOutputStream(outputFile);
            String mainSchema = generateMainSchema();
            mainFos.write(mainSchema.getBytes());
            mainFos.close();
            mainSchema = null;
            mainFos = null;
            try {
                copyBaseSchema();
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void copyBaseSchema() throws ClassNotFoundException, FileNotFoundException, IOException {
        Class cls = this.getClass();
        InputStream is = cls.getResourceAsStream("/org/netbeans/modules/wsdlextensions/ldap/resources/LdapBase.xsd");
        File output = new File(mDir, "LdapBase.xsd");
        FileOutputStream fos = new FileOutputStream(output);
        byte[] buf = new byte[is.available()];
        is.read(buf);
        fos.write(buf);
        fos.close();
    }
}

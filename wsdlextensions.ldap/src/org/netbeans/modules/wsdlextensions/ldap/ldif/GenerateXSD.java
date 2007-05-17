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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Gary
 */
public class GenerateXSD {
    private File mDir;
    private LdifObjectClass mLdif;
    private String mFunction;
    
    public GenerateXSD(File dir, LdifObjectClass ldif, String func) {
        mDir = dir;
        mLdif = ldif;
        mFunction = func;
    }
    
    private String generateXMLHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }
    
    private String upInitial(String str) {
        String ret = str.substring(0, 1).toUpperCase();
        ret += str.substring(1);
        return ret;
    }
    
    private String generateSchemaHead() {
        String type = upInitial(mLdif.getName()) + mFunction;
        String ret = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + "\n";
        ret += "\t\ttargetNamespace=\"http://xml.netbeans.org/schema/" + type + "\"" + "\n";
        ret += "\t\txmlns:tns=\"http://xml.netbeans.org/schema/" + type + "\"" + "\n";
        ret += "\t\telementFormDefault=\"qualified\">" + "\n";
        
        return ret;
    }
    
    private String generateSchemaTail() {
        return "</xsd:schema>";
    }
    
    private String generateElement(String tag, String type, String min, int level) {
        String ret = "";
        
        ret += getTab(level);
        if (min != null) {
            ret += "<xsd:element name=\"" + tag + "\" type=\"" + type + "\" minOccurs=\"" + min + "\"></xsd:element>" + "\n";
        } else {
            ret += "<xsd:element name=\"" + tag + "\" type=\"" + type + "\"></xsd:element>" + "\n";
        }
        return ret;
    }
    
    private String generateAttrs(int level) {
        String ret = "";
        
        ret += getTab(level) + "<xsd:element name=\"attrs\" maxOccurs=\"1\">" + "\n";
        ret += getTab(level + 1) + "<xsd:complexType xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "\n";
        ret += getTab(level + 2) + "<xsd:sequence>" + "\n";
        List selected = mLdif.getSelected();
        if (selected != null && selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                String item = (String) selected.get(i);
                if (item.startsWith("* ")) {
                    item = item.substring(2);
                }
                ret += generateElement(item, "tns:SearchFilterType", "0", level + 3);
            }
        } else {
            List mays = mLdif.getMay();
            if (mays != null) {
                for (int i = 0; i < mays.size(); i++) {
                    ret += generateElement((String) mays.get(i), "tns:SearchFilterType", "0", level + 3);
                }
            }
            List musts = mLdif.getMust();
            if (musts != null) {
                for (int i = 0; i < musts.size(); i++) {
                    ret += generateElement((String) musts.get(i), "tns:SearchFilterType", "0", level + 3);
                }
            }
        }
        ret += getTab(level + 2) + "</xsd:sequence>" + "\n";
        ret += getTab(level + 1) + "</xsd:complexType>" + "\n";
        ret += getTab(level) + "</xsd:element>" + "\n";
        
        return ret;
    }
    
    private String generateEntries(int level) {
        String ret = "";
        
        ret += getTab(level) + "<xsd:element name=\"entries\" maxOccurs=\"unbounded\">" + "\n";
        ret += getTab(level + 1) + "<xsd:complexType xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + "\n";
        ret += getTab(level + 2) + "<xsd:sequence>" + "\n";
        List selected = mLdif.getResultSet();
        if (selected != null && selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                String item = (String) selected.get(i);
                if (item.startsWith("* ")) {
                    item = item.substring(2);
                }
                ret += generateElement(item, "xsd:string", "0", level + 3);
            }
        } else {
            List mays = mLdif.getMay();
            if (mays != null) {
                for (int i = 0; i < mays.size(); i++) {
                    ret += generateElement((String) mays.get(i), "xsd:string", "0", level + 3);
                }
            }
            List musts = mLdif.getMust();
            if (musts != null) {
                for (int i = 0; i < musts.size(); i++) {
                    ret += generateElement((String) musts.get(i), "xsd:string", "0", level + 3);
                }
            }
        }
        ret += getTab(level + 2) + "</xsd:sequence>" + "\n";
        ret += getTab(level + 1) + "</xsd:complexType>" + "\n";
        ret += getTab(level) + "</xsd:element>" + "\n";
        
        return ret;
    }
    
    private String generateRequestComplexType(int level) {
        String ret = "";
        
        ret += getTab(level) + "<xsd:complexType name=\"" + upInitial(mLdif.getName()) + mFunction + "RequestType\">" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += generateElement("dn", "xsd:string", null, level + 2);
        ret += generateElement("scope", "xsd:string", null, level + 2);
        ret += generateElement("size", "xsd:string", null, level + 2);
        ret += generateElement("sizeLimit", "xsd:string", null, level + 2);
        ret += generateElement("timeout", "xsd:string", null, level + 2);
        ret += generateElement("requestId", "xsd:string", null, level + 2);
        ret += generateAttrs(level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        return ret;
    }
    
    private String generateFilter(int level) {
        String ret = "";
        
        ret += getTab(level) + "<xsd:complexType name=\"SearchFilterType\">" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>" + "\n";
        ret += generateElement("value", "xsd:string", null, level + 2);
        ret += generateElement("op", "xsd:string", null, level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        
        return ret;
    }
    
    private String generateResponseComplexType(int level) {
        String ret = "";
        
        ret += getTab(level) + "<xsd:complexType name=\"" + upInitial(mLdif.getName()) + mFunction + "ResponseType\">" + "\n";
        ret += getTab(level + 1) + "<xsd:sequence>";
        ret += generateElement("code", "xsd:string", null, level + 2);
        ret += generateElement("requestId", "xsd:string", null, level + 2);
        ret += generateEntries(level + 2);
        ret += getTab(level + 1) + "</xsd:sequence>" + "\n";
        ret += getTab(level) + "</xsd:complexType>" + "\n";
        
        return ret;
    }
    
    private String generateGlobalElements(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        
        ret += generateElement(tag + "Request", "tns:" + tag + "RequestType", null, level);
        ret += generateElement(tag + "Response", "tns:" + tag + "ResponseType", null, level);
        
        return ret;
    }
    
    private String getTab(int level) {
        String ret = "";
        for (int i = 0; i < level; i++) {
            ret += "    ";
        }
        
        return ret;
    }
    
    private String generateSchema() {
        String ret = "";
        
        ret += this.generateXMLHead();
        ret += this.generateSchemaHead();
        ret += this.generateRequestComplexType(1);
        ret += this.generateFilter(1);
        ret += this.generateResponseComplexType(1);
        ret += this.generateGlobalElements(1);
        ret += this.generateSchemaTail();
        
        return ret;
    }
    
    public void generate() throws IOException {
        File outputFile = new File(mDir.getAbsolutePath() + File.separator + upInitial(mLdif.getName()) + mFunction + ".xsd");
        FileOutputStream fos = new FileOutputStream(outputFile);
        String schema = generateSchema();
        fos.write(schema.getBytes());
        fos.close();
    }
    
    public static void main(String[] args) throws Exception {
        File testFile = new File("C:\\DEV\\Sun\\MPS\\slapd-zaz001\\config\\schema\\00core.ldif");
        LdifParser parser = new LdifParser(testFile);
        List list = parser.parse();
        
        for (int i = 0; i < list.size(); i++) {
            LdifObjectClass objClass = (LdifObjectClass) list.get(i);
            File outputDir = new File("c:\\temp\\TestXsd");
            GenerateXSD gen = new GenerateXSD(outputDir, objClass, "Search");
            gen.generate();
        }
    }
}

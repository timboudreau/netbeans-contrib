/*
 * GenerateWSDL.java
 *
 * Created on Apr 30, 2007, 10:40:28 PM
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
public class GenerateWSDL {
    private File mDir;
    private LdifObjectClass mLdif;
    private String mFunction;
    
    public GenerateWSDL(File dir, LdifObjectClass ldif, String func) {
        mDir = dir;
        mLdif = ldif;
        mFunction = func;
    }
    
    private String generateXMLHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }
    
    private String getTab(int level) {
        String ret = "";
        for (int i = 0; i < level; i++) {
            ret += "    ";
        }
        
        return ret;
    }

    private String upInitial(String str) {
        String ret = str.substring(0, 1).toUpperCase();
        ret += str.substring(1);
        return ret;
    }

    private String generateTypes(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        ret += getTab(level) + "<types>" + "\n";
        ret += getTab(level + 1) + "<xsd:schema targetNamespace=\"http://j2ee.netbeans.org/wsdl/" + tag + "\">" + "\n";
        ret += getTab(level + 2) + "<xsd:import namespace=\"http://xml.netbeans.org/schema/" + tag + "\" schemaLocation=\"" + tag + ".xsd\"/>" + "\n";
        ret += getTab(level + 1) + "</xsd:schema>" + "\n";
        ret += getTab(level) + "</types>" + "\n";
        
        return ret;
    }

    private String generateMessages(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        
        ret += getTab(level) + "<message name=\"" + tag + "OperationRequest\">" + "\n";
        ret += getTab(level + 1) + "<part name=\"request\" element=\"ns:" + tag + "Request\"/>" + "\n";
        ret += getTab(level) + "</message>" + "\n";
        ret += getTab(level) + "<message name=\"" + tag + "OperationReply\">" + "\n";
        ret += getTab(level + 1) + "<part name=\"reply\" element=\"ns:" + tag + "Response\"/>" + "\n";
        ret += getTab(level) + "</message>" + "\n";
        
        return ret;
    }
    
    private String generatePortType(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        
        ret += getTab(level) + "<portType name=\"" + tag + "PortType\">" + "\n";
        ret += getTab(level + 1) + "<wsdl:operation name=\"" + tag + "Operation\">" + "\n";
        ret += getTab(level + 2) + "<wsdl:input name=\"request\" message=\"tns:" + tag + "OperationRequest\"/>" + "\n";
        ret += getTab(level + 2) + "<wsdl:output name=\"reply\" message=\"tns:" + tag + "OperationReply\"/>" + "\n";
        ret += getTab(level + 1) + "</wsdl:operation>" + "\n";
        ret += getTab(level) + "</portType>" + "\n";
        
        return ret;
    }
    
    private String generateBindings(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        
        ret += getTab(level) + "<binding name=\"" + tag + "Binding\" type=\"tns:" + tag + "PortType\">" + "\n";
        ret += getTab(level + 1) + "<ldap:binding/>" + "\n";
        ret += getTab(level + 1) + "<wsdl:operation name=\"" + tag + "Operation\">" + "\n";
        ret += getTab(level + 2) + "<ldap:operation type=\"searchRequest\"/>" + "\n";
        ret += getTab(level + 2) + "<wsdl:input name=\"request\">" + "\n";
        ret += getTab(level + 3) + "<ldap:input/>" + "\n";
        ret += getTab(level + 2) + "</wsdl:input>" + "\n";
        ret += getTab(level + 2) + "<wsdl:output name=\"reply\">" + "\n";
        ret += getTab(level + 3) + "<ldap:output returnPartName=\"reply\" attributes=\"\"/>" + "\n";
        ret += getTab(level + 2) + "</wsdl:output>" + "\n";
        ret += getTab(level + 1) + "</wsdl:operation>" + "\n";
        ret += getTab(level) + "</binding>" + "\n";
        
        return ret;
    }
    
    private String generatePLink(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        ret += getTab(level) + "<plnk:partnerLinkType name=\"" + tag + "PartnerLink\">" + "\n";
        ret += getTab(level + 1) + "<plnk:role name=\"" + tag + "PortTypeRole\" portType=\"tns:" + tag + "PortType\"/>" + "\n";
        ret += getTab(level) + "</plnk:partnerLinkType>" + "\n";
        
        return ret;
    }

    private String generateService(int level) {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;
        
        ret += getTab(level) + "<service name=\"" + tag + "Service\">" + "\n";
        ret += getTab(level + 1) + "<wsdl:port name=\"port1\" binding=\"tns:" + tag + "Binding\">" + "\n";
        ret += getTab(level + 2) + "<ldap:address location=\""+mLdif.getLdapUrl()+"\""+"/>" + "\n";
        ret += getTab(level + 1) + "</wsdl:port>" + "\n";
        ret += getTab(level) + "</service>" + "\n";

        return ret;
    }
    
    private String generateDefinition() {
        String ret = "";
        String tag = upInitial(mLdif.getName()) + mFunction;

        ret += "<definitions name=\"" + tag + "\" targetNamespace=\"http://j2ee.netbeans.org/wsdl/" + tag + "\"" + "\n";
        ret += getTab(1) + "xmlns=\"http://schemas.xmlsoap.org/wsdl/\"" + "\n";
        ret += getTab(1) + "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"" + "\n";
        ret += getTab(1) + "xmlns:ldap=\"http://schemas.sun.com/jbi/wsdl-extensions/ldap/\"" + "\n";
        ret += getTab(1) + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + "\n";
        ret += getTab(1) + "xmlns:tns=\"http://j2ee.netbeans.org/wsdl/" + tag + "\"" + "\n";
        ret += getTab(1) + "xmlns:ns=\"http://xml.netbeans.org/schema/" + tag + "\"" + "\n";
        ret += getTab(1) + "xmlns:plnk=\"http://docs.oasis-open.org/wsbpel/2.0/plnktype\">" + "\n";

        ret += generateTypes(1);
        ret += generateMessages(1);
        ret += generatePortType(1);
        ret += generateBindings(1);
        ret += generateService(1);
        ret += generatePLink(1);
        
        ret += "</definitions>" + "\n";
        return ret;
    }
    
    public void generate() throws IOException {
        File outputFile = new File(mDir.getAbsolutePath() + File.separator + upInitial(mLdif.getName()) + mFunction + ".wsdl");
        FileOutputStream fos = new FileOutputStream(outputFile);
        String def = generateDefinition();
        fos.write(def.getBytes());
        fos.close();
    }
    
    public static void main(String[] args) throws Exception {
        File testFile = new File("C:\\DEV\\Sun\\MPS\\slapd-zaz001\\config\\schema\\00core.ldif");
        LdifParser parser = new LdifParser(testFile);
        List list = parser.parse();
        
        for (int i = 0; i < list.size(); i++) {
            LdifObjectClass objClass = (LdifObjectClass) list.get(i);
            File outputDir = new File("c:\\temp\\TestXsd");
            GenerateWSDL gen = new GenerateWSDL(outputDir, objClass, "Search");
            gen.generate();
        }
    }
}

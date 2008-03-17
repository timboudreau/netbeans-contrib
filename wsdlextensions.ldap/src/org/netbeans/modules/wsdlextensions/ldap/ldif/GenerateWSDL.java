package org.netbeans.modules.wsdlextensions.ldap.ldif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.netbeans.modules.wsdlextensions.ldap.utils.LdapConnection;

/**
 *
 * @author tianlize
 */
public class GenerateWSDL {

    private File mDir;
    private Map mSelectedObjectMap;
    private String mFunction;
    private String mFileName;
    private LdapConnection conn;

    public GenerateWSDL(File dir, Map objectClasses, String func, String fileName, LdapConnection conn) {
        mDir = dir;
        mSelectedObjectMap = objectClasses;
        mFunction = func;
        mFileName = fileName;
        this.conn = conn;
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
    
    private String lowInitial(String str) {
        String ret = str.substring(0, 1).toLowerCase();
        ret += str.substring(1);
        return ret;
    }

    private String generateTypes(String tag, int level) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;
        ret += getTab(level) + "<types>" + "\n";
        ret += getTab(level + 1) + "<xsd:schema targetNamespace=\"http://j2ee.netbeans.org/wsdl/" + tag + "\">" + "\n";
        ret += getTab(level + 2) + "<xsd:import namespace=\"http://xml.netbeans.org/schema/" + tag + "\" schemaLocation=\"" + tag + ".xsd\"/>" + "\n";
        ret += getTab(level + 1) + "</xsd:schema>" + "\n";
        ret += getTab(level) + "</types>" + "\n";

        return ret;
    }

    private String generateMessages(String tag, int level) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;

        ret += getTab(level) + "<message name=\"" + tag + "OperationRequest\">" + "\n";
        ret += getTab(level + 1) + "<part name=\"request\" element=\"ns:Request\"/>" + "\n";
        ret += getTab(level) + "</message>" + "\n";
        ret += getTab(level) + "<message name=\"" + tag + "OperationResponse\">" + "\n";
        ret += getTab(level + 1) + "<part name=\"response\" element=\"ns:Response\"/>" + "\n";
        ret += getTab(level) + "</message>" + "\n";
        ret += getTab(level) + "<message name=\"" + tag + "OperationFault\">" + "\n";
        ret += getTab(level + 1) + "<part name=\"fault\" element=\"ns:Fault\"/>" + "\n";
        ret += getTab(level) + "</message>" + "\n";

        return ret;
    }

    private String generatePortType(String tag, int level) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;

        ret += getTab(level) + "<portType name=\"" + tag + "PortType\">" + "\n";
        ret += getTab(level + 1) + "<wsdl:operation name=\"" + tag + "Operation\">" + "\n";
        ret += getTab(level + 2) + "<wsdl:input name=\"request\" message=\"tns:" + tag + "OperationRequest\"/>" + "\n";
        ret += getTab(level + 2) + "<wsdl:output name=\"response\" message=\"tns:" + tag + "OperationResponse\"/>" + "\n";
        ret += getTab(level + 2) + "<wsdl:fault name=\"fault\" message=\"tns:" + tag + "OperationFault\"/>" + "\n";
        ret += getTab(level + 1) + "</wsdl:operation>" + "\n";
        ret += getTab(level) + "</portType>" + "\n";

        return ret;
    }

    private String generateBindings(String tag, int level) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;

        ret += getTab(level) + "<binding name=\"" + tag + "Binding\" type=\"tns:" + tag + "PortType\">" + "\n";
        ret += getTab(level + 1) + "<ldap:binding/>" + "\n";
        ret += getTab(level + 1) + "<wsdl:operation name=\"" + tag + "Operation\">" + "\n";
        if("Add".equals(mFunction)){
           ret += getTab(level + 2) + "<ldap:operation type=\"insertRequest\"/>" + "\n";
        }else{
           ret += getTab(level + 2) + "<ldap:operation type=\""+lowInitial(mFunction)+"Request\"/>" + "\n";   
        }             
        ret += getTab(level + 2) + "<wsdl:input name=\"request\"/>" + "\n";
        ret += getTab(level + 2) + "<wsdl:output name=\"response\">" + "\n";
        ret += getTab(level + 3) + "<ldap:output returnPartName=\"response\" attributes=\"\"/>" + "\n";
        ret += getTab(level + 2) + "</wsdl:output>" + "\n";
        ret += getTab(level + 2) + "<wsdl:fault name=\"fault\"/>" + "\n";
        ret += getTab(level + 1) + "</wsdl:operation>" + "\n";
        ret += getTab(level) + "</binding>" + "\n";

        return ret;
    }

    private String generatePLink(String tag, int level) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;
        ret += getTab(level) + "<plnk:partnerLinkType name=\"" + tag + "PartnerLink\">" + "\n";
        ret += getTab(level + 1) + "<plnk:role name=\"" + tag + "PortTypeRole\" portType=\"tns:" + tag + "PortType\"/>" + "\n";
        ret += getTab(level) + "</plnk:partnerLinkType>" + "\n";

        return ret;
    }

    private String generateService(String tag, int level) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;

        ret += getTab(level) + "<service name=\"" + tag + "Service\">" + "\n";
        ret += getTab(level + 1) + "<wsdl:port name=\"port1\" binding=\"tns:" + tag + "Binding\">" + "\n";
        ret += getTab(level + 2) + "<ldap:address" + "\n";

        String[] names = null;
        names = conn.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            String value = null;
            value = (String) conn.getProperty(names[i]);
            ret += getTab(level + 4) + names[i] + " = \"" + value + "\"\n";
        }
        ret += getTab(level + 2) + "/>" + "\n";
        ret += getTab(level + 1) + "</wsdl:port>\n";
        ret += getTab(level) + "</service>" + "\n";

        return ret;
    }
    
    private String generateDefinition(String tag) {
        String ret = "";
//        String tag = upInitial(ldif.getName()) + mFunction;

        ret += "<definitions name=\"" + tag + "\" targetNamespace=\"http://j2ee.netbeans.org/wsdl/" + tag + "\"" + "\n";
        ret += getTab(1) + "xmlns=\"http://schemas.xmlsoap.org/wsdl/\"" + "\n";
        ret += getTab(1) + "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"" + "\n";
        ret += getTab(1) + "xmlns:ldap=\"http://schemas.sun.com/jbi/wsdl-extensions/ldap/\"" + "\n";
        ret += getTab(1) + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + "\n";
        ret += getTab(1) + "xmlns:tns=\"http://j2ee.netbeans.org/wsdl/" + tag + "\"" + "\n";
        ret += getTab(1) + "xmlns:ns=\"http://xml.netbeans.org/schema/" + tag + "\"" + "\n";
        ret += getTab(1) + "xmlns:plnk=\"http://docs.oasis-open.org/wsbpel/2.0/plnktype\">" + "\n";

        ret += generateTypes(tag, 1);
        ret += generateMessages(tag, 1);
        ret += generatePortType(tag, 1);
        ret += generateBindings(tag, 1);
        ret += generateService(tag, 1);
        ret += generatePLink(tag, 1);

        ret += "</definitions>" + "\n";
        return ret;
    }

    private String generateWSDL(String tag) {
        String ret = "";
        ret += generateXMLHead();
        ret += generateDefinition(upInitial(tag) + mFunction);
        return ret;
    }

    private String generateMainWSDL() {
        String ret = "";
        ret += generateXMLHead();
        ret += generateDefinition(upInitial(mFileName) + mFunction);
        return ret;
    }

    public void generate() throws IOException {
        if (mSelectedObjectMap != null & mSelectedObjectMap.size() > 0) {
            Iterator it = mSelectedObjectMap.values().iterator();
            while (it.hasNext()) {
                LdifObjectClass loc = (LdifObjectClass) it.next();
                File outputFile = new File(mDir.getAbsolutePath() + File.separator + upInitial(loc.getName()) + mFunction + ".wsdl");
                FileOutputStream fos = new FileOutputStream(outputFile);
                String def = generateWSDL(loc.getName());
                fos.write(def.getBytes());
                fos.close();
                fos = null;
                loc = null;
            }
            File outputFile = new File(mDir.getAbsolutePath() + File.separator + upInitial(mFileName) + mFunction + ".wsdl");
            FileOutputStream mainFos = new FileOutputStream(outputFile);
            String mainDef = generateMainWSDL();
            mainFos.write(mainDef.getBytes());
            mainFos.close();
            mainFos = null;
        }
    }
}

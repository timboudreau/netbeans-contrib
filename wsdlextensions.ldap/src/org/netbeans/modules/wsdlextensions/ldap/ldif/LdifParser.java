/*
 * LdifParser.java
 * 
 * Created on Apr 30, 2007, 2:42:38 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.wsdlextensions.ldap.ldif;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Gary
 */

public class LdifParser {
    File mLdifFile;
    
    public LdifParser(File f) {
        mLdifFile = f;
    }

    private String findDefinition(String str) {
        String ret = "";
        int level = -1;

        for (int i = 0; i < str.length(); i++) {
            ret += str.charAt(i);
            if (str.charAt(i) == '(') {
                if (level < 0) {
                    level = 1;
                } else {
                    level++;
                }
            }
            
            if (str.charAt(i) == ')') {
                level--;
                if (level == 0) {
                    break;
                }
            }
        }

        return ret;
    }
    public List parse() throws IOException {
        List ret = new ArrayList();
        
        FileInputStream fis = new FileInputStream(mLdifFile);
        byte[] buf = new byte[fis.available()];
        fis.read(buf);
        String ldifStr = new String(buf);
        ldifStr = ldifStr.replaceAll("[\\n]", "");
        ldifStr = ldifStr.replaceAll("[\\s]", "");
        String objClassStr1 = "objectclass(";
        String objClassStr2 = "objectClasses:(";
        String objClassTag = "";
        if (ldifStr.contains(objClassStr1)) {
            objClassTag = objClassStr1;
        } else if (ldifStr.contains(objClassStr2)) {
            objClassTag = objClassStr2;
        }
        
        int pos = ldifStr.indexOf(objClassTag);

        while (pos >= 0) {
            ldifStr = ldifStr.substring(pos + objClassTag.length() - 1);
            String ldif = findDefinition(ldifStr);
            ret.add(getObjectClass(ldif));    
            ldifStr = ldifStr.substring(ldif.length());

            if (ldifStr.contains(objClassStr1)) {
                objClassTag = objClassStr1;
            } else if (ldifStr.contains(objClassStr2)) {
                objClassTag = objClassStr2;
            }
            pos = ldifStr.indexOf(objClassTag);
        }
        
        fis.close();
        return ret;
    }

    private LdifObjectClass getObjectClass(String str) {
        LdifObjectClass ret = new LdifObjectClass();
        
        ret.setName(getName(str));
        String[] mays = getMayIds(str);
        if (mays != null) {
        for (int i = 0; i < mays.length; i++) {
            ret.addMay(mays[i]);
        }
        }
        String[] musts = getMustIds(str);
        if (musts != null) {
        for (int i = 0; i < musts.length; i++) {
            ret.addMust(musts[i]);
        }
        }
        
        return ret;
    }
    private String[] getMayIds(String str) {
        String mayPattern = "[M][A][Y][(][\\w\\$]*[)]";
        String[] splitted = str.split(mayPattern);
        int before = splitted[0].length();
        int after = 0;
        if (splitted.length > 1) {
            after = str.indexOf(splitted[1]);
        }
        
        String ret = null;
        if (after == 0) {
            ret = str.substring(before);
        } else {
            ret = str.substring(before, after);
        }
        
        if (ret == null || ret.length() == 0) {
            return null;
        }

        return ret.substring(4, ret.length() - 1).split("\\$");
    }
   
    private String[] getMustIds(String str) {
        String mustPattern = "[M][U][S][T][(][\\w\\$]*[)]";
        String[] splitted = str.split(mustPattern);
        int before = splitted[0].length();
        int after = 0;
        if (splitted.length > 1) {
            after = str.indexOf(splitted[1]);
        }
        
        String ret = null;
        if (after == 0) {
            ret = str.substring(before);
        } else {
            ret = str.substring(before, after);
        }
        
        if (ret == null || ret.length() == 0) {
            return null;
        }

        return ret.substring(5, ret.length() - 1).split("\\$");
    }
    
    private String getDescription(String str) {
        String descPattern = "[D][E][S][C]['][^']*[']";
        String[] splitted = str.split(descPattern);
        int before = splitted[0].length();
        int after = 0;
        if (splitted.length > 1) {
            after = str.indexOf(splitted[1]);
        }
        
        String ret = null;
        if (after == 0) {
            ret = str.substring(before);
        } else {
            ret = str.substring(before, after);
        }

        ret = ret.substring(5, ret.length() - 1);
        return ret;
    }

    private String getName(String str) {
        String namePattern = "[N][A][M][E]['][^']*[']";
        String[] splitted = str.split(namePattern);
        int before = splitted[0].length();
        int after = 0;
        if (splitted.length > 1) {
            after = str.indexOf(splitted[1]);
        }
        
        String ret = null;
        if (after == 0) {
            ret = str.substring(before);
        } else {
            ret = str.substring(before, after);
        }

        ret = ret.substring(5, ret.length() - 1);
        return ret;
    }
    
    public static void main(String[] args) throws IOException {
        String test1 = "objectclass ( 2.5.6.3 NAME 'locality' DESC 'RFC2256: a locality' SUP top STRUCTURAL MAY ( street $ seeAlso $ searchGuide $ st $ l $ description ) MUST ( name $ uid $ cn $ sn $ ssn $ phone ) )";
        String test2 = test1.replaceAll("[\\s]", "");
        File testFile = new File("C:\\DEV\\Sun\\MPS\\slapd-zaz001\\config\\schema\\00core.ldif");
        LdifParser parser = new LdifParser(testFile);
        List list = parser.parse();
        
        System.out.println("Completed");
        
/*        String objClassRegex = "[o][b][j][e][c][t][c][l][a][s][s][(].*[$)]";
        System.out.println("Matches?: " + test2.matches(objClassRegex));
        
        Pattern namePattern = Pattern.compile("[N][A][M][E]['][\\w]*[']");
        String[] splitted = namePattern.split(test2);
        
        for (int i = 0; i < splitted.length; i++) {
            System.out.println("Splitted: " + splitted[i] + "\n");
        }*/
    }
}
package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Arrays;
/**
 * Container for SAP connection information.
 *
 * @author Noel Ang <nang@sun.com>
 */
public class SAPConnectParams implements Cloneable {
    
    public SAPConnectParams() {
        //defaults();
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof SAPConnectParams)) {
            return false;
        }
        
        SAPConnectParams other = (SAPConnectParams) obj;
        boolean equality = true;
        
        
        equality = equality && other.getClientNumber().equals(getClientNumber());
        equality = equality && other.getLanguage().equals(getLanguage());
        equality = equality && other.getRouterString().equals(getRouterString());
        equality = equality && other.getServerName().equals(getServerName());
        equality = equality && other.getSystemId().equals(getSystemId());
        equality = equality && other.getSystemNumber().equals(getSystemNumber());
        equality = equality && other.getUserName().equals(getUserName());
        equality = equality && Arrays.equals(other.getPassword(), getPassword());
        return equality;
    }

    protected Object clone() throws CloneNotSupportedException {
        SAPConnectParams copy = (SAPConnectParams) super.clone();
        char[] password = copy.getPassword();
        char[] copiedPassword = new char[password.length];
        System.arraycopy(password, 0, copiedPassword, 0, copiedPassword.length);
        copy.setPassword(copiedPassword);
        return copy;
    }
    
    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber == null ? "" : clientNumber.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? "" : userName.trim();
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, this.password.length);
    }
    
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId == null ? "" : systemId.trim();
    }

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber == null ? "" : systemNumber.trim();
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName == null ? "" : serverName.trim();
    }

    public String getRouterString() {
        return routerString;
    }

    public void setRouterString(String routerString) {
        this.routerString = routerString == null ? "" : routerString.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language == null ? "" : language.trim();
    }

    public boolean isTraceRfc() {
        return traceRfc;
    }

    public void setTraceRfc(boolean traceRfc) {
        this.traceRfc = traceRfc;
    }
   
    private String clientNumber;
    private String userName;
    private char[] password;
    private String systemId;
    private String systemNumber;
    private String serverName;
    private String routerString;
    private String language;
    private boolean traceRfc;
}

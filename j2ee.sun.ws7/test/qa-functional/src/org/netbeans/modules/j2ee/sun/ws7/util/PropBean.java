/*
 * PropBean.java
 *
 * Created on May 10, 2007, 8:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ws7.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Prabushankar.Chinnasamy
 */
public class PropBean {

    /** Creates a new instance of PropBean */
    public PropBean(String propFile) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setWsInstallDir(properties.getProperty("ws.install.dir","nothing_set"));
        setResultDir(properties.getProperty("result.dir","c:\\"));
        setRegistrationName(properties.getProperty("registration.name","Test"));
        setLogFile(properties.getProperty("log.file","c:\\log.file"));
        setAdminUser(properties.getProperty("admin.user","admin"));
        setAdminPwd(properties.getProperty("admin.pwd","admin123"));
        setAdminSSLPort(properties.getProperty("admin.ssl.port","28989"));
        setAdminPort(properties.getProperty("admin.port","28800"));
        setBogusAdminPort(properties.getProperty("bogus.admin.port","65536"));
        setSjsString(properties.getProperty("sjs.string","Sun Java System Web Server 7.0"));
        setLocalHost(properties.getProperty("local.host","localhost"));
        setRemoteHost(properties.getProperty("remote.host","saint"));
        setBogusAdminUser(properties.getProperty("bogus.admin.user","bogus"));
        setRemotePort(properties.getProperty("remote.port","38800"));
        setRemoteSSLPort(properties.getProperty("remote.ssl.port","38989"));
        setJdbcJNDIName(properties.getProperty("jdbc.jndi.name","Test"));
        //setSleepTimeMedium(properties.getProperty("sleep.medium","2000"));

        setTestProjLoc(properties.getProperty("test.proj.loc","c:\\"));
        setJavaEEVersion(properties.getProperty("java.ee.version","Java EE 5"));
        setConfigName(properties.getProperty("config.name","test"));
        setVsName(properties.getProperty("vs.name",getConfigName()));
        setTcList(properties.getProperty("tc.list","c:\\tc.lst"));
        setRemotePort(properties.getProperty("remote.port","8989"));
        setRemoteConfigName(properties.getProperty("remote.config.name","saint"));
        setRemoteVSName(properties.getProperty("remote.vs.name","saint"));
        setIsRemote(properties.getProperty("is.remote","false").equals("true")?true:false);

//ws.install.dir="C:\\Sun\\B08"
//registration.name="Test"
//admin.user="admin"
//admin.pwd="admin123"
//admin.ssl.port="28989"
//admin.port="28800"
//bogus.admin.port="65536"
//bogus.admin.user="bogus"
//sjs.string="Sun Java System Web Server 7.0"
//result.dir="c:/"
//log.file="c:\log.file"

    }


    public String getWsInstallDir() {
        return wsInstallDir;
    }

    public void setWsInstallDir(String wsInstallDir) {
        this.wsInstallDir = wsInstallDir;
    }

    private String resultDir;
    private String wsInstallDir;
    private String registrationName;
    private String logFile;
    private String adminUser;
    private String adminPwd;
    private String localHost;
    private String adminSSLPort;
    private String adminPort;
    private String bogusAdminPort;
    private int sleepTimeMedium;
    private String sjsString;
    private boolean isRemote;
    private String bogusWsInstallDir;
    private String bogusAdminUser;
    private String remoteHost;
    private String remotePort;

    private String testProjLoc;
    private String javaEEVersion;
    private String configName;
    private String vsName;
    private String jdbcJNDIName;
    private String tcList;
    private String remoteConfigName;
    private String remoteVSName;
    private String remoteSSLPort;


    public String getResultDir() {
        return resultDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPwd() {
        return adminPwd;
    }

    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    public String getAdminSSLPort() {
        return adminSSLPort;
    }

    public void setAdminSSLPort(String adminSSLPort) {
        this.adminSSLPort = adminSSLPort;
    }

    public String getAdminPort() {
        return adminPort;
    }

    public void setAdminPort(String adminPort) {
        this.adminPort = adminPort;
    }



    public int getSleepTimeMedium() {
        return sleepTimeMedium;
    }

    public void setSleepTimeMedium(String sleepTimeMedium) {
        this.sleepTimeMedium = Integer.parseInt(sleepTimeMedium);
    }

    public String getSjsString() {
        return sjsString;
    }

    public void setSjsString(String sjsString) {
        this.sjsString = sjsString;
    }



    public boolean isRemote() {
        return isRemote;
    }

    public void setIsRemote(boolean isRemote) {
        this.isRemote = isRemote;
    }



    public String getBogusAdminUser() {
        return bogusAdminUser;
    }

    public void setBogusAdminUser(String bogusAdminUser) {
        this.bogusAdminUser = bogusAdminUser;
    }

    public String getRegistrationName() {
        return registrationName;
    }

    public void setRegistrationName(String registrationName) {
        this.registrationName = registrationName;
    }

    public String getBogusAdminPort() {
        return bogusAdminPort;
    }

    public void setBogusAdminPort(String bogusAdminPort) {
        this.bogusAdminPort = bogusAdminPort;
    }

    public String getBogusWsInstallDir() {
        return bogusWsInstallDir;
    }

    public void setBogusWsInstallDir(String bogusWsInstallDir) {
        this.bogusWsInstallDir = bogusWsInstallDir;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteSSLPort() {
        return remoteSSLPort;
    }

    public void setRemoteSSLPort(String remoteSSLPort) {
        this.remoteSSLPort = remoteSSLPort;
    }

    public String getTestProjLoc() {
        return testProjLoc;
    }

    public void setTestProjLoc(String testProjLoc) {
        this.testProjLoc = testProjLoc;
    }

    public String getJavaEEVersion() {
        return javaEEVersion;
    }

    public void setJavaEEVersion(String javaEEVersion) {
        this.javaEEVersion = javaEEVersion;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getVsName() {
        return vsName;
    }

    public void setVsName(String vsName) {
        this.vsName = vsName;
    }

    public String getJdbcJNDIName() {
        return jdbcJNDIName;
    }

    public void setJdbcJNDIName(String jdbcJNDIName) {
        this.jdbcJNDIName = jdbcJNDIName;
    }

    public String getTcList() {
        return tcList;
    }

    public void setTcList(String tcList) {
        this.tcList = tcList;
    }

    public String getRemoteConfigName() {
        return remoteConfigName;
    }

    public void setRemoteConfigName(String remoteConfigName) {
        this.remoteConfigName = remoteConfigName;
    }

    public String getRemoteVSName() {
        return remoteVSName;
    }

    public void setRemoteVSName(String remoteVSName) {
        this.remoteVSName = remoteVSName;
    }
}

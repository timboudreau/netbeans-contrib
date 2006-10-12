/*
 * ReportModel.java
 *
 * Created on April 11, 2006, 4:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ide.avk.report.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bshankar@sun.com
 */
public class ReportModel {
    
    private static ReportModel reportModel = null;
    private String resultXML;
    
    public synchronized static ReportModel getInstance() {
        if(reportModel == null) {
            reportModel = new ReportModel();
        }
        return reportModel;
    }
    
    private AppVerification resultDocument;
    
    /**
     * Generate the result document for the given result.xml file.
     */
    public boolean init(String resultXML) {
        this.resultXML = resultXML;
        try {
            resultDocument = AppVerification.createGraph(new File(resultXML));
        } catch(Throwable t) {
            return false;
        }
        return true;
    }
    
    public String getResultsDir() {
        File f = new File(resultXML);
        return f.getParent();
    }
    
    public List<WebComponent> getWebComponentsSuccessfullyCalled() {
        List<WebComponent> webCompsSuccessfullyCalled = new ArrayList<WebComponent>();
        if(resultDocument != null) {
            WebComponent[] webComps = resultDocument.getWebComponent();
            for(WebComponent webComp : webComps) {
                WebEntity[] webEntities = webComp.getWebEntity();
                for(WebEntity webEntity : webEntities) {
                    AppVerificationException[] exceptions = webEntity.getAppVerificationException();
                    String invocations = webEntity.getCounter();
                    if((exceptions == null || exceptions.length == 0) && !("0".equals(invocations))) {
                        WebComponent newWebComp = new WebComponent();
                        newWebComp.setContext(webComp.getContext());
                        newWebComp.setWebEntity(new WebEntity[]{webEntity});
                        webCompsSuccessfullyCalled.add(newWebComp);
                    }
                }
            }
        }
        return webCompsSuccessfullyCalled;
    }
    
    public List<WebComponent> getWebComponentsUnSuccessfullyCalled() {
        List<WebComponent> webCompsUnSuccessfullyCalled = new ArrayList<WebComponent>();
        if(resultDocument != null) {
            WebComponent[] webComps = resultDocument.getWebComponent();
            for(WebComponent webComp : webComps) {
                WebEntity[] webEntities = webComp.getWebEntity();
                for(WebEntity webEntity : webEntities) {
                    AppVerificationException[] exceptions = webEntity.getAppVerificationException();
                    String invocations = webEntity.getCounter();
                    if((exceptions != null && exceptions.length != 0)) {
                        WebComponent newWebComp = new WebComponent();
                        newWebComp.setContext(webComp.getContext());
                        newWebComp.setWebEntity(new WebEntity[]{webEntity});
                        webCompsUnSuccessfullyCalled.add(newWebComp);
                    }
                }
            }
        }
        return webCompsUnSuccessfullyCalled;
    }
    
    public List<WebComponent> getWebComponentsNotCalled() {
        List<WebComponent> webCompsNotCalled = new ArrayList<WebComponent>();
        if(resultDocument != null) {
            WebComponent[] webComps = resultDocument.getWebComponent();
            for(WebComponent webComp : webComps) {
                WebEntity[] webEntities = webComp.getWebEntity();
                for(WebEntity webEntity : webEntities) {
                    String invocations = webEntity.getCounter();
                    if(("0".equals(invocations))) {
                        WebComponent newWebComp = new WebComponent();
                        newWebComp.setContext(webComp.getContext());
                        newWebComp.setWebEntity(new WebEntity[]{webEntity});
                        webCompsNotCalled.add(newWebComp);
                    }
                }
            }
        }
        return webCompsNotCalled;
    }
    
    public List<EnterpriseBean> getEJBComponentsSuccessfullyCalled() {
        List<EnterpriseBean> ejbCompsSuccessfullyCalled = new ArrayList<EnterpriseBean>();
        if(resultDocument != null) {
            EnterpriseBean[] ejbs = resultDocument.getEnterpriseBean();
            for(EnterpriseBean ejb : ejbs) {
                Method[] ejbMethods = ejb.getMethod();
                for(Method ejbMethod : ejbMethods) {
                    AppVerificationException[] exceptions = ejbMethod.getAppVerificationException();
                    String invocations = ejbMethod.getCounter();
                    if((exceptions == null || exceptions.length == 0) && !("0".equals(invocations))) {
                        EnterpriseBean newEJB = new EnterpriseBean();
                        newEJB.setBeanName(ejb.getBeanName());
                        newEJB.setAppName(ejb.getAppName());
                        newEJB.setMethod(new Method[]{ejbMethod});
                        ejbCompsSuccessfullyCalled.add(newEJB);
                    }
                }
            }
        }
        return ejbCompsSuccessfullyCalled;
    }
    
    public List<EnterpriseBean> getEJBComponentsUnSuccessfullyCalled() {
        List<EnterpriseBean> ejbCompsUnSuccessfullyCalled = new ArrayList<EnterpriseBean>();
        if(resultDocument != null) {
            EnterpriseBean[] ejbs = resultDocument.getEnterpriseBean();
            for(EnterpriseBean ejb : ejbs) {
                Method[] ejbMethods = ejb.getMethod();
                for(Method ejbMethod : ejbMethods) {
                    AppVerificationException[] exceptions = ejbMethod.getAppVerificationException();
                    String invocations = ejbMethod.getCounter();
                    if((exceptions != null && exceptions.length != 0)) {
                        EnterpriseBean newEJB = new EnterpriseBean();
                        newEJB.setBeanName(ejb.getBeanName());
                        newEJB.setAppName(ejb.getAppName());
                        newEJB.setMethod(new Method[]{ejbMethod});
                        ejbCompsUnSuccessfullyCalled.add(newEJB);
                    }
                }
            }
        }
        return ejbCompsUnSuccessfullyCalled;
    }
    
    public List<EnterpriseBean> getEJBComponentsNotCalled() {
        List<EnterpriseBean> ejbCompsNotCalled = new ArrayList<EnterpriseBean>();
        if(resultDocument != null) {
            EnterpriseBean[] ejbs = resultDocument.getEnterpriseBean();
            for(EnterpriseBean ejb : ejbs) {
                Method[] ejbMethods = ejb.getMethod();
                for(Method ejbMethod : ejbMethods) {
                    String invocations = ejbMethod.getCounter();
                    if("0".equals(invocations)) {
                        EnterpriseBean newEJB = new EnterpriseBean();
                        newEJB.setBeanName(ejb.getBeanName());
                        newEJB.setAppName(ejb.getAppName());
                        newEJB.setMethod(new Method[]{ejbMethod});
                        ejbCompsNotCalled.add(newEJB);
                    }
                }
            }
        }
        return ejbCompsNotCalled;
    }
    
    
    public String getWebCompCoverage() {
        String webCompPercentage = "N.A";
        try {
            webCompPercentage = resultDocument.getPercentage().getWebPercentage();
            if (webCompPercentage != null && webCompPercentage.trim().length() != 0) {
                webCompPercentage += "%";
            } else {
                webCompPercentage = "N.A";
            }
        } catch(Throwable t) {}
        return webCompPercentage;
    }
    
    public String getEJBCompCoverage() {
        String ejbCompPercentage = "N.A";
        try {
            ejbCompPercentage = resultDocument.getPercentage().getBeanPercentage(0).getEjbPercentage();
            if (ejbCompPercentage != null && ejbCompPercentage.trim().length() != 0) {
                ejbCompPercentage += "%";
            } else {
                ejbCompPercentage = "N.A";
            }
        } catch(Throwable t) {}
        return ejbCompPercentage;
    }
    
    public String getApplicationName() {
        String applicationName = "NONE";
        try {
            applicationName = resultDocument.getPercentage().getBeanPercentage(0).getAppName();
            if (applicationName == null || applicationName.trim().length() == 0) {
                applicationName = "NONE";
            }
        } catch(Throwable t) {}
        return applicationName;
    }
}

package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Collects SAP R/3 Business Object information.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public class SAPObjectBrowser {

    public SAPObjectBrowser() {
    }
    
    public synchronized void setConnectionParams(SAPConnectParams params) {
        try {
            this.params = (SAPConnectParams) params.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns an iteration over the business objects gathered from the last
     * browsing operation.
     *
     * @return (read-only) Iteration of BrowseTreeNode objects.
     */
    public Iterator<BrowseTreeNode> iterator() {
        return Collections.unmodifiableList(busObjs).iterator();
    }
    
    /**
     * Connect to SAP and catalog BAPI/RFC objects.
     *
     * @return true if the operation successfully completes with no errors.
     */
    public boolean browse() {
        boolean success = false;
        
        setStarted();
        
        connect();
        loadObjectList();
        
        synchronized(this) {
            setCompleted();
            success = true;
        }
        
        return success;
    }
    
    public String outcomeMessage() {
        return outcomeMessage;
    }

    public void setBrowseType(ObjectType objectType) {
        this.objectType = objectType;
    }
    
    private void connect() {
    }
    
    private void loadObjectList() {
        switch (objectType) {
            case BAPI:
                readBapiObjects();
                break;
            case RFC:
                readRfcObjects();
                break;
        }
    }
    
    private synchronized void setStarted() {
        if (isStarted) {
            throw new IllegalStateException("SAPObjectBrowser already in use.");
        }
        isStarted = true;
    }
    
    private synchronized void setCompleted() {
        isStarted = false;
    }

    private void readRfcObjects() {
        busObjs.clear();
        // hack for demo
        readBapiObjects();
    }

    private void readBapiObjects() {
        busObjs.clear();
        
        // hack for demo
        ApplicationNode node0 = new ApplicationNode("Application_Components");
        busObjs.add(node0);
        
        ApplicationNode node01 = new ApplicationNode("Cross-Application_Components");
        ApplicationNode node02 = new ApplicationNode("Controlling");
        ApplicationNode node03 = new ApplicationNode("Travel");
        node0.addChild(node01);
        node0.addChild(node02);
        node0.addChild(node03);
        
        BapiNode node011 = new BapiNode("Routing");
        BapiNode node021 = new BapiNode("CostCenter");
        BapiNode node031 = new BapiNode("Flight");
        node01.addChild(node011);
        node02.addChild(node021);
        node03.addChild(node031);
    }

    public enum ObjectType { BAPI, RFC };

    private boolean isStarted;
    private boolean isInterrupted;
    private String outcomeMessage = "";
    private SAPConnectParams params;
    private SAPObjectBrowser.ObjectType objectType;
    private List<BrowseTreeNode> busObjs = new LinkedList<BrowseTreeNode>();
    //private JCO.Client jcoClient;
}

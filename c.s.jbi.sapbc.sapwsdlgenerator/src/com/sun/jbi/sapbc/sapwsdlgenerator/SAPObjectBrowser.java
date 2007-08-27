package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;

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
    public boolean browse() throws Exception{
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
    
    private void connect() throws Exception {
    	try {
	    	SAPConnectionFactory mFactory = SAPConnectionFactory.getInstance();
	    	jcoClient = mFactory.getConnection(params);
	        mPartnerEncoding = jcoClient.getAttributes().getPartnerEncoding();
	        mPartnerBytesPerChar = jcoClient.getAttributes().getPartnerBytesPerChar();
	        mSAPSystemID = jcoClient.getAttributes().getSystemID();
	        mSAPASHost = jcoClient.getASHost();
	        mSAPSystemNumber = jcoClient.getAttributes().getSystemNumber();
	        jcoClient.setTrace(params.isTraceRfc());
	        com.sap.mw.jco.JCO.setTraceLevel(4);
	        // Handle to SAP Repository
	        rfcRepository = new JCO.Repository(jcoClient.getSystemID(), jcoClient);
    	} catch (JCO.Exception e) {
            throw new Exception ("Error in SAP Connection" + e.getLocalizedMessage());
        } catch (Exception e) {
            //mLogger.error("Exception thrown in SAPBapiOtdBuilder",  e);               
        }
    	
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
       // readBapiObjects();
    }

    private void readBapiObjects() {
        busObjs.clear();
        try {
        	int rc;
        	//mlogger.debug("reading BAPI Objects");
        	if (jcoClient == null){
        		this.connect();
        	}
        	
        	
        	
        } catch(Exception e) {
        	
        }
        
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
    
    /**
     * Sets the item selected
     *
     * @param item The selected item i.e. bapi or rfc
     */
    public void setItemSelected(String item) {
        mItemSelected = item;
    }

    /**
     * Gets the item selected
     *
     * @return Getter for the item selected
     */
    public String getItemSelected() {
      return mItemSelected;
    }

    /**
     * Gets the bytes per character used by the partner SAP system
     *
     * @return Partner bytes per characters
     */
    public int getPartnerBytesPerChar() {
      return mPartnerBytesPerChar;
    }

    /**
     * Gets the SAP System ID on the client
     *
     * @return SAP System ID
     */
    public String getSAPSystemID() {
      return mSAPSystemID;
    }

    /**
     * Gets the SAP Host on the client
     *
     * @return SAP Host
     */
    public String getSAPASHost() {
      return mSAPASHost;
    }

    /**
     * Gets the SAP System Number on the client
     *
     * @return SAP System Number
     */
    public String getSAPSystemNumber() {
      return mSAPSystemNumber;
    }

    public enum ObjectType { BAPI, RFC };

    private boolean isStarted;
    private boolean isInterrupted;
    private String outcomeMessage = "";
    private SAPConnectParams params;
    private SAPObjectBrowser.ObjectType objectType;
    private List<BrowseTreeNode> busObjs = new LinkedList<BrowseTreeNode>();
   
    /** An object for connecting to SAP */
    public JCO.Client jcoClient = null;
    
    /** handle to SAP Repository */
    public JCO.Repository rfcRepository = null; 

    /** An JCO Function for SWO_QUERY_API_METHODS */
    private com.sap.mw.jco.JCO.Function swoQueryApiMethodsFunc = null;

    /** An JCO Function for RPY_BOR_TREE_INIT */
    private com.sap.mw.jco.JCO.Function rpyBorTreeInitFunc = null;

    /** An JCO Function for RFC_SEARCH_FUNCTION */
    private com.sap.mw.jco.JCO.Function rfcSearchFunc = null;
    
    /* String for the Item Selected */
    private String mItemSelected = null;
    
    private String mPartnerEncoding = null;
    int mPartnerBytesPerChar;
    String mSAPSystemID;
    String mSAPASHost;
    String mSAPSystemNumber;

}

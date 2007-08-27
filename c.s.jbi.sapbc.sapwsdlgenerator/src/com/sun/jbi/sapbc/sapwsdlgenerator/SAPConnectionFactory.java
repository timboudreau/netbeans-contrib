package com.sun.jbi.sapbc.sapwsdlgenerator;

import com.sap.mw.jco.JCO;
import java.io.Serializable;
import java.util.Properties;

/**
 * This class implements SAP connection information.
 *
 * @author 
 * @version 
 */
public class SAPConnectionFactory
    implements Serializable {

    private static SAPConnectionFactory mInstance = null;

    private Properties clientProps = new java.util.Properties();

    private JCO.Client mJcoClient = null;

    private String mClient;

    private String mUser;

    private char[] mPassword;

    private String mLanguage;

    private String mAppServHost;

    private String mSysNum;
    
    private String mSysPwd;

    
    /**
     * Creates a new SAPConnectionFactory object.
     */
    private SAPConnectionFactory() {
    }

    /**
     * Get single instance of SAPConnectionFactory
     *
     * @return connection factory
     *
     * @throws Exception factory exception
     */
    public static SAPConnectionFactory getInstance()
        throws Exception {
        if (mInstance == null) {
            mInstance = new SAPConnectionFactory();
        }

        return mInstance;
    }

    /**
     * Get SAP Client Connection.
     *
     * @param connectParams SAPConnectParams
     *
     * @return JCO.Client
     *
     * @throws Exception exception while retrieving connection
     */
    public JCO.Client getConnection(SAPConnectParams connectParams)
        throws Exception {
        setConnectParams(connectParams);
        initConnection();
        return mJcoClient;
    }

    /**
     * This method is used to get single SAP Client connection. Before call
     * this method, the caller needs to call setConnectParams() first.
     *
     * @return JCO.Client
     *
     * @throws Exception exception
     */
    public synchronized JCO.Client getConnection()
        throws Exception {
        initConnection();
        return mJcoClient;
    }

    /**
     * This method is to close the SAP connection.
     *
     * @throws Exception exception
     */
    public void close()
        throws Exception {
        mJcoClient.disconnect();
    }

    /**
     * set Connection Parameters.
     *
     * @param connectParams SAPConnectParams
     */
    public void setConnectParams(SAPConnectParams connectParams) {
        mClient = connectParams.getClientNumber();
        mUser = connectParams.getUserName();
        mPassword = connectParams.getPassword();
        mSysPwd = this.getPassword(mPassword);
        mLanguage = connectParams.getLanguage();
        mAppServHost = connectParams.getServerName();
        mSysNum = connectParams.getSystemNumber();
    }
    
    private String getPassword(char[] pass){   
        String passStr = "";       
        for (int i = 0; i < pass.length; i++) {
           passStr = passStr + pass[i];   
        }
        return passStr;
    }
    
    private void initConnection()
        throws Exception {
        //client properties to log on for repository queries
        clientProps.put(
            "jco.client.client",
            mClient);
        clientProps.put(
            "jco.client.user",
            mUser);
        clientProps.put(
            "jco.client.passwd",
            mSysPwd);
        clientProps.put(
            "jco.client.lang",
            mLanguage);
        clientProps.put(
            "jco.client.ashost",
            mAppServHost);
        clientProps.put(
            "jco.client.sysnr",
            mSysNum);

        //create an JCO client
        mJcoClient = JCO.createClient(clientProps);
        mJcoClient.connect();
    }
}

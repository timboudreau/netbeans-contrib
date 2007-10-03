/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.openoffice.config;

import com.sun.star.beans.PropertyValue;
import com.sun.star.document.XActionLockable;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.XInterface;


/**
 * Main AddOn class
 *
 * @author S. Aubrecht
 */
public final class ConfigurationViewer extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.frame.XDispatchProvider,
              com.sun.star.lang.XInitialization,
              com.sun.star.frame.XDispatch
{
    private final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = ConfigurationViewer.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler" };

    private static final String[] CONFIG_ROOTS = {
        "Writer",
        "Calc",
        "Base",
        "Draw",
        "Impress",
        "Math",
        "Commands",
        "TypeDetection",
        "DataAccess",
        "CalcAddIns"
    };

    public ConfigurationViewer( XComponentContext context )
    {
        m_xContext = context;
    };

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(ConfigurationViewer.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch( com.sun.star.util.URL aURL,
                                                       String sTargetFrameName,
                                                       int iSearchFlags )
    {
        if ( aURL.Protocol.compareTo("org.openoffice.config.configurationviewer:") == 0 )
        {
            if ( aURL.Path.compareTo("ViewConfig") == 0 )
                return this;
        }
        return null;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
         com.sun.star.frame.DispatchDescriptor[] seqDescriptors )
    {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
            new com.sun.star.frame.XDispatch[seqDescriptors.length];

        for( int i=0; i < nCount; ++i )
        {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                                             seqDescriptors[i].FrameName,
                                             seqDescriptors[i].SearchFlags );
        }
        return seqDispatcher;
    }

    // com.sun.star.lang.XInitialization:
    public void initialize( Object[] object )
        throws com.sun.star.uno.Exception
    {
        if ( object.length > 0 )
        {
            m_xFrame = (com.sun.star.frame.XFrame)UnoRuntime.queryInterface(
                com.sun.star.frame.XFrame.class, object[0]);
        }
    }

    // com.sun.star.frame.XDispatch:
     public void dispatch( com.sun.star.util.URL aURL,
                           com.sun.star.beans.PropertyValue[] aArguments )
    {
         if ( aURL.Protocol.compareTo("org.openoffice.config.configurationviewer:") == 0 )
        {
            if ( aURL.Path.compareTo("ViewConfig") == 0 )
            {
                try {
                    showAsSpreadsheet();
                } catch( Exception e ) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void addStatusListener( com.sun.star.frame.XStatusListener xControl,
                                    com.sun.star.util.URL aURL )
    {
        // add your own code here
    }

    public void removeStatusListener( com.sun.star.frame.XStatusListener xControl,
                                       com.sun.star.util.URL aURL )
    {
        // add your own code here
    }

    public void showAsSpreadsheet() throws Exception {
        ConfigurationAccess configAccess = new ConfigurationAccess( m_xContext );
        
        XSpreadsheetDocument doc = null; 
        String configPath = "/org.openoffice.Office.";
        for( String rootName : CONFIG_ROOTS ) {
            String rootPath = configPath + rootName;
            if( !configAccess.isValidRootPath( rootPath ) )
                continue;
            
            String[] defaultSpreadSheetNames = null;
            if( null == doc ) {
                doc = createSpreadsheetDocument();
                defaultSpreadSheetNames = doc.getSheets().getElementNames();
            }
            XActionLockable actionInterface = null;
            actionInterface = (XActionLockable) UnoRuntime.queryInterface( XActionLockable.class, doc );

            // lock all actions
            actionInterface.addActionLock();
            
            ConfigurationProcessor processor = new SpreadsheetConfigurationProcessor( doc, rootName );
            configAccess.browse( rootPath, processor );
            processor.format();

            //remove all default sheets
            if( null != defaultSpreadSheetNames ) {
                XSpreadsheets xSheets = doc.getSheets();
                for( String sheetName : defaultSpreadSheetNames ) {
                    xSheets.removeByName( sheetName );
                }
            }
            
            actionInterface.removeActionLock();
        }
    }

    protected XSpreadsheetDocument createSpreadsheetDocument() throws java.lang.Exception {
        // get the Desktop, we need its XComponentLoader interface to load a new document
        Object desktop = m_xContext.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
        
        // query the XComponentLoader interface from the desktop
        XComponentLoader xComponentLoader = (XComponentLoader)UnoRuntime.queryInterface(XComponentLoader.class, desktop);
        
        // create empty array of PropertyValue structs, needed for loadComponentFromURL
        PropertyValue[] loadProps = new PropertyValue[0];
        
        // load new calc file
        XComponent comp =  xComponentLoader.loadComponentFromURL("private:factory/scalc", "_blank", 0, loadProps);
        return (XSpreadsheetDocument)UnoRuntime.queryInterface( XSpreadsheetDocument.class, comp );    
    }
}

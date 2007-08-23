/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.LogFactory;
import org.netbeans.modules.tasklist.checkstyle.settings.Settings;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author S. Aubrecht
 */
public class CheckStyleTaskScanner extends FileTaskScanner implements PropertyChangeListener {
    
    private Callback callback;
    
    private Checker checker;
    private CheckstyleListener listener;
    
    /** 
     * Creates a new instance of TodoTaskProvider 
     * 
     */
    CheckStyleTaskScanner( String displayName, String description ) {
        super( displayName, description, "Advanced" ); //NOI18N
    }
    
    public static CheckStyleTaskScanner create() {
        return new CheckStyleTaskScanner( NbBundle.getBundle( CheckStyleTaskScanner.class ).getString( "LBL_checkstyletask" ), //NOI18N
                NbBundle.getBundle( CheckStyleTaskScanner.class ).getString( "HINT_checkstyletask" ) ); //NOI18N
    }

    public List<? extends Task> scan( FileObject resource ) {
        if( !isSupported( resource ) )
            return null;

        File resourceFile = FileUtil.toFile( resource );
        CheckstyleListener cl = getCheckstyleListener();
        cl.setCurrentResource( resource );
        Checker c = null;
        try {    
        c = getChecker();
        } catch( CheckstyleException ex ) {
            Logger.getLogger( CheckStyleTaskScanner.class.getName() ).log( Level.INFO, "Cannot create Checkstyle checker", ex );
            return getEmptyList();
        }
        
        c.process( new File[] { resourceFile } );
        
        List<? extends Task> tasks = cl.getTasks();
        if( null == tasks )
            tasks = getEmptyList();
        return tasks;
    }
    
    
    private boolean isSupported( FileObject file ) {
        if( null == file || file.isFolder() )
            return false;
        return Settings.getDefault().isExtensionSupported( file.getExt() );
    }
    
    private List<? extends Task> getEmptyList() {
        List<? extends Task> res = Collections.emptyList();
        return res;
    }

    public void attach( Callback callback ) {
        if( null == callback && null != this.callback ) {
            checker = null;
            listener = null;
            Settings.getDefault().removePropertyChangeListener( this );
        } else if( null != callback && null == this.callback ) {
            Settings.getDefault().addPropertyChangeListener( this );
        }
        this.callback = callback;
    }

    public void propertyChange( PropertyChangeEvent e ) {
        if( Settings.PROP_CONFIG_URL.equals( e.getPropertyName() ) ) {
            checker = null;
            listener = null;
            if( null != callback )
                callback.refreshAll();
        }
    }
    
    @Override
    public void notifyPrepare() {
    }

    @Override
    public void notifyFinish() {
        checker = null;
        listener = null;
    }

    private Checker getChecker() throws CheckstyleException {
        if( null == checker ) {
            checker = new Checker();
            checker.addListener( getCheckstyleListener() );
            Properties props = new Properties( System.getProperties() );
//            props.put( "org.apache.commons.logging.Log", "org.netbeans.modules.tasklist.checkstyle.MyLogger" );
            LogFactory.getFactory().setAttribute( "org.apache.commons.logging.Log", "org.netbeans.modules.tasklist.checkstyle.DelegatingLogger" );
            
            Configuration cfg = ConfigurationLoader.loadConfiguration( Settings.getDefault().getConfigurationUrl(), new PropertiesExpander(props) );
            checker.setClassloader( Lookup.getDefault().lookup( ClassLoader.class ) );
            checker.configure( cfg );
        }
        return checker;
    }

    private CheckstyleListener getCheckstyleListener() {
        if( null == listener ) {
            listener = new CheckstyleListener();
        }
        return listener;
    }
}

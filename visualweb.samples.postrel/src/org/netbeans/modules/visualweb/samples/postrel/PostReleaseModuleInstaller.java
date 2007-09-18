package org.netbeans.modules.visualweb.samples.postrel;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.netbeans.modules.visualweb.api.complib.ComplibException;
import org.netbeans.modules.visualweb.api.complib.ComplibService;

import org.openide.modules.ModuleInstall;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 * Module that packages sample web apps, complibs, and code clips
 * that are delivered post-release of the IDE via the update center.
 */
public class PostReleaseModuleInstaller extends ModuleInstall {
    private static final String SAMPLES_POSTREL_COMPLIBS = "samples.postrel.complibs";
    private static final String INSTALLED                = "installed";

    public void restored() {
        Preferences preferences = NbPreferences.forModule( PostReleaseModuleInstaller.class );
        try {
            if ( preferences.get(SAMPLES_POSTREL_COMPLIBS, null) == null ) {
                File samplesComplibsDir = InstalledFileLocator.getDefault().locate("samples/complibs", null, false ); // NOI18N
                Logger.getLogger("org.netbeans.modules.visualweb.samples.postrel").log(Level.WARNING, samplesComplibsDir.toString());
                ComplibService complibService = (ComplibService) Lookup.getDefault().lookup( ComplibService.class );
                for ( File complibFile : samplesComplibsDir.listFiles() ) {
                    complibService.installComplibFile( complibFile, false );
                }
                preferences.put(SAMPLES_POSTREL_COMPLIBS, INSTALLED);
            }
        } catch (ComplibException ce) {
            Logger.getLogger("org.netbeans.modules.visualweb.samples.postrel").log(Level.WARNING, ce.getMessage());
        }
    }
    
    public void close() {
    }
}


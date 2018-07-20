/*
 * Installer.java
 *
 * Created on May 15, 2007, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.servers.sunps7;
import org.openide.modules.ModuleInstall;
/**
 *
 * @author Satyaranjan
 */
public class Installer extends ModuleInstall {

  
   public void restored() {
        RegistryLibrary.getDefault().copyLibs(false);
    }
    public void installed() {
        RegistryLibrary.getDefault().copyLibs(true);
    }

}

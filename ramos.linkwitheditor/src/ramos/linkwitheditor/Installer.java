package ramos.linkwitheditor;


import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public final class Installer extends ModuleInstall {
   
   
   /**
    * Called when the module is uninstalled (from a running IDE). Should
    * remove whatever functionality from the IDE that it had registered.
    */
   
   public void uninstalled() {
      LinkWithEditorActions.detachCurrentListener();
   }
   
  
  
}

/*
 * LinkWithEditorActions.java
 *
 * Created on 6 de agosto de 2006, 10:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ramos.linkwitheditor;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
//import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author ramos
 */
public final class LinkWithEditorActions {
   
   private static PropertyChangeListener currentListener;
   private static Action linkWithProjectsAction;
   private static Action linkWithFilesAction;
   private static Action linkWithFavoritesAction;
   private static Action linkWithNothingAction;
   
   protected static Action getLinkWithProjectsAction(){
      if (linkWithProjectsAction == null) linkWithProjectsAction =
          new LinkWithProjectsAction();
      return linkWithProjectsAction;
   }
   protected  static Action geLinkWithFilesAction(){
      if (linkWithFilesAction == null) linkWithFilesAction = 
          new LinkWithFilesAction();
      return linkWithFilesAction;
   }
   protected static Action getLinkWithFavoritesAction(){
      if (linkWithFavoritesAction == null) linkWithFavoritesAction = 
          new LinkWithFavoritesAction();
      return linkWithFavoritesAction;
   }
   protected static Action getLinkWithNothingAction(){
      if (linkWithNothingAction == null) linkWithNothingAction = 
          new LinkWithNothingAction();
      return linkWithNothingAction;
   }
   private static Object getTheObject(final String pathInSystemFilesystem)
   throws DataObjectNotFoundException, IOException, ClassNotFoundException {
      InstanceCookie ck;
      FileObject fo = Repository.getDefault()
      .getDefaultFileSystem()
      .getRoot()
      .getFileObject(pathInSystemFilesystem);
      if (fo == null) return null;
      ck = (InstanceCookie) DataObject.find(fo)
      .getCookie(InstanceCookie.class);
      
      return ck.instanceCreate();
   }
   
   protected static void detachCurrentListener(){
      TopComponent.getRegistry().removePropertyChangeListener(currentListener);
   }
   
   private static void attachListener(PropertyChangeListener linkWithAction) {
      TopComponent.getRegistry().addPropertyChangeListener(linkWithAction);
      currentListener = linkWithAction;
   }
   
   public static abstract class AbstractLinkWithEditorAction
       extends AbstractAction implements PropertyChangeListener {
      
      Action selectInAction;
      protected TopComponent tc;
      private static String SLIDING = "Sliding";
      private BaseDocument mydoc = null;
      /**
       * Creates a new instance of AbstractLinkWithEditorAction
       */
      public AbstractLinkWithEditorAction() {
         try {
            if (selectInAction == null) {
               selectInAction = (Action) getTheObject(getPathToAction());
               //System.out.println("selectInAction here"+selectInAction);
               this.tc = WindowManager.getDefault().findTopComponent(getTCId());
            }
         } catch (final DataObjectNotFoundException ex) {
            ex.printStackTrace();
         } catch (final ClassNotFoundException ex) {
            ex.printStackTrace();
         } catch (final IOException ex) {
            ex.printStackTrace();
         }
      }
      
      
      
      public void actionPerformed(ActionEvent e) {
         //System.out.println("actionPerformed "+this);
         detachCurrentListener();
         attachListener(this);
      }
      
      abstract String getPathToAction();
      abstract String getTCId();
      
      public void propertyChange(PropertyChangeEvent evt) {
         //System.out.println("propertyChange "+this);
         if (evt.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED)) {
             //System.out.println("PROP_ACTIVATED "+this);
            String modeName = WindowManager.getDefault().findMode(tc).getName();
            //tc.close();
            ////System.out.println(tc.isOpened());
            if (modeName.contains(SLIDING)  || !tc.isVisible() || !tc.isOpened()){
               //System.out.println("returning1? "+this);
               //System.out.println("tc "+tc);
               return ;
            }
            ////System.out.println(WindowManager.getDefault().findTopComponent("projectTabLogical_tc"));
            BaseDocument selectedDoc = Registry.getMostActiveDocument();
            if (selectedDoc == null || selectedDoc.equals(mydoc)){
                //System.out.println("returning2? "+this);
               return;
            }
            Node[] selectedNodes = TopComponent.getRegistry().getCurrentNodes();
            if ((selectedNodes == null) || (selectedNodes.length == 0) ) {
                //System.out.println("returning3? "+this);
               return;
            }
            
            DataObject selectedDataObject = (DataObject) selectedNodes[0].getLookup()
            .lookup(DataObject.class);
            
            
            DataObject workingDocDataObject = NbEditorUtilities.getDataObject(selectedDoc);
            
            if (selectedDataObject != null && selectedDataObject.equals(workingDocDataObject)) {
               mydoc = selectedDoc;
               selectInAction.actionPerformed(null);
               org.netbeans.editor.Registry.getMostActiveComponent().requestFocus();
            }else{ 
               //System.out.println("returning4? "+this);
            }
         }
      }
      
   }
   
   private static class LinkWithProjectsAction
       extends AbstractLinkWithEditorAction{
      
      String getPathToAction(){
         return  "Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInProjects.instance";
      }
      
      
      
      String getTCId() {
         return "projectTabLogical_tc";
      }
      
     
      
       public Object getValue(String key) {
         if (key.equals(AbstractAction.NAME)){
            return org.openide.util.NbBundle.
                getBundle(LinkWithEditorActions.class).getString("Projects");
         } else return super.getValue(key);
      }
     
      
   }
   
   private static class LinkWithFilesAction
       extends AbstractLinkWithEditorAction{
      
      String getPathToAction() {
         return  "Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInFiles.instance";
      }
      
      
      
      String getTCId() {
          return "projectTab_tc";
      }
      
    
       public Object getValue(String key) {
         if (key.equals(AbstractAction.NAME)){
            return org.openide.util.NbBundle.
                getBundle(LinkWithEditorActions.class).getString("Files");
         } else return super.getValue(key);
      }
      
     
   }
   private static class LinkWithFavoritesAction
       extends AbstractLinkWithEditorAction{
      
      String getPathToAction() {
         return  "Actions/Window/SelectDocumentNode/org-netbeans-modules-favorites-Select.instance";
      }
      
      String getTCId() {
         return "favorites";
      }
      
      
      public Object getValue(String key) {
         if (key.equals(AbstractAction.NAME)){
            return org.openide.util.NbBundle.
                getBundle(LinkWithEditorActions.class).getString("Favorites");
         } else return super.getValue(key);
      }
      
      public HelpCtx getHelpCtx() {
         return HelpCtx.DEFAULT_HELP;
      }
      
   }
   private static class LinkWithNothingAction
       extends AbstractAction {
      
      public void actionPerformed(ActionEvent e) {
         //detach current
         detachCurrentListener();
      }
      
      /**
       *
       * Gets the <code>Object</code> associated with the specified key.
       *
       * @param key a string containing the specified <code>key</code>
       * @return the binding <code>Object</code> stored with this key; if there
       * 		are no keys, it will return <code>null</code>
       * @see Action#getValue
       */
      public Object getValue(String key) {
         if (key.equals(AbstractAction.NAME)){
            return org.openide.util.NbBundle.
                getBundle(LinkWithEditorActions.class).getString("Nothing");
         } else return super.getValue(key);
      }
      
   }
   
   
}

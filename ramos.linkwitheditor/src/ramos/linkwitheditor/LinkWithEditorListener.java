/*
 * NewClass.java
 *
 * Created on 4 de agosto de 2006, 20:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ramos.linkwitheditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author ramos
 */
public class LinkWithEditorListener implements PropertyChangeListener {
   public final static LinkWithEditorListener INSTANCE = new LinkWithEditorListener();
   public static LinkWithEditorListener getInstance(){
      return INSTANCE;
   }
   private static BaseDocument doc = null;
   private static Action action;
   private static PropertyChangeListener linkWithEditorListener = new LinkWithEditorListener();
   protected static TopComponent tc;
   private static String SLIDING = "Sliding";
   private LinkWithEditorListener(){
      
      try {
         if (action == null) {
            action = (Action) getTheObject(
                "Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInProjects.instance");
         }
      } catch (final DataObjectNotFoundException ex) {
         ex.printStackTrace();
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
      } catch (final IOException ex) {
         ex.printStackTrace();
      }
   }
   void attach(){
      TopComponent.getRegistry().addPropertyChangeListener(this);
   }
   void detach(){
      TopComponent.getRegistry().removePropertyChangeListener(this);
   }
   public static Object getTheObject(final String pathInSystemFilesystem)
   throws DataObjectNotFoundException, IOException, ClassNotFoundException {
      InstanceCookie ck;
      ck = (InstanceCookie) DataObject.find(Repository.getDefault()
      .getDefaultFileSystem()
      .getRoot()
      .getFileObject(pathInSystemFilesystem))
      .getCookie(InstanceCookie.class);
      
      return ck.instanceCreate();
   }
   /**
    * DOCUMENT ME!
    *
    * @param evt DOCUMENT ME!
    */
   public void propertyChange(final PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED)) {
         String modeName = WindowManager.getDefault().findMode(tc).getName();
         //tc.close();
         //System.out.println(tc.isOpened());
         if (modeName.contains(SLIDING) ||
             !tc.isVisible() ||
             !tc.isOpened()) return ;
         //System.out.println(WindowManager.getDefault().findTopComponent("projectTabLogical_tc"));
         BaseDocument selectedDoc = Registry.getMostActiveDocument();
         if (selectedDoc == null || selectedDoc.equals(doc)) return;
         Node[] selectedNodes = TopComponent.getRegistry().getCurrentNodes();
         if ((selectedNodes == null) || (selectedNodes.length == 0) ) {
            return;
         }
         
         DataObject dob1 = (DataObject) selectedNodes[0].getLookup()
         .lookup(DataObject.class);
         
         
         DataObject dob2 = NbEditorUtilities.getDataObject(selectedDoc);
         
         if (dob1 != null && dob1.equals(dob2)) {
            doc = selectedDoc;
            action.actionPerformed(null);
            Registry.getMostActiveComponent().requestFocus();
         }
      }
   }
}
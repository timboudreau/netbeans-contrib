package ramos.linkwitheditor;

import javax.swing.JMenuItem;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;

public final class LinkEditorWithAction extends CallableSystemAction{
   
   public void performAction() {
      // TODO implement action body
   }
   
   public String getName() {
      return NbBundle.getMessage(LinkEditorWithAction.class, "CTL_LinkEditorWithAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }

   public JMenuItem getMenuPresenter() {
     return LinkWithEditorMenu.getLinkWithEditorMenu();
   }
   
}

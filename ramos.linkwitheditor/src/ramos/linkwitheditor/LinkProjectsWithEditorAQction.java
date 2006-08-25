package ramos.linkwitheditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.openide.util.actions.CallableSystemAction;

public final class LinkProjectsWithEditorAQction  extends BooleanStateAction implements PropertyChangeListener {
  
   LinkProjectsWithEditorAQction(){
      addPropertyChangeListener(this);
   }
   
   public String getName() {
      return NbBundle.getMessage(LinkProjectsWithEditorAQction.class, "CTL_LinkProjectsWithEditorAQction");
   }
   
   protected String iconResource() {
      return "ramos/linkwitheditor/ResourceLink.gif";
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
   
   public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
         link(getBooleanState());
      }
   }
   
   private void link(boolean mark) {
      if (mark) {
         LinkWithEditorListener.getInstance().attach();
      } else {
         LinkWithEditorListener.getInstance().detach();
      }
   }
   protected void initialize() {
      super.initialize();
      setBooleanState(false);
   }
   
}

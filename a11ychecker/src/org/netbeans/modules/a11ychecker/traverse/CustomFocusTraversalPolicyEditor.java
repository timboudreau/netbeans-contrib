/*
 * CustomFocusTraversalPolicyEditor.java
 *
 * @author Michal Hapala, Pavel Stehlik
 */

package org.netbeans.modules.a11ychecker.traverse;

public class CustomFocusTraversalPolicyEditor extends javax.swing.JPanel{
    
    private MyGlassPane glassPane;
    
    public void forceComponentResized(java.awt.event.ComponentEvent evt) {
        if(glassPane != null) {
            glassPane.runCreate();
            glassPane.setVisible(true);
            //glassPane.setButtonsVisible(true);
        }
    }
    
    /** Creates a new instance of CustomFocusTraversalPolicyEditor */
    public CustomFocusTraversalPolicyEditor() {
        setLayout(new java.awt.BorderLayout());
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                forceComponentResized(evt);
            }
        });
    }
    
    public void setGlassPane(MyGlassPane glassPane){
            this.glassPane = glassPane;
    }
}

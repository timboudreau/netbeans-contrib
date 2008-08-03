/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UICommentWindow.java
 *
 * Created on 3.8.2008, 2:02:17
 */
package org.netbeans.modules.properties.rbe.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.properties.rbe.model.LocaleProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author denis
 */
public class UICommentWindow extends Dialog {

    protected LocaleProperty localeProperty;

    /** Creates new form UICommentWindow */
    public UICommentWindow(Frame owner, final LocaleProperty localeProperty) {
        super(owner);
        this.localeProperty = localeProperty;
        setLocationRelativeTo(owner);
        setModal(true);
        
        initComponents();
        textArea.setText(localeProperty.getComment());
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                localeProperty.setComment(textArea.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                localeProperty.setComment(textArea.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                localeProperty.setComment(textArea.getText());
            }
        });

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }

        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {


        jScrollPane1 = new JScrollPane();
        textArea = new JTextArea();

        setTitle(NbBundle.getMessage(UICommentWindow.class, "UICommentWindow.title")); // NOI18N
        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)

        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)

        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jScrollPane1;
    private JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
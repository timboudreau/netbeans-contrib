/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Jabber module.
 * The Initial Developer of the Original Code is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.netbeans.modules.jabber.Contact;
import org.netbeans.modules.jabber.Manager;
import org.netbeans.modules.jabber.MessageQueue;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * A nonmodal dialog displaying incomming message.
 * Features:<UL>
 *  <LI>Reply function
 *  <LI>Quote function
 *  <LI>Next - Displays a sequence of messages from given contact or from global queue.
 *  <LI>Add - add unknown user
 *  <LI>(autoclose)
 *  
 * @author  nenik
 */
final class MessageDisplayer extends javax.swing.JPanel {
    public static void displayMessage(Manager man, Contact from, Message m) {
        new MessageDisplayer(man, m, from).doShow();
    }

    JPanel contentPanel = new JPanel(new BorderLayout());
    
    Manager manager;
    
    Message message;

    Contact from; // can be null
    
    // the dialog we're displayed in
    Dialog d;

    
    /** Creates new form MessageDisplayer */
    private MessageDisplayer(Manager man, Message msg, Contact con) {
        manager = man;
        message = msg;
        from = con;
        initComponents();
        contentPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
    }
    
    private void setup() {
        contentPanel.removeAll();

        String jid = message.getFrom();
        fromField.setText(manager.getContactList().getLongDisplayName(jid));

        String subject = message.getSubject();
        if (subject == null) {
            subjectLabel.setVisible(false);
            subjectField.setVisible(false);
        } else {
            subjectField.setText(subject);
            subjectLabel.setVisible(true);
            subjectField.setVisible(true);
        }
        messageArea.setText(message.getBody());

        boolean replace = processExtensions();
        
        if (!replace) {
            contentPanel.add(this, BorderLayout.CENTER);
        }
        invalidate();
        revalidate();
        repaint();
    }
    
    private boolean processExtensions() {
        boolean replace = false; // TODO: allow whole UI replacement
        for (Iterator it = getExtensionDisplayers(message).iterator(); it.hasNext(); ) {
            ExtensionDisplayer dsp = (ExtensionDisplayer)it.next();
            Object pref = dsp.preferredPosition();
            
            if (pref == BorderLayout.CENTER) replace = true;

            // TODO: check the validity of provided position
            contentPanel.add(dsp.createPanel(manager, message), pref);
        }
        
        return replace;
    }
    
    
    private static Lookup.Result displayers;
    private static Map nsToDisplayer;
    private static Collection genericDisplayers;
    
    private Collection getExtensionDisplayers(Message msg) {
        if (displayers == null) {
            displayers = Lookup.getDefault().lookup(new Lookup.Template(ExtensionDisplayer.class));
            displayers.addLookupListener(new LookupListener() {
                public void resultChanged (LookupEvent ev) {
                    updateLookupResults();
                }
            });
            updateLookupResults();
        }
        
        List results = new ArrayList();
        for (Iterator it = msg.getExtensions(); it.hasNext(); ) {
            PacketExtension ext = (PacketExtension)it.next();
            String ns = ext.getNamespace();
            Object o = nsToDisplayer.get(ns);
            if (o != null) results.add(o);
        }
        
        for (Iterator it = genericDisplayers.iterator(); it.hasNext(); ) {
            ExtensionDisplayer dsp = (ExtensionDisplayer)it.next();
            if (dsp.accept(manager, msg)) results.add(dsp);
        }
        return results;
    }
    
    private static void updateLookupResults() {
        Collection all = displayers.allInstances();
        nsToDisplayer = new HashMap();
        genericDisplayers = new ArrayList();
        for (Iterator it = all.iterator(); it.hasNext();) {
            ExtensionDisplayer dsp = (ExtensionDisplayer)it.next();
            String ns = dsp.extensionNamespace();
            if (ns == null) {
                genericDisplayers.add(dsp);
            } else {
                nsToDisplayer.put(ns, dsp);
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        replyButton = new javax.swing.JButton();
        quoteButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        fromLabel = new javax.swing.JLabel();
        fromField = new javax.swing.JTextField();
        subjectLabel = new javax.swing.JLabel();
        subjectField = new javax.swing.JTextField();
        messageScroll = new javax.swing.JScrollPane();
        messageArea = new javax.swing.JTextArea();

        replyButton.setLabel("Reply");
        quoteButton.setLabel("Quote");
        nextButton.setLabel("Next");
        closeButton.setLabel("Close");

        setLayout(new java.awt.GridBagLayout());

        setMinimumSize(new java.awt.Dimension(400, 300));
        fromLabel.setLabelFor(fromField);
        fromLabel.setText("From: ");
        fromLabel.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(fromLabel, gridBagConstraints);

        fromField.setEditable(false);
        fromField.setBorder(null);
        fromField.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(fromField, gridBagConstraints);

        subjectLabel.setLabelFor(subjectField);
        subjectLabel.setText("Subject: ");
        subjectLabel.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(subjectLabel, gridBagConstraints);

        subjectField.setEditable(false);
        subjectField.setBorder(null);
        subjectField.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(subjectField, gridBagConstraints);

        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setPreferredSize(new java.awt.Dimension(400, 300));
        messageScroll.setViewportView(messageArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(messageScroll, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField fromField;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JTextArea messageArea;
    private javax.swing.JScrollPane messageScroll;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton quoteButton;
    private javax.swing.JButton replyButton;
    private javax.swing.JTextField subjectField;
    private javax.swing.JLabel subjectLabel;
    // End of variables declaration//GEN-END:variables
    
    boolean hasNextMessage() {
        MessageQueue queue = manager.getMessageQueue();
        return from == null ?
                queue.getMessageCount() > 0 :
                queue.getMessageCountFrom(from.getJid()) > 0;                
    }
    
    void updateNextButton() {
        nextButton.setEnabled(hasNextMessage());
    }
    
    void showNextMessage() {
        MessageQueue queue = manager.getMessageQueue();
        Message next = from == null ? 
                queue.nextMessage() :
                queue.nextMessageFrom(from.getJid());
                
        assert next != null : "No race condition should occur in single UI";

        message = next;
        setup();
        updateNextButton();
    }

    
    void doShow() {
        Lst listener = new Lst();
        
        manager.getMessageQueue().addListener(listener);
        updateNextButton();
        
        String title = from == null ? "Incomming message" : "Message from " + fromField.getText();
        
        DialogDescriptor desc = new DialogDescriptor(contentPanel, title, false,
            new Object[] {replyButton, quoteButton, nextButton, closeButton}, closeButton, 
            DialogDescriptor.BOTTOM_ALIGN, null, listener);
        
        setup();
        
        d = DialogDisplayer.getDefault().createDialog(desc);
        d.addWindowListener(listener);
        d.show();
        
        replyButton.requestFocusInWindow();
    }
    
    private String createReplyMessage(String original) {
        StringBuffer reply = new StringBuffer();
        StringTokenizer st = new StringTokenizer(original, "\n");
        
        while (st.hasMoreTokens()) reply.append("> ").append(st.nextToken()).append("\n");
        
        return reply.toString();
    }
    
    // User just sent a reply, maybe close this displayer
    void replySent() {
        if (d.isShowing()) {
            if (hasNextMessage()) {
                showNextMessage();
            } else {
                d.dispose();
            }
        }
    }
    
    private class Lst extends java.awt.event.WindowAdapter implements
                    MessageQueue.Listener, Runnable, ActionListener {
        
        public void messageReceived(String fromJID) {
            if (from == null || from.getJid().equals(fromJID))
                SwingUtilities.invokeLater(this);
        }
        
        public void messageRemoved(String fromJID) {
            if (from == null || from.getJid().equals(fromJID))
                SwingUtilities.invokeLater(this);
        }
        
        /** check the availability of next message in AWT thread,
         * update the next button state accordingly.
         */
        public void run() {
            updateNextButton();
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            Object src = e.getSource();
            
            if (src == replyButton) {
                MessageComposer.composeMessage(manager, 
                        new String[] { message.getFrom()}, "", MessageDisplayer.this);
            } else if (src == quoteButton) {
                MessageComposer.composeMessage(manager, 
                        new String[] { message.getFrom()},
                        createReplyMessage(message.getBody()),
                        MessageDisplayer.this);
            } else if (src == nextButton) {
                showNextMessage();
            } else if (src == closeButton) {
                d.dispose();
            }
        }
        
        public void windowClosed(java.awt.event.WindowEvent e) {
            // unregister myself
            manager.getMessageQueue().removeListener(this);
            d.removeWindowListener(this); // necessary?
        }
    }   
}

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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.netbeans.modules.jabber.Contact;
import org.netbeans.modules.jabber.Manager;
import org.netbeans.modules.jabber.remote.LocalManager;

/**
 *
 * @author  nenik
 */
public final class Actions {
    
    /** No instances - a factory only*/
    private Actions() {
        assert false;
    }
    
    private static final Action sendMessage = new SendMessageAction();
    private static final Action readMessage = new ReadMessageAction();
    private static final Action removeContact = new RemoveContactAction();
    private static final Action presenceRequest = new PresenceAction(PresenceAction.REQUEST);
    private static final Action presenceGrant = new PresenceAction(PresenceAction.GRANT);
    private static final Action presenceRevoke = new PresenceAction(PresenceAction.REVOKE);
    private static final Action remoteShow = new RemoteShowAction();
    private static final Action remoteHelp = new RemoteHelpAction();
    
    public static Action getSendMessageAction() { return sendMessage; }
    public static Action getReadMessageAction() { return readMessage; }
    public static Action getRemoveContactAction() { return removeContact; }
    public static Action getPresenceRequestAction() { return presenceRequest; }
    public static Action getPresenceGrantAction() { return presenceGrant; }
    public static Action getPresenceRevokeAction() { return presenceRevoke; }
    public static Action getRemoteShowAction() { return remoteShow; }
    public static Action getRemoteHelpAction() { return remoteHelp; }
    
    
    /**
     * An always enabled action, which invokes UI for sending message
     * to 1-N contacts 
     */
    private static final class SendMessageAction extends ContactAction {
        
        SendMessageAction() {
            super("Send message");
        }
        
        protected void performAction(Contact[] contacts) {
            String[] jids = new String[contacts.length];
            for (int i=0; i<contacts.length; i++) {
                jids[i] = contacts[i].getJid();
            }
            MessageComposer.composeMessage(contacts[0].getManager(), jids, "", null);
        }
    }
    
    /**
     * An action enabled only when there is a pending message from one contact.
     */
    private static final class ReadMessageAction extends ContactAction {
        
        ReadMessageAction() {
            super("Read message");
        }
        
        protected boolean isEnabled(Contact[] contacts) {
            return (contacts.length == 1) && contacts[0].isPendingMessage();
        }
        
        protected void performAction(Contact[] contacts) {
            assert contacts.length == 1;
            
            Contact con = contacts[0];
            Message head = con.nextMessage();
            
            assert head != null;

            showMessage(con, head);
        }
        
        private void showMessage(Contact con, Message head) {
            MessageDisplayer.displayMessage(con.getManager(), con, head);
        }
        
    }
    
    private static final class RemoveContactAction extends ContactAction {
        RemoveContactAction() {
            super("Remove contact");
        }
        
        protected void performAction(Contact[] contacts) {
            assert contacts.length > 0;
            
            RemoveContactUI.removeContacts(contacts);
        }        
    }
    
    private static final class PresenceAction extends ContactAction {
        public static final int REQUEST = 0;
        public static final int GRANT = 1;
        public static final int REVOKE = 2;
        
        private static final String[] LABELS = new String[] {
            "Request presence notifications",
            "Grant presence notifications",
            "Revoke presence notifications"
        };
        
        private int type;
        PresenceAction(int type) {
            super(LABELS[type]);
            this.type = type;
        }
        
        protected void performAction(Contact[] contacts) {
            assert contacts.length > 0;
            for (int i=0; i<contacts.length; i++) {
                switch(type) {
                    case REQUEST:
                        contacts[i].requestPresence();
                        break;
                        
                    case GRANT:
                        contacts[i].grantPresence();
                        break;
                        
                    case REVOKE:
                        contacts[i].revokePresence();
                        break;
                        
                    default:
                        assert false;
                }
            }
        }        
        
    }

    private static final class StopUIServerAction extends AbstractAction {
        JabberUI ui;
        int port;

        StopUIServerAction(JabberUI ui, int port, String label) {
            super(label);
            this.ui = ui;
            this.port = port;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            LocalManager.getDefault().stopSocketAcceptor(port);
            ui.removeAction(this);
        }
        
    }
    
    private static final class RemoteShowAction extends ContactAction {
        RemoteShowAction() {
            super("Send UI show offer");
        }

        private String encodeKey(byte[] bytes) {
            // XXX: promised hex-string, using base64...
            return StringUtils.encodeBase64(bytes);
        }
        
        protected void performAction(Contact[] contacts) {
            assert contacts.length > 0;
            
            Manager man = contacts[0].getManager();
            
            byte[] key = new byte[32];
            new Random(System.currentTimeMillis() * contacts.hashCode()).nextBytes(key);
            
            int port = 0;
            try {
                port  = LocalManager.getDefault().startSocketAcceptor(key, false);
                JabberUI ui = JabberUI.forManager(man);
                ui.addAction(new StopUIServerAction(ui, port, "Stop UI Show"));
            } catch (IOException ioe) {
                System.err.println("Can't open server socket");
                return;
            }
            
            String socket;
            try {
                socket = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            } catch (UnknownHostException uhe) {
                socket = "127.0.0.1:" + port;
            }
            
            for (int i=0; i<contacts.length; i++) {
                Message msg = new Message(contacts[i].getJid());
                msg.setBody("UI Broadcast");
                
                DefaultPacketExtension ext = new DefaultPacketExtension("x", "netbeans:x:remote:show");
                ext.setValue("socket", socket);
                ext.setValue("key", encodeKey(key));
                msg.addExtension(ext);

                contacts[i].sendMessage(msg);
            }
        }
    }

    private static final class RemoteHelpAction extends ContactAction {
        RemoteHelpAction() {
            super("Ask for assistence");
        }

        private String encodeKey(byte[] bytes) {
            // XXX: promised hex-string, using base64...
            return StringUtils.encodeBase64(bytes);
        }

        protected boolean isEnabled(Contact[] contacts) {
            return (contacts.length == 1);
        }
        
        protected void performAction(Contact[] contacts) {
            assert contacts.length == 1;

            Manager man = contacts[0].getManager();

            byte[] key = new byte[32];
            new Random(System.currentTimeMillis() * contacts.hashCode()).nextBytes(key);
            
            int port = 0;
            try {
                port  = LocalManager.getDefault().startSocketAcceptor(key, true);
                JabberUI ui = JabberUI.forManager(man);
                ui.addAction(new StopUIServerAction(ui, port, "Stop remote assistence"));
            } catch (IOException ioe) {
                System.err.println("Can't open server socket");
                return;
            }
            
            String socket;
            try {
                socket = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            } catch (UnknownHostException uhe) {
                socket = "127.0.0.1:" + port;
            }
            
            Message msg = new Message(contacts[0].getJid());
            msg.setBody("Remote assitence request");

            DefaultPacketExtension ext = new DefaultPacketExtension("x", "netbeans:x:remote:help");
            ext.setValue("socket", socket);
            ext.setValue("key", encodeKey(key));
            msg.addExtension(ext);

            contacts[0].sendMessage(msg);
        }
    }

}

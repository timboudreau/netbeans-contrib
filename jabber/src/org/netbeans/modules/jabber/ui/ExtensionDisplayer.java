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

import javax.swing.JComponent;
import org.jivesoftware.smack.packet.Message;
import org.netbeans.modules.jabber.Manager;

/**
 * This interface is a receipt how to display extension of an incomming message.
 * When a message is display using MessageDisplayer, MessageDisplayer finds all
 * relevant ExtensionDisplayers in the global lookup, asks them for their UI
 * and add the provided UI to the final dialog. The ExtensionDisplayer can hint
 * where should its UI part be placed using constants from BorderLayout,
 * but the MessageDisplayer is free to display it differently.
 *
 * @author  nenik
 */
public interface ExtensionDisplayer {
    
    /** Export the supported namespace, like "jabber:x:oob".
     * For common cases only one namespace is supported. The implementation
     * can return null, in which case the accept method will be used to check
     * whether to use this ExtensionDisplayer.
     * This method have to return consistent value, as its result may be cached
     *
     * @return supported namespace or null
     */
    public String extensionNamespace();
    
    /** Called only if the extensionNamespace returns null.
     * In that case the method is called for every displayed message
     * and can thus decide on a message by message basis.
     *
     * @param man the manager in whose context the massage come.
     * @param msg the incomming message
     *
     * @return whether this ExtensionDisplayer should be used when constructing
     * UI for given message.
     */
    public boolean accept(Manager man, Message msg);
    
    
    /**
     * Provides a hint where should the UI be placed relative to the message body.
     *
     * @return one of the positional constants from BorderLayout
     */
    public Object preferredPosition();

    /** Create the UI for displaying given message in the context of given manager.
     * The constructed UI should be self-encapsulated as no other callback
     * will be provided except removeNotify().
     */
    public JComponent createPanel(Manager man, Message msg);
    
}

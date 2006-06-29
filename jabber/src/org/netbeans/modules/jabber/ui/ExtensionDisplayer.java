/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
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

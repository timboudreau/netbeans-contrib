/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

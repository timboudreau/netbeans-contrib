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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.commands;

import java.util.Hashtable;

import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.util.NotifyDescriptorInputPassword;
import org.openide.DialogDisplayer;

/**
 * This command sets a new password to the FS password property
 * @author  Martin Entlicher
 */
public class SetPasswordCommand extends Object implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem;

    /** Creates new SetPasswordCommand */
    public SetPasswordCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        
        String passwdMsg = NbBundle.getMessage(SetPasswordCommand.class, "MSG_Password"); // NOI18N
        char passwdMsg_mnemonic = NbBundle.getMessage(SetPasswordCommand.class, "MSG_Password_mnemonic").charAt(0); // NOI18N
        String passwdTitle = NbBundle.getMessage(SetPasswordCommand.class, "TITL_Password"); // NOI18N
        NotifyDescriptorInputPassword nd = new NotifyDescriptorInputPassword (passwdMsg, passwdTitle,passwdMsg_mnemonic);
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
            fileSystem.setPassword(nd.getInputText());
        }
        return true;
    }
    
}

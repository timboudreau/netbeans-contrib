/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.IOException;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

/**
 * Retrieves the Relevant_Mask varaible and adjust it's value with files
 * that are created by NetBeans.
 * 
 * @author  Martin Entlicher
 */
public class GetAdjustedRelevantMasks extends Object implements VcsAdditionalCommand {
    
    public static final String RELEVANT_MASKS = "Relevant_Masks"; // NOI18N
    // the second should be "!.nbintdb" but according to VSS 6.0 template must contan at least one wildcard
    private static final String[] NB_RELEVANT_MASKS = { "!*~", "!.nbint?b" }; // NOI18N
    
    /** Creates a new instance of GetAdjustedRelevantMasks */
    public GetAdjustedRelevantMasks() {
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
     *         false if some error occured.
     */
    public boolean exec(final Hashtable vars, final String[] args,
                        final CommandOutputListener stdoutListener, final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, final String dataRegex,
                        final CommandDataOutputListener stderrDataListener, final String errorRegex) {
        String ssDir = (String) vars.get("ENVIRONMENT_VAR_SSDIR"); // NOI18N
        String userName = (String) vars.get("USER_NAME"); // NOI18N
        if (userName == null || userName.length() == 0) {
            userName = System.getProperty("user.name");
        }
        String relevantMasks;
        try {
            relevantMasks = GetInitializationVariable.getVariable(ssDir, userName, RELEVANT_MASKS);
        } catch (IOException ioex) {
            relevantMasks = null;
        }
        if (relevantMasks == null) {
            relevantMasks = ""; // NOI18N
        }
        //System.out.println("Original relevant masks = '"+relevantMasks+"'");
        for (int i = 0; i < NB_RELEVANT_MASKS.length; i++) {
            if (relevantMasks.indexOf(NB_RELEVANT_MASKS[i]) < 0) {
                if (relevantMasks.length() > 0) {
                    relevantMasks += ", ";
                }
                relevantMasks += NB_RELEVANT_MASKS[i];
            }
        }
        //System.out.println("New relevant masks = '"+relevantMasks+"'");
        stdoutDataListener.outputData(new String[] { relevantMasks });
        return true;
    }
}

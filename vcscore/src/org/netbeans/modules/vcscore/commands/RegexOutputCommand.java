/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import org.netbeans.api.vcs.commands.Command;

/**
 * This class represents a command which produce elements of parsed data from
 * a text output of the command using a regular expression.
 *
 * @author  Martin Entlicher
 */
public interface RegexOutputCommand extends Command {
    
    /**
     * Set the regular expression to parse the standard output.
     * @param regex The regular expression
     */
    public void setStandardRegex(String regex);
    
    /**
     * Get the regular expression to parse the standard output.
     * @return The regular expression
     */
    public String getStandardRegex(String regex);
    
    /**
     * Set the regular expression to parse the error output.
     * @param regex The regular expression
     */
    public void setErrorRegex(String regex);
    
    /**
     * Get the regular expression to parse the error output.
     * @return The regular expression
     */
    public String getErrorRegex(String regex);
    
    /**
     * Add a text listener to the standard output of the command.
     */
    public void addRegexOutputListener(RegexOutputListener listener);
    
    /**
     * Remove a text listener from the standard output of the command.
     */
    public void removeRegexOutputListener(RegexOutputListener listener);
    
    /**
     * Add a text listener to the error output of the command.
     */
    public void addRegexErrorListener(RegexErrorListener listener);
    
    /**
     * Remove a text listener from the error output of the command.
     */
    public void removeRegexErrorListener(RegexErrorListener listener);
    
}

/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 * 
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;

import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.versioning.RevisionList;

public class TeamwareHistoryCommand implements VcsAdditionalCommand {

    private static void append(StringBuffer sb, String s, int width) {
        sb.append(s);
        for (int i = width - s.length(); i > 0; i--) {
            sb.append(" ");
        }
    }
    
    public boolean exec(final Hashtable vars, String[] args,
                        final CommandOutputListener stdout,
                        final CommandOutputListener stderr,
                        final CommandDataOutputListener stdoutData, String dataRegex,
                        final CommandDataOutputListener stderrData, String errorRegex) {

        File file = TeamwareSupport.getFile(vars);
        SFile sFile = new SFile(file);
        RevisionList revisionList = sFile.getRevisions();
        int revWidth = 0;
        int dateWidth = 0;
        int whoWidth = 0;
        for (Iterator i = revisionList.iterator(); i.hasNext();) {
            SRevisionItem item = (SRevisionItem) i.next();
            revWidth = Math.max(revWidth, item.getRevision().length());
            dateWidth = Math.max(dateWidth, item.getDate().length());
            whoWidth = Math.max(whoWidth, item.getAuthor().length());
        }
        StringBuffer indent = new StringBuffer("\n      ");
        for (int i = 0; i  < revWidth + dateWidth + whoWidth; i++) {
            indent.append(" ");
        }
        for (Iterator i = revisionList.iterator(); i.hasNext();) {
            SRevisionItem item = (SRevisionItem) i.next();
            StringBuffer sb = new StringBuffer();
            append(sb, item.getRevision(), revWidth);
            sb.append("  ");
            append(sb, item.getDate(), dateWidth);
            sb.append("  ");
            append(sb, item.getAuthor(), whoWidth);
            sb.append("  ");
            String message = item.getMessage();
            sb.append(message.replaceAll("\n", indent.toString()));
            stdout.outputLine(sb.toString());
        }
        return true;
    }

}

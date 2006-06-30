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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.versioning.RevisionList;

public class TeamwareHistoryCommand implements VcsAdditionalCommand {

    private static void append(StringBuffer sb, String s, int width) {
        if (s == null) {
            for (int i = 0; i < width; i++) {
                sb.append(" ");
            }
        } else {
            sb.append(s);
            for (int i = width - s.length(); i > 0; i--) {
                sb.append(" ");
            }
        }
    }
    
    public boolean exec(final Hashtable vars, String[] args,
                        final CommandOutputListener stdout,
                        final CommandOutputListener stderr,
                        final CommandDataOutputListener stdoutData, String dataRegex,
                        final CommandDataOutputListener stderrData, String errorRegex) {

        File file = TeamwareSupport.getFile(vars);
        SFile sFile = new SFile(file);
        SortedSet revisions = sFile.getExternalRevisions();
        int revWidth = 0;
        int dateWidth = 0;
        int whoWidth = 0;
        for (Iterator i = revisions.iterator(); i.hasNext();) {
            SRevisionItem item = (SRevisionItem) i.next();
            String rev = item.getRevision();
            String date = item.getDate();
            String who = item.getAuthor();
            if (rev != null) {
                revWidth = Math.max(revWidth, rev.length());
            }
            if (date != null) {
                dateWidth = Math.max(dateWidth, date.length());
            }
            if (who != null) {
                whoWidth = Math.max(whoWidth, who.length());
            }
        }
        StringBuffer indent = new StringBuffer("\n      ");
        for (int i = 0; i  < revWidth + dateWidth + whoWidth; i++) {
            indent.append(" ");
        }
        for (Iterator i = revisions.iterator(); i.hasNext();) {
            SRevisionItem item = (SRevisionItem) i.next();
            StringBuffer sb = new StringBuffer();
            append(sb, item.getRevision(), revWidth);
            sb.append("  ");
            append(sb, item.getDate(), dateWidth);
            sb.append("  ");
            append(sb, item.getAuthor(), whoWidth);
            sb.append("  ");
            String message = item.getMessage();
            if (message != null) {
                sb.append(message.replaceAll("\n", indent.toString()));
            }
            stdout.outputLine(sb.toString());
        }
        return true;
    }

}

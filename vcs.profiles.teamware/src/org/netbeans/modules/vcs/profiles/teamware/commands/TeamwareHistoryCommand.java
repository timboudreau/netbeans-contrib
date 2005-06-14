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
        SortedSet revisions = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                SRevisionItem rev1 = (SRevisionItem) o1;
                SRevisionItem rev2 = (SRevisionItem) o2;
                long diff = rev1.getLongDate() - rev2.getLongDate();
                return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
            }
        });
        for (Iterator i = sFile.getRevisions().iterator(); i.hasNext();) {
            SRevisionItem item = (SRevisionItem) i.next();
            if (item.getDate() != null) {
                revisions.add(item);
            }
        }
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

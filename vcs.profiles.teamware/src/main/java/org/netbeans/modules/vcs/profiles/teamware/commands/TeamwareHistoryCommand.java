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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
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

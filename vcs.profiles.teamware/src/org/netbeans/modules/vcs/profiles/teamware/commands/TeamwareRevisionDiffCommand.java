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

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.windows.TopComponent;

import org.netbeans.api.diff.Diff;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

public class TeamwareRevisionDiffCommand implements VcsAdditionalCommand {

    public boolean exec(final Hashtable vars, String[] args,
                        final CommandOutputListener stdout,
                        final CommandOutputListener stderr,
                        final CommandDataOutputListener stdoutData, String dataRegex,
                        final CommandDataOutputListener stderrData, String errorRegex) {

        File file = TeamwareSupport.getFile(vars);
        FileObject fo = FileUtil.toFileObject(file);
        String MIMEType = fo.getMIMEType();
        SFile sFile = new SFile(file);
        SRevisionItem revision1 = sFile.getRevisions()
            .getRevisionByName((String) vars.get("REVISION1"));
        SRevisionItem revision2 = sFile.getRevisions()
            .getRevisionByName((String) vars.get("REVISION2"));
        Component c = null;
        try {
            if (revision2 == null) {
                // diff between current version and specified revision
                String name1 = file.getName();
                String name2 = name1 + ": " + revision1.getRevision();
                c = Diff.getDefault().createDiff(
                    name1, name1, new FileReader(file),
                    name2, name2,
                    new StringReader(sFile.getAsString(revision1, true)),
                    MIMEType);
            } else {
                String name1 = file.getName() + ": " + revision1;
                String name2 = file.getName() + ": " + revision2;
                c = Diff.getDefault().createDiff(
                    name1, name1,
                    new StringReader(sFile.getAsString(revision1, true)),
                    name2, name2,
                    new StringReader(sFile.getAsString(revision2, true)),
                    MIMEType);
            }
            if (c != null) {
                ((TopComponent) c).open();
            }
            return true;
        } catch (IOException e) {
            stderr.outputLine(e.toString());
            return false;
        }
    }

}

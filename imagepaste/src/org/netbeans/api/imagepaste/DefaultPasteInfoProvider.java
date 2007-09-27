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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.api.imagepaste;

import java.io.File;
import org.netbeans.api.imagepaste.PasteInfo;
import org.netbeans.api.imagepaste.PasteInfoProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class DefaultPasteInfoProvider implements PasteInfoProvider {
    private final String template;
    private final String relativeDir;
    protected DefaultPasteInfoProvider(String template, String relativeDir) {
        this.template = template;
        this.relativeDir = relativeDir;
    }
    
    protected DefaultPasteInfoProvider(String template) {
        this (template, null);
    }

    public final PasteInfo getPasteInfo(File pastedInto) {
        String title = askTitle();
        if (title == null) {
            return null;
        }
        String fileFormat = getOutputFormat();
        String filename = titleToFilename(title) + '.' + fileFormat; //NOI18N
        File dir = pastedInto.getParentFile();
        String rDir = getRelativePath(pastedInto);
        if (rDir != null) {
            String[] s = rDir.split("/"); //NOI18N
            for (int i=0; i < s.length; i++) {
                dir = new File (dir, s[i]);
            }
        }
        File destFile = new File (dir, filename);
        String textToPaste = substText(title, filename, rDir);
        return PasteInfo.create(destFile, fileFormat, textToPaste, title);
    }
    
    protected String getRelativePath(File pastedInto) {
        return relativeDir;
    }
    
    protected String getOutputFormat() {
        return "png"; //NOI18N
    }
    
    private String substText(String title, String filename, String rDir) {
        String dir = rDir;
        if (!dir.endsWith("/")) { //NOI18N
            dir += "/"; //NOI18N
        }
        String relpath = dir + filename;
        String result = template.replace("$TITLE", title).replace("$SHORT_TITLE", //NOI18N
                title).replace("$FILENAME", relpath).replace("$ID", filename); //NOI18N
        return result;
    }
    
    private String titleToFilename(String s) {
        StringBuilder sb = new StringBuilder();
        char[] c = s.toLowerCase().toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') { //NOI18N
                sb.append ('_'); //NOI18N
            } else if (Character.isLetterOrDigit(c[i])) {
                sb.append (c[i]);
            }
        }
        return sb.toString();
    }

    private String askTitle() {
        InputLine line = new NotifyDescriptor.InputLine (NbBundle.getMessage(
                DefaultPasteInfoProvider.class, 
                "Enter_the_title_for_this_image"), //NOI18N
                NbBundle.getMessage(ImageTransferHandler.class, 
                "Import_Image")); //NOI18N
        String inputText = null;

        boolean ok = true;
        while (ok && inputText == null) {
            ok = InputLine.OK_OPTION.equals(
                    DialogDisplayer.getDefault().notify(line));
            if (ok) {
                inputText = titleToFilename(
                        line.getInputText().trim()).length() == 0 ?
                        null : line.getInputText();
                line.setInputText(NbBundle.getMessage(ImageTransferHandler.class, 
                        "enter_more_text")); //NOI18N
//            } else {
//                break;
            }
        }
        //XXX check valid filenames, duplicates
        if (ok && inputText != null) {
            return inputText;
        }
        return null;
    }
    

}

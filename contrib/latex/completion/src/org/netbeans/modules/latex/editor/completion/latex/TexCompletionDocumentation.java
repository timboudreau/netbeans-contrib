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
package org.netbeans.modules.latex.editor.completion.latex;

import java.net.URL;
import javax.swing.Action;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.modules.latex.editor.completion.latex.help.InstallHelp;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author Jan Lahoda
 */
public class TexCompletionDocumentation implements CompletionDocumentation {

    private String text;

    /** Creates a new instance of TexCompletionDocumentation */
    public TexCompletionDocumentation(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public URL getURL() {
        return null;
    }

    public CompletionDocumentation resolveLink(String link) {
        if (TexCompletionJavaDoc.HELP_NOT_INSTALLED_LINK.equals(link)) {
            //install help:
            Completion.get().hideAll();
            InstallHelp.installHelp();
        }
        return null;
    }

    public Action getGotoSourceAction() {
        return null;
    }
    
}

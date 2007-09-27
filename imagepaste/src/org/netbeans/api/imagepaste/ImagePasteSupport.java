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

import javax.swing.JEditorPane;
import javax.swing.TransferHandler;

/**
 * Create a TransferHandler which can handle the pasting of images from the
 * clipboard.  The PasteInfoProvider passed to the factory method will be
 * queried for things like where the image file from the clipboard data
 * should be put and what text to insert into the document.
 * <p>
 * Typical usage is, for example, allowing the user to paste the image
 * into an HTML document:  Ask the user what the title of the image should
 * be, then write the image from the clipboard into the same directory as
 * the file.
 *
 * @author Tim Boudreau
 */
public class ImagePasteSupport {
    /**
     * Create a transfer handler for handling clipboard operations on this
     * JEditorPane.
     * @param pane A JEditorPane that should be able to handle image pasting
     * @param provider An object which provides the necessary information for
     *        creating a file from the clipboard contents and inserting text
     *        into the document.
     */ 
    public static TransferHandler createTransferHandler(JEditorPane pane, PasteInfoProvider provider) {
        if (pane == null) throw new NullPointerException("Null pane"); //NOI18N
        if (provider == null) throw new NullPointerException ("Null PasteInfoProvider"); //NOI18N
        return new TextAndImageTransferHandler(pane, provider);
    }
    
    /**
     * Create a TransferHandler which will create png files in a directory called
     * &lt;images&gt; relative to the file the editor pane is editing, and use
     * the template text for generating the text to paste.  The resulting transfer
     * handler will pop up a dialog to ask the user for the image title.
     * <p>
     * The template string should be in the format of whatever file is being
     * edited.  The following strings will be replaced in the template to 
     * generate the code that is pasted:
     * <table>
     * <tr><th>Template String</th><th>Will be replaced with</th></tr>
     * <tr><td>$FILENAME</td><td>The relative path to the image file that is created</th></tr>
     * <tr><td>$TITLE</td><td>The title of the image</td></tr>
     * <tr><td>$SHORT_TITLE</td><td>The title of the image</td></tr>
     * <tr><td>$ID</td><td>The name of the destination file</td></tr>
     * </table>
     * For example, the DocBook template looks like this:
     * <pre>
     *      &lt;figure id="$ID"&gt;  
     *          &lt;title&gt;$TITLE&lt;/title&gt;  
     *          &lt;titleabbrev&gt;$SHORT_TITLE&lt;/titleabbrev&gt;  
     *          &lt;mediaobject&gt;  
     *             &lt;imageobject&gt;  
     *                 &lt;imagedata fileref="$FILENAME"/&gt;  
     *             &lt;/imageobject&gt;  
     *         &lt;/mediaobject&gt;  
     *      &lt;/figure&gt;
     * </pre>
     * @param pane The editor pane in question
     * @param A template string to use for pasting
     */ 
    public static TransferHandler createTransferHandler(JEditorPane pane, String template) {
        return createTransferHandler(pane, new DefaultPasteInfoProvider(template, "images")); //NOI18N
    }
}

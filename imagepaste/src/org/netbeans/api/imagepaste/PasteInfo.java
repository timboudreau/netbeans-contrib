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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.api.imagepaste;

import java.io.File;

/**
 * Data used by the image pasting infrastructure to decide where to paste an
 * image, what to insert into the document, etc.
 *
 * @author Tim Boudreau
 */
public final class PasteInfo {

    final String imgFormat;
    final String pasteText;
    final String shortTitle;
    final String longTitle;
    final File destFile;

    private PasteInfo(File destFile, String imgFormat, String pasteText, String longTitle, String shortTitle) {
        if (pasteText == null) {
            throw new NullPointerException("Null paste text"); //NOI18N
        }
        if (longTitle == null) {
            throw new NullPointerException("Null long title"); //NOI18N
        }
        this.imgFormat = imgFormat == null ? "png" : imgFormat;
        this.pasteText = pasteText;
        this.shortTitle = shortTitle == null ? longTitle : shortTitle;
        this.longTitle = longTitle;
        this.destFile = destFile;
    }

    /**
     * Create a new PasteInfo.
     * 
     * @param destFile The file the image data should be written to.  The file
     *                 does not need to already exist on disk - if it does not,
     *                 it and anyneeded parent directories will be created.
     *                 May not benull.
     * @param imgFormat An image format such as is supported by ImageIO.  If
     *                  null, &quot;png&quot; is used.
     * @param pasteText The text that should be inserted into the document to 
     *                  reference the image
     * @param longTitle The title for this image.  May not be null.
     * @param shortTitle An abbreviated title.  If null, the value of longTitle
     *                   is used.
     */
    public static PasteInfo create(File destFile, String imgFormat, String pasteText, String longTitle, String shortTitle) {
        return new PasteInfo(destFile, imgFormat, pasteText, shortTitle, longTitle);
    }

    /**
     * Create a new PasteInfo.
     * 
     * @param destFile The file the image data should be written to.  The file
     *                 does not need to already exist on disk - if it does not,
     *                 it and anyneeded parent directories will be created.
     *                 May not benull.
     * @param imgFormat An image format such as is supported by ImageIO.  If
     *                  null, &quot;png&quot; is used.
     * @param pasteText The text that should be inserted into the document to 
     *                  reference the image
     * @param title The title for this image.  May not be null.
     */
    public static PasteInfo create(File destFile, String imgFormat, String pasteText, String title) {
        return new PasteInfo(destFile, imgFormat, pasteText, title, title);
    }
    
    public String toString() {
        return destFile.getPath() + ':' + longTitle + ":" + shortTitle + ":" +
                imgFormat + ":" + pasteText;
    }
}

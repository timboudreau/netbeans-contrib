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
package org.netbeans.modules.latex.ui.viewer;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import org.netbeans.modules.latex.ui.IconsCreator;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class Renderer {

    private static final Renderer INSTANCE = new Renderer();

    public static Renderer getDefault() {
        return INSTANCE;
    }

    /** Creates a new instance of Renderer */
    private Renderer() {
    }

    private File getIconDirectory() {
        File iconDir = new File(new File(new File(new File(System.getProperty("netbeans.user"), "var"), "cache"), "latex"), "viewer");
        
        iconDir.mkdirs();
        
        return iconDir;
    }

    private File getCache(File original, int page, int resolution) {
        String originalName = original.getAbsolutePath().replace(File.separatorChar, '_');
        return new File(getIconDirectory(), originalName + "-" + page + "-r" + resolution + ".png");
    }

    private void createPNG(File original, int page, int resolution) throws IOException, InterruptedException {
        Process gs = Runtime.getRuntime().exec(new String[] {
            "gs",
            "-sDEVICE=png16m",
            "-dBATCH",
            "-dNOPAUSE",
            "-dGraphicsAlphaBits=4",
            "-dTextAlphaBits=4",
            "-dEPSFitPage",
            "-dFirstPage=" + page,
            "-dLastPage=" + page,
            "-sOutputFile=" + getCache(original, page, resolution).getAbsolutePath(),
            "-r" + resolution,
            original.getAbsolutePath()
        });
        
        IconsCreator.waitFor(gs);
    }

    public Image getImage(FileObject source, int page, int resolution) {
        try {
            File sourceFile = FileUtil.toFile(source);
            File cache = getCache(sourceFile, page, resolution);

            if (cache == null || !cache.exists() || cache.lastModified() < sourceFile.lastModified()) {
                createPNG(sourceFile, page, resolution);
            }

            Image img = Toolkit.getDefaultToolkit().createImage(cache.getAbsolutePath());
            
            return new ImageIcon(img).getImage();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InterruptedException e) {
            ErrorManager.getDefault().notify(e);
        }

        return null;
    }

}

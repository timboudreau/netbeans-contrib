/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

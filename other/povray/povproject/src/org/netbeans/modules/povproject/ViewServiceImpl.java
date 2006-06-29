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
/*
 * ViewServiceImpl.java
 *
 * Created on February 17, 2005, 9:20 PM
 */

package org.netbeans.modules.povproject;

import org.netbeans.api.povproject.RendererService;
import org.netbeans.api.povproject.ViewService;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 * Implementation of the ViewService interface, provided in the project's
 * lookup, and used by actions on POV source nodes.
 *
 * @author Timothy Boudreau
 */
final class ViewServiceImpl implements ViewService {
    private final PovProject project;
    /** Creates a new instance of ViewServiceImpl */
    public ViewServiceImpl(PovProject project) {
        this.project = project;
    }

    public boolean isFileRendered(FileObject file) {
        return imageForFile (file) != null;
    }

    public void view(FileObject file) {
        FileObject img = imageForFile (file);
        if (img != null) {
            try {
                DataObject ob = DataObject.find (img);
                OpenCookie ck = (OpenCookie) ob.getCookie(OpenCookie.class);
                if (ck != null) {
                    ck.open();
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
        } else {
            RendererService renderer = (RendererService) 
                project.getLookup().lookup(RendererService.class);
            renderer.render(FileUtil.toFile(file));
        }
    }
    
    private FileObject imageForFile (FileObject file) {
        FileObject dir = project.getProjectDirectory();
        
        //Sanity check
        assert file.getPath().startsWith(
                project.getProjectDirectory().getPath());
        
        FileObject imagesDir = 
                project.getProjectDirectory().getFileObject(
                PovProject.IMAGES_DIR);
        
        if (imagesDir == null) {
            return null;
        }
        
        String sceneName = file.getName();
        
        int endIndex;
        if ((endIndex = sceneName.lastIndexOf('.')) != -1) {
            sceneName = sceneName.substring(0, endIndex);
        }
        
        FileObject image = imagesDir.getFileObject (sceneName + ".png");
        return image;
    }    

    public boolean isUpToDate(FileObject file) {
        FileObject image = imageForFile (file);
        if (image != null) {
            return file.lastModified().getTime() <= 
                image.lastModified().getTime();
        } else {
            return false;
        }
    }
    
}

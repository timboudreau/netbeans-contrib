/*
 * ThemeBuilderProjectOpenedHook.java
 *
 * Created on March 2, 2007, 4:28 PM
 */

package org.netbeans.modules.themebuilder.project;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Hook to react for Theme Builder Project open and close UI actions
 * An instance is placed in the Project Lookup to register this
 * @author winstonp
 */
public class ThemeBuilderProjectOpenedHook extends ProjectOpenedHook {

    private ThemeBuilderProject themeBuilderproject;

    public ThemeBuilderProjectOpenedHook(Project project) {
        themeBuilderproject = (ThemeBuilderProject) project;
    }

    /**
     * Called when the Theme Builder Project is opened
     */
    protected void projectOpened() {
        OutputStream out = null;

        try {
            File buildProperties = new File(System.getProperty("netbeans.user"), "build.properties");
            // NOI18N
            String privateProps = "nbproject/private";
            FileObject privateDir = themeBuilderproject.getProjectDirectory().getFileObject(privateProps);
            FileObject privatePropsFile = privateDir.getFileObject("private.properties");
            if (privatePropsFile != null) {
                privatePropsFile.delete();
            }
            privatePropsFile = privateDir.createData("private.properties");
            out = privatePropsFile.getOutputStream();
            String userPropsInfo = null;
            if (buildProperties.getAbsolutePath().contains("\\")) {
                userPropsInfo = "user.properties.file=" + buildProperties.getAbsolutePath().replaceAll("\\", "/");
            } else {
                userPropsInfo = "user.properties.file=" + buildProperties.getAbsolutePath();
            }
            out.write(userPropsInfo.getBytes());
        } catch (Exception exc) {
            Logger.getLogger(ThemeBuilderProjectOpenedHook.class.getName()).log(Level.SEVERE, exc.getLocalizedMessage(), exc);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Called when the Theme Builder Project is closed
     */
    protected void projectClosed() {
        //Not Yet Implemented
    }
}
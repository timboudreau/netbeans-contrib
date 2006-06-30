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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import org.openide.ErrorManager;

/**module private class
 *
 * @author Jan Lahoda
 */
public/*module private*/ final class ModuleSettings {

    /** Creates a new instance of ModuleSettings */
    private ModuleSettings() {
    }

    private static ModuleSettings instance = null;
    
    public static synchronized ModuleSettings getDefault() {
        if (instance == null)
            instance = new ModuleSettings();
        
        return instance;
    }
    
    private File getUserDir() {
        return new File(System.getProperty("netbeans.user"));
    }
    
    public Map readSettings() {
        XMLDecoder dec = null;
        
        try {
            File postInstallFlag = new File(new File(getUserDir(), "config"), "latex-ui-settings.xml");
            
            if (!postInstallFlag.canRead())
                postInstallFlag = new File(new File(getUserDir(), "var"), ".latex-ui-post-install");
            
            if (!postInstallFlag.canRead())
                return null;
            
            dec = new XMLDecoder(new FileInputStream(postInstallFlag));
            
            Object read = dec.readObject();
            
            if (read instanceof Map)
                return (Map) read;
            
            return null;
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            return null;
        } finally {
            if (dec != null)
                dec.close();
        }
    }
    
    public void writeSettings(Map settings) {
        XMLEncoder enc = null;
        try {
            File postInstallFlag = new File(new File(getUserDir(), "config"), "latex-ui-settings.xml");
            
            enc = new XMLEncoder(new FileOutputStream(postInstallFlag));
            
            enc.writeObject(settings);
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            if (enc != null)
                enc.close();
        }
        
        //TODO: replace with some listener concept:
        IconsCreator.getDefault().reloadSettings();
    }
}

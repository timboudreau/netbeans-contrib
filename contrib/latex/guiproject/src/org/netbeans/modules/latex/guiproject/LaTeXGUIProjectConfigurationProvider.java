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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.guiproject;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectConfigurationProvider implements ProjectConfigurationProvider<LaTeXProjectConfiguration> {
    
    /** Creates a new instance of LaTeXGUIProjectConfiguration */
    public LaTeXGUIProjectConfigurationProvider() {
    }
    
    public Collection<LaTeXProjectConfiguration> getConfigurations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public LaTeXProjectConfiguration getActiveConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void setActiveConfiguration(LaTeXProjectConfiguration arg0) throws IllegalArgumentException,
            IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean hasCustomizer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void customize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean configurationsAffectAction(String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

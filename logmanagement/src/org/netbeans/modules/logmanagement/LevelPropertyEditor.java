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

package org.netbeans.modules.logmanagement;

import java.beans.PropertyEditorSupport;
import java.util.logging.Level;

/**
 *
 * @author Anuradha G
 */
public class LevelPropertyEditor extends PropertyEditorSupport {

    private String level;
    @Override
    public String getAsText() {
        return level;
    }

    @Override
    public void setAsText(String text) {
        level=text;

    }

    @Override
    public void setValue(Object level) {
        this.level=(String) level;
        
    }

    @Override
    public Object getValue() {
        return level;
    }

    @Override
    public String[] getTags() {
        String [] levels=new String[9];
        levels[0]=Level.ALL.getLocalizedName();
        levels[1]=Level.CONFIG.getLocalizedName();
        levels[2]=Level.FINE.getLocalizedName();
        levels[3]=Level.FINER.getLocalizedName();
        levels[4]=Level.FINEST.getLocalizedName();
        levels[5]=Level.INFO.getLocalizedName();
        levels[6]=Level.OFF.getLocalizedName();
        levels[7]=Level.SEVERE.getLocalizedName();
        levels[8]=Level.WARNING.getLocalizedName();
        
        return levels;
    }
    
    
}
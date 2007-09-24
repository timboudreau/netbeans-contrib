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


package org.netbeans.modules.perspective.views;

import java.io.Serializable;

/**
 * Represent TopComponent State In Perspective (ex: Mode ID,Open or not) 
 * @author Anuradha G
 */
public class View implements Serializable{
    private static final long serialVersionUID = 1l;
    private String topcomponentID;
    private String mode;
    private boolean open;
    
    /**
     * Create View 
     * @param topcomponentID TopComponent ID
     * @param mode Mode ID
     */
    public View(String topcomponentID, String mode) {
        this.topcomponentID = topcomponentID;
        this.mode = mode;
    }

    /**
     * Create View 
     * @param topcomponentID TopComponent ID
     * @param mode Mode ID
     * @param open TopComponent opened or not 
     */
    public View(String topcomponentID, String mode, boolean open) {
        this.topcomponentID = topcomponentID;
        this.mode = mode;
        this.open = open;
    }

    /**
     * Return Mode ID
     * @return Mode ID
     */
    public String getMode() {
        return mode;
    }

    /**
     * Set Mode ID
     * @param mode Mode ID 
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Return TopComponent ID
     * @return TopComponent ID
     */
    public String getTopcomponentID() {
        return topcomponentID;
    }

    /**
     * Set TopComponent ID
     * @param topcomponentID TopComponent ID 
     */
    public void setTopcomponentID(String topcomponentID) {
        this.topcomponentID = topcomponentID;
    }

    /**
     * Return TopComponent Opened or not
     * @return opened
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Set TopComponent Opened or not
     * @param open 
     */
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    
}

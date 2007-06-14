
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */


package com.sun.tthub.gde.logic;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;

/**
 *
 * @author Hareesh Ravindran
 */
public interface ExtTTValueInfoProcessor {
    /**
     * This method will use the TTValueDisplayInfo gathered by the UI
     * and will generate the portlets and finally, the war file. The war file
     * will be generated into the GDE folder.
     */
    public void generateWarFile(TTValueDisplayInfo 
                    ttValueDisplayInfo) throws GDEException;
    
    /**
     * This method takes care of deploying the generated war file into the 
     * portal server. This step will be invoked only when the deployment 
     * parameters are specified by the user. If the user skips the deployment
     * parameters, the wizard will not invoke this step.
     */
    public void deployToPortalServer(TTValueDisplayInfo 
                    ttValueDisplayInfo) throws GDEException;
}

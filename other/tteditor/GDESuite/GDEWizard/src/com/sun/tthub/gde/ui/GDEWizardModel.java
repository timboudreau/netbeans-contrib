
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


package com.sun.tthub.gde.ui;

import com.sun.tthub.gde.logic.PortletDeployParams;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;

/**
 *
 * @author Hareesh Ravindran
 */
public final class GDEWizardModel {
    
    private PortletDeployParams portletDeployParams;
    private TTValueDisplayInfo ttValueDisplayInfo;
    
    /** Creates a new instance of GDEWizardModel */
    public GDEWizardModel() {
        ttValueDisplayInfo = new TTValueDisplayInfo();
    }

    public PortletDeployParams getPortletDeployParams() 
            { return portletDeployParams; }

    public void setPortletDeployParams(PortletDeployParams 
            portletDeployParams) {
        this.portletDeployParams = portletDeployParams;
    }

    public TTValueDisplayInfo getTtValueDisplayInfo() 
        { return ttValueDisplayInfo; }

    public void setTtValueDisplayInfo(
                        TTValueDisplayInfo ttValueDisplayInfo) {
        this.ttValueDisplayInfo = ttValueDisplayInfo;
    }    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Deploy Params: ["); 
        buffer.append(portletDeployParams);
        buffer.append("],  TTValueDisplay Info: [");
        buffer.append(ttValueDisplayInfo);
        buffer.append("]");
        return buffer.toString();
    }
}

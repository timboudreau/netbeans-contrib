
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

/**
 * This class is an abstraction for the main user interface for the wizard. The 
 * main user interface can be a Dialog, JFrame or any other such component. 
 * In order to separate the user interface nature from the GDE application logic,
 * we use the GDEWizardUI interface. Any user interface component that will be
 * used as the main GDE UI should implement this interface. The GDE controller
 * will take care of setting the main dialog in the GDEAppContext.
 *
 * @author Hareesh Ravindran
 */
public interface GDEWizardUI {
    
}

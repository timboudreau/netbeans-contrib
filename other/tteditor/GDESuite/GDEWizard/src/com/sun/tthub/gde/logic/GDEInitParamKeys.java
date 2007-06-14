
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

/**
 * Any additional parameters that has to be passed to the GDEController through
 * the init() method has to be added to a hashMap and passed to it. The keys
 * of the hashmap will be listed in this interface.
 *
 * @author Hareesh Ravindran
 */
public interface GDEInitParamKeys {
    public static final String GDE_WIZARD_UI = 
                        "com.sun.tthub.gde.logic.gdewizardui";
    public static final String GDE_CLASSES_MNGR = 
                        "com.sun.tthub.gde.logic.gdeclassesmanager";
}

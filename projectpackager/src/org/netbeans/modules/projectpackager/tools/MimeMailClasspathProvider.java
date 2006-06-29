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

package org.netbeans.modules.projectpackager.tools;

import java.io.File;
import org.apache.tools.ant.module.spi.AutomaticExtraClasspathProvider;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;


/**
 * Provider which registers through ANT SPI two necessary jars into ant's classpath
 * @author Roman "Roumen" Strobl
 */
public class MimeMailClasspathProvider implements AutomaticExtraClasspathProvider {

    /**
     * Default constructor for lookup
     */
    public MimeMailClasspathProvider() {}

    /**
    * Return jars to be added to classpath
     * @return jars to classpath
     */
    public File[] getClasspathItems() {
        File mailJar = InstalledFileLocator.getDefault().locate(
            "modules/ext/mail-1.3.2.jar", "org.netbeans.modules.projectpackager", false); // NOI18N
        assert mailJar != null : NbBundle.getBundle(Constants.BUNDLE).getString("Missing_mail.jar");
        File activationJar = InstalledFileLocator.getDefault().locate(
            "modules/ext/activation-1.0.2.jar", "org.netbeans.modules.projectpackager", false); // NOI18N
        assert activationJar != null : NbBundle.getBundle(Constants.BUNDLE).getString("Missing_activation.jar");
        return new File[] {mailJar, activationJar};
    }
    
}

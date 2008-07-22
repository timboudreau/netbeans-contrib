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
package org.netbeans.modules.scala.project.classpath;

import org.netbeans.modules.gsfpath.api.classpath.ClassPath;
import org.netbeans.modules.gsfpath.spi.classpath.ClassPathProvider;
import org.netbeans.modules.scala.project.classpath.JavaClassPathToGsfClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 * Supplies classpath information according to project file owner.
 * This is already available in j2seproject, but when the java support
 * is not present it causes user source paths not to be indexed etc.
 * 
 * @Note: 
 * This is for META-INFO/Services usage. Since ClassPathProviderImple has none empty 
 * constructorcan, cannot be instantiated by lookup, we use this class to provide
 * META-INFO/Services
 * 
 * @author Caoyuan Deng
 */
public class GsfProjectClassPathProvider implements ClassPathProvider {

    /** Default constructor for lookup, services. */
    public GsfProjectClassPathProvider() {
    }

    /** 
     * A proxy method which is actually call @link{ClassPathProviderImpl#findClassPath(FileObject, String)}
     * 
     * @Note: this gsf classpath provider is used only to get __PACKAGE__ value in:
     * @link{org.netbeans.modules.gsf.GsfDataLoader#createPrimaryEntry(MultiDataObject, FileObject) }
     * 
     * @Todo:
     * This class is useless at all? since there is a org.netbeans.modules.gsf.ProjectClassPathProvider
     * which registered as a META-IN.services for org.netbeans.modules.spi.classpath.ClassPathProvider too
     * 
     * @Note:
     * This method do not provide any classpath when file itself is under standard
     * libs, since these files owner project is null. In this case, these files 
     * classpath will be provided by platform modules.
     * 
     * But for project files, when type is BOOT, will return the Scala standard lib's
     * classpath.
     */
    public ClassPath findClassPath(FileObject file, String type) {
        if (!ClassPath.SOURCE.equals(type)) {
            return null;
        }
        Project p = FileOwnerQuery.getOwner(file);
        if (p != null) {
            org.netbeans.spi.java.classpath.ClassPathProvider cpp = p.getLookup().lookup(org.netbeans.spi.java.classpath.ClassPathProvider.class);
            if (cpp != null) {
                return JavaClassPathToGsfClassPath.convert(cpp.findClassPath(file, type));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

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
package org.netbeans.modules.cnd.update;

import java.io.IOException;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;

/**
 * @author Vladimir Yaroslavskiy
 * @version 2006.03.31
 */
public class Install extends ModuleInstall {

  /**{@inheritDoc}*/
  public void restored() {
    setBuildNumber();
  }

  private void setBuildNumber() {
    try {
      System.getProperties().load(getClass().getResourceAsStream(
        "/org/netbeans/modules/cnd/update/build.number")); // NOI18N

      String pkNumber = System.getProperty(PACK_BUILD_NUMBER);
      String nbNumber = System.getProperty(NETBEANS_BUILD_NUMBER);

      System.setProperty(NETBEANS_BUILD_NUMBER,
        NbBundle.getMessage(Install.class, "LBL_Ide_Number", // NOI18N
        nbNumber, pkNumber));
    } 
    catch (IOException e) {
      return;
    }
  }

  private static final String PACK_BUILD_NUMBER =
    "pack.build.number"; // NOI18N
  private static final String NETBEANS_BUILD_NUMBER =
    "netbeans.buildnumber"; // NOI18N
}

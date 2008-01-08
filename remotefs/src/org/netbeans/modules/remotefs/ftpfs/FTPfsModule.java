/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */

package org.netbeans.modules.remotefs.ftpfs;

import org.openide.modules.ModuleInstall;

/** FTP filesystem module class.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPfsModule extends ModuleInstall {
  static final long serialVersionUID = 2289861663533516525L;

  public FTPfsModule() {
    // A public default constructor is required!
    // Of course, Java makes one by default for a public class too.
  }

  public void installed() {
    // This module has been installed for the first time! Notify authors.
    // Handle setup within this session too:
    restored();
  }

  public void restored() {
    //FileUtil.setMIMEType("test", "text/x-clipboard-content-test");
  }

  public void uninstalled() {
    // Do not need to do anything special on uninstall.
    // Action will already be removed from Edit menu automatically.
  }

  public boolean closing() {
    // Ask the user to save any open, modified clipboard contents.
    // If the user selects "Cancel" on one of these dialogs, don't exit yet!
    //return DisplayClipboardAction.askAboutExiting();
    return true;
  }
}


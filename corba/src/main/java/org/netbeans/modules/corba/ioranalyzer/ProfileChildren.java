/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * ProfileChildren.java
 *
 * Created on 13. ??jen 2000, 10:07
 */

package org.netbeans.modules.corba.ioranalyzer;

import java.io.*;
import java.util.ArrayList;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.*;
import org.openide.loaders.DataObject;
import org.openide.filesystems.*;
import org.openide.util.NbBundle;
/**
 *
 * @author  root
 * @version
 */
public class ProfileChildren extends Children.Keys {

    IORDataObject dataObject;
    IORData iorData;

    /** Creates new ProfileChildren */
    public ProfileChildren(IORDataObject dataObject) {
        this.dataObject = dataObject;
        this.iorData = null;
    }
    
    
    public void addNotify () {
        boolean valid = false;
        try {
            lazyInit ();
            this.createKeys();
            valid = true;
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            this.setKeys (new java.lang.Object[0]);
        }
        finally {
            handleIOR (valid);
        }
    }
    
    public void createKeys () {
        ArrayList profiles = iorData.getProfiles();
        ProfileKey[] keys = new ProfileKey[profiles.size()];
        for (int i=0; i< keys.length; i++) {
            Object profile = profiles.get(i);
            if (profile instanceof IORProfile) {
                keys[i] = new IOPProfileKey (i, (IORProfile)profile);
            }
            else if (profile instanceof IORTaggedProfile) {
                keys[i] = new TaggedProfileKey (i, (IORTaggedProfile)profile);
            }
        }
        this.setKeys (keys);
    }
    
    
    public Node[] createNodes (Object key) {
        if (key instanceof IOPProfileKey) {
            return new Node[] {new ProfileNode (((IOPProfileKey)key).index, ((IOPProfileKey)key).value)};
        }
        else if (key instanceof TaggedProfileKey) {
            return new Node[] { new TaggedNode (((TaggedProfileKey)key).index, ((TaggedProfileKey)key).value)};
        }
        else return new Node[0];
    }
    
    public void update () {
        this.iorData = null;
        this.addNotify();
    }
    
    public Integer getProfileCount () {
        boolean valid = true;
        try {
            lazyInit ();
            return new Integer(this.iorData.getProfiles().size());
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            valid = false;
            return null;
        }
        finally {
            handleIOR (valid);
        }
    }
    
    public String getRepositoryId () {
        boolean valid = true;
        try {
            lazyInit();
            return this.iorData.getRepositoryId();
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            valid = false;
            return null;
        }
        finally {
            handleIOR (valid);
        }
    }
    
    public Boolean isLittleEndian () {
        boolean valid = true;
        try {
            lazyInit();
            return this.iorData.isLittleEndian() ? Boolean.TRUE : Boolean.FALSE;
        }catch (org.omg.CORBA.BAD_PARAM bp) {
            valid = false;
            return null;
        }
        finally {
            handleIOR (valid);
        }
    }
    
    private void lazyInit () {
        if (this.iorData == null) {
            try {
                this.iorData = new IORData ( dataObject.getContent());
            } catch (IllegalStateException illegalState) {
                throw new org.omg.CORBA.BAD_PARAM ();
            }
            catch (IllegalArgumentException illegalArgument) {
                throw new org.omg.CORBA.BAD_PARAM ();
            }
        }
    }
    
    private void handleIOR (boolean valid) {
        ((IORNode)this.getNode ()).validate (valid);
    }

}

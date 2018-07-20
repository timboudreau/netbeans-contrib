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

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.NbBundle;

public class IORNode extends DataNode implements Node.Cookie {

    static final String ICON_BASE = "org/netbeans/modules/corba/ioranalyzer/resources/ior";
    static final String FAILED_ICON_BASE = "org/netbeans/modules/corba/ioranalyzer/resources/failedior";

    public IORNode (IORDataObject dataObject) {
	super (dataObject, new ProfileChildren(dataObject));
	this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
    }
    
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(IORNode.class).getString("TITLE_Name"),String.class,NbBundle.getBundle(IORNode.class).getString("TITLE_Name"),NbBundle.getBundle(IORNode.class).getString("TIP_Name")) {
            public Object getValue () {
                return IORNode.this.getName();
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(IORNode.class).getString("TITLE_Endian"),String.class,NbBundle.getBundle(IORNode.class).getString("TITLE_Endian"),NbBundle.getBundle(IORNode.class).getString("TIP_Endian")) {
            public Object getValue () {
                ProfileChildren cld = (ProfileChildren) IORNode.this.getChildren();
                Boolean res = cld.isLittleEndian();
                if (res == null)
                    return "";
                else if (res.booleanValue()) 
                    return NbBundle.getBundle(IORNode.class).getString ("TXT_Little");
                else
                    return NbBundle.getBundle(IORNode.class).getString ("TXT_Big");
            }
        });
        ss.put ( new PropertySupport.ReadOnly (NbBundle.getBundle(IORNode.class).getString("TITLE_RepositoryId"),String.class,NbBundle.getBundle(IORNode.class).getString("TITLE_RepositoryId"),NbBundle.getBundle(IORNode.class).getString("TIP_RepositoryId")) {
            public Object getValue () {
                String rid =  ((ProfileChildren)IORNode.this.getChildren()).getRepositoryId();
                if (rid == null)
                    return "";
                else 
                    return rid;
            }
        });
        return s;
    }
    
    
    void validate (boolean valid) {
        if (valid) {
            this.setIconBase (ICON_BASE);
        }
        else {
            this.setIconBase (FAILED_ICON_BASE);
        }
    }
    

}
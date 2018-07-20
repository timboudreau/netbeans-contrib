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

package org.netbeans.modules.apisupport.beanbrowser;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.datatransfer.ClipboardEvent;
import org.openide.util.datatransfer.ClipboardListener;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.util.datatransfer.MultiTransferObject;

/** Children list of a Clipboard.
 * Each key is a DataFlavor.
 */
public class ClipboardKids extends Children.Keys {
    
    private Clipboard clip;
    private ClipboardListener list;
    
    public ClipboardKids(Clipboard clip) {
        this.clip = clip;
    }
    
    protected void addNotify() {
        updateKeys();
        if (list == null && (clip instanceof ExClipboard)) {
            list = new ClipboardListener() {
                public void clipboardChanged(ClipboardEvent ev) {
                    updateKeys();
                }
            };
            ((ExClipboard) clip).addClipboardListener(list);
        }
    }

    protected void removeNotify() {
        if (list != null) {
            ((ExClipboard) clip).removeClipboardListener(list);
            list = null;
        }
        setKeys(Collections.EMPTY_SET);
    }
    
    private void updateKeys() {
        Transferable t = clip.getContents(null);
        if (t == null) {
            setKeys(Collections.EMPTY_SET);
        } else {
            DataFlavor[] flavors = t.getTransferDataFlavors();
            if (flavors == null) {
                // Should not happen, but sometimes does.
                setKeys(Collections.EMPTY_SET);
            } else {
                setKeys(flavors);
            }
        }
    }
    
    protected Node[] createNodes(Object key) {
        DataFlavor flav = (DataFlavor) key;
        try {
            Object obj = clip.getContents(null).getTransferData(flav);
            if (obj instanceof MultiTransferObject) {
                MultiTransferObject mto = (MultiTransferObject) obj;
                List nue = new LinkedList();
                int count = mto.getCount();
                for (int i = 0; i < count; i++) {
                    nue.add(PropSetKids.makePlainNode("MultiTransferObject [" + i + "]"));
                    DataFlavor[] flavs = mto.getTransferDataFlavors(i);
                    for (int j = 0; j < flavs.length; j++) {
                        try {
                            nue.add(makeFlavorNode(flavs[j], mto.getTransferData(i, flavs[j])));
                        } catch (Exception e) {
                            nue.add(PropSetKids.makeErrorNode(e));
                        }
                    }
                }
                nue.add(PropSetKids.makePlainNode("MultiTransferObject [end]"));
                return (Node[]) nue.toArray(new Node[nue.size()]);
            } else {
                return new Node[] { makeFlavorNode(flav, obj) };
            }
        } catch (Exception e) {
            return new Node[] { PropSetKids.makeErrorNode(e) };
        }
    }
    
    private static Node makeFlavorNode(DataFlavor flav, Object obj) {
        Node n = PropSetKids.makeObjectNode(obj);
        n.setDisplayName(flav.getHumanPresentableName() + " = " + n.getDisplayName());
        return n;
    }
    
}

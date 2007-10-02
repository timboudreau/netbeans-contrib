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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.visualweb.designer.decoration;


import org.netbeans.modules.visualweb.api.designtime.idebridge.DesigntimeIdeBridgeProvider;
import org.netbeans.modules.visualweb.api.insync.InSyncService;
import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.DesignContext;
import com.sun.rave.designtime.DesignProject;
import com.sun.rave.designtime.DesignProperty;
import com.sun.rave.designtime.DisplayAction;
import org.netbeans.modules.visualweb.insync.live.CustomizeAction;
import org.netbeans.modules.visualweb.spi.designer.Decoration;
import org.netbeans.modules.visualweb.spi.designer.DecorationProvider;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Element;


/**
 * Provider of <code>Decoration</code>s for specified <code>DesignBeans</code>.
 * TODO Extract this impl from designer to individual module, impl some provider
 * api defined in designer, and registered in the global lookup.
 *
 * @author Peter Zavadsky
 */
public class DecorationProviderImpl implements DecorationProvider {

    /** Logger used for this class. */
    private static final Logger logger = Logger.getLogger(DecorationProviderImpl.class.getName());
    
    static {
        initLogging();
    }
    
    /** XXX Some global support method would be needed. */
    private static void initLogging() {
        String prop = System.getProperty(DecorationProviderImpl.class.getName());
        if (prop == null) {
            return;
        }
        
        Level level = Level.parse(prop);
        logger.setLevel(level);
        
        // XXX Also the root handlers are needed to set (the console handlers).
        // TODO Probably better is to create and use local hanlder.
        Handler[] handlers = Logger.getLogger("").getHandlers(); // NOI18N
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            if (handler instanceof ConsoleHandler) {
                // Be careful not to clash with others, this is a root handler.
                Level oldLevel = handler.getLevel();
                if (level.intValue() < oldLevel.intValue()) {
                    handler.setLevel(level);
                }
            }
        }
    }

    
    public DecorationProviderImpl() {
    }
    
    public Decoration getDecoration(Element element) {
        if (element == null) {
            return null;
        }
        
        DesignBean designBean = InSyncService.getProvider().getMarkupDesignBeanForElement(element);
        if (designBean == null) {
            return null;
        }
        
        logger.fine("designBean=" + designBean);
        DesignBean decorationBean = getDecorationBean(designBean);
        logger.fine("decorationBean=" + decorationBean);
        if (decorationBean == null) {
//            if (logger.isLoggable(Level.FINE)) {
//                return getDecoration(designBean);
//            }
            return null;
        }
        
        return getDecoration(decorationBean);
    }

    private static DesignBean getDecorationBean(DesignBean designBean) {
        // XXX Temporary, demo only impl.
        return DecorationDemoSupport.getDecorationBean(designBean);
    }

    private static Decoration getDecoration(DesignBean decorationBean) {
        return new DefaultDecoration(decorationBean);
    }
    
    
    private static class DefaultDecoration implements Decoration {

        private static final Action[] DEFAULT_ACTIONS = new Action[] {
            // XXX There shouldn't be a dep on insync.
            // TODO Other solution, e.g. to retrieve the actions via layers?
            SystemAction.get(CustomizeAction.class)
        };
        
        private final DesignBean designBean;
        
        public DefaultDecoration(DesignBean designBean) {
            this.designBean = designBean;
        }

        public int getWidth() {
            Image image = getImage();
            if (image == null) {
                return 0;
            } else {
                // XXX
                return new ImageIcon(image).getIconWidth();
            }
        }

        public int getHeight() {
            Image image = getImage();
            if (image == null) {
                return 0;
            } else {
                // XXX
                return new ImageIcon(image).getIconHeight();
            }
        }

        public Image getImage() {
            Image image = Utilities.loadImage("org/netbeans/modules/visualweb/designer/decoration/resources/dbDecoration.png"); // NOI18N
            
            // XXX Fall back to the test impl.
            if (image == null) {
                image = designBean.getBeanInfo().getIcon(BeanInfo.ICON_COLOR_16x16);
                if (image != null) {
                    Image arrowImage = Utilities.loadImage(
                            "org/netbeans/modules/visualweb/designer/resources/blueLeftArrow.gif"); // NOI18N
                    image = Utilities.mergeImages(arrowImage, image, 16, 0);
                }
            }
            
            return image;
        }

        public Action[] getActions() {
            return DEFAULT_ACTIONS;
        }

        public Action getDefaultAction() {
            return DEFAULT_ACTIONS[0];
        }

        public Lookup getContext() {
            Node node = DesigntimeIdeBridgeProvider.getDefault().getNodeRepresentation(designBean);
            if (node == null) {
                return Lookup.EMPTY;
            } else {
                return Lookups.singleton(node);
            }
        }
    } // End of DefaultDecoration.


}


/*
 * GeInstanceNode.java
 *
 */

package org.netbeans.modules.j2ee.geronimo2.nodes;

import java.awt.Component;
import org.netbeans.modules.j2ee.geronimo2.GeDeploymentManager;
import org.netbeans.modules.j2ee.geronimo2.GeJ2eePlatformFactory;
import org.netbeans.modules.j2ee.geronimo2.customiser.GeCustomizer;
import org.netbeans.modules.j2ee.geronimo2.customiser.GeCustomizerDataSupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Max Sauer
 */
public class GeInstanceNode extends AbstractNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/j2ee/geronimo2/resources/server.gif"; // NOI18N
    private Lookup lookup;
    
    public GeInstanceNode(Lookup lookup) {
        super(new Children.Array());
	this.lookup = lookup;
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(GeInstanceNode.class, "TXT_GeInstanceNode");
    }
    
    public String getShortDescription() {
        return "http://localhost:8080"; // NOI18N
    }

    public javax.swing.Action[] getActions(boolean context) {
        return new javax.swing.Action[]{};
    }
    
    public boolean hasCustomizer() {
        return true;
    }
    
    public Component getCustomizer() {
	GeCustomizerDataSupport dataSup = new GeCustomizerDataSupport(getDeploymentManager());
        return new GeCustomizer(dataSup, new GeJ2eePlatformFactory().getJ2eePlatformImpl(getDeploymentManager()));
    }
    
    public GeDeploymentManager getDeploymentManager() {
        return ((GeDeploymentManager) lookup.lookup(GeDeploymentManager.class));
    }
}

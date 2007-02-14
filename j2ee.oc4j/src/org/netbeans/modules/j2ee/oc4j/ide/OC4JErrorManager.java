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

package org.netbeans.modules.j2ee.oc4j.ide;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ErrorManager;
import javax.swing.JOptionPane;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.util.NbBundle;

/**
 * Error manager for handling exceptions from Oracle Application Server
 *
 * @author Michal Mocnak
 */
public class OC4JErrorManager extends ErrorManager {
    
    /**
     * Singleton model pattern
     */
    private static Map<OC4JDeploymentManager, OC4JErrorManager> instances = new HashMap<OC4JDeploymentManager, OC4JErrorManager>();
    
    private OC4JDeploymentManager dm;
    
    /** Creates a new instance of OC4JErrorManager */
    private OC4JErrorManager(OC4JDeploymentManager dm) {
        this.dm = dm;
    }
    
    /**
     * Returns dm specific instance of OC4JErrorManager
     *
     * @param dm specified OC4JDeploymentManager
     * @return instance of an OC4JErrorManager
     */
    public static OC4JErrorManager getInstance(OC4JDeploymentManager dm) {
        if (!instances.containsKey(dm))
            instances.put(dm, new OC4JErrorManager(dm));
        
        return instances.get(dm);
    }
    
    private void write(String s) {
        OC4JLogger.getInstance(dm.getUri()).write(s);
    }
    
    private Throwable getDepestCause(Throwable t) {
        return (null != t.getCause()) ? (getDepestCause(t.getCause())):(t);
    }
    
    /**
     * Do a reaction if any exists
     *
     * @param clazz class name what be searched for a reaction
     */
    public synchronized void reaction(String clazz) {
        ClassReaction c = ClassReaction.lookup(clazz);
        
        if (null != c)
            c.react(dm);
    }
    
    /**
     * Parses exception's stack trace and try to performs reaction
     *
     * @param s debug message string
     * @param e cause exception
     * @param code error level
     */
    @Override
    public synchronized void error(String s, Exception e, int code) {
        if (OC4JDebug.isEnabled())
            write(s);
        
        Throwable t = e;
        
        if (null != e.getCause()) {
            t = getDepestCause(e.getCause());
        }
        
        for (final StackTraceElement st : t.getStackTrace()) {
            if (OC4JDebug.isEnabled())
                write(st.toString());
            
            // Do a reaction if available
            reaction(st.getClassName());
        }
    }
    
    // Class Reactions
    static {
        new AuthenticateReaction();
        new MissingDriverReaction();
    }
    
    private static class AuthenticateReaction extends ClassReaction {
        
        public AuthenticateReaction() {
            super("oracle.oc4j.rmi.ClientRmiTransport");
        }
        
        public void react(OC4JDeploymentManager manager) {
            InstanceProperties ip = manager.getInstanceProperties();
            
            String username = ip.getProperty(InstanceProperties.USERNAME_ATTR);
            String password = OC4JPluginUtils.requestPassword(username);
            
            if(password != null)
                ip.setProperty(InstanceProperties.PASSWORD_ATTR, password);
        }
    }
       
    private static class MissingDriverReaction extends ClassReaction {
        
        public MissingDriverReaction() {
            super("java.net.URLClassLoader");
        }
        
        public void react(OC4JDeploymentManager manager) {
            JOptionPane.showMessageDialog(null, NbBundle.getMessage(OC4JErrorManager.class, "MSG_MissingDriver"),
                    NbBundle.getMessage(OC4JErrorManager.class, "MSG_MissingDriverTitle"), JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private abstract static class ClassReaction {
        
        private static Map<String, ClassReaction> reactions = new HashMap<String, ClassReaction>();
        
        private String clazz;
        
        private ClassReaction(String clazz) {
            this.clazz = clazz;
            
            reactions.put(clazz, this);
        }
        
        private String getClazz() {
            return clazz;
        }
        
        public abstract void react(OC4JDeploymentManager manager);
        
        public static ClassReaction lookup(String clazz) {
            return reactions.get(clazz);
        }
    }
}

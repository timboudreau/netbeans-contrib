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

package org.netbeans.modules.portalpack.servers.core.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanException;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.nodes.BaseNode;
import org.openide.nodes.Sheet;

/**
 *
 * @author Satya
 */
public class DefaultPSTaskHandler implements PSTaskHandler {
    
    public DefaultPSTaskHandler() {
    }

    public void createChannel(String dn, String portletName, String channelName) throws MBeanException, Exception {
    }

    public String deploy(String warfile, String serveruri) throws Exception {
        return null;
    }

    public String[] getPortlets(String dn) {
        return new String[0];
    }

    public void undeploy(String portletAppName, String dn) throws Exception {
    }

    public String[] getExistingProviders(String dn) throws Exception {
        return new String[0];
    }

    public String[] getExistingContainerProviders(String dn) throws Exception {
        return new String[0];
    }

    public String[] getExistingContainers(String baseDn, boolean all) throws Exception {
        return new String[0];
    }

    public void createContainer(String baseDn, String channelName, String providerName) throws Exception {
    }

    public boolean deleteChannel(String baseDn, String channelName, String parentcontainer) throws Exception {
        return true;
    }

    public void createPortletChannel(String baseDN, String channelName, String portletName) throws Exception {
    }

    public void setSelectedChannels(String baseDN, List selected, String containerName) throws Exception {
    }

    public List getSelectedChannels(String baseDN, String containerName) throws Exception {
        return Collections.EMPTY_LIST;
    }

    public void setAvailableChannels(String baseDN, List selected, String containerName) throws Exception {
    }

    public List getAvailableChannels(String baseDN, String containerName) throws Exception {
        return Collections.EMPTY_LIST;
    }

    public Set getExistingChannels(String baseDN, Boolean all) throws Exception {
        return Collections.EMPTY_SET;
    }

    public Set getAssignableChannels(String baseDN, String container) throws Exception {
        return Collections.EMPTY_SET;
    }

    public String getAuthlessUser(String baseDN) throws Exception {
        return null;
    }

    public Map getObjects(String type, String searchFilter, String baseDN) throws Exception {
        return Collections.EMPTY_MAP;
    }

    public String constructPortletViewURL(String dn,String portlet) {
        return "http://localhost";
    }

    public String constructAdminToolURL() {
        return "http://localhost";
    }

    public void addChannel(String dn) throws Exception{
        //empty implementation.
    }

    public String getClientURL() {
        return constructAdminToolURL();
    }

    public Sheet createContainerPropertySheet(BaseNode node) {
        return Sheet.createDefault();
    }
}

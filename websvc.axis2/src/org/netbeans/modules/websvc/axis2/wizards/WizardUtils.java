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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.websvc.axis2.wizards;

import org.netbeans.modules.websvc.axis2.AxisUtils;
import org.netbeans.modules.websvc.axis2.config.model.Axis2;
import org.netbeans.modules.websvc.axis2.config.model.Axis2ComponentFactory;
import org.netbeans.modules.websvc.axis2.config.model.Axis2Model;
import org.netbeans.modules.websvc.axis2.services.model.MessageReceiver;
import org.netbeans.modules.websvc.axis2.services.model.MessageReceivers;
import org.netbeans.modules.websvc.axis2.services.model.Parameter;
import org.netbeans.modules.websvc.axis2.services.model.Schema;
import org.netbeans.modules.websvc.axis2.services.model.Service;
import org.netbeans.modules.websvc.axis2.services.model.ServiceGroup;
import org.netbeans.modules.websvc.axis2.services.model.Services;
import org.netbeans.modules.websvc.axis2.services.model.ServicesComponentFactory;
import org.netbeans.modules.websvc.axis2.services.model.ServicesModel;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkuchtiak
 */
public class WizardUtils {
    static void addService(ServicesModel servicesModel, String serviceClass, FileObject serviceFo) {
        ServicesComponentFactory factory = servicesModel.getFactory();
        String defaultNs = AxisUtils.getNamespaceFromClassName(serviceClass);       
        Schema schema = factory.createSchema();
        schema.setSchemaNamespaceAttr(defaultNs+"xsd"); //NOI18N
        Parameter param = factory.createParameter();
        param.setNameAttr("ServiceClass"); //NOI18N
        param.setValue(serviceClass);
        MessageReceivers receivers = factory.createMessageReceivers();
        MessageReceiver receiver1 = factory.createMessageReceiver();
        MessageReceiver receiver2 = factory.createMessageReceiver();
        receiver1.setMepAttr("http://www.w3.org/2004/08/wsdl/in-only"); //NOI18N
        receiver1.setClassAttr("org.apache.axis2.rpc.receivers.RPCInOnlyMessageReceiver"); //NOI18N
        receiver2.setMepAttr("http://www.w3.org/2004/08/wsdl/in-out"); //NOI18N
        receiver2.setClassAttr("org.apache.axis2.rpc.receivers.RPCMessageReceiver"); //NOI18N
        receivers.addMessageReceiver(receiver1);
        receivers.addMessageReceiver(receiver2);

        Services services = servicesModel.getRootComponent();
        
        if (servicesModel.isServicesGroup()) {
            ServiceGroup serviceGroup = (ServiceGroup)services;
            
            servicesModel.startTransaction();
            Service service = factory.createService();
            service.setNameAttr(serviceFo.getName());
            service.setScopeAttr("application"); //NOI18N
            service.setTargetNamespaceAttr(defaultNs);
            service.setDescription(serviceFo.getName()+" service"); //NOI18N
            service.setMessageReceivers(receivers);
            service.setSchema(schema);
            service.addParameter(param);
            serviceGroup.addService(service);
            servicesModel.endTransaction();

        } else {
            Service service = (Service)services;
            
            servicesModel.startTransaction();
            service.setNameAttr(serviceFo.getName());
            service.setScopeAttr("application"); //NOI18N
            service.setTargetNamespaceAttr(defaultNs);
            service.setDescription(serviceFo.getName()+" service"); //NOI18N
            service.setMessageReceivers(receivers);
            service.setSchema(schema);
            service.addParameter(param);
            servicesModel.endTransaction();
        }
    }
    
    static void addService(Axis2Model axis2Model, String serviceClass, FileObject serviceFo) {
        Axis2ComponentFactory factory = axis2Model.getFactory();

        Axis2 axis2 = axis2Model.getRootComponent();
        if (axis2 != null) {
            //List<org.netbeans.modules.websvc.axis2.config.model.Service> services = axis2.getServices();
            
            axis2Model.startTransaction();
            org.netbeans.modules.websvc.axis2.config.model.Service service = factory.createService();
            service.setNameAttr(serviceFo.getName());
            service.setServiceClass(serviceClass);
            axis2.addService(service);
            axis2Model.endTransaction();
        }
    }
}

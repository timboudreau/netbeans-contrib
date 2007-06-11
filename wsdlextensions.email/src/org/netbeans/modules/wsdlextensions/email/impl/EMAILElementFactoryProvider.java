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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.wsdlextensions.email.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.xml.namespace.QName;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPQName;
import org.netbeans.modules.wsdlextensions.email.impl.imap.IMAPBindingImpl;
import org.netbeans.modules.wsdlextensions.email.impl.imap.IMAPAddressImpl;
import org.netbeans.modules.wsdlextensions.email.impl.imap.IMAPInputImpl;
import org.netbeans.modules.wsdlextensions.email.impl.imap.IMAPOperationImpl;
import org.netbeans.modules.wsdlextensions.email.impl.pop3.POP3AddressImpl;
import org.netbeans.modules.wsdlextensions.email.impl.pop3.POP3BindingImpl;
import org.netbeans.modules.wsdlextensions.email.impl.pop3.POP3InputImpl;
import org.netbeans.modules.wsdlextensions.email.impl.pop3.POP3OperationImpl;
import org.netbeans.modules.wsdlextensions.email.pop3.POP3QName;
import org.netbeans.modules.xml.wsdl.model.spi.ElementFactory;
import org.w3c.dom.Element;

/**
** @author Sainath Adiraju
*/
public class EMAILElementFactoryProvider {
    
    public static class IMAPBindingFactory extends ElementFactory {
        public Set<QName> getElementQNames() {
            return Collections.singleton(IMAPQName.BINDING.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new IMAPBindingImpl(context.getModel(), element);
        }
    }
    
	public static class POP3BindingFactory extends ElementFactory {
        public Set<QName> getElementQNames() {
            return Collections.singleton(POP3QName.BINDING.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new POP3BindingImpl(context.getModel(), element);
        }
    }

    public static class IMAPAddressFactory extends ElementFactory {
        public Set<QName> getElementQNames() {
            return Collections.singleton(IMAPQName.ADDRESS.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new IMAPAddressImpl(context.getModel(), element);
        }
    }

    public static class POP3AddressFactory extends ElementFactory {
        public Set<QName> getElementQNames() {
            return Collections.singleton(POP3QName.ADDRESS.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new POP3AddressImpl(context.getModel(), element);
        }
    }

    public static class IMAPOperationFactory extends ElementFactory{
        public Set<QName> getElementQNames() {
            return Collections.singleton(IMAPQName.OPERATION.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new IMAPOperationImpl(context.getModel(), element);
        }
    }

	public static class POP3OperationFactory extends ElementFactory{
        public Set<QName> getElementQNames() {
            return Collections.singleton(POP3QName.OPERATION.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new POP3OperationImpl(context.getModel(), element);
        }
    }

    public static class IMAPInputFactory  extends ElementFactory{
        public Set<QName> getElementQNames() {
            return Collections.singleton(IMAPQName.INPUT.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new IMAPInputImpl(context.getModel(), element);
        }
    }

	public static class POP3InputFactory  extends ElementFactory{
        public Set<QName> getElementQNames() {
            return Collections.singleton(POP3QName.INPUT.getQName());
        }
        public WSDLComponent create(WSDLComponent context, Element element) {
            return new POP3InputImpl(context.getModel(), element);
        }
    }
 
    }


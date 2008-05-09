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
package org.netbeans.modules.wsdlextensions.ldap.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import org.netbeans.modules.wsdlextensions.ldap.LdapConnectionProperties;
import org.netbeans.modules.wsdlextensions.ldap.ldif.LdifObjectClass;
import org.openide.util.Exceptions;

/**
 *
 * @author Gary Zheng
 */
public class LdapConnection extends LdapConnectionProperties {

    private LdapContext connection;
    private String dn;
    
    public LdapConnection() {
    }
    
    public String getDn() {
        return dn;
    }
    
    public void setDn(String dn) {
        this.dn = dn;
    }
    
    public Object getProperty(String property) {
        try {
            Class cls = this.getClass();
            property = property.substring(0, 1).toUpperCase() + property.substring(1);
            Method method = cls.getMethod("get" + property);
            return method.invoke(this, (java.lang.Object[]) null);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return "";
    }

    private boolean isEmpty(String str) {
        if (null == str) {
            return true;
        }

        if (str.length() == 0) {
            return true;
        }

        return false;
    }

    public LdapContext getConnection() {
        if (null != connection) {
            return connection;
        }

        LdapContext ret = null;
        try {
            if (!isEmpty(this.getTruststore())) {
                System.setProperty("javax.net.ssl.trustStore", this.getTruststore());
            }
            if (!isEmpty(this.getTruststoretype())) {
                System.setProperty("javax.net.ssl.trustStoreType", this.getTruststoretype());
            }
            if (!isEmpty(this.getTruststorepassword())) {
                System.setProperty("javax.net.ssl.trustStorePassword", this.getTruststorepassword());
            }
            if (!isEmpty(this.getKeystore())) {
                System.setProperty("javax.net.ssl.keyStore", this.getKeystore());
            }
            if (!isEmpty(this.getKeystorepassword())) {
                System.setProperty("javax.net.ssl.keyStorePassword", this.getKeystorepassword());
            }
            if (!isEmpty(this.getKeystoreusername())) {
                System.setProperty("javax.net.ssl.keyStoreUsername", this.getKeystoreusername());
            }
            if (!isEmpty(this.getKeystoretype())) {
                System.setProperty("javax.net.ssl.keyStoreType", this.getKeystoretype());
            }

            Hashtable<String, String> env = new Hashtable<String, String>();
            if (!isEmpty(this.getPrincipal())) {
                env.put(Context.SECURITY_PRINCIPAL, this.getPrincipal());
            }
            if (!isEmpty(this.getCredential())) {
                env.put(Context.SECURITY_CREDENTIALS, this.getCredential());
            }
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, this.getLocation());
            if (!isEmpty(this.getAuthentication())) {
                env.put(Context.SECURITY_AUTHENTICATION, this.getAuthentication());
            }
            if (!isEmpty(this.getProtocol())) {
                env.put(Context.SECURITY_PROTOCOL, this.getProtocol());
            }

            ret = new InitialLdapContext(env,null);
//            if (this.getTlssecurity().toUpperCase().equals("YES")) {
//                StartTlsResponse tls = (StartTlsResponse) ret.extendedOperation(new StartTlsRequest());
//                tls.negotiate();
//            }
            connection = ret;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public ArrayList getDNs() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            DirContext ctx = getConnection();
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(this.getDn(), "(ObjectClass=*)", constraints);
            while (null != results && results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                list.add(sr.getNameInNamespace());
                sr = null;
            }
        } catch (NamingException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List getObjectNames() throws NamingException {
        List<String> ret = new ArrayList<String>();
        DirContext top = getConnection().getSchema("");
        NamingEnumeration ne = top.list("ClassDefinition");

        while (ne.hasMore()) {
            NameClassPair pair = (NameClassPair) ne.next();
            String clsName = pair.getName();
            ret.add(clsName);
        }

        return ret;
    }

    public LdifObjectClass getObjectClass(String objName) throws NamingException {
        LdifObjectClass objClass = new LdifObjectClass();

        DirContext top = getConnection().getSchema("");
        DirContext schema = (DirContext) top.lookup("ClassDefinition/" + objName);
        Attributes atrs = schema.getAttributes("");
        Attribute name = atrs.get("NAME");
        NamingEnumeration nameValue = name.getAll();
        objClass.setName((String) nameValue.next());
        objClass.setLdapUrl(this.getLocation());

        Attribute sup = atrs.get("SUP");
        if (sup != null) {
            NamingEnumeration supValue = sup.getAll();
            String sups = "";
            while (supValue.hasMore()) {
                String a = (String) supValue.next();
                sups += a + ", ";
            }
            if (sups.length() > 1) {
                sups = sups.substring(0, sups.length() - 2);
            }
            objClass.setSuper(sups);
        }

        Attribute may = atrs.get("MAY");
        if (may != null) {
            NamingEnumeration mayValue = may.getAll();
            while (mayValue.hasMore()) {
                String a = (String) mayValue.next();
                objClass.addMay(a);
            }
        }

        Attribute must = atrs.get("MUST");
        if (must != null) {
            NamingEnumeration mustValue = must.getAll();
            while (mustValue.hasMore()) {
                String a = (String) mustValue.next();
                objClass.addMust(a);
            }
        }
        objClass.setSelected(new ArrayList());
        return objClass;
    }
}

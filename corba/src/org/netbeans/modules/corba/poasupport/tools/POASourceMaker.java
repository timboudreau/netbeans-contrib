/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.poasupport.tools;

import org.openide.text.IndentEngine;
import org.openide.text.PositionRef;
import org.openide.text.Line;
import org.openide.src.ClassElement;
import org.openide.src.MethodElement;
import org.openide.src.MethodParameter;
import org.openide.src.Identifier;
import org.openide.src.Type;
import org.openide.util.MapFormat;
import org.openide.util.WeakListener;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.LineCookie;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.corba.settings.*;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.java.JavaEditor;
import org.netbeans.modules.java.JavaDataObject;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import java.io.*;
import java.util.*;
import javax.swing.text.StyledDocument;
import java.lang.reflect.Modifier;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Dusan Balek
 */

public class POASourceMaker implements PropertyChangeListener {
    
    private static Object GEN_LOCK = new Object();
    private static Hashtable INIT_POAS_NAMES = new Hashtable();
    private static HashSet INIT_SERVANTS_NAMES = new HashSet();
    private static HashSet ACTIVATE_POAS_NAMES = new HashSet();
    private static Vector basicMatchers = new Vector();
    
    private ClassElement classElement;
    private boolean initialized;
    private boolean writeable;
    private int poaIndex;
    
    private Hashtable poaOffsets;
    private Hashtable elementOffsets;
    private int offset;
    
    static {
        Object[] _os = POASupport.getCORBASettings().getBeans();
        for (int i = 0; i < _os.length; i++) {
            POASettings _ps = ((ORBSettings)_os[i]).getPOASettings();
            if (_ps != null) {
                INIT_POAS_NAMES.put(((ORBSettings)_os[i]).getInitPOASection(),((ORBSettings)_os[i]).getORBTag());
                INIT_SERVANTS_NAMES.add(_ps.getSectionInitServants());
                ACTIVATE_POAS_NAMES.add(_ps.getSectionActivatePOAs());
                try {
                    RE _matcher = new RE(_ps.getGetRootPOAPattern());
                    basicMatchers.add(_matcher);
                }
                catch(Exception e){
                }
            }
        }
    }
    
    private RootPOAElement rootPOA = null;
    private JavaEditor je = null;
    private JavaDataObject jdo = null;
    private JavaEditor.SimpleSection initPOAsSection = null;
    private JavaEditor.SimpleSection activatePOAsSection = null;
    private JavaEditor.SimpleSection initServantsSection = null;
    private POASettings poaSettings = null;
    private String orbTag = null;
    
    /* Is source modified from the last scanning of POA hierarchy */
    private boolean sourceModified = false;
    private ChangeListener changeListener = null;
    private PropertyChangeListener weak = WeakListener.propertyChange (this, POASupport.getCORBASettings());
    
    /** Creates new POASourceMaker */
    
    public POASourceMaker(ClassElement _classElement) {
        classElement = _classElement;
    }
    
    public ClassElement getClassElement() {
        return classElement;
    }
    
    public String getORBTag() {
        return orbTag;
    }
    
    public void setORBTag(String newTag) {
        if (newTag == null || orbTag == null || newTag.equals(orbTag))
            return;
        String msg = POASupport.getString("MSG_Confirm_ORB_Change");
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
        if (NotifyDescriptor.CANCEL_OPTION.equals (TopManager.getDefault().notify(desc)))
            return;
        try {
            ORBSettings os = POASupport.getCORBASettings().getSettingByTag(newTag);
            poaSettings = os.getPOASettings();
            if (poaSettings != null) {
                if (initPOAsSection != null && !initPOAsSection.getName().equals(os.getInitPOASection()))
                    initPOAsSection.setName(os.getInitPOASection());
                if (initServantsSection != null && !initServantsSection.getName().equals(poaSettings.getSectionInitServants()))
                    initServantsSection.setName(poaSettings.getSectionInitServants());
                if (activatePOAsSection != null && !activatePOAsSection.getName().equals(poaSettings.getSectionActivatePOAs()))
                    activatePOAsSection.setName(poaSettings.getSectionActivatePOAs());
            }
            else {
                if (initPOAsSection != null) {
                    initPOAsSection.setText("");
                    initPOAsSection.removeSection();
                }
                if (initServantsSection != null) {
                    initServantsSection.setText("");
                    initServantsSection.removeSection();
                }
                if (activatePOAsSection != null) {
                    activatePOAsSection.setText("");
                    activatePOAsSection.removeSection();
                }
            }
            orbTag = newTag;
            sourceModified = true;
            propertyChange(new PropertyChangeEvent(this, "_M_orb_tag", newTag, newTag));
        }
        catch(Exception e) {
        }
    }
    
    public void setChangeListener(ChangeListener l) {
        if (je != null) {
            je.removeChangeListener(changeListener);
            je.addChangeListener(l);
        }
        changeListener = l;
    }
    
    /* Methods for analysing POA sources */
    
    public synchronized boolean checkForPOA() {
        try {
            String mBody = getMainBody();
            if (mBody != null && basicMatch(mBody))
                return true;
        }
        catch (Exception e) {
        }
        return false;
    }
    
    private synchronized void reinit() {
        if (je != null)
            je.removeChangeListener(changeListener);
        POASupport.getCORBASettings().removePropertyChangeListener (weak);
        initialized = false;
        writeable = false;
        try {
            jdo = (JavaDataObject)classElement.getSource().getCookie(JavaDataObject.class);
            if (jdo.isValid()) {
                je = (JavaEditor)jdo.getCookie (JavaEditor.class);
                je.addChangeListener(changeListener);
                ORBSettings orbSettings = null;
                Iterator sections = je.getGuardedSectionNames();
                while (sections.hasNext()) {
                    String name = (String)sections.next();
                    String _tag = (String)INIT_POAS_NAMES.get(name);
                    if (_tag != null) {
                        initPOAsSection = je.findSimpleSection(name);
                        orbTag = _tag;
                        orbSettings = POASupport.getCORBASettings().getSettingByTag(orbTag);
                    }
                    else if (INIT_SERVANTS_NAMES.contains(name))
                        initServantsSection = je.findSimpleSection(name);
                    else if (ACTIVATE_POAS_NAMES.contains(name))
                        activatePOAsSection = je.findSimpleSection(name);
                }
                if ((initPOAsSection != null)&&(activatePOAsSection != null)&&(initServantsSection != null))
                    initialized = true;
                if (orbSettings == null)
                    orbSettings = POASupport.getCORBASettings().getActiveSetting();
                poaSettings = orbSettings.getPOASettings();
                POASupport.getCORBASettings().addPropertyChangeListener (weak);
                if (orbTag != null && orbTag.equals(POASupport.getCORBASettings().getActiveSetting().getORBTag()))
                    writeable = true;
            }
        }
        catch (Exception e) {
        }
    }
    
    public synchronized RootPOAElement scanPOAHierarchy() {
        sourceModified = false;
        rootPOA = null;
        reinit();
        if (initialized) {
            try {
                RE rootPOAMatcher = new RE(poaSettings.getRootPOAPattern());
                Hashtable poas = new Hashtable();
                String text = initPOAsSection.getText();
                if (rootPOAMatcher.match(text)) {
                    rootPOA = new RootPOAElement(rootPOAMatcher.getParen(1), rootPOAMatcher.getParen(2), writeable, this);
                    poas.put(rootPOAMatcher.getParen(1), rootPOA);
                    offset = rootPOAMatcher.getParenEnd(0);
                    poaOffsets = new Hashtable();
                    text = text.substring(rootPOAMatcher.getParenEnd(0));
                    scanPOAs(text, poas);
                    text = initServantsSection.getText();
                    offset = 0;
                    elementOffsets = new Hashtable();
                    text = scanPOAActivators(text, poas);
                    text = scanServants(text, poas);
                    text = scanDefaultServants(text, poas);
                    scanServantManagers(text, poas);
                }
            }
            catch (Exception e) {
            }
        }
        if (rootPOA == null)
            rootPOA = new RootPOAElement("", "", false, this); // NOI18N
        rootPOA.addPOAListener(new PCGPOAListener());
        return rootPOA;
    }
    
    private void scanPOAs(String text, Hashtable poas) throws RESyntaxException {
        RE policyDeclarationMatcher = new RE(poaSettings.getPoliciesDeclarationPattern());
        if (policyDeclarationMatcher.match(text)) {
            offset += policyDeclarationMatcher.getParenEnd(0);
            text = text.substring(policyDeclarationMatcher.getParenEnd(0));
        }
        RE poaMatcher = new RE(poaSettings.getPOAPattern());
        while (text.length() > 0)
            if (poaMatcher.match(text)) {
                String poaVarName = poaMatcher.getParen(1);
                String parentPOAVarName = poaMatcher.getParen(2);
                String poaName = poaMatcher.getParen(3);
                String managerName = poaMatcher.getParen(4);
                String policiesText = text.substring(0, poaMatcher.getParenStart(0));
                int poa_offset = offset + poaMatcher.getParenStart(0);
                offset += poaMatcher.getParenEnd(0);
                text = text.substring(poaMatcher.getParenEnd(0));
                Properties policies = scanPolicies(policiesText);
                POAElement ppe = (POAElement)poas.get(parentPOAVarName);
                POAElement newElement = new POAElement(ppe, ppe.getRootPOA(), ppe.isWriteable());
                newElement.setPOAName(fromJavaInitializationString(poaName));
                newElement.setVarName(poaVarName);
                newElement.setManager(managerName.equals("null") ? null: managerName); // NOI18N
                newElement.setPolicies(policies);
                ppe.addChildPOA(newElement);
                poas.put(poaVarName, newElement);
                poaOffsets.put(newElement, new Integer(poa_offset));
            }
            else
                break;
    }
    
    private String scanPOAActivators(String text, Hashtable poas) throws RESyntaxException {
        RE activatorMatcher = new RE(poaSettings.getPOAActivatorPattern());
        RE instantiationMatcher = new RE(poaSettings.getServantInstancePattern());
        while (text.length() > 0)
            if (activatorMatcher.match(text)) {
                String instanceText = text.substring(0, activatorMatcher.getParenStart(0));
                int element_offset = offset + activatorMatcher.getParenStart(0);
                offset += activatorMatcher.getParenEnd(0);
                text = text.substring(activatorMatcher.getParenEnd(0));
                String varName = activatorMatcher.getParen(2);
                String parentPOAVarName = activatorMatcher.getParen(1);
                String typeName = null;
                String ctor = null;
                if (instantiationMatcher.match(instanceText)){
                    typeName = instantiationMatcher.getParen(1);
                    ctor = instantiationMatcher.getParen(2);
                }
                POAElement ppe = (POAElement)poas.get(parentPOAVarName);
                POAActivatorElement newElement = new POAActivatorElement(ppe, ppe.isWriteable());
                newElement.setVarName(varName);
                newElement.setTypeName(typeName);
                newElement.setConstructor(ctor);
                ppe.setPOAActivator(newElement);
                elementOffsets.put(newElement, new Integer(element_offset));
            }
            else
                break;
        return text;
    }
    
    private String scanServants(String text, Hashtable poas) throws RESyntaxException {
        RE servantMatcher = new RE(poaSettings.getServantPattern());
        RE instantiationMatcher = new RE(poaSettings.getServantInstancePattern());
        while (text.length() > 0)
            if (servantMatcher.match(text)) {
                String instanceText = text.substring(0, servantMatcher.getParenStart(0));
                int element_offset = offset + servantMatcher.getParenStart(0);
                offset += servantMatcher.getParenEnd(0);
                text = text.substring(servantMatcher.getParenEnd(0));
                String servantVarName;
                String parentPOAVarName;
                String id;
                if (servantMatcher.getParenCount() <= 4) {
                    servantVarName = servantMatcher.getParen(3);
                    parentPOAVarName = servantMatcher.getParen(2);
                    id = servantMatcher.getParen(1);
                }
                else {
                    servantVarName = servantMatcher.getParen(6);
                    parentPOAVarName = servantMatcher.getParen(4);
                    id = servantMatcher.getParen(5);
                }
                String typeName = null;
                String ctor = null;
                if (instantiationMatcher.match(instanceText)){
                    typeName = instantiationMatcher.getParen(1);
                    ctor = instantiationMatcher.getParen(2);
                }
                POAElement ppe = (POAElement)poas.get(parentPOAVarName);
                ServantElement newElement = new ServantElement(ppe, ppe.isWriteable());
                newElement.setVarName(servantVarName);
                if (newElement.getIDAssignmentMode().equals(POASettings.SERVANT_WITH_SYSTEM_ID))
                    newElement.setIDVarName(id);
                else
                    newElement.setObjID(fromJavaInitializationString(id));
                newElement.setTypeName(typeName);
                newElement.setConstructor(ctor);
                ppe.addServant(newElement);
                elementOffsets.put(newElement, new Integer(element_offset));
            }
            else
                break;
        return text;
    }
    
    private String scanDefaultServants(String text, Hashtable poas) throws RESyntaxException {
        RE defaultServantMatcher = new RE(poaSettings.getDefaultServantPattern());
        RE instantiationMatcher = new RE(poaSettings.getServantInstancePattern());
        while (text.length() > 0)
            if (defaultServantMatcher.match(text)) {
                String instanceText = text.substring(0, defaultServantMatcher.getParenStart(0));
                int element_offset = offset + defaultServantMatcher.getParenStart(0);
                offset += defaultServantMatcher.getParenEnd(0);
                text = text.substring(defaultServantMatcher.getParenEnd(0));
                String varName = defaultServantMatcher.getParen(2);
                String parentPOAVarName = defaultServantMatcher.getParen(1);
                String typeName = null;
                String ctor = null;
                if (instantiationMatcher.match(instanceText)){
                    typeName = instantiationMatcher.getParen(1);
                    ctor = instantiationMatcher.getParen(2);
                }
                POAElement ppe = (POAElement)poas.get(parentPOAVarName);
                DefaultServantElement newElement = new DefaultServantElement(ppe, ppe.isWriteable());
                newElement.setVarName(varName);
                newElement.setTypeName(typeName);
                newElement.setConstructor(ctor);
                ppe.setDefaultServant(newElement);
                elementOffsets.put(newElement, new Integer(element_offset));
            }
            else
                break;
        return text;
    }
    
    private String scanServantManagers(String text, Hashtable poas) throws RESyntaxException {
        RE servantManagerMatcher = new RE(poaSettings.getServantManagerPattern());
        RE instantiationMatcher = new RE(poaSettings.getServantInstancePattern());
        while (text.length() > 0)
            if (servantManagerMatcher.match(text)) {
                String instanceText = text.substring(0, servantManagerMatcher.getParenStart(0));
                int element_offset = offset + servantManagerMatcher.getParenStart(0);
                offset += servantManagerMatcher.getParenEnd(0);
                text = text.substring(servantManagerMatcher.getParenEnd(0));
                String varName = servantManagerMatcher.getParen(2);
                String parentPOAVarName = servantManagerMatcher.getParen(1);
                String typeName = null;
                String ctor = null;
                if (instantiationMatcher.match(instanceText)){
                    typeName = instantiationMatcher.getParen(1);
                    ctor = instantiationMatcher.getParen(2);
                }
                POAElement ppe = (POAElement)poas.get(parentPOAVarName);
                ServantManagerElement newElement = new ServantManagerElement(ppe, ppe.isWriteable());
                newElement.setVarName(varName);
                newElement.setTypeName(typeName);
                newElement.setConstructor(ctor);
                ppe.setServantManager(newElement);
                elementOffsets.put(newElement, new Integer(element_offset));
            }
            else
                break;
        return text;
    }
    
    private Properties scanPolicies(String text) throws RESyntaxException {
        RE policiesMatcher = new RE(poaSettings.getPoliciesHeaderPattern());
        Vector prepPolicyMatchers = new Vector();
        Vector policyMatchers = new Vector();
        ListIterator policies = poaSettings.getPolicies().listIterator();
        while (policies.hasNext()) {
            POAPolicyDescriptor policy = (POAPolicyDescriptor)policies.next();
            String prepare_pattern = policy.getPrepareCodePattern();
            if (prepare_pattern != null)
                prepPolicyMatchers.add(new NamedMatcher(policy.getName(), new RE(prepare_pattern)));
            String create_pattern = policy.getCreateCodePattern();
            if (create_pattern != null)
                policyMatchers.add(new NamedMatcher(policy.getName(), new RE(create_pattern)));
        }
        Hashtable values = new Hashtable();
        Properties ret = new Properties();
        if (policiesMatcher.match(text)) {
            String prepText = text.substring(0, policiesMatcher.getParenStart(0));
            String policyText = text.substring(policiesMatcher.getParenEnd(0));
            for (int i = 0; i < prepPolicyMatchers.size(); i++) {
                NamedMatcher prepMatcher = (NamedMatcher)prepPolicyMatchers.get(i);
                if (prepMatcher.matcher.match(prepText)) {
                    values.put(prepMatcher.name, prepMatcher.matcher.getParen(1));
                    prepText = prepText.substring(0, prepMatcher.matcher.getParenStart(0)) + prepText.substring(prepMatcher.matcher.getParenEnd(0));
                }
            }
            for (int i = 0; i < policyMatchers.size(); i++) {
                NamedMatcher polMatcher = (NamedMatcher)policyMatchers.get(i);
                if (polMatcher.matcher.match(policyText)) {
                    ret.setProperty(polMatcher.name, (polMatcher.matcher.getParenCount() > 1) ? polMatcher.matcher.getParen(1) : (String)values.get(polMatcher.name));
                    policyText = policyText.substring(0, polMatcher.matcher.getParenStart(0)) + policyText.substring(polMatcher.matcher.getParenEnd(0));
                }
            }
        }
        return ret;
    }
    
    private boolean basicMatch(String text) {
        ListIterator _li = basicMatchers.listIterator();
        while (_li.hasNext()) {
            RE _matcher = (RE)_li.next();
            if (_matcher.match(text))
                return true;
        }
        return false;
    }
    
    private String getMainBody() {
        MethodElement[] methods = classElement.getMethods();
        Identifier mainId = Identifier.create("main"); // NOI18N
        for (int i = 0; i < methods.length; i++) {
            MethodElement m = methods[i];
            if (m.getName().equals(mainId)) {
                if (m.getReturn() == Type.VOID) {
                    if ((m.getModifiers() & ~Modifier.FINAL) == Modifier.PUBLIC + Modifier.STATIC) {
                        MethodParameter[] params = m.getParameters();
                        if (params.length == 1) {
                            Type typ = params[0].getType();
                            if (typ.isArray()) {
                                typ = typ.getElementType();
                                if (typ.isClass()) {
                                    if (typ.getClassName().getFullName().equals("java.lang.String")) { // NOI18N
                                        return m.getBody();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /* Methods for generating POA sources */
    
    private String[] createPOAHierarchy() throws IOException {
        StyledDocument doc = je.openDocument();
        IndentEngine engine = IndentEngine.find(doc);
        StringWriter poaCodeBuffer = new StringWriter();
        Writer poaCodeWriter = engine.createWriter(doc, initPOAsSection.getBegin().getOffset(), poaCodeBuffer);
        StringWriter poaActivationCodeBuffer = new StringWriter();
        Writer poaActivationCodeWriter = engine.createWriter(doc, activatePOAsSection.getBegin().getOffset(), poaActivationCodeBuffer);
        poaOffsets = new Hashtable();
        poaIndex = 1;
        
        Map m = new HashMap ();
        m.put ("ROOT_POA_VAR_NAME", rootPOA.getVarName()); // NOI18N
        m.put ("ORB_VAR_NAME", rootPOA.getORBVarName()); // NOI18N
        MapFormat f = new MapFormat (m);
        f.setLeftBrace ("__"); // NOI18N
        f.setRightBrace ("__"); // NOI18N
        poaCodeWriter.write(f.format (poaSettings.getRootPOAInit()));
        poaActivationCodeWriter.write(rootPOA.getVarName() + poaSettings.getActivatePOA());
        ListIterator otherPOAs = rootPOA.getPOAListIterator();
        boolean needsPoliciesDeclaration = true;
        if (otherPOAs.hasNext())
            while (otherPOAs.hasNext()) {
                POAElement next = (POAElement)otherPOAs.next();
                if (needsPoliciesDeclaration) {
                    poaCodeWriter.write(poaSettings.getPoliciesDeclaration());
                    needsPoliciesDeclaration = false;
                }
                addPOACode(((POAElement)next).getParentPOA().getVarName(), (POAElement)next, poaCodeWriter, poaActivationCodeWriter, poaCodeBuffer);
            }
        poaCodeWriter.close();
        poaActivationCodeWriter.close();
        
        return new String[] {
            poaCodeBuffer.toString(),
            poaActivationCodeBuffer.toString()
        };
    }
    
    private void addPOACode(String parentPOAVarName, POAElement poa, Writer poaCodeWriter, Writer poaActivationCodeWriter, StringWriter buffer) throws IOException {
        addPOAPoliciesCode(poa.getPolicies(), poaCodeWriter);
        poaCodeWriter.flush();
        poaOffsets.put(poa, new Integer(buffer.toString().length()));
        Map m = new HashMap ();
        m.put ("POA_VAR_NAME", poa.getVarName()); // NOI18N
        m.put ("PARENT_POA_VAR_NAME", parentPOAVarName); // NOI18N
        m.put ("POA_NAME", toJavaInitializationString(poa.getPOAName())); // NOI18N
        m.put ("POA_MANAGER", poa.getManager() == null ? "null" : poa.getManager() + poaSettings.getGetPOAManagerMethod()); // NOI18N
        MapFormat f = new MapFormat (m);
        f.setLeftBrace ("__"); // NOI18N
        f.setRightBrace ("__"); // NOI18N
        poaCodeWriter.write(f.format (poaSettings.getCreatePOA()));
        if (poa.getManager() == null)
            poaActivationCodeWriter.write(poa.getVarName() + poaSettings.getActivatePOA());
        poaIndex++;
    }
    
    private void addPOAPoliciesCode(Properties policies, Writer poaCodeWriter) throws IOException {
        
        poaCodeWriter.write("\n"); // NOI18N
        for (Enumeration names = policies.propertyNames() ; names.hasMoreElements() ;) {
            String name = (String)names.nextElement();
            String template = poaSettings.getPolicyByName(name).getPrepareCode();
            if (template != null) {
                Map m = new HashMap ();
                m.put ("ROOT_POA_VAR_NAME", rootPOA.getVarName()); // NOI18N
                m.put ("ORB_VAR_NAME", rootPOA.getORBVarName()); // NOI18N
                m.put ("POLICY_VALUE", policies.getProperty(name)); // NOI18N
                m.put ("$INDEX$", String.valueOf(poaIndex)); // NOI18N
                MapFormat f = new MapFormat (m);
                f.setLeftBrace ("__"); // NOI18N
                f.setRightBrace ("__"); // NOI18N
                poaCodeWriter.write(f.format (template));
            }
        }
        poaCodeWriter.write(poaSettings.getPoliciesHeader());
        for (Enumeration names = policies.propertyNames() ; names.hasMoreElements() ;) {
            String name = (String)names.nextElement();
            String template = poaSettings.getPolicyByName(name).getCreateCode();
            if (template != null) {
                Map m = new HashMap ();
                m.put ("ROOT_POA_VAR_NAME", rootPOA.getVarName()); // NOI18N
                m.put ("ORB_VAR_NAME", rootPOA.getORBVarName()); // NOI18N
                m.put ("POLICY_VALUE", policies.getProperty(name)); // NOI18N
                m.put ("$INDEX$", String.valueOf(poaIndex)); // NOI18N
                MapFormat f = new MapFormat (m);
                f.setLeftBrace ("__"); // NOI18N
                f.setRightBrace ("__"); // NOI18N
                poaCodeWriter.write(f.format (template));
                if (names.hasMoreElements())
                    poaCodeWriter.write(poaSettings.getPoliciesSeparator());
                poaCodeWriter.write("\n"); // NOI18N
            }
        }
        poaCodeWriter.write(poaSettings.getPoliciesFooter());
    }
    
    private String createPOAMembers() throws IOException {
        StyledDocument doc = je.openDocument();
        IndentEngine engine = IndentEngine.find(doc);
        StringWriter memberCodeBuffer = new StringWriter();
        Writer memberCodeWriter = engine.createWriter(doc, initServantsSection.getBegin().getOffset(), memberCodeBuffer);
        elementOffsets = new Hashtable();
        
        ListIterator activators = rootPOA.getPOAActivatorListIterator();
        while (activators.hasNext()) {
            POAMemberElement member = (POAMemberElement)activators.next();
            addPOAMemberCode(member, memberCodeWriter, memberCodeBuffer);
        }
        ListIterator servants = rootPOA.getServantListIterator();
        while (servants.hasNext()) {
            POAMemberElement member = (POAMemberElement)servants.next();
            addPOAMemberCode(member, memberCodeWriter, memberCodeBuffer);
        }
        ListIterator defaultServants = rootPOA.getDefaultServantListIterator();
        while (defaultServants.hasNext()) {
            POAMemberElement member = (POAMemberElement)defaultServants.next();
            addPOAMemberCode(member, memberCodeWriter, memberCodeBuffer);
        }
        ListIterator servantManagers = rootPOA.getServantManagerListIterator();
        while (servantManagers.hasNext()) {
            POAMemberElement member = (POAMemberElement)servantManagers.next();
            addPOAMemberCode(member, memberCodeWriter, memberCodeBuffer);
        }
        memberCodeWriter.close();
        
        return memberCodeBuffer.toString();
    }
    
    private void addPOAMemberCode(POAMemberElement member, Writer memberCodeWriter, StringWriter buffer) throws IOException {
        
        Map m = new HashMap ();
        m.put ("PARENT_POA_VAR_NAME", member.getParentPOA().getVarName()); // NOI18N
        m.put ("SERVANT_VAR_NAME", member.getVarName()); // NOI18N
        if (member instanceof ServantElement) {
            m.put ("ID_VAR_NAME", ((ServantElement)member).getIDVarName()); // NOI18N
            m.put ("ID", toJavaInitializationString(((ServantElement)member).getObjID())); // NOI18N
        }
        m.put("SERVANT_TYPE_NAME", member.getTypeName()); // NOI18N
        m.put("SERVANT_CONSTRUCTOR", member.getConstructor()); // NOI18N
        MapFormat f = new MapFormat (m);
        f.setLeftBrace ("__"); // NOI18N
        f.setRightBrace ("__"); // NOI18N
        if ((member.getTypeName()!= null)&&(member.getConstructor()!= null))
            memberCodeWriter.write(f.format (poaSettings.getCreateServantInstance()));
        memberCodeWriter.flush();
        elementOffsets.put(member, new Integer(buffer.toString().length()));
        if (member instanceof POAActivatorElement)
            memberCodeWriter.write(f.format (poaSettings.getSetPOAActivator()));
        else if (member instanceof DefaultServantElement)
            memberCodeWriter.write(f.format (poaSettings.getSetDefaultServant()));
        else if (member instanceof ServantManagerElement)
            memberCodeWriter.write(f.format (poaSettings.getSetServantManager()));
        else if (member instanceof ServantElement) {
            if (((ServantElement)member).getIDAssignmentMode() == POASettings.SERVANT_WITH_SYSTEM_ID)
                memberCodeWriter.write(f.format (poaSettings.getActivateServantWithSystemId()));
            else
                memberCodeWriter.write(f.format (poaSettings.getActivateServantWithUserId()));
        }
    }
    
    private synchronized void regeneratePOAHierarchy() {
        try {
            if (initialized && writeable) {
                String originalInitText = initPOAsSection.getText();
                String originalActivateText = activatePOAsSection.getText();
                String[] newTexts = createPOAHierarchy();
                if (!newTexts[0].equals(originalInitText)) {
                    initPOAsSection.setText(newTexts[0]);
                }
                if (!newTexts[1].equals(originalActivateText)) {
                    activatePOAsSection.setText(newTexts[1]);
                }
                sourceModified = true;
            }
        } catch (IOException e) {
            throw new InternalError(); // cannot happen
        }
    }
    
    private synchronized void regeneratePOAMembers() {
        try {
            if (initialized && writeable) {
                String originalText = initServantsSection.getText();
                String newText = createPOAMembers();
                if (!newText.equals(originalText)) {
                    initServantsSection.setText(newText);
                }
                sourceModified = true;
            }
        } catch (IOException e) {
            throw new InternalError(); // cannot happen
        }
    }
    
    
    public synchronized boolean isSourceModified() {
        if (jdo != null)
            return sourceModified || !jdo.isValid();
        return sourceModified;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        if ("_M_orb_tag".equals(e.getPropertyName()) && changeListener != null) { // NOI18N
            if (orbTag == null) {
                poaSettings = POASupport.getCORBASettings().getSettingByTag((String)e.getNewValue()).getPOASettings();
                changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
            else if (orbTag.equals((String)e.getOldValue()) || orbTag.equals((String)e.getNewValue())) {
                changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
        }
    }
    
    public OpenCookie getOpenCookie() {
        return je;
    }
    
    public void setLinePosition(Object tgt) {
        try {
            if (tgt instanceof POAElement && initPOAsSection != null) {
                PositionRef pos = initPOAsSection.getBegin();
                Integer i = (Integer)poaOffsets.get(tgt);
                int idx = (i == null) ? 0 : i.intValue();
                LineNumberReader lineCounter = new LineNumberReader(new StringReader(initPOAsSection.getText()));
                lineCounter.skip(idx);
                Line line = je.getLineSet().getCurrent(pos.getLine()+lineCounter.getLineNumber());
                line.show (Line.SHOW_GOTO, 0);
                return;
            }
            if (tgt instanceof POAMemberElement && initServantsSection != null) {
                PositionRef pos = initServantsSection.getBegin();
                Integer i = (Integer)elementOffsets.get(tgt);
                int idx = (i == null) ? 0 : i.intValue();
                LineNumberReader lineCounter = new LineNumberReader(new StringReader(initServantsSection.getText()));
                lineCounter.skip(idx);
                Line line = je.getLineSet().getCurrent(pos.getLine()+lineCounter.getLineNumber());
                line.show (Line.SHOW_GOTO, 0);
                return;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private String toJavaInitializationString (String __str) {
        if (__str == null)
            return null;
        StringBuffer __buf = new StringBuffer();
        for (int i = 0; i < __str.length(); i++) {
            char c = __str.charAt(i);
            switch (c) {
                case '\b': __buf.append("\\b"); break; // NOI18N
                case '\t': __buf.append("\\t"); break; // NOI18N
                case '\n': __buf.append("\\n"); break; // NOI18N
                case '\f': __buf.append("\\f"); break; // NOI18N
                case '\r': __buf.append("\\r"); break; // NOI18N
                case '\"': __buf.append("\\\""); break; // NOI18N
                case '\'': __buf.append("\\'"); break; // NOI18N
                case '\\': __buf.append("\\\\"); break; // NOI18N
                default:
                    if (c >= 0x0020 && c <= 0x007f)
                        __buf.append(c);
                    else {
                        __buf.append("\\u"); // NOI18N
                        String hex = Integer.toHexString(c);
                        for (int j = 0; j < 4 - hex.length(); j++)
                            __buf.append('0');
                        __buf.append(hex);
                    }
            }
        }
        return __buf.toString();
    }
    
    private String fromJavaInitializationString (String __str) {
        if (__str == null)
            return null;
        StringBuffer __buf = new StringBuffer();
        int i = 0;
        int j = __str.indexOf('\\');
        while (j != -1) {
            __buf.append(__str.substring(i, j));
            char c = __str.charAt(j + 1);
            switch (c) {
                case 'b': __buf.append('\b'); i = j + 2; break; // NOI18N
                case 't': __buf.append('\t'); i = j + 2; break; // NOI18N
                case 'n': __buf.append('\n'); i = j + 2; break; // NOI18N
                case 'f': __buf.append('\f'); i = j + 2; break; // NOI18N
                case 'r': __buf.append('\r'); i = j + 2; break; // NOI18N
                case '"': __buf.append('\"'); i = j + 2; break; // NOI18N
                case '\'': __buf.append('\''); i = j + 2; break; // NOI18N
                case '\\': __buf.append('\\'); i = j + 2; break; // NOI18N
                case 'u':
                    int __val = Integer.parseInt(__str.substring(j+2, j+6), 16);
                    __buf.append(Character.forDigit(__val, 10));
                    i = j + 6;
                    break;
            }
            j = __str.indexOf('\\', i);
        }
        __buf.append(__str.substring(i));
        return __buf.toString();
    }
    
    private class PCGPOAListener implements POAListener
    {
        public void poaHierarchyChanged() {
            regeneratePOAHierarchy();
        }
        
        public void poaMembersChanged() {
            regeneratePOAMembers();
        }
        
    }
    
}

class NamedMatcher {
    
    String name;
    RE matcher;
    
    NamedMatcher(String _name, RE _matcher) {
        name = _name;
        matcher = _matcher;
    }
}

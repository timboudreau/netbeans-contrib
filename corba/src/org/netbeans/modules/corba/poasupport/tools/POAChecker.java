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

import java.text.MessageFormat;
import java.util.*;
import java.awt.Dialog;

import org.openide.src.Type;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.settings.*;

/**
 *
 * @author  Dusan Balek
 * @version
 */
public class POAChecker {
    
    public static boolean checkTypeName(String name, boolean notify) {
        try {
            if (name == null)
                return false;
            Type.parse(name);
        }
        catch (IllegalArgumentException e) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Not_Valid_Identifier"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        return true;
    }
    
    public static boolean checkPOAName(String name, POAElement element, boolean notify) {
        if (name.equals("") || !element.canUseAsPOAName( name )) { // NOI18N
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        return true;
    }
    
    public static boolean checkPOAVarName(String name, POAElement element, boolean notify) {
        if ( !Utilities.isJavaIdentifier( name ) ) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Not_Valid_Identifier"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        if (!element.canUseAsNewVarName( name )) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        return true;
    }
    
    public static boolean checkPOAMemberVarName(String name, POAMemberElement element, boolean generateInstanceCode, boolean notify) {
        if ( !Utilities.isJavaIdentifier( name ) ) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Not_Valid_Identifier"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        if (generateInstanceCode) {
            if ( !element.canUseAsNewVarName(name) ) {
                if (notify) {
                    String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                    TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
                }
                return false;
            }
        }
        else {
            if ( !element.canUseAsVarName(name) ) {
                if (notify) {
                    String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                    TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
                }
                return false;
            }
        }
        if (element instanceof ServantElement && name.equals(((ServantElement)element).getIDVarName())) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
	}
        return true;
    }
    
    public static boolean checkServantID(String id, ServantElement element, boolean notify) {
        if (!element.canUseAsServantID( id )) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {id});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        return true;
    }
    
    public static boolean checkServantIDVarName(String name, ServantElement element, boolean notify) {
        if ( !Utilities.isJavaIdentifier( name ) ) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Not_Valid_Identifier"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        if (name.equals("") || !element.canUseAsNewVarName( name )) { // NOI18N
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        if (name.equals(element.getVarName())) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Identifier_Already_Exists"), new Object[] {name});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
	}
        return true;
    }
    
    public static boolean canDeletePOA (POAElement element) {
        if ((element.getChildPOAs().size() > 0) || (element.getServants().size() > 0) ||
        (element.getPOAActivator() != null) || (element.getDefaultServant() != null) ||
        (element.getServantManager() != null)) {
            String msg = MessageFormat.format(POASupport.getString("MSG_Confirm_Delete_Non_Empty_POA"), new Object[] {element.getPOAName()});
            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
            return (NotifyDescriptor.YES_OPTION.equals (TopManager.getDefault().notify(desc))) ? true : false;
        }
        return true;
    }
    
    public static boolean checkPOAPoliciesChange(POAElement element, Properties policies, String name, String value, boolean notify) {
        Properties newPolicyValues = new Properties();
        Properties oldPolicyValues = new Properties();
        boolean legal = false;
        POASettings settings;
        String _tag = element.getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        ListIterator policyList = settings.getPolicies().listIterator();
        while (policyList.hasNext()) {
            POAPolicyDescriptor policy = (POAPolicyDescriptor)policyList.next();
            String n = policy.getName();
            if (n.equals(name) && (policy.getValueByName(value) != null || policy.getValues().size() == 0))
                legal = true;
            String v = policies.getProperty(n);
            if (v == null) {
                if (policy.getValues().size() > 0)
                    v = ((POAPolicyValueDescriptor)policy.getValues().get(0)).getName();
                else
                    v = "";
            }
            oldPolicyValues.setProperty(n, v);
        }
        if (!legal) {
            return false;
        }
        if (!addRequiredPolicyValue(settings, newPolicyValues, oldPolicyValues, name, value)) {
            if (notify) {
                String msg = MessageFormat.format(POASupport.getString("MSG_Invalid_POA_Policy_Dependencies"), new Object[] {name, value});
                TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
            }
            return false;
        }
        int count;
        do {
            count = oldPolicyValues.size();
            Properties conflictPolicyValues = new Properties();
            findConflictPolicyValues(settings, newPolicyValues, oldPolicyValues, conflictPolicyValues);
            for (Enumeration e = conflictPolicyValues.propertyNames(); e.hasMoreElements();) {
                String confName = (String)e.nextElement();
                String confValue = conflictPolicyValues.getProperty(confName);
                String newValue;
                ListIterator valueList = settings.getPolicyByName(confName).getValues().listIterator();
                Vector availableValues = new Vector();
                while (valueList.hasNext()) {
                    POAPolicyValueDescriptor __val = (POAPolicyValueDescriptor)valueList.next();
                    if ((!confValue.equals(__val.getName())) &&
                    (addRequiredPolicyValue(settings, (Properties)newPolicyValues.clone(), (Properties)oldPolicyValues.clone(), confName, __val.getName())))
                        availableValues.add(__val.getName());
                }
                if (availableValues.size() == 0)
                    return false;
                if (availableValues.size() == 1)
                    newValue = (String)availableValues.get(0);
                else {
                    String msg = MessageFormat.format(POASupport.getString("MSG_Change_POA_Policy_Value_Due_To_Dependencies"), new Object[] {name, value});
                    ComboBoxSelector selector = new ComboBoxSelector(msg, confName, availableValues);
                    if (NotifyDescriptor.OK_OPTION.equals (TopManager.getDefault().notify(selector)))
                        newValue = (String)selector.getSelectedItem();
                    else
                        return false;
                }
                if (!addRequiredPolicyValue(settings, newPolicyValues, oldPolicyValues, confName, newValue)) {
                    if (notify) {
                        String msg = MessageFormat.format(POASupport.getString("MSG_Invalid_POA_Policy_Dependencies"), new Object[] {name, value});
                        TopManager.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
                    }
                    return false;
                }
            }
        } while (oldPolicyValues.size() != count);
        for (Enumeration e = newPolicyValues.propertyNames(); e.hasMoreElements();) {
            String newName = (String)e.nextElement();
            String newVal = newPolicyValues.getProperty(newName);
            String defaultVal;
            List _values = settings.getPolicyByName(newName).getValues();
            if (_values.size() > 0)
                defaultVal = ((POAPolicyValueDescriptor)_values.get(0)).getName();
            else
                defaultVal = "";
            if (newVal.equals(defaultVal))
                policies.remove(newName);
            else
                policies.setProperty(newName, newVal);
        }
        if ((element.getServantManager() != null) && (!isServantManagerEnabled(element, policies))) {
            String msg = MessageFormat.format(POASupport.getString("MSG_Confirm_Delete_ServantManager"), new Object[] {name, value});
            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            if (NotifyDescriptor.CANCEL_OPTION.equals (TopManager.getDefault().notify(desc)))
                return false;
        }
        if ((element.getDefaultServant() != null) && (!isDefaultServantEnabled(element, policies))) {
            String msg = MessageFormat.format(POASupport.getString("MSG_Confirm_Delete_DefaultServant"), new Object[] {name, value});
            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            if (NotifyDescriptor.CANCEL_OPTION.equals (TopManager.getDefault().notify(desc)))
                return false;
        }
        if ((element.getServants().size() > 0) && (checkDisabledServantActivation(element, policies).equals(POASettings.ALL_SERVANTS))) {
            String msg = MessageFormat.format(POASupport.getString("MSG_Confirm_Delete_Servants"), new Object[] {name, value});
            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            if (NotifyDescriptor.CANCEL_OPTION.equals (TopManager.getDefault().notify(desc)))
                return false;
        }
        return true;
    }
    
    private static boolean addRequiredPolicyValue (POASettings settings, Properties newPolicyValues, Properties oldPolicyValues, String name, String value) {
        if (!checkConflict(settings, newPolicyValues, name, value))
            return false;
        newPolicyValues.setProperty(name, value);
        oldPolicyValues.remove(name);
        POAPolicyValueDescriptor _ppvd = settings.getPolicyByName(name).getValueByName(value);
        if (_ppvd == null)
            return true;
        ListIterator requirements = _ppvd.getRequiredPolicies().listIterator();
        while (requirements.hasNext()) {
            POAPolicySimpleDescriptor requirement = (POAPolicySimpleDescriptor)requirements.next();
            String reqName = requirement.getName();
            String reqValue = requirement.getValue();
            if (!addRequiredPolicyValue(settings, newPolicyValues, oldPolicyValues, reqName, reqValue))
                return false;
        }
        return true;
    }
    
    private static void findConflictPolicyValues(POASettings settings, Properties newPolicyValues, Properties oldPolicyValues, Properties conflictPolicyValues) {
        for (Enumeration e = oldPolicyValues.propertyNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            String value = oldPolicyValues.getProperty(name);
            if (!checkConflict(settings, newPolicyValues, name, value)) {
                oldPolicyValues.remove(name);
                conflictPolicyValues.setProperty(name, value);
            }
        }
    }
    
    private static boolean checkConflict(POASettings settings, Properties newPolicyValues, String name, String value) {
        String check_value = newPolicyValues.getProperty(name);
        if (check_value != null)
            if (check_value.equals(value))
                return true;
            else
                return false;
        POAPolicyValueDescriptor _ppvd = settings.getPolicyByName(name).getValueByName(value);
        if (_ppvd != null) {
            ListIterator requirements = _ppvd.getRequiredPolicies().listIterator();
            while (requirements.hasNext()) {
                POAPolicySimpleDescriptor requirement = (POAPolicySimpleDescriptor)requirements.next();
                String reqName = requirement.getName();
                String reqValue = requirement.getValue();
                String curValue = newPolicyValues.getProperty(reqName);
                if ((curValue != null) && (!reqValue.equals(curValue)))
                    return false;
            }
            ListIterator conflicts = _ppvd.getConflictsPolicies().listIterator();
            while (conflicts.hasNext()) {
                POAPolicySimpleDescriptor conflict = (POAPolicySimpleDescriptor)conflicts.next();
                String confName = conflict.getName();
                String confValue = conflict.getValue();
                String curValue = newPolicyValues.getProperty(confName);
                if (confValue.equals(curValue))
                    return false;
            }
        }
        for (Enumeration e = newPolicyValues.propertyNames(); e.hasMoreElements();) {
            String n = (String)e.nextElement();
            String v = newPolicyValues.getProperty(n);
            POAPolicyValueDescriptor _oppvd = settings.getPolicyByName(n).getValueByName(v);
            if (_oppvd == null)
                continue;
            ListIterator otherRequirements = _oppvd.getRequiredPolicies().listIterator();
            while (otherRequirements.hasNext()) {
                POAPolicySimpleDescriptor otherRequirement = (POAPolicySimpleDescriptor)otherRequirements.next();
                String reqName = otherRequirement.getName();
                String reqValue = otherRequirement.getValue();
                if ((reqName.equals(name))&&(!reqValue.equals(value)))
                    return false;
            }
        }
        return true;
    }
    
    public static String checkDisabledServantActivation(POAElement element, Properties policies) {
        POASettings settings;
        String _tag = element.getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        ListIterator policyList = settings.getPolicies().listIterator();
        String alreadyDisabled = null;
        while (policyList.hasNext()) {
            POAPolicyDescriptor policy = (POAPolicyDescriptor)policyList.next();
            String name = policy.getName();
            String valueName = policies.getProperty(name);
            POAPolicyValueDescriptor value = null;
            if (valueName != null)
                value = policy.getValueByName(valueName);
            if (value == null) {
                if (policy.getValues().size() == 0)
                    continue;
                value = (POAPolicyValueDescriptor)policy.getValues().get(0);
            }
            List disabledActions = value.getDisabledActions();
            if (disabledActions.contains(POASettings.ALL_SERVANTS))
                return POASettings.ALL_SERVANTS;
            if (disabledActions.contains(POASettings.SERVANT_WITH_SYSTEM_ID))
                if ((alreadyDisabled != null) && (alreadyDisabled.equals(POASettings.SERVANT_WITH_USER_ID)))
                    return POASettings.ALL_SERVANTS;
                else
                    alreadyDisabled = POASettings.SERVANT_WITH_SYSTEM_ID;
            if (disabledActions.contains(POASettings.SERVANT_WITH_USER_ID))
                if ((alreadyDisabled != null) && (alreadyDisabled.equals(POASettings.SERVANT_WITH_SYSTEM_ID)))
                    return POASettings.ALL_SERVANTS;
                else
                    alreadyDisabled = POASettings.SERVANT_WITH_USER_ID;
        }
        if ((alreadyDisabled != null) && (element.getServants().size() > 0) && (alreadyDisabled.equals(((ServantElement)element.getServants().get(0)).getIDAssignmentMode())))
            return POASettings.ALL_SERVANTS;
        return (alreadyDisabled != null) ? alreadyDisabled : "" ; // NOI18N
    }
    
    public static boolean isServantManagerEnabled(POAElement element, Properties policies) {
        return isEnabled(element, policies, POASettings.SERVANT_MANAGER);
    }
    
    public static boolean isDefaultServantEnabled(POAElement element, Properties policies) {
        return isEnabled(element, policies, POASettings.DEFAULT_SERVANT);
    }
    
    private static boolean isEnabled(POAElement element, Properties policies, String tag) {
        POASettings settings;
        String _tag = element.getRootPOA().getORBTag();
        if (_tag != null)
            settings = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            settings = POASupport.getPOASettings();
        ListIterator policyList = settings.getPolicies().listIterator();
        while (policyList.hasNext()) {
            POAPolicyDescriptor policy = (POAPolicyDescriptor)policyList.next();
            String name = policy.getName();
            String valueName = policies.getProperty(name);
            POAPolicyValueDescriptor value;
            if (valueName != null)
                value = policy.getValueByName(valueName);
            else {
                if (policy.getValues().size() == 0)
                    continue;
                value = (POAPolicyValueDescriptor)policy.getValues().get(0);
            }
            if (value == null)
                continue;
            List disabledActions = value.getDisabledActions();
            if (disabledActions.contains(tag))
                return false;
        }
        return true;
    }
}

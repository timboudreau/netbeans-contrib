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

package org.netbeans.modules.vcscore;

import java.beans.*;

import org.openide.TopManager;
import org.openide.util.NbBundle;

/** BeanInfo for VcsVersioningSystem.
 * 
 * @author Martin Entlicher
 */

public class VcsVersioningSystemBeanInfo extends SimpleBeanInfo {

    private static BeanDescriptor beanDescriptor = null; //GEN-FIRST:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

    //GEN-LAST:BeanDescriptor

    private static PropertyDescriptor[] properties = null; //GEN-FIRST:Properties

    // Here you can add code for customizing the properties array.

    //GEN-LAST:Properties

    private static EventSetDescriptor[] eventSets = null; //GEN-FIRST:Events

    // Here you can add code for customizing the event sets array.

    //GEN-LAST:Events

    private static MethodDescriptor[] methods = null; //GEN-FIRST:Methods

    // Here you can add code for customizing the methods array.
    
    //GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
    private static String iconNameC16 = null; //GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null; //GEN-END:Icons

    private static int defaultPropertyIndex = -1; //GEN-BEGIN:Idx
    private static int defaultEventIndex = -1; //GEN-END:Idx

    private static void initPropertyDescriptors() {
        PropertyDescriptor showDeadFiles = null;
        PropertyDescriptor showMessages = null;
        PropertyDescriptor messageLength = null;
        PropertyDescriptor showUnimportantFiles = null;
        //PropertyDescriptor showLocalFiles = null;  -- makes problems, since every file is initially local
        PropertyDescriptor ignoredGarbageFiles = null;
        
        try {
            showDeadFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_DEAD_FILES, VcsVersioningSystem.class, "isShowDeadFiles", "setShowDeadFiles"); // NOI18N
            showDeadFiles.setDisplayName      (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showDeadFiles"));
            showDeadFiles.setShortDescription (NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showDeadFiles"));
            showMessages = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_MESSAGES, VcsVersioningSystem.class, "isShowMessages", "setShowMessages"); // NOI18N
            showMessages.setDisplayName      (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showMessages")); //NOI18N
            showMessages.setShortDescription (NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showMessages")); //NOI18N
            messageLength = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_MESSAGE_LENGTH, VcsVersioningSystem.class, "getMessageLength", "setMessageLength"); // NOI18N
            messageLength.setDisplayName      (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_messageLength")); //NOI18N
            messageLength.setShortDescription (NbBundle.getMessage(VcsVersioningSystem.class, "HINT_messageLength")); //NOI18N
            showUnimportantFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_UNIMPORTANT_FILES, VcsVersioningSystem.class, "isShowUnimportantFiles", "setShowUnimportantFiles"); // NOI18N
            showUnimportantFiles.setDisplayName(NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showUnimportantFiles"));
            showUnimportantFiles.setShortDescription(NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showUnimportantFiles"));
            showUnimportantFiles.setExpert(true);
            /*  makes problems, since every file is initially local
            showLocalFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_SHOW_LOCAL_FILES, VcsVersioningSystem.class, "isShowLocalFiles", "setShowLocalFiles"); // NOI18N
            showLocalFiles.setDisplayName     (NbBundle.getMessage(VcsVersioningSystem.class, "PROP_showLocalFiles"));
            showLocalFiles.setShortDescription(NbBundle.getMessage(VcsVersioningSystem.class, "HINT_showLocalFiles"));
            showLocalFiles.setExpert(true);
             */
            ignoredGarbageFiles = new PropertyDescriptor
                          (VcsVersioningSystem.PROP_IGNORED_GARBAGE_FILES, VcsVersioningSystem.class, "getIgnoredGarbageFiles", "setIgnoredGarbageFiles"); // NOI18N
            ignoredGarbageFiles.setDisplayName(NbBundle.getMessage(VcsVersioningSystem.class, "PROP_ignoredGarbageFiles"));
            ignoredGarbageFiles.setShortDescription(NbBundle.getMessage(VcsVersioningSystem.class, "HINT_ignoredGarbageFiles"));
            ignoredGarbageFiles.setExpert(true);
            
            properties = new PropertyDescriptor[] { showDeadFiles, showMessages, messageLength, showUnimportantFiles, ignoredGarbageFiles };
        } catch (IntrospectionException ex) {
            TopManager.getDefault().notifyException(ex);
        }
    }
    
    private static void initBeanDescriptor() {
        beanDescriptor = new BeanDescriptor(VcsVersioningSystem.class, null);
        beanDescriptor.setValue(VcsFileSystem.VCS_PROVIDER_ATTRIBUTE, new Boolean(true));
    }
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     * 
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        if (beanDescriptor == null) {
            initBeanDescriptor();
        }
	return beanDescriptor;
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * 
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        if (properties == null) {
            initPropertyDescriptors();
        }
        return properties;
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     * 
     * @return  An array of EventSetDescriptors describing the kinds of 
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return eventSets;
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     * 
     * @return  An array of MethodDescriptors describing the methods 
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return methods;
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are 
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean. 
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
        case ICON_COLOR_16x16:
            if ( iconNameC16 == null )
                return null;
            else {
                if( iconColor16 == null )
                    iconColor16 = loadImage( iconNameC16 );
                return iconColor16;
            }
        case ICON_COLOR_32x32:
            if ( iconNameC32 == null )
                return null;
            else {
                if( iconColor32 == null )
                    iconColor32 = loadImage( iconNameC32 );
                return iconColor32;
            }
        case ICON_MONO_16x16:
            if ( iconNameM16 == null )
                return null;
            else {
                if( iconMono16 == null )
                    iconMono16 = loadImage( iconNameM16 );
                return iconMono16;
            }
        case ICON_MONO_32x32:
            if ( iconNameM32 == null )
                return null;
            else {
                if( iconMono32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
	default: return null;
        }
    }

}


/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */

package org.netbeans.modules.remotefs.ftpfs;

import java.beans.*;

/** FTPFileSystemBeanInfo.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPFileSystemBeanInfo extends SimpleBeanInfo {
            
  // Property identifiers //GEN-FIRST:Properties
  private static final int PROPERTY_valid = 0;
  private static final int PROPERTY_connected = 1;
  private static final int PROPERTY_server = 2;
  private static final int PROPERTY_cache = 3;
  private static final int PROPERTY_startdir = 4;
  private static final int PROPERTY_password = 5;
  private static final int PROPERTY_hidden = 6;
  private static final int PROPERTY_port = 7;
  private static final int PROPERTY_username = 8;
  private static final int PROPERTY_readOnly = 9;

  // Property array 
  private static PropertyDescriptor[] properties = new PropertyDescriptor[10];

  static {
    try {
      properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", FTPFileSystem.class, "isValid", null );
      properties[PROPERTY_valid].setExpert ( true );
      properties[PROPERTY_connected] = new PropertyDescriptor ( "connected", FTPFileSystem.class, "isConnected", "setConnected" );
      properties[PROPERTY_connected].setExpert ( true );
      properties[PROPERTY_connected].setDisplayName ( "Connected" );
      properties[PROPERTY_connected].setShortDescription ( "Connected" );
      properties[PROPERTY_server] = new PropertyDescriptor ( "server", FTPFileSystem.class, "getServer", "setServer" );
      properties[PROPERTY_server].setPreferred ( true );
      properties[PROPERTY_server].setDisplayName ( "Server" );
      properties[PROPERTY_server].setShortDescription ( "FTP Server Name" );
      properties[PROPERTY_cache] = new PropertyDescriptor ( "cache", FTPFileSystem.class, "getCache", "setCache" );
      properties[PROPERTY_cache].setExpert ( true );
      properties[PROPERTY_cache].setDisplayName ( "Cache" );
      properties[PROPERTY_cache].setShortDescription ( "Cache to store work files" );
      properties[PROPERTY_startdir] = new PropertyDescriptor ( "startdir", FTPFileSystem.class, "getStartdir", "setStartdir" );
      properties[PROPERTY_startdir].setPreferred ( true );
      properties[PROPERTY_startdir].setDisplayName ( "Start directory" );
      properties[PROPERTY_startdir].setShortDescription ( "Start directory in FTP server" );
      properties[PROPERTY_password] = new PropertyDescriptor ( "password", FTPFileSystem.class, "getPassword", "setPassword" );
      properties[PROPERTY_password].setPreferred ( true );
      properties[PROPERTY_password].setDisplayName ( "Password" );
      properties[PROPERTY_password].setShortDescription ( "Password" );
      properties[PROPERTY_password].setPropertyEditorClass ( PasswordEditor.class );
      properties[PROPERTY_hidden] = new PropertyDescriptor ( "hidden", FTPFileSystem.class, "isHidden", "setHidden" );
      properties[PROPERTY_hidden].setExpert ( true );
      properties[PROPERTY_port] = new PropertyDescriptor ( "port", FTPFileSystem.class, "getPort", "setPort" );
      properties[PROPERTY_port].setDisplayName ( "Port" );
      properties[PROPERTY_port].setShortDescription ( "FTP Server Port" );
      properties[PROPERTY_username] = new PropertyDescriptor ( "username", FTPFileSystem.class, "getUsername", "setUsername" );
      properties[PROPERTY_username].setPreferred ( true );
      properties[PROPERTY_username].setDisplayName ( "User Name" );
      properties[PROPERTY_username].setShortDescription ( "User Name" );
      properties[PROPERTY_readOnly] = new PropertyDescriptor ( "readOnly", FTPFileSystem.class, "isReadOnly", "setReadOnly" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
  
  // Here you can add code for customizing the properties array.  

}//GEN-LAST:Properties

  // EventSet identifiers//GEN-FIRST:Events

  // EventSet array
  private static EventSetDescriptor[] eventSets = new EventSetDescriptor[0];
//GEN-HEADEREND:Events

  // Here you can add code for customizing the event sets array.  

  //GEN-LAST:Events

  private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
  private static java.awt.Image iconColor32 = null;
  private static java.awt.Image iconMono16 = null;
  private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
  private static String iconNameC16 = null;//GEN-BEGIN:Icons
  private static String iconNameC32 = null;
  private static String iconNameM16 = null;
  private static String iconNameM32 = null;//GEN-END:Icons
                                                 
  private static int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
  private static int defaultEventIndex = -1;//GEN-END:Idx


  /**
   * Gets the beans <code>PropertyDescriptor</code>s.
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
    return properties;
  }

  /**
   * Gets the beans <code>EventSetDescriptor</code>s.
   * 
   * @return  An array of EventSetDescriptors describing the kinds of 
   * events fired by this bean.  May return null if the information
   * should be obtained by automatic analysis.
   */
  public EventSetDescriptor[] getEventSetDescriptors() {
    return eventSets;
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
    return defaultPropertyIndex;
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
          if( iconNameM32 == null )
            iconMono32 = loadImage( iconNameM32 );
          return iconMono32;
          }
    }
    return null;
  }

}

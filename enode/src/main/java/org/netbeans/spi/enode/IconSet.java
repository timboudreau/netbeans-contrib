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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
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
 */

package org.netbeans.spi.enode;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.netbeans.modules.enode.TimedSoftReference;


/**
 * This class describes a set of icons that have been deployed by one
 * or a set of modules. The icons can be defined for different sizes.
 * Each icon is identified by a name. The following example shows an
 * <tt>IconSet</tt> definition in <tt>layer.xml</tt>:
 * <p><tt><pre>
 * <file name="BSC.instance">
 *      <attr name="instanceClass" stringvalue="org.netbeans.spi.enode.IconSet" />
 *      <attr name="instanceCreate" methodvalue="org.netbeans.spi.enode.IconSet.getInstance" />
 *      <attr name="basedir" stringvalue="com/nokia/oss/bsmoc/resources/"/>
 *      <attr name="bundle" stringvalue="bssmoc"/>
 *      <attr name="defaultSize" stringvalue="32"/>
 *      <attr name="description" stringvalue="BSC_OBJECT_CLASS" />
 *
 *      <attr name="*BSC#32" stringvalue="bmatbsc___bmx.gif"/>
 *      <attr name="DAXW#32" stringvalue="bmatdaxwllbmx.gif"/>
 *      <attr name="OLD_BSC_BM#32" stringvalue="guib32mx.gif"/>
 *      <attr name="BSC#32" stringvalue="bmatbsc___bmx.gif"/>
 *
 *      <attr name="SMALL_OLD_BSC_BM#20" stringvalue="guib20mx.gif"/>
 *      <attr name="*BSC#20" stringvalue="bmatbsc___smx.gif"/>
 *      <attr name="DAXW#20" stringvalue="bmatdaxwllsmx.gif"/>
 *      <attr name="BSC#20" stringvalue="bmatbsc___smx.gif"/>
 *
 * </file>
 * </pre></tt><p>
 * Th attributes <tt>instanceClass</tt> and <tt>instanceCreate</tt> define
 * a factory method of this class. The other attributes have the following
 * meaning:
 * <ul>
 * <li>
 * <tt>basedir</tt><br>
 * Defines the base folder containing the resources of the <tt>IconSet</tt>.
 * Resources includes the icons and optional a bundle file for mapping icon
 * names to display names. This attribute is optional. If it is ommited, all
 * other entries pointing to resources must contain absolute paths.
 * </li><p>
 * <li>
 * <tt>bundle</tt><br>
 * The name of the proprties file that contains the mapping from icon names to
 * display names. This attribute is optional. If it is ommitted, the icon names
 * will be used as display names. The value of the attribute should contains
 * the name of a properties file without file suffix, e.g. when the file is
 * called <tt>bundle.properties</tt> the configured value should be only
 * <tt>bundle</tt>.
 * </li><p>
 * <li>
 * <tt>defaultSize</tt><br>
 * Defines the default icon size. When an <tt>IconSet</tt> contains icons of
 * different sizes, this attribute allows to define the default size. The
 * attribute is optional. When omitted 16 will be the default size (as needed
 * for nodes).
 * </li><p>
 * <li>
 * <tt>description</tt><br>
 * A description of the <tt>IconSet</tt>. The attribute is optional. It makes
 * it more easy to read the <tt>layer.xml</tt> configuration.
 * </li><p>
 * <li>
 * All other entries will be treated as icon definitions. The attribute name
 * of an icon should follow the syntax <tt>*&lt;name&gt;#&lt;size&gt;</tt>.
 * The leading * marks the icon as default icon. For each icon size there
 * should be only one default icon( when several defaults are configured one
 * of them will be chosen). The &lt;name&gt; template defines the name of the
 * icon, while the &lt;size&gt template should contain the icon size in pixels.
 * In case no icon size is defined, the default size will be assumed.
 * </li><p>
 * </ul>
 * To define display names for icons a bundle file needs to be written that
 * contains the configured name of the icon as key and the display name as
 * value.
 * <p>
 * When an icon is requested from the <tt>IconSet</tt> that cannot be found
 * then all resulting exceptions will be logged and a default icon is returned.
 * This prevents to pop up many error dialogs in case icons cannot be loaded
 * for any kind of reason.
 *
 * @author John Stuwe
 */
public class IconSet {
    //=======================================================================
    // Private data members
    private static final Logger log = Logger.getLogger(IconSet.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);
    
    //
    // Special characters in icon names (marked as default
    // icon and size separator).
    //
    private static final char DEFAULT_MARK = '*';
    private static final char SIZE_MARK = '#';
    
    //
    // Supported attribute names
    //
    private static final String DESCRIPTION_ATTRIBUTE = "description";
    private static final String BASE_DIR_ATTRIBUTE = "basedir";
    private static final String DEFAULT_SIZE_ATTRIBUTE = "defaultSize";
    private static final String BUNDLE_ATTRIBUTE = "bundle";
    private static final String INSTANCE_CLASS = "instanceClass";
    private static final String INSTANCE_CREATE = "instanceCreate";
    
    //
    // Suffix of properties file
    //
    private static final String PROPERTIES = ".properties";
    
    //
    // IconSet cache
    //
    private static Map theIconSetCache;
    
    //
    // HashMap mapping icon names and paths,
    // delegate IconSet to forward icon requests,
    // default icon size,
    // bundle file containing localized display names of the icons,
    // and finally the description coded in the layer.xml entry
    //
    private Map myIcons;
    private IconSet myDelegate;
    private int myDefaultSize;
    private String myBundle;
    private String myDescription;
    
    //=======================================================================
    // Private constructors
    
    /**
     * Creates a new empty <tt>IconSet</tt>.
     */
    public IconSet(  ) {
        myIcons = new HashMap(  );
        myDefaultSize = NbIcon.SIZE_16x16;
    }
    
    
    /**
     * Creates a new <tt>IconSet</tt> that contains the icon configured
     * by the given file system entry.
     *
     * @param file The file system entry defining this <tt>IconSet</tt>.
     */
    IconSet( FileObject file ) {
        this(  );
        
        try {
            if( file == null ) {
                throw new IllegalArgumentException(  );
            }
            
            String baseDir = (String)file.getAttribute( BASE_DIR_ATTRIBUTE );
            if( baseDir != null ) {
                baseDir = parseBaseDirAttribute( baseDir );
            }
            
            Enumeration attributes = file.getAttributes(  );
            
            while( attributes.hasMoreElements(  ) ) {
                String attr = (String)attributes.nextElement(  );
                
                if( !attr.equals( INSTANCE_CLASS ) &&
                !attr.equals( INSTANCE_CREATE ) &&
                !attr.equals( BASE_DIR_ATTRIBUTE ) ) {
                    String value = (String)file.getAttribute( attr );
                    parseAttribute( attr, value, baseDir );
                }
            }
        }
        catch( Exception e ) {
            log.log(Level.FINE, "", e);
        }
    }
    
    //=======================================================================
    // Public methods
    
    /**
     * Factory method that creates a new <tt>IconSet</tt> from the given
     * file object.
     *
     * @param file The file defining the contents of the <tt>IconSet</tt>.
     */
    public static IconSet getInstance( FileObject file ) {
        if( theIconSetCache == null ) {
            theIconSetCache = new HashMap( );
        }

        Object key = file.getPath( );
        TimedSoftReference ref = null;
        synchronized (theIconSetCache) {
            ref = (TimedSoftReference)theIconSetCache.get(key);
        }
        IconSet instance = null;
        if (ref != null) {
            instance = (IconSet)ref.get();
        }
        if (instance == null) {
            instance = new IconSet( file );
            synchronized (theIconSetCache) {
                theIconSetCache.put(key, new TimedSoftReference(instance, theIconSetCache, key));
            }
        }
        
        return instance;
    }
    
    
    /**
     * Sets the delegate <tt>IconSet</tt>. Delegates allow to chain
     * icon sets. When the icon cannot be found in this set, it will
     * be taken from the delegate instead.
     *
     * @param set The icon set that shall be used as delegate.
     *
     * @throws IllegalStateException The delegate was defined already.
     */
    public void setDelegate( IconSet set ) {
        if( myDelegate != null ) {
            throw new IllegalStateException();
        }
        
        myDelegate = set;
    }
    
    /**
     * Returns the delegate <tt>IconSet</tt>. Delegates allow to chain
     * icon sets. When the icon cannot be found in this set, it will
     * be taken from the delegate instead.
     *
     * @return the delegate or <code>null</code> if the delegate has not
     *      been set
     */
    public IconSet getDelegate() {
        return myDelegate;
    }
    
    
    /**
     * Returns the default icon size of this <tt>IconSet</tt>.
     *
     * @return The default icon size.
     */
    public int getDefaultSize(  ) {
        return myDefaultSize;
    }
    
    
    /**
     * Returns the icon defined by the name and icon size.
     *
     * @param name The name of the icon.
     * @param size The size of the icon.
     *
     * @return The icon defined by the name or size or a default
     *          icon with the given size.
     */
    public ImageIcon getIcon(String name, int size) {
        ImageIcon icon = null;
        String file = null;
        
        //
        // Name not defined -> load default icon
        //
        if (name == null) {
            String key = "" + DEFAULT_MARK + size;
            name = (String) myIcons.get(key);
            
            if (name == null) {
                if (myDelegate != null) {
                    return myDelegate.getIcon(name,size) ;
                } else {
                    if (LOGGABLE) log.fine(
                        "Icon with the size " + 
                        size + " does not exist for name " + name + 
                        "Desc: " + myDescription
                    );
                    return NbIcon.unknownIcon(size);
                }
            }
            
            file = (String)myIcons.get(makeKey(name, size));
            
        } else {
            file = (String)myIcons.get(makeKey(name, size));
            
            //
            // Try default icon if needed -> max 1 recursion
            //
            if( file == null ) {
                return getIcon(null, size);
            }
        }
        
        if (file == null && myDelegate != null ) {
            icon = myDelegate.getIcon(name, size);
        } else {
            Logger logger = Logger.getLogger("org.netbeans.spi.enode");
            logger.finest( "Loading MO symbol from file " + file );
            
            if (file == null) {
                log.log(Level.WARNING, "File cannot be computed for name " + name,
                    new IllegalStateException());
            }
            
            icon = NbIcon.loadIcon( file, size, name );
        }
        
        return icon;
    }
    
    
    /**
     * Returns the default icon for the given size.
     *
     * @return The default icon for the given size. If no default icon
     *          is defined a default icon with the given size is returned.
     */
    public ImageIcon getDefaultIcon( int size ) {
        return getIcon( null, size );
    }
    
    
    /**
     * Returns the default icon with the default size.
     *
     * @return The default icon for the default size. If no default icon
     *          is defined a default icon with the default size is returned.
     *
     * qsee #getDefaultSize
     */
    public ImageIcon getDefaultIcon(  ) {
        return getIcon( null, getDefaultSize(  ) );
    }
    
    /**
     * Returns the description of this <tt>IconSet</tt>.
     *
     * @return The description of this <tt>IconSet</tt>.
     */
    public String getDescription( ) {
        return myDescription;
    }
    
    /**
     * provides the display name of the icon taken from the bundle file
     * configured for the <tt>IconSet</tt>. If no bundle file was defined
     * or if no entry was found the internal name is returned and an
     * exception is logged.
     *
     * @param name The internal name of the icon.
     *
     * @return The localized display name of the icon.
     */
    public String getIconDisplayName( String name ) {
        String display = null;
        
        try {
            display = NbBundle.getBundle( myBundle ).getString( name );
        }
        catch( MissingResourceException e ) {
            log.log(Level.FINE, "", e );
            
            display = name;
        }
        
        return display;
    }
    
    
    /**
     * Returns the names of all icons configured in this <tt>IconSet</tt>
     * that match the given size.
     *
     * @param size The icon size.
     *
     * @return The names of all icons with the given size.
     */
    public String[] getAllIconNames( int size ) {
        ArrayList list = new ArrayList(  );
        Iterator iterator = myIcons.keySet(  ).iterator(  );
        
        while( iterator.hasNext(  ) ) {
            String key = (String)iterator.next(  );
            
            if( !isDefaultKey( key ) ) {
                int iconSize = parseSize( key, getDefaultSize( ) );
                
                if( iconSize == size ) {
                    list.add( parseName( key ) );
                }
            }
        }
        
        String[] icons = new String[list.size(  )];
        
        return (String[])list.toArray( icons );
    }
    
    
    //=======================================================================
    // Private methods
    
    /**
     * Truncates the suffix <tt>.properties</tt> from the given attribute
     * value and prepends the base dir if it was defined.
     *
     * @param value The value of the <tt>bundle</tt> attribute.
     *
     * @return The bundle file name without <tt>.properties</tt> suffix.
     */
    private void parseBundleAttribute( String baseDir, String bundle ) {
        myBundle = bundle;
        
        if( myBundle != null  &&
        myBundle.toLowerCase(  ).endsWith( PROPERTIES ) ) {
            int index = myBundle.length(  ) - PROPERTIES.length(  );
            myBundle = myBundle.substring( 0, index );
        }
        
        if( baseDir != null ) {
            myBundle = baseDir + myBundle;
        }
        
    }
    
    
    /**
     * Parses the given attribute value into an integer.
     *
     * @param value The value of the <tt>defaultSize</tt> attribute.
     *
     * @return The value of the <tt>defaultSize</tt> attribute or
     *          <tt>SIZE_16x16</tt> if the value could not be parsed into
     *          an integer.
     */
    private void parseDefaultSizeAttribute( String value ) {
        myDefaultSize = NbIcon.SIZE_16x16;
        
        try {
            myDefaultSize = Integer.parseInt( value );
        }
        catch( NumberFormatException nfe ) {
            log.log(Level.FINE, "Cannot parse " + value, nfe);
        }
    }
    
    
    /**
     * Adds tailing / to the value if needed and removes leading /
     * from the path.
     *
     * @param value The value of the <tt>baseDir</tt> attribute.
     *
     * @return The base dir path without leading / and with tailing /.
     */
    private static String parseBaseDirAttribute( String value ) {
        String baseDir = value;
        
        if( baseDir.charAt( baseDir.length(  ) - 1 ) != '/' ) {
            baseDir = value + '/';
        }
        
        if( value.charAt( 0 ) == '/' ) {
            baseDir = baseDir.substring( 1 );
        }
        
        return baseDir;
    }
    
    
    /**
     * Parses the attribute with the given name and value. For any well
     * known attribute this means to parse the value and store it in a
     * data member. All other attributes are treated as icon definitions.
     *
     * @param attribute The name of the attribute.
     * @param value The value of the attribute.
     */
    private void parseAttribute( String attribute, String value, String baseDir ) {
        //
        // Get rid of leading or tailing spaces
        //
        value = value.trim(  );
        attribute = attribute.trim(  );
        
        if( attribute.equals( DESCRIPTION_ATTRIBUTE ) ) {
            myDescription = value;
        }
        else if( attribute.equals( BUNDLE_ATTRIBUTE ) ) {
            parseBundleAttribute( baseDir, value );
        }
        else if( attribute.equals( DEFAULT_SIZE_ATTRIBUTE ) ) {
            parseDefaultSizeAttribute( value );
        }
        else {
            parseIconAttribute( attribute, baseDir, value );
        }
    }
    
    /**
     * Adds an icon defintion to this <tt>IconSet</tt>.
     *
     * @param attribute The attribute name. Defines name and size
     *         of the icon (and might mark the icon as default icon).
     * @param baseDir The base dir of the icon set.
     * @param bitmap The file name of the icon.
     */
    private void parseIconAttribute( String attribute, String baseDir, String bitmap ) {
        boolean defaultIcon = false;
        
        //
        // Attribute name starts with * -> remove
        // the star and set the defaultIcon flag
        //
        if( attribute.charAt( 0 ) == DEFAULT_MARK ) {
            defaultIcon = true;
            attribute = attribute.substring( 1 );
        }
        
        if( baseDir != null ) {
            bitmap = baseDir + bitmap;
        }
        
        myIcons.put( attribute, bitmap );
        
        //
        // Keep indirect reference to default icon key
        //
        if( defaultIcon ) {
            int size = parseSize( attribute, getDefaultSize( ) );
            String key = "" + DEFAULT_MARK + size;
            attribute = parseName( attribute );
            myIcons.put( key, attribute );
        }
    }
    
    
    /**
     * Checks if the given string starts with the {@link
     * #DEFAULT_MARK} character indicating that it refers to a default
     * icon entry.
     *
     * @param key The key to be checked.
     *
     * @return <tt>true</tt> if the key starts with the {@link
     * #DEFAULT_MARK} character, <tt>false</tt> otherwise.
     */
    private static boolean isDefaultKey( String key ) {
        return key.charAt( 0 ) == DEFAULT_MARK;
    }
    
    
    /**
     * Parses the size out of the attribute name. The size is the
     * suffix of the attribute name after the {@link #SIZE_MARK}
     * character.
     *
     * @param attribute The attribute name.
     * @param defaultSize The default size.
     *
     * @return The size coded in the attribute name or the default
     *          size if the attribute name did not contain any proper
     *          size value.
     */
    private static int parseSize( String attribute, int defaultSize ) {
        int size = defaultSize;
        
        try {
            int start = attribute.lastIndexOf( SIZE_MARK );
            String suffix = attribute.substring( start + 1 );
            size = Integer.parseInt( suffix );
        }
        catch( Exception e ) {
            log.log(Level.FINE, "Cannot parse size " + attribute, e);
        }
        
        return size;
    }
    
    /**
     * Parses the name of an icon from an icon attribute name. The name is
     * the prefix of the attribute name before the {@link #SIZE_MARK}
     * character without leading {@link #DEFAULT_MARK} character.
     *
     * @param attribute The attribute name.
     *
     * @return The name of the icon without tailing size value or leading
     *          default marker.
     */
    private static String parseName( String attribute ) {
        String name = attribute;
        
        int index = attribute.lastIndexOf( SIZE_MARK );
        
        if( index > 0 ) {
            name = attribute.substring( 0, index );
        }
        
        return name;
    }
    
    /**
     * Creates a lookup key for this icon set. The key contains out of
     * the icon name and the icon size.
     *
     * @param name The name of the icon.
     * @param size The icon size.
     *
     * @return A string with pattern &lt;name&lt;#&lt;size&gt;
     */
    private static String makeKey( String name, int size ) {
        return name + SIZE_MARK + size;
    }
}


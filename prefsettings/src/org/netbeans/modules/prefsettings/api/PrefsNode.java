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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
package org.netbeans.modules.prefsettings.api;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Timer;
import org.netbeans.modules.prefsettings.PrefsDataObject;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Sheet;
import org.openide.util.Utilities;

/**
 * Parent class for nodes that represent .prefs files.  These can be defined 
 * one of two ways:
 * <p>
 * <b>Declaratively</b><br>
 * Define a .prefs file in your module layer (see the example module).  Any
 * attributes (other than <code>icon, bundle, nodeClass, helpCtx</code>) will
 * be considered properties with a default value of the string value for that
 * attribute.  I.e.
 * <pre>
 * &lt;file name="foo.prefs"/&gt;
 *    &lt;attr name="name" stringvalue="George"/&gt;
 * &lt;/file&gt;
 * </pre>
 * creates a string property called "Name" with a default value of "George".  If
 * a file attribute called "bundle" points to a resource bundle, then the
 * node name will be used as a key to get a localized name for the
 * node, and it will also be searched for property names.  The bundle may be
 * an attribute of the parent folder, rather than specifying it on each file.
 * <p>
 * It is possible to specify a property, declaratively, which will have a 
 * range of values (usually shown in a combo box).  Supply them as follows:
 * <pre>
 * &lt;file name="foo.prefs"/&gt;
 *    &lt;attr name="bundle" stringvalue="com/foo/mymodule/Bundle.properties"/&gt;
 *    &lt;attr name="name" stringvalue="George"/&gt;
 *    &lt;attr name="name.possibleValues" stringvalue="John,Paul,George,Ringo"/&gt;
 * &lt;/file&gt;
 * </pre>
 * <p>
 * &lt;file name="foo.prefs"/&gt;
 *    &lt;attr name="bundle" stringvalue="com/foo/mymodule/Bundle.properties"/&gt;
 *    &lt;attr name="age" stringvalue="13"/&gt;
 *    &lt;attr name="age.type" stringvalue="int"/&gt;
 *
 *    &lt;attr name="citizen" stringvalue="329"/&gt;
 *    &lt;attr name="citizen.type" stringvalue="boolean"/&gt;
 * &lt;/file&gt;
 * <p>
 * <b>Programmatically</b><br>
 * Define a similar .prefs file, and declare the node class that should be
 * instantiated for it (which is a PrefsNode subclass).
 * <pre>
 * &lt;file name="foo.prefs"/&gt;
 *    &lt;attr name="nodeClass" stringvalue="org.mymodule.MyPrefsNode"/&gt;
 *    &lt;attr name="bundle" stringvalue="com/foo/mymodule/Bundle.properties"/&gt;
 * &lt;/file&gt;
 * </pre>
 * 
 *
 * @author Timothy Boudreau
 */
public abstract class PrefsNode extends AbstractNode {
    private Map keysToDefaultValues = null;
    private Map keysToClasses = null;
    private Map keysToPossibleValues = null;
    private Map keysToDisplayNames = null;
    private PrefsDataObject dob;
    private List order = null;
    private boolean initialized = false;
    protected PrefsNode(DataObject dob) {
        super (Children.LEAF);
        assert dob instanceof PrefsDataObject : "Not a PrefsDataObject";
        this.dob = (PrefsDataObject) dob;
        setDisplayName(this.dob.displayName());
    }
    
    //XXX all the property fetching should be on the event thread;  sprinkle
    //in some assertions?
    
    private void init() {
        if (!initialized) {
            keysToDefaultValues = new HashMap(5);
            order = new ArrayList(5);
            keysToDisplayNames = new HashMap(5);
            initProperties();
            initialized = true;
        }
    }
    
    public Image getIcon(int type) {
        String imgKey = (String) dob.getPrimaryFile().getAttribute(PrefsDataObject.KEY_ICON);
        Image result = null;
        if (imgKey != null) {
            result = Utilities.loadImage (imgKey);
        }
        if (result == null) {
            return super.getIcon(type);
        } else {
            return result;
        }
    }
    
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
    
    /**
     * Initialize properties that the node will have here, by calling one
     * of the <code>addProperty</code> methods.
     */
    protected abstract void initProperties();
    
    protected final void addProperty (String preferencesKey, String displayName, String defaultValue, Class valueClass) {
        if (initialized) {
            throw new IllegalStateException ("Cannot add properties on the fly, do it in initProperties()");
        }
        preferencesKey = preferencesKey.intern();
        defaultValue = defaultValue.intern();
        if (keysToDefaultValues == null || initialized) {
            throw new IllegalStateException ("Only call addProperty from initProperties"); //NOI18N
        }
        if (keysToDefaultValues.containsKey(preferencesKey)) {
            throw new IllegalArgumentException ("Duplicate: " + preferencesKey); //NOI18N
        }
        keysToDefaultValues.put (preferencesKey, defaultValue) ;
        if (valueClass != null && valueClass != String.class) {
            if (keysToClasses == null) {
                keysToClasses = new HashMap(3);
            }
            keysToClasses.put (preferencesKey, valueClass);
        }
        order.add (preferencesKey);
        if (displayName != null) {
            if (keysToDisplayNames == null) {
                keysToDisplayNames = new HashMap();
            }
            keysToDisplayNames.put (preferencesKey, displayName);
        }
    }
    
    protected final void addProperty(String preferencesKey, String displayName, String defaultValue) {
        addProperty (preferencesKey, displayName, defaultValue, (Class) null);
    }
    
    protected final void addProperty(String preferencesKey, String displayName, String defaultValue, String[] legalValues) {
        addProperty (preferencesKey, displayName, defaultValue);
        if (keysToPossibleValues == null) {
            keysToPossibleValues = new HashMap (3);
        }
        keysToPossibleValues.put (preferencesKey, legalValues);
    }
    
    protected final void addProperty (String preferencesKey, String displayName, int defaultValue) {
        addProperty (preferencesKey, displayName, Integer.toString(defaultValue), Integer.TYPE);
    }
    
    protected final void addProperty (String preferencesKey, String displayName, boolean defaultValue) {
        addProperty (preferencesKey, displayName, Boolean.toString(defaultValue), Boolean.TYPE);
    }
    
    public final Object getProperty (String preferencesKey) {
        init();
        Preferences prefs = getPreferences();
        String res = prefs.get(preferencesKey, (String) keysToDefaultValues.get(preferencesKey));
        Class clazz = getPropertyType(preferencesKey);
        Object result = res;
        if (clazz != null) {
            if (Integer.TYPE.equals(clazz)) {
                result = new Integer (res);
            } else if (Boolean.TYPE.equals(clazz)) {
                result = Boolean.valueOf(res);
            } else if (!String.class.equals(clazz)) {
                assert false : "Unsupported type " + clazz;
            }
        }
        return result;
    }
    
    public final Object setProperty (String preferencesKey, Object value) {
        init();
        Object result = getProperty (preferencesKey);
        getPreferences().put(preferencesKey, value.toString());
        enqueued = true;
        Timer t = new Timer (3000, new Flusher());
        t.start();
        return result;
    }
    
    //Probably overkill, but gets rid of the wait cursor pause while prefs are
    //flushed.  Do prefs self flush in finalizers?
    private Runnable flusher = null;
    private boolean enqueued = false;
    private class Flusher implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            enqueued = false;
            ((Timer) ae.getSource()).removeActionListener(this);
            //XXX, do this off eq? java.util.Timer?
            //At any rate, for now get it out of the way of 
            try {
                getPreferences().flush();
            } catch (BackingStoreException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    }
    
    private Class getPropertyType (String preferencesKey) {
        init();
        Class result = (Class) keysToClasses.get(preferencesKey);
        if (result == null) {
            result = String.class;
        }
        return result;
    }
    
    private String[] getLocalizedValues (String propName, String[] values) {
        String[] result = new String[values.length];
        for (int i=0; i < result.length; i++) {
            result[i] = dob.getLocalizedString(propName + "." + values[i]);
            if (result[i] == null) {
                result[i] = values[i];
            }
            result[i].intern(); //XXX
        }
        return result;
    }

    private Preferences prefs = null;
    private Preferences getPreferences() {
        if (prefs == null) {
            //Caching needed?
            prefs = dob.getPreferences();
        }
        return prefs;
    }
    
    private String getDisplayName (String preferencesKey) {
        String result = keysToDisplayNames != null ? (String) keysToDisplayNames.get(preferencesKey) : null;
        if (result == null) {
            result = dob.getLocalizedString(preferencesKey);
        }
        if (result == null) {
            result = preferencesKey;
        }
        return result;
    }
    
    protected final org.openide.nodes.Sheet createSheet() {
        init();
        Sheet sheet = new Sheet();
        Sheet.Set set = sheet.createPropertiesSet();
        Property[] props = new Property[order.size()];
        int idx = 0;
        for (Iterator i=order.iterator(); i.hasNext();) {
            String prefsKey = (String) i.next();
            props[idx] = new Prop(prefsKey);
            idx++;
        }
        set.put(props);
        sheet.put(set);
        return sheet;
    }
    
    /**
     * Validate that a the passed value is legal for the setting represented
     * by this preferences key.  If the value is legal, return null, else
     * return a localized message that can be shown to the user describing
     * the problem.
     *
     * @param preferencesKey The preferences key for the value to set
     * @param value The new value
     * @return A localized string if the value is illegal, or null if it is
     *   legal
     */
    protected String validate(String preferencesKey, Object value) {
        return null;
    }
    
    private final class Prop extends Node.Property {
        private final String key;
        Prop (String key) {
            super (getPropertyType(key));
            this.key = key;
            if (key == null) throw new NullPointerException();
            setDisplayName(dn());
        }
        
        public boolean canRead() {
            return true;
        }

        public boolean canWrite() {
            return true;
        }
        
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return getProperty(key);
        }
        
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            String error = validate (key, val);
            if (error != null) {
                IllegalArgumentException iae = new IllegalArgumentException ();
                ErrorManager.getDefault().annotate(iae, ErrorManager.USER,  "", error, null, null);
                throw iae;
            }
            Object old = getValue();
            if (!old.equals(val)) {
                setProperty(key, val);
                firePropertyChange(key, old, val);
            }
        }

        public String getName() {
            return key;
        }

        public boolean supportsDefaultValue() {
            return true;
        }

        public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
            setValue (keysToDefaultValues.get(key));
        }

        public boolean isDefaultValue() {
            return getProperty(key).equals(keysToDefaultValues.get(key));
        }

        public Class getValueType() {
            Class clazz = (Class) keysToClasses.get(key);
            if (clazz == null) {
                clazz = String.class;
            }
            return clazz;
        }

        private String dn() {
            String result = (String) keysToDisplayNames.get(key);
            if (result == null) {
                result = dob.getLocalizedString(key);
            }
            if (result == null) {
                result = key;
            }
            return result;
        }

        private PropertyEditor ed = null;
        public PropertyEditor getPropertyEditor() {
                String[] values = (String[]) keysToPossibleValues.get(key);
                if (values != null) {
                    if (ed == null) {
                        ed = new MultiPropertyEditor (values, getLocalizedValues(key, values), (String) getProperty(key));
                    }
                    return ed;
                } else {
                    return super.getPropertyEditor();
                }
            } 
        }

    private static class MultiPropertyEditor implements PropertyEditor {
        private final String[] values;
        private final String[] locValues;
        private String value;
        private List pcls = null;
        
        MultiPropertyEditor (String[] values, String[] locValues, String value) {
            this.values = values;
            this.value = value;
            this.locValues = locValues;
        }
        
        public void setValue(Object value) {
            assert value instanceof String : "Not a string " + value;
            if (index((String)value) == -1) {
                //Property sheet is passing a localized value for the key - 
                //swap it
                int ix = locIndex((String)value);
                if (ix != -1) {
                    value = values[ix];
                } else {
                    throw new IllegalArgumentException ("Not in getTags() or non-localized version: " + value);
                }
            }
            if (!this.value.equals(value)) {
                Object old = this.value;
                this.value = (String) value;
                fire (old, value);
            }
        }
        
        public Object getValue() {
            return value;
        }
        
        public boolean isPaintable() {
            return false;
        }
        
        public void paintValue(Graphics gfx, Rectangle box) {
            throw new UnsupportedOperationException();
        }
        
        public String getJavaInitializationString() {
            return null;
        }
        
        private int index(String val) {
            int result = Arrays.asList(values).indexOf (val.intern());
//            if (result == -1) {
//                result = 0;
//            }
            return result;
        }
        
        private int locIndex(String val) {
            int result = Arrays.asList(locValues).indexOf(val.intern());
//            if (result == -1) {
//                result = 0;
//            }
            return result;
        }
        
        public String getAsText() {
            if (value != null) {
                return locValues[index(value)];
            } else {
                return values[0];
            }
        }
        
        public void setAsText(String text) throws IllegalArgumentException {
            setValue (values[locIndex(text)]);
        }
        
        public String[] getTags() {
            return locValues;
        }
        
        public Component getCustomEditor() {
            return null;
        }
        
        public boolean supportsCustomEditor() {
            return false;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            if (pcls == null) {
                pcls = new ArrayList();
            }
            pcls.add (listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcls.remove(listener);
            if (pcls.isEmpty()) {
                pcls = null;
            }
        }

        private void fire(Object old, Object nue) {
            if (pcls != null) {
                PropertyChangeEvent evt = new PropertyChangeEvent (this, null, old, nue);
                for (Iterator i=pcls.iterator(); i.hasNext();) {
                    PropertyChangeListener pcl = (PropertyChangeListener) i.next();
                    pcl.propertyChange(evt);
                }
            }
        }
    }
}
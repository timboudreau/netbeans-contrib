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
package org.netbeans.modules.jackpot.engine;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * Creates a UI form from the set of bean properties for a specified class.
 * <p>
 * Localization is supported in two ways:
 * <ul><li>A panel resource file is first checked; if present, all text is
 * read from this file.  Panel resource files follow the same localization
 * rules as other resources; with the foo.Bar transformer in a French locale, 
 * for example, foo/Bar_fr.panel is first tried, followed by foo/Bar.panel.</li>
 * <li>If a panel resource file doesn't exist, then a Bundle.properties is
 * used for label text.  The property key used is the simple name of the class, 
 * plus period, plus the bean property name.  Using the above example with
 * the "mumble" property, "Bar.mumble" is the resource key in the
 * foo/Bundle_fr.properties or foo/Bundle.properties files.
 */
public class PropertySheetInfo {
    
    private final Class base;
    private final ArrayList<Item> items = new ArrayList<Item>();
    private String title = null;
    private boolean hasTabs;
    private Preferences prefs;

    public static final String REMOVE_PANEL_ACTION = "remove PropertySheetInfo panel"; //NOI18N
    private static final ErrorManager logger = ErrorManager.getDefault();
    
    private static final int DEFAULT_PAD = 12;

    public static PropertySheetInfo find(Class c) {
        return new PropertySheetInfo(c);
    }
    public static JComponent findButtonedPanel(final Object obj, ActionListener apply, ActionListener cancel) {
	return find(obj.getClass()).buildButtonedPanel(obj, apply, cancel);
    }
    public static JComponent findPanel(final Object obj, Runnable incHandler) {
	return find(obj.getClass()).buildPanel(obj,incHandler);
    }

    /** Creates new PropertySheetInfo */
    private PropertySheetInfo(Class base) {
        this.base = base;
        String panelName = base.getName().replace('.','/');
	prefs = Preferences.userRoot().node(panelName);
        Method[] methods = base.getDeclaredMethods();
        Field[] fields = base.getDeclaredFields();
        for(int i = fields.length; --i>=0; ) {
            Field f = fields[i];
            if((f.getModifiers()&Modifier.PUBLIC)==0) continue;
            if((f.getModifiers()&Modifier.STATIC)!=0) continue;
            String nm = f.getName();
            Class t = f.getType();
            PropertyEditor bi = PropertyEditorManager.findEditor(t);
            if(bi==null && t!=PickOne.class) continue;
            items.add(new FieldItem(nm,f,t,bi));
        }
        for(int i = methods.length; --i>=0; ) {
            Method m = methods[i];
            String nm = m.getName();
            String pnm;
            boolean getter;
            Class rt = m.getReturnType();
            Class[] pt = m.getParameterTypes();
            Class propType;
            if(rt!=void.class) {
                if(pt.length!=0) continue;
                if((pnm=pattern("get",nm))==null                       //NOI18N
                    && (pnm=pattern("is",nm))==null)                   //NOI18N
                    continue;
                getter = true;
                propType = rt;
            }
            else { 
                if(pt.length!=1 || (pnm=pattern("set",nm)) == null)    //NOI18N
                    continue;
                getter = false;
                propType = pt[0];
            }
            PropertyEditor bi = PropertyEditorManager.findEditor(propType);
            if(bi==null && propType!=PickOne.class) continue;
            Item item0 = null;
            for(int j = items.size(); --j>=0; ) {
                Item item = items.get(j);
                if(item.name.equals(pnm)) {
                    item0 = item;
                    break;
                }
            }
            MethodItem item;
            if(item0==null) {
                item = new MethodItem(pnm,propType,bi);
                items.add(item);
            } else if(!(item0 instanceof MethodItem)) continue;
            else {
                item = (MethodItem) item0;
                if(propType != item.propType) {
                    logger.log(ErrorManager.USER, "set/get type mismatch "+item);
                    continue;
                }
            }
            if(getter) item.getter = m;
            else item.setter = m;
        }
        pname = null;
        if (base.getPackage() == null) { 
            // no panels for rule files
            validate();
            return;
        }
        String cname = base.getSimpleName();
        String rnm = cname+"-"+Locale.getDefault().getLanguage()+".panel";   //NOI18N
        InputStream cfg = null;
        if((cfg = base.getResourceAsStream(rnm))==null) {
            if((cfg = base.getResourceAsStream(rnm=cname+".panel"))==null) { //NOI18N
		validate();
                return;
	    }
        }
        Reader in = new InputStreamReader(new BufferedInputStream(cfg));
        char[] buf = new char[2];
        int slot = 0;
        try {
            int c;
            int pos = 0;
            String tag = null;
            while((c = in.read())>=0)
                if(c!='\n' && c!='\r')
                    if(c==':' && tag==null) {
                        tag = new String(buf,0,pos).trim();
                        pos=0;
                    } else {
                        if(pos>=buf.length) {
                            char[] nb = new char[pos*2];
                            System.arraycopy(buf,0,nb,0,pos);
                            buf = nb;
                        }
                        buf[pos++] = (char)c;
                    }
                else {
                    if(pos>0 && tag!=null && tag.length()>0) {
                        String body = new String(buf,0,pos).trim();
                        if(tag.equals("*title")) {
                            title = body;
                            tag = null;
                            pos = 0;
                            continue;
                        } else if(tag.equals("*tab")) {
                            items.add(slot++,new TabItem(body));
                            hasTabs = true;
                        } else if(!tag.startsWith("#")) {
                            for(int i = items.size(); --i>=slot; ) {
                                Item item = items.get(i);
                                if(item.name.equals(tag)) {
                                    item.setLabel(body);
                                    items.remove(item);
                                    items.add(slot++,item);
                                    body = null;
                                    break;
                                }
                            }
                            if(body != null)
                                logger.log(ErrorManager.USER, "Panel field "+tag+" missing from object");
                        }
                    }
                    pos = 0;
                    tag = null;
                }
            in.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
	validate();
    }
    private void validate() {
	for(int i = items.size(); --i>=0; ) {
	    Item item = items.get(i);
	    if(!item.valid()) items.remove(i);
	}
    }
    private String pattern(String pfx, String name) {
        if(!name.startsWith(pfx)) return null;
        int nml = name.length();
        int pfl = pfx.length();
        if(pfl==nml) return null;
        if(pname.length<nml) pname = new char[nml];
        name.getChars(0,nml,pname,0);
        if(Character.isUpperCase(pname[pfl]))
            pname[pfl] = Character.toLowerCase(pname[pfl]);
        return new String(pname,pfl,nml-pfl);
    }
    public String getTitle() {
        if(title==null) {
	    title = base.getName();
	    int dot = title.lastIndexOf('.');
	    if(dot>0) title = title.substring(dot+1);
	}
        return title;
    }
    public boolean nonEmpty() { return items.size()>0; }
    public void print(PrintStream out) {
        for(int i = 0; i<items.size(); i++) {
            Item item = items.get(i);
            out.print(item.toString());
            if(item.propertyEditor!=null)
            out.print(item.propertyEditor+"\n   isP="
                +item.propertyEditor.isPaintable()+" sce="
                +item.propertyEditor.supportsCustomEditor());
            out.println();
        }
    }
    // load prefs settings for a given object.
    public void loadValues(Object dst) {
        for (Item item : items)
	    item.set(dst, item.get(dst));
    }
    public void saveValues(Object src) throws InstantiationException, IllegalAccessException {
        Object orig = src.getClass().newInstance();
        for (Item item : items) {
            Object value = item.get(src);
	    item.set(orig, item.get(orig)); // reset oldValue to original for comparison
            item.set(src, value);
        }
    }
    public int showDialog(Object obj, ActionListener apply) {
        if(apply==null && obj instanceof ActionListener) apply = (ActionListener) obj;
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(buildButtonedPanel(obj, apply));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        return 0;
    }
    public JComponent buildButtonedPanel(Object obj, ActionListener apply) {
        return buildButtonedPanel(obj, apply, null);
    }
    public JComponent buildButtonedPanel(final Object obj, ActionListener apply, ActionListener cancel) {
	if(apply==null && obj instanceof ActionListener)
	    apply = (ActionListener) obj;
	final Box panel = Box.createVerticalBox();
        Box buttons = Box.createHorizontalBox();
	panel.add(buildPanel(obj));
	panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalStrut(DEFAULT_PAD));
	panel.add(buttons);
        buttons.add(Box.createHorizontalGlue());
	final RemovablePane rpane = new RemovablePane(panel);
        if(apply != null) {
	    JButton apBut = new JButton(NbBundle.getMessage(PropertySheetInfo.class, "LBL_Apply"));
	    apBut.addActionListener(apply);
            buttons.add(apBut);
            if (cancel != null)
                buttons.add(Box.createHorizontalStrut(DEFAULT_PAD / 2));
        }
	if(cancel != null) {
	    final JButton canBut = new JButton(NbBundle.getMessage(PropertySheetInfo.class, "LBL_Cancel"));
	    if (cancel != null) {
		rpane.addActionListener(cancel);
		canBut.addActionListener(rpane);
	    }
	    buttons.add(canBut);
	}
        return rpane;
    }
    /** Pane that fires a "remove me" action in response to an ActionEvent. */
    private static class RemovablePane extends JScrollPane 
      implements ActionListener {
	RemovablePane(Component view) {
	    super(view);
	}
	public void actionPerformed(ActionEvent e) {
	    if (listener != null)
		listener.actionPerformed(new ActionEvent(this, 0, 
							 REMOVE_PANEL_ACTION));
	}
	public void addActionListener(ActionListener l) {
	    listener = l;
	}
	private ActionListener listener = null;
    }
    private Component wrapLabel(String l) {
	int length = l.length();
	int split;
	if(length<50 || (split = l.indexOf(' ', l.length()>>1))<=0)
	    return new JLabel(l);
	Box ret = Box.createVerticalBox();
	ret.add(new JLabel(l.substring(0,split).trim()));
	ret.add(new JLabel(l.substring(split+1).trim()));
	return ret;
    }
    public JComponent buildPanel() {
        try {
            Object obj = base.newInstance();
            loadValues(obj);
            return buildPanel(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public JComponent buildPanel(Object obj) {
	return buildPanel(obj, null);
    }
    public JComponent buildPanel(final Object obj, Runnable incrementalHandler) {
        try {
            BeanDescriptor beanDesc = Introspector.getBeanInfo(base).getBeanDescriptor();
            Class customizerClass = beanDesc.getCustomizerClass();
            if (customizerClass != null) {
                final Customizer customizer = (Customizer)customizerClass.newInstance();
                customizer.setObject(this);
                if (customizer instanceof JComponent)
                    return (JComponent)customizer;
                else {
                    JPanel wrapper = new JPanel();
                    wrapper.add((Component)customizer);
                    return wrapper;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // fall-through on any error
        }
        JPanel panel = null;
        JTabbedPane tabs;
        if(hasTabs)
            tabs = new JTabbedPane();
        else 
            tabs=null;
        String tabTitle = NbBundle.getMessage(PropertySheetInfo.class, "LBL_Properties_Tab");
        GridBagLayout gbl = new GridBagLayout();
        if(!base.isInstance(obj))
            throw new IllegalArgumentException("Type mismatch");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = gbc.HORIZONTAL;
        gbc.anchor = gbc.NORTHWEST;
	int last = items.size()-1;
        for(int i = 0; i<=last; i++) {
            final Item item = items.get(i);
	    if(!item.valid()) continue;
	    gbc.weighty = i==last || items.get(i+1) instanceof TabItem ? 1000 : 1;
            if(item instanceof TabItem) {
                tabTitle = item.getLabel();
                panel = null;
                continue;
            }
            if(panel==null) {
                panel = new JPanel();
                panel.setLayout(gbl);
                gbc.gridy = 0;
                if(tabs!=null) {
                    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                    tabs.addTab(tabTitle, panel);
                }
            }
            String label = item.getLabel();
            if(label!=null && label.length()>0) {
                gbc.gridx = 0;
                gbc.weightx = 0;
                gbc.insets.right = 12;
                panel.add(wrapLabel(label), gbc);
            }
            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.insets.right = 0;
	    Component comp = item.getInteractor(obj,incrementalHandler);
	    if(comp instanceof JScrollPane)
		gbc.fill = gbc.BOTH;
            panel.add(comp, gbc);
            gbc.gridy++;
        }
        return tabs!=null ? (JComponent) tabs : panel!=null ? (JComponent) panel : new JLabel(NbBundle.getMessage(PropertySheetInfo.class, "MSG_No_items_to_edit"));
    }

    private static class PropertySheet {
	private Component sheet;
	private Box buttons;
	private void addButton(JComponent b) {
	    if(buttons==null) {
		buttons = Box.createHorizontalBox();
		Box nSheet = Box.createVerticalBox();
		nSheet.add(sheet);
		nSheet.add(Box.createVerticalGlue());
		nSheet.add(buttons);
		sheet = nSheet;
		buttons.add(Box.createHorizontalGlue());
	    } else buttons.add(Box.createHorizontalStrut(10));
	    buttons.add(b);
	}
	public PropertySheet addButton(String label, final ActionListener al) {
	    JButton b = new JButton(label);
	    b.addActionListener(al);
	    addButton(b);
	    return this;
	}
	public Component getComponent() { return sheet; }
    }

    char[] pname = new char[2];
    private static String ns(Object s) { return s==null ? "" : s.toString(); }
    public abstract class Item {
        protected abstract Object get0(Object src);
        protected abstract void set0(Object dst, Object value);
        public String name;
	boolean textarea = false;
        private String label;
	private Object oldValue;
	protected boolean trnsient = false;
	public final Object get(Object src) {
	    Object fromobj = get0(src);
	    if(oldValue==null && name!=null && fromobj!=null) {
		if(fromobj instanceof Boolean)
		    fromobj = Boolean.valueOf(prefs.getBoolean(name,((Boolean)fromobj).booleanValue()));
		else if(fromobj instanceof String)
		    fromobj = prefs.get(name,((String)fromobj));
		else if(fromobj instanceof Integer)
		    fromobj = new Integer(prefs.getInt(name,((Integer)fromobj).intValue()));
		else if(fromobj instanceof PickOne)
		    ((PickOne) fromobj).value = prefs.getInt(name,((PickOne)fromobj).value);
		else logger.log(ErrorManager.USER, "Missing type "+fromobj.getClass().getName()+" for "+name);
		set0(src,fromobj);
	    }
	    oldValue = fromobj;
	    return fromobj;
	}
	public final void set(Object dst, Object value) {
	    if(!trnsient && value!=null && !value.equals(oldValue)) {
		if(value instanceof Boolean)
		    prefs.putBoolean(name,((Boolean)value).booleanValue());
		else if(value instanceof Integer)
		    prefs.putInt(name,((Integer)value).intValue());
		else if(value instanceof String)
		    prefs.put(name,(String)value);
                flush();
            }
	    set0(dst,value);
	}
        void flush() {
            try { 
                prefs.flush(); 
            } catch (java.util.prefs.BackingStoreException e) {
                e.printStackTrace();
            }
        }
        public void setLabel(String l) {
	    if(l.startsWith("textarea")) {
		l = l.substring(8).trim();
		textarea = true;
	    }
	    label = l;
	}
        public String getLabel() {
	    if(label==null) {
                // First check if resource exists.  When 1.6 is the minimum JDK,
                // switch to ResourceBundle.containsKey().
                String key = base.getSimpleName() + '.' + name;
                try {
                    label = NbBundle.getBundle(base).getString(key);
                } catch (MissingResourceException e) {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, key + " property not localized");
                    // fall-through
                }
            }
	    if(label==null) {
                // No resource, so create an unlocalized label from the name.
		StringBuffer sb = new StringBuffer();
		char lastc = 0;
		int limit = name.length();
		for(int i = 0; i<limit; i++) {
		    char c = name.charAt(i);
		    char nc;
		    if(c=='_') nc = ' ';
		    else if(Character.isUpperCase(c)) {
			nc = Character.toLowerCase(c);
			sb.append(' ');
		    } else nc = c;
		    if(lastc==0 && Character.isLowerCase(nc)) nc = Character.toUpperCase(nc);
		    sb.append(nc);
		    lastc = c;
		}
		label = sb.toString();
	    }
	    return label;
	}
        public Class propType;
        public PropertyEditor propertyEditor;
        public Component getInteractor(final Object obj, final Runnable incrementalHandler) {
            try {
                final PropertyEditor pe = propertyEditor;
                // Check for String class first since NetBeans String custom editor is broken...
                if(propType == String.class) {
                    final javax.swing.text.JTextComponent string;
		    if(textarea) {
			JTextArea ta = new JTextArea();
			string = ta;
		    } else string = new JTextField();
                    string.setText(ns(get(obj)));
		    string.addFocusListener(new java.awt.event.FocusAdapter() {
			    public void focusLost(FocusEvent e) {
                            Object newValue = string.getText();
                            if(newValue.equals(get(obj))) return;
			    if(incrementalHandler!=null) EventQueue.invokeLater(incrementalHandler);
                            set(obj,newValue);
                        }
                    });
                    if (textarea) {
                        Component c = new JScrollPane(string);
                        c.setPreferredSize(new Dimension(320,200));
                        return c;
                    }
                    else
                        return string;
                } else if(pe!=null && pe.supportsCustomEditor()) {
		    pe.setValue(get(obj));
                    pe.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
			    if(incrementalHandler!=null) EventQueue.invokeLater(incrementalHandler);
                        }
                    });
                    return pe.getCustomEditor();
                } else if(propType == boolean.class) {
                    final JCheckBox cb = new JCheckBox(null,null,((Boolean)get(obj)).booleanValue());
                    cb.addChangeListener(new javax.swing.event.ChangeListener() {
                        public void stateChanged(javax.swing.event.ChangeEvent ce) {
                            Object newValue = cb.isSelected() ? Boolean.TRUE : Boolean.FALSE;
                            if(newValue.equals(get(obj))) return;
			    if(incrementalHandler!=null) EventQueue.invokeLater(incrementalHandler);
                            set(obj,newValue);
                        }
                    });
                    return cb;
                } else if(propType == int.class) {
                    final JSpinner spinner = new JSpinner();
                    spinner.setValue(get(obj));
                    spinner.addChangeListener(new javax.swing.event.ChangeListener() {
                        public void stateChanged(javax.swing.event.ChangeEvent ce) {
                            Object newValue = spinner.getValue();
                            if(newValue.equals(get(obj))) return;
			    if(incrementalHandler!=null) EventQueue.invokeLater(incrementalHandler);
                            set(obj,newValue);
                        }
                    });
                    return spinner;
		} else if(propType == PickOne.class) {
		    final PickOne pickone = (PickOne) get(obj);
		    if(pickone==null) return new JLabel(NbBundle.getMessage(PropertySheetInfo.class, "MSG_Null_PickOne"));
		    final JComboBox combo = new JComboBox(pickone.keys);
		    combo.setSelectedIndex(pickone.value);
		    combo.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==e.SELECTED) {
				    int nValue = combo.getSelectedIndex();
				    if(nValue == pickone.value) return;
				    pickone.value = nValue;
                                    prefs.putInt(name, nValue);
                                    flush();
				    if(incrementalHandler!=null)
					EventQueue.invokeLater(incrementalHandler);
				}
			    }
			});
		    return combo;
                } else {
                    Object [] tags = pe.getTags();
                    if(tags!=null && tags.length>0) {
                        JComboBox jcb = new JComboBox(tags);
                        jcb.setEditable(false);
                        jcb.setSelectedItem(pe.getAsText());
                        return jcb;
                    } else {
                        return new JTextField(pe.getAsText());
                    }
                }
            } catch(Exception e) {
		e.printStackTrace();
		return new JLabel(e.toString());
	    }
        }
	public boolean valid() { return true; }

        protected final Object defaultItem(Class type) {
            if (type == String.class)
                return "";
            if (type == Boolean.class)
                return false;
            if (type == Integer.class)
                return 0;
            return null;
        }
    }
    private class TabItem extends Item {
        TabItem(String label) {
            setLabel(label);
            propType = String.class;
        }
        public Object get0(Object src) { return NbBundle.getMessage(PropertySheetInfo.class, "MSG_Invalid_TabItem"); }        
        public void set0(Object dst, Object value) { }
    }
    private class MethodItem extends Item {
        Method setter, getter;
        MethodItem(String nm, Class pt, PropertyEditor bi) {
            propertyEditor = bi;
            propType = pt;
            name = nm;
        }
	public boolean valid() { return getter!=null && setter!=null; }
        public Object get0(Object src) {
            try {
                Object ret = getter.invoke(src);
                if (ret == null)
                    ret = defaultItem(getter.getReturnType());
                return ret;
            }
            catch(IllegalAccessException ice) { return null; }
            catch(InvocationTargetException ice) { return null; }
        }
        public void set0(Object dst, Object value) {
            try {
                setter.invoke(dst,new Object[]{value});
            }
            catch(IllegalAccessException ice) { }
            catch(InvocationTargetException ice) { }
        }
        public String toString() { return name+"/"+getter+"/"+setter; }
    }
    private class FieldItem extends Item {
        Field field;
        FieldItem(String nm, Field f, Class t, PropertyEditor bi) {
            field = f;
            name = nm;
            propType = t;
            propertyEditor = bi;
	    trnsient = Modifier.isTransient(f.getModifiers());
        }
        public Object get0(Object src) {
            try {
                Object ret = field.get(src);
                if (ret == null)
                    ret = defaultItem(field.getType());
                return ret;
            } catch(IllegalAccessException ice) { 
                return null; 
            }
        }
        public void set0(Object dst, Object value) {
            try {
                field.set(dst,value);
            } catch(IllegalAccessException ice) { 
                logger.log(ErrorManager.USER, "bad set, value=" + value);
            }
        }
        public String toString() { return name+" "+propType; }
    }
}

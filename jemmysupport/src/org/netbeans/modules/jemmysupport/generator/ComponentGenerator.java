/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.*;
import org.netbeans.modules.jemmysupport.I18NSupport;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;


/** Jemmy Tools Component Generator class generates source code from given Container (Frame, Dialog ...) according to its visible structure.
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.2
 */
public class ComponentGenerator {

    String[] defaultComponentCode;
    String[] defaultTopCode;
    
    Hashtable operators = new Hashtable();
    HashSet componentNames;
    protected ArrayList components;
    int maxComponentCodeLength = 0;
    ComponentRecord _container;
    String _package;
    boolean _grabIcons = false;
    Robot robot = null;
    I18NSupport i18n;
        
    /** Utility field holding list of ChangeListeners. */
    private transient ChangeListener changeListener;
        
    /** Registers PropertyChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addChangeListener(ChangeListener listener) {
        changeListener = listener;
    }

    /** Removes ChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeChangeListener(ChangeListener listener) {
        changeListener = null;
    }

    /** Notifies all registered listeners about the event.
     */
    void fireStateChanged(Object source) {
        ChangeEvent e = new ChangeEvent(source);
        try {
            if (changeListener != null) 
                changeListener.stateChanged(e);
        } catch (Exception ex) {}
    }
    
    /** class holding all informations about knonw operator
     */    
    private class OperatorRecord extends Object {
        String _operatorClass;
        String _instancePrefix;
        String _instanceSuffix;
        String[] _componentCode;
        String[] _internalLogicCode;
        String _idMethod;
        boolean _recursion;
        
        /** creates new record of component operator
         * @param recursion boolean true when recursion enabled
         * @param internalLogicCode set of component codes used for source generation
         * @param operatorClass String class name
         * @param instancePrefix String prefix
         * @param instanceSuffix String suffix
         * @param idMethod string identification method
         * @param componentCode String[] set of component generation codes */        
        public OperatorRecord( String operatorClass, String instancePrefix, String instanceSuffix, String idMethod, String componentCode[], String internalLogicCode[], boolean recursion ) {
            _operatorClass=operatorClass;
            _instancePrefix=instancePrefix;
            _instanceSuffix=instanceSuffix;
            _componentCode=componentCode;
            _internalLogicCode=internalLogicCode;
            _idMethod=idMethod;
            _recursion=recursion;
        }
        
        /** returns String prefix
         * @return String prefix
         */        
        public String getInstancePrefix() {
            return _instancePrefix;
        }
        
        /** returns String suffix
         * @return String suffix
         */        
        public String getInstanceSuffix() {
            return _instanceSuffix;
        }
        
        /** returns String piece of component code
         * @param i index into components code set
         * @return String piece of component code
         */        
        public String getComponentCode(int i) {
            if ((null!=_componentCode) && (i>=0) && (i<_componentCode.length) && (null!=_componentCode[i])) {
                return _componentCode[i];
            } else {
                return ""; // NOI18N
            }
        }
        
        /** returns String piece of internal logic component code
         * @param i index into internal logic components code set
         * @return String piece internal logic of component code
         */        
        public String getInternalLogicCode(int i) {
            if ((null!=_internalLogicCode) && (i>=0) && (i<_internalLogicCode.length) && (null!=_internalLogicCode[i])) {
                return _internalLogicCode[i];
            } else {
                return ""; // NOI18N
            }
        }
        
        /** returns String identification method
         * @return String identification method
         */        
        public String getIdMethod() {
            return _idMethod;
        }
        
        /** returns boolean recursion flag
         * @return boolean recursion flag
         */        
        public boolean getRecursion() {
            return _recursion;
        }
        
        /** returns String class name of operator
         * @return String class name of operator
         */        
        public String getOperatorClass() {
            return _operatorClass;
        }
        
        /** getter for internal recursion property
         * @return boolean true if scanning for internal labels is enabled */        
        public boolean getInternalRecursion() {
            if (_internalLogicCode==null) return false;
            for (int i=0; i<_internalLogicCode.length; i++)
                if (_internalLogicCode[i]!=null && _internalLogicCode[i].length()>0)
                    return true;
            return false;
        }
    }
    /** class holding all informations about single component
     */    
    public final class ComponentRecord extends Object {
       
        OperatorRecord _operator;
        String _identification;
        String _uniqueName;
        int _index;
        String _componentClass;
        String[] _internalLabels;
        String _shortName;
        String _smallName;
        Icon _icon;
        ComponentRecord _parent;
        ComponentOperator _componentOperator;
        DefaultMutableTreeNode _node = null;
        
        /** creates new record of component
         * @param icon Icon of component
         * @param componentOperator ComponentOperator of component
         * @param parent OperatorRecord of parent container
         * @param internalLabels String[] set of components internal labels used for internal logic generation
         * @param operator OperatorRecord component's oerator
         * @param identification identification string
         * @param uniqueName generated unique name
         * @param index index used for component search inside container
         * @param componentClass compoennt's real class name */        
        public ComponentRecord( OperatorRecord operator, String identification, String uniqueName, int index, String componentClass, String[] internalLabels, Icon icon, ComponentOperator componentOperator, ComponentRecord parent ) {
            _icon = icon;
            _operator = operator;
            setIdentification(identification);
             _uniqueName = uniqueName;
            _index = index;
            _componentClass = componentClass;
            _internalLabels = internalLabels;
            if ((this==_container)&&(_index>0)) {
                _uniqueName+=String.valueOf(_index);
            }
            int i = _uniqueName.lastIndexOf(_operator.getInstanceSuffix());
            _shortName = _uniqueName.substring(_operator.getInstancePrefix().length(), i) +
                         _uniqueName.substring(i+_operator.getInstanceSuffix().length());
            _smallName = Character.toLowerCase(_shortName.charAt(0))+_shortName.substring(1);
            if (_smallName.equalsIgnoreCase("ok")) _smallName="ok";
            _parent = parent;
            _componentOperator = componentOperator;
        }
        
        /** return component's operator class name
         * @return component's operator class name
         */        
        public String getOperatorClass() {
            return _operator.getOperatorClass();
        }
        
        /** getter for recursion property
         * @return boolean true when scanning for sub-components is enabled */        
        public boolean getRecursion() {
            return _operator.getRecursion();
        }
        
        /** getter for component operator
         * @return ComponentOperator of component */        
        public ComponentOperator getComponentOperator() {
            return _componentOperator;
        }
        
        /** returns identification string
         * @return identification string
         */        
        public String getIdentification() {
            return i18n.filterI18N(_identification);
        }
        
        /** returns generated unique name
         * @return generated unique name
         */        
        public String getUniqueName() {
            return _uniqueName;
        }
        
        /** returns short name with first lower-case character
         * @return short name with first lower-case character
         */        
        public String getSmallName() {
            return _smallName;
        }
        
        /** returns short version of unique name, without prefix and suffix
         * @return short version of unique name
         */        
        public String getShortName() {
            return _shortName;
        }
        
        /** returns components class name
         * @return components class name
         */        
        public String getComponentClass() {
            return _componentClass;
        }

        /** getter for source code of getter of parent conatiner
         * @return String source code of parent getter */        
        public String getParentGetter() {
            return _parent==null? "this" : _parent.getUniqueName()+"()"; // NOI18N
        }
        
        /** getter for source code of construcotr arguments
         * @return String source code of constructor arguments */        
        public String getConstructorArgs() {
            String s=getParentGetter()+", "; // NOI18N
            if (_identification!=null && _identification.length()>0) s+=getI18NIdentification()+", "; // NOI18N
            if (_index>0) s+=String.valueOf(_index);
            if (s.endsWith(", ")) return s.substring(0, s.length()-2); // NOI18N
            return s;
        }

        public String getI18NIdentification() {
           if (_identification==null) {
                return "null"; // NOI18N
            } else {
                return i18n.translate(_identification);
            }
        }

        private String getIdent() {
           if (_identification==null) {
                return "null"; // NOI18N
            } else {
                return i18n.escape(getIdentification());
            }
        }
        
        /** getter for "visualizer" code
         * @return String source code that brings component visible */        
        public String getVisualizer() {
            ComponentRecord cr=_parent;
            while (cr!=null) {
                if (cr._componentOperator instanceof TabOperator)
                    return "        "+cr.getUniqueName()+"();\n"; // NOI18N
                cr=cr._parent;
            }
            return ""; // NOI18N
        }
        
        /** returns formated component code with given index, formating means replacing keywords with real values
         * @param i index into component's code set
         * @return formated component code
         */        
        public String getComponentCode(int i) {
            return formate(_operator.getComponentCode(i),""); // NOI18N
        }

        /** returns components internal label with given index
         * @param i index of internal label
         * @return String internal label
         */        
        public String getInternalLabels(int i) {
            if ((null!=_internalLabels) && (i>=0) && (i<_internalLabels.length) && (null!=_internalLabels[i])) {
                return _internalLabels[i];
            } else {
                return ""; // NOI18N
            }
        }
        
        /** setter for Internal Labels propert
         * @param i index of internal label
         * @param label String internal label text
         */
        public void setInternalLabels(int i, String label) {
            if (i>=_internalLabels.length) {
                String labs[] = new String [i];
                for(int j=0;j<_internalLabels.length;j++) {
                    labs[j]=_internalLabels[j];
                }
                _internalLabels=labs;
            }
            _internalLabels[i] = label;
        }
        
        /** getter for component internal labels
         * @return String[] texts of component internal labels */        
        public String[] getInternalLabels() {
            return _internalLabels;
        }
        
        /** setter for component internal labels
         * @param labels String[] texts of component internal labels */        
        public void setInternalLabels(String[] labels) {
            _internalLabels = labels;
        }
        
        /** returns formated internal component logic with given index, formating means replacing keywords with real values
         * @param i index into component's code set
         * @return formated component code
         */        
        public String getInternalLogicCode(int i) {
            if (_internalLabels==null) return ""; // NOI18N
            StringBuffer sb=new StringBuffer();
            for (int j=0;j<_internalLabels.length;j++) {
                sb.append(formate(_operator.getInternalLogicCode(i),getInternalLabels(j)));
            }
            return sb.toString();
        }
        
        String toJavaID(String s) {
            StringBuffer sb=new StringBuffer();
            char c;
            for(int i=0;i<s.length();i++) {
                c=s.charAt(i);
                if (Character.isJavaIdentifierPart(c)) {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        
        String toBigJavaID(String s) {
            return toJavaID(s).toUpperCase();
        }
        
        /** performs replacing of keywords with real values, keywords are:<pre>
         * __DATE__          - current date
         * __USER__          - current user
         * __PACKAGE__       - destination package
         * __SMALLNAME__     - short name with first character in lower case
         * __SHORTNAME__     - short version of unique name without prefix and suffix
         * __NAME__          - unique Java identification name
         * __CLASS__         - operator class name
         * __ID__            - indentification text 
         * __INDEX__         - indentification index
         * __COMPONENT__     - real class name
         * __INTERNALLABEL__ - internal label real text
         * __SHORTLABEL__    - internal label text converted to Java identifier
         * __PARENTGETTER__  - code returning parent container operator
         * __BIGLABEL__      - upper case version of short label
         * __CONSTRUCTORARGS__ - component constructor arguments code</pre>
         * @return formated string
         * @param internalLabel real internal label text
         * @param s string to be formated
         */        
        public String formate(String s, String internalLabel) {
            StringBuffer sb=new StringBuffer(s);
            replace(sb, "__DATE__", new SimpleDateFormat().format(new Date())); // NOI18N
            replace(sb, "__USER__", System.getProperty("user.name")); // NOI18N
            replace(sb, "__PACKAGE__", getPackage()); // NOI18N
            replace(sb, "__SMALLNAME__", getSmallName()); // NOI18N
            replace(sb, "__SHORTNAME__", getShortName()); // NOI18N
            replace(sb, "__NAME__", getUniqueName()); // NOI18N
            replace(sb, "__CLASS__", getOperatorClass()); // NOI18N
            replace(sb, "__ID__", getIdent()); // NOI18N
            replace(sb, "__I18NID__", getI18NIdentification()); // NOI18N
            replace(sb, "__INDEX__", String.valueOf(getIndex())); // NOI18N
            replace(sb, "__COMPONENT__", getComponentClass()); // NOI18N
            replace(sb, "__INTERNALLABEL__", i18n.translate(internalLabel)); // NOI18N
            replace(sb, "__SHORTLABEL__", toJavaID(i18n.filterI18N(internalLabel))); // NOI18N
            replace(sb, "__BIGLABEL__", toBigJavaID(i18n.filterI18N(internalLabel))); // NOI18N
            replace(sb, "__PARENTGETTER__", getParentGetter()); // NOI18N
            replace(sb, "__CONSTRUCTORARGS__", getConstructorArgs()); // NOI18N
            replace(sb, "__VISUALIZER__", getVisualizer()); // NOI18N
            return sb.toString();
        }
        
        /** returns string representation of this class
         * @return string representation of this class
         */        
        public String toString() {
            return (isDefaultName()?"<html><b>":"")+getUniqueName()+" ("+getOperatorClass()+")"; // NOI18N
        }
        
        /** Setter for property shortName.
         * @param shortName New value of property shortName.
         */
        public void setShortName(String shortName) {
            if (shortName!=null && shortName.length()>0) {
                _shortName = Character.toUpperCase(shortName.charAt(0))+shortName.substring(1);
                _smallName = Character.toLowerCase(shortName.charAt(0))+shortName.substring(1);
                setUniqueName(_operator.getInstancePrefix() + _shortName + _operator.getInstanceSuffix());
            }
        }
        
        /** setter for component unique name
         * @param name String unique name */        
        public void setUniqueName(String name) {
            if (name!=null) {
                String old = _uniqueName;
                _uniqueName = name;
                fireStateChanged(this);
            }
        }
        
        /** getter for component icon
         * @return Icon of component */        
        public Icon getIcon() {
            return _icon;
        }
        
        /** getter for components tree node
         * @return DefaultMutableTreeNode of component */        
        public DefaultMutableTreeNode getNode() {
            if (_node==null) {
                _node=new DefaultMutableTreeNode(this);
                if (_parent!=null) {
                    _parent.getNode().add(_node);
                } else if (this!=_container) {
                    _container.getNode().add(_node);
                }
            }
            return _node;
        }
        
        /** Setter for property identification.
         * @param identification New value of property identification.
         *
         */
        public void setIdentification(String identification) {
            if (identification==null || identification.length()==0) _identification=null;
            else _identification=identification;
        }
       
        /** Getter for property index.
         * @return Value of property index.
         *
         */
        public int getIndex() {
            return _index;
        }
        
        /** Setter for property index.
         * @param index New value of property index.
         *
         */
        public void setIndex(int index) {
            _index = index;
        }
        
        public boolean isDefaultName() {
            return _uniqueName.startsWith(_operator._instancePrefix+_componentClass+_operator._instanceSuffix);
        }
    }        
    
    /** creates new ComponentGenerator with configuration from given properties
     * @param props configuration properties
     */   
    public ComponentGenerator(Properties props) {
        // install JemmyQueue because later installation may cause problems
        // (it has to be installed before any modal dialog is open)
        QueueTool.installQueue();
        maxComponentCodeLength = Integer.parseInt(props.getProperty("max.code.length")); // NOI18N
        JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 0); // NOI18N
        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        addOperatorRecords(props);
        i18n=new I18NSupport();
    }
        
    public void addOperatorRecords(Properties props) {        
        int i;
        String operator;
        String code[], internalLogic[], defaultCode[] = new String[maxComponentCodeLength];
        StringTokenizer operators = new StringTokenizer(props.getProperty("component.operators"), ","); // NOI18N
        for (i=0; i<maxComponentCodeLength; i++) {
            defaultCode[i] = props.getProperty("default.component.code."+String.valueOf(i), ""); // NOI18N
        }
        String defRecursion = "false"; // NOI18N
        while (operators.hasMoreTokens()) {
            operator = operators.nextToken();
            code = new String[maxComponentCodeLength];
            internalLogic = new String[maxComponentCodeLength];
            for (i=0; i<maxComponentCodeLength; i++) {
                code[i] = props.getProperty("operator."+operator+".code."+String.valueOf(i), defaultCode[i]); // NOI18N
                internalLogic[i] = props.getProperty("operator."+operator+".internal."+String.valueOf(i), ""); // NOI18N
            }
            boolean rec = Boolean.valueOf(props.getProperty("operator."+operator+".recursion", defRecursion)).booleanValue(); // NOI18N
            addOperator(operator, props.getProperty("operator."+operator+".prefix",""), props.getProperty("operator."+operator+".suffix",""), props.getProperty("operator."+operator+".method"), code, internalLogic, rec); // NOI18N
        }
        operators = new StringTokenizer(props.getProperty("top.operators"), ","); // NOI18N
        for (i=0; i<maxComponentCodeLength; i++) {
            defaultCode[i] = props.getProperty("default.top.code."+String.valueOf(i), ""); // NOI18N
        }
        defRecursion = "true"; // NOI18N
        while (operators.hasMoreTokens()) {
            operator = operators.nextToken();
            code = new String[maxComponentCodeLength];
            for (i=0; i<maxComponentCodeLength; i++) {
                code[i] = props.getProperty("operator."+operator+".code."+String.valueOf(i), defaultCode[i]); // NOI18N
            }
            boolean rec = Boolean.valueOf(props.getProperty("operator."+operator+".recursion", defRecursion)).booleanValue(); // NOI18N
            addOperator(operator, props.getProperty("operator."+operator+".prefix",""), props.getProperty("operator."+operator+".suffix",""), props.getProperty("operator."+operator+".method"), code, null, rec); // NOI18N
        }
   }

    String getPackage() {
        if ((null==_package) || (_package.length()==0)) {
            return ""; // NOI18N
        } else {
            return "package "+_package+";\n"; // NOI18N
        }
    }
        
    static void replace(StringBuffer sb, String x, String y) {
        int i;
        while ((i=sb.toString().indexOf(x))>=0) {
            sb.delete(i,i+x.length());
            sb.insert(i,y);
        }
    }

    /** add new operator record into set of known operators
     * @param recursion boolean true if recursion is enabled
     * @param internalLogicCode set of internal logic component codes used to source generation
     * @param operatorClass String short operator class name (f.e.: "JButtonOperator")
     * @param instancePrefix prefix for generated names (f.e.: "txt")
     * @param instanceSuffix suffix for generated names (f.e.: "Dialog")
     * @param idMethod String identification method name (f.e.: "getTitle")
     * @param componentCode set of component codes used for source generation */    
    public void addOperator( String operatorClass, String instancePrefix, String instanceSuffix, String idMethod, String[] componentCode, String[] internalLogicCode, boolean recursion ) {
        operators.put( operatorClass, new OperatorRecord( operatorClass, instancePrefix, instanceSuffix, idMethod, componentCode, internalLogicCode, recursion ));
    }
    
    String execMethod( Object o, String method ) {
        if (null==method) return null;
        try {
            Object text = o.getClass().getMethod( method, null ).invoke( o, null );
            if (text!=null) {
                return text.toString();
            } else {
                return ""; // NOI18N
            }
        } catch (Exception e) {
            throw new UndeclaredThrowableException( e, NbBundle.getMessage(ComponentGenerator.class, "MSG_InvocationException", new Object[] {method, o})); // NOI18N
        }
    }
    
    String toJavaIdentifier(String s) {
        StringBuffer sb = new StringBuffer();
        s=i18n.filterI18N(s);
        int i;
        if (null!=s) {
            char ch;
            boolean shift=true;
            for (i=0; i<s.length(); i++) {
                ch = s.charAt(i);
                if (Character.isJavaIdentifierPart(ch)) {
                    if (shift) {
                        shift = false;
                        ch = Character.toUpperCase(ch);
                    }
                    sb.append(ch);
                } else if (ch == '(') {
                    String sss = s.substring (i + 1);
                    shift = !(sss.startsWith("s)")  ||  sss.startsWith ("es)"));
                } else {
                    shift = true;
                }
            }
        } 
        return sb.toString();
    }
    
    String getLabelFor(final Container container, final Component component) {
        JLabel label = JLabelOperator.findJLabel(container, new ComponentChooser() {
            public boolean checkComponent(Component comp) {
                return (comp instanceof JLabel) && (component==((JLabel)comp).getLabelFor());
            }
            public String getDescription() {
                return "GetLabelFor Chooser"; // NOI18N
            }
        });
        if (label!=null) {
            return new JLabelOperator(label).getText();
        }
        return null;
    }
    
    String getUniqueName( String identification, OperatorRecord operatorRecord, String componentClass, Component component, Container container ) {
        String name = toJavaIdentifier(identification);
        if (name.length()==0) {
            name = toJavaIdentifier(getLabelFor(container, component));
        }
        if (name.length()==0) {
            AccessibleContext ac = component.getAccessibleContext();
            if ((ac!=null)&&(!"N/A".equalsIgnoreCase(ac.getAccessibleName()))) { // NOI18N
                name = toJavaIdentifier(ac.getAccessibleName());
            }
        }
        if (name.length()==0) {
            name = operatorRecord.getInstancePrefix()+componentClass;
        } else {
            name = operatorRecord.getInstancePrefix()+name;
        }
        String suffix = operatorRecord.getInstanceSuffix();
        if (!componentNames.contains(name+suffix)) return name+suffix;
        int i=2;
        while (componentNames.contains(name+suffix+String.valueOf(i))) {
            i++;
        }
        return name+suffix+String.valueOf(i);
    }

    int searchForIndex( ComponentOperator operator, ContainerOperator container, String identification ) {
        Constructor c;
        Component component = operator.getSource();
        try {
            if (identification!=null && identification.length()>0) {
                c = operator.getClass().getConstructor( new Class[] { ContainerOperator.class, String.class, Integer.TYPE } );
            } else {
                c = operator.getClass().getConstructor( new Class[] { ContainerOperator.class, Integer.TYPE } );
            }
        } catch (NoSuchMethodException e2) {
            return -1;
        }
        try {
            int i=0;
            while (true) {
                if (identification!=null && identification.length()>0) {
                    operator = (ComponentOperator) c.newInstance(new Object[] { container, identification, new Integer(i)});
                } else {
                    operator = (ComponentOperator) c.newInstance(new Object[] { container, new Integer(i)});
                }
                if (component==operator.getSource()) return i;
                i++;
            }
        } catch ( InstantiationException e3 ) {
        } catch ( IllegalAccessException e4 ) {
        } catch ( InvocationTargetException inve) {
        }
        return -1;
    }
    
    String[] getInternalLabels( Component component ) {
        ArrayList s=new ArrayList();
        ArrayList a=new ArrayList();
        AccessibleContext c=component.getAccessibleContext();
        s.add(c);
        while (s.size()>0) {
            c=(AccessibleContext)s.remove(0);
            if (c!=null) {
                if (AccessibleRole.LABEL.equals(c.getAccessibleRole())) {
                    a.add(c.getAccessibleName());
                }
                for (int i=0;i<c.getAccessibleChildrenCount();i++) {
                    s.add(c.getAccessibleChild(i).getAccessibleContext());
                }
            }
        }
        return (String[])a.toArray(new String[a.size()]);
    }
    
    
    
    protected ComponentRecord addComponent(ComponentOperator componentOperator, ContainerOperator containerOperator, ComponentRecord parentComponent ) {
        String className = componentOperator.getClass().getName();
        OperatorRecord operatorRecord = (OperatorRecord) operators.get( className.substring(className.lastIndexOf('.')+1,className.length()) );
        if ( null==operatorRecord ) return null;
        className = componentOperator.getSource().getClass().getName();
        className = className.substring(className.lastIndexOf('.')+1,className.length());
        String identification = execMethod( componentOperator, operatorRecord.getIdMethod());
        String uniqueName = getUniqueName( identification, operatorRecord, className, componentOperator.getSource(), (Container)containerOperator.getSource() );
        Icon icon = null;
        if (_grabIcons) {
            try {
                if (robot==null) robot = new Robot();
                Rectangle rect=null;
                Component comp = componentOperator.getSource();
                if (comp instanceof JComponent) {
                    rect=((JComponent)comp).getVisibleRect();
                    Point p = comp.getLocationOnScreen();
                    rect.translate(p.x,p.y);
                } else {
                    rect=new Rectangle(comp.getLocationOnScreen(), comp.getSize());
                }
                if(rect.width == 0 || rect.height == 0) {
                    // component has zero size, so skip it
                    return null;
                }
                double scale = Math.pow(rect.width*rect.height,.4)/8;
                icon = new ImageIcon(comp.createImage(new FilteredImageSource(robot.createScreenCapture(rect).getSource(),new AreaAveragingScaleFilter(Math.round(Math.round(rect.width/scale)),Math.round(Math.round(rect.height/scale))))));
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            }
        }
        if (componentOperator.getSource()!=containerOperator.getSource()) {
            if (isTopComponent(componentOperator.getSource())) return null;
            int index;
            if (parentComponent==null) {
                index = searchForIndex( componentOperator, containerOperator, identification );
            } else {
                index = searchForIndex( componentOperator, (ContainerOperator)parentComponent.getComponentOperator(), identification );
            }
            if (index>=0) {
                ComponentRecord record = new ComponentRecord( operatorRecord, identification, uniqueName, index, className, operatorRecord.getInternalRecursion()?getInternalLabels(componentOperator.getSource()):null, icon, componentOperator, parentComponent);
                componentNames.add(uniqueName);
                components.add(record);
                return record;
            }
        } else {
            _container = new ComponentRecord( operatorRecord, identification, uniqueName, 0, className, null, icon, componentOperator, null );
        }
        return null;
    }
    
    protected boolean isTopComponent(Component comp) {
        return (comp instanceof Window)||(comp instanceof JInternalFrame);
    }
    
    /** grabs given visible container identified by ContainerOperator
     * @param _grabIcons boolean true when grab icons of components
     * @param _container Container to grab
     * @param _package String package name of generated source code */    
    public void grabComponents( Container _container, String _package, boolean _grabIcons ) {
        ContainerOperator container = (ContainerOperator)createOperator(_container);
        this._package = _package;
        this._grabIcons = _grabIcons;
        components = new ArrayList();
        componentNames = new HashSet();
        ArrayList queue = new ArrayList();
        ArrayList parentQueue = new ArrayList();
        _container = null;
        queue.add(container);
        parentQueue.add(null);
        ComponentOperator component;
        Component comps[];
        ComponentRecord record, parent;
        int i;
        while (queue.size()>0) {
            component = (ComponentOperator)queue.remove(queue.size()-1);
            parent = (ComponentRecord)parentQueue.remove(parentQueue.size()-1);
            if (component instanceof TabOperator) ((TabOperator)component).selectTab();
            if (component.isShowing()) {
                record = addComponent(component, container, parent);
                if (record!=null && (record.getComponentOperator() instanceof JTabbedPaneOperator)) {
                    JTabbedPaneOperator tabp=(JTabbedPaneOperator)record.getComponentOperator();
                    for (i=tabp.getTabCount()-1; i>=0; i--) {
                        queue.add(queue.size(), new TabOperator(tabp, i));
                        parentQueue.add(parentQueue.size(), record);
                    }
                } else if ((record==null || record.getRecursion()) && (component instanceof ContainerOperator)) {
                    if (record==null) record = parent;
                    comps = ((ContainerOperator)component).getComponents();
                    for (i=comps.length-1; i>=0; i--) {
                        queue.add(createOperator(comps[i]));
                        parentQueue.add(record);
                    }
                }
            }
        }
    }   
    
    protected ComponentOperator createOperator(Component comp) {
        return Operator.createOperator(comp);
    }
    
    private static class TabOperator extends ContainerOperator {
        JTabbedPaneOperator tabPane;
        int index;
        public TabOperator(JTabbedPaneOperator tabPane, int index) {
            super((Container)tabPane.getSource());
            this.tabPane=tabPane;
            this.index=index;
        }
        public TabOperator(ContainerOperator container, String identification, int index) {
            super((Container)container.getSource());
        }
        public String getTabName() {
            return tabPane.getTitleAt(index);
        }
        public Component[] getComponents() {
            selectTab();
            return new Component[]{tabPane.getSelectedComponent()};
        }
        public void selectTab() {
            tabPane.selectPage(index);
        }            
    }
    
    /** returns components code with given index merged from all subcomponents
     * @param i index into component code set
     * @return String generated source code
     */    
    public String getComponentCode(int i) {
        StringBuffer sb = new StringBuffer();
        ComponentRecord rec;
        for (int j=0; j<components.size(); j++) {
            rec=(ComponentRecord)components.get(j);
            sb.append(rec.getComponentCode(i));
            sb.append(rec.getInternalLogicCode(i));
        }
        return sb.toString();
    }
    
    /** returns complete source code merged from all subcomponents
     * @return String generated source code
     */    
    public String getComponentCode() {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<maxComponentCodeLength; i++) {
            if (null!=_container) {
                sb.append(_container.getComponentCode(i));
            }
            sb.append(getComponentCode(i));
        }
        return sb.toString();
    }
    
    /** returns string representation of this last grab
     * @return string representation of this last grab
     */    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int j=0; j<components.size(); j++) {
            sb.append(components.get(j));
        }
        return sb.toString();
    }
    
    /** returns class name from last grab (to be used as part of file name)
     * @return String class name
     */    
    public String getClassName() {
        if (_container!=null) {
            return _container.getUniqueName();
        }
        return null;
    }
    
    /** sets class name
     * @param name String class name */
    public void setClassName(String name) {
        if (name!=null)
            _container.setUniqueName(name);
    }
    
    TreeNode getRootNode() {
        for (int i=0; i<components.size(); i++) {
            ((ComponentRecord)components.get(i)).getNode();
        }
        return _container.getNode();
    }
    
    Collection getNodes() {
        return components;
    }
}

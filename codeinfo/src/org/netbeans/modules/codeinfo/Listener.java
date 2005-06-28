package org.netbeans.modules.codeinfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.ClassDefinition;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.ElementReference;
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.LocalVariable;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.MethodInvocation;
import org.netbeans.jmi.javamodel.MultipartId;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.javacore.api.JavaModel;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;

/**
 *
 * @author tim
 */
class Listener implements KeyListener, CaretListener, Runnable, ChangeListener {
    private long lastKeystrokeTime = Long.MIN_VALUE;
    private final CodeInfoComponent comp;
    private final RequestProcessor rp = new RequestProcessor ("CodeInfo");
    private boolean attached;
    
    Listener(CodeInfoComponent comp) {
        this.comp = comp;
    }
    
    private boolean isActive() {
        return attached;
    }
    
    void attach() {
        attached = true;
        Registry.addChangeListener (this);
        System.err.println("listener attached");
        enqueued = false;
        stateChanged (null);
    }
    
    void detach() {
        attached = false;
        Registry.removeChangeListener (this);
        setEditor (null);
        System.err.println("listener detached");
        enqueued = false;
    }
    
    void update() {
        if (!enqueued) {
            enqueued = true;
            rp.post (this);
        }
    }
    
    private JTextComponent editor = null;
    public void stateChanged (ChangeEvent ce) {
        JTextComponent nue = Registry.getMostActiveComponent();
        if (nue != editor) {
            setEditor (nue);
        }
    }
    
    private synchronized void setEditor (JTextComponent nue) {
        if (editor != null) {
            editor.removeKeyListener (this);
            editor.removeCaretListener (this);
        }
        editor = nue;
        if (editor != null) {
            editor.addKeyListener (this);
            editor.addCaretListener (this);
        }
        update();
    }

    private volatile boolean enqueued = false;
    public void caretUpdate(CaretEvent e) {
        update();
    }

    public void keyPressed(KeyEvent e) {
        //do nothing
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP :
            case KeyEvent.VK_DOWN :
            case KeyEvent.VK_RIGHT :
            case KeyEvent.VK_LEFT :
            case KeyEvent.VK_CONTROL :
            case KeyEvent.VK_META :
            case KeyEvent.VK_ALT :
            case KeyEvent.VK_SHIFT :
                break;
            default :
                lastKeystrokeTime = System.currentTimeMillis();
        }
    }

    public void keyTyped(KeyEvent e) {
        //do nothing
    }
    
    public void run() {
        if (SwingUtilities.isEventDispatchThread() && editor != null) {
            System.err.println("Updating text");
            updateText();
            enqueued = false;
        } else {
            System.err.println("Collecting data");
            if (/* isActive() && */ collectData()) {
                System.err.println(" About to set - " + doc);
                try {
                    SwingUtilities.invokeAndWait (this);
                } catch (InterruptedException e) {
                    System.err.println("Interrupted code update"); //XXX
                    enqueued = false;
                } catch (InvocationTargetException ite) {
                    ErrorManager.getDefault().notify (ite);
                    enqueued = false;
                } catch (RuntimeException re) {
                    enqueued = false;
                    throw re;
                }
            } else {
                System.err.println("  failed");
                enqueued = false;
            }
        }
    }
    
    private String src = null;
    private String doc = null;
    
    private boolean collectData() {
        JTextComponent ed = null;
        synchronized (this) {
            ed = this.editor;
        }
        if (ed == null || !(ed.getDocument() instanceof BaseDocument)) {
            src = null;
            doc = null;
            System.err.println("Not a BaseDocument " + ed + " doc class " + (ed == null ? " null " : ed.getDocument().getClass().getName()));
            return false;
        }
        BaseDocument baseDoc = (BaseDocument) ed.getDocument();
        int offset = ed.getCaret().getDot();
        System.err.println("  find thingy at " + offset);
        try {
            int lineNumber = Utilities.getLineOffset(baseDoc, offset) + 1;

            int realStart = Utilities.getFirstNonWhiteFwd(baseDoc, Utilities.getRowStart(baseDoc, offset));
            int realEnd = Utilities.getFirstNonWhiteBwd(baseDoc, Utilities.getRowEnd(baseDoc, offset));
            if (realStart > realEnd) {
                src = null;
                doc = null;
                System.err.println("Bad offsets " + realStart + " > " + realEnd);
                return false;
            }
            offset = Math.min(Math.max(offset, realStart), realEnd);

            JavaModel.getJavaRepository().beginTrans(false);

            try {
                JMIUtils utils = JMIUtils.get(baseDoc);
                Resource resource = utils.getResource();

                if (resource == null) {
                    System.err.println("  Resource was null");
                    src = null;
                    doc = null;
                    return false;
                }
                JavaModel.setClassPath(resource);
                
                Element el = resource.getElementByOffset(offset);
                
                CallableFeature m = null;
                
                while (el instanceof ElementReference) {
                    Element nue = ((ElementReference) el).getElement();
                    if (nue == el) {
                        break;
                    }
                    el = nue;
                }
                if (el instanceof MultipartId) {
                    MultipartId mid = (MultipartId) el;
                    System.err.println("MULTIPART ID CHILDREN");
                    for (Iterator i=mid.getChildren().iterator(); i.hasNext();) {
                        Object o = i.next();
                        System.err.println(o.getClass().getName() + " - " + o);
                        if (o instanceof Method) {
                            el = (Method) o;
                            break;
                        }
                    }
                }
                if (el != null) {
                    System.err.println("FOUND ELEMENT " + el.getClass().getName() + " - " + el);
                }
                
                if (el instanceof CallableFeature) {
                    m = (CallableFeature) el;
                    StringBuffer sb = new StringBuffer();
                    sb.append (Modifier.toString(m.getModifiers()));
                    sb.append (' ');
                    if (m instanceof Method) {
                        sb.append(((Method)m).getTypeName() == null ? "void" : 
                            ((Method)m).getTypeName().getName());;
                        sb.append (' ');
                    }
                    sb.append (m.getName());
                    sb.append ('(');
                    List p = m.getParameters();
                    for (Iterator i=p.iterator(); i.hasNext();) {
                        Parameter param = (Parameter) i.next();
//                        if (param instanceof MultipartId) {
//                            Element pel = param.getElement();
//                            if (pel)
//                        } else {
                        if (param.getTypeName() != null) {
                            sb.append (param.getTypeName().getName());
                        } else {
                            sb.append (param); //XXX
                        }
//                        }
                        sb.append (' ');
                        sb.append (param.getName());
                        if (i.hasNext()) {
                            sb.append (',');
                        }
                    }
                    sb.append (')');
                    List l = m.getExceptionNames();
                    if (l != null && l.size() > 0) {
                        sb.append (" throws");
                        for (Iterator i=l.iterator(); i.hasNext();) {
                            String s = i.next().toString();
                            sb.append (s);
                            if (i.hasNext()) {
                                sb.append (", ");
                            }
                        }
                    }
                    sb.append (" {\n");
                    try {
                        sb.append (unindent(m.getBodyText()));
                    } catch (NullPointerException npe) { //Bug in JavaModel
                        src = null;
                        sb.append (" //source not available");
                    }
                    sb.append ("\n    }");
                    src = sb.toString();
                    try {
                        doc = m.getJavadoc().getText();
                    } catch (NullPointerException npe) {
                        doc = null;
                    }
                } else if (el instanceof Field) {
                    Field f = (Field) el;
                    doc = f.getJavadocText();
                    StringBuffer sb = new StringBuffer();
                    sb.append (Modifier.toString(f.getModifiers()));
                    sb.append (' ');
                    sb.append (f.getTypeName().getName());
                    sb.append (' ');
                    sb.append (f.getName());
                    if (f.getInitialValue() != null) {
                        sb.append (" = ");
                        sb.append(f.getInitialValueText());
                    }
                    sb.append (';');
                    src = sb.toString();
                } else if (el instanceof LocalVariable) {
                    LocalVariable lv = (LocalVariable) el;
                    StringBuffer sb = new StringBuffer();
                    if (lv.isFinal()) {
                        sb.append ("final ");
                    }
                    sb.append (lv.getTypeName().getName());
                    sb.append (' ');
                    sb.append (lv.getName());
                    if (lv.getInitialValue() != null) {
                        sb.append (" = ");
                        sb.append (lv.getInitialValueText());
                    }
                    sb.append (';');
                    src = sb.toString();
                    doc = null;
                } else if (el instanceof JavaClass) {
                    JavaClass jc = (JavaClass) el;
                    
                }
                

            } finally {
                JavaModel.getJavaRepository().endTrans();
            }            
        } catch (BadLocationException ble) {
            ErrorManager.getDefault().notify (ble);
            src = null;
            doc = null;
            return false;
        }
        return true;
    }
    
    private static Element derive (Element el) {
        while ((el != null) && (!(el instanceof ClassDefinition)) && (!(el instanceof Method)) && (!(el instanceof Field))) {
            while (el instanceof MethodInvocation) {
                Element nue = ((MethodInvocation) el).getElement();
                if (el == nue) {
                    System.err.println("MI crapped out");
                    return null;
                }
                el = ((MethodInvocation) el).getElement();
            }
            while (el instanceof MultipartId) {
                Element nue = ((MultipartId) el).getElement();
                if (el == nue) {
                    System.err.println("Multipart crapped out - children " + new ArrayList(((MultipartId) el).getChildren()));
                    return null;
                }
                el = nue;
            }
        }
        return el;
    }
    
    private String unindent(String s) {
        StringBuffer sb = new StringBuffer (s.length());
        //XXX handle tab chars
        int min = Integer.MAX_VALUE;
        ArrayList al = new ArrayList();
        for (StringTokenizer tok = new StringTokenizer (s, "\n"); tok.hasMoreTokens();) {
            int m = firstNonWhitespace (s);
            if (m != -1) {
                min = Math.min (m, min);
            }
            al.add (tok.nextToken());
        }
        if (min == 0) {
            return s;
        }
        for (Iterator i=al.iterator(); i.hasNext();) {
            String curr = (String) i.next();
            if (curr.length() > min) {
                sb.append ("    ");
                sb.append (curr.substring(min-1));
            }
            sb.append ('\n');
        }
        return sb.toString();
    }
    
    private int firstNonWhitespace (String s) {
        char[] c = s.toCharArray();
        for (int i=0; i < c.length; i++) {
            if (!Character.isWhitespace(c[i])) {
                return i;
            }
        }
        return -1;
    }
    
    
    
    private void updateText() {
        comp.setContent (src == null ? "" : src, doc == null ? "" : doc);
    }
}

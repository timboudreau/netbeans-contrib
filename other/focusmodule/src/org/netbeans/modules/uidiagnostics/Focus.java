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
/*
 * Focus.java
 *
 * Created on February 3, 2003, 11:49 AM
 */

package org.netbeans.modules.uidiagnostics;
import org.openide.util.NbBundle;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.*;
import java.awt.event.*;
import java.util.EventObject;

/** A class that can optionally listen to events and report on those that
 *  pass its filters.
 *
 * @author  Tim Boudreau
 */
class Focus extends Object implements PropertyChangeListener {

    /** Creates a new instance of Focus */
    Focus() {
    }

    public boolean isListening() {
        return isListening;
    }
    
    public boolean toggleState() {
        if (isListening) 
            stopListening();
        else 
            startListening();
        return isListening;
    }
    
    boolean isListening=false;
    public void startListening() {
        if (isListening) return;
        System.out.println(NbBundle.getMessage (Focus.class, "MSG_LogStart") + new java.util.Date()); //NOI18N
        outFilters();
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addPropertyChangeListener (this);
        isListening = true;
    }
    
    public void outFilters() {
        EventFilter[] ef = getFilters();
        System.out.println(NbBundle.getMessage (Focus.class, "MSG_Filters"));
        for (int i=0; i < ef.length; i++) {
            System.out.println(" - " + ef[i].toString());
        }
        System.out.println("");
    }
    
    public void stopListening() {
        if (!isListening) return;
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.removePropertyChangeListener (this);
        isListening = false;
        System.out.println(NbBundle.getMessage (Focus.class, "MSG_LogStop") + new java.util.Date()); //NOI18N
    }
    
    public void configure() {
        JDialog d = new JDialog();
        d.setModal (true);
        d.getContentPane().setLayout (new java.awt.BorderLayout ());
        FilterConfigPanel jb = new FilterConfigPanel();
        jb.setFilters (getFilters());
        d.getContentPane().add (jb, java.awt.BorderLayout.CENTER);
        d.setSize(800, 400);
        d.setLocation (20,20);
        d.show();
        setFilters (jb.getFilters());
    }
    
    EventFilter[] filters=null;
    public void setFilters (EventFilter[] filters) {
        this.filters = filters;
    }
    
    public EventFilter[] getFilters () {
        if (filters != null) return filters;
        return new EventFilter[0];
    }
    
    long lastTime=System.currentTimeMillis();
    public void propertyChange(PropertyChangeEvent evt) {
        if (filters == null) return;
        boolean doOut = false;
        boolean stackTrace=false;
        boolean showEvent = false;
        for (int i=0; i < filters.length; i++) {
            if (filters[i].match (evt)) {
                doOut = true;
                stackTrace |= filters[i].isStackTrace();
                showEvent |= filters[i].isShowEvent();
            }
        }
        if (!doOut) return;
        
        long l = System.currentTimeMillis();
        //if there's been a lull, print a divider
        if (l - lastTime > 10000) {
            System.out.println("--------------------------------------------------\n");
        }
        
        final StringBuffer out = new StringBuffer();
        out.append (l);
        out.append (NbBundle.getMessage(Focus.class, "MSG_Prop") + evt.getPropertyName()); //NOI18N
        Object o = evt.getOldValue();
        Object n = evt.getNewValue();
        out.append (NbBundle.getMessage(Focus.class, "MSG_From") + o2string(o)); //NOI18N
        out.append (NbBundle.getMessage(Focus.class, "MSG_To") + o2string(n)); //NOI18N
        
        if (showEvent) {
            out.append ("\n current event:");
            out.append (eventToString(EventQueue.getCurrentEvent()));
        }
        
        final Exception ex = new Exception();
        ex.fillInStackTrace();
        final boolean trace = stackTrace;
        //Doing this inline can alter timing, changing the behavior of what
        //we're trying to measure
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.err.println(out.toString());
                if (trace) {
                ex.printStackTrace();
                }
            }
        });
        lastTime=l;
    }
    
    static String eventToString (EventObject e) {
        if (e == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        sb.append (e.getClass().getName().substring(e.getClass().getName().lastIndexOf('.')));
        sb.append (' ');
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            switch (me.getID()) {
                case MouseEvent.MOUSE_CLICKED : sb.append("MOUSE_CLICKED");
                    break;
                case MouseEvent.MOUSE_PRESSED : sb.append("MOUSE_PRESSED");
                    break;
                case MouseEvent.MOUSE_RELEASED : sb.append("MOUSE_RELEASED");
                    break;
                case MouseEvent.MOUSE_ENTERED : sb.append("MOUSE_ENTERED");
                    break;
                case MouseEvent.MOUSE_EXITED : sb.append("MOUSE_EXITED");
                    break;
                case MouseEvent.MOUSE_DRAGGED : sb.append("MOUSE_DRAGGED");
                    break;
                case MouseEvent.MOUSE_WHEEL : sb.append("MOUSE_WHEEL");
                    break;
                case MouseEvent.MOUSE_MOVED : sb.append("MOUSE_MOVED");
                    break;
            }
            sb.append (" modifiers=");
            sb.append (me.getModifiersExText(me.getModifiersEx()));
        } else if (e instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) e;
            switch (ke.getID()) {
                case KeyEvent.KEY_PRESSED : sb.append ("KEY_PRESSED");
                case KeyEvent.KEY_TYPED : sb.append("KEY_TYPED");
                case KeyEvent.KEY_RELEASED : sb.append ("KEY_RELEASED");
            }
            sb.append (" key=");
            sb.append(ke.getKeyText(ke.getKeyCode()));
            sb.append (" modifiers=");
            sb.append(KeyEvent.getModifiersExText(ke.getModifiersEx()));
        } else if (e instanceof FocusEvent) {
            FocusEvent fe = (FocusEvent) e;
            Component src = fe.getSource() != null ? (Component) fe.getSource() : null;
            Component opp = fe.getOppositeComponent();
            String type = fe.getID() == fe.FOCUS_LOST ? "FOCUS_LOST" : "FOCUS_GAINED";
            sb.append ("Type: " + type);
            sb.append ("Source: " + c2s (src));
            sb.append (" Opposite: " + c2s(opp));
            boolean lost = fe.getID() == fe.FOCUS_LOST;
            Point p = lost ? opp.getLocation() : src.getLocation();
            SwingUtilities.convertPointToScreen(p, lost ? opp : src);
            sb.append (" location of focused:" + p.x + "," + p.y);
            markComponent(src, lost ? Color.GREEN : Color.ORANGE);
            markComponent(opp, lost ? Color.ORANGE : Color.GREEN);
        }
        sb.append (" source:");
        sb.append (e.getSource() != null ? e.getSource().getClass().getName() : " null");
        if (e.getSource() instanceof Component && ((Component) e.getSource()).getName() != null) {
            sb.append (" name=");
            sb.append (((Component) e.getSource()).getName());
        }
        return sb.toString();
    }

    private Component lastComponent = null;
    static final void markComponent(Component c, Color color) {
        if (c == null) {
            return;
        }
        if (c.isShowing()) {
            Graphics g = c.getGraphics();
            Rectangle r = c.getBounds();
            if (g != null) {
                Color col = g.getColor();
                try {
                    g.setColor(color);
                    g.drawRect (0,0, r.width-1, r.height-1);
                    g.drawRect(1,1, r.width-2, r.height-2);
                } finally {
                    g.setColor(col);
                }
            }
        }
    }
    
    static String c2s (Component c) {
        if (c == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        String s = c.getName();
        if (s != null) {
            sb.append ("name:" + s);
        }
        sb.append ("(" + c.getClass().getName() + ")");
        sb.append (" isVisible=" + c.isVisible());
        sb.append (" isDisplayable=" + c.isDisplayable());
        sb.append (" isShowing=" + c.isShowing());
        return sb.toString();
    }
    
    static String o2string (Object o) {
        if (o == null) return NbBundle.getMessage(Focus.class, "MSG_Null");
        if (o instanceof Component) {
            Component c = (Component) o;
            String s = c.getName();
            if (s == null) s = c.getClass().getName();
            return s;
        } else {
            return o.toString();
        }
    }

}

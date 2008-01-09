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
package org.netbeans.modules.a11ychecker.traverse;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 *
 * @author stehlik
 */
public class OverflowLbl extends JLabel {

    private boolean inTabOrder;
    MyGlassPane glass;
    Component mycomp;
    Component nextcomp;
    OverflowLbl nextbutton;
    int state = NOTHING;
    final static int NOTHING = 0;
    final static int SELECTED = 1;
    final static int INTABORDER = 2;
    final static int HOVER = 4;
    final static int ITA_FIRST = 5;
    final static int ITA_LAST = 6;
    boolean isSelected = false;
    Border overComp = BorderFactory.createLineBorder(Color.RED, 2);
    Border selectedComp = BorderFactory.createLineBorder(Color.BLACK, 2);
    Border inTabOrderComp = BorderFactory.createLineBorder(Color.BLUE, 2);
    
    Border inTabOrderFirst = BorderFactory.createCompoundBorder(selectedComp, BorderFactory.createLineBorder(Color.GREEN, 2));
    Border inTabOrderLast = BorderFactory.createCompoundBorder(selectedComp, BorderFactory.createLineBorder(Color.RED, 2));
    
    Timer myTimer;

    OverflowLbl(final MyGlassPane glass, final Component myComp) {
        super();
        this.glass = glass;
        this.mycomp = myComp;
        nextcomp = null;
        nextbutton = null;
        final OverflowLbl thisOver = this;
        setBackground(new Color(0f, 0f, 0.5f));
        
       addKeyListener( 
               new KeyListener() {        
                    public void keyReleased(KeyEvent e) { }
                    public void keyPressed(KeyEvent e) { 
                        glass.processKey(e); 
                    }
                    public void keyTyped(KeyEvent e) { }
                }
       );
        
        addMouseListener(new MouseListener() {

                    private boolean hasMouseDoubleClicked;

                    public void mouseClicked(MouseEvent e) {

                    }

                    public void mousePressed(MouseEvent e) {
                        final MouseEvent evt = e;
                        if (e.getClickCount() == 1) {
                            myTimer = new Timer();
                            myTimer.schedule(new TimerTask() {

                                        public void run() {
                                            if (!hasMouseDoubleClicked) {
                                                if(evt.isControlDown() || evt.isShiftDown())
                                                {
                                                    if(/*thisOver.isSelected &&*/ evt.isControlDown() && (evt.getButton() == MouseEvent.BUTTON1))
                                                        glass.setStart(thisOver);
                                                    else if(/*thisOver.isSelected &&*/ evt.isShiftDown() && (evt.getButton() == MouseEvent.BUTTON1))
                                                        glass.setEnd(thisOver);
                                                }
                                                else
                                                {
                                                    glass.processClick(thisOver, mycomp, evt);
                                                    setBorder(SELECTED);
                                                }
                                            }

                                            hasMouseDoubleClicked = false;
                                            myTimer.cancel();
                                        }
                                    }, 180);
                        } else if (e.getClickCount() == 2) {
                            hasMouseDoubleClicked = true;
                            if(!e.isControlDown() && !e.isShiftDown())
                                glass.deleteClick(thisOver);
                        }
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                        if(!isSelected) setBorder(HOVER);
                    }

                    public void mouseExited(MouseEvent e) {
                        if(!isSelected) setBorder(NOTHING);
                    }
                });

    }

    public boolean isInTabOrder() {
        return inTabOrder;
    }

    public void setInTabOrder(boolean inTabOrder) {
        this.inTabOrder = inTabOrder;
    }

    public void setBorder(int state) {
        switch (state) {
            case NOTHING: {
                setBorder(null);
                break;
            }
            case HOVER: {
                setBorder(overComp);
                break;
            }
            
            case SELECTED: {
                isSelected=true;
                setBorder(selectedComp);
                break;
            }
            case INTABORDER: {
                setBorder(inTabOrderComp);
                break;
            }
            
            default:break;
        }
    }
}
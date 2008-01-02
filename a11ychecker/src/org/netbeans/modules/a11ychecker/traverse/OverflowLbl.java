package org.netbeans.modules.a11ychecker.traverse;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
                                                        glass.setEnd(thisOver);
                                                    else if(/*thisOver.isSelected &&*/ evt.isShiftDown() && (evt.getButton() == MouseEvent.BUTTON1))
                                                        glass.setStart(thisOver);
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
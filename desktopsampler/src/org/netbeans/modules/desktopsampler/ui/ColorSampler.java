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

package org.netbeans.modules.desktopsampler.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.windows.WindowManager;

/**
 * A simple desktop color sampler. Drag mouse from the color sample
 * label and release mouse anywhere on desktop to sample color at that
 * point.
 *
 * Note: Uses java.awt.Robot.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class ColorSampler extends JToolBar {
    static ImageIcon COLOR_SAMPLER_ICON = new ImageIcon(ColorSampler.class.getResource("ColorSampler.gif")); // NOI18N
    static ImageIcon FORMAT_ICON        = new ImageIcon(ColorSampler.class.getResource("Format.gif"));  // NOI18N
    
    private static final Image DROPPER_IMAGE  = new ImageIcon(ColorSampler.class.getResource("Dropper.gif")).getImage();  // NOI18N
    
    private static final Cursor DROPPER_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(DROPPER_IMAGE, new Point(9, 22), "Dropper");  // NOI18N
    
    private JLabel sampleColorLabel;
    private JLabel colorPreviewLabel;
    private JTextField colorValueField;
    private JButton formatButton;
   
    private Robot robot;
    
    private static int index = 0;
    
    private static final int POUND_XXXXXX        = index++;
    private static final int RGB_PAREN_RGB       = index++;
    private static final int NEW_COLOR_RGB       = index++;
    private static final int XXXXXX              = index++;
    private static final int SQUARE_BRACKET_RGB  = index++;
    private static final int RGB                 = index++;
    
    private static final int[] COLOR_FORMAT_INDICES =
    {
        POUND_XXXXXX       ,
        RGB_PAREN_RGB      ,
        NEW_COLOR_RGB      ,
        XXXXXX             ,
        SQUARE_BRACKET_RGB ,
        RGB                ,
    };
    
    private static String[] COLOR_FORMATS = new String[COLOR_FORMAT_INDICES.length];
    
    static
    {
        COLOR_FORMATS[POUND_XXXXXX        ] = "#XXXXXX";            // NOI18N e.g. #FFFFFF
        COLOR_FORMATS[RGB_PAREN_RGB       ] = "rgb(r, g, b)";       // NOI18N e.g. rgb(r, g, b)
        COLOR_FORMATS[NEW_COLOR_RGB       ] = "new Color(r, g, b)"; // NOI18N e.g. new Color(255, 255, 255)
        COLOR_FORMATS[XXXXXX              ] = "XXXXXX";             // NOI18N e.g. FFFFFF
        COLOR_FORMATS[SQUARE_BRACKET_RGB  ] = "[r, g, b]";          // NOI18N e.g. [r, g, b]
        COLOR_FORMATS[RGB                 ] = "r, g, b";            // NOI18N e.g. r,g,b
    };
    
    private String colorFormat = COLOR_FORMATS[0];
    
    /**
     * Creates a new <code>ColorSampler</code> instance.
     *
     */
    public ColorSampler() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println(e);
            System.exit(1);
        }
        
        setFloatable(false);
        
        sampleColorLabel = new JLabel(COLOR_SAMPLER_ICON, JLabel.CENTER) {
            public Point getToolTipLocation(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                point.y += 20;
                return point;
            }
        };
        sampleColorLabel.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
        sampleColorLabel.setToolTipText(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("TOOLTIP_ColorSampler")); // NOI18N       
        add(sampleColorLabel);
        
        colorPreviewLabel = new JLabel("                                    ");  // NOI18N
        colorPreviewLabel.setOpaque(true);
        colorPreviewLabel.setBorder(
        BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.gray),
        BorderFactory.createEmptyBorder(3,3,3,3)));
        add(colorPreviewLabel);
        
        colorValueField = new JTextField(16);
        colorValueField.setDragEnabled(true);
        colorValueField.setEditable(false);
        add(colorValueField);
        
        formatButton = new JButton(FORMAT_ICON);
        formatButton.setMargin(new Insets(1,1,1,1));
        formatButton.setBorderPainted(false);
        formatButton.setFocusPainted(false);
        formatButton.setRolloverEnabled(true);
        formatButton.setToolTipText(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("TOOLTIP_SelectColorFormat")); // NOI18N
        
        add(formatButton);
        formatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newColorFormat = (String) JOptionPane.showInputDialog(
                WindowManager.getDefault().getMainWindow(),
                ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("Select_color_format"), // NOI18N
                ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("Color_format"),  // NOI18N
                JOptionPane.PLAIN_MESSAGE,
                FORMAT_ICON,
                COLOR_FORMATS,
                colorFormat);
                if (newColorFormat != null) {
                    colorFormat = newColorFormat;
                    showColor(colorPreviewLabel.getBackground(), false);
                }
            }
        });
        
        showColor(colorPreviewLabel.getBackground(), false);
        
        sampleColorLabel.addMouseListener(
        new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                SwingUtilities.getWindowAncestor(ColorSampler.this).setCursor(DROPPER_CURSOR);
            }
            public void mouseReleased(MouseEvent me) {
                Point p = me.getPoint();
                SwingUtilities.convertPointToScreen(p, me.getComponent());
                sampleColorAtPoint(p, false);
                SwingUtilities.getWindowAncestor(ColorSampler.this).setCursor(Cursor.getDefaultCursor());
            }
        });
        
        sampleColorLabel.addMouseMotionListener(
        new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                Point p = me.getPoint();
                SwingUtilities.convertPointToScreen(p, me.getComponent());
                sampleColorAtPoint(p, true);
                SwingUtilities.getWindowAncestor(ColorSampler.this).setCursor(DROPPER_CURSOR);
            }
        });
    }
    
    public JLabel getColorPreviewLabel() {
        return colorPreviewLabel;
    }
    
    public Color getSelectedColor() {
        return colorPreviewLabel.getBackground();
    }
    
    public void setSelectedColor(Color color) {
        if (color.equals(getSelectedColor())) {
            return;
        }
        
        showColor(color, false);
    }
    
    public String getSelectedColorString() {
        return format(getSelectedColor());
    }
    
    public void sampleColorAtPoint(Point p, boolean temporary) {
        showColor(robot.getPixelColor(p.x, p.y), temporary);
    }
    
    void showColor(Color color, boolean temporary) {
        colorPreviewLabel.setBackground(color);
        colorValueField.setText(format(color));
        SwingUtilities.invokeLater(
        new Runnable() {
            public void run() {               
                colorValueField.selectAll();
                colorValueField.getCaret().setSelectionVisible(true);
            }
        }
        );
        
        //if (!temporary) {
        fireStateChanged();
        //}
    }
    
    public String format(Color color) {
        if (colorFormat.equals(COLOR_FORMATS[POUND_XXXXXX])) {
            return "#" + hex(color.getRed()) + hex(color.getGreen()) + hex(color.getBlue());  // NOI18N
        } else if (colorFormat.equals(COLOR_FORMATS[RGB_PAREN_RGB])) {
            return "rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";  // NOI18N
        } else if (colorFormat.equals(COLOR_FORMATS[NEW_COLOR_RGB])) {
            return "new Color(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";  // NOI18N
        } else if (colorFormat.equals(COLOR_FORMATS[XXXXXX])) {
            return hex(color.getRed()) + hex(color.getGreen()) + hex(color.getBlue());
        } else if (colorFormat.equals(COLOR_FORMATS[SQUARE_BRACKET_RGB])) {
            return "[" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]";  // NOI18N
        } else if (colorFormat.equals(COLOR_FORMATS[RGB])) {
            return "" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "";  // NOI18N
        }
        return "new Color(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";  // NOI18N
    }
    
    private String hex(int i) {
        String h = Integer.toHexString(i);
        if (i < 10) {
            h = "0" + h;  // NOI18N
        }
        return h.toUpperCase();
    }
    
    /**
     * Only one <code>ChangeEvent</code> is needed per model instance
     * since the event's only (read-only) state is the source property.
     * The source of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    
    protected EventListenerList listenerList = new EventListenerList();
    
    /**
     * Adds a <code>ChangeListener</code> to the model.
     *
     * @param l the <code>ChangeListener</code> to be added
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a <code>ChangeListener</code> from the model.
     * @param l the <code>ChangeListener</code> to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    /**
     * Returns an array of all the <code>ChangeListener</code>s added
     * to this <code>DefaultColorSelectionModel</code> with
     * <code>addChangeListener</code>.
     *
     * @return all of the <code>ChangeListener</code>s added, or an empty
     *         array if no listeners have been added
     * @since 1.4
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[])listenerList.getListeners(
        ChangeListener.class);
    }
    
    /**
     * Runs each <code>ChangeListener</code>'s
     * <code>stateChanged</code> method.
     *
     * <!-- @see #setRangeProperties    //bad link-->
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }

    /**
     * <code>ColorSampler</code> test.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e);
        }
        
        JFrame frame = new JFrame(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("Desktop_Color_Sampler")); // NOI18N
        frame.setContentPane(new ColorSampler());
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

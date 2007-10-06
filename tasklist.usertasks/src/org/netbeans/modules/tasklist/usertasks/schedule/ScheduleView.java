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

package org.netbeans.modules.tasklist.usertasks.schedule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JComponent;
import org.netbeans.modules.tasklist.usertasks.*;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.options.Settings;

/**
 * Schedule component.
 *
 * @author tl
 */
public class ScheduleView extends JComponent {
    private static final int DAY_WIDTH = 50;
    private static final int TASK_HEIGHT = 10;
    private static final Font FONT = new Font("SansSerif",  // NOI18N
        Font.PLAIN, 12);
    private static final Color LINES_COLOR = Color.GRAY;
    private static final Color WEEKEND_COLOR = new Color(237, 237, 237);
    private static final DateFormat MONTH_FORMAT = 
        new SimpleDateFormat("MMMM yyyy"); // NOI18N
    
    private Date start = new Date();
    private int days = 100;
    private UserTaskList utl;
    
    /** 
     * Creates a new instance of ScheduleView 
     */
    public ScheduleView() {
    }

    /**
     * Shows another user task list.
     *
     * @param list new list or null
     */
    public void setUserTaskList(UserTaskList list) {
        this.utl = list;
        repaint();
    }
    
    protected void paintComponent(java.awt.Graphics g) {
        Rectangle r = g.getClipBounds();
        g.setColor(Color.white);
        g.fillRect(r.x, r.y, r.width, r.height);
        
        paintDays(g);
        if (utl == null)
            g.drawString("---", getWidth() / 2, getHeight() / 2); // NOI18N
        else
            paintTasks(g, utl.getSubtasks(), 0, 32);
    }

    /**
     * Paints vertical lines for days
     *
     * @param g graphics object
     */
    private void paintDays(Graphics g) {
        g.setColor(WEEKEND_COLOR);
        g.fillRect(0, 0, getWidth(), 30);
        
        g.setFont(FONT);
        
        g.setColor(LINES_COLOR);
        g.drawLine(0, 15, getWidth() - 1, 15);
        g.drawLine(0, 30, getWidth() - 1, 30);
        
        Calendar gc = GregorianCalendar.getInstance();
        gc.setTime(start);
        
        for (int x = 0; x < getWidth(); x += DAY_WIDTH) {
            int day = gc.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
            
            if (day == 1) {
                g.drawString(MONTH_FORMAT.format(gc.getTime()), x, 13);
            }

            if (dayOfWeek == Calendar.SATURDAY ||
                dayOfWeek == Calendar.SUNDAY) {
                g.setColor(WEEKEND_COLOR);
                g.fillRect(x, 31, DAY_WIDTH, getHeight() - 31);
            }
            
            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(day), x, 28); // NOI18N
            
            g.setColor(LINES_COLOR);
            g.drawLine(x, 15, x, getHeight());
            gc.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    /**
     * Paints a list of tasks (and all subtasks)
     *
     * @param g Graphics object
     * @param date start date as in Date.getTime()
     * @param list list of tasks
     * @param y Y-coordinate for the first task
     */
    private void paintTasks(Graphics g, UserTaskObjectList list, int x, int y) {
        for (int i = 0; i < list.size(); i++) {
            UserTask ut = list.getUserTask(i);
            
            Duration dur = new Duration(ut.getEffort(), 
                Settings.getDefault().getMinutesPerDay(), 
                Integer.MAX_VALUE, true);
            int duration = dur.days;
            if (dur.hours != 0 || dur.minutes != 0)
                duration++;
            int w = duration * DAY_WIDTH;
            
            if (ut.getSubtasks().isEmpty()) {
                paintSimpleTask(g, x, y, w);
            } else {
                paintSuperTask(g, x, y, w);
                paintTasks(g, ut.getSubtasks(), x, y + TASK_HEIGHT);
            }
            
            x += w;
            y += TASK_HEIGHT;
        }
    }
    
    /**
     * Paints a simple task without children
     * |-------------|
     * |-------------|
     *
     * @param g graphics object
     * @param x x coordinate of the upper left corner
     * @param y y coordinate of the upper left corner
     * @param width width of the task
     */
    private void paintSimpleTask(Graphics g, int x, int y, int width) {
        g.setColor(new Color(140, 182, 206));
        g.fillRect(x, y, width, TASK_HEIGHT);
        g.setColor(Color.black);
        g.drawRect(x, y, width - 1, TASK_HEIGHT - 1);
    }
    
    /**
     * Paints a task with children.
     * |-------------|
     * |/           \|
     *
     * @param g graphics object
     * @param x x coordinate of the upper left corner
     * @param y y coordinate of the upper left corner
     * @param width width of the task
     */
    private void paintSuperTask(Graphics g, int x, int y, int width) {
        g.setColor(Color.BLACK);
        
        int x2 = x + width;
        g.fillRect(x, y, width, 2);
                
        g.fillPolygon(new int[] {x, x + 7, x}, new int[] {y, y, y + 7}, 3);
        g.fillPolygon(new int[] {x2, x2 - 7, x2}, new int[] {y, y, y + 7}, 3);
    }
    
    /**
     * @param d date difference in milliseconds
     * @return width in the view
     */
    private int widthForDateDiff(long d) {
        double days = d / (1000.0 * 60 * 60 * 24);
        return (int) Math.round(days * DAY_WIDTH);
    }
    
    /**
     * Converts a date to the X-coordinate
     *
     * @param d a date as in Date.getTime()
     * @return X-coordinate
     */
    private int xForDate(long d) {
        long diff = d - start.getTime();
        double days = diff / (1000.0 * 60 * 60 * 24);
        return (int) Math.round(days * DAY_WIDTH);
    }
    
    public java.awt.Dimension getPreferredSize() {
        return new Dimension(days * DAY_WIDTH, 200);
    }
}

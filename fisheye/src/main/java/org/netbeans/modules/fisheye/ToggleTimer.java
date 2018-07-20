/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.fisheye;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * A general timer for handling animated things which may or may not have 
 * multiple steps.  A timer has a direction which can be thought of as moving
 * toward false or true.
 *
 * @author Tim Boudreau
 */
final class ToggleTimer {
    private final Timer timer;
    private final ToggleTimer.Handler handler;
    private final int count;
    private boolean aborted = false;
    private boolean direction;
    private int ticks = 0;
    
    /** Creates a new instance of ToggleTimer 
     * If Handler is an instanceof MultiStepHandler, it will receieve ticks
     * for each timer event.
     * @param handler an object to call back at various points during the timer
     *  cycle.  May not be null.
     * @param delay The delay between individual ticks.  Must be > 0.
     * @param direction whether the eventual state when this timer completes 
     *  should be false or true.  This value is passed to the handler start,
     *  finish and tick methods
     * @param last The last tick, at which Handler.finished() is called.  If
     *  zero, Handler.finished() will be called after only one interval of 
     *  <code>delay</code> milliseconds.  Must be >= 0;
     */
    public ToggleTimer(Handler handler, int delay, boolean direction, int last) {
        this.handler = handler;
        assert handler != null;
        assert last >= 0;
        assert delay > 0;
        this.count = last;
        this.direction = direction;
        timer = new Timer (delay, new AL());
        timer.setRepeats(last > 0);
        handler.start (this, direction);
        timer.start();
    }
    
    public void abort() {
        if (timer != null) {
            timer.stop();
            handler.aborted(this, ticks, direction);
        }
    }
    
    public void forceFinish() {
        if (timer.isRunning()) {
            timer.stop();
        }
        handler.finish(this, direction);
    }
    
    public int getNextTick() {
        return ticks;
    }
    
    public boolean reverse() {
        if (timer.isRunning()) {
            direction = !direction;
            timer.restart();
            return direction;
        }
        throw new IllegalStateException ("Already finished."); //NOI18N
    }
    
    public boolean isRunning() {
        return timer.isRunning();
    }
    
    public void restart() {
        if (isRunning()) {
            timer.restart();
            handler.start(this, direction);
            ticks = 0;
        }
    }
    
    public boolean stop() {
        timer.stop();
        return timer.isRunning();
    }
    
    public boolean getDirection() {
        return direction;
    }
    
    private class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!aborted && handler instanceof MultiStepHandler) {
                ((MultiStepHandler)handler).tick (ToggleTimer.this, 
                        ticks, direction);
            }
            if (ticks >= count) {
                if (!aborted) {
                    handler.finish (ToggleTimer.this, direction);
                }
                timer.stop();
            }
            ticks++;
        }
    }
    
    public static interface Handler {
        public void aborted(ToggleTimer timer, int at, boolean direction);
        public void start (ToggleTimer timer, boolean direction);
        public void finish (ToggleTimer timer, boolean direction);
    }
    
    public static interface MultiStepHandler extends Handler {
        public void tick(ToggleTimer timer, int index, boolean direction);
    }
}

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

package org.netbeans.modules.vcs.advanced;
import java.awt.*;
import java.util.*;
import java.beans.*;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.commands.CommandsTree;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder;

/** Property editor for UserCommand.
 *
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserConditionedCommandsEditor implements PropertyEditor {
    
    //private Vector commands=new Vector(10);
    //private Node commands = null;
    //private CommandsTree commands = null;
    private ConditionedCommands ccommands = null;

    private PropertyChangeSupport changeSupport=null;

    //-------------------------------------------
    public UserConditionedCommandsEditor(){
        // each PropertyEditor should have a null constructor...
        changeSupport=new PropertyChangeSupport(this);
    }

    //-------------------------------------------
    public String getAsText(){
        // null if the value can't be expressed as an editable string...
        return NbBundle.getMessage(UserConditionedCommandsEditor.class, "PROP_commands"); // NOI18N
    }

    //-------------------------------------------
    public void setAsText(String text) {
        //D.deb("setAsText("+text+") ignored"); // NOI18N
    }

    //-------------------------------------------
    public boolean supportsCustomEditor() {
        return true ;
    }

    //-------------------------------------------
    public Component getCustomEditor() {
        return new UserConditionedCommandsPanel(this);
    }

    //-------------------------------------------
    public String[] getTags(){
        // this property cannot be represented as a tagged value..
        return null ;
    }

    //-------------------------------------------
    public String getJavaInitializationString() {
        return ""; // NOI18N
    }

    //-------------------------------------------
    public Object getValue() {
        return ccommands;
        //return new ConditionedCommandsBuilder(commands).getConditionedCommands();
    }

    //-------------------------------------------
    public void setValue(Object value) {
        if (!(value instanceof ConditionedCommands)) {
            throw new IllegalArgumentException("ConditionedCommands expected instead of "+value);
        }
        ccommands = (ConditionedCommands) value;
        /*
        // make local copy of value - deep copy using clone
        commands = new Vector();
        Vector vect = (Vector) value;
        for(int i = 0; i < vect.size (); i++) {
            UserCommand cmd = (UserCommand) vect.get (i);
            commands.add (cmd.clone ());
        }
         */

        changeSupport.firePropertyChange("",null,null); // NOI18N
    }

    //-------------------------------------------
    public boolean isPaintable() {
        return false ;
    }

    //-------------------------------------------
    public void paintValue(Graphics gfx, Rectangle box){
        // silent noop
    }

    //-------------------------------------------
    public void addPropertyChangeListener (PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }

    //-------------------------------------------
    public void removePropertyChangeListener (PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }

}

/*
 * <<Log>>
 *  13   Gandalf   1.12        1/27/00  Martin Entlicher NOI18N
 *  12   Gandalf   1.11        10/25/99 Pavel Buzek     copyright
 *  11   Gandalf   1.10        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  10   Gandalf   1.9         9/30/99  Pavel Buzek     
 *  9    Gandalf   1.8         9/8/99   Pavel Buzek     
 *  8    Gandalf   1.7         9/8/99   Pavel Buzek     class model changed, 
 *       customization improved, several bugs fixed
 *  7    Gandalf   1.6         8/31/99  Pavel Buzek     
 *  6    Gandalf   1.5         8/31/99  Pavel Buzek     
 *  5    Gandalf   1.4         5/4/99   Michal Fadljevic 
 *  4    Gandalf   1.3         5/4/99   Michal Fadljevic 
 *  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
 *  2    Gandalf   1.1         4/22/99  Michal Fadljevic 
 *  1    Gandalf   1.0         4/21/99  Michal Fadljevic 
 * $
 */

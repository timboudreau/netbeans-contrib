/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints.batch;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Jan Lahoda
 */
public final class BatchApplyAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private static final String HINT = "hint";
    
    private final Lookup context;
    private final Lookup.Result<Object> r;
    private final Map attributes;
    
    public BatchApplyAction() {
        this(prepareDefaultMap());
    }

    public BatchApplyAction(Map attributes) {
        this(Utilities.actionsGlobalContext(), attributes);
    }

    private BatchApplyAction(Lookup context, Map attributes) {
        this.context = context;

        this.r = context.lookupResult(Object.class); //XXX
        this.r.addLookupListener(this);
        this.r.allInstances();
        updateEnabled();

        this.attributes = attributes;
    }

    public void actionPerformed(ActionEvent e) {
        String hintToExecute = (String) getValue(HINT);

        if (hintToExecute == null) {
            SelectHint p = new SelectHint();
            DialogDescriptor dd = new DialogDescriptor(p, "Select Hint", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null);

            if (DialogDisplayer.getDefault().notify(dd) != DialogDescriptor.OK_OPTION) {
                return ;
            }
            
            hintToExecute = p.getSelectedHint().getId();
        }

        String error = BatchApply.applyFixes(context, Collections.singleton(hintToExecute), true);

        if (error != null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(error, NotifyDescriptor.ERROR_MESSAGE));
        }
    }

    private void updateEnabled() {
        setEnabled(!BatchApply.toProcess(context).isEmpty());
    }

    public void resultChanged(LookupEvent ev) {
        updateEnabled();
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new BatchApplyAction(context, attributes);
    }

    private static Map prepareDefaultMap() {
        Map<String, Object> m = new HashMap<String, Object>();

        m.put(NAME, NbBundle.getMessage(BatchApplyAction.class, "CTL_BatchApplyAction"));
        m.put("noIconInMenu", Boolean.TRUE);

        return m;
    }
    
    @Override
    public Object getValue(String name) {
        Object o = super.getValue(name);

        if (o != null) {
            return o;
        }

        return extractCommonAttribute(attributes, this, name);
    }

    static final Object extractCommonAttribute(Map fo, Action action, String name) {
        if (Action.NAME.equals(name)) {
            String actionName = (String) fo.get("displayName"); // NOI18N
            // NOI18N
            //return Actions.cutAmpersand(actionName);
            return actionName;
        }
        if (Action.MNEMONIC_KEY.equals(name)) {
            String actionName = (String) fo.get("displayName"); // NOI18N
            // NOI18N
            int position = Mnemonics.findMnemonicAmpersand(actionName);

            return position == -1 ? null : Character.valueOf(actionName.charAt(position + 1));
        }
        if (Action.SMALL_ICON.equals(name)) {
            Object icon = fo == null ? null : fo.get("iconBase"); // NOI18N
            if (icon instanceof Icon) {
                return (Icon) icon;
            }
            if (icon instanceof Image) {
                return ImageUtilities.image2Icon((Image)icon);
            }
            if (icon instanceof String) {
                return ImageUtilities.loadImage((String)icon);
            }
            if (icon instanceof URL) {
                return Toolkit.getDefaultToolkit().getImage((URL) icon);
            }
        }
        if ("iconBase".equals(name)) { // NOI18N
            return fo == null ? null : fo.get("iconBase"); // NOI18N
        }
        if ("noIconInMenu".equals(name)) { // NOI18N
            return fo == null ? null : fo.get("noIconInMenu"); // NOI18N
        }
        if (Action.ACCELERATOR_KEY.equals(name)) {
            Keymap map = Lookup.getDefault().lookup(Keymap.class);
            if (map != null) {
                KeyStroke[] arr = map.getKeyStrokesForAction(action);
                return arr.length > 0 ? arr[0] : null;
            }
        }
        if (HINT.equals(name)) {
            return fo.get("hint"); //NOI18N
        }

        return null;
    }

    public static Action createBatchHintAction(Map attributes) {
        return new BatchApplyAction(attributes);
    }

}

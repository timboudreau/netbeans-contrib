/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.client;

import org.openide.text.Line;

import java.awt.*;
import org.netbeans.modules.tasklist.client.SuggestionPriority;

/**
 * Agent allows provider to update suggestion.
 *
 * @since 1.11
 * @author Petr Kuzel
 */
public final class SuggestionAgent {

    private final Suggestion suggestion;

    /** Only framework may call this other will get IllegalStateException. */
    public SuggestionAgent(Suggestion suggestion) {
        if (suggestion.agent != null) {
            throw new IllegalStateException();
        }
        suggestion.agent = this;
        this.suggestion = suggestion;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setAction(SuggestionPerformer action) {
        suggestion.setAction(action);
    }

    public void setSummary(String summary) {
        suggestion.setSummary(summary);
    }

    public void setDetails(String details) {
        suggestion.setDetails(details);
    }

    public void setPriority(SuggestionPriority prio) {
        suggestion.setPriority(prio);
    }

    // TODO add line to suggestion constructor
    /** @deprecated line is live object no need to chaneg its instances */
    public void setLine(Line line) {
        suggestion.setLine(line);
    }

    public void setIcon(Image image) {
        suggestion.setIcon(image);
    }

    /**
     * This suggestion is not managed by provider anymore.
     * It should be forgoten (all string references removed)
     * because its validity status cannot be reverted to back true. 
     */
    public void invalidate() {
        suggestion.invalidate();
    }
}

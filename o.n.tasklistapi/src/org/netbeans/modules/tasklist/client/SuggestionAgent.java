/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.client;

import org.openide.text.Line;

import java.awt.*;

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

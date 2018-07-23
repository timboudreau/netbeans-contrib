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

package org.netbeans.modules.tasklist.suggestions;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.Document;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.openide.cookies.EditorCookie;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.netbeans.modules.tasklist.core.*;
import org.openide.text.CloneableEditorSupport;

/**
  Implements a document suggestion cache.
  This makes switching back and forth between files
  "immediate" once the suggestions have been computed
  in the first place. The suggestions are immediately
  posted (not just meaning that it's not recomputed
  so you get the slow refresh one by one, but there's
  no delay either).
<p>
The cache works as follows:
<ul>
   <li> When an entry is added to the cache, it adds a listener
     to the document. If the document changes when the
     document is not showing, the cache entry is invalidated/
     removed.
   <li> SuggestionImpl's keep a provider reference. When
     you swap files, all the suggestions currently showing
     are dumped to the cache. 
   <li> when you open a file, in docShown we first
     consult the cache. If found, simply use those, 
     otherwise call docShown as before.
   <li> TODO: the cache listens for document closes, and wipes
     out cache entries
   <li> PENDING: the cache is a weak listener so does not
     prevent garbage collection.
   <li> PENDING: Consider whether or not the cache should have
     a max size.
</ul>
<p>
Here's how the SuggestionManager uses the cache:
<ul>
  <li> When suggestions are modified for a document,
        the cache is cleared for that document.
  <li> When the suggestions window is hidden, the cache
        is flushed
  <li> When a file is switched:
    <ul>
     <li> the current contents are added to the cache. Some
        optimization should be added here such that if you don't edit the
        file that's open, no work is done. The items are then
        removed from the list.
     <li> the cache is consulted for the list in the existing
        file, and only if empty (or is scanOnShow is true) does a 
        new scan take place.
    </ul>
   <li> When documents are closed, the cache should be notified.
        This is done because the cache cannot easily listen for
        document close events, and the suggestion manager already
        has a way of detecting these.
</ul>
<p>
  @todo What about suggestions derived from Documents which have
     external dependencies? For example, after changing document
     "foo", document Bar may get new scan errors in it - but
     we wouldn't notice. Perhaps DocumentSuggestionProviders can
     provide a hint about whether or not they expect their
     contents to be 100% derivable from a single document.
<p>
 * @author Tor Norbye
 */

class SuggestionCache implements DocumentListener, PropertyChangeListener {

    private HashMap map = null;

    /** Add the given document and its associated suggestions
        to the cache. It will automatically be removed when
      <ul>
       <li> The document is edited
       <li> The document is closed
       <li> The cache is full
       <li> remove() is called
       <li> flush() is called
      </ul>
     <p>
        It's okay to call add repeatedly to update - you don't
        have to call remove() between add() calls.
     <p>
        @param doc The document to monitor in the cache
        @param suggestions The list of suggestions to cache.
            NOTE: The list should not be null. It can be empty.
            Null as returned from @{link lookup} means "entry
            not in cache.
    */
    public void add(Document doc, DataObject dobj, List suggestions) {
        if (map == null) {
            map = new HashMap(60); // PENDING What size to use here?
        }

        doc.removeDocumentListener(this); // prevent double registering
        doc.addDocumentListener(this);

        EditorCookie.Observable observable = 
            (EditorCookie.Observable)dobj.getCookie(
                                         EditorCookie.Observable.class);
        if (observable != null) {
            observable.removePropertyChangeListener(this); // prevent dupes
            observable.addPropertyChangeListener(this);
        }
        Entry entry = new Entry(doc, suggestions, observable);
        map.put(doc, entry);
    }

    /** Look up the given document's suggestions list.
     * @param doc The document to look up suggestions for
     * @return The list of suggestions associated with this
     *   document. Will return null if the document has
     *   not been cached, or if it has been flushed from the
     *   cache.
     */
    public List lookup(Document doc) {
        if (map == null) {
            return null;
        }
        Entry entry = (Entry)map.get(doc);
        if (entry == null) {
            return null;
        }
        return entry.list;
    }

    /** Remove all items from the cache */
    public void flush() {
        if (map == null) {
            return;
        }
        map.clear();
    }

    /** Remove a particular document's stashed list in the
        cache. 
        @param doc The document we want removed from the
          cache
    */
    public void remove(Document doc) {
        Entry entry = (Entry)map.remove(doc);
        if (entry != null) {
            // Was in the table - gotta remove doc listeners
            doc.removeDocumentListener(this);
            if (entry.listener != null) {
                entry.listener.removePropertyChangeListener(this);
            }
        }
    }

    /** Reacts to changes */
    public void propertyChange(PropertyChangeEvent ev) {
        String prop = ev.getPropertyName();
        if (prop.equals(EditorCookie.Observable.PROP_DOCUMENT)) {
            try {		
                EditorCookie ec = (EditorCookie)ev.getSource();
                Document doc = ec.getDocument();
                invalidate(doc);
            } catch (Exception e) {
                ErrorManager.getDefault().log("ev.getSource().getClass() = " + ev.getSource().getClass().getName());
		ErrorManager.getDefault().
		     notify(ErrorManager.INFORMATIONAL, e);
            }
        }
    }    

    /** The given document should be removed from the map */
    private void invalidate(Document doc) {
        remove(doc);
    }

    // Implements DocumentListener
    public void changedUpdate(DocumentEvent e) {
        // Don't care - attribute changes only
    }

    public void insertUpdate(DocumentEvent e) {
        invalidate(e.getDocument());
    }

    public void removeUpdate(DocumentEvent e) {
        invalidate(e.getDocument());
    }

    private static class Entry {
	List list;
        Document doc;
        EditorCookie.Observable listener;

        Entry(Document doc, List list, EditorCookie.Observable listener) {
            this.list = list;
            this.doc = doc;
            this.listener = listener;
        }
    }
}

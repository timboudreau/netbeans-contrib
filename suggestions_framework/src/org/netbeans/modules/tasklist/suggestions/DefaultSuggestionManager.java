/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Passive suggestion manager implementation:
 * <ul>
 * <li>creates Suggestion instances
 * <li>always enabled and observed (does not know its views)
 * <li>registers suggestions to provided list only
 *     (default register does nothing) supporting single
 *     suggestion type filters.
 * </ul>
 *
 * <p>
 * See {@link SuggestionManagerImpl} for active implementation
 * that handles events from environment to identify currently
 * opened file and shows suggestions related to it.
 *
 * @author Petr Kuzel
 */
public class DefaultSuggestionManager extends SuggestionManager {

    // See super for accurate javadoc
    public Suggestion createSuggestion(String type,
                                       String summary,
                                       SuggestionPerformer action,
                                       Object data) {
        // "Sanitize" the summary: replace newlines with ':'
        // " " or ":" (let's pick one).
        // (Oh crap. What do we do about CRLF's? Replace with ": " ?
        // This won't work right for \r-only systems, but surely OSX didn't
        // keep that bad MacOS habit, did it?
        if (summary.indexOf('\n') != -1) {
            int n = summary.length();
            StringBuffer sb = new StringBuffer(2 * n); // worst case
            for (int i = 0; i < n; i++) {
                char c = summary.charAt(i);
                if (c == '\n') {
                    sb.append(':');
                    sb.append(' ');
                } else if (c != '\r') {
                    sb.append(c);
                }
            }
            summary = sb.toString();
        }

        SuggestionType st = SuggestionTypes.getTypes().getType(type);
        if (st == null) {
            throw new IllegalArgumentException("type " + st +
                    " is not registered");
        }
        SuggestionImpl s = new SuggestionImpl(summary, st,
                action, data);
        return s;
    }

    public boolean isEnabled(String id) {
        return true;
    }

    public boolean isObserved(String id) {
        return true;
    }

    /**
     * Must be over written, does nothing.
     */
    public void register(String type, List add, List remove,
                         Object request) {
        assert false : "This public contract is not implemented use private one!";
    }


    /**
     * Update target tasklist including grouping (if over treshold).
     *
     * @param typeName suggestion type
     * @param addList  suggestions to add
     * @param removeList suggestion sto remove or null
     * @param tasklist target tasklist
     * @param sizeKnown is this registration final (otherwise
     *        another one is planned by registrant)
     */
    public void register(String typeName,
                                      List addList, List removeList,
                                      SuggestionList tasklist,
                                      boolean sizeKnown) {
        //System.err.println("register(" + typeName + ", " + addList +
        //                   ", " + removeList + "," + tasklist + ", " +
        //                   request + ", " + sizeKnown + ")");


        // TODO check instanceof Task here, and throw an exception if not?

        // Get the first element, and use its type as the type for all.
        // This works because all elements in the list must have the same
        // (meta?) type.
        SuggestionType type = null;
        if (typeName != null) {
            type = SuggestionTypes.getTypes().getType(typeName);
            if (type == null) {
                throw new IllegalArgumentException("No such SuggestionType: " + typeName);
            }
        }

        /* Not yet necessary - I'm always stuffing the cache on docHidden()
        // Clear SuggestionCache entry, if necessary
        Suggestion first = null;
        SuggestionProvider provider = null;
        if ((addList != null) && (addList.size() > 0)) {
            first = (Suggestion)addList.get(0);
        } else if ((removeList != null) && (removeList.size() > 0)) {
            first = (Suggestion)removeList.get(0);
        }
        if ((cache != null) && (first != null)) {
            provider = first.getProvider();
            if ((provider != null) &&
                (provider instanceof DocumentSuggestionProvider)) {
                Line l = first.getLine();
                if (l != null) {
                    Document doc = TLUtils.getDocument(l);
                    if (doc != null) {
                        cache.remove(doc);
                    }
                }
            }
        }
        */


        // Must iterate over the list repeatedly, if it contains
        // multiple types
        boolean split = (type == null);
        ListIterator ita = null;
        ListIterator itr = null;
        if (split) {
            List allAdds = addList;
            List allRems = removeList;
            if (allAdds != null) {
                ita = allAdds.listIterator();
                addList = new ArrayList(allAdds.size());
            }
            if (allRems != null) {
                itr = allRems.listIterator();
                removeList = new ArrayList(allRems.size());
            }
        }
        while (true) {

            // Populate the list with the next homogeneous subset of the
            // same type
            if (split) {
                if ((ita != null) && (ita.hasNext())) {
                    addList.clear(); // setSize(0); ?
                    type = null;
                    while (ita.hasNext()) {
                        SuggestionImpl s = (SuggestionImpl) ita.next();
                        if (type == null) {
                            type = s.getSType();
                        } else if (s.getSType() != type) {
                            ita.previous(); // undo advance
                            break;
                        }
                        addList.add(s);
                    }
                } else {
                    addList = null;
                }

                if ((itr != null) && (itr.hasNext())) {
                    removeList.clear();
                    type = null;
                    while (itr.hasNext()) {
                        SuggestionImpl s = (SuggestionImpl) itr.next();
                        if (type == null) {
                            type = s.getSType();
                        } else if (s.getSType() != type) {
                            itr.previous(); // undo advance
                            break;
                        }
                        removeList.add(s);
                    }
                } else {
                    removeList = null;
                }

                if ((addList == null) && (removeList == null)) {
                    break;
                }
            }

            SuggestionImpl category = tasklist.getCategoryTask(type, false);


            // XXX [PERFORMANCE] Later I can compute the type more quickly
            // than this - instead of counting each time, keep a count,
            // stored in a hashmap (I already have a type registry. Just watch
            // out and remember that because of the Directory Scanning action,
            // you can have multiple clients of the type registry.
            int currnum = 0;
            if (category != null) {
                currnum = category.getSubtasks().size();
            } else {
                if (tasklist.getTasks() != null) {
                    Iterator it = tasklist.getTasks().iterator();
                    while (it.hasNext()) {
                        SuggestionImpl s = (SuggestionImpl) it.next();
                        if (s.getSType() == type) {
                            currnum++;
                        }
                    }
                }
            }
            int addnum = (addList != null) ? addList.size() : 0;
            int remnum = (removeList != null) ? removeList.size() : 0;
            // Assume no stupidity like overlaps in tasks between the lists
            int newSize = currnum + addnum - remnum;
            if ((newSize > tasklist.getGroupTreshold()) && (getUnfilteredType() == null)) {
                // TODO - show the first MAX_INLINE-1 "inlined", followed by the
                // category node? Or hide all below the category node? For now,
                // doing the latter since it's a lot easier.

                if (category == null) {
                    // Now should have subtasks, but previously we didn't;
                    // remove the tasks from the top list
                    category = tasklist.getCategoryTask(type, true);
                    synchronized (this) {
                        List leftover = null;
                        if (removeList != null) {
                            tasklist.addRemove(null, removeList, true, null, null);
                        }
                        if (currnum - remnum > 0) {
                            leftover = new ArrayList(currnum);
                            Iterator it = tasklist.getTasks().iterator();
                            while (it.hasNext()) {
                                SuggestionImpl s = (SuggestionImpl) it.next();
                                if ((s.getSType() == type) &&
                                        (s != category)) {
                                    leftover.add(s);
                                }
                            }
                        }
                        if ((leftover != null) && (leftover.size() > 0)) {
                            tasklist.addRemove(null, leftover, false, null, null);
                            tasklist.addRemove(leftover, null, true, category, null);
                        }
                        tasklist.addRemove(addList, null, true, category, null);
                    }
                } else {
                    // Updating tasks within the category node
                    tasklist.addRemove(addList, removeList, false, category, null);
                }

                // Leave category task around? Or simply make it invisible?
                // (Need new Task attribute and appropriate handling in filter
                // and export methods.)    By leaving it around, we don't reorder
                // the tasks on the user.
                //tasklist.removeCategory((SuggestionImpl)suggestions.get(0).getParent(), false);
                updateCategoryCount(category, sizeKnown); // TODO: skip this when filtered
            } else {
                SuggestionImpl after = tasklist.findAfter(type);
                if (category == null) {
                    // Didn't have category nodes before and don't need to
                    // now either...
                    boolean append = (after != null);
                    tasklist.addRemove(addList, removeList, append, null, after);
                } else {
                    // Had category nodes before but don't need them anymore...
                    // remove the tasks from the top list
                    synchronized (this) {
                        if (removeList != null) {
                            tasklist.addRemove(null, removeList, false, category,
                                    null);
                        }
                        List leftover = category.getSubtasks();
                        if (addList != null) {
                            tasklist.addRemove(addList, null, true, null, after);
                        }
                        if ((leftover != null) && (leftover.size() > 0)) {
                            tasklist.addRemove(leftover, null, true, null, after);
                        }
                    }
                    tasklist.removeCategory(category, true);
                }
            }
            if (!split) {
                break;
            }

        }
    }

    private static void updateCategoryCount(SuggestionImpl category, boolean sizeKnown) {
        SuggestionType type = category.getSType();
        int count = category.hasSubtasks() ?
                category.getSubtasks().size() : 0;
        String summary;
        if ((count != 0) || sizeKnown) {
            summary = type.getLocalizedName() + " (" + // NOI18N
                    Integer.toString(count) + ")"; // NOI18N
        } else {
            summary = type.getLocalizedName();
        }
        category.setSummary(summary);
    }



    // XXX premature optimatization, kick it away or
    // invent view-providers filter events
    // (e.g. existing global isObserved(), isEnabled() )

    /**
     * When non null, a filter is in effect and only the unfilteredType
     * is showing.
     * <p>
     * Note that such unmatching suggestions can come from
     * "side effect" type of suggestion providers at anytime.
     */
    private SuggestionType unfilteredType = null;

    protected final SuggestionType getUnfilteredType() {
        return unfilteredType;
    }

    protected final void setUnfilteredType(SuggestionType unfilteredType) {
        this.unfilteredType = unfilteredType;
    }

}

package test.action;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ContiguousSelectEvent;
import org.netbeans.api.visual.action.ContiguousSelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.general.ListItemWidget;
import org.netbeans.api.visual.widget.general.ListWidget;
import test.SceneSupport;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author David Kaspar
 */
public class ContiguousSelectActionTest extends ObjectScene {

    public ContiguousSelectActionTest () {
        ListWidget list = new ListWidget (this);
        list.setLabel ("List of selectable items");
        addChild (list);

        WidgetAction contiguousSelectAction = ActionFactory.createContiguousSelectAction (new MyContiguousSelectProvider ());

        for (int a = 0; a < 10; a ++) {
            String item = "Item no. " + a;
            ListItemWidget itemWidget = new ListItemWidget (this);
            itemWidget.setLabel (item);
            itemWidget.getActions ().addAction (createObjectHoverAction ());
            itemWidget.getActions ().addAction (contiguousSelectAction);
            list.addChild (itemWidget);
            addObject (a, itemWidget);
        }
    }

    public static void main (String[] args) {
        SceneSupport.show (new ContiguousSelectActionTest ());
    }

    private class MyContiguousSelectProvider implements ContiguousSelectProvider {

        public boolean isSelectionAllowed (ContiguousSelectEvent event) {
            return true;
        }

        public void select (ContiguousSelectEvent event) {
            Object previous = findObject (event.getPreviouslyChoosenWidget ());
            Object last = findObject (event.getChoosenWidget ());
            switch (event.getSelectionType ()) {
                case ADDITIVE_CONTIGUOUS:
                    if (last != null) {
                        HashSet<Object> set = collectContiguousObjects ((Integer)  (previous != null ? previous : last), (Integer) last);
                        set.addAll (getSelectedObjects ());
                        userSelectionSuggested (set, false);
                    }
                    break;
                case ADDITIVE_NON_CONTIGUOUS:
                    if (last != null) {
                        HashSet<Object> set = new HashSet<Object> (getSelectedObjects ());
                        if (set.contains (last)) {
                            set.remove (last);
                        } else {
                            set.add (last);
                        }
                        userSelectionSuggested (set, false);
                    }
                    break;
                case REPLACE_CONTIGUOUS:
                    HashSet<Object> set = collectContiguousObjects ((Integer)  (previous != null ? previous : last), (Integer) last);
                    userSelectionSuggested (set, false);
                    break;
                case REPLACE_NON_CONTIGUOUS:
                    userSelectionSuggested (Collections.singleton (last), false);
                    break;
            }
        }

        private HashSet<Object> collectContiguousObjects (int first, int last) {
            int max = Math.max (first, last);
            HashSet<Object> set = new HashSet<Object> ();
            for (int i = Math.min (first, last); i <= max; i ++) {
                set.add (i);
            }
            return set;
        }

    }

}

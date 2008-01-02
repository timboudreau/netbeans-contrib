/*
 * MyTraversalPolicy.java
 *
 *
 * @author Michal Hapala, Pavel Stehlik
 */
package org.netbeans.modules.a11ychecker.traverse;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MyTraversalPolicy extends FocusTraversalPolicy implements Serializable {

    public MyTraversalPolicy() {
    }
    
    String start;
    String end;
    private Vector<MySavingButton> savedButtons;

    public Vector<MySavingButton> getSavedBtns() {
        return savedButtons;
    }
    
    public List<String> checkTabTraversalState()
    {
        //System.out.println("checking...");
        List<String> compsWithoutNextComp = new ArrayList<String>();
        for (MySavingButton mySavingButton : savedButtons) {
            //System.out.println("name: " + (mySavingButton.getName() != null ? mySavingButton.getName() : "null") + ", nextname: " + (mySavingButton.getNextName() != null ? mySavingButton.getNextName() : "null"));
            if(mySavingButton.getNextName() == null) {
                compsWithoutNextComp.add(mySavingButton.getName());
            }
        }
        return compsWithoutNextComp;
    }

    public void setSavedBtns(Vector<MySavingButton> savedButtons) {
        this.savedButtons = savedButtons;
    }

    public Component getComponentAfter(Container aContainer, Component aComponent) {
        return null;
    }

    public Component getComponentBefore(Container aContainer, Component aComponent) {
        return null;
    }

    public Component getDefaultComponent(Container aContainer) {
        return null;
    }

    public Component getFirstComponent(Container aContainer) {
        return null;
    }

    public Component getLastComponent(Container aContainer) {
        return null;
    }

    @Override
    public String toString() {
        return "customFocusTraversalPolicy";
    }
}

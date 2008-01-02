/*
 * MySavingButton.java
 *
 *
 * @author Michal Hapala, Pavel Stehlik
 */

package org.netbeans.modules.a11ychecker.traverse;

import java.io.Serializable;

public class MySavingButton implements Serializable {
    
    private String name;
    private String nextName;
        
    public MySavingButton() {
    }

    public String getName() {
        return name;
    }

    public String getNextName() {
        return nextName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNextName(String nextName) {
        this.nextName = nextName;
    }
    
}

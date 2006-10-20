/*
 * ParsingService.java
 *
 * Created on October 17, 2006, 4:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.api.docbook;

/**
 *
 * @author Tim Boudreau
 */
public abstract class ParsingService {

    public abstract ParseJob enqueue (Callback callback);
    public abstract ParseJob register (Callback callback);
    public abstract void unregister (Callback callback);

}

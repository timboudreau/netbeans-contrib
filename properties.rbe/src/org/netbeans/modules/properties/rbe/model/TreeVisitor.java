/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

/**
 *
 * @author Denis Stepanov
 */
public interface TreeVisitor<T extends TreeItem<?>> {

    /**
     * Pre tree visit
     * @param tree
     */
    void preVisit(T tree);

    /**
     * Post tree visit
     * @param tree
     */
    void postVisit(T tree);

    boolean isDone();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

/**
 *
 * @author denis
 */
public interface TreeVisitor<T extends TreeItem<?>> {

    void preVisit(T tree);

    void postVisit(T tree);

    boolean isDone();
}

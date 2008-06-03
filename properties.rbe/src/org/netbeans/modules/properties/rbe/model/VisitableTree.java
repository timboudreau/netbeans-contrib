/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

/**
 *
 * @author denis
 */
public interface VisitableTree<T extends TreeItem<?>> {

    void accept(TreeVisitor<T> visitor);
}

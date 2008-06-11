/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.lookup.annotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service is an implementation of a provided {@linkplain Contract}
 * @author Jaroslav Bachorik
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Service {    
}

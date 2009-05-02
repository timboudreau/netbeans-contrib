/*
 * Sorter.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.erlang.editor.util

import scala.collection.mutable.ArrayBuffer

object Sorter {
    
   def sort[T](ab:ArrayBuffer[T])(compareFun:(T, T) => Boolean) : Unit = {
      val sorted = ab.toList.sort{compareFun}
      var i = 0
      for (e <- sorted) {
         ab(i) = e
         i += 1
      }
   }
}

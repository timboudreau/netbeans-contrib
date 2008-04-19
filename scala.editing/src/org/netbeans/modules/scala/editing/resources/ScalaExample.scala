/** 
 * This is a Scala comment
 *
 * @author Caoyuan Deng
 */

package example

class ClassExample {
    var number :Int = 123;
    val str = "String";
    
    def getElement(element) = {
        var targetLeft = 0;
        
        while (element) {
            if (element.offsetParent) {
                targetLeft += element.offsetLeft;
            } else if (element.x) {
                targetLeft += element.x;
            }
            element = element.offsetParent;
        }
        
        return this.number + '_' + str; // line comment
    }
} 




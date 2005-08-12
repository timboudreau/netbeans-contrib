Java Source Code Formatter Beautifier Pretty Printer
Plugin Module for NetBeans 4.1

This is a port of the Java Source Code Formatter Beautifier Pretty 
Printer plugin module for NetBeans 4.1

http://jalopy.sourceforge.net/
http://jalopy.sourceforge.net/plugin-netbeans.html

It is mainly based on the original code of Marco Hunsicker. Beside fixing
compilation problems under NetBeans 4.1 major changes include

- updated to bundled Jalopy version 1.5b1 developer snapshot Aug 12 2005
- arranged the folder structure and file layout to form a NetBeans 4.1 
  conformous module project
- some minor improvements

I plan to take over the development from Marco Hunsicker concerning the
NetBeans module integration, but _not_ the development of Jalopy itself.

Jalopy is widely customizable and so a valuable replacement of the 
builtin formatting capabilities of NetBeans. But there are some known 
limitations of using the plugin: it does not work for Java 5 code esp. 
not when using generics.

There is also a commercial successor of the Jalopy project which can be 
found at http://www.triemax.com/. It has added support for all new 
J2SE 5.0 language features and includes a plugin module for NetBeans 4, 
too.


Frank-Michael Moser
moser@netbeans.org


Steps to execute automated COCO testcases.

1) Open Netbeans (5 and above) IDE
2) Add the COCOTests project
3) Install modules SJSWS7.0 server plugin, Jellytools, Jemmy Module, JemmySupport and NB JUnit (you can download them from the NetBeans Update Center (Beta)- main menu Tools|Update Center - under category Testing Tools)
4) Place the nb.properties in C:\ (This is the only hardcoded value. Place this in local directory to avoid this problem)
5) The test that are to be run has to be mentioned in the tc.lst file. Each test case name is a method implemented in the main file.
6) Now compile the com.sun.ws7.coco.ServerPluginsTest.java and run internally(only when JemmySupport module is added in step 3, this option is provided) 6) Check the log file (The location is picked up from the nb.properties file) for the results the snapshot of the verification process.

FAQs

Q) What is a nb.properties file?
A)    This is the place where all the test related properties are stored. Like the WS installation directory, the results directory, log file, admin port etc,.

Q) Where to place nb.properties file?
A)    The nb.properties file can be placed anywhere on the local file system where Netbeans IDE is running and that has to be mentioned in the main method of ServerPluginsTest.java . If this file is placed in the local directory of the Netbeans project it becomes more portable.

Q) How to run a set of selective testcases.
A)    Though the tc.lst file has a list of test cases, to run only selective ones, comment (using #) the other testcases which you don't want to run in tc.lst.

Q) How should the tests be named?
A)    Each testcase is a method implemented in the java file. So the testcase names has to follow the method naming conventions in java.

Q) Does tc.lst support regular expressions
A)    No, currently regex is not support

Q) Is there a way to filterout tests based on the OS.
A)    No, this support is not currently supported and even in future this support is not needed.

Q) How to add new properties in nb.properties file?
A)    Add a new name-value pair to the nb.properties file. Have corresponding getter and setter methods in PropBean class and use it in your tests appropriately.

Q) Is it possible to run the tests on remote web servers.
A)    Yes, it is possible with a remote webserver scenario. The is.remote property in nb.properties file has to be set to true along the remote hostname, port, vs-name all having their own appropriate values.

Q) What do I need to know to add new test cases.
A)    Core Java, JUnit, Jemmy and Jelly. A little knowledge about AWT/Swing will help.  

Last Updated: 3 Sep, 07
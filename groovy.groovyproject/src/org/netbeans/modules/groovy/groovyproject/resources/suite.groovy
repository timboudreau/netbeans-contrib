import java.io.File
import junit.textui.TestRunner;
import groovy.util.GroovyTestSuite

def findAllTests(dir, result) {
    filesAndDirs = dir.list()
    for (fileName in filesAndDirs) {
        if (fileName.equals("suite.groovy")) continue
        f = new File(dir, fileName)
        if (f.isFile()) {
            // the system property is "API" of GroovyTestSuite, hmmmm
            System.setProperty("test", f.getAbsolutePath())
            t = GroovyTestSuite.suite()
            tests = t.tests()
            for (i in tests) {
                result.addTest(i)
            }
        }
        if (f.isDirectory()) {
            findAllTests(f, result)
        }
    }
}

res = new GroovyTestSuite()
findAllTests(new File("src"), res)

TestRunner.run(res)

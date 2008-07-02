package org.netbeans.modules.scala.editing.scalasig

import _root_.scala.testing.SUnit._

object MainTest extends Assert {

  class MainTestCase(n:String) extends TestCase(n) {

    import _root_.java.io.{ByteArrayOutputStream, ByteArrayInputStream, File, FileOutputStream}

    var scala_home: String = null
    var bos: ByteArrayOutputStream = null

    override def setUp = {
      scala_home = System.getProperty("scala.home")
      if(scala_home == null)
      	throw new NullPointerException("scala.home property not set.")
      bos = new ByteArrayOutputStream(0x4000)
      Console.setOut(bos)
    }
    override def runTest: Unit = {
      import _root_.scala.tools.nsc.util.ClassPath
      val cp0 = new ClassPath(false)
      n match {
	case "jar" =>
	  //System.err.println("testing scalap on scala-library.jar")
	  // set classpath to scala-library.jar
	  val path = new cp0.Build(scala_home+"/lib/scala-library.jar") 
	  Main.process(new Arguments, path)("scala.List")
	  val s = bos.toString
	tearDown
	  assertTrue("[jar] method head() of scala.List",-1 != s.indexOf("def head(): scala.Any;"))

	case "file" =>
	  //System.err.println("testing scalap on hello.class")
	  // set classpath to scala-library.jar
	  val path = new cp0.Build(scala_home+"/lib/scala-library.jar") 
	val is = new ByteArrayInputStream(helloWorldUU.getBytes);
        val file = new File("hello.class")
        val os = new FileOutputStream(file);
	val dc = new sun.misc.UUDecoder
	try {
	  dc.decodeBuffer(is,os)
	} catch {
	  case _ => // complains about "short buffer", ignore
	}
	os.close
	val curdir = new cp0.Build(".") 
	Main.process(new Arguments, curdir)("hello")
	val s = bos.toString
	tearDown
	assertTrue("[file] method doit() of hello", -1 != s.indexOf("def doit(): scala.Unit"))
	file.delete
      }
    }
    
    override def tearDown = {
      Console.setOut(System.out)
      bos = null
    }
      
    override def run(r: TestResult): Unit =
      try {
	setUp
        runTest
      } catch {
        case t:Throwable => r.addFailure(this, t)
      }

/* //this is the uuencoded class file of the following source
object hello {
  def doit = Console println "hello"
}
*/
	  val helloWorldUU = """
begin 644 hello
MROZZO@`#`"T`%P$`"E-O=7)C949I;&4!``MH96QL;RYS8V%L80$`!"1T86<!
M``,H*4D!``1#;V1E`0`&:&5L;&\D!P`&`0`'34]$54Q%)`$`"$QH96QL;R0[
M#``(``D)``<`"@P``P`$"@`'``P!``1D;VET`0`#*"E6#``.``\*``<`$`$`
M"%-C86QA4VEG`0`%:&5L;&\'`!,!`!!J879A+VQA;F<O3V)J96-T!P`5`#$`
M%``6```````"`!D``P`$``$`!0```!,``0``````![(`"[8`#:P``````!D`
M#@`/``$`!0```!,``0``````![(`"[8`$;$```````(``0````(``@`2````
MM00`(4<&``$"B`(%`05H96QL;PH!`P$'/&5M<'1Y/@,`$`(&!PT!`D8&``@"
MB`()`@5H96QL;Q,#!PH2$`(+$`T!#`H"#0X!!&QA;F<*`0\!!&IA=F$)`A$,
M`@9/8FIE8W00`A,6#0$4"@$5`05S8V%L80D"%Q0""U-C86QA3V)J96-T2`8-
M&0>$`!H!!CQI;FET/A0!!4@&$1P'A``=`01D;VET%0$>$`(3'PD"(!0"!%5N
":70`
`
end
"""
  }

  def main(args:Array[String]) = {
    val r = new TestResult
    new TestSuite(new MainTestCase("jar"), new MainTestCase("file")) run r
    //System.err.println("tests complete")
    r.failures foreach System.err.println
  }
}

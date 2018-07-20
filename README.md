NetBeans Contrib - Mavenized
----------------------------

This is the former netbeans.org contrib repository, converted from NetBeans' Ant build harness to Maven.

Contrib was started in 2001, and grew to contain a mix of things:

 * Community contributed plugins (ex: the Jalopy code formatter plugin)
 * Side projects of NetBeans developers - small useful modules, debugging tools only useful when developing NetBeans (ex: quickfilechooser, a command-line friendly replacement for the standard Swing filechooser)
 * A graveyard for
   * Obsoleted modules and technologies (JNDI instead of Lookup, MOF metadata repository for code completion)
   * Modules which were retired from active development but might still work (Portlets)


What Was Converted and How
------------------------

At present, 266 out of 374 projects are buildable (w/o tests).

Some years ago I wrote some scrappy tools to convert a large Ant codebase to Maven, which I still use to refactor Maven projects en masse - it was pretty straightforward to read a module manifest, a project.xml and a project.properties and generate a Maven `pom.xml` that, for straightforward projects, would "just work".

In the process, we've probably raised a few modules from the dead - now you, too can build Karel Gardas' CORBA module from 1999! (well, actually having that not fail would take a bit of work)

 * Code to standard Maven layout
 * Generated `pom.xml` files from manifest, project.properties and project.xml from original project
 * Where detectable, upgraded dependencies (e.g. openide.util -> openide.util.lookup if spec version was old enough)
 * Unit tests should work (sources were scanned for references to `junit.framework` and JUnit version set accordingly in its dependencies)
 * QA-Functional tests moved to src/qa-functional - no attempt to get these working at this point
 * Modules which exposed "friend" packages now publish them as public packages - AFAICT, `nbm-maven-plugin` has no way to specify friend dependencies at all.  Perhaps could have written this into the manifest, but that is hard to maintain
 * For missing libaries, when available from a public repository, simply added dependencies on them; projects whose dependencies couldn't be satisfied are commented out in the master pom
 * Most modules are built against `RELEASE82` binaries (dependencies on, e.g. `org-openide-util-ui` and `org-netbeans-api-progress-nb` and similar were added by the conversion tool where needed).
 * For a few modules that simply could not be built any other way, set either `netbeans.version` or `java.source.version` to some older value (ex: JNDI relies on the `openide.util.enum` package, so it is only compilable with source level 1.4 - which incidentally, rules out building it at all with JDK 9).  A very few modules require a franken-netbeans combination of versions to be built, where the newer module is binary- but not source-compatible.
 * Where a trivial and obvious source code change would make the module compilable (ex: Checkstyle's Checker now takes a List<File> rather than a File[]), the change was made.  This was only 2-3 cases.


What Works
---------

The majority of modules - over 300 of them - build successfully.  Whether they work as advertised or not remains to be seen, but other than cases where the build script did unusual things, they should.

For those that could not be built, they are commented out in the [master POM](pom.xml) with a description of what's wrong as best I was able to determine it.


Things That Would Be Nice To Get Working
-------------------------------------

 * A number of modules are simply no longer friend modules of one or another dependency - this may be because those dependencies have been incompatibly changed, or it may just be missing (e.g. `dew4nb` needs to be a friend of `java-source-base` but is not)

 * Several language support modules (fortran, fortress, python) rely on running Antlr to build a parser.  The Antlr Maven plugin should do just fine to revive these (most appear to have used Antlr 2.x); doing so was just out of scope for getting the maximum number of things usable quickly.  Should be easy to fix.

 * The Solaris native tools team did a number of projects that probably need small tweaks to get working - a dependency here or there.  Dtrace support in particular might be interesting to get working again.

 * `vcscore` and friends - support for obscure version control systems via a command-line interface - missing a friend dependency from `masterfs` and probably some code updates afterwards, but might be doable.


Tests
----

Getting tests working was a non-goal at this point.  Some projects tests will pass; many may have had non-passing or non-compiling tests before migration.  In particular, the POM generator did scan sources for references to `junit.framework` to decide whether to use JUnit `4.12` or `3.8.2` in the resulting build file, but uses of `nbjunit` could obscure which was actually needed.  Most modules will need some tweaks to their dependencies to get tests compiling.

Frequently the tests have been unmaintained for far longer than the original code - Ant didn't run them by default on every build.  For example, Zeroadmin's tests had imports for (but fortunately did not use) packages which have not existed in over a decade.


Things That Should Probably Be Deleted
-----------------------------------

 * Rectangular edit tools - the base IDE does this today
 * nodejs - I moved this project to github years ago, so I could use Jackson instead of maintaining my own JSON parser
 * portalpack.* - Portlets - dead technology
 * corba - An even deader technology
 * RMI* - Another circa 1999 set of modules, dependent on long gone openide.src API
 * localhistory - The base IDE does this today
 * semicolon - The base IDE does this today
 * remoteproject* - An experiment from early in the project API, not useful
 * linkwitheditor - The base IDE does this today
 * themebuilder - An experiment, defunct
 * core.naming - pre Lookup, this was the alternative considered - using JNDI internally - a road thankfully not taken

Anything relying on `openide.src` - the NetBeans 3.x source model classes is probably more work than it is worth to revive and will never be used again.




NetBeans Contrib - Mavenized
----------------------------

This is the former netbeans.org contrib repository, converted from NetBeans' Ant build harness to Maven.

 * Read the [catalogue of what's here](catalogue.md) generated from module metadata (updated when the master POM is built), broken out
into working/not-working and modules/libraries categories
 * See the [commented out modules in the master `pom.xml`](blob/master/pom.xml) - for things that are here but not buildable - most
have detailed notes on what's wrong and whether or not it's likely to be fixable

History
-------

Contrib was started in 2001, and grew to contain a mix of things:

 * Community contributed plugins (ex: the Jalopy code formatter plugin)
 * Side projects of NetBeans developers - small useful modules, debugging tools only useful when developing NetBeans (ex: quickfilechooser, a command-line friendly replacement for the standard Swing filechooser)
 * A graveyard for
   * Obsoleted modules and technologies (JNDI instead of Lookup, NetBeans 5's MOF metadata repository for code completion / language support)
   * Modules which were retired from active development but might still work (Portlets)


What Was Converted and How
--------------------------

At present, 314 out of 374 projects are buildable (w/o tests).

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
 * Where a module bundled a library, and the exact version could not be found, added the newest version from the Maven repositories used that would allow it to compile
 * Where a trivial and obvious source code change would make the module compilable (ex: Checkstyle's Checker now takes a List<File> rather than a File[]), the change was made.  This was only 2-3 cases.
 * Modules that use ANTLR were set up with Maven support for building that (in the case of Fortran, the grammar was split out into a new module)
 * Other build hand-tweaking where necessary


What Works
---------

The majority of modules - over 300 of them - build successfully.  Whether they work as advertised or not remains to be seen, but 
other than cases where the build script did unusual things, they should.

For those that could not be built, they are commented out in the [master POM](blob/master/pom.xml) with a description of what's wrong 
as best I was able to determine it.


Things That Would Be Nice To Get Working
-------------------------------------

 * A number of modules need trivial changes to become compatible with modern APIs - the most common culprit is the removal of
DrawLayer from the editor API, replaced by the [highlighting API](http://bits.netbeans.org/7.4/javadoc/org-netbeans-modules-editor-lib2/org/netbeans/spi/editor/highlighting/package-summary.html)

 * The Solaris native tools team did a number of projects that probably need small tweaks to get working - a dependency here or there.  Dtrace support in particular might be interesting to get working again.

 * `vcscore` and friends - support for obscure version control systems via a command-line interface - the API for filesystems have changed somewhat - I did get `vcscore` close to buildable.  Whether anyone wants this is an open question - these were a maintenance nightmare.


Tests
----

Getting tests working was a non-goal at this point.  Some projects tests will pass; many may have had non-passing or non-compiling 
tests before migration.  In particular, the POM generator did scan sources for references to `junit.framework` to decide whether 
to use JUnit `4.12` or `3.8.2` in the resulting build file, but uses of `nbjunit` could obscure which was actually needed.  Most 
modules will need some tweaks to their dependencies to get tests compiling, much less passing.

Frequently the tests have been unmaintained for far longer than the original code - Ant didn't run them by default on every build.  
For example, Zeroadmin's tests had imports for (but fortunately did not use) packages which have not existed in over a decade.

The Maven build uses a profile which sets `maven.test.skip` to `true` unless a file named `tests-should-pass` exists in the root
of that project.  So if you want tests run, simply add an empty file with that name to the project.


Dependencies
------------

The POM files were generated with the additional `&lt;moduleDependencies&gt;` section in the `nbm-maven-plugin` clause, which allows
exact specification versions - so the resulting modules should run against older versions of NetBeans if their dependencies can
be satisfied.  Where that information was not available or usable, the conversion tool used the value from the manifest of the
`RELEASE82` version.


Things That Should Probably Be Deleted
-----------------------------------

 * Rectangular edit tools - the base IDE does this today
 * nodejs - [moved to github](https://github.com/timboudreau/nb-nodejs) years ago; this is an obsolete version of it
 * portalpack.* - Portlets - builds, and perhaps works, but a dead technology
 * corba - An even deader technology
 * RMI* - Another circa 1999 set of modules, dependent on long gone openide.src API
 * localhistory - The base IDE does this today
 * semicolon - The base IDE does this today
 * remoteproject* - An experiment from early in the project API, not useful
 * linkwitheditor - The base IDE does this today
 * themebuilder - An experiment, defunct
 * core.naming - pre Lookup, this was the alternative considered - using JNDI internally - a road thankfully not taken

Anything relying on `openide.src` - the NetBeans 3.x source model classes is probably more work than it is worth to revive and will never be used again.




Callgraph
---------

Callgraph is a standalone Java utility and programmatic API for generating text files which serve as input for building a graph of what calls what in all Java sources underneath a folder.  It can output what methods call what methods on what other classes, what packages call what other packages, or what classes call other classes, or all of the above, to provide different levels of granularity.

It uses javac's API to analyze sources.

It does not need library dependencies to be set up on the compiler's working classpath, since it only needs to resolve references between visible sources, and runs javac in IDE mode so unresolvable references are not treated as fatal errors.

The folder(s) passed in need not be source roots - you can pass in the root folder of a tree of projects and it will find all Java sources below them.

The `--maven` lineswitch will cause it to scan the passed folders for Maven projects, and use their `src/main/java` folders as roots (so test classes are not included in the resulting graphs, since typically you are looking for things that are most-used, and used-by-tests doesn't count at runtime).


Usage
-----

```
Callgraph prints a graph of things that call each other in a tree of Java sources,
and can output graphs of what methods / classes / packages (or all of the above) call each other
within thatsource tree.

Usage:
java -jar callgraph.jar [--noself | -n] [--simple | -s] [--maven | -m] [--methodgraph | -g methodgraph] [--exclude | -e exclude] [--packagegraph | -p packagegraph] [--classgraph | -c classgraph] [--quiet | -q] dir1 [dir2 dir3 ...]

	--noself / -n :	Hide intra-class calls (i.e. if Foo.bar() calls Foo.baz(), don't include it)
	--simple / -s :	Use simple class names without the package (may confuse results if two classes have the same name)
	--maven / -m :	Find all maven projects that are children of the passed folders, and scan their src/main/java subfolders
	--methodgraph / -g :	Set the output file for the method call graph
	--exclude / -e :	Exclude any relationships where the fully qualified class name starts with any pattern in this comma-delimited list of strings, e.g. -e foo.bar,foo.baz
	--packagegraph / -p :	Set the output file for the package call graph
	--classgraph / -c :	Set the output file for the class call graph
	--quiet / -q :	Supress writing the graph to the standard output
Errors:
	No folders of Java sources specified
```


To-Do
-----

### Limiting Memory Consumption

On extremely large codebases (say, the entire NetBeans source base), the compile process may run out of memory.  This could be optimized by processing classes in batches (the mechanism for mapping javac `Element` objects to `SourceElement` objects would need to be changed so the trees from the java compile can be garbage collected), but this is not as simple as it sounds, since javac has to attribute the entire source tree, and references that not resolvable within a subset of the tree that is one batch would not show up.

The right way to do this would be to run the parse phase, and then partition the tree based on input statements.  The parse p


### Ensure non-overlapping folders

Ensure java files are not parsed twice if, say, `/foo/bar` and `/foo/bar/baz` are both passed as input folders.


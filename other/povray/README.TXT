POVRAY MODULES

These modules are intended for demonstrating the NetBeans APIs (to
be split into demo modules showing each stage of development).  They
also provide working basic support for using POV-Ray (http://povray.org),
a 3D ray tracer that has a "scene language" which has a edit/compile
cycle remarkably like that of a programming language.

WHAT'S HERE
 - povproject/ - this is basic support for POV-Ray projects.  Provides
   templates and a project type, and offers some service APIs for marking
   a file as the one that should be rendered when a project is built,
   rendering files and viewing the output of renders (which really
   harnesses the Image module to open PNGs).

 - povray/ - DataLoader/DataObject/Node support for .pov & .inc files,
   allowing the IDE to recognize them, and providing actions that call
   into services provided by povproject (if present)


BUILDING IT

These modules are not currently part of the build process, but need to be 
in order to link at compile time.  Apply the file patch.diff in this directory
to nbbuild.  The patch is very small and easy to apply by hand if the build
files have changed and there are conflicts.  To patch:

cd $NB_HOME
patch -p0 --dry-run < contrib/povray/patch.diff
[see if there are conflicts...if none, continue]
patch -p0 < contrib/povray/patch.diff
cd nbbuild
ant build-nozip
cd ../contrib/povray
ant -f povproject/build.xml
ant -f povray/build.xml
cd nbbuild/netbeans/bin
./netbeans.sh

Try New Project > Samples > Sample Povray Project
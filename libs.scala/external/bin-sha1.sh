#!/bin/sh

SCALA_DIST=~/myprjs/scala/scala-nb/dists/latest

cp $SCALA_DIST/lib/scala-compiler.jar scala-compiler-2.8.0.jar
cp $SCALA_DIST/lib/scala-library.jar  scala-library-2.8.0.jar
cp $SCALA_DIST/lib/scala-swing.jar    scala-swing-2.8.0.jar


for f in *.{jar,gz};
  do openssl dgst -sha1 $f | tr '[a-z]' '[A-Z]' ; done

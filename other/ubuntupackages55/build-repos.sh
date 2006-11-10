#!/bin/sh

DIST=edgy

mkdir -p $DIST
rm -f $DIST/*
cp ../netbeans*.deb $DIST/
cp ../netbeans*.changes $DIST/
dpkg-scanpackages $DIST /dev/null | gzip -c9 > $DIST/Packages.gz

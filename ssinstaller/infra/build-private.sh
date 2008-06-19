#!/bin/sh -v
################################################################################
BUILD_NUMBER=200711261600
NB_HOME=`cd ../../../..; pwd`
OUTPUT_DIR="$NB_HOME/dist_new"

ANT_OPTS="-Xmx1024m -Dhttp.proxyHost=webcache.norway.sun.com -Dhttp.proxyPort=8080 "

CACHE_DIR=$NB_HOME/ssinstaller/infra/build/cache

INSTALLED_BITS=file:///net/endif/export/home3/vesta/packages

NB_BUILDS_HOST=file:///net/endif/export/home3/vesta/packages/nb
#http://bits.nbextras.org/download/trunk/nightly/2008-04-01_02-01-24/zip/moduleclusters
#NB_BUILDS_HOST=http://smetiste.czech.sun.com/builds/netbeans/6.0/fcs/zip/moduleclusters

JDK_HOME=/usr/java
NB_FILES_PREFIX=netbeans-6.1

BUNDLES_ULR=http://endif.russia.sun.com/installer/packaged
SS_VERSION=X


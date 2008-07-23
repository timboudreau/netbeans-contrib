#!/bin/sh -v
############################################################################
# This file is temporary used by nightly.
################################################################################
BUILD_NUMBER=200711261600
NB_HOME=`pwd`/build
OUTPUT_DIR="$NB_HOME/dist"

ADDITIONAL_PATH=/set/java-sqe/tools/apache-ant-1.6.5/bin

ANT_OPTS="-Xmx1024m -Dhttp.proxyHost=webcache.norway.sun.com -Dhttp.proxyPort=8080 "

CACHE_DIR=$NB_HOME/cache



SUNSTUDIO_BITS_ROOT=/shared/dp/sstrunk/latest

#INSTALLED_BITS=$CACHE_DIR/packages
#NB_BUILDS_HOST=$CACHE_DIR/packages/nb

#http://bits.nbextras.org/download/trunk/nightly/2008-04-01_02-01-24/zip/moduleclusters
#NB_BUILDS_HOST=http://smetiste.czech.sun.com/builds/netbeans/6.0/fcs/zip/moduleclusters

JDK_HOME=/usr/java
NB_FILES_PREFIX=netbeans-6.1

BUNDLES_ULR=http://endif.russia.sun.com/installer/packaged
SS_VERSION=`ls -lA /shared/dp/sstrunk/biweekly | sed s/.*' '//`

PATH="$ADDITIONAL_PATH:$PATH"

#!/bin/sh -v
################################################################################
BUILD_NUMBER=200804010004
NB_HOME=`cd ../../../..; pwd`
OUTPUT_DIR="$NB_HOME/dist_new"

ANT_OPTS="-Xmx1024m -Dhttp.proxyHost=webcache.norway.sun.com -Dhttp.proxyPort=8080 "

CACHE_DIR=$NB_HOME/ssinstaller/infra/build/cache

NB_BUILDS_HOST=http://bits.nbextras.org/download/trunk/nightly/2008-04-01_02-01-24/zip/moduleclusters

JDK_HOME=/usr/java
NB_FILES_PREFIX=netbeans-trunk-nightly


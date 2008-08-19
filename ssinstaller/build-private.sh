#!/bin/sh -v
############################################################################
# This file is temporary used by nightly.
################################################################################


# The directory to save binaries 
OUTPUT_DIR="`pwd`/build"
export OUTPUT_DIR

# The Studio build to create installer
SUNSTUDIO_BITS_ROOT=/shared/dp/sstrunk/latest
export SUNSTUDIO_BITS_ROOT

# The path to the product xml file
PRODUCTS_XML_FILE=`pwd`/ProductDescription.xml
export PRODUCTS_XML_FILE

# Remote installer use this url
BUNDLES_URL=file://../packaged
export BUNDLES_URL

# version is used in name
SS_VERSION="X"
export SS_VERSION

# the distributive version
DISTRS="intel-S2"
export DISTRS

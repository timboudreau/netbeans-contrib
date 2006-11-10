#!/bin/sh

cd `dirname $0`
dpkg-buildpackage -rfakeroot -uc -us -b
sh ./build-repos.sh

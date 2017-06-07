#!/bin/bash -x

#
# Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
#
function add_module() {
    platform=$1
    unzip -q com-oracle-tools-analysis-clangtidy-${platform}.nbm Info/info.xml
#    cat Info/info.xml | egrep -e "<.?module|<.?manifest|<.?license" >> catalog.xml
    cat Info/info.xml | egrep -e "<.?module|<.?manifest" >> catalog.xml
    downlod_size=`ls -l com-oracle-tools-analysis-clangtidy-${platform}.nbm | awk '{print $5}'`
    sed -e "s|downloadsize=\"0\"|downloadsize=\"${downlod_size}\"|" catalog.xml > tmp
    sed -e "s|distribution=\"\"|distribution=\"com-oracle-tools-analysis-clangtidy-${platform}\.nbm\"|" tmp > catalog.xml
    rm -rf tmp
    rm -rf Info
}

function add_modules_licence() {
    platform=$1
    unzip -q com-oracle-tools-analysis-clangtidy-${platform}.nbm Info/info.xml
    sed -e '/<license name/,/license>/!d' Info/info.xml >> catalog.xml
    rm -rf Info
}

#upload="/net/everest/export1/sside/parfait/upload/"
export projects=$1

#cp ${projects}/Linux_x86/build/com-oracle-tools-analysis-parfait-Linux_x86.nbm out/
cp ${projects}/Linux_x86_64/build/com-oracle-tools-analysis-clangtidy-Linux_x86_64.nbm out/

cd out
timestamp=`date +%H/%M/%S/%m/%d/%Y`

echo "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" > catalog.xml
echo "" >> catalog.xml
echo "<!DOCTYPE module_updates PUBLIC \"-//NetBeans//DTD Autoupdate Catalog 2.6//EN\" \"http://www.netbeans.org/dtds/autoupdate-catalog-2_6.dtd\">" >> catalog.xml
echo "<module_updates timestamp=\"${timestamp}\">" >> catalog.xml
echo '<module_group name="Oracle Developer Studio">' >> catalog.xml

#add_module Linux_x86
add_module Linux_x86_64

echo '</module_group>' >> catalog.xml
add_modules_licence Linux_x86_64
echo '</module_updates>' >> catalog.xml
cp catalog.xml catalog.xml.1
rm -f catalog.xml.gz
gzip catalog.xml
mv catalog.xml.1 catalog.xml

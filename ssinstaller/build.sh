echo "The tools are used:"
which ant || exit 0;
which java || exit 0;
which xjc || exit 0;

# set REBUILD=true to rebuild only 

. build-private.sh

cd BuildHelper
xjc -d src -p org.netbeans.xml.schema.productdescription xml-resources/jaxb/description/ProductDescription.xsd
ant -f build2.xml
cd -
java -cp BuildHelper/dist/BuildHelper.jar buildhelper.BuildHelper $PRODUCTS_XML_FILE . toolchain

cd infra
bash build.sh || exit 1;
cd - 

if [ -z "$REBUILD" ]; then
    cd registration
    bash build.sh || exit 1;
fi

#cd toolchain
#bash build.sh

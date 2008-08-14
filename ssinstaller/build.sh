echo "The tools are used:"
which ant || exit 0;
which java || exit 0;
which xjc || exit 0;

cd BuildHelper
xjc -d src -p org.netbeans.xml.schema.productdescription xml-resources/jaxb/description/ProductDescription.xsd
ant -f build2.xml
cd -
java -cp BuildHelper/dist/BuildHelper.jar buildhelper.BuildHelper ProductDescription.xml . toolchain

cd infra
bash build.sh
cd - 
cd toolchain
bash build.sh

#export PATH=/set/java-sqe/tools/apache-ant-1.6.5/bin:$PATH
export JAVA_HOME=/usr/java
which ant
cd BuildHelper
$JAVA_HOME/bin/xjc -d src -p org.netbeans.xml.schema.productdescription xml-resources/jaxb/description/ProductDescription.xsd
ant -f build2.xml
cd -
java -cp BuildHelper/dist/BuildHelper.jar buildhelper.BuildHelper ProductDescription.xml . toolchain

cd infra
bash build.sh
cd - 
cd toolchain
#bash build.sh
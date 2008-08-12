ant -f BuildHelper/build.xml
java -cp BuildHelper/dist/BuildHelper.jar buildhelper.BuildHelper ProductDescription.xml . toolchain
cd infra
bash build.sh
cd - 
cd toolchain
bash build.sh
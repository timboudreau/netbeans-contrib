#!/bin/sh

for f in *.{jar,gz};
  do openssl dgst -sha1 $f | tr '[a-z]' '[A-Z]' ; done

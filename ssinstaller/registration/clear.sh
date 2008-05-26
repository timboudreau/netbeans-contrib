stclient -x | grep instanc | sed s/.*\>u/u/ | sed s/\<.*// | while read f; do stclient -d -i $f; done

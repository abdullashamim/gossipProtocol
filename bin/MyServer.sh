#!/bin/bash
filename=$1"_output.txt"
>$filename
#echo $filename
if [[ -z "$4" ]]; then
echo "param 4 is not present "
java server.GossipServer $1 $2 >> $filename
else
echo "Param 4 is present"
java server.GossipServer $1 $2 $3 $4 >> $filename
fi

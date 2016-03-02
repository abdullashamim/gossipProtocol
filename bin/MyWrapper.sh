#!/bin/bash
./MyServer.sh "1" $1 "-i" "input.txt" &
./MyServer.sh "2" $1 &
./MyServer.sh "3" $1 &
./MyServer.sh "4" $1 "-i" "input4.txt" &
./MyServer.sh "5" $1 &
./MyServer.sh "6" $1 &
./MyServer.sh "7" $1 &
./MyServer.sh "8" $1 &
./MyServer.sh "9" $1 &
./MyServer.sh "10" $1 &

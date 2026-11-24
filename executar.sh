#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
javac --release 17 -encoding UTF-8 -d out src/app/*.java src/controller/*.java src/exception/*.java src/interfaces/*.java src/model/*.java src/view/*.java
java -cp out app.Principal

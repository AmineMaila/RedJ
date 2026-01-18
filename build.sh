#!/bin/sh

javac -d ./src/bin $(find src -name "*.java")

if [ ! -f "manifest.txt" ]; then
    echo "Main-Class: Main" > "manifest.txt"
    echo "" >> "manifest.txt"
fi
jar cfm RedJ.jar manifest.txt -C ./src/bin .

rm -rf ./src/bin
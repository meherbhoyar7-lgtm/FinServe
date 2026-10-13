#!/bin/bash
# FinServe - Compile and Run Script
echo "╔══════════════════════════════════════════════════════╗"
echo "║   Compiling FinServe Banking Analytics Platform...   ║"
echo "╚══════════════════════════════════════════════════════╝"

cd "$(dirname "$0")"

# Clean and create output directory
rm -rf out
mkdir -p out

# Compile all Java files
echo "  Compiling Java sources..."
find src -name "*.java" > sources.txt
javac -d out @sources.txt 2>&1

if [ $? -eq 0 ]; then
    echo "  ✓ Compilation successful!"
    echo ""
    echo "  Starting FinServe server..."
    echo ""
    java -cp out com.finserve.Main "$@"
else
    echo "  ✗ Compilation failed!"
    exit 1
fi

rm -f sources.txt

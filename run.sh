#!/bin/bash
# Compile & jalankan aplikasi Gudang Logistik
# Driver MySQL (mysql-connector-j) HARUS ikut di classpath.
set -e
cd "$(dirname "$0")"

JAR="lib/mysql-connector-j-9.1.0.jar"
OUT="out"

echo "==> Compile..."
javac -cp "$JAR" -d "$OUT" $(find src -name "*.java")

echo "==> Jalankan aplikasi..."
java -cp "$OUT:$JAR" Main

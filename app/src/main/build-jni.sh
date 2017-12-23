#!/bin/bash

rm -rf assets

ndk-build

for p in armeabi-v7a arm64-v8a mips x86; do
	mkdir -p assets/$p
	cp libs/$p/{tun2socks,pdnsd} assets/$p/
done

rm -rf libs obj

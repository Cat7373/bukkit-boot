#!/usr/bin/env bash

cd `dirname "$0"`

rm runtime/plugins/*.jar
cp ../build/libs/*.jar runtime/plugins/
cp ../demo-plugin/build/libs/*.jar runtime/plugins/

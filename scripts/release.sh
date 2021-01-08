#!/bin/sh

# do scala 2.12 release
mvn release:clean release:prepare release:perform

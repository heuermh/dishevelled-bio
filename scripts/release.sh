#!/bin/sh

# do scala 2.11 release
mvn release:clean release:prepare release:perform

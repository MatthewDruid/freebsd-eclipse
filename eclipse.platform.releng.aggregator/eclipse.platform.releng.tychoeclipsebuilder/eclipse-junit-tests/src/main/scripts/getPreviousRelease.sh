#!/usr/bin/env bash

# Convenience script.
# Utility to copy current and previous versions of SDK and platform binary to the expected location.
# some parts expected to be temporary, until 'installDir' and 'testDir' 
# correctly or redefined.


mkdir -p workarea/${buildId}/eclipse-testing
cp /home/files/buildzips/oxygen/R/R-4.13-201909161045/eclipse-platform-4.13-freebsd-gtk-amd64.tar.gz ./workarea/${buildId}/eclipse-testing/platformLocation/

cp /home/files/buildzips/oxygen/R/R-4.13-201909161045/eclipse-SDK-4.13-freebsd-gtk-amd64.tar.gz ./workarea/${buildId}/eclipse-testing/

cp eclipse-junit-tests-${buildId}.zip workarea/${buildId}/eclipse-testing/

cp library.xml workarea/${buildId}/eclipse-testing/
cp JUNIT.XSL   workarea/${buildId}/eclipse-testing/

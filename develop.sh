#!/bin/sh

#
# SYNOPSYS
#   ./develop.sh
#
# DESCRIPTION
#   This script shall be used to manage our development experience.
#   It starts a fahrenheit development box if none is running yet.
#

if [[ "" == "$(docker ps -a --filter name=fahrenheit_devbox --filter status=running -q)" ]]; then
  docker run -it --rm --name fahrenheit_devbox --mount type=bind,src=$PWD,dst=/workspaces/fahrenheit -w /workspaces/fahrenheit clojure:lein bash
fi

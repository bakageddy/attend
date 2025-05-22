#!/usr/bin/env sh

set -xe
if [ ! -f $@ ]; then
	sudo jhsdb jstack --pid $(pgrep -f tomcat) --locks > $@
else
	sudo jhsdb jstack --pid $(pgrep -f tomcat) --locks >> $@
fi

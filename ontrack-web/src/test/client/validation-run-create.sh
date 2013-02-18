#!/bin/bash

USER=$1
PASSWORD=$2
PROJECT=$3
BRANCH=$4
BUILD=$5
VALIDATION_STAMP=$6
STATUS=$7
DESCRIPTION=$8

URL=http://localhost:8080/ontrack/ui/control/validation/${PROJECT}/${BRANCH}/${VALIDATION_STAMP}/${BUILD}
DATA="{\"status\":\"${STATUS}\",\"description\":\"${DESCRIPTION}\"}"

echo POST to $URL

curl "${URL}" --user ${USER}:${PASSWORD} --header "Content-Type: application/json" --data "$DATA"



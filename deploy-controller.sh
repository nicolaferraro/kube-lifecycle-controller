#!/bin/bash

oc create serviceaccount kube-lifecycle-controller || true
oc adm policy add-role-to-user edit --serviceaccount kube-lifecycle-controller || true

mvn clean install -Dfabric8.skip
mvn -pl controller fabric8:deploy
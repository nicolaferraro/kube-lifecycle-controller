#!/bin/bash

mvn clean install -Dfabric8.skip
mvn -pl example fabric8:deploy
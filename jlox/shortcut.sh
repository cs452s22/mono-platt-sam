#!/bin/bash
rm -rf target
mvn generate-sources
mvn spring-boot:run
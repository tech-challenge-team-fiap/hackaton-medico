#!/bin/bash

if [ -z "$1" ]; then
  echo "Informe a tag para a imagem."
else
  mvn clean package install && docker build -t hackathon-doctor:"$1" .
fi
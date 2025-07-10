#!/bin/bash

echo "Please enter the directory where jatyc.jar is located:"
read -r jar_dir

# Check if the file exists
if [ ! -f "$jar_dir/jatyc.jar" ]; then
    echo "Error: jatyc.jar not found in the specified directory."
    exit 1
fi

# Install the JAR into the local Maven repository
echo "Installing jatyc.jar from $jar_dir into local Maven repository..."

mvn install:install-file \
    -Dfile="$jar_dir/jatyc.jar" \
    -DgroupId=pt.unl.fct.di.novasys.jatyc \
    -DartifactId=jatyc \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Check if the installation was successful
if [ $? -eq 0 ]; then
    echo "jatyc.jar successfully installed into the local Maven repository!"
else
    echo "Error: Failed to install jatyc.jar."
    exit 1
fi

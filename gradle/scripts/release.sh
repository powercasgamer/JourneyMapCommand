#!/bin/bash

gradleProperties = "../../gradle.properties"

# Get the current version from gradle.properties
version=$(grep -oP 'version=\K.*(?=-SNAPSHOT)' $gradleProperties)

# Remove the "-SNAPSHOT" suffix from gradle.properties
sed -i 's/^\(version=\).*-SNAPSHOT$/\1'"$version"'/' $gradleProperties

# Create a git tag for the version
git add $gradleProperties
git commit -m "release: $version"
git tag "$version" -m "Release version $version"

# Push the tag to GitHub
# Commit the change to gradle.properties
git push origin "$version"
git fetch --tags origin
./gradlew clean build
gh release create "$version" --latest --verify-tag --generate-notes --title "$version" **/build/libs/*-"$version".jar
# Publish the release artifact
./gradlew publish -PforceSign=true
./gradlew publishModrinth

# Increment the version and add the "-SNAPSHOT" suffix to gradle.properties
new_version=$(echo "$version" | awk -F. '{$NF++;print}' | sed 's/ /./g')-SNAPSHOT
sed -i 's/^\(version=\).*$/\1'"$new_version"'/' $gradleProperties

# Commit the change to gradle.properties
git add $gradleProperties
git commit -m "snapshot: $new_version"

git push

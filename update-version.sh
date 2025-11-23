#!/bin/bash
# Script to update version across all project files

if [ -z "$1" ]; then
  echo "Usage: ./update-version.sh <new-version>"
  echo "Example: ./update-version.sh 0.0.2-SNAPSHOT"
  exit 1
fi

NEW_VERSION=$1
OLD_VERSION=$(cat VERSION)

echo "Updating version from $OLD_VERSION to $NEW_VERSION..."

# Update VERSION file
echo "$NEW_VERSION" > VERSION

# Detect OS for sed compatibility
if [[ "$OSTYPE" == "darwin"* ]]; then
  # macOS requires empty string after -i
  SED_BACKUP=""
else
  # Linux doesn't need backup extension
  SED_BACKUP=""
fi

# Update .env files
if [ -f .env ]; then
  sed -i$SED_BACKUP "s/VERSION=.*/VERSION=$NEW_VERSION/" .env
fi
sed -i$SED_BACKUP "s/VERSION=.*/VERSION=$NEW_VERSION/" .env.example

# Update root pom.xml
sed -i$SED_BACKUP "s/<revision>.*<\/revision>/<revision>$NEW_VERSION<\/revision>/" pom.xml

# Update frontend package.json
sed -i$SED_BACKUP "s/\"version\": \".*\"/\"version\": \"$NEW_VERSION\"/" frontend/package.json

# Update K8s deployment files
echo "Updating Kubernetes deployment files..."
for file in k8s-configs/*-deployment.yaml; do
  if [ -f "$file" ]; then
    sed -i$SED_BACKUP "s/\${VERSION:-[^}]*}/\${VERSION:-$NEW_VERSION}/g" "$file"
  fi
done

echo "Version updated successfully to $NEW_VERSION"
echo ""
echo "Next steps:"
echo "1. Review changes: git diff"
echo "2. Build: mvn clean install"
echo "3. Commit: git add . && git commit -m 'Bump version to $NEW_VERSION'"

#!/bin/bash
# Script to deploy to Kubernetes with version validation

# Check if VERSION is set
if [ -z "$VERSION" ]; then
  echo "ERROR: VERSION environment variable is not set!"
  echo ""
  echo "Please set VERSION before deploying:"
  echo "  export VERSION=\$(cat VERSION)"
  echo "  or"
  echo "  export VERSION=0.0.2-SNAPSHOT"
  echo ""
  exit 1
fi

echo "Deploying with VERSION=$VERSION"

# Deploy all services
for config in k8s-configs/*-deployment.yaml; do
  echo "Applying $config..."
  envsubst < "$config" | kubectl apply -f -
done

echo ""
echo "Deployment complete!"
echo "Check status: kubectl get pods"

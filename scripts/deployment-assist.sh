#!/bin/bash

is_inventory(){
    # go to the inventory directory
    cd ../inventory-service
    echo "Building using maven"
    
    mvn clean install -U -DskipTests

    read -p "Do you want to docker build? (Y/N) :" DOCKER_BUILD
    if [ "$DOCKER_BUILD" == "Y" ]; then
        docker build . -t localhost:5000/inventory-service:latest
    fi

    read -p "Do you want to push image to registry? (Y/N) :" DOCKER_PUSH
    if [ "$DOCKER_PUSH" == "Y" ]; then
        docker push localhost:5000/inventory-service:latest
    fi 

    cd ../kafka-docker-installation/kube-objects/
    microk8s kubectl delete -f nginx-kube.yaml
    microk8s kubectl delete -f  word-inventory-service.yaml
    microk8s kubectl apply -f  word-inventory-service.yaml
    microk8s kubectl apply -f  nginx-kube.yaml

}

# Declare an associative array
declare -A services

# Add key-value pairs (key=service name, value=yaml file name)
services=(
  [writewise]="auth-deployment.yaml"
  [inventory]="payment-deployment.yaml"
  [ui]="order-deployment.yaml"
  [snapster]="order-deployment.yaml"
  [tts]="order-deployment.yaml"
  [nginx]="order-deployment.yaml"
  [mysql]="order-deployment.yaml"
  [mongodb]="order-deployment.yaml"
  [meaning-eval]="order-deployment.yaml"
  [llm]="order-deployment.yaml"
  [fitmate]="order-deployment.yaml"
)

# Prompt for the Git branch name
read -p "Enter the GitHub branch name: " BRANCH_NAME

# Pull the specified branch
git pull origin "$BRANCH_NAME"

# Prompt for the service name
read -p "Enter the service name to deploy: " SERVICE_NAME

if [ "$SERVICE_NAME" == "inventory" ]; then
    is_inventory
fi



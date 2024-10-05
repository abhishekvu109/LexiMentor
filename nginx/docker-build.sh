docker build . -t localhost:5000/writewise-learning-service:latest
docker push localhost:5000/writewise-learning-service:latest
# shellcheck disable=SC2164
cd ../kafka-docker-installation/kube-objects/
microk8s kubectl -f delete writewise-service.yaml
microk8s kubectl -f apply writewise-service.yaml

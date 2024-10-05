docker build . -t localhost:5000/nginx-service:latest
docker push  localhost:5000/nginx-service:latest
# shellcheck disable=SC2164
cd ../kafka-docker-installation/kube-objects/
microk8s kubectl -f delete nginx-kube.yaml
microk8s kubectl -f apply nginx-kube.yaml

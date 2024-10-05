docker build . -t localhost:5000/nginx-service:latest
docker push  localhost:5000/nginx-service:latest
# shellcheck disable=SC2164
cd ../kafka-docker-installation/kube-objects/
k -f delete nginx-kube.yaml
k -f apply nginx-kube.yaml

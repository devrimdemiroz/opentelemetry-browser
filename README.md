# opentelemetry browser

A traced chrome browser via Selenium Chrome Devtools reporting traces via grafana agent with otlp...  Own needs and ideas with something just usable ;)

Opens a browser containing instructions how to start/stop a trace. Similar to .har file but on an opentelemetry backend.

It enables a playground for those who are fairly new to tracing as well experts who know the value of http trafic on browser flows as an opentelemetry trace without configuring any instrumentation on top.

![](docs/opentelemetry-browser-architecture.drawio.png)

## Quick start 

### Install chromedriver
Install chromedriver locally according to your browser version https://chromedriver.chromium.org/downloads
Default /usr/local/bin would be for MacOS. In that case run chromedriver executable once to have run permission. 
Repeat this on each driver update. Remember to download new driver if you upgrade the browser or use an auto managed chromedriver java code ;)
```shell
unzip  ~/Downloads/chromedriver_mac64.zip
cp ~/Downloads/chromedriver  /usr/local/bin/chromedriver
```

### Download java agent
```shell
wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

### Configure and run
- Edit jvmArguments inside pom.xml to set opentelemetry collector endpoints if it is other than localhost and default ports. 
```
-Dotel.exporter.otlp.endpoint=http://grafana-agent-traces.default.svc.cluster.local:4317
<!-- -Dotel.exporter.otlp.endpoint=http://localhost:4317 -->
-Dotel.metrics.exporter=none
<!-- -Dotel.metrics.exporter=otlp-->
```
- Run
```shell
mvn build
./mvnw spring-boot:run
```
Should opens a browser containing instructions how to start/stop a tracing.

---
## Slow start ( kubic way )

This section defines installing a grafana agent in local k8s to use as "opentelemetry collector" endpoint. 

- Install docker
- Install minikube

### Install opentelemetry collector
```shell
cd kubernetes/opentelemetry
# enter your grafana cloud info into yaml 
echo -n "<your user id>:<your api key>" | base64
# kubectl apply -f otel-collector.yaml
envsubst < otel-collector.yaml  | kubectl apply -f - 
kubectl rollout restart DaemonSet/otel-collector
ps -ef|grep 4317 |  awk '{print $2}' | xargs kill
kubectl port-forward svc/otel-collector --address=0.0.0.0 4317:4317 &

```


### Install grafana agent 
#### grafana agent traces
```shell
kubectl apply -f kubernetes/agent-traces.yaml
export USERNAME=<grafana cloud username>
export PASSWORD=<grafana cloud password>
kubectl apply -f kubernetes/agent-traces-configmap.yaml
kubectl rollout restart deployment/grafana-agent
```
Reach from local:
```shell
# otlp
kubectl port-forward svc/grafana-agent-traces --address=0.0.0.0 4317:4317 &

# optional to check metrics
# trace span metrics endpoint:
kubectl port-forward svc/grafana-agent-traces --address=0.0.0.0 8889:8889 &
# trace grafana agent own metrics endpoint:
kubectl port-forward svc/grafana-agent-traces 8080:8080

```






#### alternative grafana agent operator
  - Partially Follow https://grafana.com/docs/agent/latest/operator/getting-started/ 
  - Checkout grafana agent
  ```shell
  kubectl apply -f production/operator/crds
  ```
  - Install go
  ```shell
  go run ./cmd/agent-operator
  ```
  - 

# opentelemetry-browser awsome cockpit

## Actions

<a href="http://localhost:${server.port}/start" target="_blank">Start Intercept</a>

Opens a new tab and creates a root span session. Shows TraceId where you can later find the traces. 


[Stop Intercept](http://localhost:${server.port}/stop)

Stops the interception on current root span session and ends root span.

#### Tracing Status 
```text
${tracing.status}
```

[Quit Browser](http://localhost:${server.port}/quit)

---
## Useful links

[${tracing.ui.url}](${tracing.ui.url})

You can navigate to your favorite tracing.ui.url address defined in application.properties to see the trace collected between start and stop interception.

#### spanmetrics endpoint
<a href="http://grafana-agent-traces.default.svc.cluster.local:8889/metrics" target="_blank">grafana-agent-traces.default.svc.cluster.local:8889/metrics</a>





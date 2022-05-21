<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

```mermaid
%%{init:  { htmlLabels: true, flowchart: { htmlLabels: true }, securityLevel: 'loose' }} }%%
flowchart LR
classDef green fill:green,stroke:green,stroke-width:2px,color:#fff
classDef dgreen fill:darkgreen,stroke:green,stroke-width:2px,color:#fff
agent-crd("fa:fa-file agent-crd"):::dgreen
collector-crd("fa:fa-file collector-crd"):::dgreen
subgraph oteloperator["opentelemetry operator"]
            direction LR
            inject((X)) 
            inject2((X)) 
            deploy((X)) 
         
    end
    subgraph app
        subgraph sidecar
            otelcol-sidecar(collector <br/> sidecar)
        end
        subgraph container
            direction LR 
            application(application) 
            otelagent("fa:fa-check-circle opentelemetry <br/>agent"):::dgreen
            
        end
    end

container o--o sidecar
   
  
 agent-crd -->inject-->|inject|otelagent
 collector-crd ---->inject2-->|inject|otelcol-sidecar    
 collector-crd ---->deploy    
            
otelcol-daemonset( opentelemetry <br/> daemonset):::dgreen
otelcol( opentelemetry <br/>collector)
deploy -->|deploy| otelcol-daemonset & otelcol

otelagent -->|otlp| otelcol-daemonset

```
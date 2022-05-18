<link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.1.1/css/all.css" integrity="sha384-gfdkjb5BdAXd+lj+gudLWI+BXq4IuLW5IT+brZEZsLFm++aCMlF1V92rMkPaX4PP" crossorigin="anonymous">

```mermaid
flowchart LR
classDef green fill:green,stroke:green,stroke-width:2px,color:#fff
classDef dgreen fill:darkgreen,stroke:green,stroke-width:2px,color:#fff
subgraph oteloperator["opentelemetry operator"]
            direction LR 
            agent-crd(fa:fa-file-code agent-crd):::dgreen
            collector-crd(fa:fa-file-code collector-crd) 
         
    end
    subgraph app
        subgraph container
            direction LR 
            application(application)
            otelagent(fa:fa-check-circle opentelemetry <br/>agent):::dgreen
        end
        subgraph sidecar
            otelcol-sidecar(collector sidecar)
        end
    end

container o--o sidecar
   
  
 agent-crd -->|inject|otelagent
 collector-crd ---->|inject|otelcol-sidecar    
            
otelcol-daemonset( opentelemetry daemonset):::dgreen
otelcol( opentelemetry collector)
oteloperator -->|deploy| otelcol-daemonset & otelcol

```
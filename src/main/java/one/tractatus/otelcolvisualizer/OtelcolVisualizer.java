package one.tractatus.otelcolvisualizer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import static one.tractatus.otelcolvisualizer.MermaidFormatter.*;

public class OtelcolVisualizer {

    private static JsonNode pipelines;
    private static BufferedWriter bw;
    private static String pipeLineEnd="";
    private static JsonNode collectoryConfig=null;
    private static String exporterNodes ="";
    private static String receiverNodes="";
    private static String receiversPipe ="";
    private static String links ="";
    private static String exportersPipe;
    private static String processorsPipe;
    private static String pipes;

    public static void main(String[] args) throws IOException {
        String configFile;
        configFile = "kubernetes/opentelemetry/otel-collector.yaml";
        if (args !=null ){
            configFile = args[0];
        }
        System.out.println("Reading configfile="+configFile);
        readConfig(configFile);
        generateDiagram();
    }


    private static void generateDiagram() throws IOException {

        File file = new File("src/main/resources/static/GeneratedDiagram.md");
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        bw.write("```mermaid \n");
        bw.write("flowchart LR\n");
        bw.write("classDef tclass fill:orange,stroke:orange,stroke-width:2px,color:#fff");        bw.newLine();
        bw.write("classDef mclass fill:blue,stroke:blue,stroke-width:2px,color:#fff");        bw.newLine();
        processPipelines();

        bw.write("subgraph receiverNodes[\" \"]\n");
        bw.write("direction LR\n");
        bw.write(receiverNodes+"\n");
        bw.write("end\n");

        bw.write("subgraph otelcol\n");
        bw.write("direction LR\n");
        bw.write("  "+ pipes +"\n");

        bw.write("end\n");
        bw.write("subgraph exporterNodes[\" \"]\n");
        bw.write("direction LR\n");
        bw.write(exporterNodes +"\n");
        bw.write("end\n");



        // append links last as format is dependent on link sequence
        bw.write( links +"\n");
        bw.write("```");
        bw.close();


    }

    private static void processPipelines() throws IOException {
        pipelines = collectoryConfig.findValue("pipelines");
        System.out.println("Processing pipelines yaml section of config: \n " + pipelines.toString());
        pipes="";
        for (Iterator<String> it = pipelines.fieldNames(); it.hasNext(); ) {
            String pipeName = it.next();
            pipes+="  subgraph pipeline_"+pipeName+"\n";
            pipes+="  direction LR\n";
            JsonNode pipeline = pipelines.get(pipeName);
            pipes+=addPipeline(pipeName,pipeline);

            // to end ("subgraph pipeline_"+pipeName)
            pipes+="  end\n\n";
        }

    }

    private static String addPipeline(String pipeName, JsonNode pipeline) throws IOException {
        String pipe = "";
        String receivers = addReceivers(pipeName, pipeline.get("receivers"));
        pipe+=receiversPipe;
        String processors = addProcessors(pipeName, pipeline.get("processors"));
        pipe+=processorsPipe;
        String exporters = addExporters(pipeName, pipeline.get("exporters"));
        pipe+=exportersPipe;

        if (processors!=null) {
            addLink(receivers,processors,null,null);
            addLink(processors,exporters,null,null);
        } else {
            addLink(receivers,exporters,null,null);
        }
        return pipe;

    }



    private static String addProcessors(String pipeName, JsonNode processors) throws IOException {
        if (processors==null) return null;

        String name = "processors_" + pipeName;
        processorsPipe="";
        processorsPipe+="      subgraph " + name + "\n";
        processorsPipe+="      direction LR\n";
        for (JsonNode processor : processors) {
            processorsPipe+="          "+processor.asText() + nodeSequence++ + "[["+processor.asText()+"]]\n";
        }
        processorsPipe+="      end\n\n";
        return name;
    }

    private static String addExporters(String pipeName, JsonNode exporters) throws IOException {
        String name = "exporters_" + pipeName;
        exportersPipe ="";
        exportersPipe +="      subgraph " + name + "\n";
        exportersPipe +="      direction LR\n";
        String textOnLink = null;String nodeA = null;String nodeB = null;
        for (JsonNode exporter : exporters) {
            textOnLink = exporter.asText();
            nodeA = newNodeId(exporter);
            nodeB = newNodeId(exporter);
            String endpoint = collectoryConfig.path("exporters").get(textOnLink).path("endpoint").asText();
            if (endpoint==null||endpoint.matches("")){
                endpoint = textOnLink;
            }
            exporterNodes+="          subgraph "+nodeA+"_sg["+" "+"]\n";
            exporterNodes+="          "+nodeA+"_ep("+endpoint+")\n";
            exporterNodes +="          "+formatNode(pipeName, nodeA)+"\n";
            exporterNodes +="         end\n\n";

            exportersPipe +="          "+formatNode(pipeName, nodeB)+"\n";
            addLink(nodeB,nodeA,textOnLink,pipeName);

        }
        exportersPipe +="      end\n\n";

        return name;
    }
    private static String addReceivers(String pipeName, JsonNode receivers) throws IOException {
        String name = "receivers_" + pipeName;
        receiversPipe ="";
        receiversPipe+="      subgraph " + name+"\n";
        receiversPipe+="      direction LR\n";
        String textOnLink = null;String nodeA = null;String nodeB = null;
        for (JsonNode receiver : receivers) {

            textOnLink = receiver.asText();
            nodeA = newNodeId(receiver);
            nodeB = newNodeId(receiver);


            receiverNodes+="          "+formatNode(pipeName, nodeA)+"\n";
            receiversPipe+="          "+formatNode(pipeName, nodeB)+"\n";

            //otlp0((T)):::tclass -->|oltp| otlp1 would be written outside end
            addLink(nodeA, nodeB, textOnLink, pipeName);

        }
        // to end subgraph
        receiversPipe+="      end\n\n";

        return name;
    }

    private static void addLink(String nodeA, String nodeB, String textOnLink, String name) {
        links += nodeA + "-->" + formatTextOnLink(textOnLink) + nodeB + "\n";
        links += formatLink(name)+"\n";
        linkSequence++;
    }

    private static String formatTextOnLink(String textOnLink) {
        if (textOnLink==null) return "";
        return "|" + textOnLink + "|";
    }


    private static void readConfig(String configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File file = new File(configFile);
        JsonNode config = mapper.readTree(file);
        config = config.findValue("config");


        // read data section which is yaml inside yaml
        System.out.println(" config yaml section " + config.toString());
        collectoryConfig = mapper.readTree(config.asText());

    }
}

package one.tractatus.otelcolvisualizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.Iterator;


public class OtelcolVisualizer {

    private static JsonNode pipelines;
    private static int seq;

    public static void main(String[] args) throws IOException {
        readConfig();
        generateDiagram();
    }


    private static void generateDiagram() throws IOException {

        File file = new File("docs/GeneratedDiagram.md");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("```mermaid"); bw.newLine();
        bw.write("flowchart TD");        bw.newLine();
        bw.write("classDef tclass fill:orange,stroke:orange,stroke-width:2px,color:#fff");        bw.newLine();
        bw.write("classDef mclass fill:blue,stroke:blue,stroke-width:2px,color:#fff");        bw.newLine();
        bw.write("subgraph otelcol");        bw.newLine();
        bw.write("direction LR");        bw.newLine();


        addPipelines(bw);
        bw.write("te(otlp)");bw.newLine();
        bw.write("end");bw.newLine();
        bw.write("```");
        bw.close();


    }

    private static void addPipelines(BufferedWriter bw) throws IOException {
        for (Iterator<String> it = pipelines.fieldNames(); it.hasNext(); ) {
            String pipeName = it.next();
            System.out.println(pipeName);
            bw.write("subgraph pipeline_"+pipeName);bw.newLine();
            JsonNode pipeline = pipelines.get(pipeName);
            addPipeline(pipeName,pipeline, bw);

            // to end ("subgraph pipeline_"+pipeName)
            bw.write("end");bw.newLine();
        }
    }

    private static void addPipeline(String pipeName, JsonNode pipeline, BufferedWriter bw) throws IOException {
        for (Iterator<String> it = pipeline.fieldNames(); it.hasNext(); ) {
            String itemName = it.next();
            System.out.println(itemName);
            bw.write("subgraph "+itemName+"_"+pipeName);bw.newLine();
            JsonNode worker = pipeline.get(itemName);
            addWorkers(itemName,worker,bw);
            // to end subgraph
            bw.write("end");bw.newLine();
        }
    }

    private static void addWorkers(String itemName, JsonNode worker, BufferedWriter bw) throws IOException {
        for (JsonNode work : worker) {

            System.out.println("---"+work.asText());
            bw.write("M"+ seq++ +"(("+work.asText()+")):::mclass");bw.newLine();

        }

            // to end subgraph
            //bw.write("end");bw.newLine();

    }

    private static void readConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File file = new File("kubernetes/opentelemetry/otel-collector.yaml");
        JsonNode config = mapper.readTree(file);
        config = config.findValue("config.yaml");
        System.out.println(" config yaml section " + config.toString());
        config = mapper.readTree(config.asText());
        pipelines = config.findValue("pipelines");
        System.out.println(" pipelines yaml section " + pipelines.toString());
    }
}

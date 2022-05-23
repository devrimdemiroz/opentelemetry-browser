package one.tractatus.otelcolvisualizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.Iterator;


public class OtelcolVisualizer {

    private static JsonNode pipelines;
    private static int seq;
    private static int cseq;
    private static BufferedWriter bw;
    private static String pipeLineEnd="";

    public static void main(String[] args) throws IOException {
        readConfig();
        generateDiagram();
    }


    private static void generateDiagram() throws IOException {

        File file = new File("src/main/resources/static/GeneratedDiagram.md");
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        bw.write("```mermaid"); bw.newLine();
        bw.write("flowchart TD");        bw.newLine();
        bw.write("classDef tclass fill:orange,stroke:orange,stroke-width:2px,color:#fff");        bw.newLine();
        bw.write("classDef mclass fill:blue,stroke:blue,stroke-width:2px,color:#fff");        bw.newLine();
        bw.write("subgraph otelcol");        bw.newLine();
        bw.write("direction LR");        bw.newLine();


        addPipelines(bw);
        bw.write("end");
        bw.newLine();
        bw.write("```");
        bw.close();


    }

    private static void addPipelines(BufferedWriter bw) throws IOException {
        for (Iterator<String> it = pipelines.fieldNames(); it.hasNext(); ) {
            String pipeName = it.next();
            System.out.println("    pipeName="+pipeName);
            bw.write("  subgraph pipeline_"+pipeName);bw.newLine();
            bw.write("  direction LR\n");
            JsonNode pipeline = pipelines.get(pipeName);
            addPipeline(pipeName,pipeline, bw);

            // to end ("subgraph pipeline_"+pipeName)
            bw.write("  end\n");
        }
    }

    private static void addPipeline(String pipeName, JsonNode pipeline, BufferedWriter bw) throws IOException {
        String receivers = addReceivers(pipeName, pipeline.get("receivers"));
        String processors = addProcessors(pipeName, pipeline.get("processors"));
        String exporters = addExporters(pipeName, pipeline.get("exporters"));
        String connections="";
        connections+=receivers+"----->";
        cseq++;
        if (processors!=null) {
            connections+=processors+"----->";
            cseq++;
        }
        connections+=exporters;
        bw.write("  "+connections+"\n");
    }



    private static String addProcessors(String pipeName, JsonNode processors) throws IOException {
        if (processors==null) return null;

        String name = "processors_" + pipeName;
        bw.write("      subgraph " + name + "\n");
        bw.write("      direction LR\n");
        for (JsonNode processor : processors) {
            bw.write("          "+processor.asText() + seq++ + "[["+processor.asText()+"]]\n");
        }
        bw.write("      end\n");
        return name;
    }

    private static String addExporters(String pipeName, JsonNode exporters) throws IOException {
        String name = "exporters_" + pipeName;
        bw.write("      subgraph " + name + "\n");
        bw.write("      direction LR\n");
        String p0 = null;String p1 = null;String p2 = null;
        String connections="";
        for (JsonNode exporter : exporters) {
            System.out.println("---" + exporter.asText());
            p0 = exporter.asText();
            p1 = getFormatted(pipeName, exporter);
            p2 = getFormatted(pipeName, exporter);
            bw.write("          "+p2+"\n");
            connections += p2 + "-->|" + p0 + "|" + p1+"\n";
            connections += "        linkStyle "+ cseq++ + getFormatted(pipeName);
        }
        bw.write("      end\n");
        bw.write("      "+connections);
        return name;
    }
    private static String addReceivers(String pipeName, JsonNode receivers) throws IOException {
        String name = "receivers_" + pipeName;
        bw.write("      subgraph " + name+"\n");
        bw.write("      direction LR\n");
        String p0 = null;String p1 = null;String p2 = null;
        String connections="";
        for (JsonNode receiver : receivers) {

            System.out.println("---" + receiver.asText());
            p0 = receiver.asText();
            p1 = getFormatted(pipeName, receiver);
            p2 = getFormatted(pipeName, receiver);

            bw.write("          "+p2+"\n");

            //otlp0((T)):::tclass -->|oltp| otlp1 would be written outside end
            connections += p1 + "-->|" + p0 + "|" + p2+"\n";
            connections += "        linkStyle "+ cseq++ + getFormatted(pipeName);

        }
        // to end subgraph
        bw.write("      end\n");
        bw.write("      "+connections);

        return name;
    }

    private static String getFormatted(String pipeName) {
        if (pipeName.matches("traces")){
            return " stroke:orange,stroke-width:3px\n";
        }
        return " stroke:blue,stroke-width:3px\n";
    }

    private static String getFormatted(String pipeName, JsonNode jsonNode) {
        if (pipeName.matches("traces")){
            return jsonNode.asText() + seq++ + "((T)):::tclass";
        }
        return jsonNode.asText() + seq++ + "((M)):::mclass";
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

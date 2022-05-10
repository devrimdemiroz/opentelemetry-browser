package one.tractatus.otelcolvisualizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;


public class OtelcolVisualizer {

    private static JsonNode pipelines;

    public static void main(String[] args) throws IOException {
        readConfig();
        generateDiagram();
    }

    private static void generateDiagram() {
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

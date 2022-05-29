package one.tractatus.otelcolvisualizer;

import com.fasterxml.jackson.databind.JsonNode;

public class MermaidFormatter {
    static int nodeSequence;
    static int linkSequence=0;

    static String formatLink(String name) {

        if (name == null){
            return  "";
        }

        if (name.matches("traces")){
            return  "        linkStyle "+linkSequence+ " stroke:orange,stroke-width:3px\n";
        }

        if (name.matches("metrics.*")){
            return "        linkStyle "+linkSequence+" stroke:blue,stroke-width:3px\n";
        }
        return "";
    }

    static String formatNode(String pipeName, String nodeId) {

        if (pipeName.matches("traces")){
            return nodeId + "((T)):::tclass";
        }
        return nodeId + "((M)):::mclass";
    }

    public static String newNodeId(JsonNode jsonNode) {
        return jsonNode.asText() + nodeSequence++;
    }
}

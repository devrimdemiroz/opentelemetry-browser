package one.tractatus;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.File;
import java.io.IOException;

public class UiGw {


    public static void main(String[] args) {
        updateStatus("test status");
    }
    public static void updateStatus(String status) {
        File file = new File("appgw.json");
        ObjectMapper mapper =new ObjectMapper(new JsonFactory());
        try {
            JsonNode appgw = mapper.readTree(file);
            ObjectNode objectNode = (ObjectNode) appgw.get("tracing");
            objectNode.replace("status", new TextNode(status)) ;
            mapper.writeValue(file,appgw);


        } catch (IOException e) {
            e.printStackTrace();
            return ;
        }
    }
}

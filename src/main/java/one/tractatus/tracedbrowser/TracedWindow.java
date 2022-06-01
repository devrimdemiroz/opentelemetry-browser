package one.tractatus.tracedbrowser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import one.tractatus.Cdp;
import one.tractatus.Tracing;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import one.tractatus.UiGw;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

@Slf4j
public class TracedWindow {

    static Environment env= TracedBrowserApp.ctx.getEnvironment();
    public static void awake()  {
        Cdp.initDriver();
        UiGw.updateStatus("No Tracing");
            Cdp.chromeDriver.get("http://localhost:"+env.getProperty("server.port")+"/");
            log.info("app started");


    }

    public static Span start() {
        Cdp.startIntercepting();
        UiGw.updateStatus("Started");
        Span span= Tracing.createRootSpan("session");
        try (Scope unused = span.makeCurrent()) {
            log.info("interception started");
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        }
        return span;
    }



    public static void stop() {
        Tracing.endRootSpan();
        Cdp.stopIntercepting();

    }
}

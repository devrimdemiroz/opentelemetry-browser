package one.tractatus.tracedbrowser;

import one.tractatus.Cdp;
import one.tractatus.Tracing;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

@Slf4j
public class TracedWindow {

    static Environment env= TracedBrowserApp.ctx.getEnvironment();
    private static String tracingStatus="No Active Tracing";
    private static String statusColor="";

    public static void awake()  {
        Cdp.initDriver();
        updateStatus("No Tracing");
            Cdp.chromeDriver.get("http://localhost:"+env.getProperty("server.port")+"/");
            log.info("app started");


    }

    public static Span start() {
        Cdp.startIntercepting();
        updateStatus("Started");

        Span span= Tracing.createRootSpan("sessionX");
        try (Scope unused = span.makeCurrent()) {
            log.info("interception started");
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        }
        return span;
    }

    private static void updateStatus(String status) {
        tracingStatus=status;
        statusColor=":::green";
    }


    public static void stop() {
        Cdp.stopIntercepting();
        Tracing.endRootSpan();
        updateStatus("Stopped");

    }

    public static String getTracingStatus() {
        String json="{\"status\":\""+tracingStatus+"\"}";

        return json;
    }
}

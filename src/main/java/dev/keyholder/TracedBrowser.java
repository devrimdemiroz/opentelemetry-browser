package dev.keyholder;

import dev.keyholder.tracedbrowser.TracedBrowserApp;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;
@Slf4j
public class TracedBrowser {

    static Environment env= TracedBrowserApp.ctx.getEnvironment();
    public static void awake()  {
        Cdp.initDriver();
            Cdp.chromeDriver.get("http://localhost:"+env.getProperty("server.port")+"/hello");
            log.info("app started");

    }

    public static Span start() {
        Cdp.startIntercepting();
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

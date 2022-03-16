package dev.keyholder;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
@Slf4j
public class TracedBrowser {
    public static void main(String[] args) throws InterruptedException {
        awake();
    }

    public static void awake() throws InterruptedException {
        Cdp.initDriver();
            Cdp.chromeDriver.get("http://localhost:8080/hello");
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

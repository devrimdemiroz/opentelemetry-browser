package dev.keyholder;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;

import java.util.concurrent.TimeUnit;

public class TracedBrowser {
    public static void main(String[] args) throws InterruptedException {
        Cdp.initDriverAndDevTools();
        TimeUnit.SECONDS.sleep(1);
        Cdp.chromeDriver.get("http://localhost:8080/hello");
        Span span=dev.keyholder.Tracing.createRootSpan("Test-2");
        try (Scope unused = span.makeCurrent()) {
            dev.keyholder.Cdp.chromeDriver.get("http://localhost:8080/hello");
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            dev.keyholder.Tracing.endRootSpan();
        }
        TimeUnit.SECONDS.sleep(10);
        Cdp.chromeDriver.quit();
    }
}

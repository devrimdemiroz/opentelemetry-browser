package dev.keyholder;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.Filter;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;

public class Cdp {
    private static final TextMapSetter<HttpRequest> setter = new TextMapSetter<HttpRequest>() {
        @Override
        public void set(HttpRequest carrier, String key, String value) {
            // Insert the context as Header
            carrier.addHeader(key, value);
        }
    };
    private static final String urlSkipRegex = ".*\\.ico$";
    public static ChromeDriver chromeDriver;


    public static void initDriverAndDevTools() {

        chromeDriver = new ChromeDriver();
        startIntercepting();

    }

    private static void startIntercepting() {
        NetworkInterceptor networkInterceptor = new NetworkInterceptor(
                chromeDriver,
                headerFilter());
    }

    private static Filter headerFilter() {

        Filter filter = next -> req -> {
            if (req.getUri().matches(urlSkipRegex))
                return next.execute(req);
            Span span =Tracing.startSpan_Url(req.getUri());
            HttpResponse res = null;
            try (Scope unused = span.makeCurrent()) {
                Tracing.propagator.inject(Context.current(), req,setter);
                res = next.execute(req);
            } catch (Exception e) {
                span.recordException(e);
                span.setStatus(StatusCode.ERROR);
            } finally {
                span.end();
            }

            return res;
        };
        return filter;
    }

    public static void driverQuit() {
        chromeDriver.quit();
    }
}

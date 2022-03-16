package dev.keyholder;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.Filter;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;

@Slf4j
public class Cdp {
    private static final TextMapSetter<HttpRequest> setter = new TextMapSetter<HttpRequest>() {
        @Override
        public void set(HttpRequest carrier, String key, String value) {
            // Insert the context as Header
            carrier.addHeader(key, value);
        }
    };
    private static final String urlSkipRegex = ".*\\.ico$" +
            "||.*\\.js$";
    public static ChromeDriver chromeDriver;
    private static NetworkInterceptor networkInterceptor;


    public static void initDriver() {

        chromeDriver = new ChromeDriver();

    }

    public static void startIntercepting() {
        log.info("startIntercepting");
        networkInterceptor = new NetworkInterceptor(
                chromeDriver,
                headerFilter());
    }

    public static void stopIntercepting() {
        log.info("stopIntercepting");
        networkInterceptor.close();
    }

    private static Filter headerFilter() {

        Filter filter = next -> req -> {
            if (req.getUri().matches(urlSkipRegex))
                return next.execute(req);
            Span span =Tracing.startSpan_Url(req.getUri());
            HttpResponse res = null;
            try (Scope unused = span.makeCurrent()) {
                Tracing.propagator.inject(Context.current(), req,setter);
                System.out.println(req.getUri());
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

package one.tractatus;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;


@Slf4j
public class Tracing {


    public static Span rootSpan;
    static final Tracer tracer = GlobalOpenTelemetry.getTracer("traced-browser");
    static final TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
    public static Tracer getTracer() {

        return tracer;
    }

    public static Span createRootSpan(String spanName)  {

        SpanBuilder spanBuilder = Tracing.getTracer().spanBuilder(spanName);
        log.info("Setting root="+spanName);
        spanBuilder.setNoParent();
        rootSpan = spanBuilder
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute("env", "local")
                .startSpan();
        log.info("root span=" + rootSpan.toString());

        return rootSpan;
    }


    public static boolean endRootSpan() {

        if (rootSpan != null) {
            log.debug("endRootSpan="+rootSpan.toString());
            rootSpan.end();
            rootSpan = null;
            return true;
        }
        return false;
    }

    public static Span startSpan_Url(String sUrl) {

        URL url = null;
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            log.warn("Url malformed url=" + sUrl);
            return null;
        }
        Span span = startSpan(url.getHost() + url.getPath());
        span.setAttribute(SemanticAttributes.HTTP_URL, url.toString());
        span.setAttribute(SemanticAttributes.HTTP_HOST, url.getHost());
        span.setAttribute(SemanticAttributes.HTTP_TARGET, url.getQuery());
        return span;
    }

    public static Span startSpan(String spanName) {

        SpanBuilder spanBuilder = Tracing.getTracer().spanBuilder(spanName);

        if (rootSpan != null) {
            spanBuilder.setParent(Context.current().with(rootSpan));
            log.debug(String.valueOf("Setting parent=" + rootSpan.toString()));
        }


        Span span = spanBuilder
                .setAttribute("env","envX")
                .startSpan();
        log.debug(String.valueOf("span=" + span.toString()));

        return span;
    }


    public static void main(String[] args) throws InterruptedException {


        log.info("Exit main ...");


    }


    public static void end(Span span) {
        if (span == null) return;
        try {

        span.end();
        } catch (Exception e){
            log.warn("exporterUnreachable , shutting down tracing" );
        }
    }
}

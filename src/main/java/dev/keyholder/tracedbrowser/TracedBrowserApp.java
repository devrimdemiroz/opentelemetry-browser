package dev.keyholder.tracedbrowser;

import dev.keyholder.Cdp;
import dev.keyholder.TracedBrowser;
import io.opentelemetry.api.trace.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.keyholder.ParseMarkdownToHtml.convertMarkdownFileToHTML;

@SpringBootApplication
@RestController
public class TracedBrowserApp {

	private static ConfigurableApplicationContext ctx;

	@Autowired
	public Environment env;

	public static void main(String[] args) throws InterruptedException {

		ctx=SpringApplication.run(TracedBrowserApp.class, args);
		TracedBrowser.awake();

	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return convertMarkdownFileToHTML("cockpit.md",env);
	}

	@GetMapping("/start")
	public String start() {
		Span span=TracedBrowser.start();

		return span.toString();
	}

	@GetMapping("/stop")
	public String stop() {
		TracedBrowser.stop();

		return convertMarkdownFileToHTML("cockpit.md",env);

	}

	@GetMapping("/quit")
	public void   quit() {
		Cdp.chromeDriver.quit();
		SpringApplication.exit(ctx);

	}
}

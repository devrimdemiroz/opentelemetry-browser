package one.tractatus.tracedbrowser;

import one.tractatus.Cdp;
import io.opentelemetry.api.trace.Span;
import one.tractatus.ParseMarkdownToHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TracedBrowserApp {

	public static ConfigurableApplicationContext ctx;

	@Autowired
	public Environment env;

	public static void main(String[] args) throws InterruptedException {

		ctx=SpringApplication.run(TracedBrowserApp.class, args);
		TracedBrowser.awake();

	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return ParseMarkdownToHtml.convertMarkdownFileToHTML("docs/cockpit.md");
	}

	@GetMapping("/start")
	public String start() {
		Span span=TracedBrowser.start();

		return ParseMarkdownToHtml.convertMarkdownFileToHTML("docs/start.md");
	}

	@GetMapping("/stop")
	public String stop() {
		TracedBrowser.stop();

		return ParseMarkdownToHtml.convertMarkdownFileToHTML("docs/cockpit.md");

	}

	@GetMapping("/quit")
	public void   quit() {
		Cdp.chromeDriver.quit();
		SpringApplication.exit(ctx);

	}
}

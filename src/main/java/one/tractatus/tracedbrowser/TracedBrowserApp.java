package one.tractatus.tracedbrowser;

import one.tractatus.Cdp;
import io.opentelemetry.api.trace.Span;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@Controller
public class TracedBrowserApp {

	public static ConfigurableApplicationContext ctx;

	@Autowired
	public Environment env;

	public static void main(String[] args) throws InterruptedException {

		ctx=SpringApplication.run(TracedBrowserApp.class, args);
		TracedWindow.awake();

	}
	@GetMapping(
			value = "/application.json",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody byte[] getApplicationJson() throws IOException {
		InputStream in = getClass()
				.getResourceAsStream("/application.json");

		return IOUtils.toByteArray(in);
	}
	@GetMapping(			value = "/appgw.json"	)
	public @ResponseBody byte[] getAppGwJson() {
		String status = "{\"tracing\":" +
				TracedWindow.getTracingStatus()+
				"}";
		return status.getBytes();
	}

	@RequestMapping("/start")
	public String start() {
		Span span= TracedWindow.start();
		return "redirect:/#/";

	}


	@GetMapping("/stop")
	public String stop() {
		TracedWindow.stop();
		return "redirect:/#/";

	}

	@GetMapping("/quit")
	public void   quit() {
		Cdp.chromeDriver.quit();
		SpringApplication.exit(ctx);

	}
}

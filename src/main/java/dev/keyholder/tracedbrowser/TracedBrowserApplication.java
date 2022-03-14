package dev.keyholder.tracedbrowser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.keyholder.ParseMarkdownToHtml.convertMarkdownFileToHTML;

@SpringBootApplication
@RestController
public class TracedBrowserApplication {

	public static void main(String[] args) {

		SpringApplication.run(TracedBrowserApplication.class, args);

	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		//return String.format("Hello %s!", name);
		return convertMarkdownFileToHTML("cockpit.md");
	}

}

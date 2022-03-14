package dev.keyholder;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ParseMarkdownToHtml {
    public static void main(String... args) {
        String markdownValue = "# heading h1\n"
                + "## heading h2\n"
                + "### heading h3\n"
                + "#### heading h4\n"
                + "---";
        String htmlValue = convertMarkdownFileToHTML("README.md");
        //String htmlValue = convertMarkdownToHTML(markdownValue);

        System.out.println("Markdown String:");
        System.out.println(markdownValue);
        System.out.println("HTML String:");
        System.out.println(htmlValue);

    }

    public static String convertMarkdownToHTML(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }
    public static String convertMarkdownFileToHTML(String markdown) {
        Parser parser = Parser.builder().build();

        Node document=null;
        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(markdown), StandardCharsets.UTF_8)) {

            document = parser.parseReader(reader);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        return htmlRenderer.render(document);
    }
}

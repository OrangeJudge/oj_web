package utils;

import org.pegdown.PegDownProcessor;
import org.pegdown.Extensions;

public class Markup {
    public static String fromMarkdown(String mdString) {
        if (mdString == null) mdString = "";
        PegDownProcessor processor = new PegDownProcessor(Extensions.FENCED_CODE_BLOCKS);
        return processor.markdownToHtml(mdString);
    }
}

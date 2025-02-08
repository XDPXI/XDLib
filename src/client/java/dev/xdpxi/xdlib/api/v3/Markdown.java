package dev.xdpxi.xdlib.api.v3;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.commonmark.node.Emphasis;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.parser.Parser;

/**
 * A utility class for parsing Markdown text and converting it to Minecraft's Text format.
 */
public class Markdown {
    private static final Parser parser = Parser.builder().build();

    /**
     * Parses a Markdown string and converts it to Minecraft's Text format.
     *
     * @param markdown The Markdown string to parse.
     * @return A Text object representing the parsed Markdown.
     */
    public static Text parse(String markdown) {
        Node document = parser.parse(markdown);
        return convertNodeToText(document);
    }

    /**
     * Recursively converts a CommonMark Node to Minecraft's Text format.
     *
     * @param node The CommonMark Node to convert.
     * @return A Text object representing the converted Node.
     */
    private static Text convertNodeToText(Node node) {
        if (node instanceof org.commonmark.node.Text) {
            return Text.literal(((org.commonmark.node.Text) node).getLiteral());
        } else if (node instanceof StrongEmphasis) {
            return convertStrongEmphasis((StrongEmphasis) node);
        } else if (node instanceof Emphasis) {
            return convertEmphasis((Emphasis) node);
        } else if (node instanceof Paragraph) {
            return convertParagraph((Paragraph) node);
        }

        MutableText result = Text.empty();
        Node child = node.getFirstChild();
        while (child != null) {
            result.append(convertNodeToText(child));
            child = child.getNext();
        }
        return result;
    }

    /**
     * Converts a StrongEmphasis node to bold Text.
     *
     * @param node The StrongEmphasis node to convert.
     * @return A Text object with bold styling.
     */
    private static Text convertStrongEmphasis(StrongEmphasis node) {
        return Text.literal(convertNodeToText(node.getFirstChild()).getString())
                .styled(style -> style.withBold(true));
    }

    /**
     * Converts an Emphasis node to italic Text.
     *
     * @param node The Emphasis node to convert.
     * @return A Text object with italic styling.
     */
    private static Text convertEmphasis(Emphasis node) {
        return Text.literal(convertNodeToText(node.getFirstChild()).getString())
                .styled(style -> style.withItalic(true));
    }

    /**
     * Converts a Paragraph node to Text, appending a newline character.
     *
     * @param node The Paragraph node to convert.
     * @return A Text object representing the paragraph with a newline.
     */
    private static Text convertParagraph(Paragraph node) {
        return Text.literal(convertNodeToText(node.getFirstChild()).getString() + "\n");
    }
}
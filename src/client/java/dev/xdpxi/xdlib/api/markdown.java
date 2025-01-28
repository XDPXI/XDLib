package dev.xdpxi.xdlib.api;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.commonmark.node.Emphasis;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.parser.Parser;

public class markdown {
    private static final Parser parser = Parser.builder().build();

    public static Text parse(String markdown) {
        Node document = parser.parse(markdown);
        return convertNodeToText(document);
    }

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

    private static Text convertStrongEmphasis(StrongEmphasis node) {
        return Text.literal(convertNodeToText(node.getFirstChild()).getString())
                .styled(style -> style.withBold(true));
    }

    private static Text convertEmphasis(Emphasis node) {
        return Text.literal(convertNodeToText(node.getFirstChild()).getString())
                .styled(style -> style.withItalic(true));
    }

    private static Text convertParagraph(Paragraph node) {
        return Text.literal(convertNodeToText(node.getFirstChild()).getString() + "\n");
    }
}
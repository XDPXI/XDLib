package dev.xdpxi.xdlib.api.mod.render.popupClass;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class view extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private final Screen parent;
    private final MutableText changelogText;

    public view(Text text, Screen parent, String title, String description) {
        super(text);
        this.parent = parent;
        this.changelogText = createChangelogText(title, description);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2 - 20;

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("Dismiss"), (btn) -> {
            close();
        }).dimensions(centerX - 60, centerY + 80, 120, 20).build();
        this.addDrawableChild(buttonWidget);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int buttonY = this.height / 2;

        if (changelogText != null) {
            int boxWidth = 400;
            int boxHeight = 175;
            int boxX = centerX - boxWidth / 2;
            int boxY = buttonY - boxHeight / 2 - 20;

            renderChangelogBox(context, boxX, boxY);
        } else {
            LOGGER.error("Changelog text is null. Skipping changelog rendering.");
        }
    }

    private void renderChangelogBox(DrawContext context, int x, int y) {
        int boxWidth = 400;
        int boxHeight = 175;

        context.fill(x, y, x + boxWidth, y + boxHeight, 0xFF333333);
        context.drawBorder(x, y, boxWidth, boxHeight, 0xFF000000);

        if (this.textRenderer != null && this.changelogText != null) {
            context.drawTextWrapped(this.textRenderer, this.changelogText, x + 5, y + 5, boxWidth - 10, 0xFFFFFFFF);
        }
    }

    private MutableText createChangelogText(String title, String description) {
        MutableText text = Text.literal(title + "\n\n")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true));

        text.append(Text.literal(description).setStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(false)));

        return text;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
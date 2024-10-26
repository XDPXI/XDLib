package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.api.loader;
import dev.xdpxi.xdlib.config.configHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private static boolean shownToast = false;
    private final Screen parent;
    private final MutableText changelogText;

    public CustomScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
        this.changelogText = createChangelogText();
    }

    @Override
    protected void init() {
        if (loader.isModLoaded("cloth-config")) {
            if (!configHelper.isTitlePopupsDisabled()) {
                showPopups();
            }
        } else {
            showPopups();
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2 - 20;

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.literal("Dismiss"), (btn) -> {
            close();
        }).dimensions(centerX - 60, centerY + 170, 120, 20).build();
        this.addDrawableChild(buttonWidget);
    }

    private void showPopups() {
        if (!shownToast) {
            shownToast = true;
            if (loader.isModLoaded("iris") || loader.isModLoaded("optifine")) {
                LOGGER.warn("[XDLib] - Does not support shaders!");
                this.client.getToastManager().add(
                        new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of("XD's Library"), Text.of("Does not support shaders!"))
                );
            }
            if (!loader.isModLoaded("cloth-config")) {
                LOGGER.warn("[XDLib] - Recommends the use of 'cloth-config'");
                this.client.getToastManager().add(
                        new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of("XD's Library"), Text.of("Recommends the use of 'cloth-config'"))
                );
            }
            if (updateChecker.isUpdate()) {
                this.client.getToastManager().add(
                        new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of("XD's Library"), Text.of("An update is available!"))
                );
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null || this.textRenderer == null) {
            return;
        }

        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int buttonY = this.height / 2;
        if (changelogText != null) {
            int boxWidth = 625;
            int boxHeight = 350;
            int boxX = centerX - boxWidth / 2;
            int boxY = buttonY - boxHeight / 2;

            context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF333333);
            context.drawBorder(boxX, boxY, boxWidth, boxHeight, 0xFF000000);

            if (this.textRenderer != null) {
                context.drawTextWrapped(this.textRenderer, this.changelogText, boxX + 5, boxY + 5, boxWidth - 10, 0xFFFFFFFF);
            }
        } else {
            LOGGER.error("Changelog text is null. Skipping changelog rendering.");
        }
    }

    private MutableText createChangelogText() {
        MutableText text = Text.literal("Changelog:\n\n")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(true));

        text.append(Text.literal("- \n").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(false)));

        return text;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
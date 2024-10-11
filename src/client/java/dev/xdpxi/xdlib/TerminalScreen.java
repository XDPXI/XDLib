package dev.xdpxi.xdlib;

import bsh.Interpreter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class TerminalScreen extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private static final int TITLE_Y_OFFSET = 10;
    private static final int CODE_INPUT_Y_OFFSET = 10;
    private TextFieldWidget codeInput;

    public TerminalScreen() {
        super(Text.literal("Code Terminal"));
    }

    @Override
    protected void init() {
        this.codeInput = new TextFieldWidget(this.textRenderer, 10, this.height - CODE_INPUT_Y_OFFSET - 20, this.width - 20, 20, Text.literal(""));
        this.codeInput.setMaxLength(Integer.MAX_VALUE);
        this.addSelectableChild(this.codeInput);
        this.setFocused(this.codeInput);
    }

    private void disabled() {
        this.client.getToastManager().add(
                new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of("XD's Library"), Text.of("This feature is disabled!"))
        );
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 0;
        }).thenAcceptAsync(result -> {
            close();
        }, MinecraftClient.getInstance());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && Screen.hasShiftDown()) {
            runCode(codeInput.getText());
            this.client.setScreen(null);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            codeInput.setText(codeInput.getText() + "\n");
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        int titleWidth = this.textRenderer.getWidth("Terminal");
        int titleX = (this.width - titleWidth) / 2;
        context.drawText(this.textRenderer, Text.literal("Terminal"), titleX, TITLE_Y_OFFSET, 0xFFFFFF, false);
        this.codeInput.render(context, mouseX, mouseY, delta);

        disabled();
    }

    private void runCode(String code) {
        try {
            Interpreter interpreter = new Interpreter();
            try {
                interpreter.eval(code);
            } catch (Exception e) {
                LOGGER.error("Error executing script: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Error executing script: " + e.getMessage());
        }
    }
}
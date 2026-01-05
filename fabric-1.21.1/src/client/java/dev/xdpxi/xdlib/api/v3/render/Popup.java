package dev.xdpxi.xdlib.api.v3.render;

import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Utility class for displaying popup messages in Minecraft.
 */
public class Popup {

    /**
     * Shows a popup message with the given title and description.
     * The popup is displayed after a 2-second delay and is executed on the main Minecraft thread.
     *
     * @param title       The title of the popup message.
     * @param description The description or content of the popup message.
     */
    public static void show(String title, String description) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 0;
        }).thenAcceptAsync(
            result -> {
                MinecraftClient client = MinecraftClient.getInstance();
                Screen currentScreen = client.currentScreen;
                client.execute(() ->
                    client.setScreen(
                        new PopupView(
                            Text.empty(),
                            currentScreen,
                            title,
                            description
                        )
                    )
                );
            },
            MinecraftClient.getInstance()
        );
    }
}

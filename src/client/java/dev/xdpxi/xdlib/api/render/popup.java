package dev.xdpxi.xdlib.api.render;

import dev.xdpxi.xdlib.api.render.popupClass.view;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class popup {
    public popup(String title, String description) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 0;
        }).thenAcceptAsync(result -> {
            MinecraftClient client = MinecraftClient.getInstance();
            Screen currentScreen = client.currentScreen;
            client.execute(() -> client.setScreen(new view(Text.empty(), currentScreen, title, description)));
        }, MinecraftClient.getInstance());
    }
}
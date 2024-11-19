package dev.xdpxi.xdlib.mixin.client;

import dev.xdpxi.xdlib.support;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Unique
    private static boolean Shown = false;

    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        if (!Shown) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }).thenAcceptAsync(result -> {
                Shown = true;
                MinecraftClient client = MinecraftClient.getInstance();
                Screen currentScreen = client.currentScreen;
                client.execute(() -> client.setScreen(new support(Text.empty(), currentScreen)));
            }, MinecraftClient.getInstance());
        }
    }
}
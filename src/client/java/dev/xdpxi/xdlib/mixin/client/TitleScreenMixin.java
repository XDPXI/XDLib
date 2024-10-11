package dev.xdpxi.xdlib.mixin.client;

import dev.xdpxi.xdlib.CustomScreen;
import dev.xdpxi.xdlib.api.loader;
import dev.xdpxi.xdlib.config.changelogConfig;
import dev.xdpxi.xdlib.config.configHelper;
import dev.xdpxi.xdlib.support;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    private static boolean Shown = false;

    @Unique
    private static boolean ShownConfig = false;

    @Unique
    private static boolean clothConfig = loader.isModLoaded("cloth-config");

    @Unique
    private static boolean changelogEveryStartup = false;

    @Unique
    private static int version = 8;

    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        changelogConfig config = new changelogConfig();
        changelogConfig.ConfigData loadedData = config.read();
        int configVersion = loadedData.getVersionInt();
        boolean firstStartup = version != configVersion;

        if (clothConfig) {
            configHelper.ConfigSettings settings = configHelper.titleScreenMixinConfig();
            changelogEveryStartup = settings.changelogEveryStartup;
        }

        if ((firstStartup || changelogEveryStartup) && !Shown) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }).thenAcceptAsync(result -> {
                Shown = true;
                if (clothConfig) {
                    configHelper.updateTitleScreenMixinConfig(false, changelogEveryStartup);
                }
                changelogConfig.ConfigData configData = config.read();
                configData.setVersionInt(version);
                config.write(configData);

                MinecraftClient client = MinecraftClient.getInstance();
                Screen currentScreen = client.currentScreen;

                client.execute(() -> client.setScreen(new CustomScreen(Text.empty(), currentScreen)));
            }, MinecraftClient.getInstance());
        } else {
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
}
package dev.xdpxi.xdlib.mixin;

import dev.xdpxi.xdlib.XDsLibrary;
import net.minecraft.server.dedicated.EulaReader;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@Mixin(EulaReader.class)
public abstract class EulaMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private Path eulaFile;

    @Shadow
    protected abstract boolean checkEulaAgreement();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/EulaReader;checkEulaAgreement()Z"))
    private boolean init(EulaReader instance) {
        if (XDsLibrary.eulaAccepted || checkEulaAgreement()) return true;

        LOGGER.warn("Please indicate your agreement to the minecraft EULA (https://aka.ms/MinecraftEULA)");
        LOGGER.warn("Agree [Y/n]: ");

        String input;
        try (Scanner scanner = new Scanner(System.in)) {
            input = scanner.nextLine().toLowerCase().replace(" ", "");
        }
        if (List.of(new String[]{"n", "no"}).contains(input)) return false;

        try (OutputStream outputStream = Files.newOutputStream(eulaFile)) {
            Properties properties = new Properties();
            properties.setProperty("eula", "true");
            properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://aka.ms/MinecraftEULA).");
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.config.configManager;
import dev.xdpxi.xdlib.config.pluginManager;
import dev.xdpxi.xdlib.gui.PreLaunchWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.resource.InputSupplier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XDsLibraryClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private static final float ADDITIONAL_CLOUD_HEIGHT = 3.0F;
    private static final float GRADIENT_HEIGHT = 6.0F;
    private static final float INVERTED_GRADIENT_HEIGHT = 1.0F / GRADIENT_HEIGHT;
    public static int duration = -1;
    public static List<HostileEntity> list = new ArrayList<>();
    public static Map<String, Float> WorldCloudHeights = new HashMap<>();
    public configManager CONFIG_MANAGER;

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static float getCloudHeight(World world) {
        if (world.isClient) {
            return getCloudHeightClient(world);
        } else {
            String regKey = world.getRegistryKey().getValue().toString();
            return WorldCloudHeights.getOrDefault(regKey, Float.MAX_VALUE);
        }
    }

    @Environment(EnvType.CLIENT)
    private static float getCloudHeightClient(World world) {
        if (world instanceof ClientWorld clientWorld) {
            return clientWorld.getDimensionEffects().getCloudsHeight();
        }
        return world.getBottomY();
    }

    @Environment(EnvType.CLIENT)
    public static float getRainGradient(World world, float original) {
        if (MinecraftClient.getInstance().cameraEntity != null) {
            double playerY = MinecraftClient.getInstance().cameraEntity.getPos().y;
            float cloudY = XDsLibraryClient.getCloudHeight(world) + ADDITIONAL_CLOUD_HEIGHT;

            if (playerY < cloudY - GRADIENT_HEIGHT) {
                // normal
            } else if (playerY < cloudY) {
                return (float) ((cloudY - playerY) * INVERTED_GRADIENT_HEIGHT) * original;
            } else {
                return 0.0F;
            }

        }
        return original;
    }

    public static void setIcon(InputSupplier<InputStream> icon) {
        ((IconSetter) MinecraftClient.getInstance()).ztrolixLibs$setIcon(icon);
    }

    public static void resetIcon() {
        ((IconSetter) MinecraftClient.getInstance()).ztrolixLibs$resetIcon();
    }

    public static void setIcon(NativeImage nativeImage) {
        try {
            byte[] bytes = nativeImage.getBytes();
            setIcon(bytes);
        } catch (IOException e) {
            LOGGER.error("Could not set icon: ", e);
        } finally {
            nativeImage.close();
        }
    }

    public static void setIcon(byte[] favicon) {
        if (favicon == null) resetIcon();
        else setIcon(() -> new ByteArrayInputStream(favicon));
    }

    public boolean isModLoaded(String modID) {
        return FabricLoader.getInstance().isModLoaded(modID);
    }

    public boolean isNoEarlyLoaders() {
        return !(isModLoaded("early-loading-screen") ||
                isModLoaded("early_loading_bar") ||
                isModLoaded("earlyloadingscreen") ||
                isModLoaded("mindful-loading-info") ||
                isModLoaded("neoforge") ||
                isModLoaded("connector") ||
                isModLoaded("mod-loading-screen"));
    }

    @Override
    public void onInitializeClient() {
        configManager.registerConfig();
        CONFIG_MANAGER = new configManager();

        try {
            pluginManager.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                StatusEffectInstance darknessEffect = client.player.getStatusEffect(StatusEffects.DARKNESS);
                if (darknessEffect != null) {
                    client.player.removeStatusEffect(StatusEffects.DARKNESS);
                }
            }
        });

        if (isWindows() && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && isNoEarlyLoaders()) {
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> PreLaunchWindow.remove());
        }

        if (WorldCloudHeights.isEmpty()) {
            WorldCloudHeights.put("minecraft:overworld", 182.0F);
        }
    }
}
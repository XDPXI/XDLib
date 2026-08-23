package dev.xdpxi.xdlib.api.v7;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "xdlib", value = Dist.CLIENT)
public class WindowProvider {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    init();
  }

  public static void init() {
    dev.xdpxi.xdlib.api.v7.Window.setProvider(
        () -> {
          Minecraft client = Minecraft.getInstance();

          Window window = client.getWindow();
          return new dev.xdpxi.xdlib.api.v7.Window.WindowData() {
            @Override
            public void setTitle(String title) {
              try {
                java.lang.reflect.Field handleField = Window.class.getDeclaredField("handle");
                handleField.setAccessible(true);
                long handle = handleField.getLong(window);
                org.lwjgl.glfw.GLFW.glfwSetWindowTitle(handle, title);
              } catch (Exception ex) {
              }
            }

            @Override
            public void setIcon(byte[] icon) {}

            @Override
            public int getX() {
              return window.getX();
            }

            @Override
            public int getY() {
              return window.getY();
            }

            @Override
            public int getWidth() {
              return window.getWidth();
            }

            @Override
            public int getHeight() {
              return window.getHeight();
            }
          };
        });
  }
}

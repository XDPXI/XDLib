package dev.xdpxi.xdlib.api.v7;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

public class WindowProvider {
    public static void init() {
        dev.xdpxi.xdlib.api.v7.Window.setProvider(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.getWindow() == null) {
                return null;
            }

            Window window = client.getWindow();
            return new dev.xdpxi.xdlib.api.v7.Window.WindowData() {
                @Override
                public void setTitle(String title) {
                    try {
                        java.lang.reflect.Field handleField = Window.class.getDeclaredField("handle");
                        handleField.setAccessible(true);
                        long handle = handleField.getLong(window);
                        GLFW.glfwSetWindowTitle(handle, title);
                    } catch (Exception ex) {
                    }
                }

                @Override
                public void setIcon(byte[] icon) {
                }

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

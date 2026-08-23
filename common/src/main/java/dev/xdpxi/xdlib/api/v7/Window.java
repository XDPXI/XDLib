package dev.xdpxi.xdlib.api.v7;

import java.util.Optional;
import java.util.function.Supplier;

public class Window {
  private static Supplier<WindowData> provider = () -> null;

  private Window() {}

  public static void setProvider(Supplier<WindowData> windowProvider) {
    provider = windowProvider;
  }

  public static Optional<WindowData> get() {
    return Optional.ofNullable(provider.get());
  }

  public static void setTitle(String title) {
    get().ifPresent(data -> data.setTitle(title));
  }

  public static void setIcon(byte[] icon) {
    get().ifPresent(data -> data.setIcon(icon));
  }

  public static int getX() {
    return get().map(WindowData::getX).orElse(0);
  }

  public static int getY() {
    return get().map(WindowData::getY).orElse(0);
  }

  public static int getWidth() {
    return get().map(WindowData::getWidth).orElse(0);
  }

  public static int getHeight() {
    return get().map(WindowData::getHeight).orElse(0);
  }

  public interface WindowData {
    void setTitle(String title);

    void setIcon(byte[] icon);

    int getX();

    int getY();

    int getWidth();

    int getHeight();
  }
}

package dev.xdpxi.xdlib;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef;

public class DarkUtils {
    private static final int DWMWA_USE_IMMERSIVE_DARK_MODE = 20;

    public static void enableImmersiveDarkMode(long i_hwnd, boolean enabled) {
        if (!isCompatible()) {
            XDsLibraryClient.LOGGER.info("Dark title bar is not compatible.");
            return;
        }
        int useImmersiveDarkMode = enabled ? 1 : 0;
        WinDef.HWND hWnd = new WinDef.HWND(com.sun.jna.Pointer.createConstant((int) i_hwnd));
        Dwmapi.INSTANCE.DwmSetWindowAttribute(hWnd, DWMWA_USE_IMMERSIVE_DARK_MODE, new int[]{useImmersiveDarkMode}, Integer.BYTES);
    }

    public static boolean isCompatible() {
        if (!Platform.isWindows()) {
            XDsLibraryClient.LOGGER.warn("Not windows");
            return false;
        }
        if (!WindowsVersionHelper.isWindows11Build22000OrHigher()) {
            XDsLibraryClient.LOGGER.warn("At least win 11 build 22000 is required for dark window title bars.");
            return false;
        }
        return true;
    }
}
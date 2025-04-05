/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.jthemedetecor;

import java.awt.Color;

import com.jthemedetecor.consumers.DarkModeConsumer;
import com.jthemedetecor.consumers.PrimaryColorConsumer;
import com.jthemedetecor.consumers.ThemingConsumer;
import com.jthemedetecor.util.ConcurrentHashSet;
import com.sun.jna.platform.win32.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

/**
 * Determines the dark/light theme by the windows registry values through JNA.
 * Works on a Windows 10 system.
 *
 * @author Daniel Gyorffy
 * @author airsquared
 */
class WindowsThemeDetector extends OsThemeDetector {

    private static final Logger logger = LoggerFactory.getLogger(WindowsThemeDetector.class);

    private static final String DARK_MODE_REGISTRY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
    private static final String ACCENT_COLOR_REGISTRY_PATH = "Software\\Microsoft\\Windows\\DWM";
    private static final String ACCENT_COLOR_REGISTRY_VALUE = "AccentColor";
    private static final String DARK_MODE_REGISTRY_VALUE = "AppsUseLightTheme";

    private final Set<ThemingConsumer<?>> listeners = new ConcurrentHashSet<>();
    private volatile DetectorThread detectorThread;

    WindowsThemeDetector() {
    }

    @Override
    public boolean isDark() {
        return Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, DARK_MODE_REGISTRY_PATH, DARK_MODE_REGISTRY_VALUE) &&
                Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, DARK_MODE_REGISTRY_PATH, DARK_MODE_REGISTRY_VALUE) == 0;
    }

    @Override
    public Color getPrimaryColor() {
        if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, ACCENT_COLOR_REGISTRY_PATH, ACCENT_COLOR_REGISTRY_VALUE)) {
            var color = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, ACCENT_COLOR_REGISTRY_PATH, ACCENT_COLOR_REGISTRY_VALUE);
            return new java.awt.Color(color);
        } else {
            return new java.awt.Color(0x673AB7);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public synchronized void registerListener(@NotNull ThemingConsumer<?>  darkThemeListener) {
        Objects.requireNonNull(darkThemeListener);
        final boolean listenerAdded = listeners.add(darkThemeListener);
        final boolean singleListener = listenerAdded && listeners.size() == 1;
        final DetectorThread currentDetectorThread = detectorThread;
        final boolean threadInterrupted = currentDetectorThread != null && currentDetectorThread.isInterrupted();

        if (singleListener || threadInterrupted) {
            final DetectorThread newDetectorThread = new DetectorThread(this);
            this.detectorThread = newDetectorThread;
            newDetectorThread.start();
        }
    }

    @Override
    public synchronized void removeListener(@Nullable ThemingConsumer<?> darkThemeListener) {
        listeners.remove(darkThemeListener);
        if (listeners.isEmpty()) {
            this.detectorThread.interrupt();
            this.detectorThread = null;
        }
    }


    /**
     * Thread implementation for detecting the theme changes
     */
    private static final class DetectorThread extends Thread {

        private final WindowsThemeDetector themeDetector;

        private boolean lastDarkModeValue;
        private Color lastColorValue;

        DetectorThread(WindowsThemeDetector themeDetector) {
            this.themeDetector = themeDetector;
            this.lastDarkModeValue = themeDetector.isDark();
            this.lastColorValue = themeDetector.getPrimaryColor();
            this.setName("Windows 10 Theme Detector Thread");
            this.setDaemon(true);
            this.setPriority(Thread.NORM_PRIORITY - 1);
        }

        @Override
        public void run() {
            WinReg.HKEYByReference colorHkey = new WinReg.HKEYByReference();

            WinReg.HKEYByReference darkModeHkey = new WinReg.HKEYByReference();

            int darkModeErr = Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CURRENT_USER, DARK_MODE_REGISTRY_PATH, 0, WinNT.KEY_READ, darkModeHkey);
            int colorErr = Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CURRENT_USER, ACCENT_COLOR_REGISTRY_PATH, 0, WinNT.KEY_READ, colorHkey);
            if (darkModeErr != W32Errors.ERROR_SUCCESS) {
                throw new Win32Exception(darkModeErr);
            } else if (colorErr != W32Errors.ERROR_SUCCESS) {
                throw new Win32Exception(colorErr);
            }

            while (!this.isInterrupted()) {
                darkModeErr = Advapi32.INSTANCE.RegNotifyChangeKeyValue(darkModeHkey.getValue(), false, WinNT.REG_NOTIFY_CHANGE_LAST_SET, null, false);
                colorErr = Advapi32.INSTANCE.RegNotifyChangeKeyValue(colorHkey.getValue(), false, WinNT.REG_NOTIFY_CHANGE_LAST_SET, null, false);

                if (darkModeErr != W32Errors.ERROR_SUCCESS) {
                    throw new Win32Exception(darkModeErr);
                } else if (colorErr != W32Errors.ERROR_SUCCESS) {
                    throw new Win32Exception(colorErr);
                }

                boolean currentDarkModeDetection = themeDetector.isDark();
                Color currentColorDetection = themeDetector.getPrimaryColor();

                if (currentDarkModeDetection != this.lastDarkModeValue) {
                    lastDarkModeValue = currentDarkModeDetection;
                    logger.debug("Theme change detected: dark: {}", currentDarkModeDetection);
                    for (ThemingConsumer<?> listener : themeDetector.listeners) {
                        try {
                            switch (listener) {
                                case DarkModeConsumer darkModeConsumer -> {
                                    darkModeConsumer.accept(currentDarkModeDetection);
                                }
                                case PrimaryColorConsumer primaryColorConsumer -> {
                                }
                            }

                        } catch (RuntimeException e) {
                            logger.error("Caught exception during listener notifying ", e);
                        }
                    }
                }

                if (currentColorDetection != this.lastColorValue) {
                    this.lastColorValue = currentColorDetection;
                    logger.debug("Theme change detected: dark: {}", currentColorDetection);
                    for (ThemingConsumer<?> listener : themeDetector.listeners) {
                        try {
                            switch (listener) {
                                case DarkModeConsumer darkModeConsumer -> {
                                }
                                case PrimaryColorConsumer primaryColorConsumer -> {
                                    primaryColorConsumer.accept(currentColorDetection);
                                }
                            }

                        } catch (RuntimeException e) {
                            logger.error("Caught exception during listener notifying ", e);
                        }
                    }
                }

            }
            Advapi32Util.registryCloseKey(colorHkey.getValue());
            Advapi32Util.registryCloseKey(darkModeHkey.getValue());
        }
    }
}

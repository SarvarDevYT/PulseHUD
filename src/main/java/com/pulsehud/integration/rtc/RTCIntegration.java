package com.pulsehud.integration.rtc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

public class RTCIntegration {
    private static boolean initialized = false;
    private static boolean available = false;
    private static Class<?> apiClass;
    private static Method getClocksMethod;

    public static class ClockEntry {
        public String label;
        public String formattedTime;
        public String timezone;

        public ClockEntry(String label, String formattedTime, String timezone) {
            this.label = label;
            this.formattedTime = formattedTime;
            this.timezone = timezone;
        }
    }

    public static boolean isAvailable() {
        if (!initialized) {
            initialized = true;
            if (FabricLoader.getInstance().isModLoaded("realtimeclockuz")) {
                try {
                    // Reflectively bind to the RealTimeClockUz API
                    apiClass = Class.forName("com.realtimeclockuz.api.RTCAPI");
                    getClocksMethod = apiClass.getMethod("getClocks");
                    available = true;
                    System.out.println("[PulseHUD] RealTimeClockUz integration successfully initialized!");
                } catch (Exception e) {
                    System.err.println("[PulseHUD] Optional RealTimeClockUz API classes not found or failed to load: " + e.getMessage());
                    available = false;
                }
            } else {
                available = false;
            }
        }
        return available;
    }

    @SuppressWarnings("unchecked")
    public static List<ClockEntry> getClocks() {
        List<ClockEntry> entries = new ArrayList<>();
        if (!isAvailable()) {
            return entries;
        }
        try {
            List<?> rawClocks = (List<?>) getClocksMethod.invoke(null);
            if (rawClocks != null) {
                for (Object obj : rawClocks) {
                    Class<?> clazz = obj.getClass();
                    Method getLabel = clazz.getMethod("getLabel");
                    Method getFormattedTime = clazz.getMethod("getFormattedTime");
                    Method getTimezone = clazz.getMethod("getTimezone");

                    String label = (String) getLabel.invoke(obj);
                    String time = (String) getFormattedTime.invoke(obj);
                    String tz = (String) getTimezone.invoke(obj);

                    entries.add(new ClockEntry(label, time, tz));
                }
            }
        } catch (Exception e) {
            System.err.println("[PulseHUD] Error executing RealTimeClockUz API reflections: " + e.getMessage());
        }
        return entries;
    }
}

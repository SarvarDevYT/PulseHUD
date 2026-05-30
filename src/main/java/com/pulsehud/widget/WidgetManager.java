package com.pulsehud.widget;

import com.pulsehud.config.PulseConfig;
import com.pulsehud.config.PulseConfigManager;
import com.pulsehud.theme.ThemeManager;
import com.pulsehud.widget.impl.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetManager {
    private static final List<Widget> WIDGETS = new ArrayList<>();
    private static final Map<String, int[]> hudBounds = new HashMap<>();
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 1000;

    private static Widget draggedWidget = null;
    private static String draggedHudId = null;
    private static int dragOffsetX, dragOffsetY;
    private static boolean wasDragged = false;
    private static boolean isResizing = false;

    public static void reportHudBounds(String id, int x, int y, int w, int h) {
        hudBounds.put(id, new int[]{ x, y, w, h });
    }

    public static void init() {
        WIDGETS.clear();
        WIDGETS.add(new FPSWidget());
        WIDGETS.add(new PingWidget());
        WIDGETS.add(new CoordinatesWidget());
        WIDGETS.add(new CompassWidget());
        WIDGETS.add(new ClockWidget());
        WIDGETS.add(new SessionStatsWidget());

        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("realtimeclockuz")) {
            WIDGETS.add(new com.pulsehud.widget.impl.RTCWidget());
        }

        WIDGETS.add(new SelectedItemWidget());
        WIDGETS.add(new PotionEffectsWidget());
        WIDGETS.add(new BiomeWidget());
    }

    public static void registerWidget(Widget widget) {
        if (!WIDGETS.contains(widget)) {
            WIDGETS.add(widget);
        }
    }

    public static void renderAll(DrawContext context, float tickDelta, int mouseX, int mouseY) {
        PulseConfig config = PulseConfigManager.getConfig();
        if (!config.enabled) return;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        long now = System.currentTimeMillis();
        boolean shouldUpdate = (now - lastUpdateTime >= UPDATE_INTERVAL_MS);
        if (shouldUpdate) {
            lastUpdateTime = now;
        }

        if (config.editMode) {
            handleDrag(context, screenWidth, screenHeight);
        }

        for (Widget widget : WIDGETS) {
            if (widget.isEnabled(config)) {
                widget.resolvePosition(screenWidth, screenHeight);
                if (shouldUpdate && widget != draggedWidget) {
                    widget.updateData();
                }
                widget.render(context, tickDelta, mouseX, mouseY);
                if (config.editMode) {
                    drawEditOverlay(context, widget.x, widget.y, widget.width, widget.height, widget.id);
                }
            }
        }

        if (config.editMode) {
            for (Map.Entry<String, int[]> entry : hudBounds.entrySet()) {
                int[] b = entry.getValue();
                drawEditOverlay(context, b[0], b[1], b[2], b[3], entry.getKey());
            }
            drawEditModeText(context, screenWidth, screenHeight);
        }
    }

    private static void handleDrag(DrawContext context, int screenWidth, int screenHeight) {
        if (MinecraftClient.getInstance().mouse.isCursorLocked()) {
            if (draggedWidget != null && wasDragged) PulseConfigManager.save();
            if (draggedHudId != null && wasDragged) PulseConfigManager.save();
            draggedWidget = null;
            draggedHudId = null;
            isResizing = false;
            MinecraftClient.getInstance().mouse.unlockCursor();
            return;
        }

        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        boolean leftDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        double scaleX = (double) context.getScaledWindowWidth() / MinecraftClient.getInstance().getWindow().getWidth();
        double scaleY = (double) context.getScaledWindowHeight() / MinecraftClient.getInstance().getWindow().getHeight();
        int mouseX = (int) (MinecraftClient.getInstance().mouse.getX() * scaleX);
        int mouseY = (int) (MinecraftClient.getInstance().mouse.getY() * scaleY);

        if (leftDown) {
            if (draggedWidget == null && draggedHudId == null) {
                // Check resize handles first
                for (Widget widget : WIDGETS) {
                    if (!widget.isEnabled(PulseConfigManager.getConfig())) continue;
                    int rx = widget.x + widget.width - 8;
                    int ry = widget.y + widget.height - 8;
                    int rh = 12;
                    if (mouseX >= rx && mouseX <= rx + rh && mouseY >= ry && mouseY <= ry + rh) {
                        draggedWidget = widget;
                        isResizing = true;
                        dragOffsetX = mouseX;
                        dragOffsetY = mouseY;
                        wasDragged = false;
                        break;
                    }
                }
                // Check regular drag if not resizing
                if (draggedWidget == null) {
                    for (Widget widget : WIDGETS) {
                        if (widget.isEnabled(PulseConfigManager.getConfig()) && widget.isMouseOver(mouseX, mouseY)) {
                            draggedWidget = widget;
                            isResizing = false;
                            dragOffsetX = mouseX - widget.x;
                            dragOffsetY = mouseY - widget.y;
                            wasDragged = false;
                            break;
                        }
                    }
                }
                // Check HUD elements
                if (draggedWidget == null) {
                    for (Map.Entry<String, int[]> entry : hudBounds.entrySet()) {
                        int[] b = entry.getValue();
                        if (mouseX >= b[0] && mouseX <= b[0] + b[2] && mouseY >= b[1] && mouseY <= b[1] + b[3]) {
                            draggedHudId = entry.getKey();
                            dragOffsetX = mouseX - b[0];
                            dragOffsetY = mouseY - b[1];
                            wasDragged = false;
                            break;
                        }
                    }
                }
            } else if (isResizing && draggedWidget != null) {
                int newW = Math.max(20, mouseX - draggedWidget.x);
                int newH = Math.max(16, mouseY - draggedWidget.y);
                if (Math.abs(newW - draggedWidget.width) > 1 || Math.abs(newH - draggedWidget.height) > 1) {
                    wasDragged = true;
                }
                draggedWidget.setSize(newW, newH);
            } else if (draggedWidget != null) {
                int newX = mouseX - dragOffsetX;
                int newY = mouseY - dragOffsetY;
                if (Math.abs(newX - draggedWidget.x) > 2 || Math.abs(newY - draggedWidget.y) > 2) {
                    wasDragged = true;
                }
                draggedWidget.updatePosition(newX, newY, screenWidth, screenHeight);
            } else if (draggedHudId != null) {
                int[] b = hudBounds.get(draggedHudId);
                int newX = mouseX - dragOffsetX;
                int newY = mouseY - dragOffsetY;
                if (Math.abs(newX - b[0]) > 2 || Math.abs(newY - b[1]) > 2) {
                    wasDragged = true;
                }
                PulseConfig config = PulseConfigManager.getConfig();
                config.positions.put(draggedHudId, new int[]{ newX - screenWidth / 2, newY - screenHeight, 4, b[2], b[3] });
                hudBounds.put(draggedHudId, new int[]{ newX, newY, b[2], b[3] });
            }
        } else {
            if (draggedWidget != null && wasDragged) {
                PulseConfigManager.save();
            }
            if (draggedHudId != null && wasDragged) {
                PulseConfigManager.save();
            }
            draggedWidget = null;
            draggedHudId = null;
            isResizing = false;
        }
    }

    private static void drawEditOverlay(DrawContext context, int x, int y, int w, int h, String id) {
        int c = 0x55FFFF00;
        context.fill(x, y, x + w, y + 1, c);
        context.fill(x, y + h - 1, x + w, y + h, c);
        context.fill(x, y, x + 1, y + h, c);
        context.fill(x + w - 1, y, x + w, y + h, c);

        // Resize handle (bottom-right corner)
        int handleSize = 6;
        context.fill(x + w - handleSize, y + h - handleSize, x + w, y + h, 0xCC00FF00);

        String label = id + " [" + w + "x" + h + "]";
        int txtW = MinecraftClient.getInstance().textRenderer.getWidth(label);
        context.drawText(MinecraftClient.getInstance().textRenderer, label,
            x + w / 2 - txtW / 2, y - 10,
            ThemeManager.getPrimary(), false);
    }

    private static void drawEditModeText(DrawContext context, int screenWidth, int screenHeight) {
        String text = "EDIT MODE [Press I to toggle]";
        int txtW = MinecraftClient.getInstance().textRenderer.getWidth(text);
        context.drawText(MinecraftClient.getInstance().textRenderer, text,
            screenWidth / 2 - txtW / 2, screenHeight - 30,
            0xFFFFAA00, false);
    }

    public static List<Widget> getWidgets() {
        return WIDGETS;
    }
}

package com.featherlite;

import com.featherlite.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import org.lwjgl.glfw.GLFW;

/** Logic for Freelook, Zoom and Toggle Sprint (no HUD). */
public final class Features {
    public static Module freelook, zoom, sprint;

    public static boolean freelookActive;
    public static float flYaw, flPitch;
    /** The player's real aim, frozen while freelook is active. */
    private static float realYaw, realPitch;
    /** True only while Camera.update runs. */
    public static boolean inCamera;
    /** True once vanilla read our freelook angles during this camera update. */
    public static boolean yawUsed;
    private static final java.util.Set<String> LOGGED = new java.util.HashSet<>();

    private static Perspective savedPerspective;
    private static Integer savedFov;
    private static boolean autoSprint;

    private Features() {}

    public static void tick(MinecraftClient mc) {
        boolean inGame = mc.player != null && mc.currentScreen == null;

        // Freelook (hold key)
        boolean wantLook = inGame && freelook.enabled && down(mc, freelook.keyCode);
        if (wantLook && !freelookActive) {
            flYaw = realYaw = mc.player.getYaw();
            flPitch = realPitch = mc.player.getPitch();
            freelookActive = true;
            savedPerspective = mc.options.getPerspective();
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        } else if (!wantLook && freelookActive) {
            stopFreelook(mc);
        }

        // Zoom (hold key)
        boolean wantZoom = inGame && zoom.enabled && down(mc, zoom.keyCode);
        if (wantZoom && savedFov == null) {
            savedFov = mc.options.getFov().getValue();
            mc.options.getFov().setValue(30);
        } else if (!wantZoom && savedFov != null) {
            stopZoom(mc);
        }

        // Toggle sprint
        if (inGame && sprint.enabled) {
            boolean walking = mc.options.forwardKey.isPressed() && !mc.options.sneakKey.isPressed();
            if (walking) {
                mc.options.sprintKey.setPressed(true);
                autoSprint = true;
            } else if (autoSprint) {
                mc.options.sprintKey.setPressed(false);
                autoSprint = false;
            }
        } else if (autoSprint) {
            mc.options.sprintKey.setPressed(false);
            autoSprint = false;
        }
    }

    /** Logs the first time each hook runs, so latest.log shows which hooks work. */
    public static void hook(String name) {
        if (LOGGED.add(name)) System.out.println("[FeatherLite] Freelook hook active: " + name);
    }

    /**
     * Fallback if the mouse hook could not be applied: the mouse turned the
     * player, so move that turn into the camera and restore the real aim.
     * Runs once per frame, right after the mouse is read.
     */
    public static void absorbMouse() {
        if (!freelookActive) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        float dy = mc.player.getYaw() - realYaw;
        float dp = mc.player.getPitch() - realPitch;
        if (dy == 0f && dp == 0f) return;
        flYaw += dy;
        flPitch = Math.max(-90f, Math.min(90f, flPitch + dp));
        mc.player.setYaw(realYaw);
        mc.player.setPitch(realPitch);
    }

    private static boolean down(MinecraftClient mc, int key) {
        return key > 0 && GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    private static void stopFreelook(MinecraftClient mc) {
        freelookActive = false;
        if (savedPerspective != null) mc.options.setPerspective(savedPerspective);
        savedPerspective = null;
    }

    private static void stopZoom(MinecraftClient mc) {
        mc.options.getFov().setValue(savedFov);
        savedFov = null;
    }

    /** Restore options before the game closes. */
    public static void shutdown(MinecraftClient mc) {
        if (freelookActive) stopFreelook(mc);
        if (savedFov != null) stopZoom(mc);
    }
}

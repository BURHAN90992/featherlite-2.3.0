package com.featherlite.gui;

import net.minecraft.client.gui.DrawContext;

/** FeatherLite logo drawn from pre-built rectangles (no textures needed). */
public final class Logo {
    public enum Kind { LARGE, SMALL, MARK }

    private static final int[][] SOLID = new int[3][];
    private static final int[][] SOFT = new int[3][];

    static {
        SOLID[0] = decode(LogoData.LARGE_SOLID);
        SOFT[0] = decode(LogoData.LARGE_SOFT);
        SOLID[1] = decode(LogoData.SMALL_SOLID);
        SOFT[1] = decode(LogoData.SMALL_SOFT);
        SOLID[2] = decode(LogoData.MARK_SOLID);
        SOFT[2] = decode(LogoData.MARK_SOFT);
    }

    private Logo() {}

    private static int[] decode(String[] chunks) {
        String s = String.join("", chunks);
        int[] out = new int[s.length()];
        for (int i = 0; i < out.length; i++) out[i] = s.charAt(i) - 0x2000;
        return out;
    }

    public static int width(Kind k) {
        return switch (k) {
            case LARGE -> LogoData.LARGE_W;
            case SMALL -> LogoData.SMALL_W;
            case MARK -> LogoData.MARK_W;
        };
    }

    public static int height(Kind k) {
        return switch (k) {
            case LARGE -> LogoData.LARGE_H;
            case SMALL -> LogoData.SMALL_H;
            case MARK -> LogoData.MARK_H;
        };
    }

    /** Draws the logo with its top-left corner at (x, y), tinted with the given RGB color. */
    public static void draw(DrawContext c, Kind k, int x, int y, int rgb) {
        int i = k.ordinal();
        fill(c, SOFT[i], x, y, (0x70 << 24) | (rgb & 0xFFFFFF));
        fill(c, SOLID[i], x, y, 0xFF000000 | (rgb & 0xFFFFFF));
    }

    private static void fill(DrawContext c, int[] r, int x, int y, int color) {
        for (int j = 0; j + 3 < r.length; j += 4) {
            c.fill(x + r[j], y + r[j + 1], x + r[j] + r[j + 2], y + r[j + 1] + r[j + 3], color);
        }
    }
}

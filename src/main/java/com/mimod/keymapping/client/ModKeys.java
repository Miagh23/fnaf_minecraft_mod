package com.mimod.keymapping.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeys {

    public static final String CATEGORY = "key.categories.mimod";

    public static final KeyMapping NOTE_MI_LOW =
            new KeyMapping("key.mimod.mi_low", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping NOTE_LA =
            new KeyMapping("key.mimod.la", GLFW.GLFW_KEY_X, CATEGORY);
    public static final KeyMapping NOTE_RE =
            new KeyMapping("key.mimod.re", GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping NOTE_SOL =
            new KeyMapping("key.mimod.sol", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping NOTE_SI =
            new KeyMapping("key.mimod.si", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping NOTE_MI_HIGH =
            new KeyMapping("key.mimod.mi_high", GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping LONG_MODIFIER =
            new KeyMapping("key.mimod.long_modifier", GLFW.GLFW_KEY_KP_ENTER, CATEGORY);
}
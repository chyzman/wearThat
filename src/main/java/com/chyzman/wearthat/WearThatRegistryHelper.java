package com.chyzman.wearthat;

import net.minecraft.util.Identifier;

public class WearThatRegistryHelper {

    public static Identifier id(String path) {
        return new Identifier(WearThat.MODID, path);
    }

}
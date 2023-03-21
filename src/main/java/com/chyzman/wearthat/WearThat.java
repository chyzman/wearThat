package com.chyzman.wearthat;

import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.ModInitializer;

import static com.chyzman.wearthat.WearThatRegistryHelper.id;

public class WearThat implements ModInitializer {
    public static final String MODID = "wearthat";
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(id("main"));

    @Override
    public void onInitialize() {
    }
}

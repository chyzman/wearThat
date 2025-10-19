package com.chyzman.wearthat.mixin.compat;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WearThatMixinPlugin implements IMixinConfigPlugin {
    private static final String mixinDir = "mixin com.chyzman.wearthat.mixin.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var stripped = mixinClassName.replace(mixinDir, "");
        if (stripped.startsWith("compat.")) return FabricLoader.getInstance().isModLoaded(stripped.split("\\.")[1]);
        return true;
    }

    //region @formatter:off
    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return ""; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return List.of(); }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    //endregion @formatter:on
}

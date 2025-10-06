package com.chyzman.wearthat.pond;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

public interface ModelPartDuck {
    void wearThat$setTransform(Consumer<MatrixStack> consumer);
}

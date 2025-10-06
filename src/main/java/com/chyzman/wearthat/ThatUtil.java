package com.chyzman.wearthat;

import com.chyzman.wearthat.mixin.ModelPartMixin;
import com.chyzman.wearthat.pond.ModelPartDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ThatUtil {
    @Nullable
    public static Arm renderingArm = null;

    public static <T extends BipedEntityRenderState> void applyFunny(
        BipedEntityModel<T> contextModel,
        BipedEntityModel<T> armorModel,
        EquipmentSlot slot,
        @NotNull EquipmentSlot originalSlot
    ) {
        switch (originalSlot) {
            case HEAD -> {
                armorModel.getRootPart().copyTransform(contextModel.getHead());
                switch (slot) {
                    case CHEST -> {
                        armorModel.body.translate(new Vector3f(0, -20, 0));
                        armorModel.rightArm.translate(new Vector3f(-2.001f, -20, 0));
                        armorModel.leftArm.translate(new Vector3f(2.001f, -20, 0));
                    }
                    case LEGS -> {
                        armorModel.getRootPart().rotate(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    }
                    case FEET -> {
                        armorModel.rightLeg.translate(new Vector3f(-1, -34, 0));
                        armorModel.leftLeg.translate(new Vector3f(1, -34, 0));
                    }
                }
            }
            case CHEST -> {
                switch (slot) {
                    case HEAD -> {
                        armorModel.head.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.head).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                            matrices.scale(1.25f, 1.25f, 1.25f);
                            matrices.translate(0, 4, 4);
                        });
                    }
                    case LEGS -> {
                        //middle part
                        armorModel.body.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.body).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                            matrices.translate(0, -12, 0);
                        });

                        //right leg
                        armorModel.rightLeg.copyTransform(contextModel.rightArm);
                        armorModel.rightLeg.translate(new Vector3f(-1f, 0.1f, 0));

                        //left leg
                        armorModel.leftLeg.copyTransform(contextModel.leftArm);
                        armorModel.leftLeg.translate(new Vector3f(1f, 0.1f, 0));
                    }
                    case FEET -> {
                        //right boot
                        armorModel.rightLeg.copyTransform(contextModel.rightArm);
                        ((ModelPartDuck) (Object) armorModel.rightLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(-1, -1, 0);
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                        });

                        //left boot
                        armorModel.leftLeg.copyTransform(contextModel.leftArm);
                        ((ModelPartDuck) (Object) armorModel.leftLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(1, -1, 0);
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                        });
                    }
                }
            }
            case LEGS -> {
                switch (slot) {
                    case HEAD -> {
                        armorModel.head.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.head).wearThat$setTransform(matrices -> {
                            matrices.translate(0, 20, 0);
                        });
                    }
                    case CHEST -> {
                        //middle part
                        armorModel.body.copyTransform(contextModel.body);
                        ((ModelPartDuck) (Object) armorModel.body).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -12, 0);
                        });

                        //right sleeve
                        armorModel.rightArm.copyTransform(contextModel.rightLeg);

                        //left sleeve
                        armorModel.leftArm.copyTransform(contextModel.leftLeg);
                    }
                    case FEET -> {
                        //right boot
                        armorModel.rightLeg.copyTransform(contextModel.rightLeg);
                        ((ModelPartDuck) (Object) armorModel.rightLeg).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -10, 0);
                        });

                        //left boot
                        armorModel.leftLeg.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.leftLeg).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(0, -10, 0);
                        });
                    }
                }
            }
            case FEET -> {
                switch (slot) {
                    case HEAD -> {

                    }
                    case CHEST -> {
                        //middle part
                        armorModel.body.hidden = true;

                        //right sleeve
                        armorModel.rightArm.copyTransform(contextModel.rightLeg);
                        ((ModelPartDuck) (Object) armorModel.rightArm).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(1, -9, 0);
                        });

                        //left sleeve
                        armorModel.leftArm.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.leftArm).wearThat$setTransform(matrices -> {
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                            matrices.translate(-1, -9, 0);
                        });
                    }
                    case LEGS -> {
                        //middle part
                        armorModel.body.hidden = true;

                        //right leg
                        armorModel.rightLeg.copyTransform(contextModel.rightLeg);
                        ((ModelPartDuck) (Object) armorModel.rightLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(0, 3, 0);
                            matrices.scale(1.001f, 1.001f, 1.001f);
                        });

                        //left leg
                        armorModel.leftLeg.copyTransform(contextModel.leftLeg);
                        ((ModelPartDuck) (Object) armorModel.leftLeg).wearThat$setTransform(matrices -> {
                            matrices.translate(0, 3, 0);
                            matrices.scale(1.001f, 1.001f, 1.001f);
                        });
                    }
                }
            }
        }
    }
}

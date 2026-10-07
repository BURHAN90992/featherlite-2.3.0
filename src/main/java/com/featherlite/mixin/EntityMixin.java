package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Freelook: mouse turns the camera (not the player); the camera reads the freelook angles. */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void featherlite$mouse(double dx, double dy, CallbackInfo ci) {
        if (Features.freelookActive && (Object) this == MinecraftClient.getInstance().player) {
            Features.hook("mouse");
            Features.flYaw += (float) dx * 0.15f;
            Features.flPitch = MathHelper.clamp(Features.flPitch + (float) dy * 0.15f, -90.0f, 90.0f);
            ci.cancel();
        }
    }

    // Only while the camera is being built, so aiming and attacks keep using the real rotation.
    @Inject(method = "getYaw(F)F", at = @At("HEAD"), cancellable = true)
    private void featherlite$cameraYaw(float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Features.inCamera && Features.freelookActive
                && (Object) this == MinecraftClient.getInstance().player) {
            Features.hook("camera-yaw");
            Features.yawUsed = true;
            cir.setReturnValue(Features.flYaw);
        }
    }

    @Inject(method = "getPitch(F)F", at = @At("HEAD"), cancellable = true)
    private void featherlite$cameraPitch(float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Features.inCamera && Features.freelookActive
                && (Object) this == MinecraftClient.getInstance().player) {
            Features.hook("camera-pitch");
            cir.setReturnValue(Features.flPitch);
        }
    }
}

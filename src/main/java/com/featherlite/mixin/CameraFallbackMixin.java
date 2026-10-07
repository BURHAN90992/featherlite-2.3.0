package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freelook, backup path: only used if the player angle hook did not run. */
@Mixin(Camera.class)
public abstract class CameraFallbackMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void featherlite$afterRotation(CallbackInfo ci) {
        if (Features.freelookActive && !Features.yawUsed) {
            Features.hook("camera-fallback-rotation");
            this.setRotation(Features.flYaw, Features.flPitch);
        }
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void featherlite$tail(CallbackInfo ci) {
        if (Features.freelookActive && !Features.yawUsed) {
            Features.hook("camera-fallback-tail");
            this.setRotation(Features.flYaw, Features.flPitch);
        }
    }
}

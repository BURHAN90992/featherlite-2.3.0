package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Freelook, main path: while Camera.update runs, EntityMixin makes the player
 * report the freelook angles, so vanilla builds the whole camera (rotation,
 * third-person orbit and wall clipping) from them.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "update", at = @At("HEAD"))
    private void featherlite$head(CallbackInfo ci) {
        Features.inCamera = true;
        Features.yawUsed = false;
        if (Features.freelookActive) {
            Features.hook("camera-update");
            Features.absorbMouse();
        }
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void featherlite$tail(CallbackInfo ci) {
        Features.inCamera = false;
    }
}

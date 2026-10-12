package dev.kiradiamond.miniwynn.mixin;

import dev.kiradiamond.miniwynn.EmoteMath;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
abstract class AvatarRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void miniwynn$resize(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        float originalScale = state.scale;
        state.scale *= EmoteMath.BODY;
        if (state.nameTagAttachment != null) {
            state.nameTagAttachment = state.nameTagAttachment.scale(EmoteMath.BODY)
                    .add(0, 0.5 * (EmoteMath.HEAD - 1) * state.scale * 0.9375, 0);
        }
        if (entity.isPassenger()) {
            double lift = 0.751 * 0.9375 * originalScale * (1 - EmoteMath.BODY);
            state.passengerOffset = (state.passengerOffset == null ? Vec3.ZERO : state.passengerOffset).add(0, lift, 0);
        }
    }
}

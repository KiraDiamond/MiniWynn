package dev.kiradiamond.miniwynn.mixin;

import dev.kiradiamond.miniwynn.SmallMobVisuals;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class AttachedLabelMixin {
    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("RETURN"))
    private void miniwynn$lowerLabel(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> cir) {
        SmallMobVisuals.lowerAttachedLabel(entity, cir.getReturnValue());
    }
}

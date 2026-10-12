package dev.kiradiamond.miniwynn.mixin;

import dev.kiradiamond.miniwynn.InventoryPreview;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin {
    @Redirect(method = "renderEntityInInventoryFollowsMouse", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;scale:F",
            opcode = Opcodes.PUTFIELD))
    private static void miniwynn$keepPreviewSmall(LivingEntityRenderState state, float scale) {
        InventoryPreview.normalize(state, scale);
    }
}

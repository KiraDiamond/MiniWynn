package dev.kiradiamond.miniwynn.mixin;

import dev.kiradiamond.miniwynn.InventoryPreview;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin {
    @Redirect(method = "renderEntityInInventoryFollowsMouse", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;scale:F",
            opcode = Opcodes.PUTFIELD))
    private static void miniwynn$keepPreviewSmall(LivingEntityRenderState state, float scale) {
        InventoryPreview.normalize(state, scale);
    }

    @ModifyArgs(method = "renderEntityInInventoryFollowsMouse", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;submitEntityRenderState(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIII)V"))
    private static void miniwynn$alignPreviewFeet(Args args) {
        InventoryPreview.alignFeet(args.get(0), args.get(1), args.get(2), args.get(6), args.get(8));
    }
}

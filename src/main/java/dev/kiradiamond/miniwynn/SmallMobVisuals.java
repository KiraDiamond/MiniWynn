package dev.kiradiamond.miniwynn;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.villager.AbstractVillager;

public final class SmallMobVisuals {
    private SmallMobVisuals() {}

    public static void resizeVillager(VillagerRenderState state) {
        state.scale *= EmoteMath.BODY;
        if (state.nameTagAttachment != null) {
            state.nameTagAttachment = state.nameTagAttachment.scale(EmoteMath.BODY)
                    .add(0, headGrowth(0.625, state.scale, state.ageScale), 0);
        }
    }

    public static void lowerAttachedLabel(Entity entity, EntityRenderState state) {
        if (!isLabel(entity)) return;
        Entity parent = entity.getVehicle();
        // Follow the server's passenger link, never the nearest NPC.
        for (int depth = 0; parent != null && depth < 8; depth++, parent = parent.getVehicle()) {
            if (!(parent instanceof Avatar) && !(parent instanceof AbstractVillager)) continue;
            LivingEntity owner = (LivingEntity) parent;
            double head = owner instanceof Avatar ? 0.5 * 0.9375 : 0.625;
            state.y += owner.getBbHeight() * (EmoteMath.BODY - 1)
                    + headGrowth(head, owner.getScale() * EmoteMath.BODY, owner.getAgeScale());
            return;
        }
    }

    private static boolean isLabel(Entity entity) {
        if (entity instanceof Display.TextDisplay) return true;
        if (!(entity instanceof ArmorStand stand) || !stand.isInvisible() || !stand.hasCustomName()) return false;
        for (EquipmentSlot slot : EquipmentSlot.values())
            if (!stand.getItemBySlot(slot).isEmpty()) return false;
        return true;
    }

    private static double headGrowth(double headHeight, float scale, float ageScale) {
        return headHeight * scale * ageScale * (EmoteMath.HEAD - 1);
    }
}

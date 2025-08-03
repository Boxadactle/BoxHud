package dev.boxadactle.boxhud.events;

import net.minecraft.world.entity.Entity;

public interface EntityAttackEvent {

    void onEntityAttack(Entity target);

}

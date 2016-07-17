package com.ichorcommunity.latch.listeners;

import com.ichorcommunity.latch.Latch;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class SpawnEntityListener {

    @Listener
    public void onSpawnLockItem(SpawnEntityEvent event, @Root SpawnCause sc) {
        //This is to prevent item drops from being generated when blocks break from things like Pistons
        //Sponge still drops the item even if the blocksnapshot is invalidated/event is cancelled

        //Piston generates a CUSTOM spawn type
        if( sc.getType() == SpawnTypes.CUSTOM ) {
            //For each of the entities
            for(Entity e : event.getEntities()) {
                //If it's an item and there's a lock there (have to convert item location to block location)
                if(e.getType() == EntityTypes.ITEM && Latch.getLockManager().getLock(e.getLocation().getExtent().getLocation(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ())).isPresent() ) {
                    event.setCancelled(true);
                }
            }
        }
    }
}


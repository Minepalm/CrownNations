package com.minepalm.nations.bukkit.listener.bukkit

import com.minepalm.nations.NationRank
import com.minepalm.nations.NationService
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class NationTeamProtectionListener(
    private val service: NationService
) : Listener{

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerAttack(event: EntityDamageByEntityEvent){
        val entity: Entity = event.entity
        var damager: Entity = event.damager
        if (event.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            val proj = damager as Projectile
            if (proj.shooter is Player) {
                damager = proj.shooter as Player
            }
        }
        if (entity is Player && damager is Player) {
            if(isTeam(entity, damager))
                event.isCancelled = true
        }
    }

    fun isTeam(player: Player, other: Player): Boolean{
        val member = service.memberRegistry.local[player.uniqueId]
        val nation = member?.cache?.nation
        if(member != null && nation != null)
            return nation.cache.getRank(other.uniqueId).hasPermissibleOf(NationRank.RESIDENT)
        else
            return false
    }
}
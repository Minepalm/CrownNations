package kr.rendog.nations.bukkit.listener.bukkit

import kr.rendog.nations.NationMember
import kr.rendog.nations.NationService
import kr.rendog.nations.territory.NationAction
import kr.rendog.nations.territory.NationMonument
import kr.rendog.nations.utils.ServerLoc
import org.bukkit.Location;
import org.bukkit.Material.*
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Door


class NationTerritoryProtectionListener(
    private val service: NationService
) : Listener{

    private val currentServer = service.network.host.name

    private fun Location.parse(): ServerLoc{
        return ServerLoc(currentServer, world.name, blockX, blockY, blockZ)
    }

    private fun ServerLoc.monument(): NationMonument?{
        return service.territoryService.universe.host[this]?.local?.get(this)
    }

    private fun Player.member(): NationMember{
        return service.memberRegistry[this.uniqueId]
    }

    private fun ServerLoc.passNot(action: NationAction, member: NationMember): Boolean{
        return this.monument()?.test(this, action, member)?.process()?.result?.not() == true
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val loc = block.location.parse()
        loc.passNot(NationAction.DESTROY, player.member()).let { if(it) event.isCancelled = true }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.block
        val loc = block.location.parse()
        if (loc.passNot(NationAction.PLACE, player.member())) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityInteract(event: PlayerInteractAtEntityEvent) {
        val player = event.player
        val loc = event.rightClicked.location.parse()
        if (loc.passNot(NationAction.INTERACT, player.member())) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onIgnite(event: BlockIgniteEvent) {
        val player = event.player
        if (player == null) {
            event.isCancelled = true
            return
        }
        val loc = event.block.location.parse()
        if (loc.passNot(NationAction.INTERACT, player.member())) {
            event.isCancelled = true
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onFrameDamaged(event: EntityDamageByEntityEvent) {
        val entity: Entity = event.entity
        var damager: Entity = event.damager
        if (event.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            val proj = damager as Projectile
            if (proj.shooter is Player) {
                damager = proj.shooter as Player
            }
        }
        if (damager !is Player) {
            return
        }
        if (entity is ItemFrame) {
            val player = damager as Player
            val world: World = player.world
            if (entity.location.parse().passNot(NationAction.INTERACT, player.member())) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHangingDestroy(event: HangingBreakByEntityEvent) {
        var entity = event.remover
        val world = entity?.world
        if (entity is Projectile) {
            val proj = entity as Projectile
            if (proj.shooter is Player) {
                entity = proj.shooter as Player
            }
        }
        if (entity is Player) {
            val player = entity
            if (entity.location.parse().passNot(NationAction.INTERACT, player.member())) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHangingDestroy(event: HangingBreakEvent) {
        if (event !is HangingBreakByEntityEvent) event.isCancelled = buildersUtilities(event.entity.location)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHangingPlace(event: HangingPlaceEvent) {
        val entity: Entity = event.entity
        val world: World = entity.world
        val player = entity as Player
        if (entity.location.parse().passNot(NationAction.PLACE, player.member())) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onLeafDecay(event: LeavesDecayEvent) {
        event.isCancelled = buildersUtilities(event.block.location)
    }

    /**
     * 눈 생기고 옵시디언 생기고 얼음 생기고 옵시디언 코블스톤 생기고 콘크리트 생기는 이벤트
     * @param event 이벤트
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockForm(event: BlockFormEvent) {
        event.isCancelled = buildersUtilities(event.block.location)
    }

    /**
     * 눈 사라지고 얼음 녹고, 불 꺼지는 이벤트
     * @param event 이벤트
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockFade(event: BlockFadeEvent) {
        event.isCancelled = buildersUtilities(event.block.location)
    }

    /**
     * 물, 용암 움직이는 이벤트
     * @param event 이벤트
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockFade(event: BlockFromToEvent) {
        event.isCancelled = buildersUtilities(event.block.location)
    }


    /**
     * 버섯, 불 번지는 이벤트
     * @param event 이벤트
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockForm(event: BlockSpreadEvent) {
        event.isCancelled = buildersUtilities(event.block.location)
    }


    private fun buildersUtilities(loc: Location): Boolean {
        return loc.block.location.parse().monument() != null
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockInteract(event: PlayerInteractEvent) {
        if (EquipmentSlot.OFF_HAND == event.hand) return
        val act: Action = event.action
        val player = event.player
        val block = event.clickedBlock ?: return
        val world: World = block.world

        if (act == Action.PHYSICAL || act == Action.RIGHT_CLICK_BLOCK) {
            when{
                block.type.isInteractable -> {
                    val permit = block.location.parse().passNot(NationAction.INTERACT, player.member()).not()
                    if (permit) {
                        if (event.action == Action.RIGHT_CLICK_BLOCK && player.inventory.itemInMainHand.type == AIR) {
                            val data = block.state.data

                            val openable = data as? Door
                            openable?.also {
                                it.isOpen = !it.isOpen
                                block.state.data = it
                            }

                        }
                    } else {
                        event.isCancelled = true
                    }
                }
                else -> {}
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    fun onPhysics(event: BlockPhysicsEvent) {
        event.isCancelled = buildersUtilities(event.block.location)
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBucketFillEvent(event: PlayerBucketFillEvent) {
        val player = event.player
        val block = event.blockClicked
        if (block.location.parse().passNot(NationAction.INTERACT, player.member())) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBucketEmptyEvent(event: PlayerBucketEmptyEvent) {
        val player = event.player
        val block = event.blockClicked
        if (block.location.parse().passNot(NationAction.INTERACT, player.member())) {
            event.isCancelled = true
        }
    }

}


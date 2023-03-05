package com.minepalm.nations.bukkit.territory

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.minepalm.nations.config.TerritoryConfiguration
import com.minepalm.nations.territory.*
import com.minepalm.nations.utils.ServerLoc
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader
import com.sk89q.worldedit.function.mask.ExistingBlockMask
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.io.Closer
import com.sk89q.worldedit.world.World
import com.sk89q.worldedit.world.block.BlockTypes
import org.bukkit.Bukkit
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture

class SchematicWorldModifier(
    private val storage: SchematicStorage,
    private val executor: BukkitExecutor,
    private val config: TerritoryConfiguration
) : WorldModifier {

    private fun ServerLoc.world(): org.bukkit.World? {
        return Bukkit.getWorld(this.world)
    }

    private fun ServerLoc.vector(): BlockVector3 {
        return BlockVector3.at(x, y, z)
    }

    private fun copy(schema: MonumentSchema): CompletableFuture<MonumentBlob> {
        return executor.async<MonumentBlob> {
            val center = schema.center
            val range = schema.range
            val weWorld: World = BukkitWorld(center.world())
            val minimum = range.minimumLocation.vector()
            val maximum = range.maximumLocation.vector()
            val region = CuboidRegion(weWorld, minimum, maximum)
            val clipboard = BlockArrayClipboard(region)
            val editSession = WorldEdit.getInstance().newEditSessionBuilder().world(weWorld).maxBlocks(-1).build()
            val copy = ForwardExtentCopy(editSession, region, minimum, clipboard, maximum)
            copy.sourceMask = ExistingBlockMask(editSession)

            Operations.complete(copy)

            val format = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC
            val bytes = Closer.create().use {
                val bos = it.register(ByteArrayOutputStream())
                val buf = it.register(BufferedOutputStream(bos))
                val writer = it.register(format.getWriter(buf))
                writer.write(clipboard)
                bos.toByteArray()
            }

            return@async MonumentBlob(schema.id, bytes)
        }
    }

    private fun delete0(min: ServerLoc, max: ServerLoc): CompletableFuture<Unit> {
        return executor.async<Unit> {
            val weWorld: World = BukkitWorld(min.world())
            val minimum = min.vector()
            val maximum = max.vector()
            val region = CuboidRegion(minimum, maximum)
            region.world = weWorld

            val source: EditSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(weWorld)
                .maxBlocks(100 * 100 * 255 + 1).build()
            source.setBlocks(region, BlockTypes.AIR!!.defaultState)
            source.close()
        }
    }

    private fun paste(
        center: ServerLoc,
        range: ProtectionRange,
        bytes: ByteArray,
        format: ClipboardFormat
    ): CompletableFuture<Unit> {
        return executor.async<Unit> {
            val worldEditWorld: World = BukkitWorld(range.maximumLocation.world())
            val reader: ClipboardReader = format.getReader(ByteArrayInputStream(bytes))
            val clipboard: Clipboard = reader.read()
            WorldEdit.getInstance().newEditSessionBuilder().world(worldEditWorld).maxBlocks(-1).build()
                .use { editSession ->
                    val operation: Operation = ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(center.vector())
                        .ignoreAirBlocks(true)
                        .build()
                    Operations.complete(operation)
                }
        }
    }

    override fun serialize(schema: MonumentSchema): CompletableFuture<MonumentBlob> {
        return copy(schema)
    }

    override fun paste(monument: NationMonument, blob: MonumentBlob): CompletableFuture<Unit> {
        return paste(monument.center, monument.range, blob.data, BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC)
    }

    override fun create(schema: MonumentSchema): CompletableFuture<Boolean> {
        return if (storage[schema.type] != null) {
            val schematicCenter = config.getSchematicOffset(schema.type).let {
                schema.center.add(it.x, it.y, it.z)
            }
            storage[schema.type]!!.let { paste(schematicCenter, schema.range, it.bytes, it.format) }
                .thenApply { true }
        } else
            CompletableFuture.completedFuture(false)
    }

    override fun delete(min: ServerLoc, max: ServerLoc): CompletableFuture<Unit> {
        return delete0(min, max)
    }

    override fun delete(monumentType: String, center: ServerLoc): CompletableFuture<Unit> {
        val range = config.getDeleteRange(monumentType)
        val min = center.add(-range.weightX, -range.lengthZ, -range.depth)
        val max = center.add(range.weightX, range.lengthZ, range.height)
        return delete0(min, max)
    }

    override fun shutdown() {

    }
}
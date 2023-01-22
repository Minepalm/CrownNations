package com.minepalm.nations.bukkit.territory

import com.minepalm.arkarangutils.bukkit.BukkitExecutor
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
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


//todo: 1.19.2에선 좀 다른 방식으로 ㄱㄱ ( AMPark 참고 )
class SchematicWorldModifier(
    private val storage: SchematicStorage,
    private val executor: BukkitExecutor
    ) : com.minepalm.nations.territory.WorldModifier {

    private fun com.minepalm.nations.utils.ServerLoc.world(): org.bukkit.World? {
        return Bukkit.getWorld(this.world)
    }

    private fun com.minepalm.nations.utils.ServerLoc.vector(): BlockVector3 {
        return BlockVector3.at(x, y, z)
    }

    private fun copy(schema: com.minepalm.nations.territory.MonumentSchema): CompletableFuture<com.minepalm.nations.territory.MonumentBlob> {
        return executor.async<com.minepalm.nations.territory.MonumentBlob> {
            val center = schema.center
            val range = schema.range
            val weWorld: World = BukkitWorld(center.world())
            val minimum = range.minimumLocation.vector()
            val maximum = range.maximumLocation.vector()
            val region = CuboidRegion(weWorld, minimum, maximum)
            val clipboard = BlockArrayClipboard(region)
            val editSession =  WorldEdit.getInstance().newEditSessionBuilder().world(weWorld).maxBlocks(-1).build()
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

            return@async com.minepalm.nations.territory.MonumentBlob(schema.id, bytes)
        }
    }

    private fun delete0(min: com.minepalm.nations.utils.ServerLoc, max: com.minepalm.nations.utils.ServerLoc): CompletableFuture<Unit>{
        return executor.async<Unit>{
            val weWorld: World = BukkitWorld(min.world())
            val minimum = min.vector()
            val maximum = max.vector()
            val region = CuboidRegion(minimum, maximum)
            region.world = weWorld

            val source: EditSession = WorldEdit.getInstance().editSessionFactory.getEditSession(weWorld,
                100*100*255+1)
            source.setBlocks(region, BlockTypes.AIR!!.defaultState)
        }
    }

    private fun paste(range: com.minepalm.nations.territory.ProtectionRange, bytes: ByteArray): CompletableFuture<Unit> {
        return executor.async<Unit> {
            val worldEditWorld: World = BukkitWorld(range.maximumLocation.world())
            val format = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC
            val reader: ClipboardReader = format.getReader(ByteArrayInputStream(bytes))
            val clipboard: Clipboard = reader.read()
            WorldEdit.getInstance().newEditSessionBuilder().world(worldEditWorld).maxBlocks(-1).build()
                .use { editSession ->
                    val operation: Operation = ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(range.minimumLocation.vector())
                        .ignoreAirBlocks(true)
                        .build()
                    Operations.complete(operation)
                }



        }
    }

    override fun serialize(schema: com.minepalm.nations.territory.MonumentSchema): CompletableFuture<com.minepalm.nations.territory.MonumentBlob> {
        return copy(schema)
    }

    override fun paste(monument: com.minepalm.nations.territory.NationMonument, blob: com.minepalm.nations.territory.MonumentBlob): CompletableFuture<Unit> {
        return paste(monument.range, blob.data)
    }

    override fun create(schema: com.minepalm.nations.territory.MonumentSchema): CompletableFuture<Boolean> {
        return if(storage[schema.type] != null)
            paste(schema.range, storage[schema.type]!!).thenApply { true }
        else
            CompletableFuture.completedFuture(null)
    }

    override fun delete(min: com.minepalm.nations.utils.ServerLoc, max: com.minepalm.nations.utils.ServerLoc): CompletableFuture<Unit> {
        return delete0(min, max)
    }

    override fun shutdown() {

    }
}
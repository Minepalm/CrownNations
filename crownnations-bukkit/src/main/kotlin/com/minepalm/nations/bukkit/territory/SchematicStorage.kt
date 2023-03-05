package com.minepalm.nations.bukkit.territory

import com.minepalm.nations.config.TerritoryConfiguration
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import org.bukkit.Bukkit
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap


class SchematicStorage(
    private val directory: File,
    private val config: TerritoryConfiguration
) {

    private val map = ConcurrentHashMap<String, SchematicData>()

    init {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        map["CASTLE"] = read("CASTLE")
        map["OUTPOST"] = read("OUTPOST")
    }

    operator fun get(name: String): SchematicData? {
        return map[name]
    }

    private fun read(name: String): SchematicData {
        val schematicName = config.getSchematic(name)
        val file = File(directory, schematicName)
        val format = ClipboardFormats.findByFile(file)!!
        Bukkit.getLogger().info("read $name schematic $schematicName as format ${format.name}")
        return SchematicData(format, readBytes(file))

    }

    private fun readBytes(file: File): ByteArray {
        val raf = RandomAccessFile(file, "r")
        val bytes = ByteArray(raf.length().toInt())
        raf.readFully(bytes)
        raf.close()
        return bytes
    }

}
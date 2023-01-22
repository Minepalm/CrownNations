package com.minepalm.nations.bukkit.territory

import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap


class SchematicStorage(
    private val directory: File
    ) {

    private val map = ConcurrentHashMap<String, ByteArray>()

    init{
        if(!directory.exists()){
            directory.mkdirs()
        }
        map["CASTLE"] = read("castle")
        map["OUTPOST"] = read("outpost")
    }

    operator fun get(name: String): ByteArray?{
        return map[name]
    }

    private fun read(name: String): ByteArray{
        val file = File(directory, "$name.schematic")
        return if(file.exists()){
            readBytes(file)
        }else
            ByteArray(0)
    }

    private fun readBytes(file: File): ByteArray {
        val raf = RandomAccessFile(file, "r")
        val bytes = ByteArray(raf.length().toInt())
        raf.readFully(bytes)
        return bytes

    }

}
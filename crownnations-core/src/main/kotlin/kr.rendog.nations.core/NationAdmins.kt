package kr.rendog.nations.core

import com.minepalm.library.network.api.HelloAdapter
import com.minepalm.library.network.api.HelloExecutor
import io.netty.buffer.ByteBuf
import kr.rendog.nations.core.mysql.MySQLAdminDatabase
import kr.rendog.nations.server.NationNetwork
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class NationAdmins(
    private val database : MySQLAdminDatabase,
    private val network : NationNetwork
) {

    object UpdateSignal{
        object Adapter : HelloAdapter<UpdateSignal>("NationAdmins.UpdateSignal") {

            override fun decode(p0: ByteBuf): UpdateSignal {
                return UpdateSignal
            }

            override fun encode(p0: ByteBuf, p1: UpdateSignal) {

            }

        }

        class Executor(private val admins: NationAdmins) : HelloExecutor<UpdateSignal> {
            override val identifier: String = "NationAdmins.UpdateSignal"

            override fun executeReceived(p0: UpdateSignal) {
                admins.update()
            }

        }
    }

    val map = ConcurrentHashMap<UUID, Int>()

    init {
        updateInternally(database.getAdmins().join())
    }

    fun getAdmins() : List<UUID>{
        return map.keys().toList()
    }

    fun isAdmin(uniqueId : UUID) : Boolean{
        return map.containsKey(uniqueId)
    }

    fun update(){
        database.getAdmins().thenApply { updateInternally(it) }
    }

    @Synchronized
    fun updateInternally(map : Map<UUID, Int>){
        this.map.clear()
        this.map.putAll(map)
    }

    fun setAdmin(uniqueId: UUID, level : Int) {
        database.setAdmin(uniqueId, level).thenApply { network.broadcast(UpdateSignal) }
    }

    fun deleteAdmin(uniqueId: UUID){
        database.removeAdmin(uniqueId).thenApply { network.broadcast(UpdateSignal) }
    }

    fun isAdminDirect(uniqueId: UUID) : CompletableFuture<Boolean> {
        return database.getAdmins().thenApply { it.containsKey(uniqueId) }
    }

}
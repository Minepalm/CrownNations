package com.minepalm.nations.core

import com.minepalm.nations.Nation
import com.minepalm.nations.NationEventBus
import com.minepalm.nations.NationRegistry
import com.minepalm.nations.core.mysql.MySQLNationIdDatabase
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

class PalmNationRegistry(
    override val policy: NationRegistry.Policy,
    private val factory: PalmNationFactory,
    private val eventBus: NationEventBus,
    memberDatabase: MySQLNationMemberDatabase,
    private val idDatabase: MySQLNationIdDatabase,
    private val executor : ExecutorService,
    private val cleaner: PalmNationRegistryCleaner
    ) : NationRegistry {

    override val local: NationRegistry.Local = LocalImpl(cleaner)
    override val direct = DirectImpl(factory, memberDatabase, idDatabase, executor)

    override fun get(id: Int): Nation? {
        return local[id] ?: direct.getNation(id).join()?.apply { local.add(this) }
    }

    override fun get(name: String): Nation? {
        return local[name] ?: direct.getNation(name).join()?.apply { local.add(this) }
    }

    override fun load(id: Int): CompletableFuture<Unit> {
        return if(local[id] == null){
            direct.getNation(id).thenApply { nation -> nation?.let { local.add(it) } }
        }else{
            CompletableFuture.completedFuture(Unit)
        }
    }

    override fun tryInvalidate(nationId: Int): Boolean {
        val nation = get(nationId)
        val shouldInvalidate = nation != null && policy.shouldInvalidate(nation)
        if(shouldInvalidate){
            local.remove(nationId)
        }
        return shouldInvalidate
    }

    override fun forceInvalidate(nationId: Int) {
        local.remove(nationId)
    }

    override fun refresh(id: Int): CompletableFuture<Nation?> {
        return local[id]?.let { nation -> nation.cache.update().thenApply {
            val event = com.minepalm.nations.event.NationUpdateEvent(id)
            eventBus.invoke(event)
            nation
        } } ?: CompletableFuture.completedFuture(null)

    }

    class LocalImpl(
        private val cleaner: PalmNationRegistryCleaner
    ) : NationRegistry.Local{


        private val nations = ConcurrentHashMap<Int, Nation>()
        private val nationsByName = ConcurrentHashMap<String, Nation>()
        override fun get(id: Int): Nation? {
            return nations[id]
        }

        override fun get(name: String): Nation? {
            return nationsByName[name]
        }

        override fun exists(id: Int): Boolean {
            return nations.containsKey(id)
        }

        @Synchronized
        override fun add(nation: Nation) {
            nations[nation.id] = nation
            nationsByName[nation.name] = nation
            cleaner.mark(nation.id, System.currentTimeMillis())
        }

        @Synchronized
        override fun remove(nationId: Int) {
            if(nations.containsKey(nationId)){
                val nation = nations[nationId]!!
                nations.remove(nationId)
                nationsByName.remove(nation.name)
                cleaner.unmark(nationId)
            }
        }

        override fun getNations(): List<Nation> {
            return nations.values.toList()
        }
    }

    class DirectImpl(
        private val factory: PalmNationFactory,
        private val memberDatabase: MySQLNationMemberDatabase,
        private val idDatabase: MySQLNationIdDatabase,
        private val executor: ExecutorService
    ) : NationRegistry.Direct{
        override fun exists(id: Int): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync({ idDatabase.exists(id).join() }, executor)
        }

        override fun exists(name: String): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync({ idDatabase.hasName(name).join() }, executor)
        }

        override fun getNation(name: String): CompletableFuture<Nation?> {
            return idDatabase.getId(name).thenComposeAsync( { id ->
                if(id != null)
                    factory.build(id, name)
                else
                    CompletableFuture.completedFuture(null)
            }, executor)
        }

        override fun getNation(id: Int): CompletableFuture<Nation?> {
            return idDatabase.getName(id).thenComposeAsync( { name ->
                if(name != null)
                    factory.build(id, name)
                else
                    CompletableFuture.completedFuture(null)
            }, executor)
        }

    }

}
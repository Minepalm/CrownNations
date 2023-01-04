package kr.rendog.nations.core

import kr.rendog.nations.Nation
import kr.rendog.nations.NationEventBus
import kr.rendog.nations.NationRegistry
import kr.rendog.nations.core.mysql.MySQLNationIdDatabase
import kr.rendog.nations.core.mysql.MySQLNationMemberDatabase
import kr.rendog.nations.event.NationUpdateEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

class RendogNationRegistry(
    override val policy: NationRegistry.Policy,
    private val factory: RendogNationFactory,
    private val eventBus: NationEventBus,
    memberDatabase: MySQLNationMemberDatabase,
    private val idDatabase: MySQLNationIdDatabase,
    private val executor : ExecutorService,
    private val cleaner: RendogNationRegistryCleaner
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
            val event = NationUpdateEvent(id)
            eventBus.invoke(event)
            nation
        } } ?: CompletableFuture.completedFuture(null)

    }

    class LocalImpl(
        private val cleaner: RendogNationRegistryCleaner
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
        private val factory: RendogNationFactory,
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
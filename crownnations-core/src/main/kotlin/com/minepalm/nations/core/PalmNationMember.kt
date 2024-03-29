package com.minepalm.nations.core

import com.minepalm.nations.*
import com.minepalm.nations.core.mysql.MySQLNationMemberDatabase
import com.minepalm.nations.territory.NationMonument
import java.util.*
import java.util.concurrent.CompletableFuture

class PalmNationMember(
    override val uniqueId : UUID,
    registry : NationRegistry,
    private val operationFactory: NationOperationFactory,
    database : MySQLNationMemberDatabase,
    admins: NationAdmins
) : NationMember {

    override val direct: NationMember.Direct = MemberDirect(uniqueId, registry, database, admins)
    override fun operateLeaveNation(): NationOperation<Boolean> {
        return operationFactory.buildLeave(this)
    }


    override val cache: NationMember.Cache = MemberCache(uniqueId, registry, direct, admins)

    class MemberCache(
        private val uniqueId: UUID,
        private val registry : NationRegistry,
        private val direct : NationMember.Direct,
        private val admins: NationAdmins
    ) : NationMember.Cache{

        private var nationId : Int = -1

        init {
            update()
        }

        override val nation: Nation?
            get() = registry[nationId]

        override fun update(nation: Nation) {
            nationId = nation.id
        }

        override fun update() {
            direct.getNation().thenApply { this.nationId = it?.id ?: -1 }.join()
        }

        override fun hasNation(): Boolean {
            return nationId != -1
        }

        override fun invalidate() {
            nationId = -1
        }

        override fun isAdmin(): Boolean {
            return admins.isAdmin(uniqueId)
        }

    }

    class MemberDirect(
        private val uniqueId: UUID,
        private val registry : NationRegistry,
        private val database : MySQLNationMemberDatabase,
        private val admins: NationAdmins
    ) : NationMember.Direct{
        override fun getNation(): CompletableFuture<Nation?> {
            return database.getNation(uniqueId).thenApply { registry[it] }
        }

        override fun hasNation(): CompletableFuture<Boolean> {
            return getNation().thenApply { it != null }
        }

        override fun isAdmin(): CompletableFuture<Boolean> {
            return admins.isAdminDirect(uniqueId)
        }

    }

}
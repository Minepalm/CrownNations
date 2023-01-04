package kr.rendog.nations.bukkit.invitation

import com.minepalm.arkarangutils.invitation.Invitation
import com.minepalm.arkarangutils.invitation.InvitationLoadStrategy
import com.minepalm.library.database.impl.internal.MySQLDB
import java.util.*
import java.util.concurrent.CompletableFuture

class PalmLibraryInvitationLoad(
    private var table: String,
    private val database: MySQLDB,
    timeoutMills : Long
) : InvitationLoadStrategy(timeoutMills) {

    init{
        this.database.execute { connection ->
            val ps =
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table +
                        " (`id` BIGINT UNIQUE AUTO_INCREMENT, " +
                        "`receiver` VARCHAR(36), " +
                        "`sender` VARCHAR(36), " +
                        "`issued` LONG, " +
                        "`timeout` LONG, " +
                        "PRIMARY KEY(`receiver`, `sender`)" +
                        ") charset=utf8mb4")
            ps.execute()
        }
    }

    override fun submit(sender: UUID, receiver: UUID): CompletableFuture<Invitation> {
        return database.executeAsync { connection ->
            val ps =
                connection.prepareStatement("INSERT INTO " + table +
                        " (`sender`, `receiver`, `issued`, `timeout`) " +
                        "VALUES(?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE `issued`=VALUES(`issued`), `timeout`=VALUES(`timeout`)")
            val invitation = Invitation(
                sender,
                receiver,
                System.currentTimeMillis(),
                System.currentTimeMillis() + timeoutMills
            )
            ps.setString(1, invitation.issuer.toString())
            ps.setString(2, invitation.received.toString())
            ps.setLong(3, invitation.issued)
            ps.setLong(4, invitation.timeout)
            ps.execute()
            invitation
        }
    }

    override fun getInvitation(sender: UUID, receiver: UUID): CompletableFuture<Invitation?> {
        return database.executeAsync { connection ->
            val ps =
                connection.prepareStatement("SELECT `issued`, `timeout` FROM " + table +
                        " WHERE `sender`=? AND `receiver`=?")
            ps.setString(1, sender.toString())
            ps.setString(2, receiver.toString())
            val rs = ps.executeQuery()
            if (rs.next()) Invitation(sender, receiver, rs.getLong(1), rs.getLong(2)) else null
        }
    }

    override fun getReceivedAll(uuid: UUID): CompletableFuture<List<Invitation>> {
        return database.executeAsync { connection ->
            val ps =
                connection.prepareStatement("SELECT `sender`, `issued`, `timeout` FROM " + table + " WHERE `receiver`=?")
            ps.setString(1, uuid.toString())
            val rs = ps.executeQuery()
            val list: MutableList<Invitation> = mutableListOf()
            if (rs.next()) {
                val sender = UUID.fromString(rs.getString(1))
                list.add(Invitation(sender, uuid, rs.getLong(2), rs.getLong(3)))
            }
            list
        }
    }

    override fun getSendAll(uuid: UUID): CompletableFuture<List<Invitation>> {
        return database.executeAsync { connection ->
            val ps =
                connection.prepareStatement("SELECT `receiver`, `issued`, `timeout` FROM " + table + " WHERE `sender`=?")
            ps.setString(1, uuid.toString())
            val rs = ps.executeQuery()
            val list: MutableList<Invitation> = mutableListOf()
            if (rs.next()) {
                val receiver = UUID.fromString(rs.getString(1))
                list.add(Invitation(uuid, receiver, rs.getLong(2), rs.getLong(3)))
            }
            list
        }
    }

    override fun remove(invitation: Invitation): CompletableFuture<Void?> {
        return database.executeAsync { connection ->
            val ps =
                connection.prepareStatement("DELETE FROM " + table + " WHERE `sender`= AND `receiver`=")
            ps.setString(1, invitation.issuer.toString())
            ps.setString(2, invitation.received.toString())
            ps.execute()
            null
        }
    }

    override fun isInvalidate(invitation: Invitation): CompletableFuture<Boolean> {
        return getInvitation(invitation.issuer, invitation.received).thenApply { it == null || it.isTimeout }
    }

    override fun invalidate(invitation: Invitation) {
        this.remove(invitation)
    }
}
package kr.rendog.nations.core.territory.hellobungee

import com.minepalm.library.network.api.CallbackTransformer
import com.minepalm.library.network.api.HelloAdapter
import io.netty.buffer.ByteBuf
import kr.rendog.nations.territory.NationTerritoryService

class RequestMonumentSave(
    val monumentId: Int
){
    class Adapter : HelloAdapter<RequestMonumentSave>( "RequestMonumentSave") {

        override fun decode(buf: ByteBuf): RequestMonumentSave {
            return RequestMonumentSave(buf.readInt())
        }

        override fun encode(buf: ByteBuf, request: RequestMonumentSave) {
            buf.writeInt(request.monumentId)
        }

    }

    class Callback(
        private val territoryService: NationTerritoryService
    ) : CallbackTransformer<RequestMonumentSave, Boolean> {
        override val identifier: String = "RequestMonumentSave"

        override fun transform(request: RequestMonumentSave): Boolean {
            territoryService.universe.host.getWorlds().forEach{
                val result = it[request.monumentId]?.save()?.join()
                if(result != null)
                    return true
            }
            return false
        }

    }
}
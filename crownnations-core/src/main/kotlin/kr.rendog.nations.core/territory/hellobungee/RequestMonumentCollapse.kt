package kr.rendog.nations.core.territory.hellobungee

import com.minepalm.library.network.api.CallbackTransformer
import com.minepalm.library.network.api.HelloAdapter
import io.netty.buffer.ByteBuf
import kr.rendog.nations.territory.NationTerritoryService

data class RequestMonumentCollapse(
    val monumentId: Int
){
    class Adapter : HelloAdapter<RequestMonumentCollapse>("RequestMonumentCollapse") {

        override fun decode(buf: ByteBuf): RequestMonumentCollapse {
            return RequestMonumentCollapse(buf.readInt())
        }

        override fun encode(buf: ByteBuf, request: RequestMonumentCollapse) {
            buf.writeInt(request.monumentId)
        }

    }

    class Callback(
        private val territoryService: NationTerritoryService
    ) : CallbackTransformer<RequestMonumentCollapse, Boolean> {

        override val identifier: String = "RequestMonumentCollapse"

        override fun transform(request: RequestMonumentCollapse): Boolean {
            territoryService.universe.host.getWorlds().forEach{
                val result = it[request.monumentId]?.collapse()?.join()
                if(result == true)
                    return true
            }
            return false
        }

    }
}
package com.minepalm.nations.core.territory.hellobungee

import com.minepalm.library.network.api.CallbackTransformer
import com.minepalm.library.network.api.HelloAdapter
import io.netty.buffer.ByteBuf

class RequestMonumentLoad(
    val monumentId: Int
){
    class Adapter : HelloAdapter<RequestMonumentLoad>("RequestMonumentLoad") {

        override fun decode(buf: ByteBuf): RequestMonumentLoad {
            return RequestMonumentLoad(buf.readInt())
        }

        override fun encode(buf: ByteBuf, request: RequestMonumentLoad) {
            buf.writeInt(request.monumentId)
        }

    }

    class Callback(
        private val territoryService: com.minepalm.nations.territory.NationTerritoryService
    ) : CallbackTransformer<RequestMonumentLoad, Boolean> {

        override val identifier: String = "RequestMonumentLoad"

        override fun transform(request: RequestMonumentLoad): Boolean {
            territoryService.universe.host.getWorlds().forEach{
                val result = it[request.monumentId]?.load()?.join()
                if(result != null)
                    return true
            }
            return false
        }

    }
}
package com.sundayting.wancompose.network.okhttp.cookie

import androidx.datastore.core.Serializer
import com.sundayting.wancompose.protobuf.CookieProtobuf
import java.io.InputStream
import java.io.OutputStream

object CookieSerializer : Serializer<CookieProtobuf.CookiesProto> {
    override val defaultValue: CookieProtobuf.CookiesProto
        get() = CookieProtobuf.CookiesProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CookieProtobuf.CookiesProto {
        return CookieProtobuf.CookiesProto.parseFrom(input)
    }

    override suspend fun writeTo(t: CookieProtobuf.CookiesProto, output: OutputStream) {
        t.writeTo(output)
    }


}
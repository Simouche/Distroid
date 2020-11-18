package com.safesoft.safemobile.backend.utils

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.safesoft.safemobile.backend.db.entity.Providers

class ProviderAdapterFactory : TypeAdapterFactory {


    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T> {
        TODO("Not yet implemented")
    }

    class ProviderAdapter(val defaultAdapter: TypeAdapter<Providers>) : TypeAdapter<Providers>() {


        override fun write(out: JsonWriter?, value: Providers?) {
            defaultAdapter.write(out, value);
        }

        override fun read(`in`: JsonReader?): Providers {
            return defaultAdapter.read(`in`)
        }


    }

}
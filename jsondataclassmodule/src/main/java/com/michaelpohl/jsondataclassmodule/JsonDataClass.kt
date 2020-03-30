package com.michaelpohl.jsondataclassmodule

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

/**
 * All sub classes must be data classes!
 */
open class JsonDataClass {

    open fun toJsonString(adapters: Array<out AdapterDefinition>? = adapterDefinitions): String {
        return buildMoshi(adapters).adapter(this.javaClass).toJson(this)
    }

    companion object {

        var adapterDefinitions = arrayOf<AdapterDefinition>()

        inline fun <reified T : JsonDataClass> fromJsonString(
            string: String,
            adapters: Array<out AdapterDefinition>? = adapterDefinitions
        ): T? {
            return buildMoshi(adapters).adapter(T::class.java).fromJson(string)
        }

        fun buildMoshi(adapters: Array<out AdapterDefinition>? = adapterDefinitions): Moshi {
            val builder = Moshi.Builder()

            adapters?.let {
                if (it.isNotEmpty()) {
                    builder.add(KotlinJsonAdapterFactory())
                    it.forEach { definition ->
                        builder.add(definition.first, definition.second)
                    }
                }
            }
            return builder.build()
        }
    }
}

typealias AdapterDefinition = Pair<Type, JsonAdapter<*>>

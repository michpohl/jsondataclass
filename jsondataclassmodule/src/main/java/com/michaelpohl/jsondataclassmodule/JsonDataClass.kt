package com.michaelpohl.jsondataclassmodule

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

/**
 * All sub classes must be data classes!
 */
open class JsonDataClass {
    fun toJsonString(vararg adapters: AdapterDefinition): String {
        return buildMoshi(adapters).adapter(this.javaClass).toJson(this)
    }

    companion object {

        var adapterDefinitions = arrayOf<AdapterDefinition>()

        inline fun <reified T : JsonDataClass> fromJsonString(
            string: String,
            vararg adapters: AdapterDefinition
        ): T? {
            return buildMoshi(adapters).adapter(T::class.java).fromJson(string)
        }

        fun buildMoshi(adapters: Array<out AdapterDefinition>? = adapterDefinitions): Moshi {
            val builder = Moshi.Builder()

            adapters ?: adapterDefinitions.apply {
                if (this.isNotEmpty()) {
                    builder.add(KotlinJsonAdapterFactory())
                    this.forEach { definition ->
                        builder.add(definition.first, definition.second)
                    }
                }
            }
            return builder.build()
        }
    }
}

typealias AdapterDefinition = Pair<Type, JsonAdapter<*>>

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

        inline fun <reified T : JsonDataClass> listFromJsonString(
            string: String,
            vararg adapters: AdapterDefinition
        ): List<T?> {
            val resultList = mutableListOf<T>()
            val stringList = parseJsonList(string)
            val moshi = buildMoshi(adapters).adapter(T::class.java)
            stringList.forEach {

                moshi.fromJson(it)?.let { t ->
                    resultList.add(t)
                }
            }
            return resultList
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

        inline fun parseJsonList(string: String): List<String?> {
            var string2 = ""
            if (!string.startsWith("[")) return listOf()

            var bracketCount = 0
            var start = 0
            var end = 0
            val substrings = mutableListOf<String>()

            string.asSequence().forEach {
                if (it.toString() == "{") {
                    if (bracketCount == 0) start = string2.indexOf(it)
                    bracketCount += 1
                }
                if (it.toString() == "}") {
                    bracketCount -= 1
                    if (bracketCount == 0) {
                        end = string2.indexOf(it)
                        substrings.add(string2.subSequence(start, end + 1).toString())
                    }
                }
            }
            return substrings
        }
    }
}

typealias AdapterDefinition = Pair<Type, JsonAdapter<*>>

// a tiny main() function for testing

//fun main() {
//    val testString =
//        "[{\"placeId\":\"here:2768lxx5-89caf332709a0d0a2afe8704a5fa954a\",\"category\":\"point-of-interest\",\"label\":\"Haus des Rundfunks\",\"subLabel\":\"Masurenallee, Westend, 14057 Berlin\"},{\"placeId\":\"here:276q9wpy-74b80732a276070d3f2bae2f0521e26e\",\"category\":\"station\",\"label\":\"Haus des Rundfunks\",\"subLabel\":\"Masurenallee, Westend, 14057 Berlin\"}]"
//    val result = JsonDataClass.fromJsonStringAsList<PlaceSuggestion>(testString)
//    print(result)
//}
//
//data class PlaceSuggestion(
//    val category: String,
//    val id: String,
//    val label: String,
//    val sublabel: String
//) : JsonDataClass()



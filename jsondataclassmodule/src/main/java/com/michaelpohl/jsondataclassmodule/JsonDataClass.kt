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
        ): List<T> {
            val resultList = mutableListOf<T>()
            val stringList = parseJsonList(string)
            val moshi = buildMoshi(adapters).adapter(T::class.java)
            stringList.forEach {
                it?.let {
                    moshi.fromJson(it)?.let { t -> resultList.add(t) }
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
            if (!string.startsWith("[")) return listOf()

            var bracketCount = 0
            var start = 0
            var end = 0
            val substrings = mutableListOf<String>()

            string.asSequence().forEach {
                if (it.toString() == "{") {
                    if (bracketCount == 0) start = string.indexOf(it)
                    bracketCount += 1
                }
                if (it.toString() == "}") {
                    bracketCount -= 1
                    if (bracketCount == 0) {
                        end = string.indexOf(it)
                        substrings.add(string.subSequence(start, end + 1).toString())
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
//        "[{\"placeId\":\"here:loc-dmVyc2lvbj0xO3RpdGxlPVR6dW1tYXJ1bTtsYW5nPW5sO2xhdD01My4yMzY0MTtsb249NS41NDI1NTtjaXR5PVR6dW1tYXJ1bTtjb3VudHJ5PU5MRDtzdGF0ZT1Gcmllc2xhbmQ7Y291bnR5PVdhYWRob2VrZTtjYXRlZ29yeUlkPWNpdHktdG93bi12aWxsYWdlO3NvdXJjZVN5c3RlbT1pbnRlcm5hbDtwZHNDYXRlZ29yeUlkPTkwMC05MTAwLTAwMDA\",\"category\":\"point-of-interest\",\"label\":\"Tzummarum\",\"subLabel\":\"Waadhoeke, Friesland, Niederlande\"},{\"placeId\":\"here:loc-dmVyc2lvbj0xO3RpdGxlPVR6dW07bGFuZz1ubDtsYXQ9NTMuMTU4NzY7bG9uPTUuNTYyNzE7Y2l0eT1UenVtO3Bvc3RhbENvZGU9ODgwNDtjb3VudHJ5PU5MRDtzdGF0ZT1Gcmllc2xhbmQ7Y291bnR5PVdhYWRob2VrZTtjYXRlZ29yeUlkPWNpdHktdG93bi12aWxsYWdlO3NvdXJjZVN5c3RlbT1pbnRlcm5hbDtwZHNDYXRlZ29yeUlkPTkwMC05MTAwLTAwMDA\",\"category\":\"point-of-interest\",\"label\":\"Tzum\",\"subLabel\":\"Waadhoeke, Friesland, Niederlande\"},{\"placeId\":\"here:loc-dmVyc2lvbj0xO3RpdGxlPVR6dWNhY2FiO2xhbmc9ZXM7bGF0PTIwLjA3MTg3O2xvbj0tODkuMDUwMTU7Y2l0eT1UenVjYWNhYjtjb3VudHJ5PU1FWDtzdGF0ZT1ZdWNhdGFuO3N0YXRlQ29kZT1ZVUM7Y2F0ZWdvcnlJZD1jaXR5LXRvd24tdmlsbGFnZTtzb3VyY2VTeXN0ZW09aW50ZXJuYWw7cGRzQ2F0ZWdvcnlJZD05MDAtOTEwMC0wMDAw\",\"category\":\"point-of-interest\",\"label\":\"Tzucacab\",\"subLabel\":\"Yucatan, Mexiko\"},{\"placeId\":\"here:276u1huu-9f3fee0155ef456d906844ad62c62a18\",\"category\":\"station\",\"label\":\"Tzu\",\"subLabel\":\"Mülheimer Straße, Alt-Oberhausen, 46049 Oberhausen\"},{\"placeId\":\"here:100z7fbz-76e0eced2fae022d7904ef13473c598e\",\"category\":\"point-of-interest\",\"label\":\"Tzum\",\"subLabel\":\"Bulevard Knyaginya Maria Luiza, 1000 Sofia, Bulgarien\"}]"
//    val result = JsonDataClass.listFromJsonString<PlaceSuggestion>(testString)
//    print(result)
//}
//
//data class PlaceSuggestion(
//    val category: String,
//    val id: String,
//    val label: String,
//    val sublabel: String
//) : JsonDataClass()



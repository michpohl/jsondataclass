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
            var end: Int
            val substrings = mutableListOf<String>()
            for ( index in 0 until (string.count())) {
                val char = string[index]
                if (char.toString() == "{") {
                    print("\n")
                    if (bracketCount == 0) start = index
                    bracketCount += 1
                }
                if (char.toString() == "}") {
                    bracketCount -= 1
                    if (bracketCount == 0) {
                        end = index+1
                        substrings.add(string.subSequence(start, end).toString())
                    }
                }
            }
            print(substrings)
            return substrings
        }
    }
}

typealias AdapterDefinition = Pair<Type, JsonAdapter<*>>

//// a tiny main() function for testing
//
//fun main() {
//
//    val result = JsonDataClass.listFromJsonString<PlaceSuggestion>(testString)
//    result.forEach { print("${it.label}") }
////    print(result)
//}
//
//data class PlaceSuggestion(
//    val category: String,
//    val id: String,
//    val label: String,
//    val sublabel: String
//) : JsonDataClass()
//
//val testString =
//
//    """[
//            {
//                "placeId": "here:loc-dmVyc2lvbj0xO3RpdGxlPUJlcmxpbjtsYW5nPWRlO2xhdD01Mi41MTYwNTtsb249MTMuMzc2OTE7Y2l0eT1CZXJsaW47Y291bnRyeT1ERVU7c3RhdGU9QmVybGluO2NvdW50eT1CZXJsaW47Y2F0ZWdvcnlJZD1jaXR5LXRvd24tdmlsbGFnZTtzb3VyY2VTeXN0ZW09aW50ZXJuYWw7cGRzQ2F0ZWdvcnlJZD05MDAtOTEwMC0wMDAw",
//                "category": "point-of-interest",
//                "label": "Berlin",
//                "subLabel": "Deutschland"
//            },
//            {
//                "placeId": "here:276u339g-0dd32a93728c47c3b35985e550f30a70",
//                "category": "point-of-interest",
//                "label": "Flughafen Berlin Brandenburg Willy Brandt",
//                "subLabel": "Melli-Beese-Ring 1, 12529 Schönefeld"
//            },
//            {
//                "placeId": "here:276u336w-82be381a0b844ec380085283e668c513",
//                "category": "station",
//                "label": "Zentraler Omnibusbahnhof Berlin (Berlin ZOB)",
//                "subLabel": "Masurenallee 4, Westend, 14057 Berlin"
//            },
//            {
//                "placeId": "here:loc-dmVyc2lvbj0xO3RpdGxlPUJlcm5hdStiZWkrQmVybGluO2xhbmc9ZGU7bGF0PTUyLjY4MDczO2xvbj0xMy41ODMzODtjaXR5PUJlcm5hdStiZWkrQmVybGluO3Bvc3RhbENvZGU9MTYzMjE7Y291bnRyeT1ERVU7c3RhdGU9QnJhbmRlbmJ1cmc7Y291bnR5PUJhcm5pbTtjYXRlZ29yeUlkPWNpdHktdG93bi12aWxsYWdlO3NvdXJjZVN5c3RlbT1pbnRlcm5hbDtwZHNDYXRlZ29yeUlkPTkwMC05MTAwLTAwMDA",
//                "category": "point-of-interest",
//                "label": "Bernau bei Berlin",
//                "subLabel": "Barnim, Brandenburg"
//            },
//            {
//                "placeId": "here:276u336y-a67b49e4b9fa4db5a8fd001e1dc2a835",
//                "category": "point-of-interest",
//                "label": "Flughafen Berlin-Tegel",
//                "subLabel": "Flughafen Tegel, Tegel, 13405 Berlin"
//            },
//            {
//                "placeId": "here:loc-dmVyc2lvbj0xO3RpdGxlPVN0YWR0cmluZytCZXJsaW47bGFuZz1kZTtsYXQ9NTIuNTE3MDI7bG9uPTEzLjI4NDA3O3N0cmVldD1TdGFkdHJpbmcrQmVybGluO2NpdHk9QmVybGluO3Bvc3RhbENvZGU9MTQwNTk7Y291bnRyeT1ERVU7ZGlzdHJpY3Q9Q2hhcmxvdHRlbmJ1cmc7c3RhdGU9QmVybGluO2NvdW50eT1CZXJsaW47Y2F0ZWdvcnlJZD1zdHJlZXQtc3F1YXJlO3NvdXJjZVN5c3RlbT1pbnRlcm5hbDtwZHNDYXRlZ29yeUlkPTkwMC05NDAwLTA0MDE",
//                "category": "address",
//                "label": "Stadtring Berlin",
//                "subLabel": "Charlottenburg, 14059 Berlin"
//            },
//            {
//                "placeId": "here:276u336w-3b4dfc05bf4f4fcf87a7c2a9d6a54bbc",
//                "category": "point-of-interest",
//                "label": "ICC Berlin (Internationales Congress Centrum Berlin)",
//                "subLabel": "Messedamm 22, Westend, 14055 Berlin"
//            },
//            {
//                "placeId": "here:276u336x-0b5648343a564016bcddf6a2d211bb1b",
//                "category": "point-of-interest",
//                "label": "Zoologischer Garten (ZOO BERLIN)",
//                "subLabel": "Hardenbergplatz 8, Charlottenburg, 10623 Berlin"
//            },
//            {
//                "placeId": "here:276u336v-ecd820cb52dc413db3c4ed81eabf22a1",
//                "category": "point-of-interest",
//                "label": "Olympiastadion (Stadion Berlin)",
//                "subLabel": "Olympischer Platz 3, Westend, 14053 Berlin"
//            },
//            {
//                "placeId": "here:276u336w-f4dc846bdeaf4bdf8aaaa99b4a966576",
//                "category": "point-of-interest",
//                "label": "Messe Berlin",
//                "subLabel": "Westend, 14055 Berlin"
//            },
//            {
//                "placeId": "here:276u336w-f4f294ff15c5425483c2d24b78e18597",
//                "category": "point-of-interest",
//                "label": "Funkturm",
//                "subLabel": "Hammarskjöldplatz, Westend, 14055 Berlin"
//            },
//            {
//                "placeId": "here:loc-dmVyc2lvbj0xO3RpdGxlPVByZW56bGF1ZXIrQmVyZztsYW5nPWRlO2xhdD01Mi41NDA4NDtsb249MTMuNDI1NzY7Y2l0eT1CZXJsaW47Y291bnRyeT1ERVU7ZGlzdHJpY3Q9UHJlbnpsYXVlcitCZXJnO3N0YXRlPUJlcmxpbjtjb3VudHk9QmVybGluO2NhdGVnb3J5SWQ9YWRtaW5pc3RyYXRpdmUtcmVnaW9uO3NvdXJjZVN5c3RlbT1pbnRlcm5hbDtwZHNDYXRlZ29yeUlkPTkwMC05NDAwLTAzOTk",
//                "category": "point-of-interest",
//                "label": "Prenzlauer Berg",
//                "subLabel": "Berlin"
//            },
//            {
//                "placeId": "here:loc-dmVyc2lvbj0xO3RpdGxlPUJlcmxpbmVyK1N0cmElQzMlOUZlO2xhbmc9ZGU7bGF0PTUyLjQ4ODIyMDIxNDg0Mzc1O2xvbj0xMy4zMDY2NTk2OTg0ODYzMjg7c3RyZWV0PUJlcmxpbmVyK1N0cmElQzMlOUZlO2NpdHk9QmVybGluO3Bvc3RhbENvZGU9MTA3MTM7Y291bnRyeT1ERVU7ZGlzdHJpY3Q9V2lsbWVyc2RvcmY7c3RhdGU9QmVybGluO2NvdW50eT1CZXJsaW47Y2F0ZWdvcnlJZD1zdHJlZXQtc3F1YXJlO3NvdXJjZVN5c3RlbT1pbnRlcm5hbDtwZHNDYXRlZ29yeUlkPTkwMC05NDAwLTA0MDE",
//                "category": "address",
//                "label": "Berliner Straße",
//                "subLabel": "Wilmersdorf, 10713 Berlin"
//            }
//        ]"""
//

### JsonDataClass
#### A helper to simply transform Kotlin data classes to and from JSON

For those who need to transfer their data classes to *JSON* and from *JSON* often, this aims to make life a little easier. It uses [Moshi](https://github.com/square/moshi) under the hood (To be honest, this is just some sugar on top, all the credit for the heavy lifting goes to the guys at [Square](https://github.com/square) for building *Moshi*).

Every data class that extends **JsonDataClass** gains a *toJsonString()* method that simply works and returns a JSON string. If you need to get your data class back from JSON, you can do it by *JsonDataClass.fromJsonString().* It is also possible to nest your data classes, so if you have a class that holds a number of smaller model classes, this will turn this into a JSON in a breeze.

And if your data classes are more complex, you can add custom *JsonAdapters* to your call or specify them in your data class.

This little library has been developed for **Android** projects - it might also work elsewhere as it is not dependent on anything specific to **Android**,
but this is untested.

#### Installation

If you haven't, add jitpack to your project's *build.gradle*:

    allprojects {
        repositories {
      
            // add this line   
            maven { url 'https://jitpack.io' }
    }
}

Then add the dependency to your app's *build.gradle* file:

    implementation 'com.github.michpohl:jsondataclass:[version]'

The current latest available version is: **0.6**

If you want, you can of course clone this repo and reference it in your gradle files directly.

#### Usage:

Just have your data classes extend *JsonDataClass* like the two in this example:

    data class MyDataClass(
        val a: Int,
        val b: String,
        val c: MyOtherDataClass
    ) : JsonDataClass()

    data class MyOtherDataClass(
        val a: List<Long>,
        val b: Map<String, Int>
    ) : JsonDataClass()

Note that these two are nested, too - this is just for the sake of the example.  
Now , if you have a data class of type *MyDataClass*, you can do:

    // instantiate your data class
    val dataClass = MyDataClass(
        5, 
        "Hello", 
        MyOtherDataClass(
            listOf(100L, 200L), 
            mapOf(Pair("String", 1)
            )
        )
    )
    
    // get your JSON string
    val string = dataClass.toJsonString()

And if you want it back as a data class, this is how:

    val dataClass2 = JsonDataClass.fromJsonString<MyDataClass>(string)

If your data class contains more complex types, you might have to deal with *JsonAdapters*:

    data class MyDataDate(
        val date: Date
    ) : JsonDataClass()

For this case, **JsonDataClass** uses *AdapterDefinitions*, which just hold a type and the correspondent *JsonAdapter*. This can be a custom adapter you provide or one that is already integrated in *Moshi*.

    val dataClass = MyDataDate(Date())
    
    val adapter = AdapterDefinition(
        Date::class.java, 
        Rfc3339DateJsonAdapter().nullSafe()
    )
    
    val string = dataClass.toJsonString(adapter)
    
    val dataClass2 = JsonDataClass.fromJsonString<MyDataDate>(string, adapter) 

Note that the *adapters* argument is vararg, so you can pass multiple *AdapterDefinitions*:

    someDataClass.toJsonString(adapter1, adapter2, adapter3)

Or, if you have an array of definitions, use Kotlin's *Spread* operator:

    someDataClass.toJsonString(*adapterArray)

You can also build the *AdapterDefinition* into your data class so you don't have to worry about them anymore, by writing them into it's *Companion Object* in the *init* block:

    data class AdapterDataClass(
        val a: ComplexData,
        val b: EvenMoreComplexData
    ) {       
        init {
            Companion.adapterDefinitions = arrayOf(adapter1, adapter2)
        }
    }    

This way, you can transform in and out of JSON strings without having to specify *AdapterDefinitions* in each call.


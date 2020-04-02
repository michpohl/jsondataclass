##### JsonDataClass
For those who need to transfer their Kotlin data classes to *JSON* and from *JSON* often, this little helper aims to make life a little easier. It uses [Moshi](https://github.com/square/moshi) under the hood.

Every data class that extends **JsonDataClass** gains a *toJsonString()* method that simply works and returns a JSON string. 

If you need to get your data class back from JSON, you can do it by *JsonDataClass.fromJsonString().*

And if your data classes are more complex, you can add custom *JsonAdapters* to your call or specify them in your data class.

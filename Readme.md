#JsonDataClass

For those who need to transfer data classes to *JSON* and from *JSON* often, this little helper aims to make life a little easier.

Every data class that extends **JsonDataClass** gains a *toJsonString()* method that simply works and returns a JSON string. 

If you need to get your data class back from JSON, you can do it by *JsonDataClass.fromJsonString().*
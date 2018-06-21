package io.fake.objects

data class Gist(val description: String, val public: Boolean, val files: Map<String, Map<String, String>>)
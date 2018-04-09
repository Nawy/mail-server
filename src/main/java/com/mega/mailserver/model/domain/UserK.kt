package com.mega.mailserver.model.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("user")
@Document(collection = "user")
data class UserK(
        @Id
        val id : String? = null,
        @Indexed
        val name : String = "",
        var password : String = "",
        val phoneNumber : String = "",
        val fullName : String = ""
)
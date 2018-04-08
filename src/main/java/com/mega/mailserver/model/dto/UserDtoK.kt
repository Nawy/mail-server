package com.mega.mailserver.model.dto

import com.mega.mailserver.model.domain.UserK
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("user")
@Document(collection = "user")
data class UserDtoK(
        val name: String = "",
        val phoneNumber: String,
        val fullName: String,
        val password: String
) {
    fun toUser(): UserK {
        return UserK(
                "",
                name,
                password,
                phoneNumber,
                fullName
        )
    }

    companion object {
        fun valueOf(value : UserK) : UserDtoK {
            return UserDtoK(
                    value.name,
                    value.phoneNumber,
                    value.fullName,
                    value.password
            )
        }
    }
}
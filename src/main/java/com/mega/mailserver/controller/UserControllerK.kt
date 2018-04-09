package com.mega.mailserver.controller

import com.mega.mailserver.model.SecurityRole
import com.mega.mailserver.model.dto.UserDtoK
import com.mega.mailserver.model.exception.BadRequestException
import com.mega.mailserver.model.exception.ForbiddenException
import com.mega.mailserver.model.exception.NotFoundException
import com.mega.mailserver.service.UserService
import com.mega.mailserver.service.security.AuthService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
open class UserControllerK(
        private val userService: UserService,
        private val authService: AuthService
) {

    @PostMapping
    fun insert(@RequestBody user : UserDtoK) {
        when {
            user.name.isBlank() ->
                throw BadRequestException("Name is empty")

            userService.get(user.name) != null ->
                throw ForbiddenException("user with such email :'%s' already exists".format(user.name))
        }

        userService.upsert(user.toUser())
    }

    @Secured(SecurityRole.USER)
    @PutMapping
    fun put(userDto : UserDtoK) : UserDtoK {

        userDto.name = authService.user.name

        return UserDtoK.valueOf(
                userService.upsert(userDto.toUser())
        )
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/{name}")
    fun get(@PathVariable("name") name : String) : UserDtoK {

        if (name.isBlank())
            throw BadRequestException("name is empty")

        userService.get(name)?.let {
            return UserDtoK.valueOf(it)
        }

        throw NotFoundException("Cannot find user")
    }
}
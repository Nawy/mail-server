package com.mega.mailserver.controller

import com.mega.mailserver.model.SecurityRole
import com.mega.mailserver.model.domain.Letter
import com.mega.mailserver.service.MailboxService
import com.mega.mailserver.service.security.AuthService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Secured(SecurityRole.USER)
@RestController
@RequestMapping("/mailbox")
open class MailboxControllerK(
        private val authService: AuthService,
        private val mailboxService: MailboxService
) {

    @GetMapping("{address}")
    fun getConversation(@PathVariable("address") address : String) : MutableCollection<Letter>? {
        return authService.user?.let {
            mailboxService.getConversation(it.name, address)
        }
    }

    @GetMapping("/letters")
    fun getLetters() : MutableCollection<Letter>? {
        return authService.user?.let {
            mailboxService.getLetters(it.name)
        }
    }

    @GetMapping("/spam")
    fun getSpam() : MutableCollection<Letter>? {
        return authService.user?.let {
            mailboxService.getSpam(it.name)
        }
    }
}
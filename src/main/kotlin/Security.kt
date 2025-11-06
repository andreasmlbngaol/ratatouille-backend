package com.sukakotlin

import com.kborowy.authprovider.firebase.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import org.slf4j.event.*

fun Application.configureSecurity() {
    install(Authentication) {
//        firebase {
//            adminFile = File("path/to/admin/file.json")
//            realm = "My Server"
//            validate { token ->
//                MyAuthenticatedUser(id = token.uid)
//            }
//        }
    }
}

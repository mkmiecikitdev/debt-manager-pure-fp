package com.bambz.debtmanagerpurefpdemo.infrastructure.security

import com.bambz.debtmanagerpurefpdemo.domain.users.api.UserDto
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.Constants.BEARER
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.vavr.control.Option
import io.vavr.control.Try
import java.util.*

class JwtService(private val objectMapper: ObjectMapper, private val secret: ByteArray = getSecret()) {

    fun generateJwt(userData: UserDto): String {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS512)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION))
                .setSubject(objectMapper.writeValueAsString(userData))
                .compact()
    }


    fun getUserData(token: String): Option<UserDto> {
        return Try.of {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.replace(BEARER, ""))
                    .body
                    .subject.let {
                objectMapper.readValue(it, UserDto::class.java)
            }
        }.toOption()
    }

    companion object {
        private const val EXPIRATION = 864000000

        private fun getSecret(): ByteArray {
            return this::class.java.classLoader.getResourceAsStream("keys.properties").let {
                val prop = Properties()
                prop.load(it)
                it.close()
                val toByteArray = prop.getProperty("jwt.secret")
                        .toByteArray()
                it.close()
                toByteArray
            }
        }
    }
}
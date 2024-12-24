package com.lcaohoanq.nocket

import com.lcaohoanq.nocket.domain.jwt.JWTConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.retry.annotation.EnableRetry
import java.util.*

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableConfigurationProperties(JWTConfig::class)
@EnableJpaRepositories(basePackages = ["com.lcaohoanq.nocket.domain"])
open class NocketApplication


fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
    SpringApplication.run(NocketApplication::class.java, *args)
}

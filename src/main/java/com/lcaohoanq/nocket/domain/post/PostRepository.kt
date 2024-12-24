package com.lcaohoanq.nocket.domain.post

import com.lcaohoanq.nocket.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostRepository : JpaRepository<Post?, UUID?> {
    fun findByUser(user: User): List<Post>
    fun findByUserIn(users: List<User>): List<Post>
}

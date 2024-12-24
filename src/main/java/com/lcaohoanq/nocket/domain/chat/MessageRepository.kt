package com.lcaohoanq.nocket.domain.chat

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MessageRepository : JpaRepository<Message, UUID>

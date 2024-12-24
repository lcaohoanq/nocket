package com.lcaohoanq.nocket.domain.socialaccount

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SocialAccountRepository : JpaRepository<SocialAccount, UUID>

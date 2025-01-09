package com.lcaohoanq.nocket.domain.post

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.api.PageResponse
import com.lcaohoanq.nocket.domain.asset.FileStoreService
import com.lcaohoanq.nocket.domain.cache.IPostRedisService
import com.lcaohoanq.nocket.domain.friendship.FriendshipRepository
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse
import com.lcaohoanq.nocket.domain.user.UserService
import com.lcaohoanq.nocket.enums.FriendShipStatus
import com.lcaohoanq.nocket.enums.PostType
import com.lcaohoanq.nocket.metadata.MediaMeta
import com.lcaohoanq.nocket.util.PaginationConverter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function

@RequestMapping("\${api.prefix}/posts")
@RestController
class PostController(
    private val userService: UserService,
    private val postRepository: PostRepository,
    private val postService: IPostService,
    private val friendshipRepository: FriendshipRepository,
    private val fileStoreService: FileStoreService,
    private val postMapper: PostMapper,
    private val paginationConverter: PaginationConverter,
    private val postRedisService: IPostRedisService
) {
    
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun getAll(
        @RequestParam(required = true) page: Int,
        @RequestParam(required = true) limit: Int
    ): ResponseEntity<PageResponse<PostResponse>> {
        require(!(page < 0 || limit < 0)) { "Page and limit must be greater than 0." }

        val pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending())

        // Directly return the PageResponse<PostResponse> created by mapPageResponse
        val pageResponse = paginationConverter.mapPageResponse(
            postRepository.findAll(pageRequest),
            pageRequest,
            { post: Post? -> postMapper.toPostResponse(post) },
            "Successfully get all posts"
        )

        return ResponseEntity.ok().body(pageResponse)
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    fun getPostById(
        @RequestParam id: UUID
    ): ResponseEntity<ApiResponse<PostResponse>> {
        return ResponseEntity.ok().body(
            ApiResponse.builder<PostResponse>()
                .message("Successfully get post by id")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(
                    postRepository.findById(id)
                        .map<PostResponse>(Function { post: Post? ->
                            postMapper.toPostResponse(
                                post
                            )
                        })
                        .orElseThrow()
                )
                .build()
        )
    }

    @get:PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @get:GetMapping("/user")
    val postsOfUser: ResponseEntity<ApiResponse<List<PostResponse>>>
        get() {
            val userDetails = SecurityContextHolder.getContext()
                .authentication.principal as UserDetails
            val user = userService.findByUsername(userDetails.username)
            return ResponseEntity.ok().body(
                ApiResponse.builder<List<PostResponse>>()
                    .message("Successfully get all posts of user")
                    .isSuccess(true)
                    .statusCode(HttpStatus.OK.value())
                    .data(
                        postRepository.findByUser(user).stream()
                            .map { post: Post? -> postMapper.toPostResponse(post) }
                            .toList())
                    .build()
            )
        }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @GetMapping("/user-and-friends")
    fun getPostsOfUserAndFriends(): ResponseEntity<ApiResponse<List<PostResponse>>> {
        // Get current authenticated user
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val currentUser = userService.findByUsername(userDetails.username)

        // Fetch friends who have accepted the friendship
        val friends =
            friendshipRepository.findFriendshipsByUserAndStatus(currentUser, FriendShipStatus.ACCEPTED)
                .mapNotNull { if (it.user1 == currentUser) it.user2 else it.user1 }

        // Include current user and their friends in the list
        val usersToFetchPostsFrom = listOf(currentUser) + friends

        // Fetch posts for the current user and their friends
        val posts = postRepository.findByUserIn(usersToFetchPostsFrom)

        // Sort posts by creation date (most recent first)
        val sortedPosts = posts.sortedByDescending { it.createdAt }

        // Map posts to PostResponse and return in the API response
        return ResponseEntity.ok().body(
            ApiResponse.builder<List<PostResponse>>()
                .message("Successfully retrieved posts for user and friends")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(sortedPosts.map(postMapper::toPostResponse))
                .build()
        )
    }

    @PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @Throws(
        IOException::class
    )
    fun createPost(
        @ModelAttribute("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<PostResponse>> {
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val user = userService.findByUsername(userDetails.username)

        val filename = fileStoreService
            .storeFile(fileStoreService.validateProductImage(file))

        val mediaMeta = MediaMeta()
        mediaMeta.fileName = filename
        mediaMeta.imageUrl = filename

        val post = Post()
        post.setPostType(PostType.IMAGE)
        post.setCaption("This is a caption")
        post.setMediaMeta(mediaMeta)
        post.setUser(user)
        post.createdAt = LocalDateTime.now()
        post.updatedAt = LocalDateTime.now()

        return ResponseEntity.ok().body(
            ApiResponse.builder<PostResponse>()
                .message("Successfully upload post")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .data(
                    postMapper.toPostResponse(
                        postRepository.save(post)
                    )
                )
                .build()
        )
    }
}

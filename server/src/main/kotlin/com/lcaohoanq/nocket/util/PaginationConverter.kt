package com.lcaohoanq.nocket.util

import com.lcaohoanq.nocket.api.PageResponse
import com.lcaohoanq.nocket.metadata.PaginationMeta
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors

interface PaginationConverter {
    fun <T> toPaginationMeta(page: Page<T>, pageable: Pageable): PaginationMeta {
        return PaginationMeta.Builder()
            .setTotalPages(page.totalPages)
            .setTotalItems(page.totalElements)
            .setCurrentPage(pageable.pageNumber)
            .setPageSize(pageable.pageSize)
            .build()
    }

    fun <T, R> mapPageResponse(
        entityPage: Page<T>,
        pageable: Pageable,
        mapper: Function<T, R>?,
        message: String?
    ): PageResponse<R> {
        val responseList = entityPage.content.stream()
            .map(mapper)
            .toList()

        return PageResponse.pageBuilder<R>()
            .data(responseList)
            .pagination(toPaginationMeta(entityPage, pageable))
            .statusCode(HttpStatus.OK.value())
            .isSuccess(true)
            .message(message)
            .build()
    }

    fun <T, R, C : Collection<R>?> mapPageResponse(
        entityPage: Page<T>,
        pageable: Pageable,
        mapper: Function<T, R>?,
        message: String?,
        collectionSupplier: Supplier<C>
    ): PageResponse<R> {
        // Use the supplier to create a new collection and map the results into it
        val responseCollection = entityPage.content.stream()
            .map(mapper)
            .collect(Collectors.toCollection(collectionSupplier))

        return PageResponse.pageBuilder<R>()
            .data(responseCollection)
            .pagination(toPaginationMeta(entityPage, pageable))
            .statusCode(HttpStatus.OK.value())
            .isSuccess(true)
            .message(message)
            .build()
    }
}

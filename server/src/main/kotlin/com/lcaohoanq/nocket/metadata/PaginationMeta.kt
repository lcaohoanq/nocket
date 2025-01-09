package com.lcaohoanq.nocket.metadata

import com.fasterxml.jackson.annotation.JsonProperty

data class PaginationMeta private constructor(
    @JsonProperty("total_pages")
    val totalPages: Int,

    @JsonProperty("total_items")
    val totalItems: Long,

    @JsonProperty("current_page")
    val currentPage: Int,

    @JsonProperty("page_size")
    val pageSize: Int
) {

    class Builder {
        private var totalPages = 0
        private var totalItems: Long = 0
        private var currentPage = 0
        private var pageSize = 0

        fun setTotalPages(totalPages: Int) = apply { this.totalPages = totalPages }
        fun setTotalItems(totalItems: Long) = apply { this.totalItems = totalItems }
        fun setCurrentPage(currentPage: Int) = apply { this.currentPage = currentPage }
        fun setPageSize(pageSize: Int) = apply { this.pageSize = pageSize }

        fun build(): PaginationMeta {
            return PaginationMeta(totalPages, totalItems, currentPage, pageSize)
        }
    }
}

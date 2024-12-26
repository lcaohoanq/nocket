package com.lcaohoanq.nocket.metadata

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.lcaohoanq.nocket.base.entity.BaseMedia
import jakarta.persistence.Embeddable

@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
class MediaMeta : BaseMedia() {
    @JsonProperty("file_name")
    var fileName: String? = null

    @JsonProperty("mime_type")
    var mimeType: String? = null

    @JsonProperty("file_size")
    var fileSize: Long? = null
    
    @JsonProperty("image_url")
    var imageUrl: String? = null

    @JsonProperty("video_url")
    var videoUrl: String? = null
}


package com.eit.kickit.models

class Post(
    var post_id: Int,
    var adv_id: Int,
    var poster_name: String,
    var post_caption: String,
    var post_status: Boolean,
    var post_url: String,
    var post_likes: Int
)
package com.example.task005.models.business

data class CardData(
    var total_matches_count: Int = 0,
    var main_post: MainPost = MainPost(),
    var horizontal_cards: ArrayList<HorizontalCard> = ArrayList()
)
package com.example.task005.models.business

data class MainPost(
    var uuid: String = "",
    var type: Type = Type(),
    var title: String = "",
    var sub_info: ArrayList<SubInfo> = ArrayList(),
    var post_uuid: String = "",
    var max_budget: Long = 0L,
    var match_count: Int = 0,
    var info: String = "",
    var assigned_to: AssignedTo = AssignedTo()
)
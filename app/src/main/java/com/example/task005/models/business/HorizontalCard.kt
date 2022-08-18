package com.example.task005.models.business

data class HorizontalCard(
    var uuid: String = "",
    var type: Type = Type(),
    var title: String = "",
    var sub_info: ArrayList<SubInfo> = ArrayList(),
    var price: Long = 0L,
    var post_uuid: String = "",
    var info: String = "",
    var assigned_to: AssignedTo = AssignedTo()
)
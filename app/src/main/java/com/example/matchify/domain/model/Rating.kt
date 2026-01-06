package com.example.matchify.domain.model

data class Rating(
    val id: String,
    val talentId: String,
    val recruiterId: String,
    val missionId: String?,
    val score: Int,
    val comment: String?,
    val recommended: Boolean,
    val tags: List<String>?,
    val createdAt: String,
    val recruiterName: String?,
    val recruiterAvatarUrl: String?
)

data class TalentRatingsResult(
    val rating: Double?, // average score
    val averageScore: Double?, // alias for rating
    val bayesianScore: Double? = null,
    val count: Int,
    val ratings: List<Rating>
)

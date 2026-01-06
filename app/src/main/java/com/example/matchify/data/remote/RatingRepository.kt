package com.example.matchify.data.remote

import com.example.matchify.data.remote.api.RatingApi
import com.example.matchify.domain.model.TalentRatingsResult

class RatingRepository(
    private val api: RatingApi
) {
    suspend fun getTalentRatings(talentId: String): TalentRatingsResult {
        return api.getTalentRatings(talentId)
    }
}

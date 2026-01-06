package com.example.matchify.ui.talent.filtering

import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.UserRepository
import com.example.matchify.data.remote.dto.ai.TalentCandidateDto
import com.example.matchify.data.remote.dto.ai.TalentFilterResponseDto
import com.example.matchify.data.remote.dto.ai.MatchBreakdownDto
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

/**
 * Tests unitaires pour TalentFilteringViewModel
 * Valide le bon fonctionnement du modèle IA
 */
@ExperimentalCoroutinesApi
class TalentFilteringViewModelTest {
    
    @Mock
    private lateinit var mockAiRepository: AiRepository
    
    @Mock
    private lateinit var mockUserRepository: UserRepository
    
    private lateinit var viewModel: TalentFilteringViewModel
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = TalentFilteringViewModel(mockAiRepository, mockUserRepository)
    }
    
    @Test
    fun `test filterTalentsForMission loads candidates successfully`() = runTest {
        // Arrange
        val missionId = "mission123"
        val mockResponse = createMockResponse(missionId, 3)
        
        whenever(mockAiRepository.getMissionCandidates(
            missionId = missionId,
            page = null,
            limit = null,
            minScore = null,
            experienceLevel = null,
            location = null,
            skills = null
        )).thenReturn(mockResponse)
        
        // Mock user details
        mockResponse.candidates.forEach { candidate ->
            whenever(mockUserRepository.getUserById(candidate.talentId))
                .thenReturn(createMockUser(candidate.talentId))
        }
        
        // Act
        viewModel.filterTalentsForMission(missionId)
        
        // Wait for loading to complete
        kotlinx.coroutines.delay(100)
        
        // Assert
        val candidates = viewModel.candidates.first()
        assertTrue("Les candidats doivent être chargés", candidates.isNotEmpty())
        assertEquals("Le nombre de candidats doit correspondre", 3, candidates.size)
        assertFalse("Le chargement doit être terminé", viewModel.isLoading.first())
        assertNull("Aucune erreur ne doit être présente", viewModel.errorMessage.first())
    }
    
    @Test
    fun `test filter with minScore filters correctly`() = runTest {
        // Arrange
        val missionId = "mission123"
        val minScore = 70
        val mockResponse = createMockResponse(missionId, 5)
        
        whenever(mockAiRepository.getMissionCandidates(
            missionId = missionId,
            page = null,
            limit = null,
            minScore = minScore,
            experienceLevel = null,
            location = null,
            skills = null
        )).thenReturn(mockResponse.copy(
            candidates = mockResponse.candidates.filter { it.score >= minScore }
        ))
        
        mockResponse.candidates.forEach { candidate ->
            whenever(mockUserRepository.getUserById(candidate.talentId))
                .thenReturn(createMockUser(candidate.talentId))
        }
        
        // Act
        viewModel.filterTalentsForMission(missionId, minScore = minScore)
        
        // Wait
        kotlinx.coroutines.delay(100)
        
        // Assert
        val candidates = viewModel.candidates.first()
        assertTrue("Tous les candidats doivent avoir un score >= minScore",
            candidates.all { it.score >= minScore })
    }
    
    @Test
    fun `test scores are in valid range`() = runTest {
        // Arrange
        val missionId = "mission123"
        val mockResponse = createMockResponse(missionId, 5)
        
        whenever(mockAiRepository.getMissionCandidates(
            any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(mockResponse)
        
        mockResponse.candidates.forEach { candidate ->
            whenever(mockUserRepository.getUserById(candidate.talentId))
                .thenReturn(createMockUser(candidate.talentId))
        }
        
        // Act
        viewModel.filterTalentsForMission(missionId)
        kotlinx.coroutines.delay(100)
        
        // Assert
        val candidates = viewModel.candidates.first()
        assertTrue("Tous les scores doivent être entre 0 et 100",
            candidates.all { it.score in 0..100 })
    }
    
    @Test
    fun `test breakdown consistency with global score`() = runTest {
        // Arrange
        val missionId = "mission123"
        val mockResponse = createMockResponse(missionId, 3)
        
        whenever(mockAiRepository.getMissionCandidates(
            any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(mockResponse)
        
        mockResponse.candidates.forEach { candidate ->
            whenever(mockUserRepository.getUserById(candidate.talentId))
                .thenReturn(createMockUser(candidate.talentId))
        }
        
        // Act
        viewModel.filterTalentsForMission(missionId)
        kotlinx.coroutines.delay(100)
        
        // Assert
        val candidates = viewModel.candidates.first()
        candidates.forEach { candidate ->
            candidate.matchBreakdown?.let { breakdown ->
                val avgBreakdown = listOfNotNull(
                    breakdown.skillsMatch,
                    breakdown.experienceMatch,
                    breakdown.locationMatch
                ).average()
                
                // Le score global devrait être proche de la moyenne du breakdown (±15% tolérance)
                val difference = kotlin.math.abs(candidate.score - avgBreakdown)
                assertTrue(
                    "Le score global ($candidate.score) doit être cohérent avec le breakdown ($avgBreakdown). Différence: $difference",
                    difference <= 15
                )
            }
        }
    }
    
    @Test
    fun `test error handling when API fails`() = runTest {
        // Arrange
        val missionId = "mission123"
        val errorMessage = "API Error"
        
        whenever(mockAiRepository.getMissionCandidates(
            any(), any(), any(), any(), any(), any(), any()
        )).thenThrow(RuntimeException(errorMessage))
        
        // Act
        viewModel.filterTalentsForMission(missionId)
        kotlinx.coroutines.delay(100)
        
        // Assert
        assertNotNull("Une erreur doit être présente", viewModel.errorMessage.first())
        assertTrue("Les candidats doivent être vides en cas d'erreur",
            viewModel.candidates.first().isEmpty())
    }
    
    @Test
    fun `test sortByScoreDescending sorts correctly`() = runTest {
        // Arrange
        val missionId = "mission123"
        val mockResponse = createMockResponse(missionId, 5)
        
        whenever(mockAiRepository.getMissionCandidates(
            any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(mockResponse)
        
        mockResponse.candidates.forEach { candidate ->
            whenever(mockUserRepository.getUserById(candidate.talentId))
                .thenReturn(createMockUser(candidate.talentId))
        }
        
        viewModel.filterTalentsForMission(missionId)
        kotlinx.coroutines.delay(100)
        
        // Act
        viewModel.sortByScoreDescending()
        
        // Assert
        val candidates = viewModel.candidates.first()
        assertTrue("Les candidats doivent être triés par score décroissant",
            candidates.zipWithNext().all { (a, b) -> a.score >= b.score })
    }
    
    // Helper functions
    
    private fun createMockResponse(missionId: String, candidateCount: Int): TalentFilterResponseDto {
        val candidates = (1..candidateCount).map { index ->
            val score = 100 - (index * 10) // Scores décroissants: 90, 80, 70, etc.
            TalentCandidateDto(
                talentId = "talent$index",
                score = score,
                reasons = "Test reason for score $score",
                matchBreakdown = MatchBreakdownDto(
                    skillsMatch = (score - 5).toDouble(),
                    experienceMatch = (score - 3).toDouble(),
                    locationMatch = (score - 2).toDouble()
                )
            )
        }
        
        return TalentFilterResponseDto(
            missionId = missionId,
            candidates = candidates,
            total = candidateCount,
            page = 1,
            pageSize = 20
        )
    }
    
    private fun createMockUser(talentId: String): Pair<UserModel, List<com.example.matchify.domain.model.Project>> {
        val user = UserModel(
            id = talentId,
            fullName = "Test Talent $talentId",
            email = "test$talentId@example.com",
            role = "talent",
            profileImage = null,
            location = "Paris",
            skills = listOf("Kotlin", "Android"),
            description = "Test description"
        )
        return Pair(user, emptyList())
    }
}


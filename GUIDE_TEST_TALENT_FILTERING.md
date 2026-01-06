# Guide de Test - Filtrage IA des Talents

## 🎯 Vue d'ensemble

Ce guide vous explique comment tester le système de filtrage IA des talents, avec ou sans backend disponible.

## 📋 Prérequis

1. ✅ Le code frontend est compilé sans erreurs
2. ✅ Vous avez accès à une mission créée (pour les recruteurs)
3. ⚠️ Backend avec endpoints IA (optionnel pour tester l'UI)

## 🚀 Méthode 1 : Test avec Backend (Recommandé)

### Étape 1 : Vérifier que le backend est démarré

Assurez-vous que votre backend NestJS est démarré et expose les endpoints :
- `GET /ai/mission/:missionId/candidates`
- `POST /ai/talents/filter`

### Étape 2 : Accéder à l'écran de filtrage

**Option A : Depuis l'écran de détails d'une mission**

1. Ouvrez l'application en tant que **recruteur**
2. Allez dans "Missions"
3. Cliquez sur une mission pour voir ses détails
4. Ajoutez un bouton "Voir les talents recommandés" (voir code ci-dessous)

**Option B : Navigation directe (pour test)**

Dans `MainScreen.kt`, la route est déjà ajoutée :
```kotlin
composable("talents_filter/{missionId}") { ... }
```

Pour tester rapidement, vous pouvez temporairement ajouter un bouton dans `MissionListScreenNew` :

```kotlin
// Dans MissionListScreenNew.kt, dans le CustomAppBar ou ailleurs
if (isRecruiter && missions.isNotEmpty()) {
    Button(
        onClick = {
            // Utiliser la première mission pour test
            navController.navigate("talents_filter/${missions.first().missionId}")
        }
    ) {
        Text("Test Filtrage Talents")
    }
}
```

### Étape 3 : Tester les fonctionnalités

1. **Filtrage de base** :
   - L'écran charge automatiquement les candidats pour la mission
   - Vérifiez que les cartes de candidats s'affichent avec leurs scores

2. **Vérifier les scores** :
   - Les scores doivent être entre 0 et 100
   - Les couleurs changent selon le score :
     - 🟢 Vert (80-100%) : High Match
     - 🔵 Bleu (60-79%) : Good Match
     - ⚪ Gris (0-59%) : Match

3. **Vérifier les détails** :
   - Nom du talent
   - Email
   - Localisation
   - Compétences
   - Raisons du score (si disponibles)
   - Breakdown de matching (compétences, expérience, localisation)

4. **Tester le clic sur un candidat** :
   - Cliquer sur une carte doit ouvrir le profil du talent

## 🧪 Méthode 2 : Test avec Données Mockées (Sans Backend)

Si le backend n'est pas encore disponible, vous pouvez tester l'UI avec des données mockées.

### Étape 1 : Créer un Repository Mock

Créez un fichier `AiRepositoryMock.kt` :

```kotlin
package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.ai.TalentFilterRequestDto
import com.example.matchify.data.remote.dto.ai.TalentFilterResponseDto
import com.example.matchify.data.remote.dto.ai.TalentCandidateDto
import com.example.matchify.data.remote.dto.ai.MatchBreakdownDto
import kotlinx.coroutines.delay

class AiRepositoryMock : AiRepository {
    override suspend fun getMissionCandidates(
        missionId: String,
        page: Int?,
        limit: Int?,
        minScore: Int?,
        experienceLevel: String?,
        location: String?,
        skills: List<String>?
    ): TalentFilterResponseDto {
        delay(1000) // Simuler un délai réseau
        
        return TalentFilterResponseDto(
            missionId = missionId,
            candidates = generateMockCandidates(),
            total = 5,
            page = page ?: 1,
            pageSize = limit ?: 20
        )
    }
    
    override suspend fun filterTalents(request: TalentFilterRequestDto): TalentFilterResponseDto {
        delay(1000)
        
        return TalentFilterResponseDto(
            missionId = request.missionId,
            candidates = generateMockCandidates().filter { candidate ->
                // Appliquer les filtres mock
                (request.minScore == null || candidate.score >= request.minScore) &&
                (request.experienceLevel == null || true) // Simplifié
            },
            total = 5,
            page = request.page ?: 1,
            pageSize = request.limit ?: 20
        )
    }
    
    private fun generateMockCandidates(): List<TalentCandidateDto> {
        return listOf(
            TalentCandidateDto(
                talentId = "talent1",
                score = 85,
                reasons = "Excellent match : compétences parfaitement alignées avec la mission, expérience solide dans les technologies requises.",
                matchBreakdown = MatchBreakdownDto(
                    skillsMatch = 90.0,
                    experienceMatch = 85.0,
                    locationMatch = 80.0
                )
            ),
            TalentCandidateDto(
                talentId = "talent2",
                score = 72,
                reasons = "Bon match : compétences pertinentes mais expérience limitée dans certains domaines.",
                matchBreakdown = MatchBreakdownDto(
                    skillsMatch = 75.0,
                    experienceMatch = 70.0,
                    locationMatch = 70.0
                )
            ),
            TalentCandidateDto(
                talentId = "talent3",
                score = 65,
                reasons = "Match acceptable : certaines compétences manquantes mais motivation élevée.",
                matchBreakdown = MatchBreakdownDto(
                    skillsMatch = 60.0,
                    experienceMatch = 70.0,
                    locationMatch = 65.0
                )
            ),
            TalentCandidateDto(
                talentId = "talent4",
                score = 55,
                reasons = "Match faible : compétences de base présentes mais besoin de formation.",
                matchBreakdown = MatchBreakdownDto(
                    skillsMatch = 50.0,
                    experienceMatch = 60.0,
                    locationMatch = 55.0
                )
            ),
            TalentCandidateDto(
                talentId = "talent5",
                score = 45,
                reasons = "Match très faible : profil ne correspond pas aux exigences principales.",
                matchBreakdown = MatchBreakdownDto(
                    skillsMatch = 40.0,
                    experienceMatch = 50.0,
                    locationMatch = 45.0
                )
            )
        )
    }
}
```

### Étape 2 : Modifier la Factory pour utiliser le Mock

Dans `TalentFilteringViewModelFactory.kt`, ajoutez une option pour utiliser le mock :

```kotlin
class TalentFilteringViewModelFactory(
    private val useMock: Boolean = false // Ajouter ce paramètre
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalentFilteringViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            
            val aiRepository = if (useMock) {
                AiRepositoryMock() // Utiliser le mock
            } else {
                AiRepository(apiService.aiApi) // Utiliser le vrai repository
            }
            
            val userRepository = UserRepository(apiService.userApi, prefs)
            return TalentFilteringViewModel(aiRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

### Étape 3 : Utiliser le Mock dans l'écran

Dans `TalentFilteringScreen.kt`, modifiez la création du ViewModel :

```kotlin
@Composable
fun TalentFilteringScreen(
    missionId: String,
    onTalentClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    useMock: Boolean = false, // Ajouter ce paramètre
    viewModel: TalentFilteringViewModel = viewModel(
        factory = TalentFilteringViewModelFactory(useMock = useMock)
    )
) {
    // ... reste du code
}
```

## 🔍 Méthode 3 : Test Unitaires

Créez un fichier de test `TalentFilteringViewModelTest.kt` :

```kotlin
package com.example.matchify.ui.talent.filtering

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class TalentFilteringViewModelTest {
    
    @Test
    fun `test filterTalentsForMission loads candidates`() = runTest {
        // Arrange
        val mockAiRepository = AiRepositoryMock()
        val mockUserRepository = // Créer un mock UserRepository
        
        val viewModel = TalentFilteringViewModel(mockAiRepository, mockUserRepository)
        
        // Act
        viewModel.filterTalentsForMission("mission123")
        
        // Assert
        // Attendre que le chargement soit terminé
        // Vérifier que les candidats sont chargés
        assertTrue(viewModel.candidates.value.isNotEmpty())
    }
    
    @Test
    fun `test filter with minScore filters correctly`() = runTest {
        // Test que le filtre minScore fonctionne
    }
}
```

## 📱 Checklist de Test

### Tests Fonctionnels

- [ ] L'écran se charge sans erreur
- [ ] Les candidats s'affichent avec leurs scores
- [ ] Les scores sont colorés correctement (vert/bleu/gris)
- [ ] Les détails des talents s'affichent (nom, email, compétences)
- [ ] Les raisons du score s'affichent (si disponibles)
- [ ] Le breakdown de matching s'affiche (compétences, expérience, localisation)
- [ ] Le clic sur un candidat ouvre son profil
- [ ] Le bouton retour fonctionne

### Tests de Filtrage

- [ ] Filtrage par score minimum fonctionne
- [ ] Filtrage par niveau d'expérience fonctionne
- [ ] Filtrage par localisation fonctionne
- [ ] Filtrage par compétences fonctionne
- [ ] La pagination fonctionne (si implémentée)

### Tests d'Erreurs

- [ ] Message d'erreur s'affiche si le backend est indisponible
- [ ] Message d'erreur s'affiche si la mission n'existe pas
- [ ] Message "Aucun candidat trouvé" s'affiche si aucun résultat
- [ ] Le bouton "Réessayer" fonctionne

### Tests de Performance

- [ ] Le chargement est rapide (< 2 secondes)
- [ ] Les détails des talents se chargent en parallèle
- [ ] Pas de freeze de l'UI pendant le chargement

## 🐛 Debugging

### Vérifier les logs

Activez les logs dans `TalentFilteringViewModel.kt` :

```kotlin
android.util.Log.d("TalentFiltering", "Filtering talents for mission: $missionId")
android.util.Log.d("TalentFiltering", "Found ${response.candidates.size} candidates")
android.util.Log.d("TalentFiltering", "Loading details for ${talentIds.size} talents")
```

### Vérifier les appels API

Utilisez un intercepteur HTTP pour voir les requêtes :

```kotlin
// Dans ApiService.kt, ajoutez un HttpLoggingInterceptor
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

## 📝 Exemple d'Intégration dans MissionDetailsScreen

Pour ajouter un bouton dans l'écran de détails de mission :

```kotlin
// Dans MissionDetailsScreen.kt
Button(
    onClick = {
        navController.navigate("talents_filter/${mission.missionId}")
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
) {
    Icon(
        imageVector = Icons.Rounded.AutoAwesome,
        contentDescription = null
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("Voir les talents recommandés")
}
```

## ✅ Résultat Attendu

Après avoir suivi ce guide, vous devriez pouvoir :
1. ✅ Voir l'écran de filtrage des talents
2. ✅ Voir les candidats avec leurs scores IA
3. ✅ Filtrer les candidats selon différents critères
4. ✅ Voir les détails de chaque candidat
5. ✅ Naviguer vers le profil d'un candidat

## 🆘 Problèmes Courants

### Problème : "Aucun candidat trouvé"
- **Solution** : Vérifiez que le backend retourne des données pour cette mission
- **Solution** : Vérifiez que les talents existent dans la base de données

### Problème : Les détails des talents ne s'affichent pas
- **Solution** : Vérifiez que `UserRepository.getUserById()` fonctionne
- **Solution** : Vérifiez les logs pour voir les erreurs de récupération

### Problème : Les scores ne s'affichent pas
- **Solution** : Vérifiez que le backend calcule et retourne les scores
- **Solution** : Vérifiez le format de la réponse JSON

## 📚 Ressources

- Documentation backend : `TALENT_FILTERING_BACKEND_INTEGRATION.md`
- Code source : `app/src/main/java/com/example/matchify/ui/talent/filtering/`


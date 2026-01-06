# Intégration du Filtrage IA des Talents (Basé sur Backend)

## Vue d'ensemble

Ce système permet aux **recruteurs** de filtrer intelligemment les talents pour une mission donnée en utilisant des modèles d'intelligence artificielle. Le backend calcule les scores de compatibilité et renvoie les candidats triés par pertinence.

## Architecture Backend (Référence)

D'après la structure backend fournie :

- **Service** : `src/ai/services/talent-filter.service.ts`
  - Récupère la mission (`missionId`)
  - Récupère les talents candidats (filtre dur : rôle, stack, dispo…)
  - Calcule un **score IA** pour chaque talent
  - Renvoie la liste triée

- **DTOs** :
  - `src/ai/dto/talent-filter-request.dto.ts` : Critères de filtrage
  - `src/ai/dto/talent-filter-response.dto.ts` : Liste de talents scorés

- **Endpoints** :
  - `GET /ai/mission/:missionId/candidates` (avec query params)
  - `POST /ai/talents/filter` (avec body)

## Architecture Frontend (Implémenté)

### Structure des fichiers

```
app/src/main/java/com/example/matchify/
├── data/remote/
│   ├── AiApi.kt                          # Endpoints API ajoutés
│   └── AiRepository.kt                   # Méthodes de filtrage ajoutées
├── data/remote/dto/ai/
│   ├── TalentFilterRequestDto.kt        # DTO de requête
│   ├── TalentFilterResponseDto.kt        # DTO de réponse
│   └── TalentCandidateMapper.kt          # Mapper DTO -> Domain
├── domain/model/
│   └── TalentCandidate.kt                # Modèle domaine avec score IA
└── ui/talent/filtering/
    ├── TalentFilteringViewModel.kt        # ViewModel pour la logique
    ├── TalentFilteringViewModelFactory.kt
    ├── TalentFilteringScreen.kt           # Écran principal
    └── components/
        └── TalentCandidateCard.kt         # Composant carte candidat
```

## Utilisation

### 1. Filtrer les talents pour une mission (GET)

```kotlin
val viewModel: TalentFilteringViewModel = viewModel(factory = TalentFilteringViewModelFactory())

// Filtrer avec query params
viewModel.filterTalentsForMission(
    missionId = "mission123",
    page = 1,
    limit = 20,
    minScore = 70, // Score minimum requis
    experienceLevel = "INTERMEDIATE",
    location = "Paris",
    skills = listOf("Kotlin", "Android")
)

// Observer les résultats
val candidates by viewModel.candidates.collectAsState()
val isLoading by viewModel.isLoading.collectAsState()
val totalResults by viewModel.totalResults.collectAsState()
```

### 2. Filtrer avec critères avancés (POST)

```kotlin
val request = TalentFilterRequestDto(
    missionId = "mission123",
    page = 1,
    limit = 20,
    minScore = 70,
    experienceLevel = "INTERMEDIATE",
    location = "Paris",
    skills = listOf("Kotlin", "Android")
)

viewModel.filterTalents(request)
```

### 3. Afficher l'écran de filtrage

```kotlin
// Dans votre navigation (ex: MainScreen.kt)
composable("talents_filter/{missionId}") { backStackEntry ->
    val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
    TalentFilteringScreen(
        missionId = missionId,
        onTalentClick = { talentId ->
            navController.navigate("talent_profile/$talentId")
        },
        onBack = { navController.popBackStack() }
    )
}
```

## Modèle de données

### TalentFilterRequestDto

```kotlin
data class TalentFilterRequestDto(
    val missionId: String,
    val page: Int? = null,
    val limit: Int? = null,
    val minScore: Int? = null, // 0-100
    val experienceLevel: String? = null, // ENTRY, INTERMEDIATE, EXPERT
    val location: String? = null,
    val skills: List<String>? = null
)
```

### TalentFilterResponseDto

```kotlin
data class TalentFilterResponseDto(
    val missionId: String,
    val candidates: List<TalentCandidateDto>,
    val total: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null
)
```

### TalentCandidateDto

```kotlin
data class TalentCandidateDto(
    val talentId: String,
    val score: Int, // Score IA (0-100)
    val reasons: String? = null, // Raisons du score
    val matchBreakdown: MatchBreakdownDto? = null // Détails du matching
)
```

### MatchBreakdownDto

```kotlin
data class MatchBreakdownDto(
    val skillsMatch: Double? = null, // % correspondance compétences
    val experienceMatch: Double? = null, // % correspondance expérience
    val locationMatch: Double? = null, // % correspondance localisation
    val otherFactors: Map<String, Any>? = null
)
```

## Endpoints API

### GET /ai/mission/{missionId}/candidates

**Query Parameters** :
- `page` : Numéro de page (optionnel)
- `limit` : Nombre de résultats par page (optionnel)
- `minScore` : Score minimum requis (optionnel)
- `experienceLevel` : ENTRY, INTERMEDIATE, EXPERT (optionnel)
- `location` : Localisation (optionnel)
- `skills` : Compétences séparées par virgules (optionnel)

**Response** : `TalentFilterResponseDto`

### POST /ai/talents/filter

**Body** : `TalentFilterRequestDto`

**Response** : `TalentFilterResponseDto`

## Exemple d'intégration dans l'écran de détails de mission

Pour ajouter un bouton "Voir les talents recommandés" dans l'écran de détails d'une mission :

```kotlin
// Dans MissionDetailsScreen.kt
Button(
    onClick = {
        navController.navigate("talents_filter/${mission.missionId}")
    },
    modifier = Modifier.fillMaxWidth()
) {
    Icon(
        imageVector = Icons.Rounded.AutoAwesome,
        contentDescription = null
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("Voir les talents recommandés")
}
```

## Fonctionnalités

- ✅ Filtrage par score IA minimum
- ✅ Filtrage par niveau d'expérience
- ✅ Filtrage par localisation
- ✅ Filtrage par compétences
- ✅ Pagination
- ✅ Affichage des scores avec codes couleur
- ✅ Affichage des raisons du score
- ✅ Affichage du breakdown de matching (compétences, expérience, localisation)
- ✅ Récupération automatique des détails complets des talents

## Score de matching

- **80-100%** : High Match (Vert - `#10B981`)
- **60-79%** : Good Match (Bleu - `#3B82F6`)
- **0-59%** : Match (Gris - `#6B7280`)

## Notes importantes

- Le filtrage IA fonctionne uniquement pour les **recruteurs**
- Les scores IA sont calculés par le backend en fonction de la compatibilité entre le profil du talent et les exigences de la mission
- Les détails complets des talents sont récupérés en parallèle après avoir obtenu la liste des candidats
- Le système gère automatiquement les erreurs de récupération des détails individuels

## Prochaines étapes

1. **Backend** : Implémenter les endpoints selon la structure fournie
2. **UI** : Ajouter un composant de filtrage avancé (bottom sheet ou dialog)
3. **Tests** : Ajouter des tests unitaires pour le ViewModel
4. **Cache** : Implémenter un système de cache pour les résultats filtrés
5. **Pagination** : Améliorer la pagination avec scroll infini


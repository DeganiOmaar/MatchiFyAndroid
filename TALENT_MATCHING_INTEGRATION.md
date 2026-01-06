# Intégration du Filtrage et Scoring IA des Talents

## Vue d'ensemble

Cette fonctionnalité permet aux recruteurs de filtrer et scorer les talents en utilisant des modèles d'intelligence artificielle. Le backend est responsable de l'entraînement/exécution des modèles, du calcul des scores et de l'exposition des résultats via des API. Le frontend consomme ces API pour afficher les profils filtrés, les scores et permettre l'interaction utilisateur.

## Architecture

### Backend (à implémenter)

Le backend doit exposer les endpoints suivants :

1. **GET /talents/match**
   - Paramètres : `missionId`, `minScore`, `page`, `limit`
   - Retourne : Liste des talents matchés avec scores IA pour une mission spécifique

2. **POST /talents/filter**
   - Body : `TalentFilterRequestDto` (skills, location, experienceLevel, etc.)
   - Retourne : Liste des talents filtrés avec scores IA

3. **POST /talents/{talentId}/match-score**
   - Paramètres : `talentId`, `missionId` (query)
   - Retourne : Score de matching calculé pour un talent spécifique

### Frontend (implémenté)

#### Structure des fichiers

```
app/src/main/java/com/example/matchify/
├── data/remote/
│   ├── TalentMatchingApi.kt          # Interface API pour le matching
│   └── TalentMatchingRepository.kt   # Repository pour les appels API
├── data/remote/dto/talent/
│   ├── TalentMatchDto.kt             # DTOs pour les réponses API
│   └── TalentMatchDtoMapper.kt       # Mappers DTO -> Domain
├── domain/model/
│   └── TalentMatch.kt                 # Modèle domaine pour un talent avec score
└── ui/talent/matching/
    ├── TalentMatchingViewModel.kt     # ViewModel pour la logique métier
    ├── TalentMatchingViewModelFactory.kt
    ├── TalentMatchingScreen.kt         # Écran principal de liste des talents
    └── components/
        └── TalentMatchCard.kt          # Composant carte pour un talent
```

## Utilisation

### 1. Charger les talents matchés pour une mission

```kotlin
val viewModel: TalentMatchingViewModel = viewModel(factory = TalentMatchingViewModelFactory())

// Charger les talents matchés pour une mission
viewModel.loadMatchedTalentsForMission(
    missionId = "mission123",
    minScore = 60, // Score minimum requis
    page = 1,
    limit = 20
)

// Observer les résultats
val talents by viewModel.talents.collectAsState()
val isLoading by viewModel.isLoading.collectAsState()
val errorMessage by viewModel.errorMessage.collectAsState()
```

### 2. Filtrer les talents avec critères avancés

```kotlin
viewModel.filterTalents(
    missionId = "mission123",
    skills = listOf("Kotlin", "Android", "Jetpack Compose"),
    minScore = 70,
    location = "Paris",
    experienceLevel = "INTERMEDIATE",
    page = 1,
    limit = 20
)
```

### 3. Calculer le score pour un talent spécifique

```kotlin
viewModel.calculateMatchScore(
    talentId = "talent123",
    missionId = "mission123"
)
```

### 4. Afficher l'écran de matching

```kotlin
// Dans votre navigation (ex: MainScreen.kt)
composable("talents_match/{missionId}") { backStackEntry ->
    val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
    TalentMatchingScreen(
        missionId = missionId,
        onTalentClick = { talentId ->
            navController.navigate("talent_profile/$talentId")
        },
        onBack = { navController.popBackStack() }
    )
}
```

## Modèle de données

### TalentMatch (Domain Model)

```kotlin
data class TalentMatch(
    val talentId: String,
    val fullName: String,
    val email: String,
    val profileImage: String?,
    val location: String?,
    val skills: List<String>,
    val talent: List<String>, // Catégories de talent
    val description: String?,
    val matchScore: Int, // Score de 0 à 100
    val reasoning: String?, // Explication du score par l'IA
    val cvUrl: String?
)
```

### Score de matching

- **80-100%** : High Match (Vert - `#10B981`)
- **60-79%** : Good Match (Bleu - `#3B82F6`)
- **0-59%** : Match (Gris - `#6B7280`)

## Exemple d'intégration dans l'écran de détails de mission

Pour ajouter un bouton "Voir les talents recommandés" dans l'écran de détails d'une mission :

```kotlin
// Dans MissionDetailsScreen.kt
Button(
    onClick = {
        navController.navigate("talents_match/${mission.missionId}")
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

## Prochaines étapes

1. **Backend** : Implémenter les endpoints API avec les modèles IA
2. **Tests** : Ajouter des tests unitaires pour le ViewModel et Repository
3. **Filtres avancés** : Ajouter une UI pour les filtres (skills, location, etc.)
4. **Pagination** : Implémenter la pagination complète avec scroll infini
5. **Cache** : Ajouter un système de cache pour les résultats de matching
6. **Notifications** : Notifier les recruteurs quand de nouveaux talents matchent leurs missions


# Intégration du Filtrage IA des Propositions

## Vue d'ensemble

Ce système permet aux **recruteurs** de filtrer intelligemment les propositions qu'ils reçoivent de différents talents pour une mission donnée. Le backend est responsable de l'entraînement/exécution des modèles IA, du calcul des scores de compatibilité et de l'exposition des résultats via des API. Le frontend consomme ces API pour afficher les propositions filtrées, les scores et permettre l'interaction utilisateur.

## Architecture

### Backend (à implémenter)

Le backend doit exposer les endpoints suivants :

1. **POST /recruiter/proposals/filter**
   - Body : `ProposalFilterRequestDto`
   - Retourne : `ProposalFilterResponseDto` avec les propositions filtrées et scores IA

2. **POST /recruiter/proposals/mission/{missionId}/recalculate-scores**
   - Recalcule les scores IA pour toutes les propositions d'une mission
   - Retourne : `ProposalFilterResponseDto`

### Frontend (implémenté)

#### Structure des fichiers

```
app/src/main/java/com/example/matchify/
├── data/remote/
│   ├── ProposalApi.kt                    # Endpoints API pour le filtrage
│   └── ProposalRepository.kt             # Méthodes de filtrage ajoutées
├── data/remote/dto/proposal/
│   └── ProposalFilterRequestDto.kt      # DTOs pour les filtres
└── ui/proposals/
    └── ProposalsViewModel.kt             # Méthodes de filtrage IA ajoutées
```

## Utilisation

### 1. Filtrer les propositions avec critères avancés

```kotlin
val viewModel: ProposalsViewModel = viewModel(factory = ProposalsViewModelFactory())

// Filtrer par score minimum, compétences, statut, etc.
viewModel.filterProposals(
    missionId = "mission123",
    minScore = 70, // Score IA minimum requis
    maxScore = 100,
    status = "NOT_VIEWED", // Filtrer par statut
    skills = listOf("Kotlin", "Android", "Jetpack Compose"),
    talentLocation = "Paris",
    minBudget = 5000,
    maxBudget = 15000,
    sortBy = "score", // Trier par score
    sortOrder = "desc" // Ordre décroissant
)

// Observer les résultats
val proposals by viewModel.proposals.collectAsState()
val averageScore by viewModel.averageScore.collectAsState()
val isLoading by viewModel.isLoading.collectAsState()
```

### 2. Recalculer les scores IA

```kotlin
// Recalculer les scores pour la mission sélectionnée
viewModel.recalculateProposalScores()
```

### 3. Réinitialiser les filtres

```kotlin
viewModel.resetFilters()
```

## Modèle de données

### ProposalFilterRequestDto

```kotlin
data class ProposalFilterRequestDto(
    val missionId: String? = null,
    val minScore: Int? = null, // Score IA minimum (0-100)
    val maxScore: Int? = null,
    val status: String? = null, // NOT_VIEWED, VIEWED, ACCEPTED, REFUSED
    val skills: List<String>? = null,
    val talentLocation: String? = null,
    val minBudget: Int? = null,
    val maxBudget: Int? = null,
    val sortBy: String? = null, // "score", "date", "budget"
    val sortOrder: String? = null, // "asc", "desc"
    val page: Int? = null,
    val limit: Int? = null
)
```

### ProposalFilterResponseDto

```kotlin
data class ProposalFilterResponseDto(
    val proposals: List<ProposalDto>,
    val total: Int? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val averageScore: Double? = null // Score moyen des propositions filtrées
)
```

### Proposal (modèle existant)

Le modèle `Proposal` contient déjà :
- `aiScore: Int?` : Score de compatibilité IA (0-100)
- Toutes les informations de la proposition (message, budget, durée, etc.)

## Exemple d'intégration dans l'UI

### Ajouter un bouton de filtrage dans ProposalsScreen

```kotlin
// Dans RecruiterProposalsScreen
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    // Bouton pour ouvrir le filtre
    Button(
        onClick = { /* Ouvrir bottom sheet de filtrage */ }
    ) {
        Icon(Icons.Rounded.FilterList, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Filtrer")
    }
    
    // Bouton pour recalculer les scores
    IconButton(
        onClick = { viewModel.recalculateProposalScores() }
    ) {
        Icon(Icons.Rounded.AutoAwesome, contentDescription = "Recalculer scores")
    }
}

// Afficher le score moyen si disponible
viewModel.averageScore.collectAsState().value?.let { avgScore ->
    Text(
        text = "Score moyen: ${avgScore.toInt()}%",
        style = MaterialTheme.typography.bodyMedium
    )
}
```

### Bottom Sheet de filtrage

```kotlin
@Composable
fun ProposalFilterBottomSheet(
    viewModel: ProposalsViewModel,
    onDismiss: () -> Unit
) {
    var minScore by remember { mutableStateOf<Int?>(null) }
    var selectedSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // ... autres paramètres
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Filtrer les propositions", style = MaterialTheme.typography.titleLarge)
            
            // Filtre par score minimum
            OutlinedTextField(
                value = minScore?.toString() ?: "",
                onValueChange = { minScore = it.toIntOrNull() },
                label = { Text("Score minimum") },
                placeholder = { Text("Ex: 70") }
            )
            
            // Filtre par compétences (à implémenter avec un composant de sélection)
            
            Button(
                onClick = {
                    viewModel.filterProposals(
                        minScore = minScore,
                        skills = selectedSkills.takeIf { it.isNotEmpty() }
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Appliquer les filtres")
            }
        }
    }
}
```

## Prochaines étapes

1. **Backend** : Implémenter les endpoints API avec les modèles IA
2. **UI** : Créer un composant de filtrage avancé (bottom sheet ou dialog)
3. **Affichage** : Améliorer l'affichage des scores IA dans les cartes de propositions
4. **Tests** : Ajouter des tests unitaires pour le filtrage
5. **Pagination** : Implémenter la pagination complète
6. **Cache** : Ajouter un système de cache pour les résultats filtrés

## Notes importantes

- Le filtrage IA fonctionne uniquement pour les **recruteurs**
- Les scores IA sont calculés par le backend en fonction de la compatibilité entre le profil du talent et les exigences de la mission
- Le filtrage peut être combiné avec le tri AI existant (`aiSortEnabled`)
- Les filtres peuvent être réinitialisés pour revenir à l'affichage normal


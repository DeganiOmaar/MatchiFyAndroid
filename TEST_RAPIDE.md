# 🚀 Test Rapide - Filtrage IA des Talents

## Méthode la plus simple pour tester

### Option 1 : Test Direct (Recommandé)

1. **Ouvrez l'application en tant que recruteur**

2. **Allez dans "Missions"**

3. **Cliquez sur une mission** pour voir ses détails

4. **Ajoutez temporairement ce code dans `MissionDetailsScreen.kt`** :

```kotlin
// Dans MissionDetailsScreen.kt, après le bouton "Apply" (ligne ~129)
// Ajoutez ce bouton pour les recruteurs uniquement

if (!isTalent && mission != null) {
    Button(
        onClick = {
            // Vous devrez passer un callback depuis MainScreen
            // Pour l'instant, utilisez cette navigation directe
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = blueAccent
        )
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Voir les talents recommandés",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
```

5. **Modifiez `MainScreen.kt` pour passer le callback** :

```kotlin
composable("mission_details/{missionId}") { backStackEntry ->
    val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
    com.example.matchify.ui.missions.details.MissionDetailsScreen(
        missionId = missionId,
        onBack = { navController.popBackStack() },
        onCreateProposal = { missionId, missionTitle ->
            navController.navigate("create_proposal/$missionId/$missionTitle")
        },
        onViewRecommendedTalents = { missionId -> // Ajoutez ce callback
            navController.navigate("talents_filter/$missionId")
        }
    )
}
```

### Option 2 : Test via Logcat (Sans UI)

1. **Ouvrez Logcat dans Android Studio**

2. **Filtrez par "TalentFiltering"**

3. **Ajoutez ce code temporaire dans `MainScreen.kt`** pour tester automatiquement :

```kotlin
// Dans MainScreen.kt, dans le composable "missions_list"
LaunchedEffect(Unit) {
    if (userRole == "recruiter") {
        // Attendre 2 secondes puis naviguer automatiquement (pour test)
        kotlinx.coroutines.delay(2000)
        val firstMission = listViewModel.missions.value.firstOrNull()
        firstMission?.let {
            navController.navigate("talents_filter/${it.missionId}")
        }
    }
}
```

### Option 3 : Test avec URL Directe (ADB)

Si vous avez un émulateur/device connecté :

```bash
# Naviguer directement vers l'écran de filtrage
adb shell am start -n com.example.matchify/.MainActivity --es missionId "VOTRE_MISSION_ID"
```

Puis dans le code, ajoutez la navigation automatique.

## ✅ Vérifications Rapides

Une fois l'écran ouvert, vérifiez :

1. ✅ **L'écran se charge** sans crash
2. ✅ **Le titre "Talents Recommandés"** s'affiche
3. ✅ **Les cartes de candidats** s'affichent (ou message "Aucun candidat")
4. ✅ **Les scores** sont visibles avec les bonnes couleurs
5. ✅ **Le bouton retour** fonctionne

## 🐛 Si ça ne marche pas

### Erreur : "Route not found"
- ✅ Vérifiez que la route `talents_filter/{missionId}` est bien dans `MainScreen.kt`

### Erreur : "Cannot resolve TalentFilteringScreen"
- ✅ Vérifiez que le fichier `TalentFilteringScreen.kt` existe
- ✅ Vérifiez les imports dans `MainScreen.kt`

### Erreur : "Backend error"
- ✅ Vérifiez que le backend est démarré
- ✅ Vérifiez l'URL de base dans `ApiService.kt`
- ✅ Utilisez la méthode avec données mockées (voir `GUIDE_TEST_TALENT_FILTERING.md`)

## 📱 Test Complet

Pour un test complet, suivez le guide détaillé : `GUIDE_TEST_TALENT_FILTERING.md`


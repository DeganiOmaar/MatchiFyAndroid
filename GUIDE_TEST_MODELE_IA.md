# Guide de Test du Modèle IA - Filtrage des Talents

## 🎯 Objectif

Ce guide vous permet de tester et valider le modèle IA de filtrage des talents pour vérifier :
- ✅ La cohérence des scores
- ✅ La pertinence des résultats
- ✅ La qualité des raisons fournies
- ✅ La cohérence du breakdown de matching

## 🛠️ Outils de Test Disponibles

### 1. Écran de Test Interactif

**Accès** : Naviguez vers `test_talent_filtering` dans votre app

**Fonctionnalités** :
- Test avec différentes missions
- Filtrage par score minimum
- Affichage des statistiques détaillées
- Validation automatique des résultats
- Visualisation des candidats avec leurs scores

**Utilisation** :
```kotlin
// Dans votre navigation ou depuis n'importe où
navController.navigate("test_talent_filtering")
```

### 2. Tests Unitaires

**Fichier** : `TalentFilteringViewModelTest.kt`

**Tests inclus** :
- ✅ Chargement des candidats
- ✅ Filtrage par score minimum
- ✅ Validation des plages de scores (0-100)
- ✅ Cohérence breakdown vs score global
- ✅ Gestion des erreurs
- ✅ Tri des résultats

**Exécution** :
```bash
./gradlew test --tests TalentFilteringViewModelTest
```

### 3. Utilitaires de Validation

**Fichier** : `ModelValidationUtils.kt`

**Validations disponibles** :
- `validateScores()` : Valide les plages de scores
- `validateBreakdownConsistency()` : Vérifie la cohérence breakdown/score
- `validateReasons()` : Vérifie la présence et qualité des raisons
- `validateSorting()` : Vérifie le tri des résultats
- `generateFullReport()` : Rapport complet de validation

## 📋 Checklist de Validation du Modèle

### Tests de Base

- [ ] **Scores dans la plage valide** : Tous les scores sont entre 0 et 100
- [ ] **Tri par pertinence** : Les candidats sont triés par score décroissant
- [ ] **Raisons présentes** : Les scores élevés (≥70%) ont des raisons détaillées
- [ ] **Breakdown cohérent** : Le breakdown correspond au score global (±10%)

### Tests de Cohérence

- [ ] **Distribution des scores** : Présence de scores variés (pas tous identiques)
- [ ] **Scores élevés** : Au moins un score ≥80% si plusieurs candidats
- [ ] **Breakdown complet** : Tous les éléments du breakdown sont présents
- [ ] **Raisons pertinentes** : Les raisons expliquent réellement le score

### Tests de Performance

- [ ] **Temps de réponse** : < 2 secondes pour 20 candidats
- [ ] **Chargement parallèle** : Les détails des talents se chargent en parallèle
- [ ] **Gestion d'erreurs** : Messages d'erreur clairs en cas d'échec

## 🧪 Scénarios de Test

### Scénario 1 : Test avec Mission Simple

1. Créez une mission avec des compétences claires (ex: "Kotlin", "Android")
2. Testez le filtrage avec cette mission
3. Vérifiez que :
   - Les talents avec ces compétences ont des scores élevés
   - Les raisons mentionnent ces compétences
   - Le breakdown montre un bon match de compétences

### Scénario 2 : Test avec Filtres Stricts

1. Utilisez une mission existante
2. Appliquez un filtre `minScore = 80`
3. Vérifiez que :
   - Tous les candidats retournés ont un score ≥ 80
   - Les candidats sont bien triés
   - Les raisons sont présentes pour tous

### Scénario 3 : Test de Cohérence

1. Testez la même mission plusieurs fois
2. Vérifiez que :
   - Les scores sont cohérents entre les appels
   - L'ordre des candidats est stable
   - Les raisons sont similaires

### Scénario 4 : Test avec Mission Complexe

1. Créez une mission avec plusieurs compétences et critères
2. Testez le filtrage
3. Vérifiez que :
   - Le breakdown montre des correspondances variées
   - Les raisons expliquent tous les aspects
   - Les scores reflètent la complexité

## 📊 Métriques à Surveiller

### Métriques de Qualité

- **Score moyen** : Devrait être entre 50-70% pour un bon modèle
- **Distribution** : Mix de scores élevés, moyens et faibles
- **Cohérence** : Scores similaires pour profils similaires

### Métriques de Performance

- **Temps de réponse** : < 2s pour 20 candidats
- **Taux d'erreur** : < 1%
- **Précision** : Scores alignés avec l'évaluation manuelle

## 🔍 Exemple d'Utilisation de l'Écran de Test

```kotlin
// 1. Ouvrir l'écran de test
navController.navigate("test_talent_filtering")

// 2. Entrer un Mission ID
// Exemple: "507f1f77bcf86cd799439011"

// 3. Optionnel: Définir un score minimum
// Exemple: 70

// 4. Cliquer sur "Tester le Modèle"

// 5. Analyser les résultats :
// - Statistiques affichées
// - Validations automatiques (✅ ou ❌)
// - Liste des candidats avec scores détaillés
```

## 📝 Exemple de Rapport de Validation

Un bon modèle devrait produire :

```
✅ Scores dans la plage 0-100
✅ Scores triés par pertinence
✅ Raisons présentes pour scores élevés
✅ Breakdown cohérent avec score global
✅ Présence de scores élevés

Statistiques:
- Total candidats: 15
- Score moyen: 68%
- Score min: 45%
- Score max: 92%
- High Match (≥80%): 3
- Good Match (60-79%): 7
- Low Match (<60%): 5
```

## 🐛 Debugging

### Problème : Scores incohérents

**Symptômes** : Scores qui ne correspondent pas aux profils

**Actions** :
1. Vérifier les logs du backend
2. Vérifier les données d'entrée (mission, talents)
3. Tester avec des données connues
4. Comparer avec une évaluation manuelle

### Problème : Breakdown incohérent

**Symptômes** : Breakdown très différent du score global

**Actions** :
1. Vérifier la formule de calcul du score global
2. Vérifier que tous les facteurs sont pris en compte
3. Vérifier les poids des différents facteurs

### Problème : Raisons manquantes ou génériques

**Symptômes** : Raisons vides ou trop génériques

**Actions** :
1. Vérifier que le modèle IA génère bien les raisons
2. Vérifier la qualité du prompt utilisé
3. Tester avec différents types de missions

## 📚 Code d'Exemple

### Utiliser les Utilitaires de Validation

```kotlin
import com.example.matchify.ui.talent.filtering.test.ModelValidationUtils

// Dans votre code de test
val candidates = viewModel.candidates.value
val report = ModelValidationUtils.generateFullReport(candidates)

if (!report.isValid) {
    Log.e("Test", "Erreurs: ${report.errors}")
    Log.w("Test", "Avertissements: ${report.warnings}")
} else {
    Log.d("Test", "✅ Modèle validé avec succès!")
    Log.d("Test", "Score moyen: ${report.averageScore}%")
}
```

### Test Programmatique

```kotlin
// Tester le modèle avec différentes missions
val testMissions = listOf(
    "mission1", // Mission simple
    "mission2", // Mission complexe
    "mission3"  // Mission avec critères stricts
)

testMissions.forEach { missionId ->
    viewModel.filterTalentsForMission(missionId)
    delay(2000) // Attendre le résultat
    
    val candidates = viewModel.candidates.value
    val report = ModelValidationUtils.generateFullReport(candidates)
    
    Log.d("Test", "Mission $missionId: ${report.candidatesCount} candidats, " +
          "score moyen: ${report.averageScore.toInt()}%, " +
          "valide: ${report.isValid}")
}
```

## ✅ Résultat Attendu

Après avoir effectué tous les tests, vous devriez avoir :

1. ✅ **Confiance dans le modèle** : Les scores sont cohérents et pertinents
2. ✅ **Validation des résultats** : Les candidats sont bien classés
3. ✅ **Compréhension du modèle** : Vous savez comment il fonctionne
4. ✅ **Identification des problèmes** : Vous pouvez détecter les incohérences

## 🎯 Prochaines Étapes

1. **Tester avec des données réelles** : Utilisez de vraies missions et talents
2. **Comparer avec évaluation manuelle** : Vérifiez que le modèle correspond à votre jugement
3. **Ajuster les paramètres** : Modifiez les poids du modèle si nécessaire
4. **Documenter les résultats** : Notez les performances et les limites du modèle


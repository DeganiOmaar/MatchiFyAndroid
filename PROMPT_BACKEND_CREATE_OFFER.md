# Prompt pour le Backend - Endpoint POST /offers

## Contexte
L'application Android nécessite un endpoint pour permettre aux talents de créer des offres (offers). Actuellement, l'endpoint retourne une erreur 404, ce qui indique qu'il n'existe pas ou n'est pas accessible.

## Endpoint Requis

### POST /offers

**Description**: Crée une nouvelle offre pour un talent connecté.

**Authentification**: Requis (Bearer Token dans le header Authorization)

**Content-Type**: `multipart/form-data`

**Paramètres** (tous dans le body multipart):

**Champs requis**:
- `category` (String, text/plain): La catégorie de l'offre (ex: "Web Development", "Mobile Development", etc.)
- `title` (String, text/plain): Le titre de l'offre
- `keywords` (Array<String>, text/plain): Liste des mots-clés associés à l'offre
- `price` (String, text/plain): Le prix de l'offre en euros (nombre entier)
- `description` (String, text/plain): La description détaillée de l'offre
- `banner` (File, image/*): Image bannière de l'offre (obligatoire)

**Champs optionnels**:
- `capabilities` (Array<String>, text/plain): Liste des capacités/compétences (ex: ["can work in society", "freelance"])
- `gallery` (Array<File>, image/*): Images de la galerie (maximum 10 images)
- `video` (File, video/*): Vidéo d'introduction de l'offre

**Comportement attendu**:
1. Vérifier que l'utilisateur est authentifié
2. Vérifier que l'utilisateur est un talent (pas un recruteur)
3. Valider les champs requis :
   - `category` ne doit pas être vide
   - `title` ne doit pas être vide
   - `keywords` doit contenir au moins un élément
   - `price` doit être un nombre positif
   - `description` ne doit pas être vide
   - `banner` doit être une image valide
4. Valider les champs optionnels :
   - `gallery` ne doit pas dépasser 10 images
   - `video` doit être un fichier vidéo valide
5. Sauvegarder les fichiers (banner, gallery, video) sur le serveur
6. Créer l'offre dans la base de données avec :
   - L'ID du talent connecté (`talentId`)
   - Tous les champs fournis
   - Les URLs des fichiers sauvegardés
   - La date de création (`createdAt`)
7. Retourner l'offre créée avec son ID

**Réponses**:

**201 Created** - Offre créée avec succès
```json
{
  "id": "offer_id",
  "talentId": "talent_id",
  "category": "Web Development",
  "title": "Développement d'applications web modernes",
  "keywords": ["React", "Node.js", "TypeScript"],
  "price": 5000,
  "description": "Je propose mes services pour développer des applications web modernes...",
  "capabilities": ["can work in society", "freelance"],
  "bannerUrl": "https://example.com/uploads/banner_123.jpg",
  "galleryUrls": [
    "https://example.com/uploads/gallery_1.jpg",
    "https://example.com/uploads/gallery_2.jpg"
  ],
  "videoUrl": "https://example.com/uploads/video_123.mp4",
  "createdAt": "2026-01-02T10:00:00.000Z",
  "updatedAt": "2026-01-02T10:00:00.000Z"
}
```

**400 Bad Request** - Données invalides
```json
{
  "error": "Bad Request",
  "message": "Les champs requis sont manquants ou invalides",
  "fieldErrors": {
    "title": "Le titre est requis",
    "price": "Le prix doit être un nombre positif",
    "banner": "L'image bannière est requise"
  }
}
```

**401 Unauthorized** - Utilisateur non authentifié
```json
{
  "error": "Unauthorized",
  "message": "Token manquant ou invalide"
}
```

**403 Forbidden** - L'utilisateur n'est pas un talent
```json
{
  "error": "Forbidden",
  "message": "Seuls les talents peuvent créer des offres"
}
```

**404 Not Found** - Endpoint non trouvé (si l'endpoint n'existe pas encore)
```json
{
  "error": "Not Found",
  "message": "Endpoint /offers non trouvé"
}
```

**413 Payload Too Large** - Fichiers trop volumineux
```json
{
  "error": "Payload Too Large",
  "message": "Les fichiers sont trop volumineux. Taille maximale: 10MB pour les images, 50MB pour les vidéos"
}
```

**500 Internal Server Error** - Erreur serveur
```json
{
  "error": "Internal Server Error",
  "message": "Erreur lors de la création de l'offre"
}
```

## Points importants

1. **Authentification**: Seuls les talents authentifiés peuvent créer des offres
2. **Validation**: Valider tous les champs requis avant de traiter les fichiers
3. **Stockage des fichiers**: 
   - Sauvegarder les fichiers dans un dossier dédié (ex: `/uploads/offers/`)
   - Générer des noms de fichiers uniques pour éviter les collisions
   - Retourner les URLs complètes des fichiers sauvegardés
4. **Limites**:
   - Maximum 10 images dans la galerie
   - Taille maximale recommandée : 10MB par image, 50MB pour la vidéo
5. **Format des données**:
   - `keywords` et `capabilities` sont des tableaux de chaînes
   - `price` est un nombre entier (en euros)
   - Les fichiers sont envoyés en multipart/form-data

## Exemple d'implémentation (Node.js/Express avec Multer)

```javascript
const multer = require('multer');
const path = require('path');
const fs = require('fs');

// Configuration Multer pour les fichiers
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const uploadPath = 'uploads/offers/';
    if (!fs.existsSync(uploadPath)) {
      fs.mkdirSync(uploadPath, { recursive: true });
    }
    cb(null, uploadPath);
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, file.fieldname + '-' + uniqueSuffix + path.extname(file.originalname));
  }
});

const upload = multer({
  storage: storage,
  limits: {
    fileSize: 50 * 1024 * 1024 // 50MB max
  },
  fileFilter: (req, file, cb) => {
    if (file.fieldname === 'banner' || file.fieldname === 'gallery') {
      if (!file.mimetype.startsWith('image/')) {
        return cb(new Error('Seules les images sont autorisées pour banner et gallery'));
      }
    }
    if (file.fieldname === 'video') {
      if (!file.mimetype.startsWith('video/')) {
        return cb(new Error('Seules les vidéos sont autorisées pour video'));
      }
    }
    cb(null, true);
  }
});

router.post('/offers', 
  authenticateToken,
  checkTalentRole,
  upload.fields([
    { name: 'banner', maxCount: 1 },
    { name: 'gallery', maxCount: 10 },
    { name: 'video', maxCount: 1 }
  ]),
  async (req, res) => {
    try {
      const talentId = req.user.id; // Depuis le token JWT
      
      // Validation des champs requis
      const { category, title, keywords, price, description, capabilities } = req.body;
      
      if (!category || !title || !keywords || !price || !description) {
        return res.status(400).json({
          error: "Bad Request",
          message: "Les champs requis sont manquants"
        });
      }
      
      if (!req.files || !req.files['banner'] || req.files['banner'].length === 0) {
        return res.status(400).json({
          error: "Bad Request",
          message: "L'image bannière est requise"
        });
      }
      
      // Valider le prix
      const priceInt = parseInt(price);
      if (isNaN(priceInt) || priceInt <= 0) {
        return res.status(400).json({
          error: "Bad Request",
          message: "Le prix doit être un nombre positif"
        });
      }
      
      // Valider les keywords (doit être un tableau)
      const keywordsArray = Array.isArray(keywords) ? keywords : [keywords];
      if (keywordsArray.length === 0) {
        return res.status(400).json({
          error: "Bad Request",
          message: "Au moins un mot-clé est requis"
        });
      }
      
      // Valider la galerie (max 10 images)
      const galleryFiles = req.files['gallery'] || [];
      if (galleryFiles.length > 10) {
        return res.status(400).json({
          error: "Bad Request",
          message: "Maximum 10 images dans la galerie"
        });
      }
      
      // Traiter les fichiers
      const bannerFile = req.files['banner'][0];
      const bannerUrl = `${req.protocol}://${req.get('host')}/uploads/offers/${bannerFile.filename}`;
      
      const galleryUrls = galleryFiles.map(file => 
        `${req.protocol}://${req.get('host')}/uploads/offers/${file.filename}`
      );
      
      const videoFile = req.files['video']?.[0];
      const videoUrl = videoFile ? 
        `${req.protocol}://${req.get('host')}/uploads/offers/${videoFile.filename}` : 
        null;
      
      // Traiter les capabilities (peut être un tableau ou une chaîne)
      const capabilitiesArray = capabilities ? 
        (Array.isArray(capabilities) ? capabilities : [capabilities]) : 
        [];
      
      // Créer l'offre dans la base de données
      const offer = await Offer.create({
        talentId: talentId,
        category: category,
        title: title,
        keywords: keywordsArray,
        price: priceInt,
        description: description,
        capabilities: capabilitiesArray,
        bannerUrl: bannerUrl,
        galleryUrls: galleryUrls,
        videoUrl: videoUrl,
        createdAt: new Date(),
        updatedAt: new Date()
      });
      
      res.status(201).json(offer);
    } catch (error) {
      console.error('Error creating offer:', error);
      res.status(500).json({
        error: "Internal Server Error",
        message: "Erreur lors de la création de l'offre"
      });
    }
  }
);

// Middleware pour vérifier que l'utilisateur est un talent
function checkTalentRole(req, res, next) {
  if (req.user.role !== 'talent') {
    return res.status(403).json({
      error: "Forbidden",
      message: "Seuls les talents peuvent créer des offres"
    });
  }
  next();
}
```

## Tests à effectuer

1. ✅ Créer une offre avec tous les champs requis
2. ✅ Créer une offre avec des champs optionnels (capabilities, gallery, video)
3. ✅ Tentative de création sans authentification (doit retourner 401)
4. ✅ Tentative de création par un recruteur (doit retourner 403)
5. ✅ Tentative de création sans champs requis (doit retourner 400)
6. ✅ Tentative de création avec plus de 10 images dans la galerie (doit retourner 400)
7. ✅ Tentative de création avec des fichiers trop volumineux (doit retourner 413)
8. ✅ Vérifier que les fichiers sont bien sauvegardés sur le serveur
9. ✅ Vérifier que les URLs retournées sont accessibles

## Notes importantes

- L'endpoint doit accepter `multipart/form-data` pour les fichiers
- Les tableaux (`keywords`, `capabilities`, `gallery`) peuvent être envoyés comme plusieurs champs avec le même nom ou comme un tableau JSON
- Assurez-vous que le dossier de stockage des fichiers existe et a les bonnes permissions
- Générez des noms de fichiers uniques pour éviter les collisions


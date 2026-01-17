# Script d'export du projet Scala - Graphes de Markov
# Usage: .\export-projet-simple.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "EXPORT DU PROJET - GRAPHES DE MARKOV" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = $PSScriptRoot
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$exportName = "ProjetScala_GraphesMarkov_$timestamp"
$exportDir = Join-Path $projectRoot $exportName

# Créer le dossier d'export
Write-Host "[1/5] Création du dossier d'export..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path $exportDir -Force | Out-Null
Write-Host "  ✓ Dossier créé: $exportName" -ForegroundColor Green

# Étape 1: Générer les diagrammes PNG si possible
Write-Host ""
Write-Host "[2/5] Génération des diagrammes PlantUML..." -ForegroundColor Yellow
$diagramsDir = Join-Path $projectRoot "diagrammes"
$diagramsGenerated = $false

# Télécharger PlantUML si nécessaire
$plantUmlJar = Join-Path $projectRoot "plantuml.jar"
if (-not (Test-Path $plantUmlJar)) {
    Write-Host "  → Téléchargement de PlantUML..." -ForegroundColor Gray
    try {
        $plantUmlUrl = "https://github.com/plantuml/plantuml/releases/download/v1.2024.0/plantuml-1.2024.0.jar"
        Invoke-WebRequest -Uri $plantUmlUrl -OutFile $plantUmlJar -TimeoutSec 30
        Write-Host "  ✓ PlantUML téléchargé" -ForegroundColor Green
    } catch {
        Write-Host "  ⚠ Téléchargement échoué" -ForegroundColor Yellow
    }
}

# Générer les PNG
if (Test-Path $plantUmlJar) {
    try {
        $jav (PUML + PNG si générés)
if (Test-Path "diagrammes") {
    Copy-Item -Path "diagrammes" -Destination $exportDir -Recurse -Force
    Write-Host "  ✓ Diagrammes (.puml et .png)" -ForegroundColor Green
}

# Rapport Markdown
Write-Host ""
Write-Host "[4/5] Copie du rapport..." -ForegroundColor Yellow
Copy-Item -Path "RAPPORT.md" -Destination $exportDir -Force
Write-Host "  ✓ RAPPORT.md (avec liens vers images)nerated = $true
        }
    } catch {
        Write-Host "  ⚠ Génération échouée (Java requis)" -ForegroundColor Yellow
    }
}

if (-not $diagramsGenerated) {
    Write-Host "  → Les diagrammes .puml seront inclus" -ForegroundColor Gray
    Write-Host "  → À exporter manuellement depuis VSCode si besoin" -ForegroundColor Cyan
}

# Étape 2: Copier les fichiers
Write-Host ""
Write-Host "[3/5] Copie des fichiers sources et exemples..." -ForegroundColor Yellow

# Sources
Copy-Item -Path "src" -Destination $exportDir -Recurse -Force
Write-Host "  ✓ Sources Scala" -ForegroundColor Green

# Exemples
Copy-Item -Path "exemples" -Destination $exportDir -Recurse -Force
Write-Host "  ✓ Exemples" -ForegroundColor Green

# Build
if (Test-Path "build.sbt") {
    Copy-Item -Path "build.sbt" -Destination $exportDir -Force
    Write-Host "  ✓ build.sbt" -ForegroundColor Green
}

# Scaladoc
if (Test-Path "scaladoc") {
    Copy-Item -Path "scaladoc" -Destination $exportDir -Recurse -Force
    Write-Host "  ✓ Scaladoc" -ForegroundColor Green
}

# Diagrammes
if (Test-Path "diagrammes") {
    Copy-Item -Path "diagrammes" -Destination $exportDir -Recurse -Force
    Write-Host "  ✓ Diagrammes" -ForegroundColor Green
}

# Rapport
Copy-Item -Path "RAPPORT.md" -Destination $exportDir -Force
Write-Host "  ✓ Rapport" -ForegroundColor Green
Write-Host ""
Write-Host "[5/5] Création du README..." -ForegroundColor Yellow
$readmeContent = @"
# Projet Scala - Graphes de Markov

## Contenu de l'archive

- **src/** : Fichiers sources Scala
- **exemples/** : Fichiers d'exemples pour tester le programme
- **scaladoc/** : Documentation API générée automatiquement
- **diagrammes/** : Diagrammes UML (images PNG)
- **build.sbt** : Fichier de configuration du projet
- **RAPPORT.md** : Rapport de projet au format Markdown

## Compilation et exécution

### Avec Scala-CLI (recommandé)
``````bash
scala-cli run src/
``````

### Avec SBT
``````bash
sbt run
``````

### Compilation manuelle
``````bash
scalac src/*.scala -d bin/
scala -cp bin/ Main
``````

## Visualisation du rapport

Le rapport **RAPPORT.md** contient des diagrammes UML.

### Option 1: VSCode (recommandé)
1. Ouvrir RAPPORT.md dans VSCode
2. Appuyer sur Ctrl+Shift+V pour prévisualiser
3. Les images dans diagrammes/*.png s'afficheront automatiquement

### Option 2: En ligne
- Copier le contenu sur https://stackedit.io/ ou https://dillinger.io/

### Option 3: Convertir en PDF
``````bash
# Si Pandoc installé
pandoc RAPPORT.md -o RAPPORT.pdf
``````
========================================" -ForegroundColor Cyan
Write-Host "CRÉATION DE L'ARCHIVE FINALE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
$archivePath = "$exportName.zip"
Compress-Archive -Path $exportDir -DestinationPath $archivePath -Force

# Nettoyer le dossier temporaire
Remove-Item -Recurse -Force $exportDir

# Résultat
Write-Host "✅ EXPORT TERMINÉ AVEC SUCCÈS!" -ForegroundColor Green
Write-Host ""
Write-Host "Archive créée: $archivePath" -ForegroundColor Cyan
Write-Host "Taille: $([math]::Round((Get-Item $archivePath).Length / 1MB, 2)) MB" -ForegroundColor Gray
Write-Host ""
Write-Host "Contenu de l'archive:" -ForegroundColor Yellow
Write-Host "  • src/                - Fichiers sources Scala" -ForegroundColor Gray
Write-Host "  • exemples/           - Fichiers d'exemples" -ForegroundColor Gray
Write-Host "  • scaladoc/           - Documentation API" -ForegroundColor Gray
Write-Host "  • diagrammes/         - Diagrammes UML (PNG uniquement)" -ForegroundColor Gray
Write-Host "  • build.sbt           - Configuration du projet" -ForegroundColor Gray
Write-Host "  • RAPPORT.md          - Rapport au format Markdown" -ForegroundColor Gray
Write-Host "  • README.md           - Instructions" -ForegroundColor Gray
Write-Host ""
if ($diagramsGenerated) {
    Write-Host "✓ Les diagrammes PNG sont inclus dans le rapport" -ForegroundColor Green
} else {
    Write-Host "ℹ Les diagrammes .puml sont inclus (à générer si besoin)" -ForegroundColor Cyan
}
Write-Host ""
Write-Host "📖 Le rapport RAPPORT.md contient des liens vers les images" -ForegroundColor Cyan
Write-Host "   Ouvrir avec VSCode (Ctrl+Shift+V) pour voir les diagrammes" -ForegroundColor Gray
Write-Host ""
Write-Host "Prêt pour soumission! 🎓" -ForegroundColor Green
Write-Host ""
Write-Host "Appuyez sur une touche pour continuer..." -ForegroundColor DarkGray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")scala        # Programme principal
├── exemples/          # Fichiers de test
│   ├── exemple1.txt
│   ├── exemple2.txt
│   └── ...
├── diagrammes/        # Diagrammes UML
│   ├── diagramme_classes.png
│   └── diagramme_sequence.png
└── scaladoc/          # Documentation API
``````

## Tests

Des exemples de graphes sont fournis dans le dossier **exemples/** :
- exemple1.txt : Graphe simple 4 sommets
- exemple_meteo.txt : Application météorologique
- exemple_valid_step3.txt : Test de validation

## Documentation

La Scaladoc complète est disponible dans **scaladoc/index.html**
Ouvrir dans un navigateur pour consulter la documentation API.

---

**Auteur** : Arthur  
**Date** : $(Get-Date -Format "dd MMMM yyyy")  
**Projet** : Graphes de Markov en Scala  
**Langage** : Scala 3.7.4
"@

$readmeContent | Out-File -FilePath (Join-Path $exportDir "README.md") -Encoding UTF8
Write-Host "  ✓ README.md créé
$readmeContent | Out-File -FilePath (Join-Path $exportDir "README.md") -Encoding UTF8
Write-Host "  ✓ README.md" -ForegroundColor Green

# Créer l'archive
Write-Host ""
Write-Host "Création de l'archive ZIP..." -ForegroundColor Yellow
$archivePath = "$exportName.zip"
Compress-Archive -Path $exportDir -DestinationPath $archivePath -Force

# Nettoyer
Remove-Item -Recurse -Force $exportDir

# Résultat
Write-Host ""
Write-Host "✅ EXPORT TERMINÉ!" -ForegroundColor Green
Write-Host ""
Write-Host "Archive créée: $archivePath" -ForegroundColor Cyan
Write-Host "Taille: $([math]::Round((Get-Item $archivePath).Length / 1MB, 2)) MB" -ForegroundColor Gray
Write-Host ""
Write-Host "⚠ IMPORTANT:" -ForegroundColor Yellow
Write-Host "Le rapport est au format Markdown (.md)" -ForegroundColor Yellow
Write-Host ""
Write-Host "Pour convertir en Word/PDF, utilisez:" -ForegroundColor Cyan
Write-Host "  pandoc RAPPORT.md -o RAPPORT.docx" -ForegroundColor Gray
Write-Host ""
Write-Host "Ou ouvrez RAPPORT.md dans VSCode et exportez en PDF" -ForegroundColor Gray
Write-Host ""

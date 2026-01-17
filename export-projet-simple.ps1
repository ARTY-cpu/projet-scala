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

Write-Host "[1/5] Creation du dossier d'export..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path $exportDir -Force | Out-Null
Write-Host "  [OK] Dossier cree: $exportName" -ForegroundColor Green

Write-Host ""
Write-Host "[2/5] Generation des diagrammes PlantUML..." -ForegroundColor Yellow
$diagramsDir = Join-Path $projectRoot "diagrammes"
$diagramsGenerated = $false

$plantUmlJar = Join-Path $projectRoot "plantuml.jar"
if (-not (Test-Path $plantUmlJar)) {
    Write-Host "  -> Telechargement de PlantUML..." -ForegroundColor Gray
    try {
        $plantUmlUrl = "https://github.com/plantuml/plantuml/releases/download/v1.2024.0/plantuml-1.2024.0.jar"
        Invoke-WebRequest -Uri $plantUmlUrl -OutFile $plantUmlJar -TimeoutSec 30
        Write-Host "  [OK] PlantUML telecharge" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Telechargement echoue" -ForegroundColor Yellow
    }
}

if (Test-Path $plantUmlJar) {
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        Push-Location $diagramsDir
        java -jar $plantUmlJar -tpng *.puml 2>$null
        Pop-Location
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  [OK] Diagrammes PNG generes" -ForegroundColor Green
            $diagramsGenerated = $true
        }
    } catch {
        Write-Host "  [WARN] Generation echouee (Java requis)" -ForegroundColor Yellow
    }
}

if (-not $diagramsGenerated) {
    Write-Host "  -> Les diagrammes .puml seront inclus" -ForegroundColor Gray
    Write-Host "  -> A exporter manuellement depuis VSCode si besoin" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "[3/5] Copie des fichiers sources et exemples..." -ForegroundColor Yellow

Copy-Item -Path "src" -Destination $exportDir -Recurse -Force
Write-Host "  [OK] Sources Scala" -ForegroundColor Green

Copy-Item -Path "exemples" -Destination $exportDir -Recurse -Force
Write-Host "  [OK] Exemples" -ForegroundColor Green

if (Test-Path "build.sbt") {
    Copy-Item -Path "build.sbt" -Destination $exportDir -Force
    Write-Host "  [OK] build.sbt" -ForegroundColor Green
}

if (Test-Path "scaladoc") {
    Copy-Item -Path "scaladoc" -Destination $exportDir -Recurse -Force
    Write-Host "  [OK] Scaladoc" -ForegroundColor Green
} else {
    Write-Host "  [WARN] Scaladoc manquant" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[4/5] Copie des diagrammes..." -ForegroundColor Yellow
$exportDiagramsDir = Join-Path $exportDir "diagrammes"
New-Item -ItemType Directory -Path $exportDiagramsDir -Force | Out-Null

$pngFiles = Get-ChildItem -Path "diagrammes" -Filter "*.png" -ErrorAction SilentlyContinue
if ($pngFiles) {
    foreach ($file in $pngFiles) {
        Copy-Item -Path $file.FullName -Destination $exportDiagramsDir -Force
    }
    Write-Host "  [OK] Diagrammes PNG ($($pngFiles.Count) fichiers)" -ForegroundColor Green
} else {
    Write-Host "  [WARN] Aucun fichier PNG trouve" -ForegroundColor Yellow
    Write-Host "  -> Generer depuis VSCode (Alt+D puis Export)" -ForegroundColor Cyan
}

Copy-Item -Path "RAPPORT.md" -Destination $exportDir -Force
Write-Host "  [OK] RAPPORT.md" -ForegroundColor Green

# Supprimer les dossiers .bsp et .scala-build de partout dans l'export
Write-Host ""
Write-Host "[5/5] Nettoyage et mise a jour des timestamps..." -ForegroundColor Yellow
Get-ChildItem -Path $exportDir -Recurse -Directory -Force | Where-Object { $_.Name -eq ".bsp" -or $_.Name -eq ".scala-build" } | ForEach-Object {
    Remove-Item -Path $_.FullName -Recurse -Force
    Write-Host "  [OK] Suppression: $($_.FullName)" -ForegroundColor Gray
}

# Mettre a jour la date de tous les fichiers a maintenant
$now = Get-Date
Get-ChildItem -Path $exportDir -Recurse -File -Force | ForEach-Object {
    $_.LastWriteTime = $now
    $_.CreationTime = $now
}
Write-Host "  [OK] Timestamps mis a jour" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "CREATION DE L'ARCHIVE FINALE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
$archivePath = "$exportName.zip"
Compress-Archive -Path $exportDir -DestinationPath $archivePath -Force

Remove-Item -Recurse -Force $exportDir

Write-Host "[SUCCESS] EXPORT TERMINE AVEC SUCCES!" -ForegroundColor Green
Write-Host ""
Write-Host "Archive creee: $archivePath" -ForegroundColor Cyan
Write-Host "Taille: $([math]::Round((Get-Item $archivePath).Length / 1MB, 2)) MB" -ForegroundColor Gray
Write-Host ""
Write-Host "Contenu de l'archive:" -ForegroundColor Yellow
Write-Host "  - src/                - Fichiers sources Scala" -ForegroundColor Gray
Write-Host "  - exemples/           - Fichiers d'exemples" -ForegroundColor Gray
Write-Host "  - scaladoc/           - Documentation API" -ForegroundColor Gray
Write-Host "  - diagrammes/         - Diagrammes UML (PNG uniquement)" -ForegroundColor Gray
Write-Host "  - build.sbt           - Configuration du projet" -ForegroundColor Gray
Write-Host "  - RAPPORT.md          - Rapport au format Markdown" -ForegroundColor Gray
Write-Host ""
if ($diagramsGenerated) {
    Write-Host "[OK] Les diagrammes PNG sont inclus dans le rapport" -ForegroundColor Green
} else {
    Write-Host "[INFO] Les fichiers PNG doivent etre generes manuellement si necessaire" -ForegroundColor Cyan
}
Write-Host ""
Write-Host "Le rapport RAPPORT.md contient des liens vers les images" -ForegroundColor Cyan
Write-Host "Ouvrir avec VSCode (Ctrl+Shift+V) pour voir les diagrammes" -ForegroundColor Gray
Write-Host ""
Write-Host "Pret pour soumission!" -ForegroundColor Green
Write-Host ""

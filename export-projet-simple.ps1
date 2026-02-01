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
Write-Host "[2/5] Generation de la Scaladoc..." -ForegroundColor Yellow
try {
    $scalaCliCmd = Get-Command scala-cli -ErrorAction Stop
    & scala-cli doc src --output-directory scaladoc --force 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Scaladoc regeneree" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] Scaladoc non regeneree" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  [WARN] scala-cli non trouve, scaladoc existante sera utilisee" -ForegroundColor Yellow
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
Write-Host "[4/5] Copie de la documentation..." -ForegroundColor Yellow

if (Test-Path "RAPPORT.pdf") {
    Copy-Item -Path "RAPPORT.pdf" -Destination $exportDir -Force
    Write-Host "  [OK] RAPPORT.pdf" -ForegroundColor Green
} else {
    Write-Host "  [WARN] RAPPORT.pdf manquant" -ForegroundColor Yellow
}

if (Test-Path "README.md") {
    Copy-Item -Path "README.md" -Destination $exportDir -Force
    Write-Host "  [OK] README.md" -ForegroundColor Green
}

if (Test-Path "inventaire_des_notions.md") {
    Copy-Item -Path "inventaire_des_notions.md" -Destination $exportDir -Force
    Write-Host "  [OK] inventaire_des_notions.md" -ForegroundColor Green
}

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
Write-Host "  - src/                     - Fichiers sources Scala" -ForegroundColor Gray
Write-Host "  - exemples/                - Fichiers d'exemples" -ForegroundColor Gray
Write-Host "  - scaladoc/                - Documentation API" -ForegroundColor Gray
Write-Host "  - build.sbt                - Configuration du projet" -ForegroundColor Gray
Write-Host "  - RAPPORT.pdf              - Rapport au format PDF" -ForegroundColor Gray
Write-Host "  - README                   - Instructions d'utilisation" -ForegroundColor Gray
Write-Host "  - inventaire_des_notions.md - Inventaire des concepts FP" -ForegroundColor Gray
Write-Host ""
Write-Host "Pret pour soumission!" -ForegroundColor Green
Write-Host ""

# GlobeTrotter - Git Push Script
# This script will initialize git and help you push to GitHub

Write-Host "================================" -ForegroundColor Cyan
Write-Host "  GlobeTrotter Git Setup" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Check if git is installed
try {
    git --version | Out-Null
    Write-Host "✅ Git is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Git is not installed. Please install Git first." -ForegroundColor Red
    Write-Host "Download from: https://git-scm.com/download/win" -ForegroundColor Yellow
    exit
}

Write-Host ""

# Initialize git if not already done
if (-not (Test-Path .git)) {
    Write-Host "🔧 Initializing Git repository..." -ForegroundColor Yellow
    git init
    Write-Host "✅ Git repository initialized" -ForegroundColor Green
} else {
    Write-Host "✅ Git repository already initialized" -ForegroundColor Green
}

Write-Host ""
Write-Host "📋 Checking files to be committed..." -ForegroundColor Cyan
Write-Host ""

# Add all files (respecting .gitignore)
git add .

# Show status
$status = git status --short
if ($status) {
    Write-Host "Files that will be committed:" -ForegroundColor Yellow
    git status --short | ForEach-Object { Write-Host "  $_" -ForegroundColor White }
} else {
    Write-Host "⚠️  No changes to commit" -ForegroundColor Yellow
    exit
}

Write-Host ""
Write-Host "🔍 Files that are IGNORED (.md files, secrets, etc.):" -ForegroundColor Cyan
git status --ignored --short | Select-String "^!!" | ForEach-Object {
    Write-Host "  $_" -ForegroundColor DarkGray
} | Select-Object -First 10
Write-Host "  ... and more" -ForegroundColor DarkGray

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
$confirm = Read-Host "❓ Do you want to proceed with commit? (y/n)"

if ($confirm -ne 'y') {
    Write-Host "❌ Operation cancelled" -ForegroundColor Red
    exit
}

Write-Host ""
$commitMessage = Read-Host "📝 Enter commit message (or press Enter for default)"
if ([string]::IsNullOrWhiteSpace($commitMessage)) {
    $commitMessage = "Initial commit: GlobeTrotter travel planning app"
}

Write-Host ""
Write-Host "💾 Committing changes..." -ForegroundColor Yellow
git commit -m "$commitMessage"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Committed successfully!" -ForegroundColor Green
} else {
    Write-Host "❌ Commit failed" -ForegroundColor Red
    exit
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "  Push to GitHub" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Check if remote exists
$remote = git remote get-url origin 2>$null

if (-not $remote) {
    Write-Host "🔗 No remote repository configured" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Steps to create GitHub repository:" -ForegroundColor Cyan
    Write-Host "1. Go to https://github.com/new" -ForegroundColor White
    Write-Host "2. Create a new repository (DON'T initialize with README)" -ForegroundColor White
    Write-Host "3. Copy the repository URL" -ForegroundColor White
    Write-Host ""

    $repoUrl = Read-Host "📥 Enter your GitHub repository URL (e.g., https://github.com/username/repo.git)"

    if ([string]::IsNullOrWhiteSpace($repoUrl)) {
        Write-Host "❌ No URL provided. Exiting..." -ForegroundColor Red
        exit
    }

    Write-Host ""
    Write-Host "🔗 Adding remote repository..." -ForegroundColor Yellow
    git remote add origin $repoUrl
    Write-Host "✅ Remote added: $repoUrl" -ForegroundColor Green
} else {
    Write-Host "✅ Remote repository: $remote" -ForegroundColor Green
}

Write-Host ""
$pushConfirm = Read-Host "❓ Push to GitHub now? (y/n)"

if ($pushConfirm -ne 'y') {
    Write-Host "⏸️  Push cancelled. You can push later with: git push -u origin main" -ForegroundColor Yellow
    exit
}

Write-Host ""
Write-Host "⬆️  Pushing to GitHub..." -ForegroundColor Yellow
Write-Host ""

# Set main branch and push
git branch -M main
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "================================" -ForegroundColor Green
    Write-Host "  ✅ SUCCESS!" -ForegroundColor Green
    Write-Host "================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "🎉 Your code has been pushed to GitHub!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "1. Visit your repository on GitHub" -ForegroundColor White
    Write-Host "2. Add a README.md (create directly on GitHub)" -ForegroundColor White
    Write-Host "3. Add repository description and topics" -ForegroundColor White
    Write-Host "4. Choose a license (MIT, Apache, etc.)" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "❌ Push failed!" -ForegroundColor Red
    Write-Host "Please check your GitHub credentials and repository URL" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "You can try again with: git push -u origin main" -ForegroundColor White
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")


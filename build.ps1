$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force -Path 'build/classes' | Out-Null
    $sourceFiles = @(Get-ChildItem -LiteralPath 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
    & javac -encoding UTF-8 -d 'build/classes' $sourceFiles
    if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
    Write-Host 'Compiled successfully.'
} finally {
    Pop-Location
}

$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build.ps1')
Push-Location $PSScriptRoot
try {
    & java -cp 'build/classes;lib/*' college.management.CollegeManagementApp
    if ($LASTEXITCODE -ne 0) { throw 'Application exited with an error.' }
} finally {
    Pop-Location
}

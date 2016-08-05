function prompt
{
    $date = Get-Date -Format "yyyyMMdd-HH:mm"
    $loc = Get-Location
    Write-Host "$date " -ForegroundColor DarkYello -NoNewline
    Write-Host "$loc " -ForegroundColor Magenta -NoNewline
    Write-Host ">"
}
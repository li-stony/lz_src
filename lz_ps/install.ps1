#
$modulePath = "$HOME\Documents\WindowsPowerShell\Modules\LzPs"
New-Item -Path $modulePath -ItemType directory -ErrorAction Ignore
$currentPath = Split-Path $MyInvocation.MyCommand.Path -Parent
Copy-Item -Destination $modulePath -Path "$currentPath\LzPs.psm1" -Force
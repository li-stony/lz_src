Param(
    $basePath
)
if($basePath -eq $null) {
    $basePath = Resolve-Path .
}
Write-Output "task.ps1: $basePath"

# create file
$time = $date = Get-Date -Format yyyyMM
$fileName = "vps_log_$time.txt"
$path = "$basePath/$fileName"
Get-AzureRmContext | Select-AzureRmContext *>> $path
Start-AzureRmVm -ResourceGroupName vps2 -Name lz-win
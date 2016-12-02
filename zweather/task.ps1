Param(
    $basePath
)
if($basePath -eq $null) {
    $basePath = Resolve-Path .
}
Write-Output "task.ps1: $basePath"
$logPath = "$basePath/log"
Write-Output "logPath: $logPath"

# create file
$time = $date = Get-Date -Format yyyyMM
$fileName = "weather_$time.txt"
$year = Get-Date -Format yyyy
$path = "{0}/{1}/" -f $logPath, $year
if ( !(Test-Path -Path $path )){
    New-Item -ItemType Directory $path
    
} 

$path = "$path/$fileName"
. $basePath/weather.ps1 $basePath *>>$path
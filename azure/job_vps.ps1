$basePath = $script:MyInvocation.MyCommand.Path
$basePath = Split-Path $basePath -Parent
Write-Output $basePath

$jobName = "vps-start"
$jobs = Get-ScheduledJob -Name $jobName -ErrorAction Ignore

if(($jobs -le $null) -or ($jobs.Count -eq 0) ) {
    $options = New-ScheduledJobOption -RequireNetwork -WakeToRun 
    $trigger1 = New-JobTrigger -Daily -At "10:00 AM"
    $triggers = $trigger1
    $credential = Get-Credential 
    $newJob = Register-ScheduledJob -Name $jobName -Credential $credential  -Trigger $triggers -ScheduledJobOption $options -FilePath "$basePath\task_start.ps1" -ArgumentList "$basePath"
    return $newJob
} else {
    return $jobs
}


$basePath = $script:MyInvocation.MyCommand.Path
$basePath = Split-Path $basePath -Parent
Write-Output $basePath

$jobName = "vps-stop"
$jobs = Get-ScheduledJob -Name $jobName -ErrorAction Ignore

if(($jobs -le $null) -or ($jobs.Count -eq 0) ) {
    $options = New-ScheduledJobOption -RequireNetwork -WakeToRun 
    $trigger1 = New-JobTrigger -Daily -At "7:00 PM"
    $triggers = $trigger1
    $credential = Get-Credential 
    $newJob = Register-ScheduledJob -Name $jobName -Credential $credential  -Trigger $triggers -ScheduledJobOption $options -FilePath "$basePath\task_stop.ps1" -ArgumentList "$basePath"
    return $newJob
} else {
    return $jobs
}
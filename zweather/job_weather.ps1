$basePath = $script:MyInvocation.MyCommand.Path
$basePath = Split-Path $basePath -Parent
Write-Output $basePath

$jobName = "weather_job"
$jobs = Get-ScheduledJob -Name $jobName -ErrorAction Ignore

if(($jobs -le $null) -or ($jobs.Count -eq 0) ) {
    $options = New-ScheduledJobOption -RequireNetwork -WakeToRun 
    $trigger1 = New-JobTrigger -Daily -At "6:00 AM"
    $triggers = $trigger1
    $credential = Get-Credential -UserName cussyou -Message 'Enter Password to Add the Job'
    $newJob = Register-ScheduledJob -Name $jobName -Credential $credential  -Trigger $triggers -ScheduledJobOption $options -FilePath "$basePath\task.ps1" -ArgumentList "$basePath"
    return $newJob
} else {
    return $jobs
}
$basePath = $script:MyInvocation.MyCommand.Path
$basePath = Split-Path $basePath -Parent
Write-Output $basePath

$jobName = "bjaqi_job"
$jobs = Get-ScheduledJob -Name $jobName -ErrorAction Ignore

if(($jobs -le $null) -or ($jobs.Count -eq 0) ) {
    $options = New-ScheduledJobOption -RequireNetwork -WakeToRun 
    $trigger1 = New-JobTrigger -Daily -At "7:15 AM"
    $trigger2 = New-JobTrigger -Daily -At "8:15 PM"
    $trigger3 = New-JobTrigger -Daily -At "1:15 PM"
    $triggers = $trigger1, $trigger2, $trigger3
    $credential = Get-Credential -UserName cussyou -Message 'Enter Password to Add the Job'
    $newJob = Register-ScheduledJob -Name $jobName -Credential $credential  -Trigger $triggers -ScheduledJobOption $options -FilePath "$basePath\beijing\task.ps1" -ArgumentList "$basePath\beijing"
    return $newJob
} else {
    return $jobs
}
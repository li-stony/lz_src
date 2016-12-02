Param(
    $basePath
)
Write-Output (Get-Date -Format yyyyMMdd_HH:mm:ss)
Write-Output "bjaqi.ps1 $basePath"

$dataPath = "$basePath/data"
Write-Output "dataPath :$dataPath"

$time = $date = Get-Date -Format yyyyMMdd_HHmm
$url = "http://zx.bjmemc.com.cn/web/Service.ashx?time=$time"

Write-Output $url
$response = Invoke-WebRequest  $url -Method Get
$content = $response.Content

# create file
$fileName = "bjaqi_$time.txt"
$year = Get-Date -Format yyyy
$path = "{0}/{1}/" -f $dataPath, $year
if ( !(Test-Path -Path $path )){
    New-Item -ItemType Directory $path
    
} 
$month = Get-Date -Format yyyyMM
$path = "{0}/{1}/{2}/" -f $dataPath, $year, $month
if(!(Test-Path -Path $path)) {
    New-Item -ItemType Directory $path
}

$path = "$path/$fileName"
$content = $content | ConvertFrom-JSON |ConvertTo-Json 

# publish detail in github
# I want to record the datas for one year
$content | Out-File -FilePath $path -Encoding utf8 -Force
Write-Output $path

# update git
$gitPath = Split-Path $basePath -Parent
$gitPath = Split-Path $gitPath -Parent
Set-Location $gitPath
Write-Output $gitPath
# pull first
git pull origin master
# then commit
git add aqi/beijing/data
git commit -m "update beijing data"
# at last, push it
git push origin master

# publish summary to calendar, and only once per day
<#
$now = [System.DateTime]::Now
if($now.Hour -eq 9) {
    $table = $content | ConvertFrom-Json
    #Write-Output $table
    $item = $table.Table[0]
    #Write-Output $item.Date_Time
    $publishTime = [System.DateTime]::ParseExact($item.Date_Time, "yyyy-MM-dd HH:mm:ss", $null)
    #Write-Output $publishTime
    foreach($it in $table.Table) {
        if($it.Pollutant -eq $it.PriPollutant){
            $msg = "{0} {1} PriPollutant:{2} Value:{3}" -f 'AQI', 'Beijing', $it.PriPollutant, $it.Value
            Write-Output $msg
            $app = New-Object -ComObject Outlook.Application
            $calendarItem = $app.CreateItem(1)
            $calendarItem.Start = $publishTime
            $calendarItem.End = $publishTime.AddMinutes(15)
            $calendarItem.Subject = $msg
            $calendarItem.Body = 'Create By bjaqi.ps1'
            $calendarItem.ReminderSet = $false
            $calendarItem.Save()
            #$app.Quit()
        }
    }

}
#>
# send alert mail
$table = $content | ConvertFrom-Json
#Write-Output $table
$item = $table.Table[0]
#Write-Output $item.Date_Time
$publishTime = [System.DateTime]::ParseExact($item.Date_Time, "yyyy-MM-dd HH:mm:ss", $null)
#Write-Output $publishTime
foreach($it in $table.Table) {
    if($it.Pollutant -eq $it.PriPollutant){
        $msg = "{0} {1} PriPollutant:{2} Value:{3}" -f 'AQI', 'Beijing', $it.PriPollutant, $it.Value
        Write-Output $msg
        $value = [System.Convert]::ToInt32($it.Value)
        if($value -gt 80) {
            $config = Get-Content -Path "$basePath\config.json" | ConvertFrom-Json
            # send mail
            $mail = [System.Net.Mail.MailMessage]::new()
            $mail.To.Add($config.to)
            $mail.From = $config.user
            $mail.Subject = $msg
            $mail.SubjectEncoding = [System.Text.Encoding]::UTF8
            $mail.Body = $content
            $mail.BodyEncoding = [System.Text.Encoding]::UTF8
            # Write-Output $mail
            $smtp = [System.Net.Mail.SmtpClient]::new()
            $smtp.Host = $config.smtp
            $user = $config.user
            $pass = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($config.pass))
            $smtp.Credentials = [System.Net.NetworkCredential]::new($user, $pass)
            $smtp.Send($mail)
            Write-Output 'Sent mail successfully'
            $smtp.Dispose()
        }
        
    }
}


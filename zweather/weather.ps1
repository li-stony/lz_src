Param(
    $basePath
)
if($basePath -eq $null) {
    $basePath = Resolve-Path .
}
$dataPath = "$basePath\data"
$configPath = "$basePath\config"

$baseUrl = "https://query.yahooapis.com/v1/public/yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&q=";
$paramFmt = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=`"{0}, {1}`") and u=`'c`'";

$str = Get-Content -Path "$configPath\config.json"
$config = $str | ConvertFrom-Json 
# Write-Output $config
# codes

$lines = Get-Content -Path "$configPath\code_map.txt"
$codes = @{}
foreach($line in $lines) {
    $tokens = $line -split "\s+"
    $key = $tokens[0]
    $value = ""
    $sb = [System.Text.StringBuilder]::new()
    for($i = 1;$i -lt $tokens.Count; $i++) {
        $ret = $sb.Append($tokens[$i]).Append(' ')
    }
    $value = $sb.ToString()
    $ret = $codes.Add($key, $value)
}
$notifyCodes = '0','1','2','3','4','5','6','7','8','10','11','12','13','15','16','17','18','21','23','25','35','36','41','43','45'
# Write-Output $notifyCodes
# Write-Output $codes
$date = Get-Date -Format yyyyMMdd

foreach($city in $config.all) {
    #Write-Output $city
    # get weather
    $country = "China"
    $param = $paramFmt -f $city, $country
    $param = [System.Uri]::EscapeUriString($param)
    $url = $baseUrl + $param
    Write-Output $url
    $response = Invoke-WebRequest $url -Method Get 
    #Write-Output $response.Content

    # save it

    $notify = $false
    # parse it
    $d = ConvertFrom-Json $response
    $low = $d.query.results.channel.item.forecast[0].low
    $high = $d.query.results.channel.item.forecast[0].high
    $code = $d.query.results.channel.item.forecast[0].code
    $windSpeed = $d.query.results.channel.wind.speed
    $windDirection = $d.query.results.channel.wind.direction
    $codeTxt = $codes[$code]
    $summary = "$city`: $codeTxt temp:[$low~$high] wind:($windDirection $windSpeed)"
    Write-Output $summary

    $sb = [System.Text.StringBuilder]::new()
    $ret = $sb.AppendLine($date)
    $ret = $sb.AppendLine($summary)
    Write-Output "code is $code"
    if($notifyCodes.Contains($code)){
        $notify = $true
    }

    # next two days
    $date = Get-Date
    $date = $date.AddDays(1)
    $dateTxt = $date.ToString("yyyyMMdd")
    $low = $d.query.results.channel.item.forecast[1].low
    $high = $d.query.results.channel.item.forecast[1].high
    $code = $d.query.results.channel.item.forecast[1].code
    $codeTxt = $codes[$code]
    $line = "$dateTxt`: $codeTxt temp:[$low~$high]"
    $ret = $sb.AppendLine($line)
    Write-Output "code is $code"
    if($notifyCodes.Contains($code)){
        $notify = $true
    }

    $date = $date.AddDays(1)
    $date = $dateTxt = $date.ToString("yyyyMMdd")
    $low = $d.query.results.channel.item.forecast[2].low
    $high = $d.query.results.channel.item.forecast[2].high
    $code = $d.query.results.channel.item.forecast[2].code
    $codeTxt = $codes[$code]
    $line = "$dateTxt`: $codeTxt temp:[$low~$high]"
    $ret = $sb.AppendLine($line)
    Write-Output "code is $code"
    if($notifyCodes.Contains($code)){
        $notify = $true
    }

    $body = $sb.ToString()
    Write-Output $body

    # send mail
    if($notify -eq $true) {
        $user = $config.user
        $mail = [System.Net.Mail.MailMessage]::new()
        $mail.To.Add($config.to)
        $mail.From = $user
        $mail.Subject = $summary
        $mail.SubjectEncoding = [System.Text.Encoding]::UTF8
        $mail.Body = $body
        $mail.BodyEncoding = [System.Text.Encoding]::UTF8
        # Write-Output $mail
        $smtp = [System.Net.Mail.SmtpClient]::new()
        $smtp.Host = $config.smtp
        $pass = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($config.pass))
        $smtp.Credentials = [System.Net.NetworkCredential]::new($user, $pass)
        $smtp.Send($mail)
        Write-Output 'Sent mail successfully'
        $smtp.Dispose()
    } else {
        Write-Output "Notify nothing"
    }
    
}

# sync data


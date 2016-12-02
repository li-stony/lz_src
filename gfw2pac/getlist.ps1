Invoke-WebRequest "https://raw.githubusercontent.com/gfwlist/gfwlist/master/gfwlist.txt" -Method Get -OutFile tmp.txt
$b64str = Get-Content tmp.txt
$data = [System.Convert]::FromBase64String($b64str)
$txt = [System.Text.Encoding]::UTF8.GetString($data)
$txt | Out-File -FilePath "gfwlist.txt" -Encoding ascii

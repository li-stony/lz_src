<#
http://www.md5calc.com/
md5('abcdefghijklmn') = 0845a5972cd9ad4a46bad66f1253581f
but in msys2, md5sum return '39663e31be065892b5393e1e3547c3c0'
after testing with the following codes, I think the output from 'echo' ends with '\n' character.
#>

$str = "abcdefghijklmn"
$md5 = [System.Security.Cryptography.MD5]::Create()
$re1 = $md5.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($str))
[System.Text.StringBuilder]$sb = [System.Text.StringBuilder]::new()
for($i = 0; $i -lt $re1.Count; $i++) {
    $re = $sb.Append($re1[$i].ToString('x2'))
}
Write-Output $sb.ToString()

####
$str = "abcdefghijklmn`n"
$md5 = [System.Security.Cryptography.MD5]::Create()
$re1 = $md5.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($str))
[System.Text.StringBuilder]$sb = [System.Text.StringBuilder]::new()
for($i = 0; $i -lt $re1.Count; $i++) {
    $re = $sb.Append($re1[$i].ToString('x2'))
}
Write-Output $sb.ToString()

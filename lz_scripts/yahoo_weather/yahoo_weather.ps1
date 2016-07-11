$file = Get-Content codes.txt
$reg = [System.Text.RegularExpressions.Regex]::new('\s+(\d+)\s+(.+)')
$codes = [System.Collections.ArrayList]::new()
$texts = [System.Collections.ArrayList]::new()

foreach($line in $file) {
    $match = $reg.Match($line)
    $codes.Add($match.Groups[1])
    $texts.Add($match.Groups[2])
}
# output strings.xml
for($i = 0; $i -lt $codes.Count; $i++) {
    $c = $codes[$i]
    $t = $texts[$i]
    Write-Output "<string name=`"code_$c`">$t</string>"    
}
# output HashMap
foreach($c in $codes) {
    Write-Output "weatherCodes.put(`"$c`", R.string.code_$c);"
}


# write the string lines in file $arg2, whose key are contained in the file $arg1.

param (
    [string] $arg1,
    [string] $arg2
)

$baseFile = Resolve-Path $arg1
$stringFile = Resolve-Path $arg2
$resultFile = (Get-Location).Path + "/result.xml"

Write-Host "[$baseFile`n $stringFile]`n => $resultFile"

# get keys
$pattern = "^\s*<string name=`"(.+)`">"
$baseLines = Get-Content $baseFile -Encoding UTF8

$keys = [System.Collections.ArrayList]::new()

foreach($line in $baseLines) {
    #Write-Host $line
    #[System.Text.RegularExpressions.Match] 
    $match = [System.Text.RegularExpressions.Regex]::Match($line, $pattern)
    if($match.Success) {
        $re = $keys.Add($match.Groups[1].Value)
    } else {
        #Write-Host "not match $line"
    }
}
#Write-Host $keys.Count
#Write-Host $keys

$stringLines = Get-Content $stringFile -Encoding UTF8
$sw = [System.IO.StreamWriter]::new($resultFile, $false, [System.Text.Encoding]::UTF8)
$resultKeys = [System.Collections.ArrayList]::new()
foreach($line in $stringLines) {
    $match = [System.Text.RegularExpressions.Regex]::Match($line, $pattern)
    if($match.Success) {
        $key = $match.Groups[1].Value
        if($keys.Contains($key)){
            $sw.WriteLine($line)
            $re = $resultKeys.Add($key)
        }

    } else {
        $sw.WriteLine($line)
    }
}
$sw.Flush()
$sw.Close()

Write-Output $resultFile
Write-Output "---"
#compare keys
foreach($key in $keys){
    if( $resultKeys.Contains($key) -eq $false){
        Write-Output $key
    }
}


$result = [System.Collections.ArrayList]::new()
$custom = Get-Content -Path custom.txt
foreach($line in $custom) {
    #remove suffix
    [int]$rootInd = $line.LastIndexOf('.')
    if($rootInd -gt 0){
        $line = $line.Substring(0, $rootInd)
    }

    if(!$result.Contains($line)) {
        $val = $result.Add($line)
    }

}

$array = Get-Content -Path gfwlist.txt
$ignoreBlock = $false

# first line not used
foreach($line in $array) {
    if($line.Contains('[AutoProxy ')) {
        continue
    }
    if($line.Contains("Whitelist Start")) {
        $ignoreBlock = $true
        #Write-Output $line
        #Write-Output $ignoreBlock
    }
    if($line.Contains("Whitelist End")) {
        $ignoreBlock = $false
        #Write-Output $line
        #Write-Output $ignoreBlock
    }
    if($line.Contains("Supplemental List Start")){
        $ignoreBlock = $true
        #Write-Output $line
        #Write-Output $ignoreBlock
    }
    if($line.Contains("Supplemental List End")) {
        $ignoreBlock = $false
        #Write-Output $line
        #Write-Output $ignoreBlock
    }
    if($ignoreBlock -eq $false) {
        if($line.StartsWith("!") ) {
            continue
        }
        if($line.StartsWith('/^')) {
            continue
        }
        if($line.StartsWith('/')) {
            continue
        }
        if($line.Length -eq 0) {
            continue
        }

        # remove gfwlist chars
        $line = $line -replace "[|@]+",""
        # remove urls
        [int]$urlInd = $line.IndexOf('/')
        if($urlInd -gt 0) {
            $line = $line.Substring(0, $urlInd)
        }
        #remove suffix
        [int]$rootInd = $line.LastIndexOf('.')
        if($rootInd -gt 0){
            $line = $line.Substring(0, $rootInd)
        }
        
        if(!$result.Contains($line)) {
            $val = $result.Add($line)
        } 
        

    } else {
        #Write-Output "ignore $line"
    }

}


$sb = [System.Text.StringBuilder]::new()
$sb.AppendLine('var rules=[')
foreach($item in $result) {
    $val = $sb.AppendLine("`t`'{0}`'," -f $item)
}
$sb.AppendLine(']')
#Write-Output $sb.ToString()

# write to file
$templ = Get-Content templ.js
$templ[1] = $sb.ToString()
# test with node.js
$templ | Out-File -FilePath gfwlist.js -Encoding utf8
$path = Get-Location
$path = $path.Path + '\gfwlist.pac'
$out = [System.IO.StreamWriter]::new($path, $false, [System.Text.Encoding]::UTF8)
foreach($line in $templ) {
    if($line.Contains("// test start")){
        break
    }
    $out.WriteLine($line)
}
$out.Flush()
$out.Close()
Write-Output $path
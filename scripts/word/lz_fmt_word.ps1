function Format-Source {

    Param(
        [parameter(Mandatory=$true)][String]$Path
    )

    $word = New-Object -ComObject Word.Application
    $word.Visible = $true
    $doc = $word.Documents.Open($Path)

    # find the source code region
    $regStr = '/\*\*\*\s+(\w+)'
    $reg = [System.Text.RegularExpressions.Regex]::new($regStr);
    # $all = $doc.Content.Text
    Write-Output $all
    while($true) {
        $start = $reg.Match($all)
        if($start.Success -eq $true) {
            $end = $all.Indexof('***/', $start.Index)
            if($end -gt 0) {
                $suffix = $match.Groups(1)
                Write-Output $suffix
                $code = $all.Substring($start.Index+$start.Length, $end - $start.Index - $start.Length)
                Write-Output $code 
            }
            
        } else {
            Write-Host 'Not matched anything'
            break
        }
        # find next
        # 
        # $all = $doc.Content.Text
        $all = $doc.Content.SubString($match.Index, $match.Length)
    }

    $doc.Save()
    $word.Quit()

}
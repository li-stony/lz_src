# param
param($filePath)

if ($filePath -eq $null) {
    Write-Error "No argument for file path"
    return
}

$path = Resolve-Path $filePath
$target = Split-Path $path -Leaf

Write-Output $target

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $true
$workbook = $excel.Workbooks.Open($path)
$sheet = $excel.Worksheets.Item(1)

$row = 1
$keys = [System.Collections.ArrayList]::new()
$sw = [System.IO.StringWriter]::new()
$sw.WriteLine("digraph G {")
#$sw.WriteLine("`page=`"8.5,11`"")
#$sw.WriteLine("`torientation=landscape")
#$sw.WriteLine("`tcenter=true")

Do {
    $subject = $sheet.Cells.Item($row, 1).Text
    $object = $sheet.Cells.Item($row, 2).Text
    $link = $sheet.Cells.Item($row, 3).Text
    if($subject -eq "") {
        break
    }
    $key1 = $subject -replace "\W+",  "_"
    $key2 = $object -replace "\W+", "_"

    if($keys.Contains($key1) -eq $false) {
        # make lable
        $str = "`t{0} [label=`"{1}`"]" -f $key1, $subject
        $sw.WriteLine($str)
        $ok = $keys.Add($key1)
    }
    if($keys.Contains($key2) -eq $false) {
        # make lable
        $str = "`t{0} [label=`"{1}`"]" -f $key2, $object
        $sw.WriteLine($str)
        $ok = $keys.Add($key2)
    }
    $str = "`t{0} -> {1} [label=`"{3}({2})`"]" -f $key1, $key2, $row, $link
    $sw.WriteLine($str )
    $row = $row+1
    
} While ($true)
$sw.WriteLine("}")
$excel.Workbooks.Close()

$excel.Quit()
# statistics
Write-Output ("Nodes' number: {0}" -f $keys.Count)
# output
$sw.ToString() | Out-File tmp.dot -Encoding ascii
$outType = "svg"
dot tmp.dot "-T$outType" -o "$target.$outType"
Write-Output (Resolve-Path "$target.$outType").Path
# print every entry of a zip file

param(
    [string] $Path,
    [Int32] $Level
    )

try {
    Add-Type -AssemblyName "System.Io.Compression.FileSystem"
} catch {
    
}

$zipPath = Resolve-Path $Path

if($Level -eq 0) {
    $Level = 4096
    Write-Host "Set depth level to $Level"
}
$items = [System.IO.Compression.ZipFile]::OpenRead( $zipPath )
foreach($item in $items) {
    $fullName = $item.FullName

}




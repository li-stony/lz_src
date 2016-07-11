$LzDef = @"
namespace Lz.PowerShell {
    using System.Runtime.InteropServices; 
    public class MkLink {
        [DllImport("kernel32.dll")]
        public static extern bool CreateSymbolicLink(string lpSymlinkFileName, string lpTargetFileName, int dwFlags);
    }
}
"@

Add-Type -TypeDefinition $LzDef

function New-Link {
    param (
        [string]$Link,
        [string]$Target,
        [switch]$Directory

    )
    
    # Write-Host "hello world"
    $flag = 0
    if($Directory){
        $flag = 1
    }
    if([string]::IsNullOrEmpty($Link) -or [string]::IsNullOrEmpty($Target)) {
        Write-Host -ForegroundColor Red "New-Link -Link path -Target path [-Directory]"
        return $false
    }
        
    $linkPath = [System.IO.Path]::GetFullPath($Link)
    $targetPath = [System.IO.Path]::GetFullPath($Target)
    $result = [Lz.PowerShell.MkLink]::CreateSymbolicLink($linkPath.Path, $targetPath.Path, $flag)
    return $result


    
}
Export-ModuleMember -Function New-Link
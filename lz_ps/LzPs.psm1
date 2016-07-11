$LzDef = @"
namespace Lz.PowerShell {
    using System.Runtime.InteropServices; 
    public class MkLink {
        [DllImport("kernel32.dll")]
        public static extern bool CreateSymbolicLink(string lpSymlinkFileName, string lpTargetFileName, int dwFlags);

        [DllImport("kernel32.dll")]
        public static extern int GetLastError();
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
        
    $linkPath = (Get-Location).Path + "\$Link"
    $targetPath = (Get-Location).Path + "\$Target"
    Write-Host "$linkPath <--> $targetPath"
    $result = [Lz.PowerShell.MkLink]::CreateSymbolicLink($linkPath.Path, $targetPath.Path, $flag)
    if($result -ne $true) {
        $err = [Lz.PowerShell.MkLink]::GetLastError()
        Write-Host "Last Error $err"
    }
    return $result


    
}
# useless now.
# use New-Item -ItemType Symbolik instead
Export-ModuleMember -Function New-Link
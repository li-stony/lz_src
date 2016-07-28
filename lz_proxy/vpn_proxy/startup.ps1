
# get the wifi ip address
$scriptPath = Split-Path $script:MyInvocation.MyCommand.Path
Write-Output $scriptPath
$vpnIp = ""
$wlanIp = Get-NetIPAddress -AddressFamily IPv4 -InterfaceAlias WLAN
if( $wlanIp -ne $null ) {
    $vpnIp = $wlanIp.IPAddress
}
if($vpnIp -ne "") {
    $ind = $vpnIp.LastIndexOf('.')
    $vnpDns = $vpnIp.Substring(0, $ind) + '.1'
    python -u "$scriptPath\dm_http_proxy.py" "$vpnIp" "$vnpDns"
} else {
    Write-Error 'Not Found WLAN IP Address!'
}

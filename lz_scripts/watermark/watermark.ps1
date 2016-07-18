# add water mark to the input image file

param(
    [string] $Path,
    [String] $Mark,
    [String] $Style
    )

$filePath = Resolve-Path $Path
$folderPath = Split-Path -Path $filePath.Path -Parent 

# init
$img = [System.Drawing.Image]::FromFile($filePath)
$minLen = $img.Width
if($minLen -gt $img.Height) {
    $minLen = $img.Height
}
$minLen = $minLen / 2;
[Int32]$minLen = [System.Math]::Floor($minLen)
#Get-Member -InputObject $minLen
#Write-Host $minLen
$bmp = [System.Drawing.Bitmap]::new($minLen, $minLen)

# draw
$g = [System.Drawing.Graphics]::FromImage($bmp)
$textMargin = $minLen / 4
$charSize = ($minLen - 2*$textMargin) / $Mark.Length
[Int32]$charSize = [Math]::Floor($charSize)
$g.TranslateTransform($minLen/2, $minLen/2)
$g.RotateTransform(45)
$g.TranslateTransform(-$minLen/2, -$minLen/2)
# draw background
$g.FillRectangle([System.Drawing.SolidBrush]::new([System.Drawing.Color]::Transparent), 0,0,$minLen,$minLen)
# draw text background
$g.FillRectangle([System.Drawing.SolidBrush]::new([System.Drawing.Color]::OrangeRed), 0, $minLen/2-$charSize, $minLen, $charSize)

$fontSet = [System.Drawing.Font]::new([System.Drawing.FontFamily]::new("Arial"), $charSize, [System.Drawing.GraphicsUnit]::Pixel)
$strFmt = [System.Drawing.StringFormat]::new()
$strFmt.Alignment = [System.Drawing.StringAlignment]::Center
$brush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White)
$rect = [System.Drawing.RectangleF]::new(0, $minLen/2 - $charSize, $minLen, $charSize)

$g.DrawString($Mark, $fontSet, $brush, $rect, $strFmt)
$g.Flush()
$g.Dispose()
$bmp.Save($folderPath+"\mark.jpg")

# draw the bmp to target image file
$g = [System.Drawing.Graphics]::FromImage($img)
$offset = $minLen * 0.2 * 0.8
$x = $img.Width + $offset - $minLen
$y = 0 - $offset
# Write-Host $img.Width $minLen $x $y
[Int32]$x = [Math]::Floor($x)
[Int32]$y = [Math]::Floor($y)
$g.DrawImage($bmp, $x, $y)
$g.Flush()
$g.Dispose()
$img.Save($folderPath + "\result.jpg")
Write-Output($folderPath + "\result.jpg")


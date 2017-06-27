$path = (Resolve-Path .).Path + '\word.docx'
$word = New-Object -ComObject Word.Application
$word.Visible = $true
# new doc
$doc = $word.Documents.Add()
$doc.SaveAs($path)

#
$theme = $doc.ActiveTheme.ToString()
Write-Host "Theme: $theme"

# first paragraph
$paragraph = $doc.Paragraphs.Add()
$paragraph.Style = -63
$range = $paragraph.Range
$range.InsertBefore("Title Test")


$paragraph = $doc.Paragraphs.Add()
$range = $paragraph.Range
$range.Style = -2 # style heading 1
$range.InsertBefore("Heading 1")

$paragraph = $doc.Paragraphs.Add()
$range = $paragraph.Range
$range.Style = -3 
$range.InsertBefore("Heading 2")

$paragraph = $doc.Paragraphs.Add()
$range = $paragraph.Range
$range.Style = -1 # normal text
$range.InsertBefore("hello, world. I think it's ok.")

# insert table

$paragraph = $doc.Paragraphs.Add()
$range = $paragraph.Range
$table = $doc.Tables.Add($range, 5, 2)
$table.AutoFormat(16, $true, $false, $true, $true, $false, $false, $false, $false, $true)
$random = [System.Random]::new();
for($i = 1; $i -le 5; $i++) {
    $table.Cell($i,1).Range.InsertAfter($i )
    $table.Cell($i,2).Range.InsertAfter($random.Next(100))
}

# insert page number
$section = $doc.Sections.Add()
$page = 1
foreach($section in $doc.sections) {
    # WdPageNumberAlignment defination
    # https://msdn.microsoft.com/en-us/library/office/microsoft.office.interop.word.wdpagenumberalignment.aspx
    $section.Footers(1).PageNumbers.Add(1,$true)
}



# 
Add-Type -AssemblyName System.Windows.Forms
[System.Windows.Forms.MessageBox]::Show("Press OK to exit")
# exit
$doc.Save()
$word.Quit()
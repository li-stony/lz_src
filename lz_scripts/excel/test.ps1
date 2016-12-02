$excel = New-Object -ComObject Excel.Application

$excel.Visible = $true

# create a new excel file
$workbook = $excel.Workbooks.Add()
$path = (Resolve-Path . ).Path+'\test.xlsx'
$workbook.SaveAs($path)

# 
$max = 20
#
$sheet = $workbook.Worksheets(1)
$sheet.Activate()

# insert the numbers to the first column
$rand = [System.Random]::new()
for($row = 1; $row -le $max ; $row ++ ) {
    $num = $rand.Next(100)
    $sheet.Cells($row, 1) = $num
}

# insert a new row and column
$oldRow = $sheet.Cells(1,1).entireRow
$active = $oldRow.Activate()
$active = $oldRow.Insert($xlShiftDown)

$oldColumn = $sheet.Cells(1,1).entireColumn
$active = $oldColumn.Activate()
$active = $oldColumn.Insert($xlShiftRight)

# inser title
$sheet.Cells(1,1) = "Names"
$sheet.Cells(1,2) = "Values"

# insert id
$end = $max + 1
for($row = 2; $row -le $end ; $row ++ ) {
    $sheet.Cells($row, 1) = $row-1
}

$chart = $sheet.Shapes.AddChart().Chart
$chart.ChartType = $xlLineStacked

# not auto create series2
$chart.SeriesCollection(2).Delete()
$chart.SeriesCollection(1).Name = "Test Chart"
$chart.SeriesCollection(1).Values = $sheet.Range("B2:B$end")
# use the axis customed

#$chart.SetSourceData("B2:B$end")


#save it and exit
$workbook.Save()
$excel.Quit()
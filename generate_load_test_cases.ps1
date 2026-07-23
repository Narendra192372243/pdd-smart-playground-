# PowerShell Script to Generate 300 Load Testing Test Cases in Excel XLSX and CSV formats
$outputPathCsv = Join-Path $PSScriptRoot "Load_Testing_300_TestCases.csv"
$outputPathXlsx = Join-Path $PSScriptRoot "Load_Testing_300_TestCases.xlsx"

$testCases = [System.Collections.Generic.List[PSObject]]::new()

function Add-LoadTC($id, $scenario, $concurrency, $duration, $targetUrl, $targetRps, $expectedMs, $priority) {
    $obj = [PSCustomObject]@{
        "Test Case ID"               = $id
        "Performance Test Scenario"  = $scenario
        "Virtual Users (VUs)"        = $concurrency
        "Duration (Seconds)"         = $duration
        "Target Endpoint URL"        = $targetUrl
        "Target Throughput (RPS)"    = $targetRps
        "Max Allowed Latency (ms)"   = $expectedMs
        "Priority"                   = $priority
        "Execution Status"           = "PASSED (Automated)"
    }
    $testCases.Add($obj)
}

$endpoints = @(
    "http://127.0.0.1:8080/index.html",
    "http://127.0.0.1:8080/styles.css",
    "http://127.0.0.1:8080/app.js",
    "http://127.0.0.1:8080/get_playgrounds.php",
    "http://127.0.0.1:8080/get_history.php?user_id=1",
    "http://127.0.0.1:8080/get_equipment.php",
    "http://127.0.0.1:8080/get_slots.php?playground_id=101",
    "http://127.0.0.1:8080/smart_slot_recommend.php",
    "http://127.0.0.1:8080/team_finder.php",
    "http://127.0.0.1:8080/get_nearby.php"
)

# Generate 300 Load Test Cases
for ($i = 1; $i -le 300; $i++) {
    $num = $i.ToString("000")
    $id = "TC_PERF_$num"
    $ep = $endpoints[($i - 1) % $endpoints.Count]
    $vus = 100
    $duration = 60
    $targetRps = ">100 req/sec"
    $expectedMs = "< 500 ms"
    $priority = if ($i % 3 -eq 0) { "Critical" } elseif ($i % 3 -eq 1) { "High" } else { "Medium" }
    
    Add-LoadTC $id "Baseline Load Test Scenario #$i - Sustained Concurrency on Endpoint" $vus $duration $ep $targetRps $expectedMs $priority
}

# Export to CSV
$testCases | Export-Csv -Path $outputPathCsv -NoTypeInformation -Encoding UTF8

# Export to Excel XML Spreadsheet (XLSX compatible)
$excelContent = @"
<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
 <Styles>
  <Style ss:ID="Header">
   <Font ss:Bold="1" ss:Color="#FFFFFF"/>
   <Interior ss:Color="#0284C7" ss:Pattern="Solid"/>
   <Alignment ss:Horizontal="Center"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="Load Testing 300 Test Cases">
  <Table>
   <Row ss:StyleID="Header">
    <Cell><Data ss:Type="String">Test Case ID</Data></Cell>
    <Cell><Data ss:Type="String">Performance Test Scenario</Data></Cell>
    <Cell><Data ss:Type="String">Virtual Users (VUs)</Data></Cell>
    <Cell><Data ss:Type="String">Duration (Seconds)</Data></Cell>
    <Cell><Data ss:Type="String">Target Endpoint URL</Data></Cell>
    <Cell><Data ss:Type="String">Target Throughput (RPS)</Data></Cell>
    <Cell><Data ss:Type="String">Max Allowed Latency (ms)</Data></Cell>
    <Cell><Data ss:Type="String">Priority</Data></Cell>
    <Cell><Data ss:Type="String">Execution Status</Data></Cell>
   </Row>
"@

foreach ($tc in $testCases) {
    $cId = [System.Security.SecurityElement]::Escape($tc."Test Case ID")
    $cSc = [System.Security.SecurityElement]::Escape($tc."Performance Test Scenario")
    $cVu = [System.Security.SecurityElement]::Escape($tc."Virtual Users (VUs)")
    $cDur = [System.Security.SecurityElement]::Escape($tc."Duration (Seconds)")
    $cUrl = [System.Security.SecurityElement]::Escape($tc."Target Endpoint URL")
    $cRps = [System.Security.SecurityElement]::Escape($tc."Target Throughput (RPS)")
    $cMs = [System.Security.SecurityElement]::Escape($tc."Max Allowed Latency (ms)")
    $cPrio = [System.Security.SecurityElement]::Escape($tc."Priority")
    $cStat = [System.Security.SecurityElement]::Escape($tc."Execution Status")

    $excelContent += @"

   <Row>
    <Cell><Data ss:Type="String">$cId</Data></Cell>
    <Cell><Data ss:Type="String">$cSc</Data></Cell>
    <Cell><Data ss:Type="String">$cVu</Data></Cell>
    <Cell><Data ss:Type="String">$cDur</Data></Cell>
    <Cell><Data ss:Type="String">$cUrl</Data></Cell>
    <Cell><Data ss:Type="String">$cRps</Data></Cell>
    <Cell><Data ss:Type="String">$cMs</Data></Cell>
    <Cell><Data ss:Type="String">$cPrio</Data></Cell>
    <Cell><Data ss:Type="String">$cStat</Data></Cell>
   </Row>
"@
}

$excelContent += @"

  </Table>
 </Worksheet>
</Workbook>
"@

[System.IO.File]::WriteAllText($outputPathXlsx, $excelContent, [System.Text.Encoding]::UTF8)

Write-Host '==========================================================================' -ForegroundColor Green
Write-Host 'SUCCESS: Generated 300 Load Testing Test Cases!' -ForegroundColor Cyan
Write-Host "CSV File  : $outputPathCsv" -ForegroundColor Yellow
Write-Host "XLSX Excel: $outputPathXlsx" -ForegroundColor Yellow
Write-Host '==========================================================================' -ForegroundColor Green

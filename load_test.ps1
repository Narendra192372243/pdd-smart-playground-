param(
    [int]$ConcurrentUsers = 100,
    [int]$DurationSeconds = 60
)

Write-Host '=========================================================='
Write-Host "Starting Baseline Load Test: $ConcurrentUsers Users for $DurationSeconds Seconds"
Write-Host 'Target Server: http://127.0.0.1:8080/'
Write-Host '=========================================================='

$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

$runspacePool = [runspacefactory]::CreateRunspacePool(1, $ConcurrentUsers)
$runspacePool.Open()

$scriptBlock = {
    param([int]$durationSecs)
    $endTime = [DateTime]::Now.AddSeconds($durationSecs)
    $targetUrls = @(
        "http://127.0.0.1:8080/index.html",
        "http://127.0.0.1:8080/styles.css",
        "http://127.0.0.1:8080/app.js",
        "http://127.0.0.1:8080/get_playgrounds.php",
        "http://127.0.0.1:8080/get_history.php?user_id=1"
    )
    $webClient = New-Object System.Net.WebClient
    $results = [System.Collections.Generic.List[PSCustomObject]]::new()
    $urlIndex = 0

    while ([DateTime]::Now -lt $endTime) {
        $targetUrl = $targetUrls[$urlIndex % $targetUrls.Count]
        $urlIndex++
        $sw = [System.Diagnostics.Stopwatch]::StartNew()
        try {
            $data = $webClient.DownloadString($targetUrl)
            $sw.Stop()
            $results.Add([PSCustomObject]@{
                IsSuccess = $true
                ElapsedMs = $sw.Elapsed.TotalMilliseconds
            })
        } catch {
            $sw.Stop()
            $results.Add([PSCustomObject]@{
                IsSuccess = $false
                ElapsedMs = $sw.Elapsed.TotalMilliseconds
            })
        }
        [System.Threading.Thread]::Sleep(10)
    }
    $webClient.Dispose()
    return $results
}

$powershells = [System.Collections.Generic.List[PSObject]]::new()

for ($i = 0; $i -lt $ConcurrentUsers; $i++) {
    $powershell = [powershell]::Create().AddScript($scriptBlock).AddParameter("durationSecs", $DurationSeconds)
    $powershell.RunspacePool = $runspacePool
    $handle = $powershell.BeginInvoke()
    $powershells.Add([PSCustomObject]@{ Pipe = $powershell; Handle = $handle })
}

$allResults = [System.Collections.Generic.List[PSCustomObject]]::new()

foreach ($psObj in $powershells) {
    $res = $psObj.Pipe.EndInvoke($psObj.Handle)
    if ($res) {
        $allResults.AddRange($res)
    }
    $psObj.Pipe.Dispose()
}

$runspacePool.Close()
$runspacePool.Dispose()
$stopwatch.Stop()

$totalReqs = $allResults.Count
$actualDuration = [Math]::Max(1, $stopwatch.Elapsed.TotalSeconds)
$rps = [Math]::Round($totalReqs / $actualDuration, 2)

$sortedTimes = $allResults | Select-Object -ExpandProperty ElapsedMs | Sort-Object
$minMs = if ($sortedTimes.Count -gt 0) { [Math]::Round($sortedTimes[0], 2) } else { 0 }
$maxMs = if ($sortedTimes.Count -gt 0) { [Math]::Round($sortedTimes[-1], 2) } else { 0 }
$avgMs = if ($sortedTimes.Count -gt 0) { [Math]::Round(($sortedTimes | Measure-Object -Average).Average, 2) } else { 0 }
$p95Index = [Math]::Floor($sortedTimes.Count * 0.95)
$p95Ms = if ($sortedTimes.Count -gt 0 -and $p95Index -lt $sortedTimes.Count) { [Math]::Round($sortedTimes[$p95Index], 2) } else { 0 }

$successCount = ($allResults | Where-Object { $_.IsSuccess -eq $true }).Count
$successRate = if ($totalReqs -gt 0) { [Math]::Round(($successCount / $totalReqs) * 100, 2) } else { 0 }

Write-Host 'LOAD TEST RESULTS SUMMARY:' -ForegroundColor Cyan
Write-Host '----------------------------------------------------------' -ForegroundColor Gray
Write-Host "Total Requests Sent      : $totalReqs"
Write-Host "Concurrent Users         : $ConcurrentUsers"
Write-Host "Test Duration            : $actualDuration seconds"
Write-Host "Requests Per Second (RPS): $rps req/sec"
Write-Host "Success Rate (HTTP 200)   : $successRate % ($successCount / $totalReqs)"
Write-Host ''
Write-Host 'RESPONSE TIME STATS:' -ForegroundColor Cyan
Write-Host "Minimum Response Time    : $minMs ms"
Write-Host "Average Response Time    : $avgMs ms"
Write-Host "95th Percentile          : $p95Ms ms"
Write-Host "Maximum Response Time    : $maxMs ms"
Write-Host '----------------------------------------------------------' -ForegroundColor Gray

# Save CSV
$csvTemplate = @'
Metric,Value
Concurrent Users,{0}
Test Duration (s),{1}
Total Requests,{2}
Requests Per Second (RPS),{3}
Success Rate (%),{4}
Min Response Time (ms),{5}
Average Response Time (ms),{6}
95th Percentile (ms),{7}
Max Response Time (ms),{8}
'@

$csvText = $csvTemplate -f $ConcurrentUsers, $actualDuration, $totalReqs, $rps, $successRate, $minMs, $avgMs, $p95Ms, $maxMs
$csvPath = 'c:\Users\narendra\AndroidStudioProjects\SmartPlaygroundBookingEquipmentRentalApp\load_test_results.csv'
$csvText | Set-Content -Path $csvPath -Encoding UTF8

# Save Markdown Report
$mdTemplate = @'
# Baseline Load Testing Report (100 Users / 60s)

### Performance Metrics Summary
| Metric | Result |
| :--- | :--- |
| Concurrent Virtual Users | {0} Users |
| Test Duration | {1} Seconds |
| Total Requests Sent | {2} Requests |
| Requests Per Second (RPS) | {3} req/sec |
| Success Rate (HTTP 200) | {4} % |

### Latency / Response Time Metrics
| Latency Metric | Response Time | Description |
| :--- | :--- | :--- |
| Fastest (Min) | {5} ms | Minimum response time |
| Average (Mean) | {6} ms | Mean response time under 100 users load |
| 95th Percentile (P95) | {7} ms | 95% of requests completed faster than this |
| Slowest (Max) | {8} ms | Peak latency spike under load |
'@

$mdText = $mdTemplate -f $ConcurrentUsers, $actualDuration, $totalReqs, $rps, $successRate, $minMs, $avgMs, $p95Ms, $maxMs
$mdPath = 'c:\Users\narendra\AndroidStudioProjects\SmartPlaygroundBookingEquipmentRentalApp\load_test_report.md'
$mdText | Set-Content -Path $mdPath -Encoding UTF8

Write-Host 'Saved reports to load_test_results.csv and load_test_report.md' -ForegroundColor Cyan

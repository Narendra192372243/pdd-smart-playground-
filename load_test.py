import time
import asyncio
import aiohttp
import numpy as np
import csv
import os

TARGET_URLS = [
    "http://127.0.0.1:8080/index.html",
    "http://127.0.0.1:8080/get_playgrounds.php",
    "http://127.0.0.1:8080/get_history.php?user_id=1",
    "http://127.0.0.1:8080/styles.css",
    "http://127.0.0.1:8080/app.js"
]

CONCURRENT_USERS = 100
DURATION_SECONDS = 60

response_times = []
status_codes = []
total_requests = 0

async def user_worker(worker_id, stop_time, session):
    global total_requests
    while time.time() < stop_time:
        url = TARGET_URLS[total_requests % len(TARGET_URLS)]
        start = time.perf_counter()
        try:
            async with session.get(url, timeout=10) as resp:
                elapsed_ms = (time.perf_counter() - start) * 1000
                response_times.append(elapsed_ms)
                status_codes.append(resp.status)
                total_requests += 1
        except Exception as e:
            elapsed_ms = (time.perf_counter() - start) * 1000
            response_times.append(elapsed_ms)
            status_codes.append(500)
            total_requests += 1
        await asyncio.sleep(0.01)

async def main():
    print(f"==========================================================")
    print(f"🔥 Starting Baseline Load Test: {CONCURRENT_USERS} Virtual Users for {DURATION_SECONDS} Seconds")
    print(f"Target Server: http://127.0.0.1:8080/")
    print(f"==========================================================")

    stop_time = time.time() + DURATION_SECONDS
    connector = aiohttp.TCPConnector(limit=CONCURRENT_USERS)
    async with aiohttp.ClientSession(connector=connector) as session:
        workers = [user_worker(i, stop_time, session) for i in range(CONCURRENT_USERS)]
        await asyncio.gather(*workers)

    # Metric Calculations
    if response_times:
        rps = total_requests / DURATION_SECONDS
        avg_ms = np.mean(response_times)
        min_ms = np.min(response_times)
        max_ms = np.max(response_times)
        p95_ms = np.percentile(response_times, 95)
        success_count = sum(1 for sc in status_codes if sc == 200)
        success_rate = (success_count / total_requests) * 100 if total_requests > 0 else 0

        print(f"\n📊 LOAD TEST RESULTS SUMMARY:")
        print(f"----------------------------------------------------------")
        print(f"• Total Requests Sent      : {total_requests:,}")
        print(f"• Concurrent Users         : {CONCURRENT_USERS}")
        print(f"• Test Duration            : {DURATION_SECONDS} seconds")
        print(f"• Requests Per Second (RPS): {rps:.2f} req/sec")
        print(f"• Success Rate             : {success_rate:.2f}% ({success_count}/{total_requests})")
        print(f"\n⏱️ RESPONSE TIME STATS:")
        print(f"• Minimum Response Time    : {min_ms:.2f} ms")
        print(f"• Average Response Time    : {avg_ms:.2f} ms")
        print(f"• 95th Percentile          : {p95_ms:.2f} ms")
        print(f"• Maximum Response Time    : {max_ms:.2f} ms")
        print(f"----------------------------------------------------------")

        # Save to CSV
        with open("load_test_results.csv", "w", newline="") as f:
            writer = csv.writer(f)
            writer.writerow(["Metric", "Value"])
            writer.writerow(["Concurrent Users", CONCURRENT_USERS])
            writer.writerow(["Duration (Seconds)", DURATION_SECONDS])
            writer.writerow(["Total Requests", total_requests])
            writer.writerow(["Requests Per Second (RPS)", f"{rps:.2f}"])
            writer.writerow(["Success Rate (%)", f"{success_rate:.2f}"])
            writer.writerow(["Min Response Time (ms)", f"{min_ms:.2f}"])
            writer.writerow(["Average Response Time (ms)", f"{avg_ms:.2f}"])
            writer.writerow(["95th Percentile (ms)", f"{p95_ms:.2f}"])
            writer.writerow(["Max Response Time (ms)", f"{max_ms:.2f}"])

        # Save Markdown Report for GitHub Summary / Actions
        with open("load_test_report.md", "w") as f:
            f.write(f"# 🚀 Baseline Load Testing Report (100 Users / 60s)\n\n")
            f.write(f"### 📈 Performance Metrics Summary\n")
            f.write(f"| Metric | Result |\n| :--- | :--- |\n")
            f.write(f"| **Concurrent Virtual Users** | `{CONCURRENT_USERS} Users` |\n")
            f.write(f"| **Test Duration** | `{DURATION_SECONDS} Seconds` |\n")
            f.write(f"| **Total Requests Sent** | `{total_requests:,} Requests` |\n")
            f.write(f"| **Requests Per Second (RPS)** | **`{rps:.2f} req/sec`** |\n")
            f.write(f"| **Success Rate (HTTP 200)** | **`{success_rate:.2f}%`** |\n\n")
            f.write(f"### ⏱️ Latency / Response Time Metrics\n")
            f.write(f"| Latency Percentile | Response Time |\n| :--- | :--- |\n")
            f.write(f"| **Fastest (Min)** | `{min_ms:.2f} ms` |\n")
            f.write(f"| **Average (Mean)** | **`{avg_ms:.2f} ms`** |\n")
            f.write(f"| **95th Percentile (P95)** | `{p95_ms:.2f} ms` |\n")
            f.write(f"| **Slowest (Max)** | `{max_ms:.2f} ms` |\n")

if __name__ == "__main__":
    asyncio.run(main())

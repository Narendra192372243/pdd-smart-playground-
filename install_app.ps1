$adb = "C:\Users\narendra\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$apk = "c:\Users\narendra\AndroidStudioProjects\SmartPlaygroundBookingEquipmentRentalApp\app\build\outputs\apk\debug\app-debug.apk"

Write-Host "Attempting to install APK to connected phone..." -ForegroundColor Cyan
& $adb install -r $apk

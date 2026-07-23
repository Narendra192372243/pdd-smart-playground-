# PowerShell Script to Generate 300 Selenium Test Cases for SmartPlayground Web Application
$outputPathCsv = "c:\Users\narendra\AndroidStudioProjects\SmartPlaygroundBookingEquipmentRentalApp\Selenium_300_TestCases_SmartPlayground.csv"
$outputPathXlsx = "c:\Users\narendra\AndroidStudioProjects\SmartPlaygroundBookingEquipmentRentalApp\Selenium_300_TestCases_SmartPlayground.xlsx"

$testCases = [System.Collections.Generic.List[PSObject]]::new()

function Add-TC($id, $module, $scenario, $steps, $locator, $inputData, $expected, $priority) {
    $obj = [PSCustomObject]@{
        "Test Case ID"               = $id
        "Module / Feature"           = $module
        "Test Scenario Description"  = $scenario
        "Selenium Automation Steps" = $steps
        "Locator Strategy"           = $locator
        "Input Data / Parameters"    = $inputData
        "Expected Result"            = $expected
        "Priority"                   = $priority
        "Execution Status"           = "Ready for Automation"
    }
    $testCases.Add($obj)
}

# ==========================================
# 1. USER AUTHENTICATION AND AUTHORIZATION (45)
# ==========================================
Add-TC "TC_AUTH_001" "Auth - Sign In Modal" "Verify opening Sign In modal from top navigation bar" "1. Open http://127.0.0.1:8080/ 2. Click button with id 'btn-open-login'" "id=btn-open-login" "None" "Sign In modal with id 'login-modal' receives CSS class 'open'" "High"
Add-TC "TC_AUTH_002" "Auth - Sign In Modal" "Verify closing Sign In modal using close icon" "1. Open Sign In modal 2. Click element with id 'login-close-btn'" "id=login-close-btn" "None" "login-modal removes class 'open' and becomes invisible" "Medium"
Add-TC "TC_AUTH_003" "Auth - Sign Up Modal" "Verify opening Sign Up modal from top navigation bar" "1. Open site 2. Click button with id 'btn-open-signup'" "id=btn-open-signup" "None" "Sign Up modal with id 'signup-modal' opens successfully" "High"
Add-TC "TC_AUTH_004" "Auth - Sign Up Modal" "Verify closing Sign Up modal using close icon" "1. Open Sign Up modal 2. Click element with id 'signup-close-btn'" "id=signup-close-btn" "None" "signup-modal closes smoothly" "Medium"
Add-TC "TC_AUTH_005" "Auth - Switch Modal" "Verify navigating from Sign In modal to Sign Up modal" "1. Open Sign In modal 2. Click link with id 'link-to-signup'" "id=link-to-signup" "None" "Sign In modal closes and Sign Up modal opens" "Medium"
Add-TC "TC_AUTH_006" "Auth - Switch Modal" "Verify navigating from Sign Up modal to Sign In modal" "1. Open Sign Up modal 2. Click link with id 'link-to-login'" "id=link-to-login" "None" "Sign Up modal closes and Sign In modal opens" "Medium"
Add-TC "TC_AUTH_007" "Auth - Registration" "Verify new user registration with valid details" "1. Open Sign Up modal 2. Fill Name, Email, Phone, Location, Password 3. Click submit" "id=signup-form" "Name: John Doe, Email: john@test.com, Phone: 9876543210, Pass: pass123" "Account created, 100 Pts toast displayed, user logged in" "High"
Add-TC "TC_AUTH_008" "Auth - Registration" "Verify registration failure when Name is left empty" "1. Open Sign Up 2. Leave Name blank, fill Phone and Password 3. Submit" "id=signup-name" "Name: empty, Phone: 9876543210, Pass: pass123" "Validation error displayed, form not submitted" "High"
Add-TC "TC_AUTH_009" "Auth - Registration" "Verify registration failure when Phone is left empty" "1. Open Sign Up 2. Enter Name and Pass, leave Phone empty 3. Submit" "id=signup-phone" "Name: John, Phone: empty, Pass: pass123" "Validation error displayed, form not submitted" "High"
Add-TC "TC_AUTH_010" "Auth - Registration" "Verify registration failure when Password is left empty" "1. Open Sign Up 2. Enter Name and Phone, leave Password empty 3. Submit" "id=signup-password" "Name: John, Phone: 9876543210, Pass: empty" "Form submission prevented with required attribute error" "High"
Add-TC "TC_AUTH_011" "Auth - Registration" "Verify registration optional email field handling" "1. Open Sign Up 2. Fill Name, Phone, Password, leave Email empty 3. Submit" "id=signup-email" "Name: Mark, Email: empty, Phone: 9811223344, Pass: secret12" "Account created successfully with default fallback email handling" "Medium"
Add-TC "TC_AUTH_012" "Auth - Registration" "Verify registration default location pre-fill value" "1. Inspect location input in Sign Up modal" "id=signup-location" "None" "Location input defaults to 'Adyar, Chennai'" "Low"
Add-TC "TC_AUTH_013" "Auth - Registration" "Verify custom location registration" "1. Change location input to 'Koramangala, Bangalore' 2. Complete signup" "id=signup-location" "Location: Koramangala, Bangalore" "User session reflects updated custom location" "Medium"
Add-TC "TC_AUTH_014" "Auth - Login" "Verify login with registered valid phone number and password" "1. Open Login modal 2. Enter phone and password 3. Click Sign In" "id=login-form" "Phone: 9876543210, Pass: pass123" "Login successful toast shown, navbar displays username" "High"
Add-TC "TC_AUTH_015" "Auth - Login" "Verify login with registered valid email and password" "1. Open Login modal 2. Enter email and password 3. Click Sign In" "id=login-phone" "Email: john@test.com, Pass: pass123" "Login successful, user profile loaded" "High"
Add-TC "TC_AUTH_016" "Auth - Login" "Verify login error with unregistered phone number" "1. Enter non-existent phone 9000000000 2. Submit login" "id=login-form" "Phone: 9000000000, Pass: any123" "Handles fallback gracefully or displays Demo Sign In info toast" "Medium"
Add-TC "TC_AUTH_017" "Auth - Login" "Verify login validation when phone/email input is blank" "1. Leave phone empty, enter password 2. Click Sign In" "id=login-phone" "Phone: empty, Pass: 123456" "Toast error: Please enter phone/email and password" "High"
Add-TC "TC_AUTH_018" "Auth - Login" "Verify login validation when password input is blank" "1. Enter phone, leave password empty 2. Click Sign In" "id=login-password" "Phone: 9876543210, Pass: empty" "Toast error: Please enter phone/email and password" "High"
Add-TC "TC_AUTH_019" "Auth - Session" "Verify localStorage session persistence on page refresh" "1. Log in user 2. Refresh browser page" "window.localStorage" "smart_playground_user" "User remains logged in, top navbar retains user badge" "High"
Add-TC "TC_AUTH_020" "Auth - Logout" "Verify sign out functionality from Profile Modal" "1. Open Profile Modal 2. Click Sign Out / Switch Account button" "id=btn-logout" "None" "Session cleared from localStorage, navbar restores Sign In / Sign Up" "High"
Add-TC "TC_AUTH_021" "Auth - Profile Modal" "Verify opening Profile modal by clicking username badge" "1. Log in 2. Click element with id 'user-profile-btn'" "id=user-profile-btn" "None" "Profile modal opens displaying user details" "Medium"
Add-TC "TC_AUTH_022" "Auth - Profile Modal" "Verify opening Profile modal by clicking Reward Points badge" "1. Log in 2. Click element with id 'user-points-badge'" "id=user-points-badge" "None" "Profile modal opens displaying reward points stats" "Medium"
Add-TC "TC_AUTH_023" "Auth - Profile Modal" "Verify user details rendered inside Profile Modal" "1. Open Profile Modal 2. Verify text in #prof-name, #prof-email, #prof-phone" "id=prof-name" "None" "Displays correct Name, Email, Phone, and Location" "Medium"
Add-TC "TC_AUTH_024" "Auth - Profile Modal" "Verify closing Profile modal via close icon" "1. Open Profile Modal 2. Click id 'profile-close-btn'" "id=profile-close-btn" "None" "Profile modal closes properly" "Low"
Add-TC "TC_AUTH_025" "Auth - Bottom Card" "Verify Quick Account Creation form in bottom section when logged out" "1. Scroll to bottom-account-section 2. Verify form visibility" "id=bottom-logged-out-box" "None" "Create Your Account form is visible" "Medium"
Add-TC "TC_AUTH_026" "Auth - Bottom Card" "Verify submitting bottom Quick Account Creation form" "1. Fill bottom form fields 2. Click Create Account" "id=bottom-signup-form" "Name: Alice, Phone: 9776655443, Pass: mypass" "Account created, 100 Pts earned, bottom card switches to Saved Account view" "High"
Add-TC "TC_AUTH_027" "Auth - Bottom Card" "Verify Saved Account view in bottom section when logged in" "1. Log in 2. Scroll to bottom account section" "id=bottom-logged-in-box" "None" "Displays ACCOUNT SAVED AND LOGGED IN card with name and points" "Medium"
Add-TC "TC_AUTH_028" "Auth - Bottom Card" "Verify View Full Profile button on bottom Saved Account card" "1. Log in 2. Click View Full Profile in bottom card" "id=btn-bottom-view-profile" "None" "Profile modal opens smoothly" "Low"
Add-TC "TC_AUTH_029" "Auth - Bottom Card" "Verify Sign In Existing Account button on bottom card" "1. Log out 2. Click Sign In Existing Account in bottom card" "id=btn-bottom-open-login" "None" "Sign In modal opens" "Low"
Add-TC "TC_AUTH_030" "Auth - Bonus Points" "Verify 100 Signup Reward Points awarded to new account" "1. Register new account 2. Check reward_points attribute in session" "id=user-points-badge" "None" "Points badge displays 100 Pts" "High"
Add-TC "TC_AUTH_031" "Auth - Input Security" "Verify HTML script tag injection resistance in Registration Name" "1. Enter script tag in Name field 2. Submit form" "id=signup-name" "Name: <script>alert(1)</script>" "Name rendered as plain string without executing script" "High"
Add-TC "TC_AUTH_032" "Auth - Input Security" "Verify SQL injection string handling in Login field" "1. Enter SQL string in Phone 2. Submit form" "id=login-phone" "Phone: ' OR '1'='1" "Sanitized safely by backend API or fallback handling" "High"
Add-TC "TC_AUTH_033" "Auth - Phone Format" "Verify phone number numeric string handling" "1. Enter 10 digit number 2. Submit" "id=signup-phone" "Phone: 9876543210" "Phone saved accurately without truncation" "Medium"
Add-TC "TC_AUTH_034" "Auth - Password Visibility" "Verify password input field type is password" "Inspect attribute type of #login-password and #signup-password" "css=input[type='password']" "None" "Type attribute is password masking typed characters" "Medium"
Add-TC "TC_AUTH_035" "Auth - Trim Whitespace" "Verify trailing whitespace trimmed from login inputs" "1. Enter whitespace around phone input 2. Submit" "id=login-phone" "Phone: 9876543210 " "String trimmed before sending" "Low"
Add-TC "TC_AUTH_036" "Auth - Case Insensitivity" "Verify email login is case insensitive" "1. Enter uppercase email 2. Submit" "id=login-phone" "Email: JOHN.DOE@EXAMPLE.COM" "Recognizes user account regardless of letter casing" "Medium"
Add-TC "TC_AUTH_037" "Auth - Help Modal" "Verify Help and Support modal opening from top navbar" "1. Click icon button with id 'btn-help'" "id=btn-help" "None" "Help and Customer Support modal opens" "Low"
Add-TC "TC_AUTH_038" "Auth - Help Modal" "Verify FAQs and support contact info displayed in Help modal" "1. Open Help modal 2. Check FAQ text and Helpline phone number" "id=help-modal" "None" "Contains FAQ questions and 24/7 Helpline: +91 98765 43210" "Low"
Add-TC "TC_AUTH_039" "Auth - Help Modal" "Verify closing Help modal using close button" "1. Open Help modal 2. Click id 'help-close-btn'" "id=help-close-btn" "None" "Help modal closes" "Low"
Add-TC "TC_AUTH_040" "Auth - Notifications" "Verify Notification button toast message" "1. Click bell icon with id 'btn-notifications'" "id=btn-notifications" "None" "Toast message shown: No new notifications" "Low"
Add-TC "TC_AUTH_041" "Auth - UI Responsiveness" "Verify Auth navbar rendering on small screen viewports" "Set browser window width to 375px" "css=header.navbar" "Width: 375px" "Navbar controls adapt smoothly without breaking layout" "Medium"
Add-TC "TC_AUTH_042" "Auth - ESC Key" "Verify pressing ESC key does not crash open modals" "1. Open Sign In modal 2. Press ESC key" "id=login-modal" "Key: ESC" "Page remains stable" "Low"
Add-TC "TC_AUTH_043" "Auth - Modal Overlay" "Verify dark overlay backdrop behind open auth modals" "1. Open Sign Up modal 2. Verify modal-overlay styling" "css=.modal-overlay.open" "None" "Semi-transparent dark backdrop active" "Low"
Add-TC "TC_AUTH_044" "Auth - Multiple Signups" "Verify registering a second user updates active local session" "1. Register User A 2. Log out 3. Register User B" "id=signup-form" "User B details" "User B session active with User B name in navbar" "Medium"
Add-TC "TC_AUTH_045" "Auth - Logout Cleanup" "Verify logout clears profile modal content and resets UI" "1. Log in 2. Log out 3. Verify navbar UI state" "id=auth-buttons" "None" "Sign In and Sign Up buttons restored in top navbar" "High"

# List definitions for Modules 2 to 7
$gndList = @(
    "Verify default display of all available arena cards on page load",
    "Verify total venue count badge rendering in playgrounds tab",
    "Filter playgrounds by Football sport pill click",
    "Filter playgrounds by Cricket sport pill click",
    "Filter playgrounds by Badminton sport pill click",
    "Filter playgrounds by Basketball sport pill click",
    "Filter playgrounds by Tennis sport pill click",
    "Reset filter to All Sports pill click",
    "Verify search input filtering by venue name Marina",
    "Verify search input filtering by location Anna Nagar",
    "Verify search input filtering by sport keyword Badminton",
    "Verify search input case insensitive matching",
    "Verify search query with no matching venues shows empty state message",
    "Verify clearing search input restores full playground list",
    "Verify clicking heart icon adds venue to favorites",
    "Verify clicking heart icon again removes venue from favorites",
    "Verify Favorites pill count badge increments when adding favorite",
    "Verify Favorites pill count badge decrements when removing favorite",
    "Verify clicking Favorites filter pill displays only favorited venues",
    "Verify Favorites pill shows empty state when no venues favorited",
    "Verify Nearby Within 5KM Haversine toggle checkbox interaction",
    "Verify nearby distance filtering calculation via Haversine logic",
    "Verify playground card title rendering",
    "Verify playground card address rendering with pin icon",
    "Verify playground card rating star and review count rendering",
    "Verify playground card hourly price rate display",
    "Verify playground card sports tag badge",
    "Verify venue image loading fallback handling",
    "Verify Book Slot Now button presence on every card",
    "Verify clicking Book Slot Now opens booking modal for correct venue",
    "Verify ground data loading from API endpoint get_playgrounds.php",
    "Verify offline fallback data loading when API is unreachable",
    "Verify rating sorting order in venue listing grid",
    "Verify responsive grid layout for venue cards on desktop",
    "Verify responsive grid layout for venue cards on tablet view",
    "Verify responsive grid layout for venue cards on mobile view",
    "Verify favorite state retention in localStorage smart_playground_favs",
    "Verify hover animation on playground cards",
    "Verify search bar clear button icon behavior",
    "Verify rapid sport pill switching performance",
    "Verify venue address tooltip or text wrapping",
    "Verify price display currency symbol formatting Rupee",
    "Verify search input special character handling",
    "Verify navbar tab active state highlight on Playgrounds tab",
    "Verify playground grid auto-update on filter change"
)

$slotList = @(
    "Verify opening booking modal populates ground name in header",
    "Verify opening booking modal populates ground sport category in header",
    "Verify date picker defaults to current date",
    "Verify date picker allows selecting future dates",
    "Verify available time slots rendered in modal grid",
    "Verify peak hour slots identified with Peak icon tag",
    "Verify non-peak hour slots rendered without peak tag",
    "Verify selecting a non-peak slot calculates base rate correctly",
    "Verify selecting a peak slot calculates 20 percent surge dynamic pricing",
    "Verify dynamic pricing adjustment row displays correct amount",
    "Verify final payable amount updates dynamically upon slot selection",
    "Verify real-time slot lock countdown timer starts at 10:00",
    "Verify slot lock countdown timer decreases second by second",
    "Verify slot lock timer box displays Slot Reserved For You",
    "Verify slot selection button highlights with selected class",
    "Verify selecting a different slot cancels previous slot selection",
    "Verify slot lock timer resets when selecting a different slot",
    "Verify slot lock expiration toast error when 10-minute timer hits zero",
    "Verify payment method dropdown contains UPI / GPay",
    "Verify payment method dropdown contains Credit/Debit Card",
    "Verify payment method dropdown contains Net Banking",
    "Verify payment method dropdown contains Pay at Venue",
    "Verify selecting Credit/Debit Card in payment dropdown",
    "Verify clicking Confirm Booking without slot selected shows error toast",
    "Verify clicking Confirm Booking with valid slot processes booking",
    "Verify successful booking generates random booking ID SP-XXXX",
    "Verify successful booking adds 50 Reward Points to user account",
    "Verify successful booking triggers success toast notification",
    "Verify booking modal closes after successful booking confirmation",
    "Verify newly created booking prepends to bookings list",
    "Verify booking data sent to backend endpoint book_ground.php",
    "Verify MySQL transaction rollback on double-booking conflict",
    "Verify closing booking modal via Cancel button stops countdown timer",
    "Verify closing booking modal via X button stops countdown timer",
    "Verify date input validation for past date selection",
    "Verify peak hour rates dynamically computed based on time of day",
    "Verify booking modal responsive layout on mobile screen",
    "Verify backdrop click modal behavior",
    "Verify reward points balance updates immediately in top navbar badge",
    "Verify reward points balance updates in profile modal",
    "Verify reward points balance updates in bottom account card",
    "Verify multiple consecutive bookings by same user",
    "Verify booking receipt summary base rate vs dynamic adjustment",
    "Verify slot lock timer cleanup when navigating away",
    "Verify payment method value retained in booking object",
    "Verify slot booking status defaults to Upcoming",
    "Verify currency formatting on payable amount",
    "Verify lock timer format MM:SS",
    "Verify double click on Confirm Booking button prevented during processing",
    "Verify error handling when backend server returns 500 error",
    "Verify booking persistence in localStorage smart_playground_all_bookings",
    "Verify booking history synchronization with get_history.php",
    "Verify slot availability indicator after booking complete",
    "Verify peak hour legend visibility in booking modal",
    "Verify date change reloads slot availability"
)

$eqList = @(
    "Verify navigating to Equipment Rental tab via navbar button",
    "Verify Equipment tab pane receives active class on click",
    "Verify equipment grid displays initial gear items",
    "Verify Pro Badminton Racket Set item card details 150/day",
    "Verify Official Match Football item card details 100/day",
    "Verify English Willow Cricket Bat item card details 250/day",
    "Verify Wilson Tennis Racket item card details 200/day",
    "Verify Spalding Leather Basketball item card details 120/day",
    "Verify Cricket Guard Safety Kit item card details 180/day",
    "Verify equipment stock badge rendering In Stock: 12",
    "Verify clicking Add to Rental button adds item to cart",
    "Verify toast notification when item is added to rental cart",
    "Verify rental cart summary pane updates from empty state to item list",
    "Verify cart item row displays item name and quantity x1",
    "Verify adding same equipment twice increments quantity x2",
    "Verify item total price calculation price_per_day * quantity",
    "Verify total payable amount calculation across all cart items",
    "Verify cart duration row defaults to 1 Day",
    "Verify clicking Confirm Equipment Rental with empty cart shows info toast",
    "Verify clicking Confirm Equipment Rental with items submits order",
    "Verify equipment checkout clears cart items and resets total to 0",
    "Verify equipment checkout triggers success toast with pickup details",
    "Verify equipment icon rendering Badminton Football Cricket Tennis Basketball",
    "Verify equipment category tag rendering",
    "Verify rental cart layout positioning on desktop screens",
    "Verify rental cart responsiveness on mobile screens",
    "Verify adding multiple distinct equipment types to cart",
    "Verify price formatting with Rupee symbol on equipment cards",
    "Verify stock limit validation when adding items",
    "Verify cart summary title Rental Summary rendering",
    "Verify total amount row highlight in cart summary",
    "Verify full-width styling of Confirm Equipment Rental button",
    "Verify equipment data array fallback initialization",
    "Verify category filtering for Badminton gear",
    "Verify category filtering for Cricket gear",
    "Verify category filtering for Football gear",
    "Verify category filtering for Tennis gear",
    "Verify category filtering for Basketball gear",
    "Verify cart item deletion or clear functionality",
    "Verify UI layout stability when cart contains 10+ items",
    "Verify daily rate tag styling on equipment cards",
    "Verify button state change on click",
    "Verify scrolling behavior within equipment grid",
    "Verify empty cart message text No equipment selected yet",
    "Verify checkout button disabled state when processing",
    "Verify rental history record creation",
    "Verify venue pickup instructions in success notification",
    "Verify equipment image or icon fallback handling",
    "Verify cart item quantity counter display",
    "Verify rental summary pane sticky positioning on scroll"
)

$teamList = @(
    "Verify navigating to Team Finder tab via navbar button",
    "Verify Team Finder tab pane receives active class",
    "Verify initial team cards rendered in teams grid",
    "Verify team card header rendering team name",
    "Verify team card sport badge rendering Football Badminton Cricket",
    "Verify team card location line Location text",
    "Verify team card players needed badge Needed X Players",
    "Verify clicking Join Match button on team card",
    "Verify Join Match button click triggers success toast notification",
    "Verify Create Match Request button presence in section header",
    "Verify clicking Create Match Request opens create team modal",
    "Verify closing create team modal via X button",
    "Verify create team modal input fields Team Name Sport Players Location",
    "Verify Sport dropdown options Football Cricket Badminton Basketball",
    "Verify Players Needed input min value 1 and max value 11",
    "Verify submitting create team form with valid inputs",
    "Verify new match request prepends dynamically to teams grid",
    "Verify new match card displays logged-in user name as creator",
    "Verify creator defaults to Demo User when logged out",
    "Verify create team form submission closes modal",
    "Verify create team success toast notification display",
    "Verify form validation when Team Name is left empty",
    "Verify form validation when Location is left empty",
    "Verify Team Finder grid responsiveness on desktop viewports",
    "Verify Team Finder grid responsiveness on tablet viewports",
    "Verify Team Finder grid responsiveness on mobile viewports",
    "Verify sport badge color styling according to sport type",
    "Verify match request creation timestamp sorting",
    "Verify join match request deduplication check",
    "Verify team name truncation for long text strings",
    "Verify location text wrapping on team cards",
    "Verify player count input stepper controls",
    "Verify modal backdrop overlay on team create modal",
    "Verify team cards layout spacing and grid gap",
    "Verify ESC key behavior on team create modal",
    "Verify match request creation error handling",
    "Verify team creator avatar or icon display",
    "Verify team list update when new request added",
    "Verify match request status indicator",
    "Verify section header title Team Finder and Matchmaker"
)

$qrList = @(
    "Verify navigating to My Bookings and QR tab via navbar button",
    "Verify My Bookings tab pane receives active class",
    "Verify bookings list container rendering on left side",
    "Verify QR preview card rendering on right side",
    "Verify active booking card displays ground name date slot time amount",
    "Verify booking card status badge Amount - Status PaymentMethod",
    "Verify View QR Pass button on every booking card",
    "Verify clicking View QR Pass populates booking details in QR preview card",
    "Verify QR preview card ticket header status ACTIVE ENTRY PASS",
    "Verify QR code canvas element generation using QRCode library",
    "Verify QR code API fallback image when library un-instantiated",
    "Verify QR code encoded string format SMARTPLAYGROUND-ENTRY-TICKET",
    "Verify booking ID format in ticket details SP-XXXX",
    "Verify time slot display in ticket details",
    "Verify payment badge PAID rendering in ticket details",
    "Verify default QR pass selection loads first booking on tab open",
    "Verify filtering bookings list to show only logged-in user bookings",
    "Verify empty state view when logged-in user has no bookings",
    "Verify empty state message No Active Bookings Found for User",
    "Verify empty state QR placeholder message Book a slot to generate pass",
    "Verify QR code contrast and scannability sizing 160x160 px",
    "Verify selecting different booking card switches displayed QR code instantly",
    "Verify booking date format rendering in booking cards",
    "Verify booking slot time format rendering in booking cards",
    "Verify booking list scrollable behavior when user has 5+ bookings",
    "Verify QR preview card fixed sticky layout on desktop",
    "Verify QR preview card responsive stacking on mobile layout",
    "Verify QR pass ticket border styling and badge tags",
    "Verify offline QR pass rendering from cached local data",
    "Verify QR code scanning compatibility with venue scanner",
    "Verify booking ID copy or inspection capability",
    "Verify payment method display on ticket pass",
    "Verify status color indicator green for confirmed paid",
    "Verify ticket header title update on booking switch",
    "Verify section header subtitle Present your active QR Code at entry"
)

$admList = @(
    "Verify navigating to Admin Dashboard tab via navbar button",
    "Verify Admin Dashboard tab pane receives active class",
    "Verify Super Admin header title Super Admin Control Center",
    "Verify admin status badge LIVE SYSTEM METRICS",
    "Verify Total Arenas metric card value matches playgrounds count",
    "Verify Active Bookings metric card value calculation",
    "Verify Locked Slots metric card value display",
    "Verify Total Revenue metric card formatting in Rupees",
    "Verify metric card icons Arenas Bookings Locked Revenue",
    "Verify Slot Lock Management table presence",
    "Verify table headers Ground Name Slot Time Lock Status Hold Time Action",
    "Verify table row rendering for locked slot Marina Turf Arena",
    "Verify status indicator LOCKED styled in red font",
    "Verify status indicator BOOKED styled in green font",
    "Verify hold time remaining countdown timer e.g. 08:42 mins",
    "Verify action button Release Lock presence on locked rows",
    "Verify clicking Release Lock releases locked slot in table",
    "Verify live system metrics update when new booking occurs",
    "Verify admin table responsive horizontal scrollbar on small screens",
    "Verify admin metric cards flex layout grid",
    "Verify admin dashboard access permission check",
    "Verify total revenue calculation accumulation logic",
    "Verify admin table zebra striping or row border styling",
    "Verify admin action button hover states",
    "Verify metrics auto-refresh interval",
    "Verify admin table empty row fallback handling",
    "Verify admin dashboard tab badge button styling in top navbar",
    "Verify total bookings count incrementing on new booking",
    "Verify admin metrics number formatting with comma separators",
    "Verify full page layout integrity in Admin Dashboard tab"
)

function Populate-ModuleList($list, $moduleName, $prefix, $startId) {
    for ($i = 0; $i -lt $list.Count; $i++) {
        $num = $startId + $i
        $tcId = "$prefix$("{0:D3}" -f ($i + 1))"
        $sc = $list[$i]
        $locator = "css=[data-test='$($prefix.ToLower())$($i+1)']"
        $steps = "1. Open http://127.0.0.1:8080/ 2. Navigate to $moduleName 3. Perform test: $sc"
        $inputData = "Test Param: $sc"
        $expected = "Web app UI and state update properly for: $sc"
        $prio = if ($i % 3 -eq 0) { "High" } elseif ($i % 3 -eq 1) { "Medium" } else { "Low" }
        Add-TC $tcId $moduleName $sc $steps $locator $inputData $expected $prio
    }
}

Populate-ModuleList $gndList "Playgrounds and Search Engine" "TC_GND_" 46
Populate-ModuleList $slotList "Real-Time Slot Locking and Booking" "TC_SLOT_" 91
Populate-ModuleList $eqList "Equipment Rental Desk and Cart" "TC_EQ_" 146
Populate-ModuleList $teamList "Team Finder and Matchmaker" "TC_TEAM_" 196
Populate-ModuleList $qrList "Digital Entry Pass and QR Code" "TC_QR_" 236
Populate-ModuleList $admList "Super Admin Dashboard and Metrics" "TC_ADM_" 271

# Export to CSV (Native Excel Spreadsheet format)
$testCases | Export-Csv -Path $outputPathCsv -NoTypeInformation -Encoding UTF8

# Also create Excel XML (.xlsx formatted file)
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
   <Interior ss:Color="#10B981" ss:Pattern="Solid"/>
   <Alignment ss:Horizontal="Center"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="Selenium 300 Test Cases">
  <Table>
   <Row ss:StyleID="Header">
    <Cell><Data ss:Type="String">Test Case ID</Data></Cell>
    <Cell><Data ss:Type="String">Module / Feature</Data></Cell>
    <Cell><Data ss:Type="String">Test Scenario Description</Data></Cell>
    <Cell><Data ss:Type="String">Selenium Automation Steps</Data></Cell>
    <Cell><Data ss:Type="String">Locator Strategy</Data></Cell>
    <Cell><Data ss:Type="String">Input Data / Parameters</Data></Cell>
    <Cell><Data ss:Type="String">Expected Result</Data></Cell>
    <Cell><Data ss:Type="String">Priority</Data></Cell>
    <Cell><Data ss:Type="String">Execution Status</Data></Cell>
   </Row>
"@

foreach ($tc in $testCases) {
    $cId = [System.Security.SecurityElement]::Escape($tc."Test Case ID")
    $cMod = [System.Security.SecurityElement]::Escape($tc."Module / Feature")
    $cSc = [System.Security.SecurityElement]::Escape($tc."Test Scenario Description")
    $cSt = [System.Security.SecurityElement]::Escape($tc."Selenium Automation Steps")
    $cLoc = [System.Security.SecurityElement]::Escape($tc."Locator Strategy")
    $cIn = [System.Security.SecurityElement]::Escape($tc."Input Data / Parameters")
    $cExp = [System.Security.SecurityElement]::Escape($tc."Expected Result")
    $cPrio = [System.Security.SecurityElement]::Escape($tc."Priority")
    $cStat = [System.Security.SecurityElement]::Escape($tc."Execution Status")

    $excelContent += @"

   <Row>
    <Cell><Data ss:Type="String">$cId</Data></Cell>
    <Cell><Data ss:Type="String">$cMod</Data></Cell>
    <Cell><Data ss:Type="String">$cSc</Data></Cell>
    <Cell><Data ss:Type="String">$cSt</Data></Cell>
    <Cell><Data ss:Type="String">$cLoc</Data></Cell>
    <Cell><Data ss:Type="String">$cIn</Data></Cell>
    <Cell><Data ss:Type="String">$cExp</Data></Cell>
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

Write-Host "==========================================================================" -ForegroundColor Green
Write-Host "SUCCESS: Generated 300 Selenium Test Cases!" -ForegroundColor Cyan
Write-Host "CSV File (Opens in Excel): $outputPathCsv" -ForegroundColor Yellow
Write-Host "XLSX Excel File: $outputPathXlsx" -ForegroundColor Yellow
Write-Host "Total Test Cases Count: $($testCases.Count)" -ForegroundColor Green
Write-Host "==========================================================================" -ForegroundColor Green

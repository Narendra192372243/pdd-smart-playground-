// SmartPlayground Web Application Core Logic
document.addEventListener('DOMContentLoaded', () => {
    // State Management
    let playgroundsData = [];
    let equipmentData = [];
    let teamsData = [];
    let bookingsData = [];
    let favorites = JSON.parse(localStorage.getItem('smart_playground_favs') || '[]');
    let currentUser = JSON.parse(localStorage.getItem('smart_playground_user') || 'null');
    let cart = [];
    
    let selectedGround = null;
    let selectedSlot = null;
    let lockTimerInterval = null;
    let activeQrBooking = null;

    // Initial Datasets (used as immediate fallback if MySQL is starting or offline)
    const initialPlaygrounds = [
        { id: 1, name: "Marina Turf Arena", address: "Beach Road, Triplicane, Chennai", latitude: 13.0500, longitude: 80.2824, sports: "Football, Cricket", price_per_hour: 800, rating: 4.8, reviews_count: 142, status: "Open Now", image: "https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&w=600&q=80" },
        { id: 2, name: "Anna Nagar Smash Badminton Court", address: "2nd Avenue, Anna Nagar, Chennai", latitude: 13.0850, longitude: 80.2100, sports: "Badminton", price_per_hour: 450, rating: 4.9, reviews_count: 98, status: "Open Now", image: "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=600&q=80" },
        { id: 3, name: "Adyar Pro Basketball Zone", address: "LB Road, Adyar, Chennai", latitude: 13.0012, longitude: 80.2565, sports: "Basketball", price_per_hour: 600, rating: 4.7, reviews_count: 85, status: "Open Now", image: "https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&w=600&q=80" },
        { id: 4, name: "Velachery Multi-Sport Turf", address: "Bypass Road, Velachery, Chennai", latitude: 12.9750, longitude: 80.2200, sports: "Football, Cricket", price_per_hour: 750, rating: 4.6, reviews_count: 110, status: "Open Now", image: "https://images.unsplash.com/photo-1575361204480-aadea25e6e68?auto=format&fit=crop&w=600&q=80" },
        { id: 5, name: "Nungambakkam Tennis Club", address: "College Road, Nungambakkam, Chennai", latitude: 13.0620, longitude: 80.2400, sports: "Tennis", price_per_hour: 900, rating: 4.9, reviews_count: 175, status: "Open Now", image: "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&fit=crop&w=600&q=80" },
        { id: 6, name: "ECR Beachside Football Ground", address: "East Coast Road, ECR, Chennai", latitude: 12.9100, longitude: 80.2500, sports: "Football", price_per_hour: 1000, rating: 4.9, reviews_count: 210, status: "Open Now", image: "https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=600&q=80" }
    ];

    const initialEquipment = [
        { id: 101, name: "Pro Badminton Racket Set", category: "Badminton", price_per_day: 150, stock: 12, icon: "🏸" },
        { id: 102, name: "Official Match Football (Size 5)", category: "Football", price_per_day: 100, stock: 8, icon: "⚽" },
        { id: 103, name: "English Willow Cricket Bat", category: "Cricket", price_per_day: 250, stock: 5, icon: "🏏" },
        { id: 104, name: "Wilson Tennis Racket + Balls", category: "Tennis", price_per_day: 200, stock: 10, icon: "🎾" },
        { id: 105, name: "Spalding Leather Basketball", category: "Basketball", price_per_day: 120, stock: 7, icon: "🏀" },
        { id: 106, name: "Cricket Guard Safety Kit", category: "Cricket", price_per_day: 180, stock: 4, icon: "🛡️" }
    ];

    const initialTeams = [
        { id: 1, name: "Chennai Strikers FC", sport: "Football", creator: "Alex M.", needed: 3, location: "Marina Turf Arena" },
        { id: 2, name: "Anna Nagar Smashers", sport: "Badminton", creator: "Priya S.", needed: 1, location: "Smash Court" },
        { id: 3, name: "Velachery Kings", sport: "Cricket", creator: "Karthik R.", needed: 4, location: "Multi-Sport Turf" }
    ];

    const initialBookings = [
        { id: "SP-8942", ground_name: "Marina Turf Arena", date: "2026-07-22", slot_time: "06:00 PM - 07:00 PM", amount: 960, status: "Upcoming" },
        { id: "SP-7210", ground_name: "Anna Nagar Smash Badminton Court", date: "2026-07-20", slot_time: "07:00 AM - 08:00 AM", amount: 450, status: "Completed" }
    ];

    // Navigation Tab Switching
    const navButtons = document.querySelectorAll('.nav-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');

    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.getAttribute('data-tab');
            navButtons.forEach(b => b.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));
            
            btn.classList.add('active');
            document.getElementById(targetTab).classList.add('active');
        });
    });

    // --- AUTHENTICATION & USER SESSION LOGIC ---
    const signupModal = document.getElementById('signup-modal');
    const loginModal = document.getElementById('login-modal');
    const profileModal = document.getElementById('profile-modal');
    const helpModal = document.getElementById('help-modal');

    document.getElementById('btn-open-signup').addEventListener('click', () => signupModal.classList.add('open'));
    document.getElementById('btn-open-login').addEventListener('click', () => loginModal.classList.add('open'));
    document.getElementById('signup-close-btn').addEventListener('click', () => signupModal.classList.remove('open'));
    document.getElementById('login-close-btn').addEventListener('click', () => loginModal.classList.remove('open'));
    document.getElementById('profile-close-btn').addEventListener('click', () => profileModal.classList.remove('open'));
    document.getElementById('help-close-btn').addEventListener('click', () => helpModal.classList.remove('open'));
    document.getElementById('btn-help').addEventListener('click', () => helpModal.classList.add('open'));

    document.getElementById('link-to-login').addEventListener('click', (e) => {
        e.preventDefault();
        signupModal.classList.remove('open');
        loginModal.classList.add('open');
    });

    document.getElementById('link-to-signup').addEventListener('click', (e) => {
        e.preventDefault();
        loginModal.classList.remove('open');
        signupModal.classList.add('open');
    });

    // Handle Signup Form Submit
    document.getElementById('signup-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('signup-name').value.trim();
        const email = document.getElementById('signup-email').value.trim();
        const phone = document.getElementById('signup-phone').value.trim();
        const location = (document.getElementById('signup-location')?.value || 'Adyar, Chennai').trim();
        const password = document.getElementById('signup-password').value.trim();

        if (!name || !phone || !password) {
            showToast("Please fill in required fields (Name, Phone, Password)", "error");
            return;
        }

        const payload = { name, email, phone, location, password };

        // Attempt API registration first
        fetch('register.php', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(data => {
            if (data.status === 'success') {
                currentUser = data.user || { id: Date.now(), name, email, phone, location, reward_points: 100 };
                saveUserSession(currentUser);
                signupModal.classList.remove('open');
                showToast(`🎉 Account Created! Welcome ${name}. You earned 100 Reward Points!`, "success");
            } else {
                // If DB says already exists or error, handle fallback session
                showToast(data.message || "Registration note: Using local session profile.", "info");
                currentUser = { id: Date.now(), name, email, phone, location, reward_points: 100 };
                saveUserSession(currentUser);
                signupModal.classList.remove('open');
            }
        })
        .catch(() => {
            // Offline/Local Fallback
            currentUser = { id: Date.now(), name, email: email || 'user@smartplayground.com', phone, location, reward_points: 100 };
            saveUserSession(currentUser);
            signupModal.classList.remove('open');
            showToast(`🎉 Account Created! Welcome ${name}. You earned 100 Reward Points!`, "success");
        });
    });

    // Handle Bottom Quick Signup Form Submit
    const bottomSignupForm = document.getElementById('bottom-signup-form');
    if (bottomSignupForm) {
        bottomSignupForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const name = document.getElementById('bottom-signup-name').value.trim();
            const email = document.getElementById('bottom-signup-email').value.trim();
            const phone = document.getElementById('bottom-signup-phone').value.trim();
            const password = document.getElementById('bottom-signup-password').value.trim();

            if (!name || !phone || !password) {
                showToast("Please fill in required fields (Name, Phone, Password)", "error");
                return;
            }

            const payload = { name, email, phone, location: 'Adyar, Chennai', password };

            fetch('register.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    currentUser = data.user || { id: Date.now(), name, email, phone, location: 'Adyar, Chennai', reward_points: 100 };
                } else {
                    currentUser = { id: Date.now(), name, email, phone, location: 'Adyar, Chennai', reward_points: 100 };
                }
                saveUserSession(currentUser);
                showToast(`🎉 Account Created! Welcome ${name}. You earned 100 Reward Points!`, "success");
            })
            .catch(() => {
                currentUser = { id: Date.now(), name, email: email || 'user@smartplayground.com', phone, location: 'Adyar, Chennai', reward_points: 100 };
                saveUserSession(currentUser);
                showToast(`🎉 Account Created! Welcome ${name}. You earned 100 Reward Points!`, "success");
            });
        });
    }

    const btnBottomOpenLogin = document.getElementById('btn-bottom-open-login');
    if (btnBottomOpenLogin) {
        btnBottomOpenLogin.addEventListener('click', () => loginModal.classList.add('open'));
    }

    const btnBottomViewProfile = document.getElementById('btn-bottom-view-profile');
    if (btnBottomViewProfile) {
        btnBottomViewProfile.addEventListener('click', () => profileModal.classList.add('open'));
    }

    // Handle Login Form Submit
    document.getElementById('login-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const phone = document.getElementById('login-phone').value.trim();
        const password = document.getElementById('login-password').value.trim();

        if (!phone || !password) {
            showToast("Please enter phone/email and password", "error");
            return;
        }

        fetch('login.php', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phone, password })
        })
        .then(res => res.json())
        .then(data => {
            if (data.status === 'success') {
                currentUser = data.user;
                saveUserSession(currentUser);
                loginModal.classList.remove('open');
                showToast(`Welcome back, ${currentUser.name}!`, "success");
            } else {
                // Local fallback authentication
                showToast(data.message || "Invalid credentials. Signing in as Demo User.", "info");
                currentUser = { id: 1, name: "Narendra Kumar", email: "narendra@example.com", phone: phone, reward_points: 150 };
                saveUserSession(currentUser);
                loginModal.classList.remove('open');
            }
        })
        .catch(() => {
            currentUser = { id: 1, name: "Narendra Kumar", email: "narendra@example.com", phone: phone, reward_points: 150 };
            saveUserSession(currentUser);
            loginModal.classList.remove('open');
            showToast(`Signed in successfully!`, "success");
        });
    });

    function saveUserSession(user) {
        currentUser = user;
        localStorage.setItem('smart_playground_user', JSON.stringify(user));
        updateAuthUI();
        renderBookings();
        renderAdminDashboard();
    }

    function updateAuthUI() {
        const authButtons = document.getElementById('auth-buttons');
        const loggedInProfile = document.getElementById('logged-in-profile');
        const bottomLoggedOutBox = document.getElementById('bottom-logged-out-box');
        const bottomLoggedInBox = document.getElementById('bottom-logged-in-box');

        if (currentUser) {
            if (authButtons) authButtons.classList.add('hidden');
            if (loggedInProfile) loggedInProfile.classList.remove('hidden');

            document.getElementById('display-username').textContent = currentUser.name || "User";
            document.getElementById('user-points-badge').textContent = `🏆 ${currentUser.reward_points || 100} Pts`;

            // Profile Modal Info
            document.getElementById('prof-name').textContent = currentUser.name;
            document.getElementById('prof-email').textContent = currentUser.email || 'No email provided';
            document.getElementById('prof-phone').textContent = `📞 +91 ${currentUser.phone || '9876543210'}`;
            if (document.getElementById('prof-location')) {
                document.getElementById('prof-location').textContent = `📍 ${currentUser.location || 'Adyar, Chennai'}`;
            }
            document.getElementById('prof-points').textContent = currentUser.reward_points || 100;
            document.getElementById('prof-total-bookings').textContent = bookingsData.length;

            // Bottom Saved Account Card Info
            if (bottomLoggedOutBox) bottomLoggedOutBox.classList.add('hidden');
            if (bottomLoggedInBox) {
                bottomLoggedInBox.classList.remove('hidden');
                document.getElementById('bottom-account-name').textContent = currentUser.name;
                document.getElementById('bottom-account-email-phone').textContent = `${currentUser.email || 'user@example.com'} | +91 ${currentUser.phone || '9876543210'}`;
                document.getElementById('bottom-account-points').textContent = `🏆 ${currentUser.reward_points || 100} Pts`;
            }
        } else {
            if (authButtons) authButtons.classList.remove('hidden');
            if (loggedInProfile) loggedInProfile.classList.add('hidden');
            if (bottomLoggedOutBox) bottomLoggedOutBox.classList.remove('hidden');
            if (bottomLoggedInBox) bottomLoggedInBox.classList.add('hidden');
        }
    }

    // Profile Click -> Open Profile Modal
    document.getElementById('user-profile-btn').addEventListener('click', () => {
        if (currentUser) {
            profileModal.classList.add('open');
        }
    });

    document.getElementById('user-points-badge').addEventListener('click', () => {
        if (currentUser) {
            profileModal.classList.add('open');
        }
    });

    // Logout Action
    document.getElementById('btn-logout').addEventListener('click', () => {
        currentUser = null;
        localStorage.removeItem('smart_playground_user');
        profileModal.classList.remove('open');
        updateAuthUI();
        showToast("Signed out successfully", "info");
    });

    // Notifications Button
    document.getElementById('btn-notifications').addEventListener('click', () => {
        showToast("🔔 No new notifications. You are all caught up!", "info");
    });

    // --- FAVORITES LOGIC ---
    function toggleFavorite(groundId) {
        const index = favorites.indexOf(groundId);
        if (index > -1) {
            favorites.splice(index, 1);
            showToast("Removed from favorites", "info");
        } else {
            favorites.push(groundId);
            showToast("❤️ Saved to favorites!", "success");
        }
        localStorage.setItem('smart_playground_favs', JSON.stringify(favorites));
        updateFavCount();
        renderPlaygrounds(playgroundsData);
    }

    function updateFavCount() {
        document.getElementById('fav-count').textContent = favorites.length;
    }

    // --- DATA LOADING ---
    function loadData() {
        const savedBookings = localStorage.getItem('smart_playground_all_bookings');
        if (savedBookings) {
            try {
                bookingsData = JSON.parse(savedBookings);
            } catch(e) {
                bookingsData = initialBookings;
            }
        } else {
            bookingsData = initialBookings;
        }

        fetch('get_playgrounds.php')
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data) && data.length > 0) {
                    playgroundsData = data;
                } else {
                    playgroundsData = initialPlaygrounds;
                }
                renderPlaygrounds(playgroundsData);
            })
            .catch(() => {
                playgroundsData = initialPlaygrounds;
                renderPlaygrounds(playgroundsData);
            });

        if (currentUser && currentUser.id) {
            fetch(`get_history.php?user_id=${currentUser.id}`)
                .then(res => res.json())
                .then(resData => {
                    if (resData.status === 'success' && Array.isArray(resData.data) && resData.data.length > 0) {
                        const serverBookings = resData.data.map(b => ({
                            id: `SP-${b.id}`,
                            user_id: b.user_id,
                            user_name: currentUser.name,
                            ground_name: b.playground_name || 'Sports Arena',
                            date: b.booking_date,
                            slot_time: b.time_slot,
                            amount: b.amount || 800,
                            payment_method: 'UPI / Online',
                            status: b.status || 'Confirmed'
                        }));
                        serverBookings.forEach(sb => {
                            if (!bookingsData.some(lb => lb.id === sb.id)) {
                                bookingsData.unshift(sb);
                            }
                        });
                        localStorage.setItem('smart_playground_all_bookings', JSON.stringify(bookingsData));
                        renderBookings();
                        renderAdminDashboard();
                    }
                })
                .catch(() => {});
        }

        equipmentData = initialEquipment;
        teamsData = initialTeams;

        updateFavCount();
        updateAuthUI();
        renderEquipment();
        renderTeams();
        renderBookings();
        renderAdminDashboard();
    }

    // Render Playgrounds
    function renderPlaygrounds(list) {
        const grid = document.getElementById('playground-grid');
        document.getElementById('grounds-count').textContent = `${list.length} Venues Found`;

        if (list.length === 0) {
            grid.innerHTML = `<div style="grid-column:1/-1; text-align:center; padding:3rem; color:var(--text-muted);">No arenas found matching your criteria.</div>`;
            return;
        }

        grid.innerHTML = list.map(g => {
            const isFav = favorites.includes(g.id);
            return `
                <div class="card">
                    <div class="card-img-wrapper">
                        <img src="${g.image || 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&w=600&q=80'}" alt="${g.name}">
                        <span class="card-tag">${g.sports}</span>
                        <button class="btn-fav ${isFav ? 'active' : ''}" data-fav-id="${g.id}" title="Save Venue">
                            ${isFav ? '❤️' : '🤍'}
                        </button>
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">${g.name}</h3>
                        <p class="card-address">📍 ${g.address}</p>
                        <div class="card-meta-row">
                            <div class="rating-box">
                                ★ ${g.rating || '4.8'} <span class="reviews">(${g.reviews_count || 100}+ reviews)</span>
                            </div>
                            <div class="price-box">
                                <span class="price-val">₹${g.price_per_hour}</span>
                                <span class="price-lbl"> / hour</span>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer">
                        <button class="btn-primary full-width btn-book-ground" data-id="${g.id}">⚡ Book Slot Now</button>
                    </div>
                </div>
            `;
        }).join('');

        // Attach Click Listeners for Booking and Favorites
        document.querySelectorAll('.btn-book-ground').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = parseInt(e.target.getAttribute('data-id'));
                openBookingModal(id);
            });
        });

        document.querySelectorAll('.btn-fav').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const id = parseInt(btn.getAttribute('data-fav-id'));
                toggleFavorite(id);
            });
        });
    }

    // Filter Pills Event Handler
    const filterPills = document.querySelectorAll('.pill');
    filterPills.forEach(pill => {
        pill.addEventListener('click', () => {
            filterPills.forEach(p => p.classList.remove('active'));
            pill.classList.add('active');
            const sport = pill.getAttribute('data-sport');

            if (sport === 'All') {
                renderPlaygrounds(playgroundsData);
            } else if (sport === 'Favorites') {
                const favGrounds = playgroundsData.filter(g => favorites.includes(g.id));
                renderPlaygrounds(favGrounds);
            } else {
                const filtered = playgroundsData.filter(g => g.sports.includes(sport));
                renderPlaygrounds(filtered);
            }
        });
    });

    // Search Filter
    document.getElementById('search-input').addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        const filtered = playgroundsData.filter(g => 
            g.name.toLowerCase().includes(query) ||
            g.sports.toLowerCase().includes(query) ||
            g.address.toLowerCase().includes(query)
        );
        renderPlaygrounds(filtered);
    });

    // Open Booking Modal & Real-time Slot Locking
    const modal = document.getElementById('booking-modal');
    function openBookingModal(groundId) {
        selectedGround = playgroundsData.find(g => g.id === groundId);
        if (!selectedGround) return;

        document.getElementById('modal-ground-name').textContent = selectedGround.name;
        document.getElementById('modal-ground-sport').textContent = selectedGround.sports;
        document.getElementById('summary-base-rate').textContent = `₹${selectedGround.price_per_hour} / hr`;

        const slotsGrid = document.getElementById('modal-slots-grid');
        const slots = [
            { time: "06:00 AM - 07:00 AM", peak: false },
            { time: "07:00 AM - 08:00 AM", peak: false },
            { time: "05:00 PM - 06:00 PM", peak: true },
            { time: "06:00 PM - 07:00 PM", peak: true },
            { time: "07:00 PM - 08:00 PM", peak: true },
            { time: "08:00 PM - 09:00 PM", peak: true },
        ];

        slotsGrid.innerHTML = slots.map((s, idx) => `
            <button class="slot-btn ${s.peak ? 'peak' : ''}" data-time="${s.time}" data-peak="${s.peak}">
                ${s.time.split(' - ')[0]} ${s.peak ? '🔥' : ''}
            </button>
        `).join('');

        selectedSlot = null;
        updateSummaryPrice(0, false);
        document.getElementById('slot-lock-timer-box').classList.add('hidden');

        document.querySelectorAll('.slot-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
                btn.classList.add('selected');

                const time = btn.getAttribute('data-time');
                const isPeak = btn.getAttribute('data-peak') === 'true';
                selectedSlot = { time, isPeak };

                updateSummaryPrice(selectedGround.price_per_hour, isPeak);
                startSlotLockCountdown();
            });
        });

        modal.classList.add('open');
    }

    function updateSummaryPrice(basePrice, isPeak) {
        const peakAdj = isPeak ? Math.round(basePrice * 0.20) : 0;
        const total = basePrice + peakAdj;

        document.getElementById('summary-dynamic-adj').textContent = isPeak ? `+₹${peakAdj} (Peak Hour)` : '₹0';
        document.getElementById('summary-total-amount').textContent = `₹${total}`;
    }

    function startSlotLockCountdown() {
        clearInterval(lockTimerInterval);
        const lockBox = document.getElementById('slot-lock-timer-box');
        const countdownEl = document.getElementById('lock-countdown');
        lockBox.classList.remove('hidden');

        let secondsLeft = 600;
        lockTimerInterval = setInterval(() => {
            secondsLeft--;
            const mins = Math.floor(secondsLeft / 60);
            const secs = secondsLeft % 60;
            countdownEl.textContent = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

            if (secondsLeft <= 0) {
                clearInterval(lockTimerInterval);
                showToast("Slot lock expired! Please select again.", "error");
                lockBox.classList.add('hidden');
                selectedSlot = null;
            }
        }, 1000);
    }

    document.getElementById('modal-close-btn').addEventListener('click', closeModal);
    document.getElementById('modal-cancel-btn').addEventListener('click', closeModal);

    function closeModal() {
        modal.classList.remove('open');
        clearInterval(lockTimerInterval);
    }

    // Confirm Booking & Payment
    document.getElementById('btn-confirm-booking').addEventListener('click', () => {
        if (!selectedSlot) {
            showToast("Please select an available time slot first!", "error");
            return;
        }

        const date = document.getElementById('booking-date-input').value;
        const paymentMethod = document.getElementById('payment-method-select').value;
        const peakAdj = selectedSlot.isPeak ? Math.round(selectedGround.price_per_hour * 0.20) : 0;
        const finalAmount = selectedGround.price_per_hour + peakAdj;
        const bookingId = `SP-${Math.floor(1000 + Math.random() * 9000)}`;

        const userName = currentUser ? currentUser.name : "Narendra Kumar";
        const userId = currentUser ? currentUser.id : "1";
        const userPhone = currentUser ? currentUser.phone : "9876543210";

        const newBooking = {
            id: bookingId,
            user_id: userId,
            user_name: userName,
            user_phone: userPhone,
            ground_name: selectedGround.name,
            date: date,
            slot_time: selectedSlot.time,
            amount: finalAmount,
            payment_method: paymentMethod,
            status: "Upcoming",
            created_at: new Date().toISOString()
        };

        bookingsData.unshift(newBooking);
        localStorage.setItem('smart_playground_all_bookings', JSON.stringify(bookingsData));

        try {
            const formData = new FormData();
            formData.append('user_id', userId);
            formData.append('ground_id', selectedGround.id);
            formData.append('booking_date', date);
            formData.append('time_slot', selectedSlot.time);
            formData.append('amount', finalAmount);
            fetch('book_ground.php', { method: 'POST', body: formData }).catch(() => {});
        } catch (e) {}

        // Reward points update
        if (currentUser) {
            currentUser.reward_points = (currentUser.reward_points || 100) + 50;
            saveUserSession(currentUser);
        }

        renderBookings();
        renderAdminDashboard();

        closeModal();
        showToast(`🎉 Success! Booking ${bookingId} confirmed for ${userName}! +50 Reward Pts earned.`, "success");
    });

    // Render Equipment
    function renderEquipment() {
        const grid = document.getElementById('equipment-grid');
        grid.innerHTML = equipmentData.map(eq => `
            <div class="eq-card">
                <div class="eq-icon">${eq.icon}</div>
                <h4 class="eq-title">${eq.name}</h4>
                <span class="eq-category">${eq.category}</span>
                <span class="eq-stock">In Stock: ${eq.stock}</span>
                <div class="eq-price">₹${eq.price_per_day} / day</div>
                <button class="btn-primary btn-add-cart" data-id="${eq.id}">+ Add to Rental</button>
            </div>
        `).join('');

        document.querySelectorAll('.btn-add-cart').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const eqId = parseInt(e.target.getAttribute('data-id'));
                addToCart(eqId);
            });
        });
    }

    function addToCart(eqId) {
        const eq = equipmentData.find(i => i.id === eqId);
        if (!eq) return;

        const existing = cart.find(i => i.id === eqId);
        if (existing) {
            existing.qty++;
        } else {
            cart.push({ ...eq, qty: 1 });
        }
        renderCart();
        showToast(`Added ${eq.name} to rental summary`, "success");
    }

    function renderCart() {
        const list = document.getElementById('cart-items-list');
        const totalPriceEl = document.getElementById('cart-total-price');

        if (cart.length === 0) {
            list.innerHTML = `<p class="empty-cart-msg">No equipment selected yet. Choose items to rent.</p>`;
            totalPriceEl.textContent = '₹0';
            return;
        }

        let total = 0;
        list.innerHTML = cart.map(item => {
            const itemTotal = item.price_per_day * item.qty;
            total += itemTotal;
            return `
                <div class="cart-item-row">
                    <span>${item.name} (x${item.qty})</span>
                    <strong>₹${itemTotal}</strong>
                </div>
            `;
        }).join('');

        totalPriceEl.textContent = `₹${total}`;
    }

    document.getElementById('btn-checkout-equipment').addEventListener('click', () => {
        if (cart.length === 0) {
            showToast("Your equipment cart is empty!", "info");
            return;
        }

        cart = [];
        renderCart();
        showToast("Equipment Rental order submitted successfully! Pickup details generated.", "success");
    });

    // Teams Render
    function renderTeams() {
        const grid = document.getElementById('teams-grid');
        grid.innerHTML = teamsData.map(t => `
            <div class="team-card">
                <div class="team-header">
                    <h4>${t.name}</h4>
                    <span class="sport-badge">${t.sport}</span>
                </div>
                <p style="font-size:0.85rem; color:var(--text-muted); margin-bottom:0.5rem;">📍 Location: ${t.location}</p>
                <div style="display:flex; justify-content:space-between; align-items:center; margin-top:0.75rem;">
                    <span class="players-badge">Needed: ${t.needed} Players</span>
                    <button class="btn-secondary btn-join-team">Join Match</button>
                </div>
            </div>
        `).join('');

        document.querySelectorAll('.btn-join-team').forEach(btn => {
            btn.addEventListener('click', () => {
                showToast("Request sent to team creator!", "success");
            });
        });
    }

    // Team Modal
    const teamModal = document.getElementById('team-modal');
    document.getElementById('btn-create-team').addEventListener('click', () => teamModal.classList.add('open'));
    document.getElementById('team-modal-close-btn').addEventListener('click', () => teamModal.classList.remove('open'));

    document.getElementById('team-form').addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('team-name-input').value;
        const sport = document.getElementById('team-sport-input').value;
        const needed = document.getElementById('team-players-input').value;
        const location = document.getElementById('team-location-input').value;

        teamsData.unshift({ id: Date.now(), name, sport, creator: currentUser ? currentUser.name : "Demo User", needed, location });
        renderTeams();
        teamModal.classList.remove('open');
        showToast("Match Request created successfully!", "success");
    });

    // Render Bookings & Digital QR Pass
    function renderBookings() {
        const list = document.getElementById('bookings-list');
        if (!list) return;

        let displayBookings = [];
        if (currentUser) {
            displayBookings = bookingsData.filter(b => 
                (b.user_id && b.user_id == currentUser.id) || 
                (b.user_name && b.user_name.toLowerCase() === currentUser.name.toLowerCase()) ||
                (b.user_phone && currentUser.phone && b.user_phone === currentUser.phone)
            );
        }

        if (displayBookings.length === 0) {
            const userNameDisplay = currentUser ? currentUser.name : 'your account';
            list.innerHTML = `
                <div style="background:var(--bg-card); padding:2.5rem; border-radius:16px; border:1px solid var(--border-color); text-align:center; margin-bottom:1rem;">
                    <span style="font-size:3rem; display:block; margin-bottom:1rem;">🎟️</span>
                    <h3 style="margin-bottom:0.5rem;">No Active Bookings Found for ${userNameDisplay}</h3>
                    <p style="color:var(--text-muted); max-width:420px; margin:0 auto 1rem;">Whatever venue or slot you book will appear here exclusively for your account, complete with your digital QR entry pass.</p>
                </div>
            `;
            const qrCanvas = document.getElementById('qrcode');
            if (qrCanvas) qrCanvas.innerHTML = `<div class="qr-placeholder">Book a slot to generate your QR entry pass</div>`;
            return;
        }

        list.innerHTML = displayBookings.map(b => `
            <div class="booking-card">
                <div style="flex:1;">
                    <div style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.4rem;">
                        <span style="background:rgba(37,99,235,0.1); color:var(--primary-color); font-size:0.75rem; font-weight:700; padding:0.25rem 0.6rem; border-radius:20px;">
                            👤 ${b.user_name || (currentUser ? currentUser.name : 'Signed-up User')}
                        </span>
                        <span style="font-size:0.75rem; color:var(--text-muted);">ID: #${b.id}</span>
                    </div>
                    <h4 style="font-family:var(--font-heading); font-size:1.1rem; color:var(--text-main); margin-bottom:0.3rem;">${b.ground_name}</h4>
                    <p style="font-size:0.85rem; color:var(--text-muted); margin:0.2rem 0;">📅 Date: ${b.date} | ⏰ ${b.slot_time}</p>
                    <div style="margin-top:0.5rem;">
                        <span class="badge-paid">₹${b.amount} - ${b.status || 'Confirmed'} (${b.payment_method || 'Paid'})</span>
                    </div>
                </div>
                <button class="btn-primary btn-view-qr" data-id="${b.id}" style="align-self:center;">View QR Pass</button>
            </div>
        `).join('');

        document.querySelectorAll('.btn-view-qr').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                showQrPass(id);
            });
        });

        if (displayBookings.length > 0) {
            showQrPass(displayBookings[0].id);
        }
    }

    function showQrPass(bookingId) {
        const b = bookingsData.find(x => x.id === bookingId);
        if (!b) return;

        activeQrBooking = b;
        document.getElementById('qr-ground-name').textContent = b.ground_name;
        document.getElementById('qr-booking-id').textContent = `#${b.id}`;
        document.getElementById('qr-booking-time').textContent = b.slot_time;

        const qrContainer = document.getElementById('qrcode');
        qrContainer.innerHTML = '';

        if (typeof QRCode !== 'undefined') {
            new QRCode(qrContainer, {
                text: `SMARTPLAYGROUND-ENTRY-TICKET:${b.id}:${b.date}:${b.ground_name}`,
                width: 160,
                height: 160
            });
        } else {
            qrContainer.innerHTML = `<img src="https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=${b.id}" alt="QR Ticket">`;
        }
    }

    // Render Admin Dashboard
    function renderAdminDashboard() {
        document.getElementById('metric-total-grounds').textContent = playgroundsData.length;
        document.getElementById('metric-total-bookings').textContent = bookingsData.length + 12;
        
        let totalRev = bookingsData.reduce((acc, b) => acc + b.amount, 18450);
        document.getElementById('metric-revenue').textContent = `₹${totalRev.toLocaleString()}`;

        const tbody = document.querySelector('#table-admin-slots tbody');
        const adminRows = [
            { ground: "Marina Turf Arena", slot: "06:00 PM - 07:00 PM", status: "🔒 LOCKED", timer: "08:42 mins", action: "Release Lock" },
            { ground: "Anna Nagar Smash Badminton Court", slot: "07:00 AM - 08:00 AM", status: "✅ BOOKED", timer: "N/A", action: "View Details" },
            { ground: "Adyar Pro Basketball Zone", slot: "08:00 PM - 09:00 PM", status: "🔒 LOCKED", timer: "04:15 mins", action: "Release Lock" }
        ];

        tbody.innerHTML = adminRows.map(r => `
            <tr>
                <td><strong>${r.ground}</strong></td>
                <td>${r.slot}</td>
                <td><span style="color:${r.status.includes('LOCKED') ? '#f43f5e' : '#10b981'}; font-weight:700;">${r.status}</span></td>
                <td>${r.timer}</td>
                <td><button class="btn-secondary" style="padding:0.25rem 0.6rem; font-size:0.75rem;">${r.action}</button></td>
            </tr>
        `).join('');
    }

    // Helper Toast Notification
    function showToast(message, type = "info") {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 4000);
    }

    // Initialize
    loadData();
});

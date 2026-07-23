class SignupPage {
    get communityBadge() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/tvBadge'; }
    get nameInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etFullName'; }
    get phoneInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etPhone'; }
    get emailInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etEmail'; }
    get locationInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etLocation'; }
    get passwordInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etPassword'; }
    get signUpSubmitBtn() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/btnSignUpSubmit'; }
    get signInLink() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/tvSignInLink'; }

    async registerUser(driver, name, phone, email, location, password) {
        await driver.$(this.nameInput).setValue(name);
        await driver.$(this.phoneInput).setValue(phone);
        await driver.$(this.emailInput).setValue(email);
        await driver.$(this.locationInput).setValue(location);
        await driver.$(this.passwordInput).setValue(password);
        await driver.$(this.signUpSubmitBtn).click();
    }
}

module.exports = new SignupPage();

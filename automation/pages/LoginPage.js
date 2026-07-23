class LoginPage {
    get loginTitle() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/tvLoginTitle'; }
    get phoneInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etPhone'; }
    get passwordInput() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/etPassword'; }
    get loginButton() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/btnLogin'; }
    get signUpLink() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/tvSignUpLink'; }
    get googleSignInBtn() { return 'id:com.example.smartplaygroundbookingequipmentrentalapp:id/btnGoogleSignIn'; }

    async performLogin(driver, phone, password) {
        await driver.$(this.phoneInput).setValue(phone);
        await driver.$(this.passwordInput).setValue(password);
        await driver.$(this.loginButton).click();
    }
}

module.exports = new LoginPage();

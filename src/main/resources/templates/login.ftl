<#assign pageTitle = "Login">
<#assign pageContent>
<div class="loginContainer">
    <h2>Login</h2>
    <form class="loginForm" method="POST" action="/${webpref}/login">
        <div class="formGroup username">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="formGroup password">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <button type="submit" class="formSubmit">Login</button>
        </div>
    </form>
</div>
</#assign>
<#include "base.ftl">
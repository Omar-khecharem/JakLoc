/*
 * JakLoc authentication interactions
 * 1. Show/hide password buttons.
 * 2. Live "passwords match" hint (server-side validation remains authoritative).
 */
(function () {
    "use strict";

    var toggleButtons = document.querySelectorAll("[data-toggle-password]");

    toggleButtons.forEach(function (button) {
        var input = document.getElementById(button.getAttribute("data-toggle-password"));
        if (!input) {
            return;
        }

        button.addEventListener("click", function () {
            var isHidden = input.getAttribute("type") === "password";
            input.setAttribute("type", isHidden ? "text" : "password");
            button.setAttribute("aria-pressed", isHidden ? "true" : "false");
            button.setAttribute("aria-label", isHidden ? "Hide password" : "Show password");
        });
    });

    var form = document.querySelector("[data-register-form]");
    if (!form) {
        return;
    }

    var password = form.querySelector("#password");
    var confirmPassword = form.querySelector("#confirmPassword");
    var matchHint = form.querySelector("[data-password-match]");

    if (!password || !confirmPassword || !matchHint) {
        return;
    }

    function updateMatchHint() {
        if (confirmPassword.value.length === 0) {
            matchHint.textContent = "";
            matchHint.classList.remove("field-error");
            confirmPassword.setCustomValidity("");
            return;
        }

        var matches = password.value === confirmPassword.value;
        matchHint.textContent = matches ? "Passwords match." : "Passwords do not match.";
        matchHint.classList.toggle("field-error", !matches);
        confirmPassword.setCustomValidity(matches ? "" : "Passwords do not match.");
    }

    password.addEventListener("input", updateMatchHint);
    confirmPassword.addEventListener("input", updateMatchHint);
}());

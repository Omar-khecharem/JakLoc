(function () {
    "use strict";

    var form = document.querySelector("[data-vehicle-filters]");
    if (!form) {
        return;
    }

    var submitButton = form.querySelector("[data-submit-button]");
    var autoSubmitControls = form.querySelectorAll("[data-auto-submit]");

    function submitForm() {
        if (typeof form.requestSubmit === "function") {
            form.requestSubmit();
        } else {
            form.submit();
        }
    }

    Array.prototype.forEach.call(autoSubmitControls, function (control) {
        control.addEventListener("change", submitForm);
    });

    form.addEventListener("submit", function () {
        if (!submitButton) {
            return;
        }
        submitButton.disabled = true;
        submitButton.textContent = "Searching...";
    });

    window.addEventListener("pageshow", function () {
        if (!submitButton) {
            return;
        }
        submitButton.disabled = false;
        submitButton.textContent = "Search";
    });
})();

// Filter pills show/hide request cards by data-bucket. Visual only; remove freely.
(function () {
  "use strict";
  var pills = document.querySelectorAll(".ac-pill[data-filter]");
  var cards = document.querySelectorAll(".ac-card[data-bucket]");
  pills.forEach(function (pill) {
    pill.addEventListener("click", function () {
      pills.forEach(function (p) { p.classList.remove("is-active"); });
      pill.classList.add("is-active");
      var f = pill.getAttribute("data-filter");
      cards.forEach(function (c) {
        var show = f === "all" || c.getAttribute("data-bucket") === f;
        c.style.display = show ? "" : "none";
      });
    });
  });
}());
document.addEventListener("DOMContentLoaded", () => {
    const popup = document.querySelector("[data-site-popup]");
    if (!popup) return;

    const today = new Date();
    const todayKey = `${today.getFullYear()}-${today.getMonth() + 1}-${today.getDate()}`;
    if (localStorage.getItem("smallwaxingPopupHiddenDate") === todayKey
            || sessionStorage.getItem("smallwaxingPopupClosed") === "true") {
        return;
    }

    const track = popup.querySelector(".site-popup__track");
    const dots = popup.querySelectorAll("[data-popup-index]");

    const showSlide = (index) => {
        track.style.transform = `translateX(-${index * 100}%)`;
        dots.forEach((dot, dotIndex) => dot.classList.toggle("is-active", dotIndex === index));
    };

    dots.forEach((dot) => {
        dot.addEventListener("click", () => showSlide(Number(dot.dataset.popupIndex)));
    });

    popup.querySelector("[data-popup-close]").addEventListener("click", () => {
        sessionStorage.setItem("smallwaxingPopupClosed", "true");
        popup.hidden = true;
    });

    popup.querySelector("[data-popup-today]").addEventListener("click", () => {
        localStorage.setItem("smallwaxingPopupHiddenDate", todayKey);
        popup.hidden = true;
    });

    popup.addEventListener("click", (event) => {
        if (event.target === popup) {
            sessionStorage.setItem("smallwaxingPopupClosed", "true");
            popup.hidden = true;
        }
    });

    popup.hidden = false;
});

document.addEventListener("DOMContentLoaded", () => {
    const gallery = document.querySelector(".smallwaxing-gallery");

    if (!gallery) {
        return;
    }

    const track = gallery.querySelector(".smallwaxing-gallery__track");
    const slides = gallery.querySelectorAll(".smallwaxing-gallery__slide");
    const previousButton = gallery.querySelector(".smallwaxing-gallery__button--prev");
    const nextButton = gallery.querySelector(".smallwaxing-gallery__button--next");
    const current = gallery.querySelector("[data-gallery-current]");
    let activeIndex = 0;

    const showSlide = (index) => {
        activeIndex = (index + slides.length) % slides.length;
        track.style.transform = `translateX(-${activeIndex * 100}%)`;
        current.textContent = String(activeIndex + 1);
    };

    previousButton.addEventListener("click", () => showSlide(activeIndex - 1));
    nextButton.addEventListener("click", () => showSlide(activeIndex + 1));

    gallery.addEventListener("keydown", (event) => {
        if (event.key === "ArrowLeft") {
            showSlide(activeIndex - 1);
        }

        if (event.key === "ArrowRight") {
            showSlide(activeIndex + 1);
        }
    });
});

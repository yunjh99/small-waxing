document.addEventListener("DOMContentLoaded", function () {
    const items = document.querySelectorAll(".faq-item");

    items.forEach((item) => {
        const button = item.querySelector(".faq-item__question");

        button.addEventListener("click", function () {
            item.classList.toggle("is-open");
        });
    });
});
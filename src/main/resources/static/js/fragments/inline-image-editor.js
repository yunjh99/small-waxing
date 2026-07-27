document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-inline-image-dialog]").forEach((button) => {
        button.addEventListener("click", (event) => {
            event.preventDefault();
            event.stopPropagation();
            document.getElementById(button.dataset.inlineImageDialog)?.showModal();
        });
    });

    document.querySelectorAll("[data-inline-image-close]").forEach((button) => {
        button.addEventListener("click", () => button.closest("dialog")?.close());
    });

    document.querySelectorAll(".inline-image-dialog").forEach((dialog) => {
        dialog.addEventListener("click", (event) => {
            if (event.target === dialog) dialog.close();
        });
    });
});

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-edit-dialog]").forEach((button) => {
        button.addEventListener("click", () => {
            document.getElementById(button.dataset.editDialog)?.showModal();
        });
    });

    document.querySelectorAll("[data-close-dialog]").forEach((button) => {
        button.addEventListener("click", () => button.closest("dialog")?.close());
    });

    document.querySelectorAll(".service-edit-dialog").forEach((dialog) => {
        dialog.addEventListener("click", (event) => {
            if (event.target === dialog) dialog.close();
        });
    });
});

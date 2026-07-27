document.addEventListener("DOMContentLoaded", () => {
    const parentButtons = document.querySelectorAll("[data-image-parent]");
    const submenuGroups = document.querySelectorAll("[data-image-submenus]");
    const menuButtons = document.querySelectorAll("[data-image-menu]");
    const imageCards = document.querySelectorAll("[data-image-category]");

    const showImages = (selectedMenu) => {
        menuButtons.forEach((item) => {
            item.classList.toggle("is-active", item.dataset.imageMenu === selectedMenu);
        });

        imageCards.forEach((card) => {
            card.hidden = card.dataset.imageCategory !== selectedMenu;
        });
    };

    parentButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const selectedParent = button.dataset.imageParent;

            parentButtons.forEach((item) => {
                item.classList.toggle("is-active", item === button);
            });

            submenuGroups.forEach((group) => {
                group.hidden = group.dataset.imageSubmenus !== selectedParent;
            });

            const activeGroup = document.querySelector(`[data-image-submenus="${selectedParent}"]`);
            const firstMenu = activeGroup?.querySelector("[data-image-menu]");
            if (firstMenu) {
                showImages(firstMenu.dataset.imageMenu);
            }
        });
    });

    menuButtons.forEach((button) => {
        button.addEventListener("click", () => showImages(button.dataset.imageMenu));
    });
});

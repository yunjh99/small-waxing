document.addEventListener("DOMContentLoaded", () => {
    const orderForms = document.querySelectorAll("[data-order-form]");

    orderForms.forEach((form) => {
        const category = form.dataset.orderForm;
        const list = document.querySelector(`[data-pricing-list="${category}"]`);
        if (!list) return;

        let draggedItem = null;

        list.querySelectorAll(".pricing-drag-handle").forEach((handle) => {
            handle.addEventListener("pointerdown", (event) => {
                draggedItem = handle.closest(".pricing-item");
                draggedItem.classList.add("is-dragging");
                handle.setPointerCapture(event.pointerId);
                event.preventDefault();
            });

            handle.addEventListener("pointermove", (event) => {
                if (!draggedItem) return;

                const target = document.elementFromPoint(event.clientX, event.clientY)
                        ?.closest(".pricing-item");
                if (!target || target === draggedItem || target.parentElement !== list) return;

                const targetRect = target.getBoundingClientRect();
                const insertAfter = event.clientY > targetRect.top + targetRect.height / 2;
                list.insertBefore(draggedItem, insertAfter ? target.nextSibling : target);
            });

            const finishDrag = () => {
                if (!draggedItem) return;
                draggedItem.classList.remove("is-dragging");
                draggedItem = null;
                form.classList.add("has-changes");
            };

            handle.addEventListener("pointerup", finishDrag);
            handle.addEventListener("pointercancel", finishDrag);
        });

        form.addEventListener("submit", () => {
            form.querySelectorAll('input[name="pricingIds"]').forEach((input) => input.remove());
            list.querySelectorAll("[data-pricing-id]").forEach((item) => {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "pricingIds";
                input.value = item.dataset.pricingId;
                form.appendChild(input);
            });
        });
    });
});

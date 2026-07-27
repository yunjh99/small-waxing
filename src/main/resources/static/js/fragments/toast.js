function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');

    if (!container) {
        console.warn('toast-container가 없습니다.');
        return;
    }

    const toast = document.createElement('div');
    toast.className = `toast toast--${type}`;
    toast.textContent = message;

    container.appendChild(toast);

    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    setTimeout(() => {
        toast.classList.remove('show');
        toast.classList.add('hide');

        toast.addEventListener('transitionend', () => {
            toast.remove();
        }, { once: true });
    }, 2500);
}
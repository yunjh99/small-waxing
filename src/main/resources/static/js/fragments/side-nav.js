(() => {
    const sideNav = document.querySelector('.side-nav');
    const toggle = sideNav?.querySelector('.side-nav__toggle');
    const menu = sideNav?.querySelector('.side-nav__grid');

    if (!sideNav || !toggle || !menu) return;
    const mobileQuery = window.matchMedia('(max-width: 900px)');

    const closeMenu = () => {
        sideNav.classList.remove('is-open');
        if (mobileQuery.matches) menu.setAttribute('inert', '');
        toggle.setAttribute('aria-expanded', 'false');
        toggle.setAttribute('aria-label', '빠른 메뉴 열기');
    };

    const syncForViewport = () => {
        if (mobileQuery.matches) {
            closeMenu();
        } else {
            menu.removeAttribute('inert');
        }
    };

    syncForViewport();
    mobileQuery.addEventListener('change', syncForViewport);

    toggle.addEventListener('click', () => {
        const isOpen = sideNav.classList.toggle('is-open');
        if (isOpen) {
            menu.removeAttribute('inert');
        } else {
            menu.setAttribute('inert', '');
        }
        toggle.setAttribute('aria-expanded', String(isOpen));
        toggle.setAttribute('aria-label', isOpen ? '빠른 메뉴 닫기' : '빠른 메뉴 열기');
    });

    menu.addEventListener('click', event => {
        if (event.target.closest('a')) closeMenu();
    });

    document.addEventListener('keydown', event => {
        if (event.key === 'Escape') closeMenu();
    });

    document.addEventListener('click', event => {
        if (window.innerWidth <= 900 && !sideNav.contains(event.target)) closeMenu();
    });
})();

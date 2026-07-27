(() => {
    const topNav = document.querySelector('.top-nav');

    const updateNavOnScroll = () => {
        topNav.classList.toggle('scrolled', window.scrollY > 0);
    };

    window.addEventListener('scroll', updateNavOnScroll);
    updateNavOnScroll();
})();

// overlay -----------------------------------------------------------------------------
function toggleMenu(button) {
    const overlay = document.querySelector('.overlay');
    const isOpened = button.classList.toggle('opened');
    button.setAttribute('aria-expanded', isOpened);

    // overlay의 상태에 따라 클래스 추가 또는 제거
    if (isOpened) {
        overlay.classList.add('open'); // overlay 보이게 설정
        document.body.classList.add('menu-open');
    } else {
        overlay.classList.remove('open'); // overlay 숨김
        document.body.classList.remove('menu-open');
        overlay.querySelectorAll('.submenu-open').forEach(item => item.classList.remove('submenu-open'));
        overlay.querySelectorAll('.overlay__submenu-toggle')
            .forEach(toggle => toggle.setAttribute('aria-expanded', 'false'));
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const button = document.querySelector('.hamburger-menu');
    const overlay = document.querySelector('.overlay');
    const submenuToggles = overlay?.querySelectorAll('.overlay__submenu-toggle') ?? [];

    const closeSubmenus = except => {
        submenuToggles.forEach(toggle => {
            if (toggle === except) return;
            toggle.closest('li')?.classList.remove('submenu-open');
            toggle.setAttribute('aria-expanded', 'false');
        });
    };

    const toggleSubmenu = toggle => {
        const parent = toggle.closest('li');
        const willOpen = !parent.classList.contains('submenu-open');
        closeSubmenus(toggle);
        parent.classList.toggle('submenu-open', willOpen);
        toggle.setAttribute('aria-expanded', String(willOpen));
    };

    button?.setAttribute('aria-expanded', 'false');

    submenuToggles.forEach(toggle => {
        toggle.addEventListener('click', event => {
            event.preventDefault();
            event.stopPropagation();
            toggleSubmenu(toggle);
        });

        const parentLink = toggle.previousElementSibling;
        parentLink?.addEventListener('click', event => {
            if (!window.matchMedia('(max-width: 900px)').matches) return;
            event.preventDefault();
            event.stopPropagation();
            toggleSubmenu(toggle);
        });
    });

    overlay?.addEventListener('click', event => {
        if (event.target.closest('a') && button?.classList.contains('opened')) {
            closeSubmenus();
            toggleMenu(button);
        }
    });

    document.addEventListener('keydown', event => {
        if (event.key === 'Escape' && button?.classList.contains('opened')) {
            closeSubmenus();
            toggleMenu(button);
            button.focus();
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const stats = document.querySelector(".smallwaxing-stats");
    const counters = document.querySelectorAll("[data-counter]");
    const countries = document.querySelector("[data-counter-countries]");
    const features = document.querySelectorAll(".smallwaxing-feature");
    const experience = document.querySelector(".smallwaxing-experience");
    const experienceHeader = document.querySelector(".smallwaxing-experience__header");

    if (experience && experienceHeader) {
        let ticking = false;

        const updateExperienceLine = () => {
            const rect = experience.getBoundingClientRect();
            const viewportHeight = window.innerHeight;
            const start = viewportHeight * 0.9;
            const end = viewportHeight * 0.52;
            const progress = Math.min(Math.max((start - rect.top) / (start - end), 0), 1);

            experience.style.setProperty("--line-progress", progress);
            experienceHeader.classList.toggle("is-visible", progress >= 0.82);
            ticking = false;
        };

        const requestExperienceUpdate = () => {
            if (ticking) {
                return;
            }

            ticking = true;
            requestAnimationFrame(updateExperienceLine);
        };

        updateExperienceLine();
        window.addEventListener("scroll", requestExperienceUpdate, { passive: true });
        window.addEventListener("resize", requestExperienceUpdate);
    }

    if (features.length > 0) {
        const featureObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach((entry) => {
                if (!entry.isIntersecting) {
                    return;
                }

                entry.target.classList.add("is-visible");
                observer.unobserve(entry.target);
            });
        }, {
            threshold: 0.3
        });

        features.forEach((feature) => featureObserver.observe(feature));
    }

    if (!stats || counters.length === 0) {
        return;
    }

    const formatNumber = (value) => Math.round(value).toLocaleString("ko-KR");

    const runCounters = () => {
        const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        const duration = reduceMotion ? 0 : 1800;
        const startTime = performance.now();

        const update = (now) => {
            const progress = duration === 0 ? 1 : Math.min((now - startTime) / duration, 1);
            const easedProgress = 1 - Math.pow(1 - progress, 3);

            counters.forEach((counter) => {
                const target = Number(counter.dataset.counter);
                counter.textContent = formatNumber(target * easedProgress);
            });

            if (progress < 1) {
                requestAnimationFrame(update);
            } else if (countries) {
                countries.classList.add("is-visible");
            }
        };

        requestAnimationFrame(update);
    };

    const observer = new IntersectionObserver((entries) => {
        if (!entries[0].isIntersecting) {
            return;
        }

        runCounters();
        observer.disconnect();
    }, {
        threshold: 0.35
    });

    observer.observe(stats);
});

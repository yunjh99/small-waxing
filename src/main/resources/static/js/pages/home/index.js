// 화면에 들어온 제목과 이미지가 아래에서 위로 나타나는 효과
document.addEventListener('DOMContentLoaded', () => {
    const elementsToShow = document.querySelectorAll('.image-item, .middle-middle h1');

    const observer = new IntersectionObserver((entries, currentObserver) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('show');
                currentObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    elementsToShow.forEach(element => observer.observe(element));
});

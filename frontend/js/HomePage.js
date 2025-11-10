function redirectToCredits() {
    window.location.href = 'creditos.html';
}

function redirectToLogin() {
    window.location.href = 'login.html';
}

function showComingSoon(feature) {
    const message = `La funcionalidad "${feature}" estará disponible próximamente.\n\n¿Te interesa? Regístrate ahora para ser el primero en conocer cuando esté lista.`;

    if (confirm(message)) {
        window.location.href = 'registro.html';
    }
}

window.redirectToCredits = redirectToCredits;
window.redirectToLogin = redirectToLogin;
window.showComingSoon = showComingSoon;

document.addEventListener('DOMContentLoaded', () => {
    const prevButton = document.querySelector('.carousel-control.prev');
    const nextButton = document.querySelector('.carousel-control.next');
    const slides = document.querySelectorAll('.slide-item');
    const notifs = document.querySelectorAll('.floating-notification-block');
    let currentSlideIndex = 1;

    function updateCarousel(newIndex) {
        currentSlideIndex = newIndex;
        slides.forEach(slide => {
            slide.classList.remove('active');
            if (parseInt(slide.getAttribute('data-slide')) === currentSlideIndex) {
                slide.classList.add('active');
            }
        });
        notifs.forEach(notif => {
            notif.classList.remove('active-notif');
            if (parseInt(notif.getAttribute('data-slide-target')) === currentSlideIndex) {
                notif.classList.add('active-notif');
            }
        });
    }

    prevButton.addEventListener('click', () => {
        let newIndex = currentSlideIndex - 1;
        if (newIndex < 1) {
            newIndex = slides.length;
        }
        updateCarousel(newIndex);
    });

    nextButton.addEventListener('click', () => {
        let newIndex = currentSlideIndex + 1;
        if (newIndex > slides.length) {
            newIndex = 1;
        }
        updateCarousel(newIndex);
    });

    notifs.forEach(notif => {
        notif.addEventListener('click', (e) => {
            e.preventDefault();
            const targetIndex = parseInt(notif.getAttribute('data-slide-target'));
            updateCarousel(targetIndex);
        });
    });

    updateCarousel(currentSlideIndex);
    initializeQuickAccess();
    initializeDropdowns();

    const helpButton = document.getElementById('helpButton');
    const chatPanel = document.getElementById('chatPanel');
    const closeChat = document.getElementById('closeChat');
    const minimizeChat = document.getElementById('minimizeChat');

    if (helpButton && chatPanel) {
        helpButton.addEventListener('click', () => {
            chatPanel.classList.toggle('show');
            chatPanel.classList.remove('minimized');
        });
    }
    if (closeChat && chatPanel) {
        closeChat.addEventListener('click', () => chatPanel.classList.remove('show'));
    }
    if (minimizeChat && chatPanel) {
        minimizeChat.addEventListener('click', () => {
            chatPanel.classList.toggle('minimized');
        });
    }
});

function initializeQuickAccess() {
    const quickAccessItems = document.querySelectorAll('.quick-access-item');
    quickAccessItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-3px)';
            this.style.transition = 'transform 0.3s ease';
        });
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
        const text = item.querySelector('span').textContent;
        if (text.includes('Pagos')) {
            item.addEventListener('click', function() {
                alert('Funcionalidad de pagos próximamente. Por favor, accede a tu cuenta para pagar facturas.');
            });
        } else if (text.includes('Centro de Ayuda')) {
            item.addEventListener('click', function() {
                alert('¿Necesitas ayuda? Contáctanos:\n📞 01 8000 123 456\n📧 ayuda@bancoagilcol.com');
            });
        } else if (!text.includes('Trámites digitales')) {
            item.addEventListener('click', function() {
                showComingSoon(text);
            });
        }
    });
}

function initializeDropdowns() {
    const dropdowns = document.querySelectorAll('.virtual-branch-dropdown');
    dropdowns.forEach(dropdown => {
        const button = dropdown.querySelector('.dropdown-button');
        const content = dropdown.querySelector('.dropdown-content');
        if (button && content) {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                dropdowns.forEach(otherDropdown => {
                    if (otherDropdown !== dropdown) {
                        const otherContent = otherDropdown.querySelector('.dropdown-content');
                        if (otherContent) {
                            otherContent.style.display = 'none';
                        }
                    }
                });
                const isVisible = content.style.display === 'block';
                content.style.display = isVisible ? 'none' : 'block';
            });
        }
    });
    document.addEventListener('click', function() {
        dropdowns.forEach(dropdown => {
            const content = dropdown.querySelector('.dropdown-content');
            if (content) {
                content.style.display = 'none';
            }
        });
    });
}

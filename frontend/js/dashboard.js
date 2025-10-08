// Dashboard de Bancolombia - JavaScript
// Funcionalidad interactiva y animaciones

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar todas las funcionalidades
    initializeHeader();
    initializeHeroAnimations();
    initializeScrollAnimations();
    initializeProductTabs();
    initializeFormHandlers();
    initializeLoadingStates();
    initializeResponsiveMenu();
    initializeSearchFunctionality();
    initializeServiceButtons();
    initializeSmoothScroll();
    
    // Ocultar overlay de carga inicial
    hideLoadingOverlay();
});

// ====================================
// FUNCIONES DE NAVEGACIÓN Y HEADER
// ====================================

function initializeHeader() {
    const header = document.querySelector('.header');
    const headerContainer = document.querySelector('.header-container');
    
    // Efecto de scroll en el header
    let lastScrollTop = 0;
    
    window.addEventListener('scroll', function() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        
        // Cambiar opacidad del header basado en el scroll
        if (scrollTop > 100) {
            header.style.backgroundColor = 'rgba(0, 82, 204, 0.95)';
            header.style.backdropFilter = 'blur(10px)';
        } else {
            header.style.backgroundColor = '';
            header.style.backdropFilter = '';
        }
        
        lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
    });
    
    // Hover effects para los nav items
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-1px)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
}

function initializeResponsiveMenu() {
    const menuToggle = document.getElementById('menuToggle');
    const navMenu = document.querySelector('.nav-menu');
    
    if (menuToggle && navMenu) {
        menuToggle.addEventListener('click', function() {
            navMenu.classList.toggle('active');
            
            // Animación del botón hamburguesa
            const spans = menuToggle.querySelectorAll('span');
            spans.forEach((span, index) => {
                if (navMenu.classList.contains('active')) {
                    if (index === 0) span.style.transform = 'rotate(45deg) translate(5px, 5px)';
                    if (index === 1) span.style.opacity = '0';
                    if (index === 2) span.style.transform = 'rotate(-45deg) translate(7px, -6px)';
                } else {
                    span.style.transform = '';
                    span.style.opacity = '';
                }
            });
        });
        
        // Cerrar menú al hacer click fuera
        document.addEventListener('click', function(e) {
            if (!menuToggle.contains(e.target) && !navMenu.contains(e.target)) {
                navMenu.classList.remove('active');
                const spans = menuToggle.querySelectorAll('span');
                spans.forEach(span => {
                    span.style.transform = '';
                    span.style.opacity = '';
                });
            }
        });
    }
}

// ====================================
// ANIMACIONES HERO Y SCROLL
// ====================================

function initializeHeroAnimations() {
    const heroContent = document.querySelector('.hero-content');
    const heroImage = document.querySelector('.hero-image');
    
    // Animación de entrada del hero
    setTimeout(() => {
        if (heroContent) {
            heroContent.style.opacity = '1';
            heroContent.style.transform = 'translateX(0)';
        }
    }, 300);
    
    setTimeout(() => {
        if (heroImage) {
            heroImage.style.opacity = '1';
            heroImage.style.transform = 'translateX(0)';
        }
    }, 600);
    
    // Efecto parallax suave en el hero
    window.addEventListener('scroll', function() {
        const scrolled = window.pageYOffset;
        const parallax = document.querySelector('.hero-section');
        if (parallax) {
            parallax.style.transform = `translateY(${scrolled * 0.2}px)`;
        }
    });
}

function initializeScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate');
                
                // Animación escalonada para tarjetas
                const cards = entry.target.querySelectorAll('.access-card, .product-card, .service-card');
                cards.forEach((card, index) => {
                    setTimeout(() => {
                        card.style.opacity = '1';
                        card.style.transform = 'translateY(0)';
                    }, index * 100);
                });
            }
        });
    }, observerOptions);
    
    // Observar secciones para animaciones
    const sections = document.querySelectorAll('.quick-access, .products-section, .services-section');
    sections.forEach(section => observer.observe(section));
    
    // Observar tarjetas individuales
    const cards = document.querySelectorAll('.access-card, .product-card, .service-card');
    cards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(card);
    });
}

// ====================================
// FUNCIONALIDAD DE PESTAÑAS DE PRODUCTOS
// ====================================

function initializeProductTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetTab = this.dataset.tab;
            
            // Remover clase active de todos los botones y contenidos
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            
            // Agregar clase active al botón clickeado
            this.classList.add('active');
            
            // Mostrar el contenido correspondiente con animación
            const targetContent = document.getElementById(targetTab);
            if (targetContent) {
                setTimeout(() => {
                    targetContent.classList.add('active');
                }, 50);
            }
            
            // Efecto de loading temporal
            showTabLoading(targetContent);
        });
    });
}

function showTabLoading(targetContent) {
    if (targetContent) {
        targetContent.style.opacity = '0.3';
        setTimeout(() => {
            targetContent.style.opacity = '1';
        }, 200);
    }
}

// ====================================
// MANEJADORES DE FORMULARIOS
// ====================================

function initializeFormHandlers() {
    // Formulario de newsletter
    const newsletterForm = document.getElementById('newsletterForm');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleNewsletterSubmit(this);
        });
    }
    
    // Formulario de búsqueda
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(handleSearch, 300));
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                handleSearchSubmit(this.value);
            }
        });
    }
}

function handleNewsletterSubmit(form) {
    const email = form.querySelector('input[type="email"]').value;
    const button = form.querySelector('button');
    
    if (!validateEmail(email)) {
        showNotification('Por favor ingresa un email válido', 'error');
        return;
    }
    
    // Mostrar loading
    button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Enviando...';
    button.disabled = true;
    
    // Simular envío
    setTimeout(() => {
        showNotification('¡Te has suscrito exitosamente!', 'success');
        form.reset();
        button.innerHTML = 'Suscribirse';
        button.disabled = false;
    }, 2000);
}

function handleSearch(e) {
    const query = e.target.value;
    if (query.length > 2) {
        // Simular búsqueda en tiempo real
        console.log('Buscando:', query);
        // Aquí se implementaría la lógica de búsqueda
    }
}

function handleSearchSubmit(query) {
    if (query.trim()) {
        showLoadingOverlay();
        setTimeout(() => {
            hideLoadingOverlay();
            showNotification(`Resultados para: "${query}"`, 'info');
        }, 1500);
    }
}

// ====================================
// FUNCIONALIDAD DE BOTONES DE SERVICIO
// ====================================

function initializeServiceButtons() {
    // Botones de acceso rápido
    const accessCards = document.querySelectorAll('.access-card .btn-card');
    accessCards.forEach(button => {
        button.addEventListener('click', function() {
            const cardTitle = this.closest('.access-card').querySelector('h3').textContent;
            handleServiceRequest(cardTitle, 'access');
        });
    });
    
    // Botones de productos
    const productButtons = document.querySelectorAll('.btn-product');
    productButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productTitle = this.closest('.product-card').querySelector('h3').textContent;
            handleServiceRequest(productTitle, 'product');
        });
    });
    
    // Botones de servicios
    const serviceButtons = document.querySelectorAll('.btn-service');
    serviceButtons.forEach(button => {
        button.addEventListener('click', function() {
            const serviceTitle = this.closest('.service-card').querySelector('h3').textContent;
            handleServiceRequest(serviceTitle, 'service');
        });
    });
    
    // Botón de login
    const loginButton = document.getElementById('loginBtn');
    if (loginButton) {
        loginButton.addEventListener('click', function() {
            handleLogin();
        });
    }
}

function handleServiceRequest(title, type) {
    showLoadingOverlay();
    
    setTimeout(() => {
        hideLoadingOverlay();
        
        switch(type) {
            case 'access':
                showNotification(`Redirigiendo a ${title}...`, 'info');
                break;
            case 'product':
                showNotification(`Iniciando solicitud de ${title}...`, 'info');
                break;
            case 'service':
                showNotification(`Accediendo a ${title}...`, 'info');
                break;
        }
    }, 1000);
}

function handleLogin() {
    showLoadingOverlay();
    
    setTimeout(() => {
        hideLoadingOverlay();
        showNotification('Redirigiendo a Sucursal Virtual...', 'info');
    }, 1500);
}

// ====================================
// SCROLL SUAVE Y NAVEGACIÓN
// ====================================

function initializeSmoothScroll() {
    // Scroll suave para enlaces internos
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Botón de scroll to top (se puede agregar al HTML si se desea)
    window.addEventListener('scroll', function() {
        const scrolled = window.pageYOffset;
        const scrollToTop = document.querySelector('.scroll-to-top');
        
        if (scrollToTop) {
            if (scrolled > 500) {
                scrollToTop.style.display = 'block';
                scrollToTop.style.opacity = '1';
            } else {
                scrollToTop.style.opacity = '0';
                setTimeout(() => {
                    scrollToTop.style.display = 'none';
                }, 300);
            }
        }
    });
}

// ====================================
// FUNCIONALIDAD DE BÚSQUEDA
// ====================================

function initializeSearchFunctionality() {
    const searchBox = document.querySelector('.search-box');
    const searchInput = document.getElementById('searchInput');
    
    if (searchBox && searchInput) {
        // Efecto de focus en la caja de búsqueda
        searchInput.addEventListener('focus', function() {
            searchBox.style.boxShadow = '0 0 0 2px rgba(255, 204, 0, 0.3)';
            searchBox.style.transform = 'scale(1.02)';
        });
        
        searchInput.addEventListener('blur', function() {
            searchBox.style.boxShadow = '';
            searchBox.style.transform = '';
        });
    }
}

// ====================================
// ESTADOS DE CARGA
// ====================================

function initializeLoadingStates() {
    // Mostrar loading al cargar la página
    showLoadingOverlay();
    
    // Simular carga de datos
    setTimeout(() => {
        hideLoadingOverlay();
    }, 1000);
}

function showLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.classList.add('show');
    }
}

function hideLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.classList.remove('show');
    }
}

// ====================================
// SISTEMA DE NOTIFICACIONES
// ====================================

function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${getNotificationIcon(type)}"></i>
            <span>${message}</span>
            <button class="notification-close">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    // Estilos inline para la notificación
    Object.assign(notification.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '15px 20px',
        backgroundColor: getNotificationColor(type),
        color: 'white',
        borderRadius: '8px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
        zIndex: '10000',
        transform: 'translateX(100%)',
        transition: 'transform 0.3s ease',
        maxWidth: '400px'
    });
    
    // Agregar al DOM
    document.body.appendChild(notification);
    
    // Animar entrada
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Manejar cierre
    const closeBtn = notification.querySelector('.notification-close');
    closeBtn.addEventListener('click', () => closeNotification(notification));
    
    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        closeNotification(notification);
    }, 5000);
}

function closeNotification(notification) {
    notification.style.transform = 'translateX(100%)';
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 300);
}

function getNotificationIcon(type) {
    const icons = {
        success: 'check-circle',
        error: 'exclamation-circle',
        warning: 'exclamation-triangle',
        info: 'info-circle'
    };
    return icons[type] || 'info-circle';
}

function getNotificationColor(type) {
    const colors = {
        success: '#28a745',
        error: '#dc3545',
        warning: '#ffc107',
        info: '#0052cc'
    };
    return colors[type] || '#0052cc';
}

// ====================================
// UTILIDADES
// ====================================

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Función para formatear números (útil para mostrar datos financieros)
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP'
    }).format(amount);
}

// Función para detectar dispositivo móvil
function isMobileDevice() {
    return window.innerWidth <= 768;
}

// ====================================
// EVENTOS DE REDIMENSIONAMIENTO
// ====================================

window.addEventListener('resize', debounce(function() {
    // Ajustar elementos responsivos
    const heroSection = document.querySelector('.hero-section');
    const navMenu = document.querySelector('.nav-menu');
    
    if (window.innerWidth > 768 && navMenu) {
        navMenu.classList.remove('active');
        const menuToggle = document.getElementById('menuToggle');
        if (menuToggle) {
            const spans = menuToggle.querySelectorAll('span');
            spans.forEach(span => {
                span.style.transform = '';
                span.style.opacity = '';
            });
        }
    }
}, 250));

// ====================================
// MANEJO DE ERRORES GLOBALES
// ====================================

window.addEventListener('error', function(e) {
    console.error('Error en dashboard:', e.error);
    // En producción, esto se podría enviar a un servicio de logging
});

// ====================================
// ANALÍTICS Y TRACKING (SIMULADO)
// ====================================

function trackEvent(eventName, eventData) {
    // Simular tracking de eventos
    console.log('Track Event:', eventName, eventData);
    // Aquí se implementaría integración con Google Analytics, etc.
}

// Trackear clicks en botones importantes
document.addEventListener('click', function(e) {
    if (e.target.matches('.btn-primary, .btn-secondary, .btn-login, .btn-card, .btn-product, .btn-service')) {
        const buttonText = e.target.textContent.trim();
        trackEvent('button_click', {
            button_text: buttonText,
            button_type: e.target.className
        });
    }
});

// ====================================
// INICIALIZACIÓN DE TERCEROS
// ====================================

// Función para inicializar widgets de terceros (chat, etc.)
function initializeThirdPartyWidgets() {
    // Simular inicialización de chat en vivo
    setTimeout(() => {
        console.log('Widgets de terceros inicializados');
    }, 2000);
}

// Llamar después de que todo esté cargado
window.addEventListener('load', function() {
    initializeThirdPartyWidgets();
});

// ====================================
// GESTIÓN DE COOKIES (SIMULADO)
// ====================================

function showCookieConsent() {
    if (!localStorage.getItem('cookieConsent')) {
        const consent = document.createElement('div');
        consent.innerHTML = `
            <div style="position: fixed; bottom: 0; left: 0; right: 0; background: #333; color: white; padding: 20px; text-align: center; z-index: 10000;">
                <p>Este sitio utiliza cookies para mejorar tu experiencia. 
                <button onclick="acceptCookies()" style="background: #0052cc; color: white; border: none; padding: 8px 16px; margin-left: 10px; border-radius: 4px; cursor: pointer;">Aceptar</button></p>
            </div>
        `;
        document.body.appendChild(consent);
    }
}

function acceptCookies() {
    localStorage.setItem('cookieConsent', 'true');
    const consent = document.querySelector('[style*="position: fixed; bottom: 0"]');
    if (consent) {
        consent.remove();
    }
}

// Mostrar consentimiento de cookies después de 3 segundos
setTimeout(showCookieConsent, 3000);

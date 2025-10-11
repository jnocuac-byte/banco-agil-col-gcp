// Funciones de navegación globales
function redirectToCredits() {
    window.location.href = 'creditos.html'; // Redirigir a la página de créditos
}

// Función para redirigir a la página de login
function redirectToLogin() {
    window.location.href = 'login.html'; // Redirigir a la página de login
}

// Función para mostrar alerta de "Próximamente" y redirigir a registro si el usuario acepta
function showComingSoon(feature) {
    // Mensaje de confirmación
    const message = `La funcionalidad "${feature}" estará disponible próximamente.\n\n¿Te interesa? Regístrate ahora para ser el primero en conocer cuando esté lista.`;

    // Mostrar confirmación
    if (confirm(message)) {
        window.location.href = 'registro.html'; // Redirigir a la página de registro
    }
}

// Hacer las funciones globales
window.redirectToCredits = redirectToCredits;
window.redirectToLogin = redirectToLogin;
window.showComingSoon = showComingSoon;

// Funcionalidad del carrusel y notificaciones flotantes
document.addEventListener('DOMContentLoaded', () => {
    console.log('Homepage script iniciado'); // Log para verificar que el script se carga
    
    // Elementos del carrusel y notificaciones
    const prevButton = document.querySelector('.carousel-control.prev');
    const nextButton = document.querySelector('.carousel-control.next');
    const slides = document.querySelectorAll('.slide-item');
    const notifs = document.querySelectorAll('.floating-notification-block');
    
    // Estado inicial
    let currentSlideIndex = 1;

    // Función para actualizar el carrusel y las notificaciones
    function updateCarousel(newIndex) {
        currentSlideIndex = newIndex; // Actualizar índice actual

        // Actualizar clases activas
        slides.forEach(slide => {
            // Quitar clase active de todos los slides
            slide.classList.remove('active');
            // Añadir clase active al slide actual
            if (parseInt(slide.getAttribute('data-slide')) === currentSlideIndex) {
                slide.classList.add('active'); // Activar slide actual
            }
        });

        // Actualizar notificaciones
        notifs.forEach(notif => {
            // Quitar clase active-notif de todas las notificaciones
            notif.classList.remove('active-notif');

            // Añadir clase active-notif a la notificación correspondiente al slide actual
            if (parseInt(notif.getAttribute('data-slide-target')) === currentSlideIndex) {
                notif.classList.add('active-notif'); // Activar notificación actual
            }
        });
    }

    // Eventos de los botones y notificaciones
    prevButton.addEventListener('click', () => {
        // Navegar al slide anterior, con wrap-around
        let newIndex = currentSlideIndex - 1;
        // Si es menor que 1, ir al último slide
        if (newIndex < 1) {
            newIndex = slides.length; // Wrap-around al último slide
        }
        updateCarousel(newIndex); // Actualizar carrusel
    });

    // Evento del botón siguiente
    nextButton.addEventListener('click', () => {
        let newIndex = currentSlideIndex + 1; // Incrementar índice

        // Si es mayor que el número de slides, volver al primero
        if (newIndex > slides.length) {
            newIndex = 1; // Wrap-around al primer slide
        }
        updateCarousel(newIndex); // Actualizar carrusel
    });

    // Evento de las notificaciones
    notifs.forEach(notif => {
        // Al hacer clic en una notificación, ir al slide correspondiente
        notif.addEventListener('click', (e) => {
            e.preventDefault();  // Prevenir comportamiento por defecto
            const targetIndex = parseInt(notif.getAttribute('data-slide-target')); // Obtener índice objetivo
            updateCarousel(targetIndex); // Actualizar carrusel
        });
    });

    // Inicializar carrusel en el slide 1
    updateCarousel(currentSlideIndex);
    
    // Inicializar otros elementos interactivos
    initializeQuickAccess();
    initializeDropdowns();
});

// Inicializar accesos rápidos
function initializeQuickAccess() {
    // Seleccionar todos los elementos de acceso rápido
    const quickAccessItems = document.querySelectorAll('.quick-access-item');
    
    // Añadir efectos hover y eventos click
    quickAccessItems.forEach(item => {
        // Efecto hover
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-3px)'; 
            this.style.transition = 'transform 0.3s ease';
        });
        
        // Quitar efecto hover
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
        
        // Agregar eventos de click para funcionalidades específicas
        const text = item.querySelector('span').textContent;
        
        // Dependiendo del texto, asignar diferentes acciones
        if (text.includes('Trámites digitales')) {
            // Ya configurado con onclick en HTML
        } else if (text.includes('Pagos')) { // Ejemplo de funcionalidad futura
            // Mostrar alerta de próximamente
            item.addEventListener('click', function() {
                alert('Funcionalidad de pagos próximamente. Por favor, accede a tu cuenta para pagar facturas.'); // Mensaje informativo
            });
        } else if (text.includes('Centro de Ayuda')) { // Ejemplo de funcionalidad de ayuda
            // Mostrar información de contacto
            item.addEventListener('click', function() {
                alert('¿Necesitas ayuda? Contáctanos:\n📞 01 8000 123 456\n📧 ayuda@bancoagilcol.com'); // Mensaje informativo
            });
        } else { // Otras funcionalidades próximamente
            // Mostrar alerta de próximamente
            item.addEventListener('click', function() {
                showComingSoon(text); // Usar función global para mostrar mensaje
            });
        }
    });
}

// Inicializar dropdowns
function initializeDropdowns() {
    // Seleccionar todos los dropdowns
    const dropdowns = document.querySelectorAll('.virtual-branch-dropdown');
    
    // Añadir eventos a cada dropdown
    dropdowns.forEach(dropdown => {
        const button = dropdown.querySelector('.dropdown-button'); // Botón del dropdown
        const content = dropdown.querySelector('.dropdown-content'); // Contenido del dropdown
        
        // Evento click en el botón
        if (button && content) {
            // Prevenir propagación para evitar cierre inmediato
            button.addEventListener('click', function(e) {
                e.stopPropagation(); // Prevenir cierre inmediato
                
                // Cerrar otros dropdowns
                dropdowns.forEach(otherDropdown => {
                    // Cerrar si no es el actual
                    if (otherDropdown !== dropdown) {
                        const otherContent = otherDropdown.querySelector('.dropdown-content'); // Contenido del otro dropdown

                        // Cerrar si no es el actual
                        if (otherContent) {
                            otherContent.style.display = 'none'; // Ocultar contenido
                        }
                    }
                });
                
                // Toggle este dropdown
                const isVisible = content.style.display === 'block';
                content.style.display = isVisible ? 'none' : 'block';
            });
        }
    });
    
    // Cerrar dropdowns al hacer click fuera
    document.addEventListener('click', function() {
        // Cerrar todos los dropdowns
        dropdowns.forEach(dropdown => {
            // Cerrar cada dropdown
            const content = dropdown.querySelector('.dropdown-content');

            // Cerrar si existe
            if (content) {
                content.style.display = 'none';
            }
        });
    });
}

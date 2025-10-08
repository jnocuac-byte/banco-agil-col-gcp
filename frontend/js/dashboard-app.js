// =========================================
// DASHBOARD - FRONTEND FUNCIONAL
// =========================================

// Variables globales
let balanceVisible = false;
let currentUser = null;

// Inicialización de la página
document.addEventListener('DOMContentLoaded', () => {
    console.log('Dashboard cargado');
    
    // Verificar si el usuario está logueado
    checkAuthentication();
    
    // Inicializar funcionalidades
    initializeDashboard();
    updateDateTime();
    
    // Actualizar fecha cada minuto
    setInterval(updateDateTime, 60000);
    
    // Restaurar estado del sidebar desde localStorage
    const savedSidebar = localStorage.getItem('sidebarExpanded');
    if (savedSidebar === 'true') {
        document.querySelector('.sidebar')?.classList.add('sidebar-expanded');
        document.getElementById('menuToggle')?.setAttribute('aria-expanded', 'true');
    }

    // Conectar evento del botón hamburger
    const menuBtn = document.getElementById('menuToggle');
    if (menuBtn) {
        menuBtn.addEventListener('click', toggleSidebar);
    }
});

// Verificar autenticación
function checkAuthentication() {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const userJson = localStorage.getItem('currentUser');
    
    if (!isLoggedIn || !userJson) {
        alert('Debes iniciar sesión para acceder al dashboard');
        window.location.href = 'login.html';
        return;
    }
    
    try {
        currentUser = JSON.parse(userJson);
        updateUserInfo();
    } catch (error) {
        console.error('Error parsing user data:', error);
        logout();
    }
}

// Actualizar información del usuario en la interfaz
function updateUserInfo() {
    if (!currentUser) return;
    
    const userNameElements = document.querySelectorAll('#userName, .user-name-header');
    userNameElements.forEach(element => {
        if (element) {
            element.textContent = currentUser.name || 'Usuario';
        }
    });
    
    const greetingElement = document.getElementById('greeting');
    if (greetingElement) {
        const hour = new Date().getHours();
        let greeting;
        
        if (hour < 12) greeting = 'Buenos días';
        else if (hour < 18) greeting = 'Buenas tardes';
        else greeting = 'Buenas noches';
        
        greetingElement.textContent = `${greeting}, ${currentUser.name || 'Usuario'}`;
    }
}

// Actualizar fecha y hora
function updateDateTime() {
    const now = new Date();
    const dateTimeElement = document.getElementById('currentDateTime');
    
    if (dateTimeElement) {
        const options = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        };
        
        const formattedDate = now.toLocaleDateString('es-CO', options);
        dateTimeElement.textContent = formattedDate;
    }
}

// Inicializar funcionalidades del dashboard
function initializeDashboard() {
    // Agregar eventos a las tarjetas de transacciones
    const transactionCards = document.querySelectorAll('.transaction-card');
    transactionCards.forEach(card => {
        card.addEventListener('click', function() {
            const text = this.querySelector('.transaction-text').textContent;
            showComingSoon(text);
        });
    });
    
    // Agregar hover effects
    addHoverEffects();
    
    // Simular datos de cuenta
    simulateAccountData();
}

// Agregar efectos hover dinámicos
function addHoverEffects() {
    const cards = document.querySelectorAll('.transaction-card, .welcome-card, .product-detail-card');
    
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 8px 25px rgba(0,0,0,0.2)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '';
        });
    });
}

// Simular datos de la cuenta
function simulateAccountData() {
    const accountNumberMask = document.querySelector('.account-number-mask');
    if (accountNumberMask) {
        // Generar número de cuenta basado en el documento del usuario
        const lastDigits = currentUser?.documentNumber?.slice(-4) || '1234';
        accountNumberMask.textContent = `*****-${lastDigits}`;
    }
}

// Mostrar/ocultar balance
function toggleBalance() {
    const balanceElement = document.getElementById('balance');
    const linkElement = document.querySelector('.show-balance-link');
    
    if (!balanceElement || !linkElement) return;
    
    balanceVisible = !balanceVisible;
    
    if (balanceVisible) {
        // Mostrar balance simulado
        const simulatedBalance = generateSimulatedBalance();
        balanceElement.textContent = simulatedBalance;
        balanceElement.classList.add('balance-visible');
        linkElement.textContent = 'Ocultar saldos';
        
        // Efecto de "cargando"
        balanceElement.style.opacity = '0.5';
        setTimeout(() => {
            balanceElement.style.opacity = '1';
        }, 300);
        
    } else {
        // Ocultar balance
        balanceElement.textContent = '$ *** *** ***';
        balanceElement.classList.remove('balance-visible');
        linkElement.textContent = 'Mostrar saldos';
    }
}

// Generar balance simulado
function generateSimulatedBalance() {
    // Generar un balance aleatorio pero consistente basado en el usuario
    const seed = currentUser?.documentNumber || '12345678';
    let hash = 0;
    for (let i = 0; i < seed.length; i++) {
        const char = seed.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32bit integer
    }
    
    const balance = Math.abs(hash % 5000000) + 500000; // Entre $500K y $5.5M
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(balance);
}

// Mostrar modal "Próximamente"
function showComingSoon(featureName) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    
    if (modal && modalTitle && modalMessage) {
        modalTitle.textContent = `${featureName} - En desarrollo`;
        modalMessage.textContent = `La funcionalidad "${featureName}" estará disponible próximamente. Estamos trabajando para brindarte la mejor experiencia bancaria.`;
        
        modal.classList.add('show');
        modal.style.display = 'flex';
        
        // Agregar efecto de entrada
        setTimeout(() => {
            modal.querySelector('.modal-content').style.transform = 'scale(1)';
        }, 10);
    }
}

// Cerrar modal
function closeModal() {
    const modal = document.getElementById('modal');
    if (modal) {
        modal.classList.remove('show');
        
        setTimeout(() => {
            modal.style.display = 'none';
        }, 300);
    }
}

// Función de logout
function logout() {
    // Mostrar confirmación
    if (confirm('¿Estás seguro que quieres cerrar sesión?')) {
        // Limpiar datos de sesión
        localStorage.removeItem('currentUser');
        localStorage.removeItem('isLoggedIn');
        
        // Mostrar mensaje
        showMessage('Sesión cerrada correctamente', 'success');
        
        // Redirigir después de un momento
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1000);
    }
}

// Función para mostrar mensajes (igual que en app.js)
function showMessage(message, type = 'info') {
    const existingMessage = document.querySelector('.message-notification');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message-notification message-${type}`;
    messageDiv.innerHTML = `
        <p>${message}</p>
        <button onclick="this.parentElement.remove()">×</button>
    `;
    
    Object.assign(messageDiv.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '15px 20px',
        borderRadius: '8px',
        color: 'white',
        fontWeight: 'bold',
        zIndex: '10000',
        maxWidth: '400px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
        backgroundColor: type === 'error' ? '#dc3545' : 
                        type === 'success' ? '#28a745' : 
                        type === 'warning' ? '#ffc107' : '#0052cc',
        transform: 'translateX(100%)',
        transition: 'transform 0.3s ease'
    });
    
    messageDiv.querySelector('button').style.cssText = `
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        font-weight: bold;
        cursor: pointer;
        float: right;
        margin-left: 10px;
    `;
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.style.transform = 'translateX(0)';
    }, 100);
    
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (messageDiv.parentNode) {
                    messageDiv.remove();
                }
            }, 300);
        }
    }, 5000);
}

// Manejar navegación en sidebar
document.addEventListener('click', function(e) {
    if (e.target.closest('.nav-item')) {
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach(item => item.classList.remove('active'));
        
        e.target.closest('.nav-item').classList.add('active');
        
        // Simular navegación (ya que solo tenemos una página)
        const href = e.target.closest('.nav-item').getAttribute('href');
        if (href && href !== '#' && href !== 'dashboard.html') {
            showComingSoon('Sección ' + href.replace('#', ''));
            e.preventDefault();
        }
    }
});

// Manejar tecla Escape para cerrar modal
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// Prevenir navegación hacia atrás si no está logueado
window.addEventListener('popstate', function() {
    checkAuthentication();
});

// Función para simular transacciones recientes (futuro)
function getRecentTransactions() {
    return [
        { date: '2025-10-08', description: 'Pago en línea', amount: -150000, type: 'debit' },
        { date: '2025-10-07', description: 'Depósito', amount: 500000, type: 'credit' },
        { date: '2025-10-06', description: 'Transferencia recibida', amount: 250000, type: 'credit' },
    ];
}

// Exportar funciones para uso global
window.logout = logout;
window.toggleBalance = toggleBalance;
window.showComingSoon = showComingSoon;
window.closeModal = closeModal;

// Alternar estado de la barra lateral (expandir / contraer)
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const menuBtn = document.getElementById('menuToggle');
    if (!sidebar || !menuBtn) return;

    const expanded = sidebar.classList.toggle('sidebar-expanded');
    menuBtn.setAttribute('aria-expanded', expanded);
    localStorage.setItem('sidebarExpanded', expanded ? 'true' : 'false');
}

// Hacer accesible globalmente si hace falta
window.toggleSidebar = toggleSidebar;

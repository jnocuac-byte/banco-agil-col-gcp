/**
 * ====================================
 * CORE DASHBOARD LOGIC (from dashboard.js)
 * ====================================
 */

// Verificar autenticación
function verificarAutenticacion() {
    const usuario = sessionStorage.getItem('usuario');
    
    if (!usuario) {
        // No hay sesión, redirigir al login
        window.location.href = 'login.html';
        return null;
    }
    
    return JSON.parse(usuario);
}

// Cargar datos del usuario
function cargarDatosUsuario() {
    const usuario = verificarAutenticacion();
    
    if (usuario) {
        const nombre = usuario.nombreCompleto || 'Usuario';
        
        // 1. Mostrar nombre en el header
        const userNameHeader = document.getElementById('userName');
        if (userNameHeader) {
             userNameHeader.textContent = nombre;
        }

        // 2. Mostrar nombre en el saludo principal ("Hola, [Usuario]")
        const greetingElement = document.getElementById('greeting');
        if (greetingElement) {
            greetingElement.textContent = `Hola, ${nombre}`; 
        }
        
        // Cargar estadísticas
        cargarEstadisticas(usuario);
    }
}

// Cargar estadísticas (mockup)
async function cargarEstadisticas(usuario) {
    // TODO: Conectar con APIs reales
    // Por ahora mostramos datos de ejemplo

    // Nota: Los IDs 'statSolicitudes', 'statAprobados', 'statCuentas' se usan
    // en la sección de estadísticas que fusionamos del dashboard.html.
    
    document.getElementById('statSolicitudes').textContent = '2'; // Ejemplo
    document.getElementById('statAprobados').textContent = '1'; // Ejemplo
    document.getElementById('statCuentas').textContent = '1'; // Ejemplo
}

// Cerrar sesión
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('usuario');
        window.location.href = 'login.html';
    }
}

/**
 * ====================================
 * UI FUNCTIONALITY (from previous inline script)
 * ====================================
 */

// Lógica para mostrar el modal de "Próximamente"
function showComingSoon(feature) {
    document.getElementById('modalTitle').textContent = `Función: ${feature}`;
    document.getElementById('modalMessage').innerHTML = `La funcionalidad de **${feature}** estará disponible próximamente.`;
    document.getElementById('modal').classList.add('show');
}

// Lógica para cerrar el modal
function closeModal() {
    document.getElementById('modal').classList.remove('show');
}

// Lógica para alternar la visibilidad del saldo
function toggleBalance() {
    const balanceElement = document.getElementById('balance');
    const linkElement = document.querySelector('.show-balance-link');
    const balanceMockValue = '$ 4.560.789';

    if (balanceElement.textContent.includes('***')) {
        // Simular saldo visible y cambiar texto del enlace
        balanceElement.textContent = balanceMockValue;
        balanceElement.classList.add('balance-visible');
        linkElement.textContent = 'Ocultar saldos';
    } else {
         // Simular saldo oculto y cambiar texto del enlace
        balanceElement.textContent = '$ *** *** ***';
        balanceElement.classList.remove('balance-visible');
        linkElement.textContent = 'Mostrar saldos';
    }
}

// Lógica de navegación del menú lateral
function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded');
        });
    }
}

// Simulación de fecha y hora actual
function updateDateTime() {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true };
    const formattedDate = now.toLocaleDateString('es-ES', options).replace(',', '').replace('p. m.', 'p.m.').replace('a. m.', 'a.m.');
    
    const dateTimeElement = document.getElementById('currentDateTime');
    if (dateTimeElement) {
        dateTimeElement.textContent = formattedDate;
    }
}


/**
 * ====================================
 * UTILITIES (from dashboard2.js)
 * ====================================
 */

// Función para limitar la frecuencia de ejecución de una función (útil para eventos de resize/scroll)
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

// Función para formatear números (útil para mostrar datos financieros)
// Aunque no se usa en el mockup de saldo, es buena práctica tenerla.
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP'
    }).format(amount);
}


/**
 * ====================================
 * INICIALIZACIÓN
 * ====================================
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Cargar datos del usuario y estadísticas
    cargarDatosUsuario(); 
    
    // 2. Inicializar listeners UI
    initializeMenuToggle();
    updateDateTime();
    
    // Asignar listeners a los botones de transacciones (que llaman a showComingSoon)
    const transactionCards = document.querySelectorAll('.transactions-grid .transaction-card');
    transactionCards.forEach(card => {
        // Aseguramos que solo las transacciones del inicio abran el modal (si no tienen un 'href')
        if (!card.matches('a[href]')) { 
            const featureName = card.querySelector('.transaction-text').textContent;
            card.setAttribute('onclick', `showComingSoon('${featureName}')`);
        }
    });

    // Asignar el listener de logout al botón
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});

// Nota: La función 'logout' del core dashboard.js ya está disponible globalmente y anula el simple 'alert' anterior.
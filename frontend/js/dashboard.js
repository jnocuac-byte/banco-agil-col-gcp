// Variables globales adicionales (de dashboard-app.js)
let balanceVisible = false;
let currentUser = null;

// Verificar autenticación
function verificarAutenticacion() {
    // Obtener datos del usuario desde sessionStorage
    const usuario = sessionStorage.getItem('usuario');
    
    // Si no hay usuario, redirigir al login
    if (!usuario) {
        // No hay sesión, redirigir al login
        window.location.href = 'login.html';
        return null;
    }
    
    // Retornar objeto usuario
    return JSON.parse(usuario);
}

// Cargar datos del usuario
function cargarDatosUsuario() {
    // Para este mockup, solo verificamos si hay usuario
    const usuario = verificarAutenticacion();
    
    // Si hay usuario, mostrar su nombre y cargar estadísticas
    if (usuario) {
        // Guardar en variable global para funciones adicionales
        currentUser = usuario;
        
        // Obtener nombre completo o email como fallback
        const nombre = usuario.nombreCompleto || 'Usuario';
        
        // Mostrar nombre en el header
        const userNameHeader = document.getElementById('userName');
        // Si el elemento existe, actualizar su contenido
        if (userNameHeader) {
            userNameHeader.textContent = nombre;
        }

        // Mostrar nombre en el saludo principal ("Hola, [Usuario]")
        const greetingElement = document.getElementById('greeting');
        // Si el elemento existe, actualizar su contenido
        if (greetingElement) {
            greetingElement.textContent = `Hola, ${nombre}`; 
        }
        
        // Cargar estadísticas
        cargarEstadisticas(usuario);
        
        // NUEVA: Simular datos de cuenta adicionales
        simulateAccountData();
    }
}

// Cargar estadísticas (mockup)
async function cargarEstadisticas(usuario) {
    // En un caso real, haríamos fetch a la API para obtener datos reales
    // Aquí simulamos con datos estáticos
    document.getElementById('statSolicitudes').textContent = '2'; // Ejemplo
    document.getElementById('statAprobados').textContent = '1'; // Ejemplo
    document.getElementById('statCuentas').textContent = '1'; // Ejemplo
}

// Cerrar sesión
function logout() {
    // Confirmar acción
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        // Eliminar datos de sessionStorage y redirigir al login
        sessionStorage.removeItem('usuario');
        window.location.href = 'login.html';
    }
}

// Lógica para mostrar el modal de "Próximamente"
function showComingSoon(feature) {
    // Actualizar contenido del modal
    document.getElementById('modalTitle').textContent = `Función: ${feature}`; // Título dinámico
    document.getElementById('modalMessage').innerHTML = `La funcionalidad de **${feature}** estará disponible próximamente.`; // Mensaje dinámico
    document.getElementById('modal').classList.add('show'); // Mostrar el modal
}

// Lógica para cerrar el modal
function closeModal() {
    // Ocultar el modal
    document.getElementById('modal').classList.remove('show');
}

// Lógica para alternar la visibilidad del saldo
function toggleBalance() {
    // Simulación de mostrar/ocultar saldo
    const balanceElement = document.getElementById('balance');
    const linkElement = document.querySelector('.show-balance-link');
    const balanceMockValue = '$ 4.560.789';

    // Alternar entre mostrar y ocultar
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
    // Alternar clase para expandir/colapsar sidebar
    const menuToggle = document.getElementById('menuToggle');
    // Asegurarse que el elemento existe
    if (menuToggle) {
        menuToggle.addEventListener('click', function() { // Alternar clase
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded'); // Alternar clase
        });
    }
}

// Simulación de fecha y hora actual
function updateDateTime() {
    // Formatear fecha y hora en español
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true };
    const formattedDate = now.toLocaleDateString('es-ES', options).replace(',', '').replace('p. m.', 'p.m.').replace('a. m.', 'a.m.');
    
    // Actualizar en el DOM si el elemento existe
    const dateTimeElement = document.getElementById('currentDateTime');
    if (dateTimeElement) {
        dateTimeElement.textContent = formattedDate; // Ejemplo: "lunes, 1 de enero de 2024 10:30 a.m."
    }
}

// Función para limitar la frecuencia de ejecución de una función (útil para eventos de resize/scroll)
function debounce(func, wait) {
    // Varianle para almacenar el timeout
    let timeout;
    // Para evitar problemas con el contexto 'this', usamos una función flecha
    return function executedFunction(...args) {
        // Función que se ejecuta después del tiempo de espera
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        // Limpiar el timeout previo y reiniciar el conteo
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Función para formatear números (útil para mostrar datos financieros)
// Aunque no se usa en el mockup de saldo, es buena práctica tenerla.
function formatCurrency(amount) {
    // Para COP
    return new Intl.NumberFormat('es-CO', {
        style: 'currency', // Formato de moneda
        currency: 'COP' // Peso colombiano
    }).format(amount); // Retornar cadena formateada
}

// Simular datos de la cuenta (NUEVA)
function simulateAccountData() {
    // Mostrar número de cuenta enmascarado
    const accountNumberMask = document.querySelector('.account-number-mask');
    // Si el elemento existe y hay usuario
    if (accountNumberMask && currentUser) {
        // Generar número de cuenta basado en el documento del usuario
        const lastDigits = currentUser.documentNumber?.slice(-4) ||  // Usar los últimos 4 dígitos del documento o '1234' como fallback
                          currentUser.documento?.slice(-4) || '1234'; // Soporte para ambos campos
        accountNumberMask.textContent = `*****-${lastDigits}`; // Ejemplo: *****-5678
    }
}

// Agregar efectos hover dinámicos
function addHoverEffects() {
    // Seleccionar todas las tarjetas relevantes
    const cards = document.querySelectorAll('.transaction-card, .welcome-card, .product-detail-card');
    
    // Agregar listeners para hover
    cards.forEach(card => {
        // Usar funciones normales para mantener el contexto 'this'
        card.addEventListener('mouseenter', function() {
            // Aplicar transformaciones CSS para efecto hover
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 8px 25px rgba(0,0,0,0.2)';
        });
        
        // Revertir transformaciones al salir del hover
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '';
        });
    });
}

// Mostrar mensaje de notificación 
function showMessage(message, type = 'info') {
    // type: 'success', 'error', 'warning', 'info'
    const existingMessage = document.querySelector('.message-notification');
    // Eliminar mensaje previo si existe
    if (existingMessage) {
        existingMessage.remove(); 
    }
    
    // Crear nuevo mensaje
    const messageDiv = document.createElement('div');
    messageDiv.className = `message-notification message-${type}`; // Añadir clase según tipo
    messageDiv.innerHTML = `
        <p>${message}</p>
        <button onclick="this.parentElement.remove()">×</button>
    `;
    
    // Estilos básicos
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
    
    // Estilos del botón de cierre
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
    
    // Añadir al body y mostrar con animación
    document.body.appendChild(messageDiv);
    
    // Animar entrada
    setTimeout(() => {
        messageDiv.style.transform = 'translateX(0)'; // Slide in
    }, 100);
    
    // Ocultar después de 5 segundos con animación de salida
    setTimeout(() => {
        // Animar salida
        if (messageDiv.parentNode) {
            messageDiv.style.transform = 'translateX(100%)'; // Slide out
            // Remover del DOM después de la animación
            setTimeout(() => {
                // Verificar si el elemento aún existe antes de remover
                if (messageDiv.parentNode) {
                    messageDiv.remove(); // Remover del DOM
                }
            }, 300); // Tiempo de la animación
        }
    }, 5000); // 5 segundos
}

// Función para transacciones recientes (NUEVA - para futuro uso)
function getRecentTransactions() {
    // Simulación de transacciones
    return [
        { date: '2025-10-08', description: 'Pago en línea', amount: -150000, type: 'debit' },
        { date: '2025-10-07', description: 'Depósito', amount: 500000, type: 'credit' },
        { date: '2025-10-06', description: 'Transferencia recibida', amount: 250000, type: 'credit' },
    ];
}


// Cerrar modal con tecla Escape (NUEVO)
document.addEventListener('keydown', function(e) {
    // Cerrar modal si está abierto y se presiona Escape
    if (e.key === 'Escape') {
        closeModal(); // Cerrar el modal
    }
});

// Inicialización al cargar el DOM
document.addEventListener('DOMContentLoaded', () => {
    // Cargar datos del usuario y estadísticas
    cargarDatosUsuario(); 
    
    // Inicializar listeners UI
    initializeMenuToggle();
    updateDateTime();
    
    // NUEVO: Agregar efectos hover
    addHoverEffects();
    
    // NUEVO: Actualizar fecha cada minuto
    setInterval(updateDateTime, 60000);
    
    // Asignar listeners a los botones de transacciones (que llaman a showComingSoon)
    const transactionCards = document.querySelectorAll('.transactions-grid .transaction-card');
    transactionCards.forEach(card => {
        // Aseguramos que solo las transacciones del inicio abran el modal (si no tienen un 'href')
        if (!card.matches('a[href]')) { 
            // Obtener el nombre de la función desde el texto del card
            const featureName = card.querySelector('.transaction-text').textContent;
            card.setAttribute('onclick', `showComingSoon('${featureName}')`);
        }
    });

    // Asignar el listener de logout al botón
    const logoutBtn = document.querySelector('.logout-btn');
    // Si el botón existe, asignar el evento
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});
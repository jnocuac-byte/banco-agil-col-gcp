// Verificar autenticación
function verificarAutenticacion() {
    // Obtener datos del usuario desde sessionStorage
    const usuario = sessionStorage.getItem('usuario');
    
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
        // Obtener nombre completo o email como fallback
        const nombre = usuario.nombreCompleto || 'Usuario';
        
        // Mostrar nombre en el header
        const userNameHeader = document.getElementById('userName');
        // Si el elemento existe, actualizar su contenido
        if (userNameHeader) {
            // userNameHeader.textContent = usuario.nombreCompleto || 'Usuario';
             userNameHeader.textContent = nombre;
        }

        // Mostrar nombre en el saludo principal ("Hola, [Usuario]")
        const greetingElement = document.getElementById('greeting');
        // Si el elemento existe, actualizar su contenido
        if (greetingElement) {
            // greetingElement.textContent = `Hola, ${usuario.nombreCompleto || 'Usuario'}`;
            greetingElement.textContent = `Hola, ${nombre}`; 
        }
        
        // Cargar estadísticas
        cargarEstadisticas(usuario);
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
    document.getElementById('modalTitle').textContent = `Función: ${feature}`;
    document.getElementById('modalMessage').innerHTML = `La funcionalidad de **${feature}** estará disponible próximamente.`;
    document.getElementById('modal').classList.add('show');
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
    }).format(amount);
}

// Inicialización al cargar el DOM
document.addEventListener('DOMContentLoaded', () => {
    // Cargar datos del usuario y estadísticas
    cargarDatosUsuario(); 
    
    // Inicializar listeners UI
    initializeMenuToggle();
    updateDateTime();
    
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
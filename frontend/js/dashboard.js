// Variables globales
let balanceVisible = false;
let currentUser = null;

// Verificar autenticación
function verificarAutenticacion() {
    const usuario = sessionStorage.getItem('usuario');
    if (!usuario) {
        window.location.href = 'login.html';
        return null;
    }
    
    // Retornar objeto usuario
    return JSON.parse(usuario);
}

// Cargar datos del usuario
function cargarDatosUsuario() {
    const usuario = verificarAutenticacion();
    
    if (usuario) {
        // Guardar en variable global para funciones adicionales
        currentUser = usuario;
        
        // Obtener nombre completo o email como fallback
        const nombre = usuario.nombreCompleto || 'Usuario';
        
        // Mostrar nombre en el header
        const userNameHeader = document.getElementById('userName');
        if (userNameHeader) {
            userNameHeader.textContent = nombre;
        }

        // Mostrar nombre en el saludo principal ("Hola, [Usuario]")
        const greetingElement = document.getElementById('greeting');
        if (greetingElement) {
            greetingElement.textContent = `Hola, ${nombre}`; 
        }
        
        // Cargar estadísticas
        cargarEstadisticas(usuario);
        
        // Simular datos de cuenta adicionales
        simulateAccountData();

        // Verificar estado de documentos y mostrar alerta si es necesario
        verificarEstadoDocumentos(usuario);
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
    // Usar el nuevo modal de confirmación en lugar del confirm() del navegador
    showConfirmationModal(
        'Cerrar Sesión',
        '¿Estás seguro de que deseas cerrar tu sesión?',
        () => {

            sessionStorage.removeItem('usuario');
            sessionStorage.removeItem('authToken'); 
            window.location.href = 'login.html';
        }
    );
}

// Muestra el modal "Próximamente"
function showComingSoon(feature) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const btnConfirm = document.getElementById('modalBtnConfirm');
    const btnCancel = document.getElementById('modalBtnCancel');

    modalTitle.textContent = `Función: ${feature}`;
    modalMessage.innerHTML = `La funcionalidad de <strong>${feature}</strong> estará disponible próximamente.`;
    
    // Para "Próximamente", solo mostramos un botón de confirmación que actúa como "Entendido"
    btnConfirm.textContent = 'Entendido';
    btnConfirm.onclick = closeModal; // El botón de confirmar solo cierra el modal
    btnCancel.style.display = 'none'; // Ocultamos el botón de cancelar
    btnConfirm.style.display = 'inline-block';

    modal.classList.add('show');
}

// Cierra el modal
function closeModal() {
    document.getElementById('modal').classList.remove('show');
}

/**
 * Muestra un modal de confirmación genérico.
 * @param {string} title - El título del modal.
 * @param {string} message - El mensaje a mostrar en el cuerpo del modal.
 * @param {function} onConfirm - La función a ejecutar si el usuario confirma.
 */
function showConfirmationModal(title, message, onConfirm) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const btnConfirm = document.getElementById('modalBtnConfirm');
    const btnCancel = document.getElementById('modalBtnCancel');

    modalTitle.textContent = title;
    modalMessage.textContent = message;

    btnConfirm.textContent = 'Confirmar';
    btnConfirm.style.display = 'inline-block';
    btnCancel.style.display = 'inline-block';

    // Asignar la acción de confirmación. Se usa .onclick para simplicidad.
    btnConfirm.onclick = () => {
        onConfirm();
        closeModal();
    };

    modal.classList.add('show');
}

// Alterna la visibilidad del saldo
function toggleBalance() {
    // Simulación de mostrar/ocultar saldo
    const balanceElement = document.getElementById('balance');
    const linkElement = document.querySelector('.show-balance-link');
    const balanceMockValue = '$ 4.560.789';

    // Alternar entre mostrar y ocultar
    if (balanceElement.textContent.includes('***')) {
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

// Inicializa el menú lateral
function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded');
        });
    }
}

// Actualiza la fecha y hora
function updateDateTime() {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true };
    const formattedDate = now.toLocaleDateString('es-ES', options).replace(',', '').replace('p. m.', 'p.m.').replace('a. m.', 'a.m.');
    
    const dateTimeElement = document.getElementById('currentDateTime');
    if (dateTimeElement) {
        dateTimeElement.textContent = formattedDate;
    }
}

// Limita la frecuencia de ejecución de una función (debounce)
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

// Formatea números como moneda
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP'
    }).format(amount);
}

// Simula y muestra datos de la cuenta
function simulateAccountData() {
    const accountNumberMask = document.querySelector('.account-number-mask');
    if (accountNumberMask && currentUser) {
        const lastDigits = currentUser.documentNumber?.slice(-4) || currentUser.documento?.slice(-4) || '1234';
        accountNumberMask.textContent = `*****-${lastDigits}`;
    }
}

// Agregar efectos hover dinámicos
function addHoverEffects() {
    // Seleccionar todas las tarjetas relevantes
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

// Muestra un mensaje de notificación flotante
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

// Obtiene transacciones recientes (simulación)
function getRecentTransactions() {
    // Simulación de transacciones
    return [
        { date: '2025-10-08', description: 'Pago en línea', amount: -150000, type: 'debit' },
        { date: '2025-10-07', description: 'Depósito', amount: 500000, type: 'credit' },
        { date: '2025-10-06', description: 'Transferencia recibida', amount: 250000, type: 'credit' },
    ];
}

// Verifica y muestra alerta de estado de documentos
function verificarEstadoDocumentos(usuario) {
    const alertContainer = document.getElementById('documentStatusAlert');
    const alertMessage = document.getElementById('documentStatusMessage');
    const alertAction = alertContainer.querySelector('.alert-action');

    if (!usuario.estadoDocumento) {
        return;
    }

    const estado = usuario.estadoDocumento.toUpperCase();

    if (estado === 'PENDIENTE') {
        alertMessage.textContent = 'Tu cuenta está casi lista. Por favor, sube tu documento de identidad para activar todas las funciones.';
        alertContainer.className = 'document-status-alert alert-warning';
        alertAction.textContent = 'Subir documento';
        alertContainer.classList.remove('hidden');
    } else if (estado === 'NEGADO') {
        alertMessage.textContent = 'Hubo un problema con tu documento. Por favor, súbelo de nuevo para poder continuar.';
        alertContainer.className = 'document-status-alert alert-danger';
        alertAction.textContent = 'Subir de nuevo';
        alertContainer.classList.remove('hidden');
    }
}

// Restringe la navegación si los documentos no están aprobados
function interceptarNavegacionRestringida(usuario) {
    const enlacesRestringidos = document.querySelectorAll('a[href="solicitud-credito.html"], a[href="mis-solicitudes.html"]');
    const estado = usuario.estadoDocumento?.toUpperCase();

    // Si el estado es pendiente o negado, bloqueamos la navegación
    if (estado === 'PENDIENTE' || estado === 'NEGADO') {
        enlacesRestringidos.forEach(enlace => {
            enlace.addEventListener('click', function(event) {
                event.preventDefault(); // Prevenir la navegación
                
                const mensaje = estado === 'PENDIENTE' 
                    ? 'Debes subir tu documento de identidad antes de poder solicitar un crédito.'
                    : 'Tu documento fue negado. Por favor, súbelo de nuevo para poder solicitar un crédito.';
                
                showMessage(mensaje, 'warning');
            });
        });
    }
}

// Cierra el modal con la tecla Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// Inicialización al cargar el DOM
document.addEventListener('DOMContentLoaded', () => {
    cargarDatosUsuario(); 
    
    initializeMenuToggle();
    updateDateTime();
    
    addHoverEffects();
    
    setInterval(updateDateTime, 60000);

    if (currentUser) {
        interceptarNavegacionRestringida(currentUser);
    }
    
    // Selector más específico para evitar incluir las tarjetas de estadísticas
    const transactionCards = document.querySelectorAll('.transactions-area .transaction-card');
    transactionCards.forEach(card => {
        if (!card.matches('a[href]')) { 
            const featureName = card.querySelector('.transaction-text').textContent;
            card.setAttribute('onclick', `showComingSoon('${featureName}')`);
        }
    });
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});
const API_CREDIT_URL = 'http://localhost:8082/api/solicitudes';

let solicitudesData = [];

// --- FUNCIONES CORE DE AUTH Y DATA ---

// Verificar autenticación (Tomado de mis-solicitudes.js)
function verificarAutenticacion() {
    const usuario = sessionStorage.getItem('usuario');
    
    if (!usuario) {
        // Redirigir al login (Comportamiento del dashboard.js/mis-solicitudes.js)
        window.location.href = 'login.html';
        return null;
    }
    
    return JSON.parse(usuario);
}

// Formatear números como moneda (Tomado de mis-solicitudes.js)
function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(valor);
}

// Formatear fecha (Tomado de mis-solicitudes.js)
function formatearFecha(fecha) {
    // Usamos 'es-ES' o 'es-CO' para mantener consistencia con el dashboard y el archivo original
    return new Date(fecha).toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Obtener clase CSS del badge según estado (Tomado de mis-solicitudes.js)
function getBadgeClass(estado) {
    const clases = {
        'PENDIENTE': 'badge-pendiente',
        'EN_REVISION': 'badge-en-revision',
        'APROBADA': 'badge-aprobada',
        'RECHAZADA': 'badge-rechazada'
    };
    return clases[estado] || 'badge-pendiente';
}

// Obtener texto amigable del estado (Tomado de mis-solicitudes.js)
function getEstadoTexto(estado) {
    const textos = {
        'PENDIENTE': 'Pendiente',
        'EN_REVISION': 'En Revisión',
        'APROBADA': 'Aprobada',
        'RECHAZADA': 'Rechazada'
    };
    return textos[estado] || estado;
}

// Cargar solicitudes del cliente (Tomado y adaptado de mis-solicitudes.js)
async function cargarSolicitudes() {
    const usuario = verificarAutenticacion();
    if (!usuario) return;
    
    // 1. Mostrar nombre del usuario en el header (se usa el ID del dashboard)
    const userNameHeaderElements = document.querySelectorAll('#userName'); // Se puede repetir en la nueva estructura
    userNameHeaderElements.forEach(el => {
        el.textContent = usuario.nombreCompleto || 'Usuario';
    });
    
    // Nota: El email no tiene un ID en la nueva estructura, se omite.

    try {
        const response = await fetch(`${API_CREDIT_URL}/cliente/${usuario.clienteId}`);
        
        if (response.ok) {
            solicitudesData = await response.json();
            mostrarSolicitudes(solicitudesData);
        } else {
            mostrarEstadoVacio('Error al cargar solicitudes');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarEstadoVacio('Error al conectar con el servidor');
    }
}

// Mostrar solicitudes en el DOM (Tomado de mis-solicitudes.js)
function mostrarSolicitudes(solicitudes) {
    const container = document.getElementById('solicitudesContainer');
    
    if (solicitudes.length === 0) {
        mostrarEstadoVacio('No tienes solicitudes aún');
        return;
    }
    
    container.innerHTML = solicitudes.map(sol => `
        <div class="solicitud-card">
            <div class="solicitud-header">
                <div class="solicitud-id">
                    <i class="fas fa-file-alt"></i> Solicitud #${sol.id}
                </div>
                <span class="solicitud-badge ${getBadgeClass(sol.estado)}">
                    ${getEstadoTexto(sol.estado)}
                </span>
            </div>
            
            <div class="solicitud-details">
                <div class="detail-item">
                    <span class="detail-label">Monto Solicitado</span>
                    <span class="detail-value">${formatearMoneda(sol.montoSolicitado)}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Plazo</span>
                    <span class="detail-value">${sol.plazoMeses} meses</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Tasa de Interés</span>
                    <span class="detail-value">${sol.tasaInteres ? sol.tasaInteres + '% EA' : 'Por definir'}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Fecha de Solicitud</span>
                    <span class="detail-value">${formatearFecha(sol.fechaSolicitud)}</span>
                </div>
            </div>
            
            ${sol.observaciones ? `
                <div class="solicitud-observaciones">
                    <strong style="color: var(--color-primary-yellow);">Observaciones:</strong>
                    <p>${sol.observaciones}</p>
                </div>
            ` : ''}
            
            ${sol.fechaDecision ? `
                <div class="detail-item" style="margin-top: 1rem;">
                    <span class="detail-label">Fecha de Decisión</span>
                    <span class="detail-value">${formatearFecha(sol.fechaDecision)}</span>
                </div>
            ` : ''}
        </div>
    `).join('');
}

// Mostrar estado vacío (Tomado de mis-solicitudes.js)
function mostrarEstadoVacio(mensaje) {
    const container = document.getElementById('solicitudesContainer');
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-inbox"></i>
            <h3>${mensaje}</h3>
            <p>Solicita tu primer crédito y aparecerá aquí</p>
            <a href="solicitud-credito.html" class="btn-primary">
                <i class="fas fa-plus-circle"></i> Nueva Solicitud
            </a>
        </div>
    `;
}

// Filtrar solicitudes por estado (Tomado de mis-solicitudes.js)
function filtrarSolicitudes() {
    const filtro = document.getElementById('filtroEstado').value;
    
    if (filtro === 'TODAS') {
        mostrarSolicitudes(solicitudesData);
    } else {
        const filtradas = solicitudesData.filter(sol => sol.estado === filtro);
        mostrarSolicitudes(filtradas);
    }
}

// --- FUNCIONES UI DEL DASHBOARD (INTEGRADAS) ---

// Cerrar sesión (Tomado de dashboard.js)
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('usuario');
        window.location.href = 'login.html';
    }
}

// Lógica de navegación del menú lateral (Tomado de dashboard.js)
function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('sidebar-expanded');
            // La clase .sidebar-expanded ~ .main-content ajusta el margen automáticamente vía CSS
        });
    }
}

// Inicializar (Adaptado para ejecutar la carga de datos y la inicialización UI)
document.addEventListener('DOMContentLoaded', () => {
    // 1. Cargar datos del usuario y solicitudes
    cargarSolicitudes(); 
    
    // 2. Inicializar listeners UI (Solo la función de menú)
    initializeMenuToggle();
    
    // Nota: El listener de logout en el HTML ya está asignado a la función global logout()
});
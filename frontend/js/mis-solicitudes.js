// URL base de la API
const API_CREDIT_URL = 'http://localhost:8082/api/solicitudes';

// Datos de solicitudes en memoria
let solicitudesData = [];

// Verificar si el usuario está autenticado
function verificarAutenticacion() {
    // Obtener datos del usuario desde sessionStorage
    const usuario = sessionStorage.getItem('usuario');
    
    // Si no hay usuario, redirigir al login
    if (!usuario) {
        window.location.href = 'login.html';
        return null;
    }
    
    // Retornar objeto usuario
    return JSON.parse(usuario);
}

// Formatear moneda en COP
function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-CO', { // Formato colombiano
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0 // Sin decimales
    }).format(valor); // Ejemplo: $1.000.000
}

// Formatear fecha a formato legible
function formatearFecha(fecha) {
    return new Date(fecha).toLocaleDateString('es-CO', { // Formato colombiano
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }); // Ejemplo: 15 de marzo de 2023, 14:30
}

// Mostrar alertas
function getBadgeClass(estado) {
    // Mapear estado a clase CSS
    const clases = {
        'PENDIENTE': 'badge-pendiente',
        'EN_REVISION': 'badge-en-revision',
        'APROBADA': 'badge-aprobada',
        'RECHAZADA': 'badge-rechazada'
    };
    return clases[estado] || 'badge-pendiente'; // Clase por defecto
}

// Mapear estado a texto legible
function getEstadoTexto(estado) {
    // Mapear estado a texto
    const textos = {
        'PENDIENTE': 'Pendiente',
        'EN_REVISION': 'En Revisión',
        'APROBADA': 'Aprobada',
        'RECHAZADA': 'Rechazada'
    };
    return textos[estado] || estado; // Texto por defecto
}

// Cargar solicitudes del usuario
async function cargarSolicitudes() {
    // Verificar autenticación
    const usuario = verificarAutenticacion();
    if (!usuario) return;
    
    // Mostrar nombre del usuario en el header
    const userNameHeaderElements = document.querySelectorAll('#userName'); 
    // Puede haber múltiples elementos con id userName
    userNameHeaderElements.forEach(el => {
        el.textContent = usuario.nombreCompleto || 'Usuario'; // Nombre completo o 'Usuario' por defecto
    });

    // Mostrar loading mientras se cargan las solicitudes
    try {
        // Hacer petición a la API para obtener solicitudes del cliente
        const response = await fetch(`${API_CREDIT_URL}/cliente/${usuario.clienteId}`);
        
        // Verificar respuesta
        if (response.ok) {
            solicitudesData = await response.json();
            mostrarSolicitudes(solicitudesData); // Mostrar todas las solicitudes
        } else { // Error en la respuesta
            mostrarEstadoVacio('Error al cargar solicitudes'); // Mostrar mensaje de error
        }
    } catch (error) { // Error de red o servidor
        console.error('Error:', error);
        mostrarEstadoVacio('Error al conectar con el servidor'); // Mostrar mensaje de error
    }
}

// Mostrar solicitudes en el DOM
function mostrarSolicitudes(solicitudes) {
    // Referencia al contenedor
    const container = document.getElementById('solicitudesContainer');
    
    // Si no hay solicitudes, mostrar estado vacío
    if (solicitudes.length === 0) {
        mostrarEstadoVacio('No tienes solicitudes aún'); // Mensaje personalizado
        return;
    }
    
    // Limpiar contenedor
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
} // Unir todas las tarjetas en el contenedor

// Mostrar estado vacío con mensaje personalizado
function mostrarEstadoVacio(mensaje) {
    // Referencia al contenedor
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
} // Mensaje personalizado

// Filtrar solicitudes por estado
function filtrarSolicitudes() {
    // Obtener valor del filtro
    const filtro = document.getElementById('filtroEstado').value;
    
    // Filtrar y mostrar solicitudes
    if (filtro === 'TODAS') {
        mostrarSolicitudes(solicitudesData); // Mostrar todas
    } else {
        const filtradas = solicitudesData.filter(sol => sol.estado === filtro); // Filtrar por estado
        mostrarSolicitudes(filtradas); // Mostrar filtradas
    }
}

// Cerrar sesión
function logout() {
    // Confirmar acción
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('usuario'); // Eliminar datos de sessionStorage
        window.location.href = 'login.html'; // Redirigir al login
    }
}

// Inicializar menú lateral
function initializeMenuToggle() {
    // Toggle para menú lateral
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');
    // Asegurarse que los elementos existen
    if (menuToggle && sidebar) {
        // Alternar clase para expandir/colapsar sidebar
        menuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('sidebar-expanded'); // Alternar clase
        });
    }
}

// Inicializar la página
document.addEventListener('DOMContentLoaded', () => {
    cargarSolicitudes(); // Cargar solicitudes al cargar la página
    initializeMenuToggle(); // Inicializar menú lateral
});
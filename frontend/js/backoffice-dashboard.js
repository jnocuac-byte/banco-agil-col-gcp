// URL base de la API de solicitudes
const API_CREDIT_URL = 'http://localhost:8082/api/solicitudes';

// Datos de las solicitudes
let solicitudesData = [];

// Verificar autenticación de asesor
function verificarAutenticacion() {
    const asesor = sessionStorage.getItem('asesor');
    
    // No hay sesión, redirigir al login
    if (!asesor) {
        window.location.href = 'backoffice-login.html';
        return null;
    }
    
    // Retornar datos del asesor
    return JSON.parse(asesor);
}

// Formatear moneda
function formatearMoneda(valor) {
    // Usar Intl para formatear moneda en COP
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(valor);
}

// Formatear fecha
function formatearFecha(fecha) {
    // Formatear fecha a "DD MMMM YYYY"
    return new Date(fecha).toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Obtener clase del badge
function getBadgeClass(estado) {
    // Mapeo de estados a clases CSS
    const clases = {
        'PENDIENTE': 'badge-pendiente',
        'EN_REVISION': 'badge-en-revision',
        'APROBADA': 'badge-aprobada',
        'RECHAZADA': 'badge-rechazada'
    };
    // Retornar clase correspondiente o clase por defecto
    return clases[estado] || 'badge-pendiente';
}

// Obtener texto del estado
function getEstadoTexto(estado) {
    // Mapeo de estados a textos legibles
    const textos = {
        'PENDIENTE': 'Pendiente',
        'EN_REVISION': 'En Revisión',
        'APROBADA': 'Aprobada',
        'RECHAZADA': 'Rechazada'
    };
    // Retornar texto correspondiente o el estado original si no se encuentra
    return textos[estado] || estado;
}

// Cargar todas las solicitudes
async function cargarSolicitudes() {
    // Verificar autenticación
    const asesor = verificarAutenticacion();
    if (!asesor) return;
    
    // Actualizar nombre del asesor
    document.getElementById('userName').textContent = asesor.nombreCompleto;
    
    // Mostrar loading mientras se cargan las solicitudes
    try {
        // Llamada a la API para obtener solicitudes
        const response = await fetch(`${API_CREDIT_URL}/todas`, {
            // Por ahora obtenemos todas las solicitudes
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        // Procesar respuesta
        if (response.ok) {
            solicitudesData = await response.json();
            actualizarEstadisticas(solicitudesData);
            mostrarSolicitudes(solicitudesData);
        } else { // Error en la respuesta
            mostrarEstadoVacio('Error al cargar solicitudes');
        }
    } catch (error) { // Error de red u otro
        console.error('Error:', error);
        mostrarEstadoVacio('Error al conectar con el servidor');
    }
}

// Actualizar estadísticas
function actualizarEstadisticas(solicitudes) {
    // Calcular estadísticas básicas
    const pendientes = solicitudes.filter(s => s.estado === 'PENDIENTE').length;
    const enRevision = solicitudes.filter(s => s.estado === 'EN_REVISION').length;
    
    // Aprobadas hoy
    const hoy = new Date().toDateString();

    // Filtrar solicitudes aprobadas hoy
    const aprobadasHoy = solicitudes.filter(s => {
        // Verificar si la solicitud fue aprobada hoy
        if (s.estado === 'APROBADA' && s.fechaDecision) { 
            // Comparar solo la parte de la fecha
            return new Date(s.fechaDecision).toDateString() === hoy;
        }
        // No es aprobada o no tiene fecha de decisión
        return false;
    }).length;
    
    // Actualizar elementos del DOM
    document.getElementById('statPendientes').textContent = pendientes;
    document.getElementById('statRevision').textContent = enRevision;
    document.getElementById('statAprobadas').textContent = aprobadasHoy;
    document.getElementById('statTotal').textContent = solicitudes.length;
}

// Mostrar solicitudes en la tabla
function mostrarSolicitudes(solicitudes) {
    // Referencia al cuerpo de la tabla
    const tbody = document.getElementById('tablaSolicitudes');
    
    // Si no hay solicitudes, mostrar estado vacío
    if (solicitudes.length === 0) {
        // Mostrar mensaje de estado vacío
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>No hay solicitudes para mostrar</p>
                </td>
            </tr>
        `;
        // Salir de la función
        return;
    }
    
    // Construir filas de la tabla
    tbody.innerHTML = solicitudes.map(sol => `
        <tr>
            <td><strong>#${sol.id}</strong></td>
            <td>Cliente ID: ${sol.idCliente}</td>
            <td>${formatearMoneda(sol.montoSolicitado)}</td>
            <td>${sol.plazoMeses} meses</td>
            <td>${sol.tasaInteres ? sol.tasaInteres + '%' : '-'}</td>
            <td>
                <span class="badge ${getBadgeClass(sol.estado)}">
                    ${getEstadoTexto(sol.estado)}
                </span>
            </td>
            <td>${formatearFecha(sol.fechaSolicitud)}</td>
            <td>
                ${sol.estado === 'PENDIENTE' || sol.estado === 'EN_REVISION' ? `
                    <button class="btn-action btn-aprobar" onclick="aprobarSolicitud(${sol.id})">
                        <i class="fas fa-check"></i> Aprobar
                    </button>
                    <button class="btn-action btn-rechazar" onclick="rechazarSolicitud(${sol.id})">
                        <i class="fas fa-times"></i> Rechazar
                    </button>
                ` : '-'}
            </td>
        </tr>
    `).join('');
}

// Mostrar estado vacío
function mostrarEstadoVacio(mensaje) {
    // Referencia al cuerpo de la tabla
    const tbody = document.getElementById('tablaSolicitudes');
    tbody.innerHTML = `
        <tr>
            <td colspan="8" class="empty-state">
                <i class="fas fa-exclamation-circle"></i>
                <p>${mensaje}</p>
            </td>
        </tr>
    `;
}

// Filtrar solicitudes
function filtrarSolicitudes() {
    // Obtener valor del filtro
    const filtro = document.getElementById('filtroEstado').value;
    
    // Filtrar solicitudes según el estado seleccionado
    if (filtro === 'TODAS') {
        // Mostrar todas las solicitudes
        mostrarSolicitudes(solicitudesData);
    } else {
        // Filtrar por estado específico
        const filtradas = solicitudesData.filter(sol => sol.estado === filtro);
        mostrarSolicitudes(filtradas);
    }
}

// Aprobar solicitud
async function aprobarSolicitud(id) {
    // Confirmar acción
    if (!confirm('¿Estás seguro de aprobar esta solicitud?')) return;
    
    // Verificar autenticación
    const asesor = verificarAutenticacion();
    
    // Llamada a la API para aprobar
    try {
        // Llamada a la API para aprobar
        const response = await fetch(`${API_CREDIT_URL}/${id}/aprobar`, {
            // Usar método PUT para actualizar estado
            method: 'PUT',
            // Enviar ID del asesor que aprueba
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                asesorId: asesor.asesorId
            })
        });
        
        // Procesar respuesta
        if (response.ok) {
            // Éxito
            alert('Solicitud aprobada exitosamente');
            cargarSolicitudes(); // Recargar la lista
        } else {
            // Error
            alert('Error al aprobar la solicitud');
        }
    } catch (error) { // Error de red u otro
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Rechazar solicitud
async function rechazarSolicitud(id) {
    // Confirmar acción
    const motivo = prompt('Motivo del rechazo:');
    if (!motivo) return;
    
    // Verificar autenticación
    const asesor = verificarAutenticacion();
    
    // Llamada a la API para rechazar
    try {
        // Llamada a la API para rechazar
        const response = await fetch(`${API_CREDIT_URL}/${id}/rechazar`, {
            // Usar método PUT para actualizar estado
            method: 'PUT',
            // Enviar ID del asesor y motivo del rechazo
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                asesorId: asesor.asesorId,
                motivo: motivo
            })
        });
        
        // Procesar respuesta
        if (response.ok) {
            // Éxito
            alert('Solicitud rechazada');
            cargarSolicitudes();
        } else {
            // Error
            alert('Error al rechazar la solicitud');
        }
    } catch (error) { // Error de red u otro
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Lógica de navegación del menú lateral
function initializeMenuToggle() {
    // Toggle del menú lateral
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        // Alternar clase para expandir/colapsar sidebar
        menuToggle.addEventListener('click', function() {
            // Alternar clase CSS
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded');
        });
    }
}

// Cerrar sesión
function logout() {
    // Confirmar cierre de sesión
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        // Remover datos del asesor y redirigir al login
        sessionStorage.removeItem('asesor');
        window.location.href = 'backoffice-login.html';
    }
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    // Cargar solicitudes al cargar la página
    cargarSolicitudes();
});
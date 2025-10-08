const API_CREDIT_URL = 'http://localhost:8082/api/solicitudes';

let solicitudesData = [];

// Verificar autenticación de asesor
function verificarAutenticacion() {
    const asesor = sessionStorage.getItem('asesor');
    
    if (!asesor) {
        window.location.href = 'backoffice-login.html';
        return null;
    }
    
    return JSON.parse(asesor);
}

// Formatear moneda
function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(valor);
}

// Formatear fecha
function formatearFecha(fecha) {
    return new Date(fecha).toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Obtener clase del badge
function getBadgeClass(estado) {
    const clases = {
        'PENDIENTE': 'badge-pendiente',
        'EN_REVISION': 'badge-en-revision',
        'APROBADA': 'badge-aprobada',
        'RECHAZADA': 'badge-rechazada'
    };
    return clases[estado] || 'badge-pendiente';
}

// Obtener texto del estado
function getEstadoTexto(estado) {
    const textos = {
        'PENDIENTE': 'Pendiente',
        'EN_REVISION': 'En Revisión',
        'APROBADA': 'Aprobada',
        'RECHAZADA': 'Rechazada'
    };
    return textos[estado] || estado;
}

// Cargar todas las solicitudes
async function cargarSolicitudes() {
    const asesor = verificarAutenticacion();
    if (!asesor) return;
    
    // Actualizar nombre del asesor
    document.getElementById('userName').textContent = asesor.nombreCompleto;
    
    try {
        // Por ahora obtenemos todas las solicitudes
        // TODO: Crear endpoint GET /api/solicitudes/todas en el backend
        const response = await fetch(`${API_CREDIT_URL}/todas`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            solicitudesData = await response.json();
            actualizarEstadisticas(solicitudesData);
            mostrarSolicitudes(solicitudesData);
        } else {
            mostrarEstadoVacio('Error al cargar solicitudes');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarEstadoVacio('Error al conectar con el servidor');
    }
}

// Actualizar estadísticas
function actualizarEstadisticas(solicitudes) {
    const pendientes = solicitudes.filter(s => s.estado === 'PENDIENTE').length;
    const enRevision = solicitudes.filter(s => s.estado === 'EN_REVISION').length;
    
    // Aprobadas hoy
    const hoy = new Date().toDateString();
    const aprobadasHoy = solicitudes.filter(s => {
        if (s.estado === 'APROBADA' && s.fechaDecision) {
            return new Date(s.fechaDecision).toDateString() === hoy;
        }
        return false;
    }).length;
    
    document.getElementById('statPendientes').textContent = pendientes;
    document.getElementById('statRevision').textContent = enRevision;
    document.getElementById('statAprobadas').textContent = aprobadasHoy;
    document.getElementById('statTotal').textContent = solicitudes.length;
}

// Mostrar solicitudes en la tabla
function mostrarSolicitudes(solicitudes) {
    const tbody = document.getElementById('tablaSolicitudes');
    
    if (solicitudes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>No hay solicitudes para mostrar</p>
                </td>
            </tr>
        `;
        return;
    }
    
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
    const filtro = document.getElementById('filtroEstado').value;
    
    if (filtro === 'TODAS') {
        mostrarSolicitudes(solicitudesData);
    } else {
        const filtradas = solicitudesData.filter(sol => sol.estado === filtro);
        mostrarSolicitudes(filtradas);
    }
}

// Aprobar solicitud
async function aprobarSolicitud(id) {
    if (!confirm('¿Estás seguro de aprobar esta solicitud?')) return;
    
    const asesor = verificarAutenticacion();
    
    try {
        const response = await fetch(`${API_CREDIT_URL}/${id}/aprobar`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                asesorId: asesor.asesorId
            })
        });
        
        if (response.ok) {
            alert('Solicitud aprobada exitosamente');
            cargarSolicitudes(); // Recargar la lista
        } else {
            alert('Error al aprobar la solicitud');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Rechazar solicitud
async function rechazarSolicitud(id) {
    const motivo = prompt('Motivo del rechazo:');
    if (!motivo) return;
    
    const asesor = verificarAutenticacion();
    
    try {
        const response = await fetch(`${API_CREDIT_URL}/${id}/rechazar`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                asesorId: asesor.asesorId,
                motivo: motivo
            })
        });
        
        if (response.ok) {
            alert('Solicitud rechazada');
            cargarSolicitudes();
        } else {
            alert('Error al rechazar la solicitud');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}

// Lógica para alternar la visibilidad del menú lateral (MenuTogle)
function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle'); 
    const sidebar = document.querySelector('.sidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function() {
             sidebar.classList.toggle('sidebar-expanded');
        });
    }
}

// Cerrar sesión (Ya existente, pero la incluimos para asegurar)
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('asesor'); // Usa 'asesor'
        window.location.href = 'backoffice-login.html'; // Redirige al login de Backoffice
    }
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    cargarSolicitudes();
});
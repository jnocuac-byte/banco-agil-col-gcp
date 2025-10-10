const API_CREDIT_URL = 'http://localhost:8083/api/solicitudes';
const API_DOCUMENTOS_URL = 'http://localhost:8083/api/documentos';

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

// Ordenar solicitudes (En Revisión primero, luego Pendientes, luego por fecha de decisión)
function ordenarSolicitudes(solicitudes) {
    return solicitudes.sort((a, b) => {
        // Prioridad de estados
        const prioridad = {
            'EN_REVISION': 1,
            'PENDIENTE': 2,
            'APROBADA': 3,
            'RECHAZADA': 3
        };
        
        const prioridadA = prioridad[a.estado] || 4;
        const prioridadB = prioridad[b.estado] || 4;
        
        if (prioridadA !== prioridadB) {
            return prioridadA - prioridadB;
        }
        
        // Si tienen la misma prioridad, ordenar por fecha
        if (prioridadA === 3) {
            // Usar fechaDecision si existe, sino fechaSolicitud, y ordenar de más reciente a más antigua (b - a)
            return new Date(b.fechaDecision || b.fechaSolicitud) - new Date(a.fechaDecision || a.fechaSolicitud);
        }
        
        // Para pendientes y en revisión, más antiguas primero
        return new Date(a.fechaSolicitud) - new Date(b.fechaSolicitud);
    });
}

// Cargar todas las solicitudes
async function cargarSolicitudes() {
    const asesor = verificarAutenticacion();
    if (!asesor) return;
    
    document.getElementById('userName').textContent = asesor.nombreCompleto;
    
    try {
        const response = await fetch(`${API_CREDIT_URL}/todas`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            solicitudesData = await response.json();
            solicitudesData = ordenarSolicitudes(solicitudesData);
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
    
    tbody.innerHTML = solicitudes.map(sol => {
        const filaId = `fila-${sol.id}`;
        const detallesId = `detalles-${sol.id}`;
        
        return `
            <tr id="${filaId}">
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
                    ${generarBotonesAccion(sol)}
                </td>
            </tr>
            ${sol.estado === 'EN_REVISION' ? `
            <tr id="${detallesId}" class="fila-detalles hidden"> 
                <td colspan="8">
                    <div class="detalles-container">
                        <div class="detalles-section">
                            <h4><i class="fas fa-comment-alt"></i> Observaciones</h4>
                            <p>${sol.observaciones || 'Sin observaciones'}</p>
                        </div>
                        <div class="detalles-section">
                            <h4><i class="fas fa-file-alt"></i> Documentos</h4>
                            <div id="docs-${sol.id}" class="documentos-lista">
                                <i class="fas fa-spinner fa-spin"></i> Cargando documentos...
                            </div>
                        </div>
                        <div class="detalles-actions">
                            <button class="btn-action btn-aprobar" onclick="aprobarSolicitud(${sol.id})">
                                <i class="fas fa-check"></i> Aprobar
                            </button>
                            <button class="btn-action btn-rechazar" onclick="rechazarSolicitud(${sol.id})">
                                <i class="fas fa-times"></i> Rechazar
                            </button>
                        </div>
                    </div>
                </td>
            </tr>
            ` : ''}
        `;
    }).join('');
    
    // Cargar documentos para solicitudes en revisión (aunque estén ocultas, para tenerlas listas)
    solicitudes.filter(s => s.estado === 'EN_REVISION').forEach(sol => {
        cargarDocumentosSolicitud(sol.id);
    });
}

// Generar botones de acción según el estado (MODIFICADO)
function generarBotonesAccion(sol) {
    if (sol.estado === 'PENDIENTE') {
        return `
            <button class="btn-action btn-revisar" onclick="marcarEnRevision(${sol.id})">
                <i class="fas fa-search"></i> Revisar
            </button>
        `;
    } else if (sol.estado === 'EN_REVISION') {
        // Nuevo botón para expandir/colapsar detalles
        return `
            <button class="btn-action btn-toggle-details" onclick="toggleDetallesSolicitud(${sol.id}, this)">
                <i class="fas fa-chevron-down toggle-icon"></i> Detalles
            </button>
        `;
    }
    return '-';
}

// NUEVA FUNCIÓN: Alternar visibilidad de la fila de detalles
function toggleDetallesSolicitud(id, button) {
    const detallesId = `detalles-${id}`;
    const filaDetalles = document.getElementById(detallesId);
    const icon = button.querySelector('.toggle-icon');

    if (filaDetalles.classList.contains('hidden')) {
        // Mostrar detalles
        filaDetalles.classList.remove('hidden');
        button.classList.add('active'); // Opcional: Estilo activo para el botón
        icon.classList.replace('fa-chevron-down', 'fa-chevron-up');
    } else {
        // Ocultar detalles
        filaDetalles.classList.add('hidden');
        button.classList.remove('active');
        icon.classList.replace('fa-chevron-up', 'fa-chevron-down');
    }
}

// Cargar documentos de una solicitud
async function cargarDocumentosSolicitud(solicitudId) {
    try {
        const response = await fetch(`${API_DOCUMENTOS_URL}/solicitud/${solicitudId}`);
        const documentos = await response.json();
        
        const container = document.getElementById(`docs-${solicitudId}`);
        
        if (documentos.length === 0) {
            container.innerHTML = '<p class="sin-documentos">No hay documentos adjuntos</p>';
            return;
        }
        
        container.innerHTML = documentos.map(doc => `
            <div class="documento-item">
                <i class="fas fa-file-pdf"></i>
                <span class="doc-nombre">${doc.nombreArchivo}</span>
                <span class="doc-tipo">${doc.tipoDocumento}</span>
                <a href="${API_DOCUMENTOS_URL}/${doc.id}/descargar" 
                   target="_blank" 
                   class="btn-descargar"
                   title="Descargar documento">
                    <i class="fas fa-download"></i>
                </a>
            </div>
        `).join('');
        
    } catch (error) {
        console.error('Error cargando documentos:', error);
        document.getElementById(`docs-${solicitudId}`).innerHTML = 
            '<p class="error-documentos">Error al cargar documentos</p>';
    }
}

// Marcar solicitud en revisión
async function marcarEnRevision(id) {
    const asesor = verificarAutenticacion();
    
    try {
        const response = await fetch(`${API_CREDIT_URL}/${id}/revision`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                asesorId: asesor.asesorId
            })
        });
        
        if (response.ok) {
            // Cambiar filtro a EN_REVISION automáticamente
            document.getElementById('filtroEstado').value = 'EN_REVISION';
            
            // Recargar solicitudes
            await cargarSolicitudes();
            
            // Aplicar filtro
            filtrarSolicitudes();
        } else {
            alert('Error al marcar solicitud en revisión');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
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
                asesorId: asesor.asesorId // Aquí viene el ID del asesor desde sessionStorage
            })
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert(`¡Solicitud aprobada!\n\nCrédito ID: ${result.creditoId}\nCuota mensual: ${formatearMoneda(result.cuotaMensual)}\nNuevo saldo: ${formatearMoneda(result.nuevoSaldo)}`);
            cargarSolicitudes();
        } else {
            alert('Error: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al aprobar: ' + error.message);
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

// Lógica de navegación del menú lateral
function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded');
        });
    }
}

// Cerrar sesión
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('asesor');
        window.location.href = 'backoffice-login.html';
    }
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    initializeMenuToggle();
    cargarSolicitudes();
});
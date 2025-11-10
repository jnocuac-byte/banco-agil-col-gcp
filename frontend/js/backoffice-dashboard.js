// backend-dashboard.js
const API_CREDIT_URL = 'https://credit-service-514751056677.us-central1.run.app/api/solicitudes';
const API_DOCUMENTOS_URL = 'https://credit-service-514751056677.us-central1.run.app/api/documentos';

// Estado global de solicitudes
let solicitudesData = [];

// Verificar autenticación de asesor
function verificarAutenticacion() {
    // Verificar si el asesor está en sessionStorage
    const asesor = sessionStorage.getItem('asesor');
    // Redirigir a login si no está autenticado
    if (!asesor) {
        window.location.href = 'backoffice-login.html'; // Redirigir a login
        return null; // Detener ejecución
    }
    return JSON.parse(asesor); // Devolver objeto asesor
}

// Formatear moneda
function formatearMoneda(valor) {
    // Formato de moneda colombiana (COP)
    return new Intl.NumberFormat('es-CO', {
        // Configuración de formato
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(valor); // Devolver valor formateado
}

// Formatear fecha
function formatearFecha(fecha) {
    // Formato de fecha en español (Colombia)
    return new Date(fecha).toLocaleDateString('es-CO', { // Configuración de formato
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Obtener clase del badge
function getBadgeClass(estado) {
    // Clases CSS para cada estado
    const clases = {
        'PENDIENTE': 'badge-pendiente',
        'EN_REVISION': 'badge-en-revision',
        'APROBADA': 'badge-aprobada',
        'RECHAZADA': 'badge-rechazada'
    };
    return clases[estado] || 'badge-pendiente'; // Clase por defecto
}

// Obtener texto del estado
function getEstadoTexto(estado) {
    // Textos legibles para cada estado
    const textos = {
        'PENDIENTE': 'Pendiente',
        'EN_REVISION': 'En Revisión',
        'APROBADA': 'Aprobada',
        'RECHAZADA': 'Rechazada'
    };
    return textos[estado] || estado; // Texto por defecto
}

// Ordenar solicitudes (En Revisión primero, luego Pendientes, luego por fecha de decisión)
function ordenarSolicitudes(solicitudes) {
    // Ordenar según prioridad y fecha
    return solicitudes.sort((a, b) => {
        // Prioridad de estados
        const prioridad = {
            'EN_REVISION': 1,
            'PENDIENTE': 2,
            'APROBADA': 3,
            'RECHAZADA': 3
        };
        
        // Obtener prioridad de cada solicitud
        const prioridadA = prioridad[a.estado] || 4;
        const prioridadB = prioridad[b.estado] || 4;
        
        // Ordenar por prioridad primero
        if (prioridadA !== prioridadB) {
            return prioridadA - prioridadB; // Menor prioridad primero
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
    // Verificar autenticación
    const asesor = verificarAutenticacion();
    if (!asesor) return;
    
    // Mostrar nombre del asesor
    document.getElementById('userName').textContent = asesor.nombreCompleto;
    
    // Obtener solicitudes desde la API
    try {
        // Realizar petición GET
        const response = await fetch(`${API_CREDIT_URL}/todas`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }); // Esperar respuesta
        
        // Procesar respuesta
        if (response.ok) {
            solicitudesData = await response.json(); // Obtener datos JSON
            solicitudesData = ordenarSolicitudes(solicitudesData); // Ordenar solicitudes
            actualizarEstadisticas(solicitudesData); // Actualizar estadísticas
            mostrarSolicitudes(solicitudesData); // Mostrar en tabla
        } else { // Manejar error
            console.error('Error al cargar solicitudes:', response.statusText); // Log del error
            mostrarEstadoVacio('Error al cargar solicitudes'); // Mostrar mensaje de error
        }
    } catch (error) { // Manejar error de red
        console.error('Error:', error); // Log del error
        mostrarEstadoVacio('Error al conectar con el servidor'); // Mostrar mensaje de error
    }
}

// Actualizar estadísticas
function actualizarEstadisticas(solicitudes) {
    // Calcular estadísticas
    const pendientes = solicitudes.filter(s => s.estado === 'PENDIENTE').length;
    const enRevision = solicitudes.filter(s => s.estado === 'EN_REVISION').length;
    
    // Contar aprobadas hoy
    const hoy = new Date().toDateString();
    const aprobadasHoy = solicitudes.filter(s => { // Solo contar si fue aprobada hoy
        if (s.estado === 'APROBADA' && s.fechaDecision) { // Asegurarse de que fechaDecision exista
            return new Date(s.fechaDecision).toDateString() === hoy; // Comparar fechas
        }
        return false; // No contar si no es aprobada o no tiene fechaDecision
    }).length; // Contar aprobadas hoy
    
    // Actualizar elementos del DOM
    document.getElementById('statPendientes').textContent = pendientes;
    document.getElementById('statRevision').textContent = enRevision;
    document.getElementById('statAprobadas').textContent = aprobadasHoy;
    document.getElementById('statTotal').textContent = solicitudes.length;
}

// Mostrar solicitudes en la tabla
function mostrarSolicitudes(solicitudes) {
    // Obtener referencia al tbody
    const tbody = document.getElementById('tablaSolicitudes');
    
    // Manejar estado vacío
    if (solicitudes.length === 0) { // Si no hay solicitudes
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
    
    // Generar filas de la tabla
    tbody.innerHTML = solicitudes.map(sol => {
        // IDs únicos para filas y detalles
        const filaId = `fila-${sol.id}`;
        const detallesId = `detalles-${sol.id}`;
        
        // Generar HTML de la fila
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
    }).join(''); // Unir todas las filas
    
    // Cargar documentos para solicitudes en revisión (aunque estén ocultas, para tenerlas listas)
    solicitudes.filter(s => s.estado === 'EN_REVISION').forEach(sol => {
        cargarDocumentosSolicitud(sol.id); // Cargar documentos
    });
}

// Generar botones de acción según el estado (MODIFICADO)
function generarBotonesAccion(sol) {
    // Botones según estado
    if (sol.estado === 'PENDIENTE') {
        return `
            <button class="btn-action btn-revisar" onclick="marcarEnRevision(${sol.id})">
                <i class="fas fa-search"></i> Revisar
            </button>
        `;
    } else if (sol.estado === 'EN_REVISION') { // Si está en revisión
        // Nuevo botón para expandir/colapsar detalles
        return `
            <button class="btn-action btn-toggle-details" onclick="toggleDetallesSolicitud(${sol.id}, this)">
                <i class="fas fa-chevron-down toggle-icon"></i> Detalles
            </button>
        `;
    }
    return '-'; // No hay acciones para otros estados
}

// NUEVA FUNCIÓN: Alternar visibilidad de la fila de detalles
function toggleDetallesSolicitud(id, button) {
    const detallesId = `detalles-${id}`; // ID de la fila de detalles
    const filaDetalles = document.getElementById(detallesId); // Obtener fila de detalles
    const icon = button.querySelector('.toggle-icon'); // Obtener icono del botón

    // Alternar clase 'hidden' para mostrar/ocultar detalles y cambiar icono
    if (filaDetalles.classList.contains('hidden')) {
        // Mostrar detalles
        filaDetalles.classList.remove('hidden'); // Quitar clase hidden
        button.classList.add('active'); // Opcional: Estilo activo para el botón
        icon.classList.replace('fa-chevron-down', 'fa-chevron-up'); // Cambiar icono
    } else {
        // Ocultar detalles
        filaDetalles.classList.add('hidden'); // Añadir clase hidden
        button.classList.remove('active'); // Quitar estilo activo
        icon.classList.replace('fa-chevron-up', 'fa-chevron-down'); // Cambiar icono
    }
}

// Cargar documentos de una solicitud
async function cargarDocumentosSolicitud(solicitudId) {
    // Obtener documentos desde la API
    try {
        const response = await fetch(`${API_DOCUMENTOS_URL}/solicitud/${solicitudId}`); // Esperar respuesta
        const documentos = await response.json(); // Obtener datos JSON
        
        // Mostrar documentos en el contenedor correspondiente
        const container = document.getElementById(`docs-${solicitudId}`);
        
        // Manejar caso sin documentos
        if (documentos.length === 0) {
            container.innerHTML = '<p class="sin-documentos">No hay documentos adjuntos</p>'; // Mostrar mensaje
            return; // Salir de la función
        }
        
        // Generar HTML de documentos
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
        `).join(''); // Unir todos los documentos
        
    } catch (error) { // Manejar error de red
        console.error('Error cargando documentos:', error); // Log del error
        document.getElementById(`docs-${solicitudId}`).innerHTML =  // Mostrar mensaje de error
            '<p class="error-documentos">Error al cargar documentos</p>';
    }
}

// Marcar solicitud en revisión
async function marcarEnRevision(id) {
    // Confirmar acción
    const asesor = verificarAutenticacion();
    
    // Realizar petición PUT para marcar en revisión
    try {
        const response = await fetch(`${API_CREDIT_URL}/${id}/revision`, { // Endpoint para marcar en revisión
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ // Enviar ID del asesor
                asesorId: asesor.asesorId // Aquí viene el ID del asesor desde sessionStorage
            })
        });
        
        // Procesar respuesta
        if (response.ok) {
            // Cambiar filtro a EN_REVISION automáticamente
            document.getElementById('filtroEstado').value = 'EN_REVISION';
            
            // Recargar solicitudes
            await cargarSolicitudes();
            
            // Aplicar filtro
            filtrarSolicitudes();
        } else {
            alert('Error al marcar solicitud en revisión'); // Mostrar mensaje de error
        }
    } catch (error) { // Manejar error de red
        console.error('Error:', error); // Log del error
        alert('Error al conectar con el servidor'); // Mostrar mensaje de error
    }
}

// Aprobar solicitud
async function aprobarSolicitud(id) {
    // Confirmar acción
    if (!confirm('¿Estás seguro de aprobar esta solicitud?')) return;

    // Obtener ID del asesor
    const asesor = verificarAutenticacion();
    
    // Realizar petición PUT para aprobar
    try {
        const response = await fetch(`${API_CREDIT_URL}/${id}/aprobar`, { // Endpoint para aprobar
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ // Enviar ID del asesor
                asesorId: asesor.asesorId // Aquí viene el ID del asesor desde sessionStorage
            })
        });
        
        // Procesar respuesta
        const result = await response.json();
        
        // Mostrar mensaje según resultado
        if (result.success) {
            // Mostrar detalles del crédito aprobado
            alert(`¡Solicitud aprobada!\n\nCrédito ID: ${result.creditoId}\nCuota mensual: ${formatearMoneda(result.cuotaMensual)}\nNuevo saldo: ${formatearMoneda(result.nuevoSaldo)}`);
            cargarSolicitudes(); // Recargar solicitudes
        } else { // Mostrar error
            alert('Error: ' + result.message); // Mostrar mensaje de error
        }
    } catch (error) { // Manejar error de red
        console.error('Error:', error); // Log del error
        alert('Error al aprobar: ' + error.message); // Mostrar mensaje de error
    }
}

// Rechazar solicitud
async function rechazarSolicitud(id) {
    // Confirmar acción y pedir motivo
    const motivo = prompt('Motivo del rechazo:');
    if (!motivo) return;
    
    // Obtener ID del asesor
    const asesor = verificarAutenticacion();
    
    // Realizar petición PUT para rechazar
    try {
        const response = await fetch(`${API_CREDIT_URL}/${id}/rechazar`, { // Endpoint para rechazar
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ // Enviar ID del asesor y motivo
                asesorId: asesor.asesorId,
                motivo: motivo
            })
        });
        
        // Procesar respuesta
        if (response.ok) {
            alert('Solicitud rechazada'); // Mostrar mensaje de éxito
            cargarSolicitudes(); // Recargar solicitudes
        } else { // Mostrar error
            alert('Error al rechazar la solicitud'); // Mostrar mensaje de error
        }
    } catch (error) { // Manejar error de red
        console.error('Error:', error);  // Log del error
        alert('Error al conectar con el servidor'); // Mostrar mensaje de error
    }
}

// Filtrar solicitudes
function filtrarSolicitudes() {
    // Obtener valor del filtro
    const filtro = document.getElementById('filtroEstado').value;
    
    // Filtrar y mostrar solicitudes
    if (filtro === 'TODAS') {
        mostrarSolicitudes(solicitudesData); // Mostrar todas
    } else { // Filtrar por estado
        const filtradas = solicitudesData.filter(sol => sol.estado === filtro); // Filtrar solicitudes
        mostrarSolicitudes(filtradas); // Mostrar filtradas
    }
}

// Mostrar estado vacío
function mostrarEstadoVacio(mensaje) {
    // Mostrar mensaje en la tabla
    const tbody = document.getElementById('tablaSolicitudes');
    // Mensaje personalizado
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
    // Toggle del menú
    const menuToggle = document.getElementById('menuToggle');
    // Añadir evento click
    if (menuToggle) {
        menuToggle.addEventListener('click', function() { // Al hacer click
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded'); // Alternar clase
        });
    }
}

// Cerrar sesión
function logout() {
    // Confirmar cierre de sesión
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('asesor'); // Eliminar asesor de sessionStorage
        window.location.href = 'backoffice-login.html'; // Redirigir a login
    }
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    initializeMenuToggle(); // Inicializar toggle del menú
    cargarSolicitudes(); // Cargar solicitudes al inicio
});
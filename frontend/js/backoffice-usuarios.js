// Variables globales
const API_BASE_URL = 'http://localhost:8081/api';

let clientesData = []; // Almacenar datos completos de clientes
let asesoresData = []; // Almacenar datos completos de asesores
let currentTab = 'clientes'; // Pestaña actual
let token = null; // Token de autenticación

// Inicializar la aplicación
document.addEventListener('DOMContentLoaded', () => {
    // Obtener token del sessionStorage
    token = sessionStorage.getItem('token');
    
    // Si no hay token, redirigir a login
    cargarClientes();
    cargarAsesores();
    
    // Inicializar menú lateral
    document.getElementById('searchClientes')?.addEventListener('input', filtrarClientesLocal);
    document.getElementById('searchAsesores')?.addEventListener('input', filtrarAsesoresLocal);
});

// Cambiar pestaña
function cambiarTab(tab) {
    currentTab = tab; // Actualizar pestaña actual
    
    // Actualizar clases activas
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    event.target.closest('.tab-button').classList.add('active');
    
    // Mostrar contenido correspondiente
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    document.getElementById(`tab-${tab}`).classList.add('active');
}

// Toggle de secciones colapsables
function toggleCollapsible(header) {
    header.classList.toggle('collapsed'); // Alternar clase
    const content = header.nextElementSibling; // Contenido siguiente
    content.classList.toggle('collapsed'); // Alternar clase
}

// Cargar clientes desde la API
async function cargarClientes() {
    // Mostrar loading
    try {
        // Petición GET a /usuarios/clientes
        const response = await fetch(`${API_BASE_URL}/usuarios/clientes`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cargar clientes');

        // Parsear respuesta JSON
        clientesData = await response.json();
        renderizarClientes(clientesData); // Renderizar clientes
        actualizarEstadisticasClientes(); // Actualizar estadísticas

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error

        // Mostrar mensaje de error en la tabla
        document.getElementById('tablaClientes').innerHTML = `
            <tr>
                <td colspan="8" class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Error al cargar clientes</p>
                </td>
            </tr>
        `;
    }
}

// Renderizar clientes en la tabla
function renderizarClientes(clientes) {
    // Obtener referencia al tbody
    const tbody = document.getElementById('tablaClientes');
    
    // Si no hay clientes, mostrar estado vacío
    if (clientes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-state">
                    <i class="fas fa-users"></i>
                    <p>No hay clientes registrados</p>
                </td>
            </tr>
        `;
        return; // Salir de la función
    }
    
    // Mapear clientes a filas HTML
    tbody.innerHTML = clientes.map(cliente => `
        <tr>
            <td>${cliente.clienteId}</td>
            <td>${cliente.nombreCompleto}</td>
            <td>${cliente.documento}</td>
            <td>${cliente.email}</td>
            <td>${cliente.telefono || 'N/A'}</td>
            <td><span class="status-badge badge-rol">${cliente.tipoCliente === 'PERSONA_NATURAL' ? 'Persona Natural' : 'Empresa'}</span></td>
            <td><span class="status-badge status-${cliente.activo ? 'activo' : 'inactivo'}">${cliente.activo ? 'ACTIVO' : 'INACTIVO'}</span></td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action btn-view" onclick="verDetalleCliente(${cliente.clienteId})" title="Ver detalles">
                        <i class="fas fa-eye"></i> Ver
                    </button>
                    <button class="btn-action btn-block" onclick="cambiarEstadoCliente(${cliente.clienteId}, ${cliente.activo})" title="Cambiar estado">
                        <i class="fas fa-ban"></i> Estado
                    </button>
                </div>
            </td>
        </tr>
    `).join(''); // Unir filas en un solo string
}

// Filtrar clientes localmente
function filtrarClientesLocal() {
    // Obtener término de búsqueda
    const searchTerm = document.getElementById('searchClientes').value.toLowerCase();
    
    // Si no hay término, mostrar todos
    if (!searchTerm) {
        renderizarClientes(clientesData); // Renderizar todos los clientes
        return; // Salir de la función
    }
    
    // Filtrar clientes que coincidan con el término
    const filtrados = clientesData.filter(cliente => 
        cliente.nombreCompleto.toLowerCase().includes(searchTerm) || // Buscar por nombre
        cliente.documento.includes(searchTerm) || // Buscar por documento
        cliente.email.toLowerCase().includes(searchTerm) // Buscar por email
    );
    
    // Renderizar clientes filtrados
    renderizarClientes(filtrados);
}

// Buscar clientes (invoca el filtrado local)
function buscarClientes() {
    filtrarClientesLocal(); // Filtrar clientes localmente
}

// Actualizar estadísticas de clientes
function actualizarEstadisticasClientes() {
    // Calcular estadísticas
    const total = clientesData.length; // Total de clientes
    const activos = clientesData.filter(c => c.activo).length; // Clientes activos
    const inactivos = total - activos; // Clientes inactivos
    const personasNaturales = clientesData.filter(c => c.tipoCliente === 'PERSONA_NATURAL').length; // Personas naturales
    const empresas = clientesData.filter(c => c.tipoCliente === 'EMPRESA').length; // Empresas
    
    // Actualizar elementos del DOM
    document.getElementById('statTotalClientes').textContent = total;
    document.getElementById('statClientesActivos').textContent = activos;
    document.getElementById('statClientesInactivos').textContent = inactivos;
    document.getElementById('statPersonasNaturales').textContent = personasNaturales;
    document.getElementById('statEmpresas').textContent = empresas;
}

// Cargar asesores desde la API
async function cargarAsesores() {
    // Mostrar loading
    try {
        // Petición GET a /usuarios/asesores
        const response = await fetch(`${API_BASE_URL}/usuarios/asesores`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cargar asesores');

        // Parsear respuesta JSON
        asesoresData = await response.json();
        renderizarAsesores(asesoresData); // Renderizar asesores
        actualizarEstadisticasAsesores(); // Actualizar estadísticas

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error

        // Mostrar mensaje de error en la tabla
        document.getElementById('tablaAsesores').innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Error al cargar asesores</p>
                </td>
            </tr>
        `;
    }
}

// Renderizar asesores en la tabla
function renderizarAsesores(asesores) {
    // Obtener referencia al tbody
    const tbody = document.getElementById('tablaAsesores');
    
    // Si no hay asesores, mostrar estado vacío
    if (asesores.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-user-tie"></i>
                    <p>No hay asesores registrados</p>
                </td>
            </tr>
        `;
        return; // Salir de la función
    }
    
    // Mapear asesores a filas HTML
    tbody.innerHTML = asesores.map(asesor => `
        <tr>
            <td>${asesor.asesorId}</td>
            <td>${asesor.nombreCompleto}</td>
            <td>${asesor.codigoEmpleado || 'N/A'}</td>
            <td>${asesor.email}</td>
            <td><span class="status-badge badge-rol">${asesor.area || 'N/A'}</span></td>
            <td><span class="status-badge status-${asesor.activo ? 'activo' : 'inactivo'}">${asesor.activo ? 'ACTIVO' : 'INACTIVO'}</span></td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action btn-view" onclick="verDetalleAsesor(${asesor.asesorId})" title="Ver detalles">
                        <i class="fas fa-eye"></i> Ver
                    </button>
                    <button class="btn-action btn-block" onclick="cambiarEstadoAsesor(${asesor.asesorId}, ${asesor.activo})" title="Cambiar estado">
                        <i class="fas fa-ban"></i> Estado
                    </button>
                </div>
            </td>
        </tr>
    `).join(''); // Unir filas en un solo string
}

// Filtrar asesores localmente
function filtrarAsesoresLocal() {
    // Obtener término de búsqueda
    const searchTerm = document.getElementById('searchAsesores').value.toLowerCase();
    
    // Si no hay término, mostrar todos
    if (!searchTerm) {
        renderizarAsesores(asesoresData); // Renderizar todos los asesores
        return;
    }

    // Filtrar asesores por nombre o email
    const filtrados = asesoresData.filter(asesor => 
        asesor.nombreCompleto.toLowerCase().includes(searchTerm) || // Buscar por nombre
        asesor.email.toLowerCase().includes(searchTerm) // Buscar por email
    );
    
    // Renderizar asesores filtrados
    renderizarAsesores(filtrados);
}

// Buscar asesores (invoca el filtrado local)
function buscarAsesores() {
    filtrarAsesoresLocal(); // Filtrar asesores localmente
}

// Actualizar estadísticas de asesores
function actualizarEstadisticasAsesores() {
    const total = asesoresData.length; // Total de asesores
    const activos = asesoresData.filter(a => a.activo).length; // Asesores activos
    const inactivos = total - activos; // Asesores inactivos
    
    // Actualizar elementos del DOM
    document.getElementById('statTotalAsesores').textContent = total;
    document.getElementById('statAsesoresActivos').textContent = activos;
    document.getElementById('statAsesoresInactivos').textContent = inactivos;
}

// Ver detalles de un cliente
async function verDetalleCliente(id) {
    // Mostrar loading
    try {
        const response = await fetch(`${API_BASE_URL}/usuarios/clientes/${id}`, { // Endpoint para obtener detalles del cliente
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        }); 

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cargar detalles');

        // Parsear respuesta JSON
        const cliente = await response.json();

        // Mostrar detalles en el modal
        const modalBody = document.getElementById('modalBody');
        // Construir contenido HTML
        modalBody.innerHTML = `
            <div class="detail-grid">
                <div class="detail-item">
                    <label>ID Cliente</label>
                    <span>${cliente.clienteId}</span>
                </div>
                <div class="detail-item">
                    <label>Nombre Completo</label>
                    <span>${cliente.nombreCompleto}</span>
                </div>
                <div class="detail-item">
                    <label>Documento/NIT</label>
                    <span>${cliente.documento}</span>
                </div>
                <div class="detail-item">
                    <label>Email</label>
                    <span>${cliente.email}</span>
                </div>
                <div class="detail-item">
                    <label>Teléfono</label>
                    <span>${cliente.telefono || 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Ciudad</label>
                    <span>${cliente.ciudad || 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Dirección</label>
                    <span>${cliente.direccion || 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Tipo Cliente</label>
                    <span>${cliente.tipoCliente === 'PERSONA_NATURAL' ? 'Persona Natural' : 'Empresa'}</span>
                </div>
                <div class="detail-item">
                    <label>Estado</label>
                    <span class="status-badge status-${cliente.activo ? 'activo' : 'inactivo'}">${cliente.activo ? 'ACTIVO' : 'INACTIVO'}</span>
                </div>
                <div class="detail-item">
                    <label>Fecha Registro</label>
                    <span>${new Date(cliente.fechaCreacion).toLocaleDateString('es-CO')}</span>
                </div>
            </div>
            
            <div class="solicitudes-history">
                <h3>📋 Historial de Solicitudes</h3>
                <div id="historialSolicitudes">
                    <p style="text-align: center; color: #999;">Cargando historial...</p>
                </div>
            </div>
        `;
        
        // Actualizar título y mostrar modal
        document.getElementById('modalTitulo').textContent = `Detalles del Cliente - ${cliente.nombreCompleto}`;
        document.getElementById('modalDetalles').classList.add('show');
        
        // Cargar historial de solicitudes
        cargarHistorialSolicitudes(cliente.clienteId);

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        alert('Error al cargar los detalles del cliente'); // Mostrar alerta de error
    }
}

// Cargar historial de solicitudes de un cliente
async function cargarHistorialSolicitudes(clienteId) {
    // Mostrar loading
    try {
        // Petición GET a /usuarios/clientes/{id}/solicitudes
        const response = await fetch(`${API_BASE_URL}/usuarios/clientes/${clienteId}/solicitudes`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cargar historial');

        // Parsear respuesta JSON
        const solicitudes = await response.json();
        const container = document.getElementById('historialSolicitudes');
        
        // Si no hay solicitudes, mostrar estado vacío
        if (solicitudes.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #999;">No hay solicitudes registradas</p>'; // Mensaje de no hay solicitudes
            return;
        }
        
        // Mapear solicitudes a elementos HTML
        container.innerHTML = solicitudes.map(sol => `
            <div class="history-item">
                <strong>Solicitud #${sol.id}</strong> - ${formatCurrency(sol.montoSolicitado)}
                <br>
                <small>Plazo: ${sol.plazoMeses} meses | Tasa: ${sol.tasaInteres}% | Estado: <span class="status-badge status-${sol.estado.toLowerCase()}">${sol.estado}</span></small>
                <br>
                <small>Fecha: ${new Date(sol.fechaSolicitud).toLocaleDateString('es-CO')}</small>
            </div>
        `).join(''); // Unir elementos en un solo string

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        document.getElementById('historialSolicitudes').innerHTML = '<p style="text-align: center; color: #999;">Error al cargar historial</p>'; // Mostrar mensaje de error
    }
}

// Ver detalles de un asesor
async function verDetalleAsesor(id) {
    // Mostrar loading
    try {
        // Petición GET a /usuarios/asesores/{id}
        const response = await fetch(`${API_BASE_URL}/usuarios/asesores/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cargar detalles');

        // Parsear respuesta JSON
        const asesor = await response.json();
        
        // Mostrar detalles en el modal
        const modalBody = document.getElementById('modalBody');
        // Construir contenido HTML
        modalBody.innerHTML = `
            <div class="detail-grid">
                <div class="detail-item">
                    <label>ID Asesor</label>
                    <span>${asesor.asesorId}</span>
                </div>
                <div class="detail-item">
                    <label>Nombre Completo</label>
                    <span>${asesor.nombreCompleto}</span>
                </div>
                <div class="detail-item">
                    <label>Código Empleado</label>
                    <span>${asesor.codigoEmpleado || 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Email</label>
                    <span>${asesor.email}</span>
                </div>
                <div class="detail-item">
                    <label>Área</label>
                    <span>${asesor.area || 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Estado</label>
                    <span class="status-badge status-${asesor.activo ? 'activo' : 'inactivo'}">${asesor.activo ? 'ACTIVO' : 'INACTIVO'}</span>
                </div>
            </div>
            
            <div class="solicitudes-history">
                <h3>📊 Estadísticas de Rendimiento</h3>
                <div id="estadisticasAsesor">
                    <p style="text-align: center; color: #999;">Cargando estadísticas...</p>
                </div>
            </div>
        `;
        
        // Actualizar título y mostrar modal
        document.getElementById('modalTitulo').textContent = `Detalles del Asesor - ${asesor.nombreCompleto}`;
        document.getElementById('modalDetalles').classList.add('show');
        
        // Cargar estadísticas del asesor
        cargarEstadisticasAsesor(asesor.asesorId);

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        alert('Error al cargar los detalles del asesor'); // Mostrar alerta de error
    }
}

// Cargar estadísticas de un asesor
async function cargarEstadisticasAsesor(asesorId) {
    // Mostrar loading
    try {
        // Petición GET a /usuarios/asesores/{id}/estadisticas
        const response = await fetch(`${API_BASE_URL}/usuarios/asesores/${asesorId}/estadisticas`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cargar estadísticas');

        // Parsear respuesta JSON
        const stats = await response.json();
        const container = document.getElementById('estadisticasAsesor');
        
        // Mostrar estadísticas en formato de tarjetas
        container.innerHTML = `
            <div class="mini-stats-grid">
                <div class="mini-stat-card">
                    <div class="mini-stat-icon" style="background: #d4e9d7; color: #2d6a4f;">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="mini-stat-info">
                        <h4>Solicitudes Procesadas</h4>
                        <p>${stats.totalProcesadas || 0}</p>
                    </div>
                </div>
                <div class="mini-stat-card">
                    <div class="mini-stat-icon" style="background: #dce7f5; color: #3d5a80;">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="mini-stat-info">
                        <h4>Aprobadas</h4>
                        <p>${stats.aprobadas || 0}</p>
                    </div>
                </div>
            </div>
        `;

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        document.getElementById('estadisticasAsesor').innerHTML = '<p style="text-align: center; color: #999;">Error al cargar estadísticas</p>'; // Mostrar mensaje de error
    }
}

// Cambiar estado (activar/desactivar) de un cliente
async function cambiarEstadoCliente(clienteId, estadoActual) {
    const nuevoEstado = !estadoActual; // Invertir estado
    const mensaje = nuevoEstado ? 'activar' : 'desactivar'; // Mensaje según el nuevo estado
    
    // Confirmar acción
    if (!confirm(`¿Está seguro que desea ${mensaje} este cliente?`)) {
        return;
    }

    // Mostrar loading
    try {
        // Petición PUT a /usuarios/clientes/{id}/estado
        const response = await fetch(`${API_BASE_URL}/usuarios/clientes/${clienteId}/estado`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ activo: nuevoEstado }) // Enviar nuevo estado en el cuerpo
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cambiar estado');

        // Parsear respuesta JSON
        const result = await response.json();
        
        // Manejar respuesta
        if (result.success) {
            alert(`Cliente ${nuevoEstado ? 'activado' : 'desactivado'} correctamente`); // Mostrar mensaje de éxito
            cargarClientes(); // Recargar lista de clientes
        } else { // Error al cambiar estado
            alert(result.message || 'Error al cambiar estado'); // Mostrar mensaje de error
        }

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        alert('Error al cambiar el estado del cliente'); // Mostrar alerta de error
    }
}

// Cambiar estado (activar/desactivar) de un asesor
async function cambiarEstadoAsesor(asesorId, estadoActual) {
    const nuevoEstado = !estadoActual; // Invertir estado
    const mensaje = nuevoEstado ? 'activar' : 'desactivar'; // Mensaje según el nuevo estado
    
    // Confirmar acción
    if (!confirm(`¿Está seguro que desea ${mensaje} este asesor?`)) {
        return;
    }
    
    // Mostrar loading
    try {
        // Petición PUT a /usuarios/asesores/{id}/estado
        const response = await fetch(`${API_BASE_URL}/usuarios/asesores/${asesorId}/estado`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ activo: nuevoEstado }) // Enviar nuevo estado en el cuerpo
        });

        // Verificar respuesta
        if (!response.ok) throw new Error('Error al cambiar estado');

        // Parsear respuesta JSON
        const result = await response.json();
        
        // Manejar respuesta
        if (result.success) {
            alert(`Asesor ${nuevoEstado ? 'activado' : 'desactivado'} correctamente`); // Mostrar mensaje de éxito
            cargarAsesores(); // Recargar lista de asesores
        } else { // Error al cambiar estado
            alert(result.message || 'Error al cambiar estado'); // Mostrar mensaje de error
        }

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        alert('Error al cambiar el estado del asesor'); // Mostrar alerta de error
    }
}

// Cerrar modal de detalles
function cerrarModal() {
    document.getElementById('modalDetalles').classList.remove('show'); // Ocultar modal
}

// Formatear número como moneda COP
function formatCurrency(amount) {
    // Usar Intl.NumberFormat para formatear
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(amount); // Retornar cadena formateada
}

// Cerrar modal al hacer clic fuera del contenido
document.addEventListener('click', function(e) {
    // Verificar si el clic fue fuera del contenido del modal
    const modal = document.getElementById('modalDetalles');
    // Si el modal está abierto y el clic fue en el fondo
    if (e.target === modal) {
        cerrarModal(); // Cerrar modal
    }
});

window.cambiarTab = cambiarTab; // Exponer función globalmente
window.toggleCollapsible = toggleCollapsible; // Exponer función globalmente
window.buscarClientes = buscarClientes; // Exponer función globalmente
window.buscarAsesores = buscarAsesores; // Exponer función globalmente
window.verDetalleCliente = verDetalleCliente; // Exponer función globalmente
window.verDetalleAsesor = verDetalleAsesor; // Exponer función globalmente
window.cambiarEstadoCliente = cambiarEstadoCliente; // Exponer función globalmente
window.cambiarEstadoAsesor = cambiarEstadoAsesor; // Exponer función globalmente
window.cerrarModal = cerrarModal; // Exponer función globalmente
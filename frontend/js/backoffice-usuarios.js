// =========================================
// BACKOFFICE - GESTIÓN DE USUARIOS
// =========================================

// Variables globales
let clientesData = [];
let asesoresData = [];
let currentTab = 'clientes';

// =========================================
// DATOS MOCKUP (temporal hasta integrar con backend)
// =========================================

// Generar datos de clientes de ejemplo
function generarClientesMockup() {
    const nombres = ['Juan Pérez', 'María García', 'Carlos López', 'Ana Martínez', 'Luis Rodríguez', 
                     'Sofia Torres', 'Diego Ramírez', 'Laura Sánchez', 'Pedro Gómez', 'Carmen Díaz'];
    const estados = ['ACTIVO', 'ACTIVO', 'ACTIVO', 'ACTIVO', 'INACTIVO', 'ACTIVO', 'BLOQUEADO', 'ACTIVO', 'ACTIVO', 'INACTIVO'];
    
    return nombres.map((nombre, index) => ({
        id: 1000 + index,
        nombreCompleto: nombre,
        documento: `100${index}567890`,
        email: `${nombre.toLowerCase().replace(' ', '.')}@email.com`,
        telefono: `300${Math.floor(Math.random() * 10000000)}`,
        estado: estados[index],
        fechaRegistro: `2024-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
        solicitudesActivas: Math.floor(Math.random() * 3),
        solicitudesHistoricas: Math.floor(Math.random() * 10)
    }));
}

// Generar datos de asesores de ejemplo
function generarAsesoresMockup() {
    const nombres = ['Roberto Castillo', 'Patricia Moreno', 'Fernando Ruiz', 'Gabriela Ortiz', 'Miguel Herrera'];
    const roles = ['Asesor Senior', 'Asesor Junior', 'Supervisor', 'Asesor Senior', 'Gerente'];
    const estados = ['ACTIVO', 'ACTIVO', 'ACTIVO', 'INACTIVO', 'ACTIVO'];
    
    return nombres.map((nombre, index) => ({
        id: 5000 + index,
        nombreCompleto: nombre,
        documento: `200${index}123456`,
        email: `${nombre.toLowerCase().replace(' ', '.')}@bancoagilcol.com`,
        rol: roles[index],
        estado: estados[index],
        fechaIngreso: `2023-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-01`,
        solicitudesProcesadas: Math.floor(Math.random() * 100) + 50,
        tasaAprobacion: (Math.random() * 20 + 80).toFixed(1) + '%'
    }));
}

// Generar historial de solicitudes para un cliente
function generarHistorialSolicitudes(clienteId) {
    const numSolicitudes = Math.floor(Math.random() * 5) + 1;
    const estados = ['APROBADA', 'RECHAZADA', 'APROBADA', 'PENDIENTE'];
    const productos = ['Crédito Personal', 'Crédito Vehicular', 'Tarjeta de Crédito', 'Crédito Hipotecario'];
    
    return Array.from({ length: numSolicitudes }, (_, i) => ({
        id: clienteId * 100 + i,
        producto: productos[Math.floor(Math.random() * productos.length)],
        monto: (Math.floor(Math.random() * 50) + 10) * 1000000,
        estado: estados[Math.floor(Math.random() * estados.length)],
        fecha: `2024-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`
    }));
}

// =========================================
// FUNCIONES DE INICIALIZACIÓN
// =========================================

document.addEventListener('DOMContentLoaded', () => {
    // Cargar datos mockup
    clientesData = generarClientesMockup();
    asesoresData = generarAsesoresMockup();
    
    // Cargar datos iniciales
    cargarClientes();
    actualizarEstadisticasClientes();
    actualizarEstadisticasAsesores();
    
    // Configurar búsqueda en tiempo real
    document.getElementById('searchClientes')?.addEventListener('input', buscarClientes);
    document.getElementById('searchAsesores')?.addEventListener('input', buscarAsesores);
});

// =========================================
// FUNCIONES DE TABS
// =========================================

function cambiarTab(tab) {
    currentTab = tab;
    
    // Actualizar botones de tabs
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    event.target.closest('.tab-button').classList.add('active');
    
    // Actualizar contenido de tabs
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    document.getElementById(`tab-${tab}`).classList.add('active');
    
    // Cargar datos según el tab
    if (tab === 'clientes') {
        cargarClientes();
    } else {
        cargarAsesores();
    }
}

// =========================================
// FUNCIONES DE COLAPSIBLES
// =========================================

function toggleCollapsible(header) {
    header.classList.toggle('collapsed');
    const content = header.nextElementSibling;
    content.classList.toggle('collapsed');
}

// =========================================
// FUNCIONES DE CLIENTES
// =========================================

function cargarClientes() {
    const tbody = document.getElementById('tablaClientes');
    
    if (clientesData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-users"></i>
                    <p>No hay clientes registrados</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = clientesData.map(cliente => `
        <tr>
            <td>${cliente.id}</td>
            <td>${cliente.nombreCompleto}</td>
            <td>${cliente.documento}</td>
            <td>${cliente.email}</td>
            <td>${cliente.telefono}</td>
            <td>
                <span class="status-badge status-${cliente.estado.toLowerCase()}">
                    ${cliente.estado}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action btn-view" onclick="verDetalleCliente(${cliente.id})" title="Ver detalles">
                        <i class="fas fa-eye"></i> Ver
                    </button>
                    <button class="btn-action btn-edit" onclick="editarCliente(${cliente.id})" title="Editar">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn-action btn-block" onclick="cambiarEstadoCliente(${cliente.id})" title="Cambiar estado">
                        <i class="fas fa-ban"></i> Estado
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function buscarClientes() {
    const searchTerm = document.getElementById('searchClientes').value.toLowerCase();
    
    if (!searchTerm) {
        cargarClientes();
        return;
    }
    
    const filtrados = clientesData.filter(cliente => 
        cliente.nombreCompleto.toLowerCase().includes(searchTerm) ||
        cliente.documento.includes(searchTerm) ||
        cliente.email.toLowerCase().includes(searchTerm)
    );
    
    const tbody = document.getElementById('tablaClientes');
    
    if (filtrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-search"></i>
                    <p>No se encontraron resultados para "${searchTerm}"</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = filtrados.map(cliente => `
        <tr>
            <td>${cliente.id}</td>
            <td>${cliente.nombreCompleto}</td>
            <td>${cliente.documento}</td>
            <td>${cliente.email}</td>
            <td>${cliente.telefono}</td>
            <td>
                <span class="status-badge status-${cliente.estado.toLowerCase()}">
                    ${cliente.estado}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action btn-view" onclick="verDetalleCliente(${cliente.id})">
                        <i class="fas fa-eye"></i> Ver
                    </button>
                    <button class="btn-action btn-edit" onclick="editarCliente(${cliente.id})">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn-action btn-block" onclick="cambiarEstadoCliente(${cliente.id})">
                        <i class="fas fa-ban"></i> Estado
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function verDetalleCliente(id) {
    const cliente = clientesData.find(c => c.id === id);
    if (!cliente) return;
    
    const historial = generarHistorialSolicitudes(id);
    
    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = `
        <div class="detail-grid">
            <div class="detail-item">
                <label>ID Cliente</label>
                <span>${cliente.id}</span>
            </div>
            <div class="detail-item">
                <label>Nombre Completo</label>
                <span>${cliente.nombreCompleto}</span>
            </div>
            <div class="detail-item">
                <label>Documento</label>
                <span>${cliente.documento}</span>
            </div>
            <div class="detail-item">
                <label>Email</label>
                <span>${cliente.email}</span>
            </div>
            <div class="detail-item">
                <label>Teléfono</label>
                <span>${cliente.telefono}</span>
            </div>
            <div class="detail-item">
                <label>Estado</label>
                <span class="status-badge status-${cliente.estado.toLowerCase()}">${cliente.estado}</span>
            </div>
            <div class="detail-item">
                <label>Fecha de Registro</label>
                <span>${cliente.fechaRegistro}</span>
            </div>
            <div class="detail-item">
                <label>Solicitudes Activas</label>
                <span>${cliente.solicitudesActivas}</span>
            </div>
        </div>
        
        <div class="solicitudes-history">
            <h3>📋 Historial de Solicitudes</h3>
            ${historial.length > 0 ? historial.map(sol => `
                <div class="history-item">
                    <strong>${sol.producto}</strong> - ${formatCurrency(sol.monto)}
                    <br>
                    <small>Estado: <span class="status-badge status-${sol.estado.toLowerCase()}">${sol.estado}</span> | Fecha: ${sol.fecha}</small>
                </div>
            `).join('') : '<p style="text-align: center; color: #999;">No hay solicitudes registradas</p>'}
        </div>
    `;
    
    document.getElementById('modalTitulo').textContent = `Detalles del Cliente - ${cliente.nombreCompleto}`;
    document.getElementById('modalDetalles').classList.add('show');
}

function editarCliente(id) {
    showComingSoon('Editar Cliente');
}

function cambiarEstadoCliente(id) {
    const cliente = clientesData.find(c => c.id === id);
    if (!cliente) return;
    
    const nuevoEstado = prompt(`Estado actual: ${cliente.estado}\n\nNuevo estado:\n1. ACTIVO\n2. INACTIVO\n3. BLOQUEADO\n\nIngrese el número:`);
    
    if (nuevoEstado === '1') cliente.estado = 'ACTIVO';
    else if (nuevoEstado === '2') cliente.estado = 'INACTIVO';
    else if (nuevoEstado === '3') cliente.estado = 'BLOQUEADO';
    else return;
    
    cargarClientes();
    actualizarEstadisticasClientes();
    alert(`Estado del cliente actualizado a: ${cliente.estado}`);
}

function actualizarEstadisticasClientes() {
    const total = clientesData.length;
    const activos = clientesData.filter(c => c.estado === 'ACTIVO').length;
    const inactivos = clientesData.filter(c => c.estado === 'INACTIVO').length;
    const bloqueados = clientesData.filter(c => c.estado === 'BLOQUEADO').length;
    
    document.getElementById('statTotalClientes').textContent = total;
    document.getElementById('statClientesActivos').textContent = activos;
    document.getElementById('statClientesInactivos').textContent = inactivos;
    document.getElementById('statClientesBloqueados').textContent = bloqueados;
}

// =========================================
// FUNCIONES DE ASESORES
// =========================================

function cargarAsesores() {
    const tbody = document.getElementById('tablaAsesores');
    
    if (asesoresData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-user-tie"></i>
                    <p>No hay asesores registrados</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = asesoresData.map(asesor => `
        <tr>
            <td>${asesor.id}</td>
            <td>${asesor.nombreCompleto}</td>
            <td>${asesor.documento}</td>
            <td>${asesor.email}</td>
            <td><span class="status-badge" style="background: #e3f2fd; color: #1976d2;">${asesor.rol}</span></td>
            <td>
                <span class="status-badge status-${asesor.estado.toLowerCase()}">
                    ${asesor.estado}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action btn-view" onclick="verDetalleAsesor(${asesor.id})" title="Ver detalles">
                        <i class="fas fa-eye"></i> Ver
                    </button>
                    <button class="btn-action btn-edit" onclick="editarAsesor(${asesor.id})" title="Editar">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn-action btn-block" onclick="cambiarEstadoAsesor(${asesor.id})" title="Cambiar estado">
                        <i class="fas fa-ban"></i> Estado
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function buscarAsesores() {
    const searchTerm = document.getElementById('searchAsesores').value.toLowerCase();
    
    if (!searchTerm) {
        cargarAsesores();
        return;
    }
    
    const filtrados = asesoresData.filter(asesor => 
        asesor.nombreCompleto.toLowerCase().includes(searchTerm) ||
        asesor.email.toLowerCase().includes(searchTerm)
    );
    
    const tbody = document.getElementById('tablaAsesores');
    
    if (filtrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-search"></i>
                    <p>No se encontraron resultados para "${searchTerm}"</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = filtrados.map(asesor => `
        <tr>
            <td>${asesor.id}</td>
            <td>${asesor.nombreCompleto}</td>
            <td>${asesor.documento}</td>
            <td>${asesor.email}</td>
            <td><span class="status-badge" style="background: #e3f2fd; color: #1976d2;">${asesor.rol}</span></td>
            <td>
                <span class="status-badge status-${asesor.estado.toLowerCase()}">
                    ${asesor.estado}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-action btn-view" onclick="verDetalleAsesor(${asesor.id})">
                        <i class="fas fa-eye"></i> Ver
                    </button>
                    <button class="btn-action btn-edit" onclick="editarAsesor(${asesor.id})">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn-action btn-block" onclick="cambiarEstadoAsesor(${asesor.id})">
                        <i class="fas fa-ban"></i> Estado
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function verDetalleAsesor(id) {
    const asesor = asesoresData.find(a => a.id === id);
    if (!asesor) return;
    
    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = `
        <div class="detail-grid">
            <div class="detail-item">
                <label>ID Asesor</label>
                <span>${asesor.id}</span>
            </div>
            <div class="detail-item">
                <label>Nombre Completo</label>
                <span>${asesor.nombreCompleto}</span>
            </div>
            <div class="detail-item">
                <label>Documento</label>
                <span>${asesor.documento}</span>
            </div>
            <div class="detail-item">
                <label>Email</label>
                <span>${asesor.email}</span>
            </div>
            <div class="detail-item">
                <label>Rol</label>
                <span>${asesor.rol}</span>
            </div>
            <div class="detail-item">
                <label>Estado</label>
                <span class="status-badge status-${asesor.estado.toLowerCase()}">${asesor.estado}</span>
            </div>
            <div class="detail-item">
                <label>Fecha de Ingreso</label>
                <span>${asesor.fechaIngreso}</span>
            </div>
            <div class="detail-item">
                <label>Solicitudes Procesadas</label>
                <span>${asesor.solicitudesProcesadas}</span>
            </div>
        </div>
        
        <div class="solicitudes-history">
            <h3>📊 Estadísticas de Rendimiento</h3>
            <div class="mini-stats-grid">
                <div class="mini-stat-card">
                    <div class="mini-stat-icon" style="background: #e8f5e9; color: #4caf50;">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="mini-stat-info">
                        <h4>Tasa de Aprobación</h4>
                        <p>${asesor.tasaAprobacion}</p>
                    </div>
                </div>
                <div class="mini-stat-card">
                    <div class="mini-stat-icon" style="background: #e3f2fd; color: #2196f3;">
                        <i class="fas fa-file-alt"></i>
                    </div>
                    <div class="mini-stat-info">
                        <h4>Total Procesadas</h4>
                        <p>${asesor.solicitudesProcesadas}</p>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('modalTitulo').textContent = `Detalles del Asesor - ${asesor.nombreCompleto}`;
    document.getElementById('modalDetalles').classList.add('show');
}

function editarAsesor(id) {
    showComingSoon('Editar Asesor');
}

function cambiarEstadoAsesor(id) {
    const asesor = asesoresData.find(a => a.id === id);
    if (!asesor) return;
    
    const nuevoEstado = prompt(`Estado actual: ${asesor.estado}\n\nNuevo estado:\n1. ACTIVO\n2. INACTIVO\n\nIngrese el número:`);
    
    if (nuevoEstado === '1') asesor.estado = 'ACTIVO';
    else if (nuevoEstado === '2') asesor.estado = 'INACTIVO';
    else return;
    
    cargarAsesores();
    actualizarEstadisticasAsesores();
    alert(`Estado del asesor actualizado a: ${asesor.estado}`);
}

function actualizarEstadisticasAsesores() {
    const total = asesoresData.length;
    const activos = asesoresData.filter(a => a.estado === 'ACTIVO').length;
    const inactivos = asesoresData.filter(a => a.estado === 'INACTIVO').length;
    
    document.getElementById('statTotalAsesores').textContent = total;
    document.getElementById('statAsesoresActivos').textContent = activos;
    document.getElementById('statAsesoresInactivos').textContent = inactivos;
}

// =========================================
// FUNCIONES AUXILIARES
// =========================================

function cerrarModal() {
    document.getElementById('modalDetalles').classList.remove('show');
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(amount);
}

// Cerrar modal al hacer clic fuera
document.addEventListener('click', function(e) {
    const modal = document.getElementById('modalDetalles');
    if (e.target === modal) {
        cerrarModal();
    }
});

// Hacer funciones accesibles globalmente
window.cambiarTab = cambiarTab;
window.toggleCollapsible = toggleCollapsible;
window.buscarClientes = buscarClientes;
window.buscarAsesores = buscarAsesores;
window.verDetalleCliente = verDetalleCliente;
window.verDetalleAsesor = verDetalleAsesor;
window.editarCliente = editarCliente;
window.editarAsesor = editarAsesor;
window.cambiarEstadoCliente = cambiarEstadoCliente;
window.cambiarEstadoAsesor = cambiarEstadoAsesor;
window.cerrarModal = cerrarModal;
let currentUser = null;
let originalUserData = {};

// Verificar autenticación al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    currentUser = verificarAutenticacion();
    if (currentUser) {
        cargarDatosPerfil(currentUser);
        initializeMenuToggle();
        setupEventListeners();
    }
});

function verificarAutenticacion() {
    const usuario = sessionStorage.getItem('usuario');
    if (!usuario) {
        window.location.href = 'login.html';
        return null;
    }
    return JSON.parse(usuario);
}

function cargarDatosPerfil(usuario) {
    // Guardar datos originales para cancelar edición
    originalUserData = {
        nombres: usuario.nombreCompleto?.split(' ')[0] || '',
        apellidos: usuario.nombreCompleto?.split(' ').slice(1).join(' ') || '',
        email: usuario.email || 'cargando@email.com',
        documento: usuario.documento || 'N/A',
        direccion: usuario.direccion || '',
        fechaNacimiento: usuario.fechaNacimiento || '',
        ciudadNacimiento: usuario.ciudadNacimiento || ''
    };

    // Cargar datos en la UI
    document.getElementById('profileName').textContent = usuario.nombreCompleto || 'Usuario';
    document.getElementById('profileEmail').textContent = usuario.email || 'cargando@email.com';

    // Cargar datos en el formulario
    document.getElementById('nombres').value = originalUserData.nombres;
    document.getElementById('apellidos').value = originalUserData.apellidos;
    document.getElementById('email').value = originalUserData.email;
    document.getElementById('documento').value = originalUserData.documento;
    document.getElementById('direccion').value = originalUserData.direccion;
    document.getElementById('fechaNacimiento').value = originalUserData.fechaNacimiento;
    document.getElementById('ciudadNacimiento').value = originalUserData.ciudadNacimiento;

    // Mostrar nombre en el header
    const userNameHeader = document.getElementById('userName');
    if (userNameHeader) {
        userNameHeader.textContent = usuario.nombreCompleto || 'Usuario';
    }
}

function setupEventListeners() {
    const btnEditar = document.getElementById('btnEditar');
    const btnGuardar = document.getElementById('btnGuardar');
    const btnCancelar = document.getElementById('btnCancelar');
    const fileInput = document.getElementById('fileUpload');
    const fileNameSpan = document.getElementById('fileName');

    btnEditar.addEventListener('click', toggleEditMode);
    btnCancelar.addEventListener('click', toggleEditMode);

    fileInput.addEventListener('change', () => {
        fileNameSpan.textContent = fileInput.files.length > 0 ? fileInput.files[0].name : 'Ningún archivo seleccionado.';
    });
}

function toggleEditMode() {
    const form = document.getElementById('profileForm');
    const isEditing = form.classList.toggle('editing');

    // Habilitar/deshabilitar inputs
    ['direccion'].forEach(id => {
        const input = document.getElementById(id);
        input.readOnly = !isEditing;
    });

    // Mostrar/ocultar botones
    document.getElementById('btnEditar').classList.toggle('hidden', isEditing);
    document.getElementById('btnGuardar').classList.toggle('hidden', !isEditing);
    document.getElementById('btnCancelar').classList.toggle('hidden', !isEditing);
    document.getElementById('documentUploadSection').classList.toggle('hidden', !isEditing);

    // Si se cancela la edición, restaurar datos originales
    if (!isEditing) {
        document.getElementById('direccion').value = originalUserData.direccion;
    }
}

function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded');
        });
    }
}

function showComingSoon(feature) {
    alert(`La funcionalidad de '${feature}' estará disponible próximamente.`);
}

/**
 * Cierra la sesión del cliente previa confirmación.
 */
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('usuario');
        window.location.href = 'login.html';
    }
}
// Configuración de la API
const API_AUTH_URL = 'http://localhost:8081/api';

// Variables globales
let datosOriginales = {};

// Verificar autenticación de asesor
function verificarAutenticacion() {
    const asesor = sessionStorage.getItem('asesor');
    if (!asesor) {
        window.location.href = 'backoffice-login.html';
        return null;
    }
    return JSON.parse(asesor);
}

// Cargar datos del asesor desde el backend
async function cargarDatosAsesor() {
    const asesor = verificarAutenticacion();
    if (!asesor) return;

    try {
        const response = await fetch(`${API_AUTH_URL}/asesores/${asesor.asesorId}`);

        if (!response.ok) {
            throw new Error('Error al cargar los datos del perfil');
        }

        const datosAsesor = await response.json();

        // Guardar datos originales para detectar cambios
        datosOriginales = {
            nombres: datosAsesor.nombres,
            apellidos: datosAsesor.apellidos,
            area: datosAsesor.area
        };

        // Poblar formulario con los datos
        poblarFormulario(datosAsesor);

        // Actualizar nombre en el header
        document.getElementById('userName').textContent = datosAsesor.nombreCompleto;

    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('mensajeEstado', 'error', 'Error al cargar los datos del perfil');
    }
}

// Poblar formulario con los datos del asesor
function poblarFormulario(datosAsesor) {
    // Información del perfil (header)
    document.getElementById('perfilNombreCompleto').textContent = datosAsesor.nombreCompleto;
    document.getElementById('perfilEmail').textContent = datosAsesor.email;
    document.getElementById('perfilArea').textContent = datosAsesor.area || 'Sin área';

    // Formulario de edición
    document.getElementById('inputNombres').value = datosAsesor.nombres || '';
    document.getElementById('inputApellidos').value = datosAsesor.apellidos || '';
    document.getElementById('inputCodigoEmpleado').value = datosAsesor.codigoEmpleado || '';
    document.getElementById('inputEmail').value = datosAsesor.email || '';
    document.getElementById('inputArea').value = datosAsesor.area || '';
}

// Habilitar edición cuando hay cambios
function habilitarEdicion() {
    const nombres = document.getElementById('inputNombres').value;
    const apellidos = document.getElementById('inputApellidos').value;
    const area = document.getElementById('inputArea').value;

    const btnActualizar = document.getElementById('btnActualizar');

    // Comparar con datos originales
    const hayCambios = 
        nombres !== datosOriginales.nombres ||
        apellidos !== datosOriginales.apellidos ||
        area !== datosOriginales.area;

    if (hayCambios) {
        btnActualizar.disabled = false;
        btnActualizar.classList.add('enabled');
    } else {
        btnActualizar.disabled = true;
        btnActualizar.classList.remove('enabled');
    }
}

// Actualizar perfil del asesor
async function actualizarPerfil(event) {
    event.preventDefault();

    const asesor = verificarAutenticacion();
    if (!asesor) return;

    const btnActualizar = document.getElementById('btnActualizar');
    const originalText = btnActualizar.innerHTML;

    // Validaciones frontend
    const nombres = document.getElementById('inputNombres').value.trim();
    const apellidos = document.getElementById('inputApellidos').value.trim();
    const area = document.getElementById('inputArea').value;

    if (nombres.length < 2 || nombres.length > 100) {
        mostrarMensaje('mensajeEstado', 'error', 'Los nombres deben tener entre 2 y 100 caracteres');
        return;
    }

    if (apellidos.length < 2 || apellidos.length > 100) {
        mostrarMensaje('mensajeEstado', 'error', 'Los apellidos deben tener entre 2 y 100 caracteres');
        return;
    }

    if (!['CREDITO', 'RIESGO', 'ADMINISTRACION'].includes(area)) {
        mostrarMensaje('mensajeEstado', 'error', 'Área inválida');
        return;
    }

    // Deshabilitar botón y mostrar loading
    btnActualizar.disabled = true;
    btnActualizar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Actualizando...';

    try {
        const response = await fetch(`${API_AUTH_URL}/asesores/${asesor.asesorId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nombres: nombres,
                apellidos: apellidos,
                area: area
            })
        });

        const result = await response.json();

        if (result.success) {
            mostrarMensaje('mensajeEstado', 'success', result.message);

            // Actualizar datos originales
            datosOriginales = {
                nombres: result.asesor.nombres,
                apellidos: result.asesor.apellidos,
                area: result.asesor.area
            };

            // Actualizar información del perfil
            document.getElementById('perfilNombreCompleto').textContent = result.asesor.nombreCompleto;
            document.getElementById('perfilArea').textContent = result.asesor.area;
            document.getElementById('userName').textContent = result.asesor.nombreCompleto;

            // Actualizar sessionStorage
            const asesorData = JSON.parse(sessionStorage.getItem('asesor'));
            asesorData.nombreCompleto = result.asesor.nombreCompleto;
            sessionStorage.setItem('asesor', JSON.stringify(asesorData));

            // Deshabilitar botón después de actualizar
            btnActualizar.disabled = true;
            btnActualizar.classList.remove('enabled');
        } else {
            mostrarMensaje('mensajeEstado', 'error', result.message);
        }

    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('mensajeEstado', 'error', 'Error al actualizar el perfil');
    } finally {
        btnActualizar.innerHTML = originalText;
    }
}

// Cambiar contraseña
async function cambiarPassword(event) {
    event.preventDefault();

    const asesor = verificarAutenticacion();
    if (!asesor) return;

    const btnCambiar = event.target.querySelector('button[type="submit"]');
    const originalText = btnCambiar.innerHTML;

    const passwordActual = document.getElementById('inputPasswordActual').value;
    const passwordNueva = document.getElementById('inputPasswordNueva').value;
    const passwordConfirmar = document.getElementById('inputPasswordConfirmar').value;

    // Validaciones frontend
    if (passwordNueva.length < 8) {
        mostrarMensaje('mensajePassword', 'error', 'La nueva contraseña debe tener al menos 8 caracteres');
        return;
    }

    if (passwordNueva !== passwordConfirmar) {
        mostrarMensaje('mensajePassword', 'error', 'Las contraseñas no coinciden');
        return;
    }

    // Deshabilitar botón y mostrar loading
    btnCambiar.disabled = true;
    btnCambiar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Cambiando contraseña...';

    try {
        const response = await fetch(`${API_AUTH_URL}/asesores/${asesor.asesorId}/cambiar-password`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                passwordActual: passwordActual,
                passwordNueva: passwordNueva
            })
        });

        const result = await response.json();

        if (result.success) {
            mostrarMensaje('mensajePassword', 'success', result.message);

            // Limpiar formulario
            document.getElementById('formPassword').reset();
        } else {
            mostrarMensaje('mensajePassword', 'error', result.message);
        }

    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('mensajePassword', 'error', 'Error al cambiar la contraseña');
    } finally {
        btnCambiar.disabled = false;
        btnCambiar.innerHTML = originalText;
    }
}

// Resetear formulario a valores originales
function resetearFormulario() {
    document.getElementById('inputNombres').value = datosOriginales.nombres;
    document.getElementById('inputApellidos').value = datosOriginales.apellidos;
    document.getElementById('inputArea').value = datosOriginales.area;

    // Deshabilitar botón de actualizar
    const btnActualizar = document.getElementById('btnActualizar');
    btnActualizar.disabled = true;
    btnActualizar.classList.remove('enabled');

    // Ocultar mensaje si existe
    const mensajeEstado = document.getElementById('mensajeEstado');
    mensajeEstado.classList.add('hidden');
}

// Mostrar mensajes de éxito/error
function mostrarMensaje(contenedor, tipo, mensaje) {
    const mensajeElemento = document.getElementById(contenedor);

    // Configurar mensaje
    mensajeElemento.textContent = mensaje;
    mensajeElemento.className = `mensaje-estado ${tipo}`;
    mensajeElemento.classList.remove('hidden');

    // Ocultar después de 5 segundos
    setTimeout(() => {
        mensajeElemento.classList.add('hidden');
    }, 5000);
}

// Cerrar sesión
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('asesor');
        window.location.href = 'backoffice-login.html';
    }
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

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    // Inicializar menú
    initializeMenuToggle();

    // Cargar datos del asesor
    cargarDatosAsesor();

    // Event listeners para detectar cambios en el formulario
    document.getElementById('inputNombres').addEventListener('input', habilitarEdicion);
    document.getElementById('inputApellidos').addEventListener('input', habilitarEdicion);
    document.getElementById('inputArea').addEventListener('change', habilitarEdicion);

    // Event listener para actualizar perfil
    document.getElementById('formPerfil').addEventListener('submit', actualizarPerfil);

    // Event listener para cambiar contraseña
    document.getElementById('formPassword').addEventListener('submit', cambiarPassword);
});
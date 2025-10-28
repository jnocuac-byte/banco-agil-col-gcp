// Configuración de la API
const API_AUTH_URL = 'https://auth-service-514751056677.us-central1.run.app/api';

// Variables globales
let datosOriginales = {};

// Verificar autenticación de asesor
function verificarAutenticacion() {
    // Verificar si el asesor está en sessionStorage
    const asesor = sessionStorage.getItem('asesor');
    // Si no está, redirigir a login
    if (!asesor) {
        window.location.href = 'backoffice-login.html'; // Redirigir a login
        return null; // Detener ejecución
    }
    return JSON.parse(asesor); // Retornar datos del asesor
}

// Cargar datos del asesor desde el backend
async function cargarDatosAsesor() {
    // Verificar autenticación
    const asesor = verificarAutenticacion();
    // Si no está autenticado, salir
    if (!asesor) return;

    // Hacer petición GET a /asesores/:id
    try {
        const response = await fetch(`${API_AUTH_URL}/asesores/${asesor.asesorId}`); // Usar asesorId del sessionStorage

        // Verificar respuesta
        if (!response.ok) {
            throw new Error('Error al cargar los datos del perfil'); // Lanzar error si no es ok
        }

        // Parsear datos del asesor
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

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        mostrarMensaje('mensajeEstado', 'error', 'Error al cargar los datos del perfil'); // Mostrar mensaje de error
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
    
    // Área (select)
    const selectArea = document.getElementById('inputArea');
    const areaActual = datosAsesor.area || '';

    // Aplicar lógica de restricciones según el área
    if (areaActual === 'ADMIN_TOTAL') {
        // Si es ADMIN_TOTAL: solo mostrar esa opción y hacer el select de solo lectura
        selectArea.innerHTML = '<option value="ADMIN_TOTAL">Admin Total</option>';
        selectArea.disabled = true;
        selectArea.value = 'ADMIN_TOTAL';
    } else {
        // Si NO es ADMIN_TOTAL: mostrar solo CREDITO, RIESGO, ADMINISTRACION (sin ADMIN_TOTAL)
        selectArea.innerHTML = `
            <option value="CREDITO">Crédito</option>
            <option value="RIESGO">Riesgo</option>
            <option value="ADMINISTRACION">Administración</option>
        `;
        selectArea.disabled = false; // Habilitar selección
        selectArea.value = areaActual; // Seleccionar área actual
    }
}

// Habilitar edición cuando hay cambios
function habilitarEdicion() {
    const nombres = document.getElementById('inputNombres').value; // Obtener valores actuales
    const apellidos = document.getElementById('inputApellidos').value; // Obtener valores actuales
    const area = document.getElementById('inputArea').value; // Obtener valores actuales

    const btnActualizar = document.getElementById('btnActualizar'); // Botón de actualizar

    // Comparar con datos originales
    const hayCambios = 
        nombres !== datosOriginales.nombres || // Verificar si hay cambios
        apellidos !== datosOriginales.apellidos || // Verificar si hay cambios
        area !== datosOriginales.area; // Verificar si hay cambios

    // Habilitar o deshabilitar botón según si hay cambios
    if (hayCambios) {
        btnActualizar.disabled = false; // Habilitar botón
        btnActualizar.classList.add('enabled'); // Añadir clase enabled
    } else { // No hay cambios
        btnActualizar.disabled = true; // Deshabilitar botón
        btnActualizar.classList.remove('enabled'); // Remover clase enabled
    }
}

// Actualizar perfil del asesor
async function actualizarPerfil(event) {
    event.preventDefault(); // Prevenir envío por defecto

    // Verificar autenticación
    const asesor = verificarAutenticacion();
    if (!asesor) return;

    // Obtener referencia al botón y su texto original
    const btnActualizar = document.getElementById('btnActualizar');
    const originalText = btnActualizar.innerHTML;

    // Validaciones frontend
    const nombres = document.getElementById('inputNombres').value.trim();
    const apellidos = document.getElementById('inputApellidos').value.trim();
    const area = document.getElementById('inputArea').value;

    // Validar longitud de nombres
    if (nombres.length < 2 || nombres.length > 100) {
        mostrarMensaje('mensajeEstado', 'error', 'Los nombres deben tener entre 2 y 100 caracteres'); // Mostrar mensaje de error
        return; // Detener ejecución
    }

    // Validar longitud de apellidos
    if (apellidos.length < 2 || apellidos.length > 100) {
        mostrarMensaje('mensajeEstado', 'error', 'Los apellidos deben tener entre 2 y 100 caracteres'); // Mostrar mensaje de error
        return; // Detener ejecución
    }

    // Validar área
    if (!['CREDITO', 'RIESGO', 'ADMINISTRACION', 'ADMIN_TOTAL'].includes(area)) {
        mostrarMensaje('mensajeEstado', 'error', 'Área inválida'); // Mostrar mensaje de error
        return; // Detener ejecución
    }

    // Deshabilitar botón y mostrar loading
    btnActualizar.disabled = true;
    btnActualizar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Actualizando...';

    // Enviar petición PUT a /asesores/:id
    try {
        // Petición PUT a /asesores/:id
        const response = await fetch(`${API_AUTH_URL}/asesores/${asesor.asesorId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ // Enviar datos como JSON
                nombres: nombres,
                apellidos: apellidos,
                area: area
            })
        });

        // Parsear respuesta JSON
        const result = await response.json();

        // Manejar respuesta
        if (result.success) {
            mostrarMensaje('mensajeEstado', 'success', result.message); // Mostrar mensaje de éxito

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
        } else { // Error de actualización
            mostrarMensaje('mensajeEstado', 'error', result.message); // Mostrar mensaje de error
        }

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        mostrarMensaje('mensajeEstado', 'error', 'Error al actualizar el perfil'); // Mostrar mensaje de error
    } finally { // Siempre ejecutar
        btnActualizar.innerHTML = originalText; // Restaurar texto original
    }
}

// Cambiar contraseña
async function cambiarPassword(event) {
    event.preventDefault(); // Prevenir envío por defecto

    // Verificar autenticación
    const asesor = verificarAutenticacion();
    if (!asesor) return;

    // Obtener referencia al botón y su texto original
    const btnCambiar = event.target.querySelector('button[type="submit"]');
    const originalText = btnCambiar.innerHTML;

    // Obtener valores del formulario
    const passwordActual = document.getElementById('inputPasswordActual').value;
    const passwordNueva = document.getElementById('inputPasswordNueva').value;
    const passwordConfirmar = document.getElementById('inputPasswordConfirmar').value;

    // Validaciones frontend
    if (passwordNueva.length < 8) {
        mostrarMensaje('mensajePassword', 'error', 'La nueva contraseña debe tener al menos 8 caracteres'); // Mostrar mensaje de error
        return;
    }

    // Validar que las contraseñas coincidan
    if (passwordNueva !== passwordConfirmar) {
        mostrarMensaje('mensajePassword', 'error', 'Las contraseñas no coinciden'); // Mostrar mensaje de error
        return;
    }

    // Deshabilitar botón y mostrar loading
    btnCambiar.disabled = true;
    btnCambiar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Cambiando contraseña...';

    // Enviar petición PUT a /asesores/:id/cambiar-password
    try {
        // Petición PUT a /asesores/:id/cambiar-password
        const response = await fetch(`${API_AUTH_URL}/asesores/${asesor.asesorId}/cambiar-password`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            // Enviar datos como JSON
            body: JSON.stringify({
                passwordActual: passwordActual,
                passwordNueva: passwordNueva
            })
        });

        // Parsear respuesta JSON
        const result = await response.json();

        // Manejar respuesta
        if (result.success) {
            mostrarMensaje('mensajePassword', 'success', result.message); // Mostrar mensaje de éxito

            // Limpiar formulario
            document.getElementById('formPassword').reset();
        } else { // Error al cambiar la contraseña
            mostrarMensaje('mensajePassword', 'error', result.message); // Mostrar mensaje de error
        }

    } catch (error) { // Manejo de errores
        console.error('Error:', error); // Log del error
        mostrarMensaje('mensajePassword', 'error', 'Error al cambiar la contraseña'); // Mostrar mensaje de error
    } finally {// Siempre ejecutar
        btnCambiar.disabled = false; // Rehabilitar botón
        btnCambiar.innerHTML = originalText; // Restaurar texto original
    }
}

// Resetear formulario a valores originales
function resetearFormulario() {
    // Poblar formulario con datos originales
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
    // Obtener referencia al contenedor del mensaje
    const mensajeElemento = document.getElementById(contenedor);

    // Configurar mensaje
    mensajeElemento.textContent = mensaje;
    mensajeElemento.className = `mensaje-estado ${tipo}`;
    mensajeElemento.classList.remove('hidden');

    // Ocultar después de 5 segundos
    setTimeout(() => {
        mensajeElemento.classList.add('hidden'); // Ocultar mensaje
    }, 5000); // 5 segundos
}

// Cerrar sesión
function logout() {
    // Confirmar cierre de sesión
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('asesor'); // Eliminar asesor de sessionStorage
        window.location.href = 'backoffice-login.html'; // Redirigir a login
    }
}

// Lógica de navegación del menú lateral
function initializeMenuToggle() {
    // Toggle del menú
    const menuToggle = document.getElementById('menuToggle');
    // Añadir evento click
    if (menuToggle) {
        // Al hacer click
        menuToggle.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('sidebar-expanded'); // Alternar clase
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
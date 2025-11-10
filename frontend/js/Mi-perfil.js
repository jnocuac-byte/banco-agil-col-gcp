/**
 * Objeto para almacenar el estado de la edición.
 */
const editState = { isEditing: false };
/**
 * Verifica si hay un usuario autenticado en sessionStorage.
 * Para desarrollo, si no hay usuario, crea uno falso.
 * @returns {object|null} El objeto del usuario o null si la autenticación falla.
            email: "test@example.com",
            clienteId: 123,
            documento: "12345678",
            nombres: "Usuario",
            apellidos: "de Prueba",
            fechaNacimiento: "1990-01-15",
            ciudadNacimiento: "Bogotá D.C.",
            direccion: "Calle Falsa 123",
            estadoDocumento:"pendiente"
        };
        return fakeUser;
    }
    if (usuario) {
        // Cargar nombre en el header
        document.getElementById('userName').textContent = usuario.nombreCompleto || 'Usuario';
        
        // Cargar datos en la tarjeta de perfil
        document.getElementById('profileName').textContent = usuario.nombreCompleto || 'Nombre no disponible';
        document.getElementById('profileEmail').textContent = usuario.email || 'Email no disponible';

        document.getElementById('documento').value = usuario.documento || '';
        document.getElementById('nombres').value = usuario.nombres || '';
        document.getElementById('apellidos').value = usuario.apellidos || '';
        document.getElementById('email').value = usuario.email || '';
        document.getElementById('fechaNacimiento').value = usuario.fechaNacimiento || '';
        document.getElementById('ciudadNacimiento').value = usuario.ciudadNacimiento || '';
        document.getElementById('direccion').value = usuario.direccion || '';
    }
}

/**
 * Activa o desactiva el modo de edición del formulario.
 */
function toggleEditMode() {
    editState.isEditing = !editState.isEditing;

    // Campos que se podrán editar
    const editableFields = ['nombres', 'apellidos', 'email', 'fechaNacimiento', 'ciudadNacimiento', 'direccion'];
    
    editableFields.forEach(id => {
        const input = document.getElementById(id);
        input.readOnly = !editState.isEditing;
    });

    document.getElementById('documentUploadSection').classList.toggle('hidden', !editState.isEditing);

    // Alternar visibilidad de los botones
    document.getElementById('btnEditar').classList.toggle('hidden', editState.isEditing);
    document.getElementById('btnCambiarPass').classList.toggle('hidden', editState.isEditing);
    document.getElementById('btnGuardar').classList.toggle('hidden', !editState.isEditing);
    document.getElementById('btnCancelar').classList.toggle('hidden', !editState.isEditing);

    if (!editState.isEditing) {
        // Al salir del modo edición, recargar datos y limpiar campos.
        cargarDatosUsuario();
        document.getElementById('fileName').textContent = 'Ningún archivo seleccionado.';
        document.getElementById('fileUpload').value = ''; // Limpiar el input de archivo
    }
}

/**
 * Guarda los cambios realizados en el perfil.
 */
function guardarCambios() {
    const usuario = verificarAutenticacion();
    if (!usuario) return;

    // Actualizar el objeto de usuario con los nuevos valores del formulario
    usuario.nombres = document.getElementById('nombres').value;
    usuario.apellidos = document.getElementById('apellidos').value;
    usuario.nombreCompleto = `${usuario.nombres} ${usuario.apellidos}`; // Actualizar nombre completo
    usuario.email = document.getElementById('email').value;
    usuario.fechaNacimiento = document.getElementById('fechaNacimiento').value;
    usuario.ciudadNacimiento = document.getElementById('ciudadNacimiento').value;
    usuario.direccion = document.getElementById('direccion').value;

    sessionStorage.setItem('usuario', JSON.stringify(usuario));

    // Simular subida de archivo
    const fileInput = document.getElementById('fileUpload');
    if (fileInput.files.length > 0) {
        const fileName = fileInput.files[0].name;
        console.log(`Simulando subida del archivo: ${fileName}`);
        alert(`Perfil guardado y se ha "subido" el archivo: ${fileName}`);
    } else {
        alert('Perfil guardado correctamente.');
    }

    // Salir del modo edición
    toggleEditMode();
}

/**
 * Cancela la edición y revierte los cambios en el formulario.
 */
function cancelarEdicion() {
    if (confirm('¿Estás seguro de que quieres descartar los cambios?')) {
        toggleEditMode();
    }
}

/**
 * Actualiza el nombre del archivo seleccionado en la UI.
 */
function actualizarNombreArchivo() {
    const fileInput = document.getElementById('fileUpload');
    const fileNameSpan = document.getElementById('fileName');
    if (fileInput.files.length > 0) {
        fileNameSpan.textContent = fileInput.files[0].name;
    } else {
        fileNameSpan.textContent = 'Ningún archivo seleccionado.';
    }
}

/**
 * Event listener que se ejecuta cuando el contenido del DOM está completamente cargado.
 */
document.addEventListener('DOMContentLoaded', () => {
    const usuario = verificarAutenticacion();
    if (!usuario) {
        alert('No se pudo verificar la sesión. Serás redirigido al login.');
        window.location.href = 'login.html';
        return;
    }

    initializeMenuToggle();
    cargarDatosUsuario();

    document.getElementById('btnEditar').addEventListener('click', toggleEditMode);
    document.getElementById('btnGuardar').addEventListener('click', guardarCambios);
    document.getElementById('btnCancelar').addEventListener('click', cancelarEdicion);
    document.getElementById('fileUpload').addEventListener('change', actualizarNombreArchivo);
});

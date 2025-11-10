// URL base de la API
const API_URL = 'https://auth-service-514751056677.us-central1.run.app/api/auth';

// Mostrar alertas
function mostrarAlerta(mensaje, tipo) {
    // tipo: 'success', 'error', 'info'
    const alert = document.getElementById('alert');
    alert.textContent = mensaje;
    alert.className = `alert ${tipo}`;
    alert.style.display = 'block';
    
    // Ocultar después de 5 segundos
    setTimeout(() => {
        alert.style.display = 'none'; // Añadir transición de desvanecimiento
    }, 5000);
}

// Manejar el envío del formulario de login
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    // Prevenir el envío por defecto
    e.preventDefault();
    
    // Obtener referencia al botón y su texto original
    const btnLogin = document.getElementById('btnLogin');
    const originalText = btnLogin.innerHTML;
    
    // Deshabilitar botón y mostrar loading
    btnLogin.disabled = true;
    btnLogin.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Iniciando sesión...';
    
    // Preparar datos del formulario
    const data = {
        email: document.getElementById('email').value, // El campo 'email' en el login.html tiene id 'email'
        password: document.getElementById('password').value // El campo 'password' en el login.html tiene id 'password'
    };
    
    // Enviar petición a la API
    try {
        // Petición POST a /login
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST', // Metodo POST y headers JSON
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data) // Enviar datos como JSON
        });
        
        // Parsear respuesta JSON
        const result = await response.json();
        
        // Manejar respuesta
        if (result.success) {
            // Guardar datos en sessionStorage
            sessionStorage.setItem('usuario', JSON.stringify({
                usuarioId: result.usuarioId,
                email: result.email,
                tipoUsuario: result.tipoUsuario,
                clienteId: result.clienteId,
                tipoCliente: result.tipoCliente,
                nombres: result.nombres,
                apellidos: result.apellidos,
                fechaNacimiento: result.fechaNacimiento,
                ciudad: result.ciudad,
                direccion: result.direccion,
                documentoIdentidadEstado: result.documentoIdentidadEstado,
                token: result.token
            }));
            
            // Mostrar mensaje de éxito y redirigir
            mostrarAlerta('¡Login exitoso! Redirigiendo...', 'success');
            
            // Redirigir después de un breve retraso
            setTimeout(() => {
                window.location.href = 'dashboard.html'; // Redirigir a dashboard.html
            }, 1500);
        } else { // Error de autenticación
            // Mostrar mensaje de error
            mostrarAlerta(result.message, 'error');
            btnLogin.disabled = false;
            btnLogin.innerHTML = originalText;
        }
    } catch (error) { // Error de red o servidor
        console.error('Error:', error);
        mostrarAlerta('Error al conectar con el servidor', 'error');
        btnLogin.disabled = false; // Rehabilitar botón
        btnLogin.innerHTML = originalText; // Restaurar texto original
    }
});
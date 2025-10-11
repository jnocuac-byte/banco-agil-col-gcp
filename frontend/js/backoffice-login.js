// URL base de la API de autenticación
const API_URL = 'http://localhost:8081/api/auth';

// Mostrar alertas
function mostrarAlerta(mensaje, tipo) {
    // tipo: 'success', 'error', 'info'
    const alert = document.getElementById('alert');
    alert.textContent = mensaje;
    alert.className = `alert ${tipo}`;
    alert.style.display = 'block';
    
    // Ocultar después de 5 segundos
    setTimeout(() => {
        // Añadir transición de desvanecimiento
        alert.style.display = 'none';
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
    btnLogin.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Verificando credenciales...';
    
    // Preparar datos del formulario
    const data = {
        // El campo 'email' en el backoffice-login.html tiene id 'email'
        // El campo 'password' en el backoffice-login.html tiene id 'password'
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };
    
    // Enviar petición a la API
    try {
        // Petición POST a /backoffice/login
        const response = await fetch(`${API_URL}/backoffice/login`, {
            // Metodo POST y headers JSON
            method: 'POST',
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
            sessionStorage.setItem('asesor', JSON.stringify({
                usuarioId: result.usuarioId,
                email: result.email,
                tipoUsuario: result.tipoUsuario,
                asesorId: result.clienteId, // Reutilizamos este campo para el ID del asesor
                nombreCompleto: result.nombreCompleto,
                token: result.token
            }));
            
            // Mostrar alerta de éxito
            mostrarAlerta('¡Acceso autorizado! Redirigiendo...', 'success');
            
            // Redirigir al dashboard después de 1.5 segundos
            setTimeout(() => {
                // Redirigir a backoffice-dashboard.html
                window.location.href = 'backoffice-dashboard.html';
            }, 1500);
        } else { // Error de autenticación
            // Mostrar mensaje de error
            mostrarAlerta(result.message, 'error');
            btnLogin.disabled = false;
            btnLogin.innerHTML = originalText;
        }
    } catch (error) { // Error de conexión u otro
        console.error('Error:', error); // Log del error
        mostrarAlerta('Error al conectar con el servidor', 'error'); // Mostrar mensaje de error
        btnLogin.disabled = false; // Rehabilitar botón
        btnLogin.innerHTML = originalText; // Restaurar texto original
    }
});
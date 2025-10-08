const API_URL = 'http://localhost:8081/api/auth';

function mostrarAlerta(mensaje, tipo) {
    const alert = document.getElementById('alert');
    alert.textContent = mensaje;
    alert.className = `alert ${tipo}`;
    alert.style.display = 'block';
    
    setTimeout(() => {
        alert.style.display = 'none';
    }, 5000);
}

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const btnLogin = document.getElementById('btnLogin');
    const originalText = btnLogin.innerHTML;
    
    // Deshabilitar botón y mostrar loading
    btnLogin.disabled = true;
    btnLogin.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Verificando credenciales...';
    
    const data = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };
    
    try {
        const response = await fetch(`${API_URL}/backoffice/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
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
            
            mostrarAlerta('¡Acceso autorizado! Redirigiendo...', 'success');
            
            setTimeout(() => {
                window.location.href = 'backoffice-dashboard.html';
            }, 1500);
        } else {
            mostrarAlerta(result.message, 'error');
            btnLogin.disabled = false;
            btnLogin.innerHTML = originalText;
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al conectar con el servidor', 'error');
        btnLogin.disabled = false;
        btnLogin.innerHTML = originalText;
    }
});
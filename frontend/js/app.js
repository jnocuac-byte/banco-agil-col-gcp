// =========================================
// LOGIN Y REGISTRO - FRONTEND SIMULADO
// =========================================

// Simulación de base de datos de usuarios en localStorage
function initializeUsers() {
    if (!localStorage.getItem('bancoUsers')) {
        const defaultUsers = [
            {
                email: 'usuario@banco.com',
                password: '1234',
                name: 'Juan Pérez',
                documentType: 'CC',
                documentNumber: '12345678'
            },
            {
                email: 'admin@banco.com', 
                password: 'admin',
                name: 'Administrador',
                documentType: 'CC',
                documentNumber: '87654321'
            }
        ];
        localStorage.setItem('bancoUsers', JSON.stringify(defaultUsers));
    }
}

// Función para validar credenciales
function validateLogin(email, password) {
    const users = JSON.parse(localStorage.getItem('bancoUsers') || '[]');
    return users.find(user => user.email === email && user.password === password);
}

// Función para registrar nuevo usuario
function registerUser(userData) {
    const users = JSON.parse(localStorage.getItem('bancoUsers') || '[]');
    
    // Verificar si el usuario ya existe
    const existingUser = users.find(user => 
        user.email === userData.email || 
        user.documentNumber === userData.documentNumber
    );
    
    if (existingUser) {
        return { success: false, message: 'Usuario ya existe' };
    }
    
    // Agregar nuevo usuario
    users.push(userData);
    localStorage.setItem('bancoUsers', JSON.stringify(users));
    return { success: true, message: 'Usuario registrado exitosamente' };
}

// Función para mostrar mensajes
function showMessage(message, type = 'info') {
    // Remover mensaje anterior si existe
    const existingMessage = document.querySelector('.message-notification');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message-notification message-${type}`;
    messageDiv.innerHTML = `
        <p>${message}</p>
        <button onclick="this.parentElement.remove()">×</button>
    `;
    
    // Estilos inline para el mensaje
    Object.assign(messageDiv.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        padding: '15px 20px',
        borderRadius: '8px',
        color: 'white',
        fontWeight: 'bold',
        zIndex: '10000',
        maxWidth: '400px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
        backgroundColor: type === 'error' ? '#dc3545' : 
                        type === 'success' ? '#28a745' : 
                        type === 'warning' ? '#ffc107' : '#0052cc',
        transform: 'translateX(100%)',
        transition: 'transform 0.3s ease'
    });
    
    messageDiv.querySelector('button').style.cssText = `
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        font-weight: bold;
        cursor: pointer;
        float: right;
        margin-left: 10px;
    `;
    
    document.body.appendChild(messageDiv);
    
    // Animar entrada
    setTimeout(() => {
        messageDiv.style.transform = 'translateX(0)';
    }, 100);
    
    // Auto-remover después de 5 segundos
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (messageDiv.parentNode) {
                    messageDiv.remove();
                }
            }, 300);
        }
    }, 5000);
}

// Inicialización cuando carga la página
document.addEventListener('DOMContentLoaded', () => {
    initializeUsers();
    console.log('Sistema de login iniciado (modo frontend)');
    
    // Manejar formulario de login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(e.target);
            const email = formData.get('email');
            const password = formData.get('password');
            
            if (!email || !password) {
                showMessage('Por favor completa todos los campos', 'error');
                return;
            }
            
            // Simular delay de conexión
            const submitBtn = e.target.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'Verificando...';
            submitBtn.disabled = true;
            
            setTimeout(() => {
                const user = validateLogin(email, password);
                
                if (user) {
                    // Guardar sesión
                    localStorage.setItem('currentUser', JSON.stringify(user));
                    localStorage.setItem('isLoggedIn', 'true');
                    
                    showMessage('¡Bienvenido! Redirigiendo...', 'success');
                    
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1500);
                } else {
                    showMessage('Credenciales incorrectas. Intenta con: usuario@banco.com / 1234', 'error');
                }
                
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }, 1000);
        });
    }
    
    // Manejar formulario de registro
    const registroForm = document.getElementById('registroForm');
    if (registroForm) {
        registroForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(e.target);
            const userData = {
                email: `${formData.get('documentNumber')}@banco.com`, // Email generado
                password: formData.get('claveCajero'),
                name: 'Usuario Nuevo',
                documentType: formData.get('tipoDocumento'),
                documentNumber: formData.get('numeroDocumento')
            };
            
            // Validar campos
            if (!userData.documentType || !userData.documentNumber || !userData.password) {
                showMessage('Por favor completa todos los campos', 'error');
                return;
            }
            
            if (userData.documentNumber.length < 6) {
                showMessage('El número de documento debe tener al menos 6 dígitos', 'error');
                return;
            }
            
            if (userData.password.length < 4) {
                showMessage('La clave debe tener al menos 4 caracteres', 'error');
                return;
            }
            
            // Simular delay de procesamiento
            const submitBtn = e.target.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'Registrando...';
            submitBtn.disabled = true;
            
            setTimeout(() => {
                const result = registerUser(userData);
                
                if (result.success) {
                    showMessage('¡Registro exitoso! Redirigiendo al login...', 'success');
                    setTimeout(() => {
                        window.location.href = 'login.html';
                    }, 2000);
                } else {
                    showMessage(result.message, 'error');
                }
                
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }, 1500);
        });
    }
});

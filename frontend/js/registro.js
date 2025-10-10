// URL base de la API
const API_URL = 'http://localhost:8081/api/auth';

// Cambiar tipo de cliente y mostrar campos correspondientes
function cambiarTipo(tipo) {
    // Actualizar valor oculto
    document.getElementById('tipoCliente').value = tipo;
    
    // Actualizar estilos de botones
    document.querySelectorAll('.tipo-btn').forEach(btn => {
        btn.classList.remove('active'); // Remover clase active de todos
    });
    // Añadir clase active al botón seleccionado
    event.target.classList.add('active');
    
    // Mostrar/ocultar campos según tipo
    const camposPersona = document.getElementById('camposPersona');
    const camposEmpresa = document.getElementById('camposEmpresa');
    
    // Mostrar campos según tipo
    if (tipo === 'PERSONA_NATURAL') { // PERSONA NATURAL
        camposPersona.classList.remove('hidden'); // Mostrar campos persona
        camposEmpresa.classList.add('hidden'); // Ocultar campos empresa
    } else { // EMPRESA
        camposPersona.classList.add('hidden'); // Ocultar campos persona
        camposEmpresa.classList.remove('hidden'); // Mostrar campos empresa
    }
}

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

// Manejar el envío del formulario de registro
document.getElementById('registroForm').addEventListener('submit', async (e) => {
    // Prevenir el envío por defecto
    e.preventDefault();
    
    // Obtener tipo de cliente
    const tipoCliente = document.getElementById('tipoCliente').value;
    
    // Preparar datos del formulario
    const data = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        tipoCliente: tipoCliente,
        telefono: document.getElementById('telefono').value,
        direccion: document.getElementById('direccion').value,
        ciudad: document.getElementById('ciudad').value
    };
    
    // Añadir campos según tipo
    if (tipoCliente === 'PERSONA_NATURAL') {
        data.tipoDocumento = document.getElementById('tipoDocumento').value;
        data.numDocumento = document.getElementById('numDocumento').value;
        data.nombres = document.getElementById('nombres').value;
        data.apellidos = document.getElementById('apellidos').value;
        data.fechaNacimiento = document.getElementById('fechaNacimiento').value || null;
    } else {
        data.nit = document.getElementById('nit').value;
        data.razonSocial = document.getElementById('razonSocial').value;
        data.nombreComercial = document.getElementById('nombreComercial').value || null;
        data.fechaConstitucion = document.getElementById('fechaConstitucion').value || null;
        data.numEmpleados = parseInt(document.getElementById('numEmpleados').value) || null; // Convertir a entero o null
        data.sectorEconomico = document.getElementById('sectorEconomico').value || null;
    }
    
    // Enviar petición a la API
    try {
        // Petición POST a /registro
        const response = await fetch(`${API_URL}/registro`, {
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
            let mensaje = '¡Registro exitoso!';
            if (result.numeroCuenta) {
                mensaje += `\nTu número de cuenta: ${result.numeroCuenta}`;
            }
            mostrarAlerta(mensaje, 'success');
            
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000); // Dar 3 segundos para que lean el número de cuenta
        } else {
            mostrarAlerta(result.message, 'error');
        }
    } catch (error) { // Error de red o servidor
        console.error('Error:', error);
        mostrarAlerta('Error al conectar con el servidor', 'error');
    }
});
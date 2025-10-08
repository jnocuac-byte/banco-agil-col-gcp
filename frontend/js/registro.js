const API_URL = 'http://localhost:8081/api/auth';

function cambiarTipo(tipo) {
    document.getElementById('tipoCliente').value = tipo;
    
    // Actualizar botones
    document.querySelectorAll('.tipo-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    // Mostrar/ocultar campos
    const camposPersona = document.getElementById('camposPersona');
    const camposEmpresa = document.getElementById('camposEmpresa');
    
    if (tipo === 'PERSONA_NATURAL') {
        camposPersona.classList.remove('hidden');
        camposEmpresa.classList.add('hidden');
    } else {
        camposPersona.classList.add('hidden');
        camposEmpresa.classList.remove('hidden');
    }
}

function mostrarAlerta(mensaje, tipo) {
    const alert = document.getElementById('alert');
    alert.textContent = mensaje;
    alert.className = `alert ${tipo}`;
    alert.style.display = 'block';
    
    setTimeout(() => {
        alert.style.display = 'none';
    }, 5000);
}

document.getElementById('registroForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const tipoCliente = document.getElementById('tipoCliente').value;
    
    const data = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        tipoCliente: tipoCliente,
        telefono: document.getElementById('telefono').value,
        direccion: document.getElementById('direccion').value,
        ciudad: document.getElementById('ciudad').value
    };
    
    // Agregar campos según tipo
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
        data.numEmpleados = parseInt(document.getElementById('numEmpleados').value) || null;
        data.sectorEconomico = document.getElementById('sectorEconomico').value || null;
    }
    
    try {
        const response = await fetch(`${API_URL}/registro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (result.success) {
            mostrarAlerta('¡Registro exitoso! Redirigiendo...', 'success');
            setTimeout(() => {
                // TODO: Redirigir al dashboard
                window.location.href = 'index.html';
            }, 2000);
        } else {
            mostrarAlerta(result.message, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al conectar con el servidor', 'error');
    }
});
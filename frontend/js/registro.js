// URL base de la API
const API_URL = 'https://auth-service-514751056677.us-central1.run.app/api/auth';

// Cambiar tipo de cliente y mostrar campos correspondientes
function cambiarTipo(tipo) {
    // Actualizar valor oculto
    document.getElementById('tipoCliente').value = tipo;
    
    // Actualizar estilos de botones
    document.querySelectorAll('.tipo-btn').forEach(btn => {
        btn.classList.remove('active'); // Remover clase active de todos
    });
    // Añadir clase active al botón seleccionado
    // Nota: event.target dentro de cambiarTipo() se refiere al botón que fue clickeado
    // Esto asume que la función fue llamada por un evento de click en los botones
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
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
// Mostrar alertas con opción de confirm
// mostrarAlerta(mensaje, tipo, options)
// options: { confirm: boolean, confirmText: string, onConfirm: function, autoHideMs: number }
function mostrarAlerta(mensaje, tipo, options = {}) {
    // tipo: 'success', 'error', 'info', 'warning'
    const alertEl = document.getElementById('alert');
    // Limpiar clases y contenido
    alertEl.className = '';
    alertEl.classList.add('alert');
    if (tipo) alertEl.classList.add(tipo);

    // Build content without emoji and without markdown
    const content = document.createElement('div');
    content.className = 'alert-content';
    content.textContent = mensaje;

    // Clear existing
    alertEl.innerHTML = '';
    alertEl.appendChild(content);

    // If confirm button requested, add it and do NOT auto-hide unless specified
    if (options.confirm) {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn-confirm';
        btn.textContent = options.confirmText || 'Confirmar';
        btn.style.marginTop = '12px';
        btn.style.padding = '8px 14px';
        btn.style.borderRadius = '6px';
        btn.style.border = 'none';
        btn.style.cursor = 'pointer';
        // Style depending on type
        if (tipo === 'error') {
            btn.style.background = '#ffffff';
            btn.style.color = '#f44336';
            btn.style.fontWeight = '600';
        } else {
            btn.style.background = 'var(--primary-dark)';
            btn.style.color = '#ffffff';
            btn.style.fontWeight = '600';
        }

        btn.addEventListener('click', () => {
            // Call handler if provided
            if (typeof options.onConfirm === 'function') {
                try { options.onConfirm(); } catch (e) { console.error(e); }
            }
            alertEl.style.display = 'none';
        });

        alertEl.appendChild(btn);
        alertEl.style.display = 'block';
        return;
    }

    // Otherwise show and auto-hide (default 5s or provided)
    alertEl.style.display = 'block';
    const hideMs = typeof options.autoHideMs === 'number' ? options.autoHideMs : 5000;
    if (hideMs > 0) {
        setTimeout(() => { alertEl.style.display = 'none'; }, hideMs);
    }
}

// *** VALIDACIÓN DE CAMPOS Y MANEJO DE ENVÍO ***
document.getElementById('registroForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const tipoCliente = document.getElementById('tipoCliente').value;
    let camposFaltantes = []; // Lista para almacenar los nombres de los campos faltantes

    const camposComunes = [
        { id: 'email', nombre: 'Email' },
        { id: 'password', nombre: 'Contraseña' },
        { id: 'telefono', nombre: 'Teléfono' },
        { id: 'direccion', nombre: 'Dirección' },
        { id: 'ciudad', nombre: 'Ciudad' }
    ];

    camposComunes.forEach(campo => {
        const elemento = document.getElementById(campo.id);
        // Si es un campo requerido y su valor está vacío, lo agregamos a la lista
        if (elemento && !elemento.value.trim()) {
            camposFaltantes.push(campo.nombre);
        }
    });

    if (tipoCliente === 'PERSONA_NATURAL') {
        const camposPersona = [
            { id: 'numDocumento', nombre: 'Número de Documento' },
            { id: 'nombres', nombre: 'Nombres' },
            { id: 'apellidos', nombre: 'Apellidos' }
        ];
        camposPersona.forEach(campo => {
            const elemento = document.getElementById(campo.id);
            if (elemento && !elemento.value.trim()) {
                camposFaltantes.push(campo.nombre);
            }
        });

        // Además, la contraseña debe tener al menos 6 caracteres (aunque ya está en el HTML, es mejor validar aquí también)
        if (document.getElementById('password').value.length < 6) {
            mostrarAlerta('La Contraseña debe tener al menos 6 caracteres.', 'error');
            return; // Detener el envío
        }

    } else if (tipoCliente === 'EMPRESA') {
        const camposEmpresa = [
            { id: 'nit', nombre: 'NIT' },
            { id: 'razonSocial', nombre: 'Razón Social' }
        ];
        camposEmpresa.forEach(campo => {
            const elemento = document.getElementById(campo.id);
            if (elemento && !elemento.value.trim()) {
                camposFaltantes.push(campo.nombre);
            }
        });
    }

    if (camposFaltantes.length > 0) {
        // Construir un mensaje claro con los campos que faltan (sin emojis ni markdown)
        const mensajeError = `Por favor, ingresa los siguientes campos obligatorios: ${camposFaltantes.join(', ')}`;
        // Mostrar alerta con botón de confirmación para que el usuario la cierre explícitamente
        mostrarAlerta(mensajeError, 'error', {
            confirm: true,
            confirmText: 'Aceptar',
            onConfirm: function() {
                // Focus the first empty input/select inside the form
                const fields = document.querySelectorAll('#registroForm input, #registroForm select');
                for (let i = 0; i < fields.length; i++) {
                    const el = fields[i];
                    if (el.type === 'hidden') continue;
                    if (!el.value || !el.value.toString().trim()) {
                        try { el.focus(); } catch (e) {}
                        break;
                    }
                }
            }
        });
        return; // Detener el envío del formulario
    }
    
    // PREPARAR DATOS Y ENVIAR A LA API 
    const data = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        tipoCliente: tipoCliente,
        telefono: document.getElementById('telefono').value,
        direccion: document.getElementById('direccion').value,
        ciudad: document.getElementById('ciudad').value
    };
    
    // Añadir campos específicos 
    if (tipoCliente === 'PERSONA_NATURAL') {
        data.tipoDocumento = document.getElementById('tipoDocumento').value;
        data.numDocumento = document.getElementById('numDocumento').value;
        data.nombres = document.getElementById('nombres').value;
        data.apellidos = document.getElementById('apellidos').value;
        // Usar null para campos opcionales vacíos
        data.fechaNacimiento = document.getElementById('fechaNacimiento').value || null;
    } else {
        data.nit = document.getElementById('nit').value;
        data.razonSocial = document.getElementById('razonSocial').value;
        // Usar null para campos opcionales vacíos
        data.nombreComercial = document.getElementById('nombreComercial').value || null;
        data.fechaConstitucion = document.getElementById('fechaConstitucion').value || null;
        // Convertir a entero o null, asegurando que el valor no sea solo un espacio
        const numEmpleadosVal = document.getElementById('numEmpleados').value.trim();
        data.numEmpleados = numEmpleadosVal ? parseInt(numEmpleadosVal) : null; 
        data.sectorEconomico = document.getElementById('sectorEconomico').value || null;
    }
    
    // Enviar petición a la API
    try {
        // *Aquí la URL base de la API debe ser solo la base, sin '/registro.html' o incluir la ruta correcta del endpoint*
        // Asumiendo que el endpoint es /registro en la raíz del servicio
        const BASE_URL = 'https://frontend-service-514751056677.us-central1.run.app';
        const response = await fetch(`${BASE_URL}/registro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (result.success) {
            let mensaje = 'Registro exitoso.';
            if (result.numeroCuenta) {
                mensaje += ` Tu número de cuenta: ${result.numeroCuenta}`;
            }
            mostrarAlerta(mensaje, 'success');
            
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000);
        } else {
            // Maneja errores de la lógica de negocio del servidor (ej. email ya existe)
            mostrarAlerta(`Error: ${result.message}`, 'error', { confirm: true, confirmText: 'Aceptar' });
        }
    } catch (error) {
        console.error('Error de red o servidor:', error);
        mostrarAlerta('Error al conectar con el servidor. Intenta de nuevo más tarde.', 'error', { confirm: true, confirmText: 'Aceptar' });
    }
});
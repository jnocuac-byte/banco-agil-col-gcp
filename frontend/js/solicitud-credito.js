const API_CREDIT_URL = 'http://localhost:8082/api/solicitudes';
const API_DOCUMENTOS_URL = 'http://localhost:8082/api/documentos';

// =================================================================================
// FUNCIONES DE UI Y SESIÓN (COPIADAS DE dashboard.js)
// =================================================================================

// Lógica para alternar la visibilidad del menú lateral (MenuTogle)
function initializeMenuToggle() {
    const menuToggle = document.getElementById('menuToggle'); 
    
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            // El menú lateral debe tener la clase .sidebar
            const sidebar = document.querySelector('.sidebar');
            if (sidebar) {
                 sidebar.classList.toggle('sidebar-expanded');
                 console.log('Menú lateral toggled (clase sidebar-expanded alternada).');
            } else {
                 console.warn('Elemento con clase ".sidebar" no encontrado para expandir/contraer.');
            }
        });
        console.log('Listener de Menu Toggle (ID: menuToggle) asignado.');
    } else {
        console.error('ERROR CRÍTICO: Botón de Menu Toggle (ID: menuToggle) no encontrado.');
    }
}

// Cerrar sesión
function logout() {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('usuario');
        window.location.href = 'login.html';
    }
}
// =================================================================================
// FIN DE FUNCIONES DE UI Y SESIÓN
// =================================================================================


// Verificar autenticación al cargar la página
(function verificarSesion() {
    const usuario = sessionStorage.getItem('usuario');
    if (!usuario) {
        window.location.href = 'login.html';
        return;
    }
    cargarDatosUsuario();
})();

// Obtener datos del usuario
function obtenerUsuario() {
    const usuario = sessionStorage.getItem('usuario');
    if (!usuario) {
        window.location.href = 'login.html';
        return null;
    }
    return JSON.parse(usuario);
}

// Cargar datos del usuario en el header
function cargarDatosUsuario() {
    const usuario = obtenerUsuario();
    if (usuario) {
        const nombreUsuarioElement = document.getElementById('nombre-usuario');
        if (nombreUsuarioElement) {
            nombreUsuarioElement.textContent = usuario.nombreCompleto || 'Usuario';
        }
    }
}

// =================================================================================
// LÓGICA DE SOLICITUD DE CRÉDITO
// =================================================================================

function calcularCuota() {
    const montoInput = document.getElementById('montoSolicitado');
    const plazoInput = document.getElementById('plazoMeses');
    const cuotaResultado = document.getElementById('cuota-resultado');
    
    if (!montoInput || !plazoInput || !cuotaResultado) {
        return;
    }
    
    const monto = parseFloat(montoInput.value) || 0;
    const plazo = parseInt(plazoInput.value) || 0;
    let tasaAnual = 0; 

    if (monto <= 0 || plazo <= 0) {
        cuotaResultado.textContent = '$0';
        return;
    }

    // Lógica para determinar la TASA ANUAL
    if (monto < 10000000) {
        tasaAnual = 12.0; 
    } else if (monto < 50000000) {
        tasaAnual = 10.0; 
    } else {
        tasaAnual = 8.5; 
    }

    const tasaMensual = (tasaAnual / 100) / 12;

    if (tasaMensual === 0) {
        const cuota = monto / plazo;
        cuotaResultado.textContent = new Intl.NumberFormat('es-CO', { 
            style: 'currency', 
            currency: 'COP',
            minimumFractionDigits: 0
        }).format(cuota);
        return;
    }

    // Fórmula de Amortización
    const potencia = Math.pow(1 + tasaMensual, plazo);
    const cuota = monto * (tasaMensual * potencia) / (potencia - 1);
    
    cuotaResultado.textContent = new Intl.NumberFormat('es-CO', { 
        style: 'currency', 
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(cuota);
}

function validarDocumentos() {
    const documentosRequeridos = ['rut', 'cedula', 'estados', 'camara'];
    const documentosFaltantes = [];
    
    documentosRequeridos.forEach(tipo => {
        const input = document.getElementById(`doc-${tipo}`);
        if (!input || !input.files || input.files.length === 0) {
            documentosFaltantes.push(tipo);
        }
    });
    
    return documentosFaltantes;
}

function validarTamanoArchivo(archivo) {
    const maxSize = 10 * 1024 * 1024; // 10MB
    return archivo.size <= maxSize;
}

async function subirDocumento(solicitudId, tipoDocumento, archivo) {
    if (!validarTamanoArchivo(archivo)) {
        throw new Error('El archivo excede el tamaño máximo de 10MB');
    }
    
    const formData = new FormData();
    formData.append('tipoDocumento', tipoDocumento);
    formData.append('file', archivo);
    
    const response = await fetch(`${API_DOCUMENTOS_URL}/solicitud/${solicitudId}/subir`, {
        method: 'POST',
        body: formData
    });
    
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error al subir documento');
    }
    
    return await response.json();
}

async function subirDocumentos(solicitudId) {
    const documentos = [
        { tipo: 'RUT', inputId: 'doc-rut', statusId: 'status-rut' },
        { tipo: 'CEDULA', inputId: 'doc-cedula', statusId: 'status-cedula' },
        { tipo: 'ESTADOS_FINANCIEROS', inputId: 'doc-estados', statusId: 'status-estados' },
        { tipo: 'CAMARA_COMERCIO', inputId: 'doc-camara', statusId: 'status-camara' }
    ];
    
    let todosSubidos = true;
    
    for (const doc of documentos) {
        const input = document.getElementById(doc.inputId);
        const status = document.getElementById(doc.statusId);
        
        if (!input || !status) continue;
        
        if (input.files && input.files.length > 0) {
            try {
                status.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Subiendo...';
                status.className = 'file-status';
                await subirDocumento(solicitudId, doc.tipo, input.files[0]);
                status.innerHTML = '<i class="fas fa-check-circle"></i> Subido correctamente';
                status.className = 'file-status success';
            } catch (error) {
                console.error(`Error subiendo ${doc.tipo}:`, error);
                status.innerHTML = `<i class="fas fa-exclamation-circle"></i> Error: ${error.message}`;
                status.className = 'file-status error';
                todosSubidos = false;
            }
        }
    }
    
    return todosSubidos;
}

function mostrarAlerta(mensaje, tipo) {
    const alert = document.getElementById('alert');
    if (!alert) {
        console.warn('Elemento alert no encontrado');
        return;
    }
    
    alert.textContent = mensaje;
    alert.className = `alert ${tipo}`;
    alert.style.display = 'block';
    
    if (tipo !== 'error') {
        setTimeout(() => {
            alert.style.display = 'none';
        }, 5000);
    }
}

async function enviarSolicitud(event) {
    event.preventDefault();
    
    const usuario = obtenerUsuario();
    if (!usuario) {
        mostrarAlerta('No hay sesión activa', 'error');
        return;
    }
    
    const documentosFaltantes = validarDocumentos();
    if (documentosFaltantes.length > 0) {
        mostrarAlerta(`Faltan documentos: ${documentosFaltantes.join(', ')}`, 'error');
        return;
    }
    
    const montoInput = document.getElementById('montoSolicitado');
    const plazoInput = document.getElementById('plazoMeses');
    const observacionesInput = document.getElementById('observaciones');
    
    if (!montoInput || !plazoInput) {
        mostrarAlerta('Error: Formulario incompleto', 'error');
        return;
    }
    
    const monto = parseFloat(montoInput.value);
    const plazo = parseInt(plazoInput.value);
    const observaciones = observacionesInput ? observacionesInput.value : '';
    
    if (!monto || monto <= 0 || !plazo || plazo <= 0) {
        mostrarAlerta('Monto y plazo deben ser mayores a cero', 'error');
        return;
    }
    
    const solicitudData = {
        idCliente: usuario.clienteId, 
        montoSolicitado: monto,
        plazoMeses: plazo,
        observaciones: observaciones
    };
    
    const btnEnviar = document.querySelector('button[type="submit"]');
    if (btnEnviar) {
        btnEnviar.disabled = true;
        btnEnviar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
    }
    
    try {
        const responseSolicitud = await fetch(`${API_CREDIT_URL}/crear`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(solicitudData)
        });
        
        const resultSolicitud = await responseSolicitud.json();
        
        if (!resultSolicitud.success) {
            throw new Error(resultSolicitud.message || 'Error al crear solicitud');
        }
        
        mostrarAlerta('Solicitud creada. Subiendo documentos...', 'info');
        
        const documentosSubidos = await subirDocumentos(resultSolicitud.solicitudId);
        
        if (documentosSubidos) {
            mostrarAlerta('¡Solicitud creada exitosamente con todos los documentos!', 'success');
            setTimeout(() => {
                window.location.href = 'mis-solicitudes.html';
            }, 2000); 
        } else {
            mostrarAlerta('Solicitud creada, pero algunos documentos fallaron. Revisa los mensajes de error.', 'warning');
        }
        
    } catch (error) {
        console.error('Error en enviarSolicitud:', error);
        mostrarAlerta('Error: ' + error.message, 'error');
        
        if (btnEnviar) {
            btnEnviar.disabled = false;
            btnEnviar.innerHTML = '<i class="fas fa-paper-plane"></i> Enviar Solicitud';
        }
    }
}

// Inicializar eventos cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM cargado, inicializando eventos...');
    
    // 1. Inicializar el toggle del menú lateral
    initializeMenuToggle();

    // 2. Asignar el listener de logout al botón
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    } 

    // 3. Listeners para el cálculo de cuota
    const montoInput = document.getElementById('montoSolicitado');
    const plazoInput = document.getElementById('plazoMeses');
    
    if (montoInput) {
        montoInput.addEventListener('input', calcularCuota);
    }
    
    if (plazoInput) {
        plazoInput.addEventListener('input', calcularCuota);
    }
    
    // 4. Listener para el formulario
    const form = document.getElementById('form-solicitud');
    if (form) {
        form.addEventListener('submit', enviarSolicitud);
    } 
    
    // 5. Calcular cuota inicial
    calcularCuota();
});
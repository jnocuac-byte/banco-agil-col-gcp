// URL base de la API solicitudes y documentos
// TEMPORAL: // TEMPORAL: // TEMPORAL: const API_CREDIT_URL = 'http://localhost:8083/api/solicitudes';
// TEMPORAL: // TEMPORAL: // TEMPORAL: const API_DOCUMENTOS_URL = 'http://localhost:8083/api/documentos';

// Inicializar menú lateral
function initializeMenuToggle() {
    // Botón para expandir/contraer menú lateral
    const menuToggle = document.getElementById('menuToggle'); 
    
    // Asegurarse que el botón existe
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            // El menú lateral debe tener la clase .sidebar
            const sidebar = document.querySelector('.sidebar');
            // Asegurarse que el sidebar existe
            if (sidebar) {
                 sidebar.classList.toggle('sidebar-expanded'); // Alternar clase
                 console.log('Menú lateral toggled (clase sidebar-expanded alternada).'); // Debug
            } else { // Si no existe, log de advertencia
                 console.warn('Elemento con clase ".sidebar" no encontrado para expandir/contraer.');
            }
        }); // Listener de click
        console.log('Listener de Menu Toggle (ID: menuToggle) asignado.'); // Debug
    } else { // Si no existe, log de error crítico
        console.error('ERROR CRÍTICO: Botón de Menu Toggle (ID: menuToggle) no encontrado.');
    }
}

// Cerrar sesión
function logout() {
    // Confirmar acción
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        sessionStorage.removeItem('usuario'); // Eliminar datos de sessionStorage
        window.location.href = 'login.html'; // Redirigir al login
    }
}

// Verificar sesión al cargar la página
(function verificarSesion() {
    // Obtener datos del usuario
    const usuario = sessionStorage.getItem('usuario');
    if (!usuario) { // Si no hay sesión, redirigir al login
        window.location.href = 'login.html';
        return;
    }
    cargarDatosUsuario(); // Cargar datos del usuario en la interfaz
})();

// Función autoejecutable para verificar sesión al cargar la página
function obtenerUsuario() {
    // Obtener datos del usuario desde sessionStorage
    const usuario = sessionStorage.getItem('usuario');
    // Si no hay sesión, redirigir al login
    if (!usuario) {
        window.location.href = 'login.html';
        return null;
    }
    return JSON.parse(usuario); // Parsear y retornar objeto usuario
}

// Función para obtener el nombre del usuario y mostrarlo en la interfaz
function cargarDatosUsuario() {
    // Obtener datos del usuario
    const usuario = obtenerUsuario();
    // Si hay usuario, mostrar su nombre en el elemento con id 'nombre-usuario'
    if (usuario) {
        const nombreUsuarioElement = document.getElementById('nombre-usuario'); // Elemento en el header
        if (nombreUsuarioElement) { // Si el elemento existe, actualizar su contenido
            nombreUsuarioElement.textContent = usuario.nombreCompleto || 'Usuario'; // Mostrar nombre completo o 'Usuario' como fallback
        }
    }
}

// Calcular cuota mensual
function calcularCuota() {
    // Referencias a inputs y resultado
    const montoInput = document.getElementById('montoSolicitado');
    const plazoInput = document.getElementById('plazoMeses');
    const cuotaResultado = document.getElementById('cuota-resultado');
    
    // Validar que los elementos existen
    if (!montoInput || !plazoInput || !cuotaResultado) {
        return; // Si no existen, salir de la función
    }
    
    // Obtener valores y convertir a números
    const monto = parseFloat(montoInput.value) || 0;
    const plazo = parseInt(plazoInput.value) || 0;
    let tasaAnual = 0; 

    // Validar monto y plazo
    if (monto <= 0 || plazo <= 0) {
        cuotaResultado.textContent = '$0'; // Si monto o plazo no son válidos, mostrar $0 y sali
        return;
    }

    // Determinar tasa anual según monto
    if (monto < 10000000) {
        tasaAnual = 12.0; // Tasa del 12% anual
    } else if (monto < 50000000) {
        tasaAnual = 10.0; // Tasa del 10% anual
    } else {
        tasaAnual = 8.5; // Tasa del 8.5% anual
    }

    // Calcular cuota mensual usando fórmula de amortización
    const tasaMensual = (tasaAnual / 100) / 12;

    // Si la tasa es 0 (caso borde), evitar división por cero
    if (tasaMensual === 0) {
        // Cuota significa que no hay interés
        const cuota = monto / plazo;

        // Formatear y mostrar resultado
        cuotaResultado.textContent = new Intl.NumberFormat('es-CO', { 
            style: 'currency', 
            currency: 'COP',
            minimumFractionDigits: 0
        }).format(cuota); // Ejemplo: $1.000.000
        return;
    }

    // Fórmula de amortización
    const potencia = Math.pow(1 + tasaMensual, plazo);
    const cuota = monto * (tasaMensual * potencia) / (potencia - 1);
    
    // Formatear y mostrar resultado
    cuotaResultado.textContent = new Intl.NumberFormat('es-CO', { 
        style: 'currency', 
        currency: 'COP',
        minimumFractionDigits: 0
    }).format(cuota); // Ejemplo: $1.000.000
}

// Validar documentos seleccionados
function validarDocumentos() {
    // Documentos requeridos
    const documentosRequeridos = ['rut', 'cedula', 'estados', 'camara'];
    const documentosFaltantes = [];
    
    // Verificar cada documento
    documentosRequeridos.forEach(tipo => {
        // Obtener input file correspondiente
        const input = document.getElementById(`doc-${tipo}`);
        // Si no existe o no tiene archivo seleccionado, agregar a faltantes
        if (!input || !input.files || input.files.length === 0) {
            documentosFaltantes.push(tipo); // Agregar tipo de documento faltante
        }
    });
    
    // Retornar array de documentos faltantes
    return documentosFaltantes;
}

// Validar tamaño de archivo (máx 10MB)
function validarTamanoArchivo(archivo) {
    const maxSize = 10 * 1024 * 1024; // 10MB
    return archivo.size <= maxSize; // Retorna true si es válido
}

// Subir un documento
async function subirDocumento(solicitudId, tipoDocumento, archivo) {
    // Validar tamaño del archivo
    if (!validarTamanoArchivo(archivo)) {
        throw new Error('El archivo excede el tamaño máximo de 10MB'); // Lanzar error si es muy grande
    }
    
    // Preparar FormData para envío
    const formData = new FormData();
    formData.append('tipoDocumento', tipoDocumento);
    formData.append('file', archivo);
    
    // Enviar petición POST al endpoint de subir documento
    const response = await fetch(`${API_DOCUMENTOS_URL}/solicitud/${solicitudId}/subir`, {
        method: 'POST',
        body: formData
    }); // No se necesitan headers, el navegador los maneja
    
    // Manejar respuesta
    if (!response.ok) {
        const error = await response.json(); // Intentar parsear mensaje de error
        throw new Error(error.message || 'Error al subir documento'); // Lanzar error con mensaje
    }
    
    // Retornar respuesta JSON
    return await response.json();
}

// Subir todos los documentos
async function subirDocumentos(solicitudId) {
    // Tipos de documentos y sus respectivos IDs de input y status
    const documentos = [
        { tipo: 'RUT', inputId: 'doc-rut', statusId: 'status-rut' },
        { tipo: 'CEDULA', inputId: 'doc-cedula', statusId: 'status-cedula' },
        { tipo: 'ESTADOS_FINANCIEROS', inputId: 'doc-estados', statusId: 'status-estados' },
        { tipo: 'CAMARA_COMERCIO', inputId: 'doc-camara', statusId: 'status-camara' }
    ];
    
    // Variable para rastrear si todos los documentos se subieron correctamente
    let todosSubidos = true;
    
    // Iterar sobre cada documento y subir si está seleccionado
    for (const doc of documentos) {
        // Obtener referencia al input y al span de estado
        const input = document.getElementById(doc.inputId);
        const status = document.getElementById(doc.statusId);
        
        // Si no existe el input o el status, continuar al siguiente
        if (!input || !status) continue;
        
        // Si hay archivo seleccionado, intentar subirlo
        if (input.files && input.files.length > 0) {
            // Mostrar estado de subida
            try {
                // Indicar que se está subiendo
                status.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Subiendo...';
                status.className = 'file-status';
                // Intentar subir el documento
                await subirDocumento(solicitudId, doc.tipo, input.files[0]);
                // Si se sube correctamente, actualizar estado
                status.innerHTML = '<i class="fas fa-check-circle"></i> Subido correctamente';
                status.className = 'file-status success';
            } catch (error) { // Si hay error al subir, mostrar mensaje de error
                console.error(`Error subiendo ${doc.tipo}:`, error); // Log de error para debug
                // Actualizar estado a error
                status.innerHTML = `<i class="fas fa-exclamation-circle"></i> Error: ${error.message}`;
                status.className = 'file-status error';
                todosSubidos = false; // Marcar que no todos se subieron correctamente
            }
        }
    }
    
    // Retornar si todos los documentos se subieron correctamente
    return todosSubidos;
}

// Mostrar alerta en el DOM
function mostrarAlerta(mensaje, tipo) {
    // Referencia al contenedor de alertas
    const alert = document.getElementById('alert');
    // Validar que el elemento existe
    if (!alert) {
        console.warn('Elemento alert no encontrado'); // Debug
        return;
    }
    
    // Actualizar contenido y clase según tipo
    alert.textContent = mensaje;
    alert.className = `alert ${tipo}`;
    alert.style.display = 'block';
    
    // Si no es error, ocultar después de 5 segundos
    if (tipo !== 'error') {
        // Añadir transición de desvanecimiento
        setTimeout(() => {
            alert.style.display = 'none';
        }, 5000); // 5 segundos
    }
}

// Manejar el envío del formulario de solicitud
async function enviarSolicitud(event) {
    // Prevenir el envío por defecto
    event.preventDefault();
    
    // Validar sesión
    const usuario = obtenerUsuario();
    // Si no hay usuario, mostrar alerta y salir
    if (!usuario) {
        mostrarAlerta('No hay sesión activa', 'error');
        return;
    }
    
    // Validar documentos
    const documentosFaltantes = validarDocumentos();
    // Si faltan documentos, mostrar alerta y salir
    if (documentosFaltantes.length > 0) {
        mostrarAlerta(`Faltan documentos: ${documentosFaltantes.join(', ')}`, 'error'); // Mostrar cuáles faltan
        return;
    }
    
    // Obtener y validar datos del formulario
    const montoInput = document.getElementById('montoSolicitado');
    const plazoInput = document.getElementById('plazoMeses');
    const observacionesInput = document.getElementById('observaciones');
    
    // Validar que los elementos existen
    if (!montoInput || !plazoInput) {
        mostrarAlerta('Error: Formulario incompleto', 'error');
        return;
    }
    
    // Obtener valores y convertir a números
    const monto = parseFloat(montoInput.value);
    const plazo = parseInt(plazoInput.value);
    const observaciones = observacionesInput ? observacionesInput.value : '';
    
    // Validar monto y plazo
    if (!monto || monto <= 0 || !plazo || plazo <= 0) {
        mostrarAlerta('Monto y plazo deben ser mayores a cero', 'error');
        return;
    }
    
    // Preparar datos de la solicitud
    const solicitudData = {
        idCliente: usuario.clienteId, 
        montoSolicitado: monto,
        plazoMeses: plazo,
        observaciones: observaciones
    };
    
    // Deshabilitar botón de enviar y mostrar loading
    const btnEnviar = document.querySelector('button[type="submit"]');
    // Validar que el botón existe
    if (btnEnviar) {
        btnEnviar.disabled = true; // Deshabilitar botón
        btnEnviar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...'; // Mostrar loading
    }
    
    // Enviar solicitud a la API
    try {
        // Petición POST a /crear
        const responseSolicitud = await fetch(`${API_CREDIT_URL}/crear`, {
            method: 'POST', // Metodo POST y headers JSON
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(solicitudData) // Enviar datos como JSON
        });
        
        // Manejar respuesta
        const resultSolicitud = await responseSolicitud.json();
        
        // Si no fue exitosa, lanzar error
        if (!resultSolicitud.success) {
            throw new Error(resultSolicitud.message || 'Error al crear solicitud');
        }
        
        // Mostrar alerta de subida de documentos
        mostrarAlerta('Solicitud creada. Subiendo documentos...', 'info');
        
        // Subir documentos
        const documentosSubidos = await subirDocumentos(resultSolicitud.solicitudId);
        
        // Mostrar alerta final según si todos los documentos se subieron correctamente
        if (documentosSubidos) {
            mostrarAlerta('¡Solicitud creada exitosamente con todos los documentos!', 'success');
            // Redirigir a mis-solicitudes después de 2 segundos
            setTimeout(() => {
                window.location.href = 'mis-solicitudes.html'; // Redirigir a mis-solicitudes.html
            }, 2000); 
        } else { // Si hubo errores al subir algunos documentos
            mostrarAlerta('Solicitud creada, pero algunos documentos fallaron. Revisa los mensajes de error.', 'warning'); // Mensaje de advertencia
        }
        
    } catch (error) { // Manejar errores
        // Log de error para debug
        console.error('Error en enviarSolicitud:', error);
        mostrarAlerta('Error: ' + error.message, 'error');
        
        // Rehabilitar botón de enviar
        if (btnEnviar) {
            btnEnviar.disabled = false;
            btnEnviar.innerHTML = '<i class="fas fa-paper-plane"></i> Enviar Solicitud';
        }
    }
}

// Inicializar eventos al cargar el DOM
document.addEventListener('DOMContentLoaded', function() {
    // Debug
    console.log('DOM cargado, inicializando eventos...');
    
    // Inicializar menú lateral
    initializeMenuToggle();

    // Asignar evento al botón de logout
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    } 
    
    // Asignar eventos a inputs de monto y plazo para calcular cuota
    const montoInput = document.getElementById('montoSolicitado');
    const plazoInput = document.getElementById('plazoMeses');
    
    // Validar que los inputs existen antes de asignar eventos
    if (montoInput) {
        montoInput.addEventListener('input', calcularCuota); // Recalcular al cambiar monto
    }
    
    // Validar que el input de plazo existe
    if (plazoInput) {
        plazoInput.addEventListener('input', calcularCuota); // Recalcular al cambiar plazo
    }
    
    // Asignar evento al formulario de solicitud
    const form = document.getElementById('form-solicitud');
    // Validar que el formulario existe
    if (form) {
        form.addEventListener('submit', enviarSolicitud); // Manejar envío del formulario
    } 
    
    // Calcular cuota inicial al cargar la página
    calcularCuota();
});
const API_URL = 'https://auth-service-514751056677.us-central1.run.app/api/auth';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('user-creation-form');
    const stepDivs = [
        document.getElementById('step-1'),
        document.getElementById('step-2'),
        document.getElementById('step-3')
    ];
    const stepIndicators = [
        document.getElementById('step1-indicator'),
        document.getElementById('step2-indicator'),
        document.getElementById('step3-indicator')
    ];
    let currentStep = 0;
    let currentClientType = 'natural';

    const inputStates = {};

    const btnNaturalType = document.getElementById('btn-natural-type');
    const btnEmpresaType = document.getElementById('btn-empresa-type');

    const step1Btn = document.getElementById('btn-step1');
    const step2ContinueBtn = document.querySelector('#step-2 .btn-primary');
    const step3FinalizeBtn = document.querySelector('#step-3 .btn-primary');

    const docType = document.getElementById('document_type');
    const docNumber = document.getElementById('document_number');
    const NameComplet = document.getElementById('NameComplet');
    const LastnameComplet = document.getElementById('LastnameComplet');

    const newCorreo = document.getElementById('new_correo');
    const newPassword = document.getElementById('new_password');
    const confirmPassword = document.getElementById('confirm_password');
    
    const phone = document.getElementById('phone');
    const address = document.getElementById('address');
    const ciudad = document.getElementById('ciudad_nacimiento');
    const fechaInput = document.getElementById('fecha_nacimiento');
    const sectorEconomicoGroup = document.getElementById('sector_economico_group');
    const nombreComercialGroup = document.getElementById('nombre_comercial_group');
    const nombreComercial = document.getElementById('nombre_comercial');
    const sectorEconomico = document.getElementById('sector_economico');
    const numEmpleadosGroup = document.getElementById('num_empleados_group');
    const numEmpleados = document.getElementById('num_empleados');

    const alertEl = document.getElementById('alert');

    // --- Funciones de Utilidad ---
    function getStep1Inputs() {
        return [docType, docNumber, NameComplet, LastnameComplet, nombreComercial, numEmpleados];
    }

    function removeErrorState(input) {
        if (!input) return;
        input.classList.remove('input-error');
        const group = input.closest('.input-group');
        group?.querySelector('.error-icon')?.remove();
        group?.querySelector('.error-message')?.remove();
    }

    function addErrorState(input, message) {
        if (!input) return; 
        input.classList.add('input-error');
        const group = input.closest('.input-group');
        if (!group) return;
        
        let errorIcon = group.querySelector('.error-icon');
        if (!errorIcon) {
            errorIcon = document.createElement('i');
            errorIcon.className = 'fa-solid fa-exclamation-circle error-icon';
            group.appendChild(errorIcon); 
        }
        
        let errorMessage = group.querySelector('.error-message');
        if (!errorMessage) {
            errorMessage = document.createElement('p');
            errorMessage.className = 'error-message';
            group.appendChild(errorMessage); 
        }
        errorMessage.textContent = message;
    }

    function mostrarAlerta(mensaje, tipo, options = {}) {
        if (!alertEl) return;

        alertEl.className = '';
        alertEl.classList.add('alert');
        if (tipo) alertEl.classList.add(tipo);

        const content = document.createElement('div');
        content.className = 'alert-content';

        const messageP = document.createElement('p');
        messageP.textContent = mensaje;
        content.appendChild(messageP);

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn-confirm';
        btn.textContent = options.confirmText || 'Confirmar';
        btn.onclick = () => {
            alertEl.style.display = 'none';
            if (typeof options.onConfirm === 'function') {
                options.onConfirm();
            }
        };
        content.appendChild(btn);

        alertEl.innerHTML = '';
        alertEl.appendChild(content);
        alertEl.style.display = 'block';
    }

    // --- Lógica de Navegación y UI ---
    function goToStep(stepIndex) {
        if (stepIndex < 0 || stepIndex >= stepDivs.length) return;
        
        stepDivs.forEach(div => div.classList.remove('active'));
        stepIndicators.forEach(indicator => indicator.classList.remove('active'));
        
        stepDivs[stepIndex].classList.add('active');
        stepIndicators[stepIndex].classList.add('active');
        currentStep = stepIndex;
        
        [...getStep1Inputs(), newCorreo, newPassword, confirmPassword, phone, address, ciudad, fechaInput, sectorEconomico].forEach(removeErrorState);
        updateButtonState();
    }
    
    function goToNextStep() {
        goToStep(currentStep + 1);
    }

    function switchUserType(type) {
        currentClientType = type;
        const isEmpresa = (type === 'empresa');
        
        btnEmpresaType.classList.toggle('active', isEmpresa);
        btnNaturalType.classList.toggle('active', !isEmpresa);

        // Actualizar etiqueta y placeholder del campo de fecha
        const fechaLabel = document.querySelector('label[for="fecha"]');

        if (isEmpresa) {
            docType.value = 'nit';
            docType.disabled = false;
            docNumber.placeholder = "NIT de la Empresa";
            NameComplet.placeholder = "Razón Social";
            if (fechaLabel) fechaLabel.textContent = "Fecha de Constitución";
            fechaInput.setAttribute('aria-label', 'Fecha de Constitución');
            if (sectorEconomicoGroup) sectorEconomicoGroup.style.display = 'block';
            if (nombreComercialGroup) nombreComercialGroup.style.display = 'block';
            if (numEmpleadosGroup) numEmpleadosGroup.style.display = 'block';
            LastnameComplet.parentElement.style.display = 'none';
            LastnameComplet.value = '';

        } else {
            docType.value = '';
            docType.disabled = false;
            docNumber.placeholder = "Número de documento";
            NameComplet.placeholder = "Nombres Completos";
            LastnameComplet.placeholder = "Apellidos Completos"; 
            LastnameComplet.parentElement.style.display = 'block';
            if (nombreComercialGroup) nombreComercialGroup.style.display = 'none';
            if (nombreComercial) nombreComercial.value = '';
            if (sectorEconomicoGroup) sectorEconomicoGroup.style.display = 'none';
            if (sectorEconomico) sectorEconomico.value = ''; 
            if (numEmpleadosGroup) numEmpleadosGroup.style.display = 'none'; 
            if (numEmpleados) numEmpleados.value = ''; 
        }
        Object.keys(inputStates).forEach(key => delete inputStates[key]);
        updateButtonState(); 

        if (!isEmpresa && fechaLabel) fechaLabel.textContent = "Fecha de Nacimiento";
        if (!isEmpresa) fechaInput.setAttribute('aria-label', 'Fecha de Nacimiento');
    }

    // --- Validación de Formularios ---
    function validateStep(step, showErrors = false) {
        let inputs = [];
        let allValid = true;
        
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/; 
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const phoneRegex = /^\d{7,15}$/; 

        const validateInput = (input, validator, errorMessage) => {
            if (!input) return true;
            
            const isValid = validator();
            const shouldShowError = showErrors || inputStates[input.id];

            if (!isValid) {
                allValid = false;
                if (shouldShowError) {
                    addErrorState(input, errorMessage);
                } else {
                    removeErrorState(input);
                }
            } else {
                removeErrorState(input);
            }
            return isValid;
        };

        if (step === 0) {
            inputs = getStep1Inputs();
            
            validateInput(docType, 
                () => docType.value !== '', 
                'Debes seleccionar un tipo de documento.'
            );
            
            inputs.forEach(input => {
                // Para empresa, el campo LastnameComplet (numEmpleados) es opcional.
                const isOptionalForEmpresa = currentClientType === 'empresa' && (input.id === 'LastnameComplet' || input.id === 'num_empleados');
                // Para natural, el campo nombre_comercial no existe/valida.
                const isNotForNatural = currentClientType === 'natural' && (input.id === 'nombre_comercial' || input.id === 'num_empleados');
                // Para empresa, el campo apellidos no se valida
                const isNotForEmpresa = currentClientType === 'empresa' && input.id === 'LastnameComplet';


                if (input && input.id !== docType.id && !isOptionalForEmpresa && !isNotForNatural && !isNotForEmpresa) {
                    validateInput(input, 
                        () => input.value.trim() !== '', 
                        'Este campo es obligatorio.'
                    );
                }

                // Si es empresa y el campo de empleados tiene un valor, validar que sea número.
                if (currentClientType === 'empresa' && input.id === 'num_empleados' && input.value.trim() !== '') {
                    validateInput(input,
                        () => /^\d+$/.test(input.value),
                        'Debe ser un número entero.'
                    );
                }
            });

            if (currentClientType === 'empresa') {
                validateInput(nombreComercial,
                    () => nombreComercial.value.trim() !== '',
                    'El nombre comercial es obligatorio.'
                );
            }
            
        } else if (step === 1) {
            inputs = [newCorreo, newPassword, confirmPassword];
            
            validateInput(newCorreo, 
                () => emailRegex.test(newCorreo.value), 
                'Formato de email inválido.'
            );
            
            validateInput(newPassword, 
                () => passwordRegex.test(newPassword.value), 
                'Mínimo 8 caracteres, incluye mayúscula, minúscula y número.'
            );
            
            if(validateInput(confirmPassword, 
                () => confirmPassword.value.trim() !== '' && newPassword.value === confirmPassword.value, 
                'Las contraseñas no coinciden.'
            )){
                validateInput(newPassword, () => passwordRegex.test(newPassword.value), 'Mínimo 8 caracteres, incluye mayúscula, minúscula y número.');
            }
            
        } else if (step === 2) {
            inputs = [phone, address, ciudad, fechaInput, sectorEconomico];
            
            validateInput(phone, 
                () => phoneRegex.test(phone.value), 
                'El teléfono debe tener entre 7 y 15 dígitos.'
            );
            
            validateInput(address, 
                () => address.value.trim() !== '', 
                'La dirección es obligatoria.'
            );
            
            if (ciudad) {
                validateInput(ciudad, 
                    () => ciudad.value.trim() !== '', 
                    'La ciudad es obligatoria.'
                );
            }

            if (fechaInput) {
                validateInput(fechaInput,
                    () => fechaInput.value !== '',
                    'La fecha es obligatoria.'
                );
            }

            if (currentClientType === 'empresa' && sectorEconomico) {
                validateInput(sectorEconomico,
                    () => sectorEconomico.value.trim() !== '',
                    'El sector económico es obligatorio.'
                );
            }
        }

        return allValid;
    }

    function updateButtonState() {
        let button = null;
        if (currentStep === 0) button = step1Btn;
        else if (currentStep === 1) button = step2ContinueBtn;
        else if (currentStep === 2) button = step3FinalizeBtn;
        
        if (button) {
            const isValid = validateStep(currentStep); 
            button.disabled = !isValid;
            button.classList.toggle('active-btn', isValid);
        }
    }
    
    function handleBlur(e) {
        const input = e.target;
        inputStates[input.id] = true;
        validateStep(currentStep, true); 
        updateButtonState();
    }
    
    // --- Envío a la API ---
    async function submitRegistration(e) {
        e.preventDefault();
        
        if (!validateStep(2, true)) { 
            mostrarAlerta(
                'Por favor, completa correctamente todos los campos obligatorios.', 
                'error'
            );
            return;
        }

        const data = {
            email: newCorreo.value,
            password: newPassword.value,
            tipoCliente: currentClientType === 'natural' ? 'PERSONA_NATURAL' : 'EMPRESA',
            telefono: phone.value,
            direccion: address.value,
            ciudad: ciudad ? ciudad.value : null
        };
        
        if (currentClientType === 'natural') {
            data.tipoDocumento = docType.value;
            data.numDocumento = docNumber.value;
            data.nombres = NameComplet.value;
            data.apellidos = LastnameComplet.value;
            data.fechaNacimiento = fechaInput.value;
        } else {
            data.nit = docNumber.value;
            data.razonSocial = NameComplet.value;
            data.nombreComercial = nombreComercial ? nombreComercial.value : NameComplet.value;
            data.fechaConstitucion = fechaInput.value || null;
            data.numEmpleados = numEmpleados.value ? parseInt(numEmpleados.value, 10) : null;
            data.sectorEconomico = sectorEconomico ? sectorEconomico.value : null;
        }
        
        console.log('Datos a enviar:', JSON.stringify(data, null, 2));
        
        try {
            const response = await fetch(`${API_URL}/registro`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();

            if (response.ok && result.success) {
                mostrarAlerta('¡Has sido registrado correctamente!', 'success', {
                    onConfirm: () => {
                        window.location.href = 'login.html';
                    }
                });
            } else {
                const message = result.message || 'Error desconocido. Intenta de nuevo.';
                mostrarAlerta(`Error: ${message}`, 'error');
            }
        } catch (error) {
            console.error('Error de red o servidor:', error);
            mostrarAlerta('Error al conectar con el servidor. Intenta de nuevo más tarde.', 'error');
        }
    }

    // --- Event Listeners ---
    btnNaturalType.addEventListener('click', () => switchUserType('natural'));
    btnEmpresaType.addEventListener('click', () => switchUserType('empresa'));
    
    step1Btn.addEventListener('click', () => {
        if (validateStep(0, true)) goToNextStep(); 
    });

    step2ContinueBtn.addEventListener('click', (e) => {
        e.preventDefault();
        if (validateStep(1, true)) goToNextStep();
    });

    document.getElementById('btn-back-step2').addEventListener('click', () => goToStep(0));
    document.getElementById('btn-back-step3').addEventListener('click', () => goToStep(1));

    form.addEventListener('submit', submitRegistration);

    const allInputs = [
        ...getStep1Inputs(), 
        newCorreo, 
        newPassword, 
        confirmPassword, 
        phone, 
        address, 
        ciudad, 
        docType,
        nombreComercial,
        numEmpleados,
        fechaInput,
        sectorEconomico
    ].filter(input => input);
    
    allInputs.forEach(input => {
        if (input) {
            input.addEventListener('input', updateButtonState);
            input.addEventListener('blur', handleBlur);
        }
    });
    
    docType.addEventListener('change', handleBlur);
    fechaInput.addEventListener('change', handleBlur);
    if (sectorEconomico) sectorEconomico.addEventListener('change', handleBlur);

    // --- Inicialización ---
    switchUserType('natural'); 
    goToStep(0);
});
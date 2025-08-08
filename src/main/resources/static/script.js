// --- DOM Element References ---
const views = {
    selection: document.getElementById('view-mode-selection'),
    app: document.getElementById('view-app')
};
const appPanel = document.getElementById('app-panel');
const form = {
    title: document.getElementById('form-title'),
    messageGroup: document.getElementById('message-group'),
    keyLabel: document.getElementById('key-label'),
    submitBtn: document.getElementById('submitBtn'),
    clearBtn: document.getElementById('clearBtn'),
    form: document.getElementById('stegoForm'),
    imageInput: document.getElementById('imageInput'),
    messageInput: document.getElementById('messageInput'),
    keyInput: document.getElementById('keyInput'),
    fileUploadArea: document.getElementById('fileUploadArea'),
    keyToggle: document.getElementById('key-toggle'),
    keyStrengthIndicator: document.getElementById('key-strength-indicator'),
    strengthText: document.querySelector('.strength-text')
};
const info = {
    bar: document.getElementById('info-bar'),
    capacityText: document.getElementById('capacity-text'),
    charCounter: document.getElementById('char-counter')
};
const indicators = {
    loading: document.getElementById('loadingIndicator'),
    result: document.getElementById('resultBox'),
    resultText: document.getElementById('resultText'),
    copyBtn: document.getElementById('copyBtn')
};
const preview = {
    container: document.getElementById('imagePreviewContainer'),
    canvas: document.getElementById('imagePreviewCanvas'),
    info: document.getElementById('fileInfo')
};
const modals = {
    about: document.getElementById('about-modal'),
    manual: document.getElementById('manual-modal')
};

// --- SVG Icons ---
const eyeIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>`;
const eyeOffIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>`;


// --- State Management ---
let currentMode = 'encode';
let imageFile = null;
let maxCapacity = 0;

// --- View Switching Logic ---
const showView = (viewName) => {
    Object.values(views).forEach(v => v.classList.remove('active'));
    if (views[viewName]) views[viewName].classList.add('active');
};

const resetForm = () => {
    imageFile = null;
    maxCapacity = 0;
    form.form.reset();
    preview.container.style.display = 'none';
    appPanel.classList.remove('with-preview');
    indicators.result.style.display = 'none';
    info.bar.style.display = 'none';
    form.keyStrengthIndicator.style.display = 'none'; // Hide by default
    form.strengthText.textContent = '';
    form.keyInput.type = 'password';
    form.keyToggle.innerHTML = eyeIcon;
};

const setupMode = (mode) => {
    currentMode = mode;
    resetForm();

    if (mode === 'encode') {
        form.title.textContent = 'Encode Message';
        form.messageGroup.style.display = 'block';
        form.keyLabel.textContent = '3. Encryption Key';
        form.submitBtn.textContent = 'Encode & Download';
        form.keyStrengthIndicator.style.display = 'flex'; // Show for encode
    } else {
        form.title.textContent = 'Decode Message';
        form.messageGroup.style.display = 'none';
        form.keyLabel.textContent = '2. Decryption Key';
        form.submitBtn.textContent = 'Decode Message';
        form.keyStrengthIndicator.style.display = 'none'; // Hide for decode
    }
    showView('app');
};

// --- Event Listeners ---
document.getElementById('select-encode-mode').addEventListener('click', () => setupMode('encode'));
document.getElementById('select-decode-mode').addEventListener('click', () => setupMode('decode'));
document.querySelector('.back-button').addEventListener('click', () => showView('selection'));
form.clearBtn.addEventListener('click', resetForm);

// --- Modal Logic ---
const toggleModal = (modalId, show) => {
    const modal = modals[modalId];
    if (modal) {
        modal.classList.toggle('visible', show);
    }
};

document.getElementById('about-link').addEventListener('click', (e) => {
    e.preventDefault();
    toggleModal('about', true);
});
document.getElementById('manual-link').addEventListener('click', (e) => {
    e.preventDefault();
    toggleModal('manual', true);
});
document.querySelectorAll('.modal-close-btn, .modal-overlay').forEach(el => {
    el.addEventListener('click', (e) => {
        if (e.target === el) {
            toggleModal('about', false);
            toggleModal('manual', false);
        }
    });
});

// --- File Handling & Preview ---
const handleFileSelect = (file) => {
    if (!file) return;
    if (!file.type.includes('png')) {
        showResult('error', 'Invalid File: Only PNG images are supported.');
        return;
    }
    imageFile = file;

    const reader = new FileReader();
    reader.onload = (e) => {
        const img = new Image();
        img.onload = () => {
            const canvas = preview.canvas;
            const ctx = canvas.getContext('2d');
            const maxHeight = 300;
            const scale = Math.min(1, maxHeight / img.height);
            canvas.width = img.width * scale;
            canvas.height = img.height * scale;
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            preview.info.textContent = `${file.name} (${formatFileSize(file.size)})`;
            preview.container.style.display = 'flex';
            appPanel.classList.add('with-preview');
        };
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);

    if (currentMode === 'encode') {
        checkImageCapacity(file);
    }
};

form.fileUploadArea.addEventListener('click', () => form.imageInput.click());
form.imageInput.addEventListener('change', () => handleFileSelect(form.imageInput.files[0]));

['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
    form.fileUploadArea.addEventListener(eventName, e => {
        e.preventDefault();
        e.stopPropagation();
    });
});
form.fileUploadArea.addEventListener('drop', (e) => {
    handleFileSelect(e.dataTransfer.files[0]);
});

// --- Character Counter & Key Strength ---
form.messageInput.addEventListener('input', () => {
    const currentLength = form.messageInput.value.length;
    if (maxCapacity > 0) {
        info.charCounter.textContent = `${currentLength} / ${maxCapacity}`;
        info.charCounter.style.color = currentLength > maxCapacity ? 'var(--strength-weak)' : 'var(--accent-purple)';
    }
});

form.keyInput.addEventListener('input', () => {
    const key = form.keyInput.value;
    let score = 0;
    if (key.length >= 8) score++;
    if (key.length >= 12) score++;
    if (/[A-Z]/.test(key) && /[a-z]/.test(key)) score++;
    if (/\d/.test(key)) score++;
    if (/[^A-Za-z0-9]/.test(key)) score++;

    let strength = '';
    let strengthClass = '';
    if (key.length === 0) {
        strength = '';
        strengthClass = '';
    } else if (score < 3) {
        strength = 'Weak';
        strengthClass = 'weak';
    } else if (score < 5) {
        strength = 'Medium';
        strengthClass = 'medium';
    } else {
        strength = 'Strong';
        strengthClass = 'strong';
    }
    form.keyStrengthIndicator.className = `key-strength-indicator ${strengthClass}`;
    form.strengthText.textContent = strength;
});

form.keyToggle.addEventListener('click', () => {
    if (form.keyInput.type === 'password') {
        form.keyInput.type = 'text';
        form.keyToggle.innerHTML = eyeOffIcon;
    } else {
        form.keyInput.type = 'password';
        form.keyToggle.innerHTML = eyeIcon;
    }
});

// --- Form Submission ---
form.form.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!imageFile) {
        showResult('error', 'Please upload a PNG image.');
        return;
    }
    if (!form.keyInput.value.trim()) {
        showResult('error', 'An encryption/decryption key is required.');
        return;
    }
    if (currentMode === 'encode' && !form.messageInput.value.trim()) {
        showResult('error', 'A message to hide is required.');
        return;
    }

    toggleLoading(true);

    const formData = new FormData();
    formData.append('image', imageFile);
    formData.append('key', form.keyInput.value);

    if (currentMode === 'encode') {
        formData.append('message', form.messageInput.value);
        await handleEncode(formData);
    } else {
        await handleDecode(formData);
    }

    toggleLoading(false);
});

// --- API Call Handlers ---
async function handleApiError(response) {
    try {
        const errorData = await response.json();
        // Use the specific message from backend if available
        return `Error: ${errorData.message || response.statusText}`;
    } catch (e) {
        // Fallback for non-JSON errors
        return `An unexpected error occurred (HTTP ${response.status}).`;
    }
}

async function handleEncode(formData) {
    try {
        const response = await fetch('/api/hide', { method: 'POST', body: formData });
        if (response.ok) {
            const blob = await response.blob();
            downloadFile(blob, 'encoded_image.png');
            showResult('success', 'Encoding successful. Your new image has been downloaded.');
        } else {
            const errorMessage = await handleApiError(response);
            showResult('error', errorMessage);
        }
    } catch (error) {
        showResult('error', 'A network or server error occurred. Please try again.');
    }
}

async function handleDecode(formData) {
    try {
        const response = await fetch('/api/reveal', { method: 'POST', body: formData });
        if (response.ok) {
            const data = await response.json();
            if (data.status === 'success') {
                let revealedMessage = data.message.replace(/\0+$/, ''); // Clean trailing null chars
                showResult('success', `Message Revealed:\n\n${revealedMessage}`);
            } else {
                 // This case might happen if response is 200 OK but logical error
                showResult('error', `Decoding Failed: ${data.message || 'Invalid key or no message found.'}`);
            }
        } else {
            const errorMessage = await handleApiError(response);
            showResult('error', errorMessage);
        }
    } catch (error) {
        showResult('error', 'A network or server error occurred. Please try again.');
    }
}

async function checkImageCapacity(imageFile) {
    const formData = new FormData();
    formData.append('image', imageFile);
    info.bar.style.display = 'flex';
    info.capacityText.textContent = 'Calculating capacity...';
    info.charCounter.textContent = '';

    try {
        const response = await fetch('/api/capacity', { method: 'POST', body: formData });
        const data = await response.json();
        if (response.ok && data.status === 'success') {
            maxCapacity = data.capacityCharacters;
            info.capacityText.textContent = `Image capacity: ~${maxCapacity} characters.`;
            info.charCounter.textContent = `0 / ${maxCapacity}`;
        } else {
            info.capacityText.textContent = 'Could not determine capacity.';
        }
    } catch (error) {
        console.error('Could not fetch image capacity.', error);
        info.capacityText.textContent = 'Could not determine capacity.';
    }
}

// --- UI Utility Functions ---
const toggleLoading = (isLoading) => {
    indicators.loading.style.display = isLoading ? 'block' : 'none';
    form.submitBtn.disabled = isLoading;
    form.clearBtn.disabled = isLoading;
    if (isLoading) indicators.result.style.display = 'none';
};

const showResult = (type, message) => {
    indicators.result.className = `result ${type}`;
    indicators.resultText.textContent = message;
    indicators.result.style.display = 'block';

    // Show copy button only for successful decodes
    if (type === 'success' && currentMode === 'decode') {
        indicators.copyBtn.style.display = 'block';
    } else {
        indicators.copyBtn.style.display = 'none';
    }
};

indicators.copyBtn.addEventListener('click', () => {
    // Create a temporary textarea to hold the text and copy it
    const textToCopy = indicators.resultText.textContent.replace("Message Revealed:\n\n", "");
    const tempTextArea = document.createElement('textarea');
    tempTextArea.value = textToCopy;
    document.body.appendChild(tempTextArea);
    tempTextArea.select();
    document.execCommand('copy');
    document.body.removeChild(tempTextArea);

    // Provide feedback to the user
    const originalText = indicators.copyBtn.innerHTML;
    indicators.copyBtn.innerHTML = 'Copied!';
    setTimeout(() => {
        indicators.copyBtn.innerHTML = originalText;
    }, 1500);
});

const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
};

const downloadFile = (blob, filename) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
};

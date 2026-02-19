/**
 * navbar.js — Avatar de usuario en el navbar.
 */

(function () {
    const API = "https://pokemon-backend-849x.onrender.com/api";

    async function initNavbar() {
        const user = await window.api.getUser();
        if (!user) return;

        const navUserInfo = document.getElementById("navUserInfo");
        if (!navUserInfo) return;

        // ── Construir HTML — sin src en el img para evitar petición vacía ──
        navUserInfo.innerHTML = `
        <span>Hola, <strong>${user.username}</strong></span>
<div id="avatarWrapper" data-bs-toggle="popover" data-bs-trigger="hover" title="Cambiar foto" data-bs-content="Selecciona una nueva foto para tu avatar.">
    <canvas id="userAvatarCanvas" width="38" height="38" style="border-radius:50%;border:2.5px solid rgba(255,255,255,0.8);display:block;"></canvas>
    <div class="avatar-edit-icon">✏️</div>
    <div id="avatarSpinner" class="d-none">
        <div class="spinner-border text-white"></div>
    </div>
</div>
<input type="file" id="inputFotoUsuario" accept="image/*">
    `;

        // Mostrar inicial mientras carga
        dibujarInicialAvatar(user.username);

        // Intentar cargar foto real del servidor
        cargarAvatar(user.id);

        // Click en avatar → abrir selector
        const avatarWrapper = document.getElementById("avatarWrapper");
        avatarWrapper.addEventListener("click", () => {
            document.getElementById("inputFotoUsuario").click();
        });

        // ── Inicializar popover aquí, después de crear avatarWrapper ──
        new bootstrap.Popover(avatarWrapper, {
            trigger: 'hover',
            placement: 'top',  // top, bottom, left, right
            html: true,
            container: 'body', // importante para que no se corte
        });

        // Selección de archivo → subir
        document.getElementById("inputFotoUsuario").addEventListener("change", async (e) => {
            const file = e.target.files[0];
            if (!file) return;
            if (!file.type.startsWith("image/")) {
                mostrarToastNavbar("Solo se permiten imágenes.", "danger"); return;
            }
            if (file.size > 5 * 1024 * 1024) {
                mostrarToastNavbar("La imagen no puede superar 5 MB.", "danger"); return;
            }
            await subirFoto(file, user.id);
            e.target.value = "";
        });


    }

    // ── Dibuja la inicial del usuario en el canvas ────────────
    function dibujarInicialAvatar(username) {
        const canvas = document.getElementById("userAvatarCanvas");
        if (!canvas) return;
        const ctx = canvas.getContext("2d");
        const w = canvas.width, h = canvas.height;

        const grad = ctx.createLinearGradient(0, 0, w, h);
        grad.addColorStop(0, "#cc2200");
        grad.addColorStop(1, "#ff8c42");
        ctx.fillStyle = grad;
        ctx.beginPath();
        ctx.arc(w / 2, h / 2, w / 2, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = "#fff";
        ctx.font = `bold ${Math.round(w * 0.45)}px Arial`;
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText(username.charAt(0).toUpperCase(), w / 2, h / 2 + 1);
    }

    // ── Dibuja una imagen (blob) en el canvas usando data: URL ──
    function dibujarImagenEnCanvas(blob) {
        return new Promise((resolve) => {
            const canvas = document.getElementById("userAvatarCanvas");
            if (!canvas) return resolve();
            const ctx = canvas.getContext("2d");

            // Usar FileReader para convertir blob a data: URL (compatible con CSP)
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = new Image();
                img.onload = () => {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    ctx.save();
                    ctx.beginPath();
                    ctx.arc(canvas.width / 2, canvas.height / 2, canvas.width / 2, 0, Math.PI * 2);
                    ctx.clip();
                    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
                    ctx.restore();
                    resolve();
                };
                img.onerror = () => resolve();
                img.src = e.target.result; // data: URL — no bloqueado por CSP
            };
            reader.onerror = () => resolve();
            reader.readAsDataURL(blob);
        });
    }

    // ── Cargar foto del servidor (con token) ──────────────────
    async function cargarAvatar(userId) {
        try {
            const token = await window.api.getToken();
            const res = await fetch(`${API}/users/${userId}/profile-picture`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            console.log(`[Avatar] GET profile-picture → ${res.status}`);

            if (res.ok) {
                const blob = await res.blob();
                await dibujarImagenEnCanvas(blob);
            }
            // Si 404 o 500 → se queda la inicial, no hay error visible
        } catch (err) {
            // Sin conexión o error de red → se queda la inicial
            console.warn("[Avatar] No se pudo cargar la foto:", err.message);
        }
    }

    // ── Subir foto al servidor ────────────────────────────────
    async function subirFoto(file, userId) {
        const spinner = document.getElementById("avatarSpinner");
        if (spinner) spinner.classList.add("visible");

        try {
            const token = await window.api.getToken();
            const formData = new FormData();
            formData.append("file", file);

            const res = await fetch(`${API}/users/me/profile-picture`, {
                method: "POST",
                headers: { "Authorization": `Bearer ${token}` },
                // ⚠️ Sin Content-Type: fetch lo pone solo con el boundary correcto
                body: formData
            });

            console.log(`[Avatar] POST profile-picture → ${res.status}`);

            if (res.ok) {
                // Recargar desde servidor para confirmar que se guardó
                await cargarAvatar(userId);
                mostrarToastNavbar("¡Foto actualizada!", "success");
            } else {
                const txt = await res.text().catch(() => "");
                console.error("[Avatar] Error subiendo:", res.status, txt);

                if (res.status === 500) {
                    // Render no persiste archivos — mostramos aviso específico
                    mostrarToastNavbar("El servidor no puede guardar archivos permanentemente (Render free tier). La foto se ve localmente.", "warning");
                } else {
                    mostrarToastNavbar(`Error al subir la foto (${res.status}).`, "danger");
                }
            }
        } catch (err) {
            console.error("[Avatar] Error de red:", err);
            mostrarToastNavbar("No se pudo conectar con el servidor.", "danger");
        } finally {
            if (spinner) spinner.classList.remove("visible");
        }
    }

    // ── Toast ─────────────────────────────────────────────────
    function mostrarToastNavbar(msg, tipo = "success") {
        let container = document.getElementById("toastNavbarContainer");
        if (!container) {
            container = document.createElement("div");
            container.id = "toastNavbarContainer";
            container.className = "toast-container position-fixed bottom-0 end-0 p-3";
            container.style.zIndex = "9999";
            document.body.appendChild(container);
        }
        const id = "toast_" + Date.now();
        container.insertAdjacentHTML("beforeend", `
        <div id="${id}" class="toast align-items-center text-bg-${tipo} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">${msg}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>`);
        const el = document.getElementById(id);
        bootstrap.Toast.getOrCreateInstance(el, { delay: 4000 }).show();
        el.addEventListener("hidden.bs.toast", () => el.remove());
    }

    document.addEventListener("DOMContentLoaded", initNavbar);
})();

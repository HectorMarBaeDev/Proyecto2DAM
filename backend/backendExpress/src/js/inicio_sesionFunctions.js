const API = "https://pokemon-backend-849x.onrender.com/api";

document.addEventListener("DOMContentLoaded", async () => {

    // Si ya hay sesión iniciada, ir directo al index
    const token = await window.api.getToken();
    if (token) { window.location.href = "index.html"; return; }

    const btnLogin      = document.getElementById("iniciarSesion");
    const btnRegister   = document.getElementById("registrarse");
    const botonesInicio = document.getElementById("botonesInicio");
    const contenedor    = document.getElementById("contenedorForm");
    const header        = document.getElementById("bloqueHeader");

    btnLogin.addEventListener("click", () => cambiarVista("login"));
    btnRegister.addEventListener("click", () => cambiarVista("registro"));

    function cambiarVista(tipo) {
        botonesInicio.classList.remove("animar-entrada");
        botonesInicio.classList.add("animar-salida");
        setTimeout(() => {
            botonesInicio.classList.add("oculto");
            botonesInicio.classList.remove("animar-salida");
            if (tipo === "login") cargarLogin();
            else cargarRegistro();
            contenedor.classList.remove("oculto");
            contenedor.classList.add("animar-entrada");
            header.classList.add("header-pequeño");
        }, 300);
    }

    function volverInicio() {
        contenedor.classList.remove("animar-entrada");
        contenedor.classList.add("animar-salida");
        setTimeout(() => {
            contenedor.classList.add("oculto");
            contenedor.classList.remove("animar-salida");
            contenedor.innerHTML = "";
            botonesInicio.classList.remove("oculto");
            botonesInicio.classList.add("animar-entrada");
            header.classList.remove("header-pequeño");
        }, 300);
    }

    function mostrarError(id, msg) {
        const el = document.getElementById(id);
        if (el) { el.textContent = msg; el.classList.remove("d-none"); }
    }
    function ocultarError(id) {
        const el = document.getElementById(id);
        if (el) el.classList.add("d-none");
    }

    // ── LOGIN ─────────────────────────────────────────────
    function cargarLogin() {
        contenedor.innerHTML = `
        <div class="card p-4 shadow mx-auto" style="width:350px;">
            <h4 class="mb-3">Iniciar Sesión</h4>
            <div class="mb-3">
                <label class="form-label">Nombre de usuario</label>
                <input type="text" class="form-control" id="username" autocomplete="username">
            </div>
            <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" class="form-control" id="password" autocomplete="current-password">
            </div>
            <div id="errorLogin" class="alert alert-danger d-none py-2 mb-3"></div>
            <div class="mb-3 d-flex justify-content-center gap-2">
                <button class="btn btn-success flex-fill" id="btnEntrar">Entrar</button>
                <button class="btn btn-outline-secondary flex-fill" id="cancelar">Cancelar</button>
            </div>
        </div>`;

        document.getElementById("cancelar").addEventListener("click", volverInicio);
        document.getElementById("btnEntrar").addEventListener("click", hacerLogin);
        document.getElementById("password").addEventListener("keypress", e => { if (e.key === "Enter") hacerLogin(); });
    }

    async function hacerLogin() {
        ocultarError("errorLogin");
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;
        if (!username || !password) { mostrarError("errorLogin", "Completa todos los campos."); return; }

        const btn = document.getElementById("btnEntrar");
        btn.disabled = true; btn.textContent = "Entrando...";

        try {
            const res = await window.api.fetchWithAuth(`${API}/auth/login`, {
                method: "POST",
                body: JSON.stringify({ username, password })
            });

            if (!res.ok) { mostrarError("errorLogin", "Usuario o contraseña incorrectos."); return; }

            await window.api.setToken(res.data.token);
            await window.api.setUser({ id: res.data.userId, username: res.data.username });
            window.location.href = "index.html";

        } catch (err) {
            mostrarError("errorLogin", "No se pudo conectar con el servidor.");
            console.error(err);
        } finally {
            btn.disabled = false; btn.textContent = "Entrar";
        }
    }

    // ── REGISTRO ──────────────────────────────────────────
    function cargarRegistro() {
        contenedor.innerHTML = `
        <div class="card p-4 shadow mx-auto" style="width:380px;">
            <h4 class="mb-3">Registro</h4>
            <div class="mb-3">
                <label class="form-label">Nombre de usuario</label>
                <input type="text" class="form-control" id="regUsername" autocomplete="username">
            </div>
            <div class="mb-3">
                <label class="form-label">Correo electrónico</label>
                <input type="email" class="form-control" id="regEmail" autocomplete="email">
            </div>
            <div class="mb-3">
                <label class="form-label">Contraseña</label>
                <input type="password" class="form-control" id="regPassword" autocomplete="new-password">
            </div>
            <div class="mb-3">
                <label class="form-label">Repetir contraseña</label>
                <input type="password" class="form-control" id="regPassword2" autocomplete="new-password">
            </div>
            <div id="errorRegistro" class="alert alert-danger d-none py-2 mb-3"></div>
            <div id="okRegistro" class="alert alert-success d-none py-2 mb-3"></div>
            <div class="mb-3 d-flex justify-content-center gap-2">
                <button class="btn btn-success flex-fill" id="btnRegistrarse">Registrarse</button>
                <button class="btn btn-outline-secondary flex-fill" id="cancelar">Cancelar</button>
            </div>
        </div>`;

        document.getElementById("cancelar").addEventListener("click", volverInicio);
        document.getElementById("btnRegistrarse").addEventListener("click", hacerRegistro);
    }

    async function hacerRegistro() {
        ocultarError("errorRegistro");
        ocultarError("okRegistro");
        const username  = document.getElementById("regUsername").value.trim();
        const email     = document.getElementById("regEmail").value.trim();
        const password  = document.getElementById("regPassword").value;
        const password2 = document.getElementById("regPassword2").value;

        if (!username || !email || !password || !password2) { mostrarError("errorRegistro", "Completa todos los campos."); return; }
        if (password !== password2) { mostrarError("errorRegistro", "Las contraseñas no coinciden."); return; }
        if (password.length < 4) { mostrarError("errorRegistro", "La contraseña debe tener al menos 4 caracteres."); return; }

        const btn = document.getElementById("btnRegistrarse");
        btn.disabled = true; btn.textContent = "Registrando...";

        try {
            const res = await window.api.fetchWithAuth(`${API}/auth/register`, {
                method: "POST",
                body: JSON.stringify({ username, email, password })
            });

            if (!res.ok) { mostrarError("errorRegistro", res.data || "Error al registrarse."); return; }

            const ok = document.getElementById("okRegistro");
            ok.textContent = "¡Registro exitoso! Ahora puedes iniciar sesión.";
            ok.classList.remove("d-none");

            setTimeout(() => { volverInicio(); setTimeout(() => cambiarVista("login"), 400); }, 1800);

        } catch (err) {
            mostrarError("errorRegistro", "No se pudo conectar con el servidor.");
            console.error(err);
        } finally {
            btn.disabled = false; btn.textContent = "Registrarse";
        }
    }
});

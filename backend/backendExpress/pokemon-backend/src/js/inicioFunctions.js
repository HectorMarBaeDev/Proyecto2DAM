document.addEventListener("DOMContentLoaded", () => {

    const btnLogin = document.getElementById("iniciarSesion");
    const btnRegister = document.getElementById("registrarse");
    const botonesInicio = document.getElementById("botonesInicio");
    const contenedor = document.getElementById("contenedorForm");
    const bloquePrincipal = document.getElementById("bloquePrincipal");

    btnLogin.addEventListener("click", () => cambiarVista("login"));
    btnRegister.addEventListener("click", () => cambiarVista("registro"));

    function cambiarVista(tipo) {
        // Animar botones y bloque principal hacia arriba
        bloquePrincipal.classList.remove("animar-bajar");
        bloquePrincipal.classList.add("animar-subir");
        botonesInicio.classList.remove("animar-entrada");
        botonesInicio.classList.add("animar-salida");

        setTimeout(() => {
            botonesInicio.classList.add("oculto");
            botonesInicio.classList.remove("animar-salida");

            if (tipo === "login") cargarLogin();
            else cargarRegistro();

            contenedor.classList.remove("oculto");
            contenedor.classList.remove("animar-salida");
            contenedor.classList.add("animar-entrada");

        }, 400);
    }

    function volverInicio() {
        // Animar salida del formulario
        contenedor.classList.remove("animar-entrada");
        contenedor.classList.add("animar-salida");

        // Animar bloque principal hacia abajo
        bloquePrincipal.classList.remove("animar-subir");
        bloquePrincipal.classList.add("animar-bajar");

        setTimeout(() => {
            contenedor.classList.add("oculto");
            contenedor.classList.remove("animar-salida");
            contenedor.innerHTML = "";

            botonesInicio.classList.remove("oculto");
            botonesInicio.classList.remove("animar-salida");
            botonesInicio.classList.add("animar-entrada");

            // Limpiar animaciones del bloque principal
            bloquePrincipal.classList.remove("animar-bajar");
        }, 400);
    }

    function cargarLogin() {
        contenedor.innerHTML = `
            <div class="card p-4 shadow mx-auto" style="width:350px;">
                <h4 class="mb-3">Iniciar Sesión</h4>

                <div class="mb-3">
                    <label class="form-label">Correo electrónico</label>
                    <input type="email" class="form-control">
                </div>

                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" class="form-control">
                </div>

                <div class="mb-3 d-flex justify-content-center gap-2">
                    <button class="btn btn-success flex-fill">Entrar</button>
                    <button class="btn btn-outline-secondary flex-fill" id="cancelar">Cancelar</button>
                </div>
            </div>
        `;
        document.getElementById("cancelar").addEventListener("click", volverInicio);
    }

    function cargarRegistro() {
        contenedor.innerHTML = `
            <div class="card p-4 shadow mx-auto" style="width:350px;">
                <h4 class="mb-3">Registro</h4>

                <div class="mb-3">
                    <label class="form-label">Nombre de usuario</label>
                    <input type="text" class="form-control">
                </div>

                <div class="mb-3">
                    <label class="form-label">Correo electrónico</label>
                    <input type="email" class="form-control">
                </div>

                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" class="form-control">
                </div>

                <div class="mb-3">
                    <label class="form-label">Repetir contraseña</label>
                    <input type="password" class="form-control">
                </div>

                <div class="mb-3 d-flex justify-content-center gap-2">
                    <button class="btn btn-success flex-fill">Registrarse</button>
                    <button class="btn btn-outline-secondary flex-fill" id="cancelar">Cancelar</button>
                </div>
            </div>
        `;
        document.getElementById("cancelar").addEventListener("click", volverInicio);
    }

});

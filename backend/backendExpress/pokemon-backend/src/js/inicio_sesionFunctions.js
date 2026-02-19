document.addEventListener("DOMContentLoaded", () => {

    const btnLogin = document.getElementById("iniciarSesion");
    const btnRegister = document.getElementById("registrarse");
    const botonesInicio = document.getElementById("botonesInicio");
    const contenedor = document.getElementById("contenedorForm");
    const header = document.getElementById("bloqueHeader");

    btnLogin.addEventListener("click", () => cambiarVista("login"));
    btnRegister.addEventListener("click", () => cambiarVista("registro"));

    function cambiarVista(tipo) {

        // Ocultar botones con animación
        botonesInicio.classList.remove("animar-entrada");
        botonesInicio.classList.add("animar-salida");

        setTimeout(() => {

            botonesInicio.classList.add("oculto");
            botonesInicio.classList.remove("animar-salida");

            if (tipo === "login") cargarLogin();
            else cargarRegistro();

            contenedor.classList.remove("oculto");
            contenedor.classList.add("animar-entrada");

            // 👇 Reducir header suavemente
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

            // 👇 Restaurar tamaño normal
            header.classList.remove("header-pequeño");

        }, 300);
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

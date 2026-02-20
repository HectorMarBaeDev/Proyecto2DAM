const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('api', {

    // ── Token ────────────────────────────────────────────
    setToken: (token) => ipcRenderer.invoke('store-set', 'jwt', token),
    getToken: ()      => ipcRenderer.invoke('store-get', 'jwt'),
    clearToken: ()    => ipcRenderer.invoke('store-del', 'jwt'),

    // ── Usuario ──────────────────────────────────────────
    setUser: (user)   => ipcRenderer.invoke('store-set', 'user', JSON.stringify(user)),
    getUser: async () => {
        const u = await ipcRenderer.invoke('store-get', 'user');
        return u ? JSON.parse(u) : null;
    },
    clearUser: ()     => ipcRenderer.invoke('store-del', 'user'),

    // ── Fetch con auth ───────────────────────────────────
    fetchWithAuth: async (url, options = {}) => {
        const token = await ipcRenderer.invoke('store-get', 'jwt');

        const headers = {
            "Content-Type": "application/json",
            ...(options.headers || {})
        };

        if (token) headers["Authorization"] = `Bearer ${token}`;

        const res  = await fetch(url, { ...options, headers });
        const text = await res.text();
        let data;
        try { data = JSON.parse(text); } catch { data = text; }

        return { ok: res.ok, status: res.status, data };
    }
});

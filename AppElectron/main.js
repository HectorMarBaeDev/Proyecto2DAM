const { app, BrowserWindow, ipcMain, session } = require('electron');
const path = require('path');
const fs   = require('fs');

// ── Almacenamiento simple en JSON ────────────────────────
const storePath = path.join(app.getPath('userData'), 'pokebuild-store.json');

function readStore() {
    try { return JSON.parse(fs.readFileSync(storePath, 'utf8')); }
    catch { return {}; }
}
function writeStore(data) {
    fs.writeFileSync(storePath, JSON.stringify(data), 'utf8');
}

ipcMain.handle('store-get', (_, key)        => readStore()[key] ?? null);
ipcMain.handle('store-set', (_, key, value) => { const s = readStore(); s[key] = value; writeStore(s); });
ipcMain.handle('store-del', (_, key)        => { const s = readStore(); delete s[key]; writeStore(s); });

// ── Ventana ──────────────────────────────────────────────
function createWindow() {
    // Eliminar Content-Security-Policy que bloquea HTTP
    session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
        callback({
            responseHeaders: {
                ...details.responseHeaders,
                'Content-Security-Policy': [
                    "default-src 'self' 'unsafe-inline' 'unsafe-eval' http://localhost:* https:; " +
                    "img-src 'self' data: blob: https: http:; " +
                    "connect-src 'self' http://localhost:* https:;"
                ]
            }
        });
    });

    let win = new BrowserWindow({
    width: 1300,
    height: 1000,

    minWidth: 1300,
    minHeight: 1000,

    resizable: true,
    maximizable: true,
    fullscreenable: true,

    webPreferences: {
        nodeIntegration: false,
        contextIsolation: true,
        preload: path.join(__dirname, 'src/js/preload.js')
    }
});

    win.loadFile(path.join(__dirname, 'src/inicio_sesion.html'));
    win.removeMenu();
    win.webContents.openDevTools();
}

app.whenReady().then(() => { session.defaultSession.clearCache().then(createWindow); });
app.commandLine.appendSwitch('ignore-certificate-errors', 'true');
app.commandLine.appendSwitch('allow-insecure-localhost', 'true');
app.commandLine.appendSwitch('disable-cache', 'true');
const { app, BrowserWindow } = require('electron');
const path = require('path');
//const server = require('./server/server');

function createWindow() {
    let win = new BrowserWindow({
        width: 1300,
        height: 1000,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false
        }
    });

    win.loadFile(path.join(__dirname, 'src/index.html'));
    win.removeMenu();
    win.webContents.openDevTools();
    win.resizable = false;
}

app.whenReady().then(() => {
    createWindow();
});

// Hay que instalar los módulos de client y server para el funcionamiento general
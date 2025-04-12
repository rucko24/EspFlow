// Archivo: frontend/js/espToolFlow.js
// Importa las clases necesarias desde el bundle de esptool-js
import { ESPLoader, Transport } from "./esptool-bundle-0.5.4.js";

let esploader = null;
let transport = null;
let device = null;
const serialLib = !navigator.serial && navigator.usb ? serial : navigator.serial;

let espTerminalServerRef = null;

window.setEspTerminalServerRef = function(server) {
  espTerminalServerRef = server;
  console.log("espTerminalServerRef asignado:", espTerminalServerRef);
};

/**
 * Conecta o desconecta el dispositivo en modo toggle.
 * Si ya hay conexión activa, se desconecta;
 * si no hay conexión, se conecta usando el parámetro resetMode.
 *
 * @param {number} baudRate - Velocidad de transmisión (por ejemplo, 115200).
 * @param {boolean} noReset - Indica si se quiere usar el modo "no_reset".
 * @returns {string} JSON con el resultado.
 */
window.espConnect = async (baudRate, noReset) => {
  try {
    // Modo toggle: desconectar si ya hay conexión

    // Disconnect if connected
    if (transport !== null) {
        await transport.disconnect();
        await transport.waitForUnlock(1500);
        //toggleUIConnected(false);
        transport = null;
        if (device !== null) {
            await device.close();
            device = null;
        }
        chip = null;
        return;
    }

//    if (esploader !== null) {
//      await transport.disconnect();
//      await transport.waitForUnlock(1500);
//      if (device) {
//        await device.close();
//        device = null;
//      }
//      esploader = null;
//      transport = null;
//      return JSON.stringify({
//        success: true,
//        message: "Desconectado exitosamente"
//      });
//    }

    if (device === null) {
        device = await serialLib.requestPort({});
    }

    if (transport === null) {
        transport = new Transport(device, true);
    }

    // await device.open({ baudRate: parseInt(baudRate) });

    // Opciones para ESPLoader
    const options = {
      transport: transport,
      baudrate: parseInt(baudRate),
      terminal: window.espLoaderTerminal,
      debugLogging: false,
      resetConstructors: {
          hardReset: (transport, baudrate) => {
            return {
              reset: async () => {
                console.log("Hard resetting via RTS pin...");
                // Usamos el objeto device en lugar de transport para setSignals
                await transport.setRTS(true);
                await new Promise(resolve => setTimeout(resolve, 50)); // Pausa de 50 ms
                await transport.setRTS(false);
              }
            };
          }
        }
      };

    window.resetConstructors = options.resetConstructors; // Asigna globalmente
    window.baudRate = parseInt(baudRate); // Asigna globalmente

    // Crear la instancia de ESPLoader y llamar a main pasando resetMode
    esploader = new ESPLoader(options);

    let resetMode = "hard_reset";
    if (noReset.checked) {
        resetMode = "no_reset";
        try {
            // Initiate passthrough serial setup
            await transport.connect(romBaudrate);
            await transport.disconnect();
            await sleep(350);
        } catch (e) {
        }
    }

    const chip = await esploader.main(resetMode);

    return JSON.stringify({
      success: true,
      message: "Conectado exitosamente al dispositivo " + chip,
      chip: chip
    });

  } catch (err) {
    console.error("Error en espConnect:", err);
    return JSON.stringify({
      success: false,
      error: err.message || "Error desconocido en espConnect"
    });
  }
};

window.testHardReset = async () => {
    if (window.resetConstructors && typeof window.resetConstructors.hardReset === "function") {
        // Suponiendo que transport y baudRate están definidos en el ámbito adecuado
        const resetObj = window.resetConstructors.hardReset(transport, window.baudRate);
        await resetObj.reset();
    } else {
        console.log("No hay función hardReset definida globalmente");
    }
};

/**
 * Obtiene el Flash ID del dispositivo.
 *
 * @returns {string} JSON con el resultado.
 */
window.espFlashId = async () => {
  try {
    if (!esploader) {
      return JSON.stringify({
        success: false,
        error: "No hay conexión activa. Conecte el dispositivo primero."
      });
    }
    const flashId = await esploader.flash_id();
    return JSON.stringify({
      success: true,
      data: {
        manufacturerId: flashId.manufacturerId,
        deviceId: flashId.deviceId,
        sizeInBytes: flashId.sizeBytes,
        sizeInMB: (flashId.sizeBytes / (1024 * 1024)).toFixed(2)
      }
    });
  } catch (err) {
    console.error("Error en espFlashId:", err);
    return JSON.stringify({
      success: false,
      error: err.message || "Error desconocido al obtener Flash ID"
    });
  }
};

/**
 * Lee datos de la flash del dispositivo.
 *
 * @param {number|string} address - Dirección desde donde leer.
 * @param {number|string} size - Cantidad de bytes a leer.
 * @returns {string} JSON con el resultado (datos en hexadecimal).
 */
window.espReadFlash = async (address, size) => {
  try {
    if (!esploader) {
      return JSON.stringify({
        success: false,
        error: "No hay conexión activa. Conecte el dispositivo primero."
      });
    }
    const data = await esploader.read_flash(parseInt(address), parseInt(size));
    const hexString = Array.from(data).map(b => b.toString(16).padStart(2, '0')).join('');
    return JSON.stringify({
      success: true,
      data: {
        hexData: hexString,
        readSize: size,
        readAddress: address
      }
    });
  } catch (err) {
    console.error("Error en espReadFlash:", err);
    return JSON.stringify({
      success: false,
      error: err.message || "Error desconocido al leer flash"
    });
  }
};

/**
 * Escribe datos en la flash del dispositivo.
 *
 * @param {number|string} address - Dirección donde escribir.
 * @param {string} hexString - Datos en formato hexadecimal.
 * @param {boolean|string} eraseAll - Indica si se debe borrar toda la flash.
 * @returns {string} JSON con el resultado.
 */
window.espWriteFlash = async (address, hexString, eraseAll) => {
  try {
    if (!esploader) {
      return JSON.stringify({
        success: false,
        error: "No hay conexión activa. Conecte el dispositivo primero."
      });
    }
    const bytes = new Uint8Array(hexString.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));
    const options = {
      erase_all: eraseAll === true || eraseAll === "true"
    };
    await esploader.write_flash([parseInt(address)], [bytes], options);
    return JSON.stringify({
      success: true,
      message: "Flash escrita exitosamente"
    });
  } catch (err) {
    console.error("Error en espWriteFlash:", err);
    return JSON.stringify({
      success: false,
      error: err.message || "Error desconocido al escribir flash"
    });
  }
};

// Definimos el objeto terminal de forma global. Este objeto se utilizará para
// escribir logs en el servidor a través de los métodos expuestos en $server.
window.espLoaderTerminal = {
  clean() {
    if (espTerminalServerRef && espTerminalServerRef.cleanLog) {
      espTerminalServerRef.cleanLog();
    } else {
      console.log("Terminal clean: función cleanLog no disponible");
    }
  },
  writeLine(data) {
    if (espTerminalServerRef && espTerminalServerRef.writeLogLine) {
      espTerminalServerRef.writeLogLine(data);
    } else {
      console.log("Terminal writeLine:", data);
    }
  },
  write(data) {
    if (espTerminalServerRef && espTerminalServerRef.writeLog) {
      espTerminalServerRef.writeLog(data);
    } else {
      console.log("Terminal write:", data);
    }
  }
};


/**
 * Cambia el baudrate de la conexión.
 *
 * @param {number|string} newBaudRate - Nuevo baudrate.
 * @returns {string} JSON con el resultado.
 */
window.espChangeBaudrate = async (newBaudRate) => {
  try {
    if (!esploader) {
      return JSON.stringify({
        success: false,
        error: "No hay conexión activa. Conecte el dispositivo primero."
      });
    }
    await esploader.change_baudrate(parseInt(newBaudRate));
    return JSON.stringify({
      success: true,
      message: "Baudrate cambiado a " + newBaudRate
    });
  } catch (err) {
    console.error("Error en espChangeBaudrate:", err);
    return JSON.stringify({
      success: false,
      error: err.message || "Error desconocido al cambiar baudrate"
    });
  }
};

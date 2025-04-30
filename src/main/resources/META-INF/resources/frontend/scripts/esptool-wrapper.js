// Archivo: frontend/js/espToolFlow.js
// Importa las clases necesarias desde el bundle de esptool-js
// Asegúrate de que este archivo (esptool-bundle-0.5.4.js) esté accesible en tu estructura de frontend.
import { ESPLoader, Transport } from "./esptool-bundle-0.5.4.js";

// --- Helper Functions ---

/**
 * Función de pausa asíncrona.
 * @param {number} ms - Milisegundos para pausar.
 * @returns {Promise<void>}
 */
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// --- Global State for Vaadin Interaction ---

// Determina qué librería serial usar (Web Serial API o WebUSB con polyfill 'serial')
// NOTA: Asegúrate de que si navigator.serial no existe, haya un polyfill 'serial' disponible globalmente.
const serialLib = !navigator.serial && navigator.usb ? serial : navigator.serial;

// Referencia global para la comunicación con el servidor Vaadin.
// Será establecida por el servidor.
let espTerminalServerRef = null;

// Define el objeto terminal de forma global. Este objeto se utilizará para
// escribir logs en el servidor a través de los métodos expuestos en $server.
// esptool-js espera un objeto con estos métodos para la opción 'terminal'.
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
      // Vaadin Server RPC handle
      espTerminalServerRef.writeLogLine(String(data)); // Ensure it's a string
    } else {
      console.log("Terminal writeLine:", data);
    }
  },
  write(data) {
    if (espTerminalServerRef && espTerminalServerRef.writeLog) {
       // Vaadin Server RPC handle
      espTerminalServerRef.writeLog(String(data)); // Ensure it's a string
    } else {
      console.log("Terminal write:", data);
    }
  }
};

// Función global para que Vaadin establezca la referencia al objeto servidor.
window.setEspTerminalServerRef = function(server) {
  espTerminalServerRef = server;
  console.log("espTerminalServerRef asignado:", espTerminalServerRef);
};

// --- EspTool Connection Manager Class ---

/**
 * Clase para gestionar la conexión y las operaciones con esptool-js.
 */
class EspConnectionManager {
  constructor() {
    /** @type {ESPLoader | null} */
    this.esploader = null;
    /** @type {Transport | null} */
    this.transport = null;
    /** @type {SerialPort | USBDevice | null} */
    this.device = null;
    // No necesitamos almacenar baudRate o resetConstructors aquí,
    // se accede a ellos a través de this.esploader.options si es necesario.
  }

  /**
   * Conecta o desconecta el dispositivo en modo toggle.
   * Si ya hay conexión activa, se desconecta;
   * si no hay conexión, se conecta usando el parámetro resetMode.
   *
   * @param {number|string} baudRate - Velocidad de transmisión (por ejemplo, 115200).
   * @param {boolean} noReset - Indica si se quiere usar el modo "no_reset".
   * @returns {Promise<{success: boolean, message?: string, chip?: string, error?: string}>}
   */
  async connect(baudRate, noReset) {
    // Modo toggle: desconectar si ya hay conexión
    if (this.esploader !== null) {
      await this.disconnect(); // Usamos el método de instancia para desconectar
      return JSON.stringify({
          success: true,
          message: "Desconectado exitosamente"
      });
    }

    try {
      // Solicitar el puerto si no tenemos uno ya seleccionado
      if (this.device === null) {
          this.device = await serialLib.requestPort({});
          console.log("Puerto seleccionado:", this.device);
      }

      // Crear la instancia de Transport si no existe
      if (this.transport === null) {
          this.transport = new Transport(this.device, true);
          console.log("Instancia de Transport creada");
      }

      // Opciones para ESPLoader
      const options = {
        transport: this.transport,
        baudrate: parseInt(baudRate),
        terminal: window.espLoaderTerminal, // Usamos el terminal global
        debugLogging: false
      };

      // Crear la instancia de ESPLoader
      this.esploader = new ESPLoader(options);
      console.log("Instancia de ESPLoader creada");

      // Determinar resetMode y, en caso de ser necesario, realizar pasos extras
      let resetMode = "default_reset";
      if (noReset) {
        resetMode = "no_reset";
        // Lógica específica para el modo no_reset: conectar y desconectar brevemente
        try {
          await this.transport.connect(parseInt(baudRate));
          await this.transport.disconnect();
          // Pausa para permitir que se estabilice la conexión
          await sleep(350);
        } catch (e) {
           // InvalidStateError (puerto ya abierto) es esperado a veces en no_reset
          if (e.name !== "InvalidStateError") {
                console.error("Error inesperado durante la fase de no_reset:", e);
                 // Intenta limpiar antes de re-lanzar
                await this.disconnect(false);
                return JSON.stringify({
                    success: false,
                    error: `Error en la fase de no_reset: ${e.message}`
                });
          } else {
                console.warn("El puerto ya estaba abierto (InvalidStateError) en no_reset. Se continúa.");
          }
        }
      }

      console.log(`Iniciando main con resetMode: ${resetMode}`);
      const chip = await this.esploader.main(resetMode);
      console.log(`Chip detectado: ${chip}`);

      return JSON.stringify({
        success: true,
        message: `Conectado exitosamente al dispositivo ${chip}`,
        chip: chip
      });

    } catch (err) {
      console.error("Error en espConnect:", err);
      // Intentar desconectar y limpiar el estado en caso de error
      await this.disconnect(false); // No esperar por unlock en caso de error
      // Re-lanzar el error para que el caller lo maneje
      //throw new Error(err.message || "Error desconocido durante la conexión");
      return JSON.stringify({
        success: false,
        error: err.message || "Error desconocido durante la conexión"
      });
    }
  }

  /**
   * Desconecta el dispositivo y limpia el estado.
   * @param {boolean} [waitForUnlock=true] - Esperar a que el puerto se desbloquee después de desconectar.
   * @returns {Promise<void>}
   */
  async disconnect(waitForUnlock = true) {
    console.log("Iniciando desconexión...");
    if (this.transport) {
      try {
        await this.transport.disconnect();
         console.log("Transport desconectado.");
        if (waitForUnlock) {
           console.log("Esperando unlock...");
           await this.transport.waitForUnlock(1500); // Espera un poco para que el puerto se libere
            console.log("Unlock completado.");
        }
      } catch (e) {
        console.warn("Error durante el transport disconnect:", e);
        // Continuar con el cierre del dispositivo aunque falle el disconnect del transport
      }
    }
    if (this.device) {
        try {
             // Cerrar el dispositivo si es necesario (depende de la implementación de Transport/Device)
            // await this.device.close(); // El disconnect del transport suele ser suficiente
            console.log("Dispositivo listo para ser liberado.");
        } catch(e) {
             console.warn("Error during device close:", e);
        }
        // No reseteamos this.device = null aquí inmediatamente,
        // podríamos querer reusar la referencia del puerto en una futura conexión.
        // Lo reseteamos solo si el usuario 'olvida' el puerto.
    }
    this.esploader = null;
    this.transport = null; // Reset transport so a new one is created on reconnect
    console.log("Desconexión completada. Estado limpiado (esploader, transport).");
  }

    /**
     * "Olvida" la referencia al dispositivo serial/USB seleccionado,
     * forzando una nueva solicitud requestPort() en la próxima conexión.
     */
    forgetDevice() {
        if (this.esploader !== null) {
            console.warn("Intentando olvidar dispositivo con conexión activa. Desconecte primero.");
            return;
        }
        this.device = null;
        console.log("Referencia al dispositivo olvidada.");
    }


  /**
   * Verifica si hay una conexión activa. Lanza un error si no la hay.
   * @private
   */
  _checkConnected() {
    if (!this.esploader) {
      throw new Error("No hay conexión activa. Conecte el dispositivo primero.");
    }
  }

  /**
   * Obtiene el Flash ID del dispositivo.
   *
   * @returns {Promise<{success: boolean, data?: object, error?: string}>}
   */
  async flashId() {
    try {
      this._checkConnected();
      const flashId = await this.esploader.flash_id();
      return {
        success: true,
        data: {
          manufacturerId: flashId.manufacturerId,
          deviceId: flashId.deviceId,
          sizeInBytes: flashId.sizeBytes,
          sizeInMB: (flashId.sizeBytes / (1024 * 1024)).toFixed(2)
        }
      };
    } catch (err) {
      console.error("Error en espFlashId:", err);
      //throw new Error(err.message || "Error desconocido al obtener Flash ID");
      return {
              success: true,
              message: err,
              chip: chip
            };
    }
  }

  /**
   * Lee datos de la flash del dispositivo.
   *
   * @param {number|string} address - Dirección desde donde leer (en formato string o number).
   * @param {number|string} size - Cantidad de bytes a leer (en formato string o number).
   * @returns {Promise<{success: boolean, data?: object, error?: string}>}
   */
  async readFlash(address, size) {
    try {
      this._checkConnected();
      const data = await this.esploader.read_flash(parseInt(address, 16), parseInt(size)); // Address is likely hex
      const hexString = Array.from(data).map(b => b.toString(16).padStart(2, '0')).join('');
      return {
        success: true,
        data: {
          hexData: hexString,
          readSize: parseInt(size),
          readAddress: parseInt(address, 16)
        }
      };
    } catch (err) {
      console.error("Error en espReadFlash:", err);
      throw new Error(err.message || "Error desconocido al leer flash");
    }
  }

  /**
   * Escribe datos en la flash del dispositivo.
   *
   * @param {number|string} address - Dirección donde escribir (en formato string o number).
   * @param {string} hexString - Datos en formato hexadecimal.
   * @param {boolean|string} eraseAll - Indica si se debe borrar toda la flash (boolean o string "true"/"false").
   * @returns {Promise<{success: boolean, message?: string, error?: string}>}
   */
  async writeFlash(address, hexString, eraseAll) {
    try {
      this._checkConnected();
       // Convertir la cadena hex a Uint8Array
       if (hexString.length % 2 !== 0) {
           throw new Error("La cadena hexadecimal debe tener un número par de caracteres.");
       }
      const bytes = new Uint8Array(hexString.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));

      const options = {
        erase_all: eraseAll === true || eraseAll === "true",
        // Los callbacks de progreso pueden ir aquí si quieres reportar al server
        // progressCallback: (data, progress) => { ... }
      };
      console.log(`Escribiendo ${bytes.length} bytes en la dirección 0x${parseInt(address, 16).toString(16)} con erase_all: ${options.erase_all}`);

      // write_flash espera un array de direcciones y un array de datos.
      await this.esploader.write_flash([parseInt(address, 16)], [bytes], options); // Address is likely hex

      return {
        success: true,
        message: "Flash escrita exitosamente"
      };
    } catch (err) {
      console.error("Error en espWriteFlash:", err);
      throw new Error(err.message || "Error desconocido al escribir flash");
    }
  }

  /**
   * Cambia el baudrate de la conexión.
   *
   * @param {number|string} newBaudRate - Nuevo baudrate.
   * @returns {Promise<{success: boolean, message?: string, error?: string}>}
   */
  async changeBaudrate(newBaudRate) {
    try {
      this._checkConnected();
      const baud = parseInt(newBaudRate);
      await this.esploader.change_baudrate(baud);
      return {
        success: true,
        message: "Baudrate cambiado a " + baud
      };
    } catch (err) {
      console.error("Error en espChangeBaudrate:", err);
       // Nota: cambiar el baudrate en el loader no cambia automáticamente el baudrate del transport subyacente.
       // Esptool-js gestiona esto internamente durante las operaciones.
      throw new Error(err.message || "Error desconocido al cambiar baudrate");
    }
  }

  /**
   * Ejecuta un hard reset en el dispositivo si es posible.
   * Requiere que la conexión esté activa y que el loader haya expuesto resetConstructors.
   *
   * @returns {Promise<{success: boolean, message?: string, error?: string}>}
   */
  async hardReset() {
      try {
          this._checkConnected();
          // Accedemos a resetConstructors a través de las opciones del loader
          if (this.esploader.options && this.esploader.options.resetConstructors && typeof this.esploader.options.resetConstructors.hardReset === "function") {
              // Usamos el transport y el baudrate de las opciones del loader
              const resetObj = this.esploader.options.resetConstructors.hardReset(this.transport, this.esploader.options.baudrate);
              console.log("Ejecutando hard reset...");
              await resetObj.reset();
              console.log("Hard reset completado.");
              return JSON.stringify({
                success: true,
                message: "Hard reset ejecutado"
              });
          } else {
              console.warn("Función hardReset no disponible en el loader actual o conexión no activa.");
              return JSON.stringify({
                success: false,
                error: "Función hardReset no disponible"
              });
          }
      } catch (err) {
          console.error("Error durante hard reset:", err);
          return JSON.stringify({
            success: false,
            error: err.message || "Error desconocido durante hard reset"
          });
      }
  }

}

// --- Instancia de la Clase y Exposición Global ---

// Creamos una única instancia del gestor de conexión.
const espConnectionManager = new EspConnectionManager();

// Exponemos los métodos de la instancia al objeto window.
// Cada función envuelve la llamada al método de la clase en una promesa
// y añade un catch para formatear el error de vuelta a un objeto con { success: false, error: ... }.
// Esto es conveniente para manejar los resultados y errores en el lado Vaadin
// usando CallJS.onPromise().

/**
 * Llama al método connect de la instancia del gestor.
 * Ver {@link EspConnectionManager#connect}.
 */
window.espConnect = async (baudRate, noReset) => {
    try {
        const result = await espConnectionManager.connect(baudRate, noReset);
        // Si la desconexión fue exitosa en modo toggle, el resultado ya es { success: true, message: ... }
        return result;
    } catch (err) {
        console.error("Error capturado en window.espConnect:", err);
        return { success: false, error: err.message || "Error desconocido" };
    }
};

/**
 * Llama al método disconnect de la instancia del gestor.
 * Desconecta la sesión pero mantiene la referencia al dispositivo serial seleccionado.
 * Ver {@link EspConnectionManager#disconnect}.
 */
window.espDisconnect = async () => {
     try {
         // Llama al método disconnect de la clase. No olvides el dispositivo.
         await espConnectionManager.disconnect();
         return JSON.stringify({
            success: true,
            message: "Desconectado exitosamente"
         });
     } catch (err) {
          console.error("Error capturado en window.espDisconnect:", err);
         // Aunque haya un error, el estado interno del gestor debería haberse limpiado
         return JSON.stringify({
            success: false,
            error: err.message || "Error desconocido durante la desconexión"
         });
     }
};

/**
 * Llama al método disconnect de la instancia del gestor y olvida el dispositivo.
 * Diseñado para ser llamado explícitamente para cerrar y permitir seleccionar otro puerto.
 * Ver {@link EspConnectionManager#disconnect}.
 */
window.espDisconnectAndForget = async () => {
     try {
         await espConnectionManager.disconnect();
         espConnectionManager.forgetDevice(); // Olvidar el puerto seleccionado
         return JSON.stringify({
            success: true,
            message: "Desconectado y dispositivo olvidado exitosamente"
         });
     } catch (err) {
          console.error("Error capturado en window.espDisconnectAndForget:", err);
          // A pesar del error, intentamos olvidar el dispositivo de todas formas
          espConnectionManager.forgetDevice();
         return JSON.stringify({
            success: false,
            error: err.message || "Error desconocido durante la desconexión y olvido"
         });
     }
}

/**
 * Llama al método flashId de la instancia del gestor.
 * Ver {@link EspConnectionManager#flashId}.
 */
window.espFlashId = async () => {
    try {
        return await espConnectionManager.flashId();
    } catch (err) {
         console.error("Error capturado en window.espFlashId:", err);
        return { success: false, error: err.message || "Error desconocido" };
    }
};

/**
 * Llama al método readFlash de la instancia del gestor.
 * Ver {@link EspConnectionManager#readFlash}.
 */
window.espReadFlash = async (address, size) => {
    try {
        return await espConnectionManager.readFlash(address, size);
    } catch (err) {
        console.error("Error capturado en window.espReadFlash:", err);
        return { success: false, error: err.message || "Error desconocido" };
    }
};

/**
 * Llama al método writeFlash de la instancia del gestor.
 * Ver {@link EspConnectionManager#writeFlash}.
 */
window.espWriteFlash = async (address, hexString, eraseAll) => {
    try {
        return await espConnectionManager.writeFlash(address, hexString, eraseAll);
    } catch (err) {
        console.error("Error capturado en window.espWriteFlash:", err);
        return { success: false, error: err.message || "Error desconocido" };
    }
};

/**
 * Llama al método changeBaudrate de la instancia del gestor.
 * Ver {@link EspConnectionManager#changeBaudrate}.
 */
window.espChangeBaudrate = async (newBaudRate) => {
    try {
        return await espConnectionManager.changeBaudrate(newBaudRate);
    } catch (err) {
        console.error("Error capturado en window.espChangeBaudrate:", err);
        return { success: false, error: err.message || "Error desconocido" };
    }
};

/**
 * Llama al método hardReset de la instancia del gestor.
 * Ver {@link EspConnectionManager#hardReset}.
 */
window.espHardReset = async () => { // Renombrado de testHardReset a espHardReset para consistencia
     try {
         return await espConnectionManager.hardReset();
     } catch (err) {
         console.error("Error capturado en window.espHardReset:", err);
         return JSON.stringify({
            success: false, error: err.message || "Error desconocido"
         });
     }
};

// Dentro de la sección 'Instancia de la Clase y Exposición Global...'

/**
 * Inspecciona el objeto esploader.options si la conexión está activa.
 * Solo para fines de depuración.
 * @returns {Promise<string>} - Promesa que resuelve con un JSON string del objeto options o un mensaje de error.
 */
window.inspectEspLoaderOptions = async () => {
    if (espConnectionManager.esploader && espConnectionManager.esploader.options) {
        try {
            // Quitamos referencias circulares o no serializables si las hay
            const optionsToSerialize = { ...espConnectionManager.esploader.options };
            delete optionsToSerialize.transport; // Transport no es serializable
            // Puedes añadir otras propiedades no serializables si causan error
            if (optionsToSerialize.resetConstructors) {
                 // Clonamos para evitar referencias, pero no podemos serializar las funciones directamente
                 optionsToSerialize.resetConstructors = { ...optionsToSerialize.resetConstructors };
                 // Convertimos las funciones a strings o indicadores para serializar
                 for (const key in optionsToSerialize.resetConstructors) {
                     if (typeof optionsToSerialize.resetConstructors[key] === 'function') {
                         optionsToSerialize.resetConstructors[key] = `[Function: ${key}]`;
                     }
                 }
            }
            return JSON.stringify({
                success: true,
                options: optionsToSerialize
            });
        } catch (e) {
             console.error("Error al serializar options:", e);
             return JSON.stringify({
                success: false,
                error: "Error interno al serializar options: " + e.message
             });
        }
    } else {
        return JSON.stringify({
            success: false,
            error: "No hay conexión activa o options no está disponible."
        });
    }
};

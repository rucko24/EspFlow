import React, { useEffect, useRef } from 'react';
import { ReactAdapterElement, RenderHooks } from 'Frontend/generated/flow/ReactAdapter';
import { ESPLoaderProvider, useEspLoader } from 'esptool-react';

const SyncEngine = ({
    command,
    commandId,
    setStatus,
    setLastLog
}: any) => {
    const { state, actions } = useEspLoader();
    const lastProcessedCommandId = useRef<number>(0);
    const lastLogLength = useRef<number>(0);

    useEffect(() => {
        if (setStatus) {
            setStatus(state.status);
        }
    }, [state.status, setStatus]);

    useEffect(() => {
        const currentLength = state.terminalOutput.length;
        if (currentLength > lastLogLength.current) {
            const newLine = state.terminalOutput[currentLength - 1];
            if (setLastLog && newLine) {
                setLastLog(newLine);
            }
            lastLogLength.current = currentLength;
        }
    }, [state.terminalOutput, setLastLog]);

    useEffect(() => {

        if (commandId > lastProcessedCommandId.current) {
            console.log(`[Esptool] Ejecutando comando Java: ${command}`);

            switch (command) {
                case 'connect':
                    actions.connect().catch((e) => console.error("Error connecting:", e));
                    break;
                case 'disconnect':
                    actions.disconnect();
                    break;
                default:
                    break;
            }

            lastProcessedCommandId.current = commandId;
        }
    }, [command, commandId, actions]);

    return <div style={{ display: 'none' }} />;
};

class EsptoolWrapper extends ReactAdapterElement {
    protected override render(hooks: RenderHooks): React.ReactElement | null {

        const [command] = hooks.useState<string>("command");
        const [commandId] = hooks.useState<number>("commandId");
        const [baudRate] = hooks.useState<number>("baudRate");
        const [debugLogging] = hooks.useState<boolean>("debugLogging");

        const [status, setStatus] = hooks.useState<string>("status");
        const [lastLog, setLastLog] = hooks.useState<string>("lastLog");

        return (
            <ESPLoaderProvider

                initialBaudrate={baudRate ?? 115200}
                initialDebugLogging={debugLogging ?? false}
            >
                <SyncEngine
                    command={command}
                    commandId={commandId}
                    setStatus={setStatus}
                    setLastLog={setLastLog}
                />
            </ESPLoaderProvider>
        );
    }
}

customElements.define('esptool-wrapper', EsptoolWrapper);
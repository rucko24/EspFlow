import React, { useEffect, useRef } from 'react';
import { ReactAdapterElement, RenderHooks } from 'Frontend/generated/flow/ReactAdapter';
import { ESPLoaderProvider, useEspLoader } from 'esptool-react';

const Engine = ({ command, commandId, onStatusUpdate }: any) => {
    const { state, actions } = useEspLoader();
    const lastProcessedId = useRef<number>(0);

    useEffect(() => {
        if (onStatusUpdate) onStatusUpdate(state.status);
    }, [state.status, onStatusUpdate]);

    useEffect(() => {
        if (commandId > lastProcessedId.current) {
            switch (command) {
                case 'connect':
                    actions.connect().catch((e) => console.error("Error:", e));
                    break;
                case 'disconnect':
                    actions.disconnect();
                    break;
            }
            lastProcessedId.current = commandId;
        }
    }, [command, commandId, actions]);

    return <div style={{ display: 'none' }} />;
};

class EsptoolWrapper extends ReactAdapterElement {
    protected override render(hooks: RenderHooks): React.ReactElement | null {

        const [command] = hooks.useState<string>("command");
        const [commandId] = hooks.useState<number>("commandId");
        const [status, setStatus] = hooks.useState<string>("status");

        const [baudRate] = hooks.useState<number>("baudRate");
        const [debugLogging] = hooks.useState<boolean>("debugLogging");

        return (
            <ESPLoaderProvider
                initialBaudrate={baudRate ?? 115200}
                initialDebugLogging={debugLogging ?? false}
            >
                <Engine
                    command={command}
                    commandId={commandId}
                    onStatusUpdate={setStatus}
                />
            </ESPLoaderProvider>
        );
    }
}

customElements.define('esptool-wrapper', EsptoolWrapper);
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/src/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'f18e1ddfe3f371f577b7e6d3f6053e472012c4de20fa1969f4df97b5a49a0cb5') {
    pending.push(import('./chunks/chunk-ac0e6714ed86dbcbc1646945f304d698c19eb6c553ed642fa5fe88d46fbc257b.js'));
  }
  if (key === 'dff6cb925a1200976d4a73b782756a23fb37a43d8682623b291e63a99b1b2f32') {
    pending.push(import('./chunks/chunk-e2454946031db7651ac5a1da7ace70945b397c399b0fef722fec2fd2c21e2130.js'));
  }
  if (key === '971f88183d4ec698b1447b7a9d104bbf1eaca2b2983ba8ab2b65462d9a63aea7') {
    pending.push(import('./chunks/chunk-0c94acd8290e6991befd6b90f5ea3773cf6c789326d32bbe43f73bb91cd517a3.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}
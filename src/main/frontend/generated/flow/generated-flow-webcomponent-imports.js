import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '0ef434eda28567f8892d9fbcf571b63ed3a043a8306c5aa023c487026f9fc8cc') {
    pending.push(import('./chunks/chunk-121ff563a421a724f21f6534ea47e22fdb5c29c5996258c48c8439eea8f3b4c9.js'));
  }
  if (key === '9f50b7caedb2c852bf9ecd33338f7cd3cf85a7a3a4cfbc4d73763a1989945b0d') {
    pending.push(import('./chunks/chunk-7508fe63eee1691130921d740cfbadcf3981c268d5d8cd0a3b222dad7e035e9e.js'));
  }
  if (key === 'fd07cb347eb35ba31cdbe5288c495dbe40d7e7a6af18fe4b202d6bfc83204ee2') {
    pending.push(import('./chunks/chunk-8eaa08e5526b222f58145de35f25b4437d236fe2cc89303e09bcd4dffc66f1f3.js'));
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
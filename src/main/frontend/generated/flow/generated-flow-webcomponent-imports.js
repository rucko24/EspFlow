import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'ae59ccbf61f925fc523468d821916353b4137249761af72dc206e52abbec2626') {
    pending.push(import('./chunks/chunk-ffcf18d7c42a2e2c4a9928bbac92f983e9fa72f6e7e5c1885b7c9694ab4622cb.js'));
  }
  if (key === '9f50b7caedb2c852bf9ecd33338f7cd3cf85a7a3a4cfbc4d73763a1989945b0d') {
    pending.push(import('./chunks/chunk-34a61e43ab29294dca0f71d1a1dbfae5318e6f4defd47efe36f9a4b7a97fef22.js'));
  }
  if (key === 'fd07cb347eb35ba31cdbe5288c495dbe40d7e7a6af18fe4b202d6bfc83204ee2') {
    pending.push(import('./chunks/chunk-ffcf18d7c42a2e2c4a9928bbac92f983e9fa72f6e7e5c1885b7c9694ab4622cb.js'));
  }
  if (key === '0ef434eda28567f8892d9fbcf571b63ed3a043a8306c5aa023c487026f9fc8cc') {
    pending.push(import('./chunks/chunk-121ff563a421a724f21f6534ea47e22fdb5c29c5996258c48c8439eea8f3b4c9.js'));
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
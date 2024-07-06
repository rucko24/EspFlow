import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/src/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '0ef434eda28567f8892d9fbcf571b63ed3a043a8306c5aa023c487026f9fc8cc') {
    pending.push(import('./chunks/chunk-6cd0ea702bab74126e1671548385485d0e63362292ad1cd2883a24ed3536a541.js'));
  }
  if (key === 'ae59ccbf61f925fc523468d821916353b4137249761af72dc206e52abbec2626') {
    pending.push(import('./chunks/chunk-341adefcc4eb5d9926ba517640166eb3b698c299302921edeb9d531bbad4d38d.js'));
  }
  if (key === '9f50b7caedb2c852bf9ecd33338f7cd3cf85a7a3a4cfbc4d73763a1989945b0d') {
    pending.push(import('./chunks/chunk-e8852bd7dfb3f878e918ff6d024bda73d9fe986c8fa73e9848b6929089f1c774.js'));
  }
  if (key === 'fd07cb347eb35ba31cdbe5288c495dbe40d7e7a6af18fe4b202d6bfc83204ee2') {
    pending.push(import('./chunks/chunk-341adefcc4eb5d9926ba517640166eb3b698c299302921edeb9d531bbad4d38d.js'));
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
import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/generated/jar-resources/login-view-background.css?inline';
const $css_0 = typeof $cssFromFile_0  === 'string' ? unsafeCSS($cssFromFile_0) : $cssFromFile_0;
registerStyles('vaadin-login-overlay-wrapper', $css_0, {moduleId: 'flow_css_mod_0'});
import $cssFromFile_1 from 'Frontend/generated/jar-resources/animate.css?inline';

injectGlobalCss($cssFromFile_1.toString(), 'CSSImport end', document);
import $cssFromFile_2 from 'xterm/css/xterm.css?inline';

injectGlobalCss($cssFromFile_2.toString(), 'CSSImport end', document);
import $cssFromFile_3 from 'Frontend/generated/jar-resources/carousel-demo-styles.css?inline';
const $css_3 = typeof $cssFromFile_3  === 'string' ? unsafeCSS($cssFromFile_3) : $cssFromFile_3;
registerStyles('fc-l2t-paper-slider', $css_3, {moduleId: 'flow_css_mod_3'});
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-overlay.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'ba5c454d3a92512853bd81758a5042cc7ffcf18a4943281cba5418e847285d30') {
    pending.push(import('./chunks/chunk-121ff563a421a724f21f6534ea47e22fdb5c29c5996258c48c8439eea8f3b4c9.js'));
  }
  if (key === 'abde3f4745195c06e11065a9babe1321675c236a20978a3047ccc3839fa05b82') {
    pending.push(import('./chunks/chunk-778de7907c53b6d2b9d1c87cdf55f6ecf41af5672eebfc7f0ad0aab84bfd188e.js'));
  }
  if (key === 'e0bcf4f3081bab831fa7200d3a17527641eff25d3ccd4229c1b730c20dd50551') {
    pending.push(import('./chunks/chunk-25e0d4aaec262ac5781f4a2816f80841852108562a984988ea3c96e72ea865f4.js'));
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
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
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/src/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'ba5c454d3a92512853bd81758a5042cc7ffcf18a4943281cba5418e847285d30') {
    pending.push(import('./chunks/chunk-6cd0ea702bab74126e1671548385485d0e63362292ad1cd2883a24ed3536a541.js'));
  }
  if (key === 'f73b0b856e2a1348c5a831f2ffa8e20fbc326dab6b2464ca178db546eded0897') {
    pending.push(import('./chunks/chunk-693ec8a4fe827a450babdff1e43ff8507264f72006eb980349ec4b7b60b21698.js'));
  }
  if (key === 'abde3f4745195c06e11065a9babe1321675c236a20978a3047ccc3839fa05b82') {
    pending.push(import('./chunks/chunk-f93b8151c9f7f58a6906c2c17c4dfc1221ff7fb38f0422a7dfc166fd50fd692d.js'));
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
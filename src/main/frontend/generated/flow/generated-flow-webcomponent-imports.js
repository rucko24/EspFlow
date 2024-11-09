import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/generated/jar-resources/login-view-background.css?inline';
import $cssFromFile_1 from 'Frontend/generated/jar-resources/animate.css?inline';
import $cssFromFile_2 from 'xterm/css/xterm.css?inline';
import $cssFromFile_3 from 'Frontend/generated/jar-resources/carousel-demo-styles.css?inline';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
const $css_0 = typeof $cssFromFile_0  === 'string' ? unsafeCSS($cssFromFile_0) : $cssFromFile_0;
registerStyles('vaadin-login-overlay-wrapper', $css_0, {moduleId: 'flow_css_mod_0'});

injectGlobalCss($cssFromFile_1.toString(), 'CSSImport end', document);
injectGlobalWebcomponentCss($cssFromFile_1.toString());

injectGlobalCss($cssFromFile_2.toString(), 'CSSImport end', document);
injectGlobalWebcomponentCss($cssFromFile_2.toString());
const $css_3 = typeof $cssFromFile_3  === 'string' ? unsafeCSS($cssFromFile_3) : $cssFromFile_3;
registerStyles('fc-l2t-paper-slider', $css_3, {moduleId: 'flow_css_mod_3'});

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'ea36561a69622ad9a5c744a824192556322c6c6c5dc85f215e8d86e6ab6faa86') {
    pending.push(import('./chunks/chunk-c1e3cad48b56a7845ad0d4f553287a6185a898bba2b8a83e0ec9b7438718d556.js'));
  }
  if (key === '138df28b007a65a4c1a6339dca961aff56ab2f75353bf8a3394729e2c030b034') {
    pending.push(import('./chunks/chunk-c1e3cad48b56a7845ad0d4f553287a6185a898bba2b8a83e0ec9b7438718d556.js'));
  }
  if (key === 'ba5c454d3a92512853bd81758a5042cc7ffcf18a4943281cba5418e847285d30') {
    pending.push(import('./chunks/chunk-c1e3cad48b56a7845ad0d4f553287a6185a898bba2b8a83e0ec9b7438718d556.js'));
  }
  if (key === 'fe9ccae9f90f21c3df23cb7d89236f466fb86e091dfeabfeda749a37fc7908df') {
    pending.push(import('./chunks/chunk-c1e3cad48b56a7845ad0d4f553287a6185a898bba2b8a83e0ec9b7438718d556.js'));
  }
  if (key === 'f73b0b856e2a1348c5a831f2ffa8e20fbc326dab6b2464ca178db546eded0897') {
    pending.push(import('./chunks/chunk-49cec491c8ea6d59546ad8cffadcbe2f74f987e28048441661aaf00442ceff2e.js'));
  }
  if (key === 'abde3f4745195c06e11065a9babe1321675c236a20978a3047ccc3839fa05b82') {
    pending.push(import('./chunks/chunk-9ea9e2c790a72d0ba1b5fd60c88b3f5d9f5fd14e7187a6820b66116bee038088.js'));
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
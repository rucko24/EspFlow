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
  if (key === 'abde3f4745195c06e11065a9babe1321675c236a20978a3047ccc3839fa05b82') {
    pending.push(import('./chunks/chunk-9ea9e2c790a72d0ba1b5fd60c88b3f5d9f5fd14e7187a6820b66116bee038088.js'));
  }
  if (key === '53aec1116595a67c38e2aa9768651e550a280174abeb710e62f1fdd956f6f451') {
    pending.push(import('./chunks/chunk-d773525c9c38517440629a8dfea84d2593fbe67d8dc6ad79023408b64beee761.js'));
  }
  if (key === 'cdc2fc2c701a5a5f5b93103c402a519e3f3a896206a67d08cd7ce7f1ca490ccb') {
    pending.push(import('./chunks/chunk-9ea9e2c790a72d0ba1b5fd60c88b3f5d9f5fd14e7187a6820b66116bee038088.js'));
  }
  if (key === '1773598ab52c9094e14778569cc887c457a6d30eb24780fc9fa08fb63716b25b') {
    pending.push(import('./chunks/chunk-9ea9e2c790a72d0ba1b5fd60c88b3f5d9f5fd14e7187a6820b66116bee038088.js'));
  }
  if (key === 'fc5ec61339deebde3eb8a43d6adc8479b075426a68ae6df014d26c3de2652a5b') {
    pending.push(import('./chunks/chunk-9ea9e2c790a72d0ba1b5fd60c88b3f5d9f5fd14e7187a6820b66116bee038088.js'));
  }
  if (key === 'ba5c454d3a92512853bd81758a5042cc7ffcf18a4943281cba5418e847285d30') {
    pending.push(import('./chunks/chunk-c1e3cad48b56a7845ad0d4f553287a6185a898bba2b8a83e0ec9b7438718d556.js'));
  }
  if (key === '9bc4325455c2005cc40699e18da7539b7c560d21c22a0c561cd495f003a36873') {
    pending.push(import('./chunks/chunk-d773525c9c38517440629a8dfea84d2593fbe67d8dc6ad79023408b64beee761.js'));
  }
  if (key === '6087cdb43d41fea423c62ce0cea1a1d821fd9fbdc4a342358c2d2651e3f9dc5d') {
    pending.push(import('./chunks/chunk-d773525c9c38517440629a8dfea84d2593fbe67d8dc6ad79023408b64beee761.js'));
  }
  if (key === '21d6e81df35631baa87f967823704b7951422444516fa9ff883ab465432e063b') {
    pending.push(import('./chunks/chunk-44aa42cb402b392d116b55e6dd74d4f919f692eb2785949ab8b7e01bbf265fbf.js'));
  }
  if (key === 'f73b0b856e2a1348c5a831f2ffa8e20fbc326dab6b2464ca178db546eded0897') {
    pending.push(import('./chunks/chunk-49cec491c8ea6d59546ad8cffadcbe2f74f987e28048441661aaf00442ceff2e.js'));
  }
  if (key === '16f8f533c11be545a9a363547d448ed60e712870b123104670675403c559d4b2') {
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
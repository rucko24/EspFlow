import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/generated/jar-resources/login-view-background.css?inline';
import $cssFromFile_1 from 'Frontend/generated/jar-resources/animate.css?inline';
import $cssFromFile_2 from 'Frontend/generated/jar-resources/styles/vaadin-checkbox.css?inline';
import $cssFromFile_3 from 'xterm/css/xterm.css?inline';
import $cssFromFile_4 from 'Frontend/generated/jar-resources/carousel-demo-styles.css?inline';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-overlay.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/combo-box/theme/lumo/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav.js';
import '@vaadin/radio-group/theme/lumo/vaadin-radio-group.js';
import '@vaadin/radio-group/theme/lumo/vaadin-radio-button.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/tabs/theme/lumo/vaadin-tab.js';
import '@vaadin/progress-bar/theme/lumo/vaadin-progress-bar.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/split-layout/theme/lumo/vaadin-split-layout.js';
import '@vaadin/checkbox-group/theme/lumo/vaadin-checkbox-group.js';
import 'Frontend/generated/jar-resources/menubarConnector.js';
import '@vaadin/menu-bar/theme/lumo/vaadin-menu-bar.js';
import 'Frontend/generated/jar-resources/messageListConnector.js';
import '@vaadin/message-list/theme/lumo/vaadin-message-list.js';
import 'Frontend/generated/jar-resources/clipboard-helper.js';
import '@vaadin/dialog/theme/lumo/vaadin-dialog.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/confirm-dialog/theme/lumo/vaadin-confirm-dialog.js';
import 'Frontend/generated/jar-resources/fc-xterm/xterm.ts';
import '@vaadin/popover/theme/lumo/vaadin-popover.js';
import 'Frontend/generated/jar-resources/vaadin-popover/popover.ts';
import 'Frontend/generated/jar-resources/paper-slider/fc-l2t-paper-slider.js';
import '@vaadin/integer-field/theme/lumo/vaadin-integer-field.js';
import 'Frontend/generated/jar-resources/fc-xterm/xterm-element.ts';
import '@vaadin/password-field/theme/lumo/vaadin-password-field.js';
import '@vaadin/email-field/theme/lumo/vaadin-email-field.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/upload/theme/lumo/vaadin-upload.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav-item.js';
import '@vaadin/context-menu/theme/lumo/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/multi-select-combo-box/theme/lumo/vaadin-multi-select-combo-box.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/checkbox/theme/lumo/vaadin-checkbox.js';
import '@vaadin/icons/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/file-download-wrapper.js';
import '@vaadin/text-area/theme/lumo/vaadin-text-area.js';
import '@vaadin/app-layout/theme/lumo/vaadin-drawer-toggle.js';
import '@vaadin/tabsheet/theme/lumo/vaadin-tabsheet.js';
import '@vaadin/tabs/theme/lumo/vaadin-tabs.js';
import '@vaadin/avatar/theme/lumo/vaadin-avatar.js';
import '@vaadin/scroller/theme/lumo/vaadin-scroller.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
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

injectGlobalCss($cssFromFile_3.toString(), 'CSSImport end', document);
injectGlobalWebcomponentCss($cssFromFile_3.toString());
const $css_4 = typeof $cssFromFile_4  === 'string' ? unsafeCSS($cssFromFile_4) : $cssFromFile_4;
registerStyles('fc-l2t-paper-slider', $css_4, {moduleId: 'flow_css_mod_4'});

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'abde3f4745195c06e11065a9babe1321675c236a20978a3047ccc3839fa05b82') {
    pending.push(import('./chunks/chunk-dfb54933ed685aa6fb566d9e5d25d938f6bf9d178fc3806a506acc899a39c684.js'));
  }
  if (key === 'cdc2fc2c701a5a5f5b93103c402a519e3f3a896206a67d08cd7ce7f1ca490ccb') {
    pending.push(import('./chunks/chunk-0e6adad4abdd8049e2ac2abec96d37747caecb8387a785956cfe63452555d5bd.js'));
  }
  if (key === '16f8f533c11be545a9a363547d448ed60e712870b123104670675403c559d4b2') {
    pending.push(import('./chunks/chunk-0e6adad4abdd8049e2ac2abec96d37747caecb8387a785956cfe63452555d5bd.js'));
  }
  if (key === 'fc5ec61339deebde3eb8a43d6adc8479b075426a68ae6df014d26c3de2652a5b') {
    pending.push(import('./chunks/chunk-0e6adad4abdd8049e2ac2abec96d37747caecb8387a785956cfe63452555d5bd.js'));
  }
  if (key === '1773598ab52c9094e14778569cc887c457a6d30eb24780fc9fa08fb63716b25b') {
    pending.push(import('./chunks/chunk-0e6adad4abdd8049e2ac2abec96d37747caecb8387a785956cfe63452555d5bd.js'));
  }
  if (key === 'ba5c454d3a92512853bd81758a5042cc7ffcf18a4943281cba5418e847285d30') {
    pending.push(import('./chunks/chunk-19e94161ac0a21e79637d1f4f67568ce75f7d1c88634298e06f966380c8ff5a9.js'));
  }
  if (key === 'f73b0b856e2a1348c5a831f2ffa8e20fbc326dab6b2464ca178db546eded0897') {
    pending.push(import('./chunks/chunk-49cec491c8ea6d59546ad8cffadcbe2f74f987e28048441661aaf00442ceff2e.js'));
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
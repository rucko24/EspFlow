import { t as u, x as d, u as e } from "./copilot-DKr2YKvS.js";
import { B as g } from "./base-panel-Cl8rL5aQ.js";
import { o as h } from "./unsafe-html-B3_veTIk.js";
import { i as l } from "./icons-DrBP115r.js";
const f = "copilot-shortcuts-panel{font:var(--font-xsmall);padding:var(--space-200);display:flex;flex-direction:column;gap:var(--space-50)}copilot-shortcuts-panel h3{font:var(--font-xsmall-strong);margin:0;padding:0}copilot-shortcuts-panel h3:not(:first-of-type){margin-top:var(--space-200)}copilot-shortcuts-panel ul{list-style:none;margin:0;padding:0 var(--space-50);display:flex;flex-direction:column}copilot-shortcuts-panel ul li{display:flex;align-items:center;gap:var(--space-150);padding:var(--space-75) 0}copilot-shortcuts-panel ul li:not(:last-of-type){border-bottom:1px dashed var(--border-color)}copilot-shortcuts-panel ul li svg{height:16px;width:16px}copilot-shortcuts-panel ul li .kbds{flex:1;text-align:right}copilot-shortcuts-panel kbd{display:inline-block;border-radius:var(--radius-1);border:1px solid var(--border-color);min-width:1em;min-height:1em;text-align:center;margin:0 .1em;padding:.25em;box-sizing:border-box;font-size:var(--font-size-1);font-family:var(--font-family);line-height:1}";
var m = Object.defineProperty, b = Object.getOwnPropertyDescriptor, v = (i, r, n, a) => {
  for (var t = a > 1 ? void 0 : a ? b(r, n) : r, s = i.length - 1, p; s >= 0; s--)
    (p = i[s]) && (t = (a ? p(r, n, t) : p(t)) || t);
  return a && t && m(r, n, t), t;
};
let c = class extends g {
  render() {
    return d`<style>
        ${f}
      </style>
      <h3>Global</h3>
      <ul>
        <li>${l.vaadinLogo} Copilot ${o(e.toggleCopilot)}</li>
        <li>${l.terminal} Command window ${o(e.toggleCommandWindow)}</li>
        <li>${l.undo} Undo ${o(e.undo)}</li>
        <li>${l.redo} Redo ${o(e.redo)}</li>
      </ul>
      <h3>Selected component</h3>
      <ul>
        <li>${l.code} Go to source ${o(e.goToSource)}</li>
        <li>${l.copy} Duplicate ${o(e.duplicate)}</li>
        <li>${l.userUp} Select parent ${o(e.selectParent)}</li>
        <li>${l.userLeft} Select previous sibling ${o(e.selectPreviousSibling)}</li>
        <li>${l.userRight} Select first child / next sibling ${o(e.selectNextSibling)}</li>
        <li>${l.trash} Delete ${o(e.delete)}</li>
      </ul>`;
  }
};
c = v([
  u("copilot-shortcuts-panel")
], c);
function o(i) {
  return d`<span class="kbds">${h(i)}</span>`;
}
const x = {
  header: "Keyboard Shortcuts",
  expanded: !0,
  expandable: !1,
  draggable: !0,
  panelOrder: 0,
  floating: !1,
  tag: "copilot-shortcuts-panel",
  width: 400,
  height: 475,
  floatingPosition: {
    top: 50,
    left: 50
  }
}, $ = {
  init(i) {
    i.addPanel(x);
  }
};
window.Vaadin.copilot.plugins.push($);

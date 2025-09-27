import { j as p, B as d, a2 as m, T as R, al as b, U as y, am as $, M as F, E as u, an as x, y as w, ao as T, r as f } from "./copilot-Clqe7NnV.js";
import { r as h } from "./state-B6616V18.js";
import { B as O } from "./base-panel-DFXiqdfs.js";
import { i as v } from "./icons-jl0-u2LC.js";
import { m as P } from "./overlay-monkeypatch-ByWcx4Vt.js";
const S = "copilot-features-panel{padding:var(--space-100);font:var(--font-xsmall);display:grid;grid-template-columns:auto 1fr;gap:var(--space-50);height:auto}copilot-features-panel a{display:flex;align-items:center;gap:var(--space-50);white-space:nowrap}copilot-features-panel a svg{height:12px;width:12px;min-height:12px;min-width:12px}";
var q = Object.defineProperty, A = Object.getOwnPropertyDescriptor, o = (e, t, a, s) => {
  for (var r = s > 1 ? void 0 : s ? A(t, a) : t, i = e.length - 1, n; i >= 0; i--)
    (n = e[i]) && (r = (s ? n(t, a, r) : n(r)) || r);
  return s && r && q(t, a, r), r;
};
const l = window.Vaadin.devTools;
let g = class extends O {
  constructor() {
    super(...arguments), this.toggledFeaturesThatAreRequiresServerRestart = [];
  }
  render() {
    return d` <style>
        ${S}
      </style>
      ${p.featureFlags.map(
      (e) => d`
          <copilot-toggle-button
            .title="${e.title}"
            ?checked=${e.enabled}
            @on-change=${(t) => this.toggleFeatureFlag(t, e)}>
          </copilot-toggle-button>
          <a class="ahreflike" href="${e.moreInfoLink}" title="Learn more" target="_blank"
            >learn more ${v.share}</a
          >
        `
    )}`;
  }
  toggleFeatureFlag(e, t) {
    const a = e.target.checked;
    m("use-feature", { source: "toggle", enabled: a, id: t.id }), l.frontendConnection ? (l.frontendConnection.send("setFeature", { featureId: t.id, enabled: a }), t.requiresServerRestart && p.toggleServerRequiringFeatureFlag(t), R({
      type: y.INFORMATION,
      message: `“${t.title}” ${a ? "enabled" : "disabled"}`,
      details: t.requiresServerRestart ? b() : void 0,
      dismissId: `feature${t.id}${a ? "Enabled" : "Disabled"}`
    }), $()) : l.log("error", `Unable to toggle feature ${t.title}: No server connection available`);
  }
};
o([
  h()
], g.prototype, "toggledFeaturesThatAreRequiresServerRestart", 2);
g = o([
  f("copilot-features-panel")
], g);
let c = class extends F {
  constructor() {
    super(...arguments), this.serverRestarting = !1;
  }
  createRenderRoot() {
    return this;
  }
  updated(e) {
    super.updated(e), P(this.renderRoot);
  }
  render() {
    if (p.serverRestartRequiringToggledFeatureFlags.length === 0)
      return u;
    if (!x())
      return u;
    const e = this.serverRestarting ? "Restarting..." : "Click to restart server";
    return d`
      <style>
        .fade-in-out {
          animation: fadeInOut 2s ease-in-out infinite;
          animation-play-state: running;
        }
        .fade-in-out:hover {
          animation-play-state: paused;
          opacity: 1 !important;
        }
        ${w}
      </style>
      <button
        ?disabled="${this.serverRestarting}"
        id="restart-server-btn"
        class="icon ${this.serverRestarting ? "" : "fade-in-out"}"
        @click=${() => {
      this.serverRestarting = !0, T();
    }}>
        <span>${v.refresh}</span>
      </button>
      <vaadin-tooltip for="restart-server-btn" text=${e}></vaadin-tooltip>
    `;
  }
};
o([
  h()
], c.prototype, "serverRestarting", 2);
c = o([
  f("copilot-features-actions")
], c);
const C = {
  header: "Features",
  expanded: !1,
  panelOrder: 35,
  panel: "right",
  floating: !1,
  tag: "copilot-features-panel",
  helpUrl: "https://vaadin.com/docs/latest/flow/configuration/feature-flags",
  actionsTag: "copilot-features-actions"
}, I = {
  init(e) {
    e.addPanel(C);
  }
};
window.Vaadin.copilot.plugins.push(I);
export {
  c as CopilotFeaturesActions,
  g as CopilotFeaturesPanel
};

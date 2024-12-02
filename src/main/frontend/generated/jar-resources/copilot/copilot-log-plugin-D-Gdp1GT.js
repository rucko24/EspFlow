import { e as E, W as k, X as m, T as d, u as l, U as c, Y as M, Q as T, M as L, b as S, Z as R, l as x } from "./copilot-TlOHSgJo.js";
import { r as v } from "./state-u4Ab_TUl.js";
import { B as D } from "./base-panel-DqpMqJVg.js";
import { i as n } from "./icons-BIim_4GH.js";
const C = "copilot-log-panel{padding:var(--space-100);font:var(--font-xsmall);display:flex;flex-direction:column;gap:var(--space-50);overflow-y:auto;max-width:100vw}copilot-log-panel .row{display:flex;align-items:flex-start;padding:var(--space-50) var(--space-100);border-radius:var(--radius-2);gap:var(--space-100)}copilot-log-panel .row.information{background-color:var(--blue-50)}copilot-log-panel .row.warning{background-color:var(--yellow-50)}copilot-log-panel .row.error{background-color:var(--red-50)}copilot-log-panel .type{margin-top:var(--space-25)}copilot-log-panel .type.error{color:var(--red)}copilot-log-panel .type.warning{color:var(--yellow)}copilot-log-panel .type.info{color:var(--color)}copilot-log-panel .message{display:flex;flex-direction:column;flex-grow:1;gap:var(--space-25);overflow:hidden}copilot-log-panel .message>*:not(.expanded){white-space:nowrap;overflow:hidden;text-overflow:ellipsis;width:100%}copilot-log-panel .message>.expanded{overflow-wrap:break-word}copilot-log-panel .firstrow{display:flex;align-items:baseline;gap:.5em;flex-direction:column}copilot-log-panel .firstrowmessage{width:100%;overflow:hidden;text-overflow:ellipsis}copilot-log-panel button{padding:0;border:0;background:transparent}copilot-log-panel svg{height:12px;width:12px}copilot-log-panel .secondrow,copilot-log-panel .timestamp{font-size:var(--font-size-0);line-height:var(--line-height-1)}copilot-log-panel .expand span{height:12px;width:12px}";
var I = Object.defineProperty, q = Object.getOwnPropertyDescriptor, h = (e, t, a, o) => {
  for (var s = o > 1 ? void 0 : o ? q(t, a) : t, p = e.length - 1, i; p >= 0; p--)
    (i = e[p]) && (s = (o ? i(t, a, s) : i(s)) || s);
  return o && s && I(t, a, s), s;
};
class B {
  constructor() {
    this.showTimestamps = !1, R(this);
  }
  toggleShowTimestamps() {
    this.showTimestamps = !this.showTimestamps;
  }
}
const g = new B();
let r = class extends D {
  constructor() {
    super(...arguments), this.unreadErrors = !1, this.messages = [], this.nextMessageId = 1, this.transitionDuration = 0;
  }
  connectedCallback() {
    super.connectedCallback(), this.onCommand("log", (e) => {
      this.handleLogEventData({ type: e.data.type, message: e.data.message });
    }), this.onEventBus("log", (e) => this.handleLogEvent(e)), this.onEventBus("update-log", (e) => this.updateLog(e.detail)), this.onEventBus("notification-shown", (e) => this.handleNotification(e)), this.onEventBus("clear-log", () => this.clear()), this.reaction(
      () => E.sectionPanelResizing,
      () => {
        this.requestUpdate();
      }
    ), this.transitionDuration = parseInt(
      window.getComputedStyle(this).getPropertyValue("--dev-tools-transition-duration"),
      10
    ), k((e) => {
      this.log(e.type, e.message, !!e.internal, e.details, e.link);
    }), m.forEach((e) => {
      this.log(e.type, e.message, !!e.internal, e.details, e.link);
    }), m.length = 0;
  }
  clear() {
    this.messages = [];
  }
  handleNotification(e) {
    this.log(e.detail.type, e.detail.message, !0, e.detail.details, e.detail.link, void 0);
  }
  handleLogEvent(e) {
    this.handleLogEventData(e.detail);
  }
  handleLogEventData(e) {
    this.log(
      e.type,
      e.message,
      !!e.internal,
      e.details,
      e.link,
      d(e.expandedMessage),
      d(e.expandedDetails),
      e.id
    );
  }
  activate() {
    this.unreadErrors = !1, this.updateComplete.then(() => {
      const e = this.renderRoot.querySelector(".message:last-child");
      e && e.scrollIntoView();
    });
  }
  render() {
    return l`<style>
        ${C}
      </style>
      ${this.messages.map((e) => this.renderMessage(e))} `;
  }
  renderMessage(e) {
    let t, a, o;
    return e.type === c.ERROR ? (t = "error", o = n.exclamationMark, a = "Error") : e.type === c.WARNING ? (t = "warning", o = n.warning, a = "Warning") : (t = "info", o = n.info, a = "Info"), e.internal && (t += " internal"), l`
      <div
        data-id="${e.id}"
        class="row ${e.type} ${e.details || e.link ? "has-details" : ""}">
        <span class="type ${t}" title="${a}">${o}</span>
        <div class="message" @click=${() => this.toggleExpanded(e)}>
          <span class="firstrow">
            <span class="timestamp" ?hidden=${!g.showTimestamps}>${W(e.timestamp)}</span>
            <span class="firstrowmessage"
              >${e.expanded && e.expandedMessage ? e.expandedMessage : e.message}
            </span>
          </span>
          ${e.expanded ? l` <span class="secondrow expanded">${e.expandedDetails ?? e.details}</span>` : l`<span class="secondrow" ?hidden="${!e.details && !e.link}"
                >${d(e.details)}
                ${e.link ? l`<a class="ahreflike" href="${e.link}" target="_blank">Learn more</a>` : ""}</span
              >`}
        </div>
        <button
          aria-label="Expand details"
          theme="icon tertiary"
          class="expand"
          @click=${() => this.toggleExpanded(e)}
          ?hidden=${!this.canBeExpanded(e)}>
          <span>${e.expanded ? n.chevronDown : n.chevronRight}</span>
        </button>
      </div>
    `;
  }
  log(e, t, a, o, s, p, i, y) {
    const $ = this.nextMessageId;
    this.nextMessageId += 1, i || (i = t);
    const u = {
      id: $,
      type: e,
      message: t,
      details: o,
      link: s,
      dontShowAgain: !1,
      deleted: !1,
      expanded: !1,
      expandedMessage: p,
      expandedDetails: i,
      timestamp: /* @__PURE__ */ new Date(),
      internal: a,
      userId: y
    };
    for (this.messages.push(u); this.messages.length > r.MAX_LOG_ROWS; )
      this.messages.shift();
    return this.requestUpdate(), this.updateComplete.then(() => {
      const f = this.renderRoot.querySelector(".message:last-child");
      f ? (setTimeout(() => f.scrollIntoView({ behavior: "smooth" }), this.transitionDuration), this.unreadErrors = !1) : e === c.ERROR && (this.unreadErrors = !0);
    }), u;
  }
  updateLog(e) {
    let t = this.messages.find((a) => a.userId === e.id);
    t || (t = this.log(c.INFORMATION, "<Log message to update was not found>", !1)), Object.assign(t, e), M(t.expandedDetails) && (t.expandedDetails = d(t.expandedDetails)), this.requestUpdate();
  }
  updated() {
    const e = this.querySelector(".row:last-child");
    e && this.isTooLong(e.querySelector(".firstrowmessage")) && e.querySelector("button.expand")?.removeAttribute("hidden");
  }
  toggleExpanded(e) {
    this.canBeExpanded(e) && (e.expanded = !e.expanded, this.requestUpdate()), T("use-log", { source: "toggleExpanded" });
  }
  canBeExpanded(e) {
    if (e.expandedMessage || e.expanded)
      return !0;
    const t = this.querySelector(`[data\\-id="${e.id}"]`)?.querySelector(
      ".firstrowmessage"
    );
    return this.isTooLong(t);
  }
  isTooLong(e) {
    return e && e.offsetWidth < e.scrollWidth;
  }
};
r.MAX_LOG_ROWS = 1e3;
h([
  v()
], r.prototype, "unreadErrors", 2);
h([
  v()
], r.prototype, "messages", 2);
r = h([
  x("copilot-log-panel")
], r);
let w = class extends L {
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.style.display = "flex";
  }
  render() {
    return l`
      <button title="Clear log" aria-label="Clear log" theme="icon tertiary">
        <span
          @click=${() => {
      S.emit("clear-log", {});
    }}
          >${n.trash}</span
        >
      </button>
      <button title="Toggle timestamps" aria-label="Toggle timestamps" theme="icon tertiary">
        <span
          class="${g.showTimestamps ? "on" : "off"}"
          @click=${() => {
      g.toggleShowTimestamps();
    }}
          >${n.clock}</span
        >
      </button>
    `;
  }
};
w = h([
  x("copilot-log-panel-actions")
], w);
const _ = {
  header: "Log",
  expanded: !0,
  panelOrder: 0,
  panel: "bottom",
  floating: !1,
  tag: "copilot-log-panel",
  actionsTag: "copilot-log-panel-actions"
}, b = {
  init(e) {
    e.addPanel(_);
  }
};
window.Vaadin.copilot.plugins.push(b);
const P = { hour: "numeric", minute: "numeric", second: "numeric", fractionalSecondDigits: 3 }, A = new Intl.DateTimeFormat(navigator.language, P);
function W(e) {
  return A.format(e);
}
export {
  w as Actions,
  r as CopilotLogPanel
};

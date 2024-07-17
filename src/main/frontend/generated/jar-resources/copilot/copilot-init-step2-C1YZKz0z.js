import { t as k, M as T, C as S, l as a, b as c, m as G, r as N, n as D, e as l, O as X, A as z, o as p, x as u, q as Z, u as R, w as U, T as A, y as K, z as J, B as Q, D as ee, E as te, F as ie } from "./copilot-DKr2YKvS.js";
import { n as E, r as oe } from "./state-eevs1asN.js";
import { e as b, m as ne } from "./overlay-monkeypatch-BpxWVdY5.js";
import { i as x } from "./icons-DrBP115r.js";
import { o as se } from "./unsafe-html-B3_veTIk.js";
import { h as re } from "./react-utils-DFD62MFY.js";
import { dismissNotification as ae, showNotification as le } from "./copilot-notification-CBUUP7Vd.js";
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
function de(e) {
  return (t, n) => {
    const o = typeof t == "function" ? t : t[n];
    Object.assign(o, e);
  };
}
const F = "@keyframes bounce{0%{transform:scale(.8)}50%{transform:scale(1.5)}to{transform:scale(1)}}@keyframes pulse{0%{box-shadow:0 0 calc(var(--pulse-size) * 2) 0 transparent}25%{box-shadow:0 0 calc(var(--pulse-size) * 2) 0 var(--pulse-first-color, var(--selection-color))}50%{box-shadow:0 0 calc(var(--pulse-size) * 2) 0 transparent}75%{box-shadow:0 0 calc(var(--pulse-size) * 2) 0 var(--pulse-second-color, var(--accent-color))}to{box-shadow:0 0 calc(var(--pulse-size) * 2) 0 transparent}}@keyframes around-we-go-again{0%{background-position:0 0,0 0,calc(var(--glow-size) * -.5) calc(var(--glow-size) * -.5),calc(100% + calc(var(--glow-size) * .5)) calc(100% + calc(var(--glow-size) * .5))}25%{background-position:0 0,0 0,calc(100% + calc(var(--glow-size) * .5)) calc(var(--glow-size) * -.5),calc(var(--glow-size) * -.5) calc(100% + calc(var(--glow-size) * .5))}50%{background-position:0 0,0 0,calc(100% + calc(var(--glow-size) * .5)) calc(100% + calc(var(--glow-size) * .5)),calc(var(--glow-size) * -.5) calc(var(--glow-size) * -.5)}75%{background-position:0 0,0 0,calc(var(--glow-size) * -.5) calc(100% + calc(var(--glow-size) * .5)),calc(100% + calc(var(--glow-size) * .5)) calc(var(--glow-size) * -.5)}to{background-position:0 0,0 0,calc(var(--glow-size) * -.5) calc(var(--glow-size) * -.5),calc(100% + calc(var(--glow-size) * .5)) calc(100% + calc(var(--glow-size) * .5))}}@keyframes swirl{0%{rotate:0deg;filter:hue-rotate(20deg)}50%{filter:hue-rotate(-30deg)}to{rotate:360deg;filter:hue-rotate(20deg)}}";
var ce = Object.defineProperty, he = Object.getOwnPropertyDescriptor, w = (e, t, n, o) => {
  for (var i = o > 1 ? void 0 : o ? he(t, n) : t, r = e.length - 1, s; r >= 0; r--)
    (s = e[r]) && (i = (o ? s(t, n, i) : s(i)) || i);
  return o && i && ce(t, n, i), i;
};
const _ = "data-drag-initial-index", M = "data-drag-final-index";
let m = class extends T {
  constructor() {
    super(...arguments), this.position = "right", this.opened = !1, this.keepOpen = !1, this.resizing = !1, this.closingForcefully = !1, this.draggingSectionPanel = null, this.activationAnimationTransitionEndListener = () => {
      this.style.removeProperty("--closing-delay"), this.style.removeProperty("--initial-position"), this.removeEventListener("transitionend", this.activationAnimationTransitionEndListener);
    }, this.resizingMouseMoveListener = (e) => {
      if (!this.resizing)
        return;
      const { x: t, y: n } = e;
      e.stopPropagation(), e.preventDefault(), requestAnimationFrame(() => {
        let o;
        if (this.position === "right") {
          const i = document.body.clientWidth - t;
          this.style.setProperty("--size", `${i}px`), S.saveDrawerSize(this.position, i), o = { width: i };
        } else if (this.position === "left") {
          const i = t;
          this.style.setProperty("--size", `${i}px`), S.saveDrawerSize(this.position, i), o = { width: i };
        } else if (this.position === "bottom") {
          const i = document.body.clientHeight - n;
          this.style.setProperty("--size", `${i}px`), S.saveDrawerSize(this.position, i), o = { height: i };
        }
        a.panels.filter((i) => !i.floating && i.panel === this.position).forEach((i) => {
          a.updatePanel(i.tag, o);
        });
      });
    }, this.sectionPanelDraggingStarted = (e, t) => {
      this.draggingSectionPanel = e, c.emit("user-select", { allowSelection: !1 }), this.draggingSectionPointerStartY = t.clientY, e.toggleAttribute("dragging", !0), e.style.zIndex = "1000", Array.from(this.querySelectorAll("copilot-section-panel-wrapper")).forEach((n, o) => {
        n.setAttribute(_, `${o}`);
      }), document.addEventListener("mousemove", this.sectionPanelDragging), document.addEventListener("mouseup", this.sectionPanelDraggingFinished);
    }, this.sectionPanelDragging = (e) => {
      if (!this.draggingSectionPanel)
        return;
      const { clientX: t, clientY: n } = e;
      if (!G(this.getBoundingClientRect(), t, n)) {
        this.cleanUpDragging();
        return;
      }
      const o = n - this.draggingSectionPointerStartY;
      this.draggingSectionPanel.style.transform = `translateY(${o}px)`, this.updateSectionPanelPositionsWhileDragging();
    }, this.sectionPanelDraggingFinished = () => {
      if (!this.draggingSectionPanel)
        return;
      c.emit("user-select", { allowSelection: !0 });
      const e = this.getAllPanels().filter(
        (t) => t.panelInfo?.panelOrder !== Number.parseInt(t.getAttribute(M), 10)
      ).map((t) => ({
        tag: t.panelTag,
        order: Number.parseInt(t.getAttribute(M), 10)
      }));
      this.cleanUpDragging(), a.updateOrders(e), document.removeEventListener("mouseup", this.sectionPanelDraggingFinished), document.removeEventListener("mousemove", this.sectionPanelDragging);
    }, this.updateSectionPanelPositionsWhileDragging = () => {
      const e = this.draggingSectionPanel.getBoundingClientRect().height;
      this.getAllPanels().sort((t, n) => {
        const o = t.getBoundingClientRect(), i = n.getBoundingClientRect(), r = (o.top + o.bottom) / 2, s = (i.top + i.bottom) / 2;
        return r - s;
      }).forEach((t, n) => {
        if (t.setAttribute(M, `${n}`), t.panelTag !== this.draggingSectionPanel?.panelTag) {
          const o = Number.parseInt(t.getAttribute(_), 10);
          o > n ? t.style.transform = `translateY(${-e}px)` : o < n ? t.style.transform = `translateY(${e}px)` : t.style.removeProperty("transform");
        }
      });
    };
  }
  static get styles() {
    return [
      N(F),
      D`
        :host {
          --size: 350px;
          --min-size: 20%;
          --max-size: 80%;
          --default-content-height: 300px;
          --transition-duration: var(--duration-2);
          --opening-delay: var(--duration-2);
          --closing-delay: var(--duration-3);
          --hover-size: 18px;
          --pulse-size: var(--hover-size);
          --pulse-animation-duration: 8s;
          --initial-position: 0px;
          position: absolute;
          z-index: var(--z-index-drawer);
          transition: translate var(--transition-duration) var(--closing-delay);
        }

        :host([no-transition]),
        :host([no-transition]) .container {
          transition: none;
          -webkit-transition: none;
          -moz-transition: none;
          -o-transition: none;
        }

        :host(:is([position='left'], [position='right'])) {
          width: var(--size);
          min-width: var(--min-size);
          max-width: var(--max-size);
          top: 0;
          bottom: 0;
        }

        :host([position='left']) {
          left: var(--initial-position);
          translate: calc(-100% + var(--hover-size)) 0%;
          padding-right: var(--hover-size);
        }

        :host([position='right']) {
          right: var(--initial-position);
          translate: calc(100% - var(--hover-size)) 0%;
          padding-left: var(--hover-size);
        }

        :host([position='bottom']) {
          height: var(--size);
          min-height: var(--min-size);
          max-height: var(--max-size);
          bottom: var(--initial-position);
          left: 0;
          right: 0;
          translate: 0% calc(100% - var(--hover-size));
          padding-top: var(--hover-size);
        }

        /* The visible container. Needed to have extra space for hover and resize handle outside it. */

        .container {
          display: flex;
          flex-direction: column;
          box-sizing: border-box;
          height: 100%;
          background: var(--surface);
          -webkit-backdrop-filter: var(--surface-backdrop-filter);
          backdrop-filter: var(--surface-backdrop-filter);
          overflow-y: auto;
          overflow-x: hidden;
          box-shadow: var(--surface-box-shadow-2);
          transition:
            opacity var(--transition-duration) var(--closing-delay),
            visibility calc(var(--transition-duration) * 2) var(--closing-delay);
          opacity: 0;
          /* For accessibility (restored when open) */
          visibility: hidden;
        }

        :host([position='left']) .container {
          border-right: 1px solid var(--surface-border-color);
        }

        :host([position='right']) .container {
          border-left: 1px solid var(--surface-border-color);
        }

        :host([position='bottom']) .container {
          border-top: 1px solid var(--surface-border-color);
        }

        /* Opened state */

        :host(:is([opened], [keepopen])) {
          translate: 0% 0%;
          transition-delay: var(--opening-delay);
          z-index: var(--z-index-opened-drawer);
        }

        :host(:is([opened], [keepopen])) .container {
          transition-delay: var(--opening-delay);
          visibility: visible;
          opacity: 1;
        }

        .resize {
          position: absolute;
          z-index: 10;
          inset: 0;
        }

        :host(:is([position='left'], [position='right'])) .resize {
          width: var(--hover-size);
          cursor: col-resize;
        }

        :host([position='left']) .resize {
          left: auto;
          right: calc(var(--hover-size) * 0.5);
        }

        :host([position='right']) .resize {
          right: auto;
          left: calc(var(--hover-size) * 0.5);
        }

        :host([position='bottom']) .resize {
          height: var(--hover-size);
          bottom: auto;
          top: calc(var(--hover-size) * 0.5);
          cursor: row-resize;
        }

        :host([resizing]) .container {
          /* vaadin-grid (used in the outline) blocks the mouse events */
          pointer-events: none;
        }

        /* Visual indication of the drawer */

        :host::before {
          content: '';
          position: absolute;
          pointer-events: none;
          z-index: -1;
          inset: var(--hover-size);
          transition: opacity var(--transition-duration) var(--closing-delay);
          animation: pulse var(--pulse-animation-duration) infinite;
        }

        :host([attention-required]) {
          --pulse-animation-duration: 2s;
          --pulse-first-color: var(--red-500);
          --pulse-second-color: var(--red-800);
        }

        :host(:is([opened], [keepopen]))::before {
          transition-delay: var(--opening-delay);
          opacity: 0;
        }
        .hasmore {
          position: absolute;
          bottom: 0;
          width: 100%;

          text-align: center;
          padding-bottom: 0.5em;
          background: linear-gradient(to bottom, rgba(0, 0, 0, 0), var(--surface-2));
          padding-top: 2em;
          display: none;
        }
        .hasmoreContainer {
          height: 100%;
          position: relative;
        }
        :host([position='left']) .hasmoreContainer[canscroll] .hasmore,
        :host([position='right']) .hasmoreContainer[canscroll] .hasmore {
          display: block;
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.reaction(
      () => a.panels,
      () => this.requestUpdate()
    ), this.reaction(
      () => l.operationInProgress,
      (t) => {
        t === X.DragAndDrop && !this.opened && !this.keepOpen ? this.style.setProperty("pointer-events", "none") : this.style.setProperty("pointer-events", "auto");
      }
    ), this.reaction(
      () => a.getAttentionRequiredPanelConfiguration(),
      () => {
        const t = a.getAttentionRequiredPanelConfiguration();
        t && !t.floating && this.toggleAttribute(z, t.panel === this.position);
      }
    ), this.reaction(
      () => l.active,
      () => {
        if (!l.active || !p.isActivationAnimation() || l.activatedFrom === "restore" || l.activatedFrom === "test")
          return;
        const t = a.getAttentionRequiredPanelConfiguration();
        t && !t.floating && t.panel === this.position || (this.addEventListener("transitionend", this.activationAnimationTransitionEndListener), this.toggleAttribute("no-transition", !0), this.opened = !0, this.style.setProperty("--closing-delay", "var(--duration-1)"), this.style.setProperty("--initial-position", "calc(-1 * (max(var(--size), var(--min-size)) * 1) / 3)"), requestAnimationFrame(() => {
          this.toggleAttribute("no-transition", !1), this.opened = !1;
        }));
      }
    ), document.addEventListener("mouseup", () => {
      this.resizing = !1, l.setDrawerResizing(!1), this.removeAttribute("resizing"), c.emit("user-select", { allowSelection: !0 });
    });
    const e = S.getDrawerSize(this.position);
    e && this.style.setProperty("--size", `${e}px`), document.addEventListener("mousemove", this.resizingMouseMoveListener), this.addEventListener("mouseenter", this.mouseEnterListener);
  }
  firstUpdated(e) {
    super.firstUpdated(e), requestAnimationFrame(() => this.toggleAttribute("no-transition", !1)), this.resizeElement.addEventListener("mousedown", (t) => {
      t.button === 0 && (this.resizing = !0, l.setDrawerResizing(!0), this.setAttribute("resizing", ""), c.emit("user-select", { allowSelection: !1 }));
    });
  }
  updated(e) {
    super.updated(e), e.has("opened") && this.opened && this.hasAttribute(z) && (this.removeAttribute(z), a.clearAttention()), this.updateScrollable();
  }
  disconnectedCallback() {
    super.disconnectedCallback(), document.removeEventListener("mousemove", this.resizingMouseMoveListener), this.removeEventListener("mouseenter", this.mouseEnterListener);
  }
  /**
   * Cleans up attributes/styles etc... for dragging operations
   * @private
   */
  cleanUpDragging() {
    this.draggingSectionPanel && (l.setSectionPanelDragging(!1), this.draggingSectionPanel.style.zIndex = "", Array.from(this.querySelectorAll("copilot-section-panel-wrapper")).forEach((e) => {
      e.style.removeProperty("transform"), e.removeAttribute(M), e.removeAttribute(_);
    }), this.draggingSectionPanel.removeAttribute("dragging"), this.draggingSectionPanel = null);
  }
  getAllPanels() {
    return Array.from(this.querySelectorAll("copilot-section-panel-wrapper"));
  }
  /**
   * Closes the drawer and disables mouse enter event for a while.
   */
  forceClose() {
    this.closingForcefully = !0, this.opened = !1, setTimeout(() => {
      this.closingForcefully = !1;
    }, 0.5);
  }
  mouseEnterListener(e) {
    if (this.closingForcefully || l.sectionPanelResizing)
      return;
    document.querySelector("copilot-main").shadowRoot.querySelector("copilot-drawer-panel[opened]") || (this.opened = !0);
  }
  render() {
    return u`
      <div class="hasmoreContainer">
        <div class="container" @scroll=${this.updateScrollable}>
          <slot></slot>
        </div>
        <div class="hasmore">⌄</div>
      </div>
      <div class="resize"></div>
    `;
  }
  updateScrollable() {
    this.hasmoreContainer.toggleAttribute(
      "canscroll",
      this.container.scrollHeight - this.container.scrollTop - this.container.clientHeight > 10
    );
  }
};
w([
  E({ reflect: !0, attribute: !0 })
], m.prototype, "position", 2);
w([
  E({ reflect: !0, type: Boolean })
], m.prototype, "opened", 2);
w([
  E({ reflect: !0, type: Boolean })
], m.prototype, "keepOpen", 2);
w([
  b(".container")
], m.prototype, "container", 2);
w([
  b(".hasmoreContainer")
], m.prototype, "hasmoreContainer", 2);
w([
  b(".resize")
], m.prototype, "resizeElement", 2);
w([
  de({ passive: !0 })
], m.prototype, "updateScrollable", 1);
m = w([
  k("copilot-drawer-panel")
], m);
var ge = Object.defineProperty, pe = Object.getOwnPropertyDescriptor, j = (e, t, n, o) => {
  for (var i = o > 1 ? void 0 : o ? pe(t, n) : t, r = e.length - 1, s; r >= 0; r--)
    (s = e[r]) && (i = (o ? s(t, n, i) : s(i)) || i);
  return o && i && ge(t, n, i), i;
};
let O = class extends Z {
  constructor() {
    super(...arguments), this.checked = !1;
  }
  static get styles() {
    return D`
      .switch {
        display: inline-flex;
        align-items: center;
        gap: var(--space-100);
      }

      .switch input {
        display: none;
      }

      .slider {
        background-color: var(--gray-300);
        border-radius: 9999px;
        cursor: pointer;
        inset: 0;
        position: absolute;
        transition: 0.4s;
        height: 0.75rem;
        position: relative;
        width: 1.5rem;
        min-width: 1.5rem;
      }

      .slider:before {
        background-color: white;
        border-radius: 50%;
        bottom: 1px;
        content: '';
        height: 0.625rem;
        left: 1px;
        position: absolute;
        transition: 0.4s;
        width: 0.625rem;
      }

      input:checked + .slider {
        background-color: var(--selection-color);
      }

      input:checked + .slider:before {
        transform: translateX(0.75rem);
      }

      label:has(input:focus) {
        outline: 2px solid var(--selection-color);
        outline-offset: 2px;
      }
    `;
  }
  render() {
    return u`
      <label class="switch">
        <input
          class="feature-toggle"
          id="feature-toggle-${this.id}"
          type="checkbox"
          ?checked="${this.checked}"
          @change=${(e) => {
      e.preventDefault(), this.checked = e.target.checked, this.dispatchEvent(new CustomEvent("on-change"));
    }} />
        <span class="slider"></span>
        ${this.title}
      </label>
    `;
  }
  //  @change=${(e: InputEvent) => this.toggleFeatureFlag(e, feature)}
};
j([
  E({ reflect: !0, type: Boolean })
], O.prototype, "checked", 2);
O = j([
  k("copilot-toggle-button")
], O);
function v(e, t) {
  const n = document.createElement(e);
  if (t.style && (n.className = t.style), t.icon) {
    const o = document.createElement("vaadin-icon");
    o.setAttribute("icon", t.icon), n.append(o);
  }
  if (t.label) {
    const o = document.createElement("span");
    o.className = "label", o.innerHTML = t.label, n.append(o);
  }
  if (t.hint) {
    const o = document.createElement("span");
    o.className = "hint", o.innerHTML = t.hint, n.append(o);
  }
  return n;
}
class ue {
  constructor() {
    this.offsetX = 0, this.offsetY = 0;
  }
  draggingStarts(t, n) {
    this.offsetX = n.clientX - t.getBoundingClientRect().left, this.offsetY = n.clientY - t.getBoundingClientRect().top;
  }
  dragging(t, n) {
    const o = n.clientX, i = n.clientY, r = o - this.offsetX, s = o - this.offsetX + t.getBoundingClientRect().width, d = i - this.offsetY, h = i - this.offsetY + t.getBoundingClientRect().height;
    return this.adjust(t, r, d, s, h);
  }
  adjust(t, n, o, i, r) {
    let s, d, h, y;
    const I = document.documentElement.getBoundingClientRect().width, L = document.documentElement.getBoundingClientRect().height;
    return (i + n) / 2 < I / 2 ? (t.style.setProperty("--left", `${n}px`), t.style.setProperty("--right", ""), y = void 0, s = Math.max(0, n)) : (t.style.removeProperty("--left"), t.style.setProperty("--right", `${I - i}px`), s = void 0, y = Math.max(0, I - i)), (o + r) / 2 < L / 2 ? (t.style.setProperty("--top", `${o}px`), t.style.setProperty("--bottom", ""), h = void 0, d = Math.max(0, o)) : (t.style.setProperty("--top", ""), t.style.setProperty("--bottom", `${L - r}px`), d = void 0, h = Math.max(0, L - r)), {
      left: s,
      right: y,
      top: d,
      bottom: h
    };
  }
  anchor(t) {
    const { left: n, top: o, bottom: i, right: r } = t.getBoundingClientRect();
    return this.adjust(t, n, o, r, i);
  }
  anchorLeftTop(t) {
    const { left: n, top: o } = t.getBoundingClientRect();
    return t.style.setProperty("--left", `${n}px`), t.style.setProperty("--right", ""), t.style.setProperty("--top", `${o}px`), t.style.setProperty("--bottom", ""), {
      left: n,
      top: o
    };
  }
}
const f = new ue();
var ve = Object.defineProperty, fe = Object.getOwnPropertyDescriptor, q = (e, t, n, o) => {
  for (var i = o > 1 ? void 0 : o ? fe(t, n) : t, r = e.length - 1, s; r >= 0; r--)
    (s = e[r]) && (i = (o ? s(t, n, i) : s(i)) || i);
  return o && i && ve(t, n, i), i;
};
const me = 8;
let $ = class extends T {
  constructor() {
    super(...arguments), this.initialMouseDownPosition = null, this.dragging = !1, this.mouseDownListener = (e) => {
      this.initialMouseDownPosition = { x: e.clientX, y: e.clientY }, f.draggingStarts(this, e), document.addEventListener("mousemove", this.documentDraggingMouseMoveEventListener);
    }, this.documentDraggingMouseMoveEventListener = (e) => {
      if (this.initialMouseDownPosition && !this.dragging) {
        const { clientX: t, clientY: n } = e;
        this.dragging = Math.abs(t - this.initialMouseDownPosition.x) + Math.abs(n - this.initialMouseDownPosition.y) > me;
      }
      this.dragging && (this.setOverlayVisibility(!1), f.dragging(this, e));
    }, this.documentMouseUpListener = (e) => {
      if (this.dragging) {
        const t = f.dragging(this, e);
        p.setActivationButtonPosition(t), this.setOverlayVisibility(!0);
      }
      this.dragging = !1, this.initialMouseDownPosition = null, document.removeEventListener("mousemove", this.documentDraggingMouseMoveEventListener), this.setMenuBarOnClick();
    }, this.dispatchSpotlightActivationEvent = (e) => {
      this.dispatchEvent(
        new CustomEvent("spotlight-activation-changed", {
          detail: e
        })
      );
    }, this.activationBtnClicked = (e) => {
      if (this.dragging) {
        e?.stopPropagation(), this.dragging = !1;
        return;
      }
      if (l.active && this.handleAttentionRequiredOnClick()) {
        e?.stopPropagation(), e?.preventDefault();
        return;
      }
      e?.stopPropagation(), this.dispatchEvent(new CustomEvent("activation-btn-clicked"));
    }, this.handleAttentionRequiredOnClick = () => {
      const e = a.getAttentionRequiredPanelConfiguration();
      return e ? e.panel && !e.floating ? (c.emit("open-attention-required-drawer", null), !0) : (a.clearAttention(), !0) : !1;
    }, this.setMenuBarOnClick = () => {
      const e = this.shadowRoot.querySelector("vaadin-menu-bar-button");
      e && (e.onclick = this.activationBtnClicked);
    };
  }
  static get styles() {
    return [
      N(F),
      D`
        :host {
          --space: 8px;
          --height: 28px;
          --width: 28px;
          position: absolute;
          top: clamp(var(--space), var(--top), calc(100vh - var(--height) - var(--space)));
          left: clamp(var(--space), var(--left), calc(100vw - var(--width) - var(--space)));
          bottom: clamp(var(--space), var(--bottom), calc(100vh - var(--height) - var(--space)));
          right: clamp(var(--space), var(--right), calc(100vw - var(--width) - var(--space)));
          user-select: none;
          -ms-user-select: none;
          -moz-user-select: none;
          -webkit-user-select: none;
          /* Don't add a z-index or anything else that creates a stacking context */
        }

        .menu-button::part(container) {
          overflow: visible;
        }

        .menu-button vaadin-menu-bar-button {
          all: initial;
          display: block;
          position: relative;
          z-index: var(--z-index-activation-button);
          width: var(--width);
          height: var(--height);
          overflow: hidden;
          color: transparent;
          background: hsl(0 0% 0% / 0.25);
          border-radius: 8px;
          box-shadow: 0 0 0 1px hsl(0 0% 100% / 0.1);
          cursor: default;
          -webkit-backdrop-filter: blur(8px);
          backdrop-filter: blur(8px);
          transition:
            box-shadow 0.2s,
            background-color 0.2s;
        }

        /* pointer-events property is set when the menu is open */

        .menu-button[style*='pointer-events'] + .monkey-patch-close-on-hover {
          position: fixed; /* escapes the host positioning context */
          inset: 0;
          bottom: 40px;
          z-index: calc(var(--z-index-popover) - 1);
          pointer-events: auto;
        }

        /* visual effect when active */

        .menu-button vaadin-menu-bar-button::before {
          all: initial;
          content: '';
          position: absolute;
          inset: -6px;
          background-image: radial-gradient(circle at 50% -10%, hsl(221 100% 55% / 0.6) 0%, transparent 60%),
            radial-gradient(circle at 25% 40%, hsl(303 71% 64%) 0%, transparent 70%),
            radial-gradient(circle at 80% 10%, hsla(262, 38%, 9%, 0.5) 0%, transparent 80%),
            radial-gradient(circle at 110% 50%, hsla(147, 100%, 77%, 1) 20%, transparent 100%);
          animation: 5s swirl linear infinite;
          animation-play-state: paused;
          opacity: 0;
          transition: opacity 0.5s;
        }

        /* vaadin symbol */

        .menu-button vaadin-menu-bar-button::after {
          all: initial;
          content: '';
          position: absolute;
          inset: 1px;
          background: url('data:image/svg+xml;utf8,<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12.7407 9.70401C12.7407 9.74417 12.7378 9.77811 12.7335 9.81479C12.7111 10.207 12.3897 10.5195 11.9955 10.5195C11.6014 10.5195 11.2801 10.209 11.2577 9.8169C11.2534 9.7801 11.2504 9.74417 11.2504 9.70401C11.2504 9.31225 11.1572 8.90867 10.2102 8.90867H7.04307C5.61481 8.90867 5 8.22698 5 6.86345V5.70358C5 5.31505 5.29521 5 5.68008 5C6.06495 5 6.35683 5.31505 6.35683 5.70358V6.09547C6.35683 6.53423 6.655 6.85413 7.307 6.85413H10.4119C11.8248 6.85413 11.9334 7.91255 11.98 8.4729H12.0111C12.0577 7.91255 12.1663 6.85413 13.5791 6.85413H16.6841C17.3361 6.85413 17.614 6.54529 17.614 6.10641L17.6158 5.70358C17.6158 5.31505 17.9246 5 18.3095 5C18.6943 5 19 5.31505 19 5.70358V6.86345C19 8.22698 18.3763 8.90867 16.9481 8.90867H13.7809C12.8338 8.90867 12.7407 9.31225 12.7407 9.70401Z" fill="white"/><path d="M12.7536 17.7785C12.6267 18.0629 12.3469 18.2608 12.0211 18.2608C11.6907 18.2608 11.4072 18.0575 11.2831 17.7668C11.2817 17.7643 11.2803 17.7619 11.279 17.7595C11.2761 17.7544 11.2732 17.7495 11.2704 17.744L8.45986 12.4362C8.3821 12.2973 8.34106 12.1399 8.34106 11.9807C8.34106 11.4732 8.74546 11.0603 9.24238 11.0603C9.64162 11.0603 9.91294 11.2597 10.0985 11.6922L12.0216 15.3527L13.9468 11.6878C14.1301 11.2597 14.4014 11.0603 14.8008 11.0603C15.2978 11.0603 15.7021 11.4732 15.7021 11.9807C15.7021 12.1399 15.6611 12.2973 15.5826 12.4374L12.7724 17.7446C12.7683 17.7524 12.7642 17.7597 12.7601 17.767C12.7579 17.7708 12.7557 17.7746 12.7536 17.7785Z" fill="white"/></svg>');
          background-size: 100%;
        }

        .menu-button vaadin-menu-bar-button[focus-ring] {
          outline: 2px solid var(--selection-color);
          outline-offset: 2px;
        }

        .menu-button vaadin-menu-bar-button:hover {
          background: hsl(0 0% 0% / 0.8);
          box-shadow:
            0 0 0 1px hsl(0 0% 100% / 0.1),
            0 2px 8px -1px hsl(0 0% 0% / 0.3);
        }

        :host([active]) .menu-button vaadin-menu-bar-button {
          background-color: transparent;
          box-shadow:
            inset 0 0 0 1px hsl(0 0% 0% / 0.2),
            0 2px 8px -1px hsl(0 0% 0% / 0.3);
        }

        :host([active]) .menu-button vaadin-menu-bar-button::before {
          opacity: 1;
          animation-play-state: running;
        }

        :host([attention-required]) {
          animation: bounce 0.5s;
          animation-iteration-count: 2;
        }

        :host([attention-required]) [part='attention-required-indicator'] {
          top: -1px;
          right: -1px;
          width: 6px;
          height: 6px;
          box-sizing: border-box;
          border-radius: 100%;
          position: absolute;
          background: var(--red-500);
          z-index: calc(var(--z-index-activation-button) + 1);
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.reaction(
      () => a.attentionRequiredPanelTag,
      () => {
        this.toggleAttribute(z, a.attentionRequiredPanelTag !== null);
      }
    ), this.reaction(
      () => l.active,
      () => {
        this.toggleAttribute("active", l.active);
      },
      { fireImmediately: !0 }
    ), this.addEventListener("mousedown", this.mouseDownListener), document.addEventListener("mouseup", this.documentMouseUpListener);
    const e = p.getActivationButtonPosition();
    e ? (this.style.setProperty("--left", `${e.left}px`), this.style.setProperty("--bottom", `${e.bottom}px`), this.style.setProperty("--right", `${e.right}px`), this.style.setProperty("--top", `${e.top}px`)) : (this.style.setProperty("--bottom", "var(--space)"), this.style.setProperty("--right", "var(--space)"));
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removeEventListener("mousedown", this.mouseDownListener), document.removeEventListener("mouseup", this.documentMouseUpListener);
  }
  /**
   * To hide overlay while dragging
   * @param visible
   */
  setOverlayVisibility(e) {
    const t = this.shadowRoot.querySelector("vaadin-menu-bar-button").__overlay;
    e ? (t?.style.setProperty("display", "flex"), t?.style.setProperty("visibility", "visible")) : (t?.style.setProperty("display", "none"), t?.style.setProperty("visibility", "invisible"));
  }
  render() {
    const e = [
      {
        text: "Vaadin Copilot",
        children: [
          {
            component: v("vaadin-menu-bar-item", {
              label: '<span class="deactivate">Deactivate</span><span class="activate">Activate</span> Copilot',
              hint: p.isActivationShortcut() ? R.toggleCopilot : void 0
            }),
            action: "copilot"
          },
          {
            component: v("vaadin-menu-bar-item", {
              label: "Toggle Command Window",
              hint: R.toggleCommandWindow,
              style: "toggle-spotlight"
            }),
            action: "spotlight"
          },
          { component: "hr" },
          // Settings sub menu
          {
            text: "Settings",
            children: [
              {
                component: v("vaadin-menu-bar-item", {
                  label: "Activation shortcut enabled",
                  hint: p.isActivationShortcut() ? "✓" : void 0
                }),
                action: "shortcut"
              },
              {
                component: v("vaadin-menu-bar-item", {
                  label: "Show animation when activating",
                  hint: p.isActivationAnimation() ? "✓" : void 0
                }),
                action: "animate-on-activate"
              }
            ]
          }
        ]
      }
    ];
    return l.active && (l.idePluginState?.supportedActions?.find((t) => t === "undo") && (e[0].children = [
      {
        component: v("vaadin-menu-bar-item", {
          label: "Undo",
          hint: R.undo
        }),
        action: "undo"
      },
      {
        component: v("vaadin-menu-bar-item", {
          label: "Redo",
          hint: R.redo
        }),
        action: "redo"
      },
      ...e[0].children
    ]), e[0].children = [
      {
        component: v("vaadin-menu-bar-item", {
          label: "Tell us what you think"
          // Label used also in ScreenshotsIT.java
        }),
        action: "feedback"
      },
      {
        component: v("vaadin-menu-bar-item", {
          label: "Show welcome message"
        }),
        action: "welcome"
      },
      {
        component: v("vaadin-menu-bar-item", {
          label: "Show keyboard shortcuts"
        }),
        action: "shortcuts"
      },
      {
        component: "hr"
      },
      ...e[0].children
    ]), u`
      <vaadin-menu-bar
        class="menu-button"
        .items="${e}"
        @item-selected="${(t) => {
      this.handleMenuItemClick(t.detail.value);
    }}"
        ?open-on-hover=${!this.dragging}
        overlay-class="activation-button-menu">
      </vaadin-menu-bar>
      <div class="monkey-patch-close-on-hover" @mouseenter="${this.closeMenu}"></div>
      <div part="attention-required-indicator"></div>
    `;
  }
  closeMenu() {
    this.menubar._close();
  }
  handleMenuItemClick(e) {
    switch (e.action) {
      case "copilot":
        this.activationBtnClicked();
        break;
      case "spotlight":
        l.setSpotlightActive(!l.spotlightActive);
        break;
      case "shortcut":
        p.setActivationShortcut(!p.isActivationShortcut());
        break;
      case "animate-on-activate":
        p.setActivationAnimation(!p.isActivationAnimation());
        break;
      case "undo":
      case "redo":
        c.emit("undoRedo", { undo: e.action === "undo" });
        break;
      case "feedback":
        a.updatePanel("copilot-feedback-panel", {
          floating: !0
        });
        break;
      case "welcome":
        l.setWelcomeActive(!0), l.setSpotlightActive(!0);
        break;
      case "shortcuts":
        a.updatePanel("copilot-shortcuts-panel", {
          floating: !0
        });
        break;
    }
  }
  firstUpdated() {
    this.setMenuBarOnClick(), ne(this.shadowRoot);
  }
};
q([
  b("vaadin-menu-bar")
], $.prototype, "menubar", 2);
q([
  oe()
], $.prototype, "dragging", 2);
$ = q([
  k("copilot-activation-button")
], $);
var be = Object.defineProperty, we = Object.getOwnPropertyDescriptor, C = (e, t, n, o) => {
  for (var i = o > 1 ? void 0 : o ? we(t, n) : t, r = e.length - 1, s; r >= 0; r--)
    (s = e[r]) && (i = (o ? s(t, n, i) : s(i)) || i);
  return o && i && be(t, n, i), i;
};
const g = "resize-dir", B = "floating-resizing-active";
let P = class extends T {
  constructor() {
    super(...arguments), this.panelTag = "", this.floatingResizingStarted = !1, this.resizingInDrawerStarted = !1, this.toggling = !1, this.rectangleBeforeResizing = null, this.floatingResizeHandlerMouseMoveListener = (e) => {
      if (!this.panelInfo?.floating || this.floatingResizingStarted || !this.panelInfo?.expanded)
        return;
      const t = this.getBoundingClientRect(), n = Math.abs(e.clientX - t.x), o = Math.abs(t.x + t.width - e.clientX), i = Math.abs(e.clientY - t.y), r = Math.abs(t.y + t.height - e.clientY), s = Number.parseInt(
        window.getComputedStyle(this).getPropertyValue("--floating-offset-resize-threshold"),
        10
      );
      let d = "";
      if (n < s ? i < s ? (d = "nw-resize", this.setAttribute(g, "top left")) : r < s ? (d = "sw-resize", this.setAttribute(g, "bottom left")) : (d = "col-resize", this.setAttribute(g, "left")) : o < s ? i < s ? (d = "ne-resize", this.setAttribute(g, "top right")) : r < s ? (d = "se-resize", this.setAttribute(g, "bottom right")) : (d = "col-resize", this.setAttribute(g, "right")) : r < s ? (d = "row-resize", this.setAttribute(g, "bottom")) : i < s && (d = "row-resize", this.setAttribute(g, "top")), d !== "") {
        const h = window.getComputedStyle(this), y = Number.parseInt(h.borderTopWidth, 10), I = Number.parseInt(h.borderTopWidth, 10), L = Number.parseInt(h.borderLeftWidth, 10), W = Number.parseInt(h.borderRightWidth, 10);
        this.rectangleBeforeResizing = this.getBoundingClientRect(), this.rectangleBeforeResizing.width -= L + W, this.rectangleBeforeResizing.height -= y + I, this.style.setProperty("--resize-cursor", d);
      } else
        this.style.removeProperty("--resize-cursor"), this.removeAttribute(g);
      this.toggleAttribute(B, d !== "");
    }, this.floatingResizingMouseDownListener = (e) => {
      this.hasAttribute(B) && (e.stopPropagation(), e.preventDefault(), f.anchorLeftTop(this), this.floatingResizingStarted = !0, this.toggleAttribute("resizing", !0), U(() => {
        l.sectionPanelResizing = !0;
      }));
    }, this.floatingResizingMouseLeaveListener = () => {
      this.panelInfo?.floating && (this.floatingResizingStarted || (this.removeAttribute("resizing"), this.removeAttribute(B), this.removeAttribute("dragging"), this.style.removeProperty("--resize-cursor"), this.removeAttribute(g)));
    }, this.floatingResizingMouseMoveListener = (e) => {
      if (!this.panelInfo?.floating || !this.floatingResizingStarted)
        return;
      const t = this.getAttribute(g);
      if (t === null)
        return;
      e.stopPropagation(), e.preventDefault();
      const { clientX: n, clientY: o } = e, i = t.split(" "), r = this.rectangleBeforeResizing;
      if (i.includes("left")) {
        const s = Math.max(0, n);
        this.setFloatingResizeDirectionProps("left", s, r.left - s + r.width);
      }
      if (i.includes("right")) {
        const s = Math.max(0, n);
        this.setFloatingResizeDirectionProps("right", s, s - r.right + r.width);
      }
      if (i.includes("top")) {
        const s = Math.max(0, o), d = r.top - s + r.height;
        this.setFloatingResizeDirectionProps("top", s, void 0, d);
      }
      if (i.includes("bottom")) {
        const s = Math.max(0, o), d = s - r.bottom + r.height;
        this.setFloatingResizeDirectionProps("bottom", s, void 0, d);
      }
    }, this.setFloatingResizeDirectionProps = (e, t, n, o) => {
      n && n > Number.parseFloat(window.getComputedStyle(this).getPropertyValue("--min-width")) && (this.style.setProperty(`--${e}`, `${t}px`), this.style.setProperty("width", `${n}px`));
      const i = window.getComputedStyle(this), r = Number.parseFloat(i.getPropertyValue("--header-height")), s = Number.parseFloat(i.getPropertyValue("--floating-offset-resize-threshold")) / 2;
      o && o > r + s && (this.style.setProperty(`--${e}`, `${t}px`), this.style.setProperty("height", `${o}px`), this.container.style.setProperty("margin-top", "calc(var(--floating-offset-resize-threshold) / 4)"), this.container.style.height = `calc(${o}px - var(--floating-offset-resize-threshold) / 2)`);
    }, this.floatingResizingMouseUpListener = (e) => {
      if (!this.floatingResizingStarted || !this.panelInfo?.floating)
        return;
      e.stopPropagation(), e.preventDefault(), this.floatingResizingStarted = !1, U(() => {
        l.sectionPanelResizing = !1;
      });
      const { width: t, height: n } = this.getBoundingClientRect(), { left: o, top: i, bottom: r, right: s } = f.anchor(this), d = window.getComputedStyle(this.container), h = Number.parseInt(d.borderTopWidth, 10), y = Number.parseInt(d.borderTopWidth, 10);
      a.updatePanel(this.panelInfo.tag, {
        width: t,
        height: n - (h + y),
        floatingPosition: {
          ...this.panelInfo.floatingPosition,
          left: o,
          top: i,
          bottom: r,
          right: s
        }
      }), this.style.removeProperty("width"), this.style.removeProperty("height"), this.container.style.removeProperty("height"), this.container.style.removeProperty("margin-top"), this.setCssSizePositionProperties(), this.toggleAttribute("dragging", !1);
    }, this.transitionEndEventListener = () => {
      this.toggling && (this.toggling = !1, f.anchor(this));
    }, this.resizeInDrawerMouseDownListener = (e) => {
      e.button === 0 && (this.resizingInDrawerStarted = !0, this.setAttribute("resizing", ""), c.emit("user-select", { allowSelection: !1 }));
    }, this.resizeInDrawerMouseMoveListener = (e) => {
      if (!this.resizingInDrawerStarted)
        return;
      const { y: t } = e;
      e.stopPropagation(), e.preventDefault();
      const n = t - this.getBoundingClientRect().top;
      this.style.setProperty("--section-height", `${n}px`), a.updatePanel(this.panelInfo.tag, {
        height: n
      });
    }, this.resizeInDrawerMouseUpListener = () => {
      this.resizingInDrawerStarted && (this.panelInfo?.floating || (this.resizingInDrawerStarted = !1, this.removeAttribute("resizing"), c.emit("user-select", { allowSelection: !0 }), this.style.setProperty("--section-height", `${this.getBoundingClientRect().height}px`)));
    }, this.sectionPanelMouseEnterListener = () => {
      this.hasAttribute(z) && (this.removeAttribute(z), a.clearAttention());
    }, this.contentAreaMouseDownListener = () => {
      a.addFocusedFloatingPanel(this.panelInfo);
    }, this.documentMouseUpEventListener = () => {
      document.removeEventListener("mousemove", this.draggingEventListener), this.panelInfo?.floating && (this.toggleAttribute("dragging", !1), l.setSectionPanelDragging(!1));
    }, this.panelHeaderMouseDownEventListener = (e) => {
      e.button === 0 && (e.target instanceof HTMLButtonElement && e.target.getAttribute("part") === "title-button" || (a.addFocusedFloatingPanel(this.panelInfo), !this.hasAttribute(g) && (f.draggingStarts(this, e), document.addEventListener("mousemove", this.draggingEventListener), l.setSectionPanelDragging(!0), this.panelInfo?.floating ? this.toggleAttribute("dragging", !0) : this.parentElement.sectionPanelDraggingStarted(this, e), e.preventDefault(), e.stopPropagation())));
    }, this.draggingEventListener = (e) => {
      const t = f.dragging(this, e);
      if (this.panelInfo?.floating && this.panelInfo?.floatingPosition) {
        e.preventDefault();
        const { left: n, top: o, bottom: i, right: r } = t;
        a.updatePanel(this.panelInfo.tag, {
          floatingPosition: {
            ...this.panelInfo.floatingPosition,
            left: n,
            top: o,
            bottom: i,
            right: r
          }
        });
      }
    }, this.setCssSizePositionProperties = () => {
      const e = a.getPanelByTag(this.panelTag);
      if (e && (e.height !== void 0 && (this.panelInfo?.floating || e.panel === "left" || e.panel === "right" ? this.style.setProperty("--section-height", `${e.height}px`) : this.style.removeProperty("--section-height")), e.width !== void 0 && (e.floating || e.panel === "bottom" ? this.style.setProperty("--section-width", `${e.width}px`) : this.style.removeProperty("--section-width")), e.floating && e.floatingPosition && !this.toggling)) {
        const { left: t, top: n, bottom: o, right: i } = e.floatingPosition;
        this.style.setProperty("--left", t !== void 0 ? `${t}px` : "auto"), this.style.setProperty("--top", n !== void 0 ? `${n}px` : "auto"), this.style.setProperty("--bottom", o !== void 0 ? `${o}px` : ""), this.style.setProperty("--right", i !== void 0 ? `${i}px` : "");
      }
    }, this.renderPopupButton = () => {
      if (!this.panelInfo)
        return A;
      let e;
      return this.panelInfo.panel === void 0 ? e = "Close the popup" : e = this.panelInfo.floating ? `Dock ${this.panelInfo.header} to ${this.panelInfo.panel}` : `Open ${this.panelInfo.header} as a popup`, u`
      <button
        part="popup-button"
        @click="${(t) => this.changePanelFloating(t)}"
        @mousedown="${(t) => t.stopPropagation()}"
        title="${e}"
        aria-label=${e}>
        ${this.getPopupButtonIcon()}
      </button>
    `;
    }, this.changePanelFloating = (e) => {
      if (this.panelInfo)
        if (e.stopPropagation(), K(this), this.panelInfo?.floating)
          a.updatePanel(this.panelInfo?.tag, { floating: !1 });
        else {
          let t;
          if (this.panelInfo.floatingPosition)
            t = this.panelInfo?.floatingPosition;
          else {
            const { left: i, top: r } = this.getBoundingClientRect();
            t = {
              left: i,
              top: r
            };
          }
          let n = this.panelInfo?.height;
          n === void 0 && this.panelInfo.expanded && (n = Number.parseInt(window.getComputedStyle(this).height, 10)), this.parentElement.forceClose(), a.updatePanel(this.panelInfo?.tag, {
            floating: !0,
            width: this.panelInfo?.width || Number.parseInt(window.getComputedStyle(this).width, 10),
            height: n,
            floatingPosition: t
          }), a.addFocusedFloatingPanel(this.panelInfo);
        }
    }, this.toggleExpand = (e) => {
      this.panelInfo && (e.stopPropagation(), f.anchorLeftTop(this), a.updatePanel(this.panelInfo.tag, {
        expanded: !this.panelInfo.expanded
      }), this.toggling = !0, this.toggleAttribute("expanded", this.panelInfo.expanded));
    };
  }
  static get styles() {
    return [
      N(F),
      D`
        * {
          box-sizing: border-box;
        }

        :host {
          flex: none;
          display: grid;
          align-content: start;
          grid-template-rows: auto 1fr;
          transition: grid-template-rows var(--duration-2);
          overflow: hidden;
          position: relative;
          --min-width: 160px;
          --resize-div-size: 10px;
          --header-height: 37px;
          --content-height: calc(var(--section-height) - var(--header-height));
          --content-width: var(--content-width, 100%);
          --floating-border-width: 1px;
          --floating-offset-resize-threshold: 16px;
          cursor: var(--cursor, var(--resize-cursor, default));
        }

        :host(:not([expanded])) {
          grid-template-rows: auto 0fr;
          --content-height: 0px !important;
        }

        [part='header'] {
          align-items: center;
          color: var(--color-high-contrast);
          display: flex;
          flex: none;
          font: var(--font-small-bold);
          justify-content: space-between;
          min-width: 100%;
          user-select: none;
          -webkit-user-select: none;
          width: var(--min-width);
          height: var(--header-height);
        }

        :host([floating]:not([expanded])) [part='header'] {
          --min-width: unset;
        }

        [part='header'] {
          border-bottom: 1px solid var(--border-color);
        }

        :host([floating]) [part='header'] {
          transition: border-color var(--duration-2);
        }

        :host([floating]:not([expanded])) [part='header'] {
          border-color: transparent;
        }

        [part='title'] {
          flex: auto;
          margin: 0;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        [part='content'] {
          height: var(--content-height);
          overflow: auto;
          transition:
            height var(--duration-2),
            width var(--duration-2),
            opacity var(--duration-2),
            visibility calc(var(--duration-2) * 2);
        }

        [part='drawer-resize'] {
          resize: vertical;
          cursor: row-resize;
          position: absolute;
          bottom: -5px;
          left: 0;
          width: 100%;
          height: 10px;
        }

        :host([floating]) [part='drawer-resize'] {
          display: none;
        }

        :host(:not([expanded])) [part='drawer-resize'] {
          display: none;
        }

        :host(:not([floating]):not(:last-child)) {
          border-bottom: 1px solid var(--border-color);
        }

        :host(:not([expanded])) [part='content'] {
          opacity: 0;
          visibility: hidden;
        }

        :host([floating]:not([expanded])) [part='content'] {
          width: 0;
          height: 0;
        }

        :host(:not([expanded])) [part='content'][style*='height'] {
          height: 0 !important;
        }

        :host(:not([expanded])) [part='content'][style*='width'] {
          width: 0 !important;
        }

        :host([floating]) {
          position: fixed;
          min-width: 0;
          min-height: 0;
          z-index: calc(var(--z-index-floating-panel) + var(--z-index-focus, 0));
          top: clamp(0px, var(--top), calc(100vh - var(--section-height) * 0.5));
          left: clamp(calc(var(--section-width) * -0.5), var(--left), calc(100vw - var(--section-width) * 0.5));
          bottom: clamp(calc(var(--section-height) * -0.5), var(--bottom), calc(100vh - var(--section-height) * 0.5));
          right: clamp(calc(var(--section-width) * -0.5), var(--right), calc(100vw - var(--section-width) * 0.5));
          width: var(--section-width);
          overflow: visible;
        }
        :host([floating]) [part='container'] {
          background: var(--surface);
          border: var(--floating-border-width) solid var(--surface-border-color);
          -webkit-backdrop-filter: var(--surface-backdrop-filter);
          backdrop-filter: var(--surface-backdrop-filter);
          border-radius: var(--radius-2);
          margin: auto;
          height: calc(100% - var(--floating-offset-resize-threshold) / 2);
          width: calc(100% - var(--floating-offset-resize-threshold) / 2);
          box-shadow: var(--surface-box-shadow-2);
          overflow: hidden;
        }

        :host([floating]:not([expanded])) {
          width: unset;
        }

        :host([floating]) .drag-handle {
          cursor: var(--resize-cursor, move);
        }

        :host([floating][expanded]) [part='content'] {
          min-width: var(--min-width);
          min-height: 0;
          max-height: 85vh;
          max-width: 90vw;
          width: var(--content-width);
        }

        /* :hover for Firefox, :active for others */

        :host([floating][expanded]) [part='content']:is(:hover, :active) {
          transition: none;
        }

        [part='header'] button {
          align-items: center;
          appearance: none;
          background: transparent;
          border: 0px;
          border-radius: var(--radius-1);
          color: var(--color);
          display: flex;
          flex: 0 0 auto;
          height: 2.25rem;
          justify-content: center;
          padding: 0px;
          width: 16px;
          margin-left: 10px;
          margin-right: 10px;
        }

        div.actions {
          width: auto;
        }

        :host(:not([expanded])) div.actions {
          display: none;
        }

        [part='title'] button {
          color: var(--color-high-contrast);
          font: var(--font-xsmall-strong);
          width: auto;
        }

        [part='header'] button:hover {
          color: var(--color-high-contrast);
        }

        [part='header'] button:focus-visible {
          outline: 2px solid var(--blue-500);
          outline-offset: -2px;
        }

        [part='header'] button svg {
          display: block;
        }

        [part='header'] .actions:empty {
          display: none;
        }

        ::slotted(*) {
          box-sizing: border-box;
          display: block;
          height: var(--content-height, var(--default-content-height, 100%));
          /* padding: var(--space-150); */
          width: 100%;
        }

        :host(:not([floating])) ::slotted(*) {
          /* padding-top: var(--space-50); */
        }

        :host([dragging]) {
          opacity: 0.4;
        }

        :host([dragging]) [part='content'] {
          pointer-events: none;
        }

        :host([attention-required]) {
          --pulse-animation-duration: 2s;
          --pulse-first-color: var(--red-500);
          --pulse-second-color: var(--red-800);
          --pulse-size: 12px;
          animation: pulse 2s infinite;
        }

        :host([resizing]),
        :host([resizing]) [part='content'] {
          transition: none;
        }

        :host([hiding-while-drag-and-drop]) {
          display: none;
        }

        // dragging in drawer

        :host(:not([floating])) .drag-handle {
          cursor: grab;
        }

        :host(:not([floating])[dragging]) .drag-handle {
          cursor: grabbing;
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.setAttribute("role", "region"), this.reaction(
      () => a.getAttentionRequiredPanelConfiguration(),
      () => {
        const e = a.getAttentionRequiredPanelConfiguration();
        this.toggleAttribute(z, e?.tag === this.panelTag && e?.floating);
      }
    ), this.addEventListener("mouseenter", this.sectionPanelMouseEnterListener), document.addEventListener("mousemove", this.resizeInDrawerMouseMoveListener), document.addEventListener("mouseup", this.resizeInDrawerMouseUpListener), this.reaction(
      () => l.operationInProgress,
      () => {
        requestAnimationFrame(() => {
          this.toggleAttribute(
            "hiding-while-drag-and-drop",
            l.operationInProgress === X.DragAndDrop && this.panelInfo?.floating && !this.panelInfo.showWhileDragging
          );
        });
      }
    ), this.reaction(
      () => a.floatingPanelsZIndexOrder,
      () => {
        this.style.setProperty("--z-index-focus", `${a.getFloatingPanelZIndex(this.panelTag)}`);
      },
      { fireImmediately: !0 }
    ), this.addEventListener("transitionend", this.transitionEndEventListener), this.addEventListener("mousemove", this.floatingResizeHandlerMouseMoveListener), this.addEventListener("mousedown", this.floatingResizingMouseDownListener), this.addEventListener("mouseleave", this.floatingResizingMouseLeaveListener), document.addEventListener("mousemove", this.floatingResizingMouseMoveListener), document.addEventListener("mouseup", this.floatingResizingMouseUpListener);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removeEventListener("mouseenter", this.sectionPanelMouseEnterListener), this.drawerResizeElement.removeEventListener("mousedown", this.resizeInDrawerMouseDownListener), document.removeEventListener("mousemove", this.resizeInDrawerMouseMoveListener), document.removeEventListener("mouseup", this.resizeInDrawerMouseUpListener), this.removeEventListener("mousemove", this.floatingResizeHandlerMouseMoveListener), this.removeEventListener("mousedown", this.floatingResizingMouseDownListener), document.removeEventListener("mousemove", this.floatingResizingMouseMoveListener), document.removeEventListener("mouseup", this.floatingResizingMouseUpListener);
  }
  willUpdate(e) {
    super.willUpdate(e), e.has("panelTag") && (this.panelInfo = a.getPanelByTag(this.panelTag), this.setAttribute("aria-labelledby", this.panelInfo.tag.concat("-title"))), this.toggleAttribute("floating", this.panelInfo?.floating);
  }
  updated(e) {
    super.updated(e), this.setCssSizePositionProperties();
  }
  firstUpdated(e) {
    super.firstUpdated(e), document.addEventListener("mouseup", this.documentMouseUpEventListener), this.headerDraggableArea.addEventListener("mousedown", this.panelHeaderMouseDownEventListener), this.toggleAttribute("expanded", this.panelInfo?.expanded), J(this), this.setCssSizePositionProperties(), this.contentArea.addEventListener("mousedown", this.contentAreaMouseDownListener), this.drawerResizeElement.addEventListener("mousedown", this.resizeInDrawerMouseDownListener);
  }
  render() {
    return this.panelInfo ? u`
      <div part="container">
        <div part="header" class="drag-handle">
          ${this.panelInfo.expandable !== !1 ? u` <button
                part="toggle-button"
                @mousedown="${(e) => e.stopPropagation()}"
                @click="${(e) => this.toggleExpand(e)}"
                aria-expanded="${this.panelInfo.expanded}"
                aria-controls="content"
                aria-label="Expand ${this.panelInfo.header}">
                ${this.panelInfo.expanded ? x.chevronDown : x.chevronRight}
              </button>` : A}
          <h2 id="${this.panelInfo.tag}-title" part="title">
            <button part="title-button" @dblclick="${(e) => this.toggleExpand(e)}">
              ${this.panelInfo.header}
            </button>
          </h2>
          <div class="actions" @mousedown="${(e) => e.stopPropagation()}">${this.renderActions()}</div>
          ${this.renderPopupButton()}
        </div>
        <div part="content" id="content">
          <slot name="content"></slot>
        </div>
        <div part="drawer-resize"></div>
      </div>
    ` : A;
  }
  getPopupButtonIcon() {
    return this.panelInfo ? this.panelInfo.panel === void 0 ? x.close : this.panelInfo.floating ? this.panelInfo.panel === "bottom" ? x.dockBottom : this.panelInfo.panel === "left" ? x.dockLeft : this.panelInfo.panel === "right" ? x.dockRight : A : x.popup : A;
  }
  renderActions() {
    if (!this.panelInfo?.actionsTag)
      return A;
    const e = this.panelInfo.actionsTag;
    return se(`<${e}></${e}>`);
  }
};
C([
  E()
], P.prototype, "panelTag", 2);
C([
  b(".drag-handle")
], P.prototype, "headerDraggableArea", 2);
C([
  b("#content")
], P.prototype, "contentArea", 2);
C([
  b('[part="drawer-resize"]')
], P.prototype, "drawerResizeElement", 2);
C([
  b('[part="container"]')
], P.prototype, "container", 2);
P = C([
  k("copilot-section-panel-wrapper")
], P);
c.on("undoRedo", (e) => {
  const t = e.detail.files ?? re();
  e.detail.undo ? c.send("copilot-plugin-undo", { files: t }) : c.send("copilot-plugin-redo", { files: t });
});
var ye = Object.defineProperty, xe = Object.getOwnPropertyDescriptor, ze = (e, t, n, o) => {
  for (var i = o > 1 ? void 0 : o ? xe(t, n) : t, r = e.length - 1, s; r >= 0; r--)
    (s = e[r]) && (i = (o ? s(t, n, i) : s(i)) || i);
  return o && i && ye(t, n, i), i;
};
let Y = class extends T {
  static get styles() {
    return D`
      :host {
        position: fixed;
        bottom: 2.5rem;
        right: 0rem;
        visibility: visible; /* Always show, even if copilot is off */
        user-select: none;
        z-index: 10000;

        --dev-tools-text-color: rgba(255, 255, 255, 0.8);

        --dev-tools-text-color-secondary: rgba(255, 255, 255, 0.65);
        --dev-tools-text-color-emphasis: rgba(255, 255, 255, 0.95);
        --dev-tools-text-color-active: rgba(255, 255, 255, 1);

        --dev-tools-background-color-inactive: rgba(45, 45, 45, 0.25);
        --dev-tools-background-color-active: rgba(45, 45, 45, 0.98);
        --dev-tools-background-color-active-blurred: rgba(45, 45, 45, 0.85);

        --dev-tools-border-radius: 0.5rem;
        --dev-tools-box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 12px -2px rgba(0, 0, 0, 0.4);

        --dev-tools-blue-hsl: 206, 100%, 70%;
        --dev-tools-blue-color: hsl(var(--dev-tools-blue-hsl));
        --dev-tools-green-hsl: 145, 80%, 42%;
        --dev-tools-green-color: hsl(var(--dev-tools-green-hsl));
        --dev-tools-grey-hsl: 0, 0%, 50%;
        --dev-tools-grey-color: hsl(var(--dev-tools-grey-hsl));
        --dev-tools-yellow-hsl: 38, 98%, 64%;
        --dev-tools-yellow-color: hsl(var(--dev-tools-yellow-hsl));
        --dev-tools-red-hsl: 355, 100%, 68%;
        --dev-tools-red-color: hsl(var(--dev-tools-red-hsl));

        /* Needs to be in ms, used in JavaScript as well */
        --dev-tools-transition-duration: 180ms;
      }

      .notification-tray {
        display: flex;
        flex-direction: column-reverse;
        align-items: flex-end;
        margin: 0.5rem;
        flex: none;
      }

      @supports (backdrop-filter: blur(1px)) {
        .notification-tray div.message {
          backdrop-filter: blur(8px);
        }

        .notification-tray div.message {
          background-color: var(--dev-tools-background-color-active-blurred);
        }
      }

      .notification-tray .message {
        pointer-events: auto;
        background-color: var(--dev-tools-background-color-active);
        color: var(--dev-tools-text-color);
        max-width: 30rem;
        box-sizing: border-box;
        border-radius: var(--dev-tools-border-radius);
        margin-top: 0.5rem;
        transition: var(--dev-tools-transition-duration);
        transform-origin: bottom right;
        animation: slideIn var(--dev-tools-transition-duration);
        box-shadow: var(--dev-tools-box-shadow);
        padding-top: 0.25rem;
        padding-bottom: 0.25rem;
      }

      .notification-tray .message.animate-out {
        animation: slideOut forwards var(--dev-tools-transition-duration);
      }

      .notification-tray .message .message-details {
        max-height: 10em;
        overflow: hidden;
      }

      .message.information {
        --dev-tools-notification-color: var(--dev-tools-blue-color);
      }

      .message.warning {
        --dev-tools-notification-color: var(--dev-tools-yellow-color);
      }

      .message.error {
        --dev-tools-notification-color: var(--dev-tools-red-color);
      }

      .message {
        display: flex;
        padding: 0.1875rem 0.75rem 0.1875rem 2rem;
        background-clip: padding-box;
      }

      .message.log {
        padding-left: 0.75rem;
      }

      .message-content {
        margin-right: 0.5rem;
        -webkit-user-select: text;
        -moz-user-select: text;
        user-select: text;
      }

      .message-heading {
        position: relative;
        display: flex;
        align-items: center;
        margin: 0.125rem 0;
      }

      .message .message-details {
        font-weight: 400;
        color: var(--dev-tools-text-color-secondary);
        margin: 0.25rem 0;
      }

      .message .message-details[hidden] {
        display: none;
      }

      .message .message-details p {
        display: inline;
        margin: 0;
        margin-right: 0.375em;
        word-break: break-word;
      }

      .message .persist {
        color: var(--dev-tools-text-color-secondary);
        white-space: nowrap;
        margin: 0.375rem 0;
        display: flex;
        align-items: center;
        position: relative;
        -webkit-user-select: none;
        -moz-user-select: none;
        user-select: none;
      }

      .message .persist::before {
        content: '';
        width: 1em;
        height: 1em;
        border-radius: 0.2em;
        margin-right: 0.375em;
        background-color: rgba(255, 255, 255, 0.3);
      }

      .message .persist:hover::before {
        background-color: rgba(255, 255, 255, 0.4);
      }

      .message .persist.on::before {
        background-color: rgba(255, 255, 255, 0.9);
      }

      .message .persist.on::after {
        content: '';
        order: -1;
        position: absolute;
        width: 0.75em;
        height: 0.25em;
        border: 2px solid var(--dev-tools-background-color-active);
        border-width: 0 0 2px 2px;
        transform: translate(0.05em, -0.05em) rotate(-45deg) scale(0.8, 0.9);
      }

      .message .dismiss-message {
        font-weight: 600;
        align-self: stretch;
        display: flex;
        align-items: center;
        padding: 0 0.25rem;
        margin-left: 0.5rem;
        color: var(--dev-tools-text-color-secondary);
      }

      .message .dismiss-message:hover {
        color: var(--dev-tools-text-color);
      }

      .message.log {
        color: var(--dev-tools-text-color-secondary);
      }

      .message:not(.log) .message-heading {
        font-weight: 500;
      }

      .message.has-details .message-heading {
        color: var(--dev-tools-text-color-emphasis);
        font-weight: 600;
      }

      .message-heading::before {
        position: absolute;
        margin-left: -1.5rem;
        display: inline-block;
        text-align: center;
        font-size: 0.875em;
        font-weight: 600;
        line-height: calc(1.25em - 2px);
        width: 14px;
        height: 14px;
        box-sizing: border-box;
        border: 1px solid transparent;
        border-radius: 50%;
      }

      .message.information .message-heading::before {
        content: 'i';
        border-color: currentColor;
        color: var(--dev-tools-notification-color);
      }

      .message.warning .message-heading::before,
      .message.error .message-heading::before {
        content: '!';
        color: var(--dev-tools-background-color-active);
        background-color: var(--dev-tools-notification-color);
      }

      .ahreflike {
        font-weight: 500;
        color: var(--dev-tools-text-color-secondary);
        text-decoration: underline;
        cursor: pointer;
      }

      @keyframes slideIn {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0%);
          opacity: 1;
        }
      }

      @keyframes slideOut {
        from {
          transform: translateX(0%);
          opacity: 1;
        }
        to {
          transform: translateX(100%);
          opacity: 0;
        }
      }

      @keyframes fade-in {
        0% {
          opacity: 0;
        }
      }

      @keyframes bounce {
        0% {
          transform: scale(0.8);
        }
        50% {
          transform: scale(1.5);
          background-color: hsla(var(--dev-tools-red-hsl), 1);
        }
        100% {
          transform: scale(1);
        }
      }
    `;
  }
  render() {
    return u`<div class="notification-tray">
      ${l.notifications.map((e) => this.renderNotification(e))}
    </div>`;
  }
  renderNotification(e) {
    return u`
      <div
        class="message ${e.type} ${e.animatingOut ? "animate-out" : ""} ${e.details || e.link ? "has-details" : ""}"
        data-testid="message">
        <div class="message-content">
          <div class="message-heading">${e.message}</div>
          <div class="message-details" ?hidden="${!e.details && !e.link}">
            ${Q(e.details)}
            ${e.link ? u`<a class="ahreflike" href="${e.link}" target="_blank">Learn more</a>` : ""}
          </div>
          ${e.dismissId ? u`<div
                class="persist ${e.dontShowAgain ? "on" : "off"}"
                @click=${() => {
      this.toggleDontShowAgain(e);
    }}>
                ${Pe(e)}
              </div>` : ""}
        </div>
        <div
          class="dismiss-message"
          @click=${(t) => {
      ae(e), t.stopPropagation();
    }}>
          Dismiss
        </div>
      </div>
    `;
  }
  toggleDontShowAgain(e) {
    e.dontShowAgain = !e.dontShowAgain, this.requestUpdate();
  }
};
Y = ze([
  k("copilot-notifications-container")
], Y);
function Pe(e) {
  return e.dontShowAgainMessage ? e.dontShowAgainMessage : "Do not show this again";
}
le({
  type: ee.WARNING,
  message: "Development Mode",
  details: "This application is running in development mode.",
  dismissId: "devmode"
});
const V = te(() => {
  c.emit("component-tree-updated", {});
});
c.on("vite-after-update", () => {
  V();
});
const H = window?.Vaadin?.connectionState?.stateChangeListeners;
H ? H.add((e, t) => {
  e === "loading" && t === "connected" && l.active && V();
}) : console.warn("Unable to add listener for connection state changes");
c.on("copilot-plugin-state", (e) => {
  l.setIdePluginState(e.detail), e.detail.active && ie("plugin-active", `${e.detail.version}-${e.detail.ide}`), e.preventDefault();
});

import { M as t, v as n, j as a, ar as i, b as r } from "./copilot-Clqe7NnV.js";
class l extends t {
  constructor() {
    super(...arguments), this.eventBusRemovers = [], this.messageHandlers = {}, this.handleESC = (e) => {
      const s = n.getPanelByTag(this.tagName);
      !a.active && s && !s.individual || e.key === "Escape" && i(this);
    };
  }
  createRenderRoot() {
    return this;
  }
  onEventBus(e, s) {
    this.eventBusRemovers.push(r.on(e, s));
  }
  connectedCallback() {
    super.connectedCallback(), this.addESCListener();
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.eventBusRemovers.forEach((e) => e()), this.removeESCListener();
  }
  addESCListener() {
    document.addEventListener("keydown", this.handleESC);
  }
  removeESCListener() {
    document.removeEventListener("keydown", this.handleESC);
  }
  onCommand(e, s) {
    this.messageHandlers[e] = s;
  }
  handleMessage(e) {
    return this.messageHandlers[e.command] ? (this.messageHandlers[e.command].call(this, e), !0) : !1;
  }
}
export {
  l as B
};

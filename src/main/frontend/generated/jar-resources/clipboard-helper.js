import { html, LitElement, css } from "lit";
import "@polymer/polymer/polymer-legacy.js";

class ClipboardHelper extends LitElement {
  render() {
    return html` <div id="wrapper" @click="${this.copy}"></div> `;
  }

  static get is() {
    return "clipboard-helper";
  }

  static get properties() {
    return {
      content: {
        type: String,
      },
    };
  }
  copy() {
    const el = document.createElement("textarea");
    el.value = this.content;
    el.setAttribute("readonly", "");
    el.style.position = "absolute";
    el.style.left = "-9999px";
    document.body.appendChild(el);
    const selected =
      document.getSelection().rangeCount > 0
        ? document.getSelection().getRangeAt(0)
        : false;
    el.select();
    document.execCommand("copy");
    document.body.removeChild(el);
    if (selected) {
      document.getSelection().removeAllRanges();
      document.getSelection().addRange(selected);
    }
  }
  constructor() {
    super();
    this.content = "";
  }
}
customElements.define(ClipboardHelper.is, ClipboardHelper);

import { a as D } from "./copilot-Clqe7NnV.js";
var g, b;
function x() {
  return b || (b = 1, g = function() {
    var a = document.getSelection();
    if (!a.rangeCount)
      return function() {
      };
    for (var o = document.activeElement, s = [], i = 0; i < a.rangeCount; i++)
      s.push(a.getRangeAt(i));
    switch (o.tagName.toUpperCase()) {
      // .toUpperCase handles XHTML
      case "INPUT":
      case "TEXTAREA":
        o.blur();
        break;
      default:
        o = null;
        break;
    }
    return a.removeAllRanges(), function() {
      a.type === "Caret" && a.removeAllRanges(), a.rangeCount || s.forEach(function(d) {
        a.addRange(d);
      }), o && o.focus();
    };
  }), g;
}
var m, C;
function E() {
  if (C) return m;
  C = 1;
  var a = x(), o = {
    "text/plain": "Text",
    "text/html": "Url",
    default: "Text"
  }, s = "Copy to clipboard: #{key}, Enter";
  function i(n) {
    var t = (/mac os x/i.test(navigator.userAgent) ? "⌘" : "Ctrl") + "+C";
    return n.replace(/#{\s*key\s*}/g, t);
  }
  function d(n, t) {
    var c, y, v, u, l, e, f = !1;
    t || (t = {}), c = t.debug || !1;
    try {
      v = a(), u = document.createRange(), l = document.getSelection(), e = document.createElement("span"), e.textContent = n, e.ariaHidden = "true", e.style.all = "unset", e.style.position = "fixed", e.style.top = 0, e.style.clip = "rect(0, 0, 0, 0)", e.style.whiteSpace = "pre", e.style.webkitUserSelect = "text", e.style.MozUserSelect = "text", e.style.msUserSelect = "text", e.style.userSelect = "text", e.addEventListener("copy", function(r) {
        if (r.stopPropagation(), t.format)
          if (r.preventDefault(), typeof r.clipboardData > "u") {
            c && console.warn("unable to use e.clipboardData"), c && console.warn("trying IE specific stuff"), window.clipboardData.clearData();
            var p = o[t.format] || o.default;
            window.clipboardData.setData(p, n);
          } else
            r.clipboardData.clearData(), r.clipboardData.setData(t.format, n);
        t.onCopy && (r.preventDefault(), t.onCopy(r.clipboardData));
      }), document.body.appendChild(e), u.selectNodeContents(e), l.addRange(u);
      var w = document.execCommand("copy");
      if (!w)
        throw new Error("copy command was unsuccessful");
      f = !0;
    } catch (r) {
      c && console.error("unable to copy using execCommand: ", r), c && console.warn("trying IE specific stuff");
      try {
        window.clipboardData.setData(t.format || "text", n), t.onCopy && t.onCopy(window.clipboardData), f = !0;
      } catch (p) {
        c && console.error("unable to copy using clipboardData: ", p), c && console.error("falling back to prompt"), y = i("message" in t ? t.message : s), window.prompt(y, n);
      }
    } finally {
      l && (typeof l.removeRange == "function" ? l.removeRange(u) : l.removeAllRanges()), e && document.body.removeChild(e), v();
    }
    return f;
  }
  return m = d, m;
}
var h = E();
const T = /* @__PURE__ */ D(h);
export {
  T as c
};

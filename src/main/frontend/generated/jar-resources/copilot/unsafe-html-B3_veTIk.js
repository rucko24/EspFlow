import { Q as r, T as e, R as n, S as o, U as c } from "./copilot-DKr2YKvS.js";
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
class s extends r {
  constructor(t) {
    if (super(t), this.it = e, t.type !== n.CHILD)
      throw Error(this.constructor.directiveName + "() can only be used in child bindings");
  }
  render(t) {
    if (t === e || t == null)
      return this._t = void 0, this.it = t;
    if (t === o)
      return t;
    if (typeof t != "string")
      throw Error(this.constructor.directiveName + "() called with a non-string value");
    if (t === this.it)
      return this._t;
    this.it = t;
    const i = [t];
    return i.raw = i, this._t = { _$litType$: this.constructor.resultType, strings: i, values: [] };
  }
}
s.directiveName = "unsafeHTML", s.resultType = 1;
const h = c(s);
export {
  h as o
};

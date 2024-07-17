import { e as s, b as f, o as n } from "./copilot-DKr2YKvS.js";
const a = 5e3;
let o = 1;
function c(i) {
  s.notifications.includes(i) && (i.dontShowAgain && i.dismissId && m(i.dismissId), s.removeNotification(i), f.emit("notification-dismissed", i));
}
function d(i) {
  return n.getDismissedNotifications().includes(i);
}
function m(i) {
  d(i) || n.addDismissedNotification(i);
}
function r(i) {
  return !(i.dismissId && (d(i.dismissId) || s.notifications.find((t) => t.dismissId === i.dismissId)));
}
function N(i) {
  r(i) && u(i);
}
function u(i) {
  const t = o;
  o += 1;
  const e = { ...i, id: t, dontShowAgain: !1, animatingOut: !1 };
  s.setNotifications([...s.notifications, e]), !i.link && !i.dismissId && setTimeout(() => {
    c(e);
  }, a);
}
export {
  c as dismissNotification,
  N as showNotification
};

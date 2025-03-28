window.copyToClipboard = async (str) => {
  if (navigator.clipboard && navigator.clipboard.writeText) {
    try {
      await navigator.clipboard.writeText(str);
      console.log("Text copied to clipboard!");
      return;
    } catch (err) {
      console.warn("Crash when using Clipboard API, using fallback: ", err);
    }
  }

  // Fallback para navegadores antiguos
  const textarea = document.createElement("textarea");
  textarea.value = str;
  textarea.style.position = "absolute";
  textarea.style.opacity = "0";
  document.body.appendChild(textarea);
  textarea.select();
  try {
    document.execCommand("copy");
    console.log("Text copied to clipboard using fallback!");
  } catch (err) {
    console.error("Error when copying to clipboard with fallback: ", err);
  } finally {
    document.body.removeChild(textarea);
  }
};
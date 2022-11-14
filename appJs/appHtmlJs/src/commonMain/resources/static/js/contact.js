'use strict';

if (window.attachEvent) {window.attachEvent('onload', performWindowLoadOperations);}
else if (window.addEventListener) {window.addEventListener('load', performWindowLoadOperations, false);}
else {document.addEventListener('load', performWindowLoadOperations, false);}

function performWindowLoadOperations() {
    injectEmail();
}

// Email injection

function injectEmail() {
    var email = 'gmail.com'
    email = "ga.christov" + "@" + email
    var mailTo = "mailto:" + email
    setHref("contact_email_1", mailTo)
    setHref("contact_email_2", mailTo)
    setHref("contact_email_3", mailTo)
}

function setHref(id, content) {
  const item = document.getElementById(id)
  if (item === null) return
  item.href = content
}

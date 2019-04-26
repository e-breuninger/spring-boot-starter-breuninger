function ontogglzchange(togglz) {
  let xhr = new XMLHttpRequest();
  let url = `/actuator/togglz/${togglz.id}?featureName=${togglz.id}&enabled=${togglz.checked}`;
  xhr.open('POST', url, true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.send();
};

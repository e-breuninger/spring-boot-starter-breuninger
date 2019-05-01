function onToggleChange(toggleCheckbox) {
  const xhr = new XMLHttpRequest();
  const url = `/actuator/togglz/${toggleCheckbox.id}?featureName=${toggleCheckbox.id}&enabled=${toggleCheckbox.checked}`;
  xhr.open('POST', url, true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.send();
}

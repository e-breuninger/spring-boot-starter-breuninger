function onToggleChange(toggleCheckbox) {
  fetch(`/actuator/togglz/${toggleCheckbox.id}?featureName=${toggleCheckbox.id}&enabled=${toggleCheckbox.checked}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: ''
  });
}

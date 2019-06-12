function onToggleChange(toggleCheckbox) {
  $.ajax({
    type: 'POST',
    url: `/actuator/togglz/${toggleCheckbox.id}?featureName=${toggleCheckbox.id}&enabled=${toggleCheckbox.checked}`,
    contentType: 'application/json'
  });
}

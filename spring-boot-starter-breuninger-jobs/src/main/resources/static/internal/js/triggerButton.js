$('.js_triggerButton').on('click', function () {
  $.ajax({
    type: 'POST',
    url: $(this).data('trigger-url'),
    data: {},
    dataType: 'json',
    error: function (data, status, error) {
      if (error === 'Conflict') {
        alert('Job is currently running or blocked by a different job.');
      } else {
        alert('Failed to trigger job. \n\nStatus: ' + status + '\nError: ' + error);
      }
    },
    success: function (data, textStatus, xhr) {
      window.location.href = xhr.getResponseHeader('Location');
    }
  });
});

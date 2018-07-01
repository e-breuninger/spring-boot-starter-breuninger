function getLog(logIndex) {
  $.ajax({
    type: 'GET',
    url: $('.logWindow').data('job-url'),
    headers: {
      Accept: 'application/json; charset=utf-8',
      'Content-Type': 'application/json; charset=utf-8'
    },
    data: {},
    dataType: 'json',
    error: () => {
      const jobStatus = $('#job-status');
      jobStatus.attr('class', 'label label-danger');
      jobStatus.attr('style', 'width:10em; height:2em;');
      jobStatus.html('<span>UNKNOWN</span>');
    },
    success: (data) => {
      const numberOfMessages = data.messages.length;
      const logWindow = $('.logWindow');

      while (logIndex < numberOfMessages) {
        if (logIndex === 0) {
          logWindow.empty();
        }
        logWindow.append(`<div>${data.messages[logIndex]}</div>`);
        logIndex++;
      }

      if ($('#follow-log').prop('checked')) {
        logWindow.each(function () {
          const scrollHeight = Math.max(this.scrollHeight, this.clientHeight);
          this.scrollTop = scrollHeight - this.clientHeight;
        });
      }

      // schedule further polling if still runnin'
      if (data.state === 'Running') {
        setTimeout(() => {
          getLog(logIndex);
        }, 2000);
      } else {
        const jobStatus = $('#job-status');
        if (data.status === 'OK') {
          jobStatus.attr('class', 'label label-success');
          jobStatus.attr('style', 'width:10em; height:2em;');
          jobStatus.html(`<span>${data.status}</span>`);
        } else if (data.status === 'SKIPPED') {
          jobStatus.attr('class', 'label label-default');
          jobStatus.attr('style', 'width:10em; height:2em;');
          jobStatus.html(`<span>${data.status}</span>`);
        } else if (data.status === 'ERROR') {
          jobStatus.attr('class', 'label label-danger');
          jobStatus.attr('style', 'width:10em; height:2em;');
          jobStatus.html(`<span>${data.status}</span>`);
        } else if (data.status === 'DEAD') {
          jobStatus.attr('class', 'label label-warning');
          jobStatus.attr('style', 'width:10em; height:2em; background: rgba(230, 110, 30, 1);');
          jobStatus.html(`<span>${data.status}</span>`);
        }
        $('#job-stopped').text(data.stopped);
        $('.triggerButton').prop('disabled', false);
      }
      $('#job-last-updated').text(data.lastUpdated);
    }
  });
}

// uncheck follow log checkbox if real mouse scrolling detected
$('.logWindow').bind('scroll mousedown DOMMouseScroll mousewheel keyup', (event) => {
  if (event.which > 0 || event.type === 'mousedown' || event.type === 'mousewheel') {
    $('#follow-log').prop('checked', false);
  }
});

setTimeout(() => {
  getLog(0);
}, 1000);

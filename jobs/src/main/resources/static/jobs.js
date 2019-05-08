// TODO(BS): check this file
const timermap = new Map();
const faSpinner = 'fas fa-spinner fa-spin';
const faCheck = 'fas fa-check';
const faExclamation = 'fas fa-exclamation';
const faTimes = 'fas fa-times';
const faMinusCircle = 'fas fa-minus-circle';
const displayTableRow = 'display-table-row';
const displayNone = 'display-none';
const statusOk = 'OK';
const statusSkipped = 'SKIPPED';
const localeDE = 'de-DE';

function openCollapseCards(button) {
  const content = $(`#${button.value}content`)[0];
  button.classList.toggle('card-header-title');
  button.classList.toggle('is-primary');
  button.classList.toggle('hide-last-child');
  content.classList.toggle('flex');
  content.classList.toggle('flex-wrap');
}

function updateMessagesAndDates(input) {
  const id = input.value;

  const callJobExecutionUpdateFromServer = () => {
    $.getJSON(`../../jobExecutions/${id}`, fragment => {
      if (fragment) {
        const messageElement = $(`#${id}`)[0];
        messageElement.innerHTML = formatMessages(fragment.messages);
        messageElement.scrollTop = messageElement.scrollHeight;

        const statusElement = $(`#${id}status`)[0];
        statusElement.innerHTML = fragment.status === statusOk && !fragment.stopped ? 'Running' : fragment.status;
        statusElement.className = fragment.status === statusOk ? 'green' : fragment.status === statusSkipped ? 'yellow' : 'red';

        $(`#${id}headerstate`)[0].className = fragment.status === statusOk ?
          !fragment.stopped ? faSpinner : faCheck :
          fragment.status === statusSkipped ? faExclamation : faTimes;

        const options = {
          year: 'numeric',
          month: '2-digit',
          day: 'numeric',
          hour: 'numeric',
          minute: 'numeric',
          second: 'numeric'
        };

        // updateDisableState last updated date
        $(`#${id}update`)[0].textContent = new Date(fragment.lastUpdated).toLocaleDateString(localeDE, options).replace(',', '');

        // if stopped updateDisableState stopped date
        if (fragment.stopped) {
          $(`#${id}stopped`)[0].textContent = new Date(fragment.stopped).toLocaleDateString(localeDE, options).replace(',', '');
          input.checked = false;
          clearInterval(timermap.get(id));
          timermap.delete(id);
        }
      }
    });
  };

  if (input.checked) {
    callJobExecutionUpdateFromServer();
    const intervalId = setInterval(callJobExecutionUpdateFromServer, 1000);
    timermap.set(id, intervalId);
  } else {
    clearInterval(timermap.get(id));
    timermap.delete(id);
  }
}

function formatMessages(rawMessages) {
  const formattedMessages = [];
  for (const rawMessage of rawMessages) {
    const formattedMessage = [];
    const date = new Date(rawMessage.timestamp);
    const month = date.getMonth() + 1;
    formattedMessage.push([
      date.getFullYear(),
      month < 10 ? `0${month}` : month,
      date.getDate()
    ].join('-'), date.toLocaleTimeString(), rawMessage.level, '---', rawMessage.message);
    formattedMessages.push(formattedMessage.join(' '));
  }
  return formattedMessages.join('\n');
}

function startJob(button) {
  $.post(`jobExecutions?jobId=${button.value}`, job => {
    if (job && job.runningJobExecutionId) {
      const jobExecutionLink = $(`#${button.value}executionid`)[0];
      jobExecutionLink.textContent = job.runningJobExecutionId.value;
      jobExecutionLink.href = `../jobexecutions/single/${job.runningJobExecutionId.value}`;

      const jobExecutionLinkHeader = $(`#${button.value}executionidheader`)[0];
      jobExecutionLinkHeader.textContent = job.runningJobExecutionId.value;
      jobExecutionLinkHeader.href = `../jobexecutions/single/${job.runningJobExecutionId.value}`;

      // updateDisableState header status
      updateJob(job.id.value, job);
    }
  });
}

function disableJob(disable, button) {
  $.ajax({
    data: JSON.stringify({
      id: {value: button.value},
      disabled: disable,
      disableComment: $(`#${button.value}disabledcommentinput`)[0].value
    }),
    contentType: 'application/json',
    type: 'PUT',
    url: `jobs/${button.value}`,
    success: fragment => {
      if (fragment) {
        updateJob(button.value, fragment);
      }
    },
    dataType: 'json'
  });
}

function updateJob(jobId, jobData) {
  const statusElement = $(`#${jobId}status`)[0];
  statusElement.textContent = jobData.disabled ? 'Disabled' : 'Enabled';
  statusElement.className = jobData.disabled ? 'disabled' : 'enabled';

  $(`#${jobId}disabledcomment`)[0].textContent = jobData.disableComment;

  $(`#${jobId}disabled`)[0].className = jobData.disabled ? displayTableRow : displayNone;
  $(`#${jobId}enabled`)[0].className = jobData.disabled ? displayNone : displayTableRow;
  $(`#${jobId}headerstate`)[0].className = jobData.disabled ? faMinusCircle : jobData.runningJobExecutionId ? faSpinner : faCheck;

  $(`#${jobId}start`)[0].disabled = jobData.runningJobExecutionId ? true : jobData.disabled;
}

function stopPropagation(e) {
  e.stopPropagation();
  e.stopImmediatePropagation();
}

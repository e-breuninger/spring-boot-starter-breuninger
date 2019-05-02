// TODO(BS): check this file
const timermap = new Map();

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
    $.get(`../../messages?jobExecutionId=${id}`, fragment => {
      if (fragment) {
        const messageElement = $(`#${id}`)[0];
        messageElement.innerHTML = formatMessages(fragment.messages);
        messageElement.scrollTop = messageElement.scrollHeight;

        const statusElement = $(`#${id}status`)[0];
        statusElement.innerHTML = fragment.status === 'OK' && !fragment.stopped ? 'Running' : fragment.status;

        statusElement.className = fragment.status === 'OK' ? 'green' : fragment.status === 'SKIPPED' ? 'yellow' : 'red';

        $(`#${id}headerstate`)[0].className = fragment.status === 'OK' ?
          !fragment.stopped ? 'fas fa-spinner fa-spin' : 'fas fa-check' :
          fragment.status === 'SKIPPED' ? 'fas fa-exclamation' : 'fas fa-times';

        const options = {
          year: 'numeric',
          month: '2-digit',
          day: 'numeric',
          hour: 'numeric',
          minute: 'numeric',
          second: 'numeric'
        };

        // update last updated date
        const lastUpdateElement = $(`#${id}update`)[0];
        lastUpdateElement.innerHTML = new Date(fragment.lastUpdated).toLocaleDateString('de-DE', options).replace(',', '');

        // if stopped update stopped date
        if (fragment.stopped) {
          const stoppedElement = $(`#${id}stopped`)[0];
          stoppedElement.innerHTML = new Date(fragment.stopped).toLocaleDateString('de-DE', options).replace(',', '');
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
  $.post(`job/start?jobId=${button.value}`, job => {
    if (job && job.runningJobExecutionId) {
      const jobExecutionLink = $(`#${button.value}executionid`)[0];
      jobExecutionLink.innerHTML = job.runningJobExecutionId.value;
      jobExecutionLink.href = `../jobexecutions/single/${job.runningJobExecutionId.value}`;

      const jobExecutionLinkHeader = $(`#${button.value}executionidheader`)[0];
      jobExecutionLinkHeader.innerHTML = job.runningJobExecutionId.value;
      jobExecutionLinkHeader.href = `../jobexecutions/single/${job.runningJobExecutionId.value}`;

      // update header status
      updateJob(job.id.value, job);
    }
  });
}

function disableJob(disable, button) {
  const data = $(`#${button.value}disabledcommentinput`)[0].value;
  $.ajax({
    data: data,
    contentType: 'text/plain',
    type: 'POST',
    url: `job/disable?jobId=${button.value}&disabled=${disable}`,
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
  statusElement.innerHTML = jobData.disabled ? 'Disabled' : 'Enabled';
  statusElement.className = jobData.disabled ? 'disabled' : 'enabled';

  const disabledElement = $(`#${jobId}disabled`)[0];
  disabledElement.className = jobData.disabled ? 'display-table-row' : 'display-none';

  const disabledCommentElement = $(`#${jobId}disabledcomment`)[0];
  disabledCommentElement.innerHTML = jobData.disableComment;

  const enabledElement = $(`#${jobId}enabled`)[0];
  enabledElement.className = jobData.disabled ? 'display-none' : 'display-table-row';

  const startButton = $(`#${jobId}start`)[0];
  startButton.disabled = jobData.runningJobExecutionId ? true : jobData.disabled;

  const headerState = $(`#${jobId}headerstate`)[0];
  headerState.className = jobData.disabled ?
    'fas fa-minus-circle' :
    jobData.runningJobExecutionId ? 'fas fa-spinner fa-spin' : 'fas fa-check';
}

function stopPropagation(e) {
  e.stopPropagation();
  e.stopImmediatePropagation();
}
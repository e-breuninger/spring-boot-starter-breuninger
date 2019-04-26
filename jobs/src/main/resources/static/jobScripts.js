const timermap = new Map();

/**
 * Open or collapse a specific card.
 *
 * @param button the button which was pressed to open/collapse the card which contains the jobid as its value
 */
function openCollapseCards(button) {
  const content = $("#" + button.value + 'content')[0];
  button.classList.toggle('card-header-title');
  button.classList.toggle('is-primary');
  button.classList.toggle('hide-last-child');
  content.classList.toggle('flex');
  content.classList.toggle('flex-wrap');
}

/**
 * Starts or stops the continuous update of messages and some other job execution information. The updating stops if the stop
 * date is set.
 *
 * @param input (emlement) the input element which was checked/unchecked
 */
function updateMessagesAndDates(input) {
  const id = input.value;

  const callJobExecutionUpdateFromServer = function () {
    $.get('../../messages?jobExecutionId=' + id, function (fragment) {
      if (fragment) {
        const messageElement = $("#" + id)[0];
        messageElement.innerHTML = formatMessages(fragment.messages);
        messageElement.scrollTop = messageElement.scrollHeight;

        const statusElement = $("#" + id + "status")[0];
        statusElement.innerHTML = fragment.status == 'OK' && !fragment.stopped ? 'Running' : fragment.status;

        const newClass = fragment.status === 'OK' ? 'green' : fragment.status === 'SKIPPED' ? 'yellow' : 'red';
        statusElement.className = newClass;

        const headerstate = $('#' + id + 'headerstate')[0];
        headerstate.className = fragment.status == 'OK' ? !fragment.stopped
          ? 'fas fa-spinner fa-spin' : 'fas fa-check' : fragment.status == 'SKIPPED'
          ? 'fas fa-exclamation' : 'fas fa-times';

        const options = {
          year: 'numeric',
          month: '2-digit',
          day: 'numeric',
          hour: 'numeric',
          minute: 'numeric',
          second: 'numeric'
        };

        // update last updated date
        const lastUpdateElement = $("#" + id + 'update')[0];
        const lastUpdatedDate = new Date(fragment.lastUpdated).toLocaleDateString('de-DE', options).replace(',', '');
        lastUpdateElement.innerHTML = lastUpdatedDate;

        // if stopped update stopped date
        if (fragment.stopped) {
          const stoppedElement = $("#" + id + 'stopped')[0];
          const stoppedDate = new Date(fragment.stopped).toLocaleDateString('de-DE', options).replace(',', '');
          stoppedElement.innerHTML = stoppedDate;
          input.checked = false;
          clearInterval(timermap.get(id));
          timermap.delete(id);
        }
      }
    });
  };

  if (input.checked) {
    //start interval to fetch changes every 1 second
    callJobExecutionUpdateFromServer();
    const intervalId = setInterval(callJobExecutionUpdateFromServer, 1000);
    timermap.set(id, intervalId);
  } else {
    //stop interval
    clearInterval(timermap.get(id));
    timermap.delete(id);
  }
}

/**
 * Formats the messages of the server to be displayed on the client
 *
 * @param rawMessages the messages from the server
 * @returns {string} the formatted messages as an concatenated string
 */
function formatMessages(rawMessages) {
  const formattedMessages = [];
  for (const rawMessage of rawMessages) {
    const formattedMessage = [];
    const date = new Date(rawMessage.timestamp);
    const month = date.getMonth() + 1;
    formattedMessage.push([
      date.getFullYear(),
      month < 10 ? '0' + month : month,
      date.getDate()
    ].join('-'), date.toLocaleTimeString(), rawMessage.level, '---', rawMessage.message);
    formattedMessages.push(formattedMessage.join(' '));
  }
  return formattedMessages.join('\n');
}

function startJob(button) {
  $.post('job/start?jobId=' + button.value, function (job) {
    if(job && job.runningJobExecutionId) {
      const jobExecutionLink = $('#' + button.value + 'executionid')[0];
      jobExecutionLink.innerHTML = job.runningJobExecutionId.value;
      jobExecutionLink.href = '../jobexecutions/single/' + job.runningJobExecutionId.value;

      const jobExecutionLinkHeader = $('#' + button.value + 'executionidheader')[0];
      jobExecutionLinkHeader.innerHTML = job.runningJobExecutionId.value;
      jobExecutionLinkHeader.href = '../jobexecutions/single/' + job.runningJobExecutionId.value;

      // update header status
      updateJob(job.id.value, job)
    }
  });
}

/**
 * Disable or enable the given job. If the job is disabled the value of the disable comment input field is used for the
 * disabled comment
 *
 * @param disable (boolean) whether the job should be disabled or enabled
 * @param button (element) the button element of the disabled button. this is needed to get the job id. it is assumed the
 *   button contains the job id as its value
 */
function disableJob(disable, button) {
  const data = $('#' + button.value + 'disabledcommentinput')[0].value;
  $.ajax({
    data: data,
    contentType: "text/plain",
    type: 'POST',
    url: 'job/disable?jobId=' + button.value + '&disabled=' + disable,
    success: function (fragment) {
      if (fragment) {
        updateJob(button.value, fragment)
      }
    },
    dataType: 'json'
  });
}

/**
 * Updates a shown job with the given data
 * @param jobId the jobid of the job which should be updated
 * @param jobData the new job data
 */
function updateJob(jobId, jobData) {
  const stausElement = $('#' + jobId + 'status')[0];
  stausElement.innerHTML = jobData.disabled ? "Disabled" : "Enabled";
  stausElement.className = jobData.disabled ? "disabled" : "enabled";

  const disabledElement = $('#' + jobId + 'disabled')[0];
  disabledElement.className = jobData.disabled ? "display-table-row" : "display-none";

  const disabledCommentElement = $('#' + jobId + 'disabledcomment')[0];
  disabledCommentElement.innerHTML = jobData.disableComment;

  const enabledElement = $('#' + jobId + 'enabled')[0];
  enabledElement.className = jobData.disabled ? "display-none" : "display-table-row";

  const startButton = $('#' + jobId + 'start')[0];
  startButton.disabled = jobData.runningJobExecutionId ? true : jobData.disabled;

  const headerState = $('#' + jobId + 'headerstate')[0];
  headerState.className = jobData.disabled ? 'fas fa-minus-circle' :
    jobData.runningJobExecutionId ? 'fas fa-spinner fa-spin' : 'fas fa-check';
}

function stopPropagation(e){
  e.stopPropagation();
  e.stopImmediatePropagation();
}

// TODO(BS): check this file
const faSpinner = 'fas fa-spinner fa-spin';
const faCheck = 'fas fa-check';
const faMinusCircle = 'fas fa-minus-circle';
const displayTableRow = 'display-table-row';
const displayNone = 'display-none';

function openCollapseCards(button) {
  const content = $(`#${button.value}content`)[0];
  button.classList.toggle('card-header-title');
  button.classList.toggle('is-primary');
  button.classList.toggle('hide-last-child');
  content.classList.toggle('flex');
  content.classList.toggle('flex-wrap');
}

function startJob(button) {
  $.post(`/jobExecutions?jobId=${button.value}`, job => {
    if (job && job.runningJobExecutionId) {
      const jobExecutionLink = $(`#${button.value}executionid`)[0];
      jobExecutionLink.textContent = job.runningJobExecutionId.value;
      jobExecutionLink.href = `/jobExecutions/${job.runningJobExecutionId.value}`;

      const jobExecutionLinkHeader = $(`#${button.value}executionidheader`)[0];
      jobExecutionLinkHeader.textContent = job.runningJobExecutionId.value;
      jobExecutionLinkHeader.href = `/jobExecutions/${job.runningJobExecutionId.value}`;

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
    url: `/jobs/${button.value}`,
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

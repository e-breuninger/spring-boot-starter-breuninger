const displayNone = 'display-none';

function openCollapseCards(button) {
  button.classList.toggle('is-primary');
  button.classList.toggle('card-header-title');
  document.getElementById(`${button.value}-content`).classList.toggle('flex-wrap');
}

function startJob(button) {
  fetch(`/jobExecutions?jobId=${button.value}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: ''
  }).then(response => {
    response.json().then(job => {
      if (job && job.runningJobExecutionId) {
        const jobExecutionId = job.runningJobExecutionId.value;
        const jobExecutionsLink = `/jobExecutions/${jobExecutionId}`;

        const jobExecutionLinkHeader = document.getElementById(`${button.value}-executionid-header`);
        jobExecutionLinkHeader.textContent = jobExecutionId;
        jobExecutionLinkHeader.href = jobExecutionsLink;

        const jobExecutionLink = document.getElementById(`${button.value}-executionid`);
        jobExecutionLink.textContent = jobExecutionId;
        jobExecutionLink.href = jobExecutionsLink;

        updateJob(job.id.value, job);
      }
    });
  });
}

function disableJob(disable, button) {
  fetch(`/jobs/${button.value}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      id: {value: button.value},
      disabled: disable,
      disableComment: document.getElementById(`${button.value}-disabledcomment-input`).value
    })
  }).then(response => {
    response.json().then(job => {
      if (job) {
        updateJob(button.value, job);
      }
    });
  })
}

function updateJob(jobId, job) {
  document.getElementById(`${jobId}-headerstate`).className = job.runningJobExecutionId ?
    'fas fa-spinner fa-spin has-text-primary' :
    job.disabled ? 'fas fa-minus-circle has-text-grey' : 'fas fa-check has-text-success';

  document.getElementById(`${jobId}-start`).disabled = job.disabled;

  const statusElement = document.getElementById(`${jobId}-status`);
  statusElement.textContent = job.disabled ? 'Disabled' : 'Enabled';
  statusElement.className = job.disabled ? 'badge has-background-grey' : 'badge has-background-success';

  document.getElementById(`${jobId}-disabledcomment`).textContent = job.disableComment;
  document.getElementById(`${jobId}-disabled`).className = job.disabled ? '' : displayNone;

  document.getElementById(`${jobId}-enabled`).className = job.disabled ? displayNone : '';
}

const intervalIds = new Map();

function openCollapseCards(button) {
  button.classList.toggle('is-primary');
  button.classList.toggle('card-header-title');
  document.getElementById(`${button.value}-card-content`).classList.toggle('flex-wrap');
  updateJobExecution(document.getElementById(`${button.value}-update-messages-checkbox`), true);
}

function updateJobExecution(input, checked) {
  const id = input.value;
  const updateStatus = jobExecution => {
    const statusOk = 'OK';
    const statusSkipped = 'SKIPPED';

    document.getElementById(`${id}-status`).className = jobExecution.status === statusOk ?
      !jobExecution.stopped ? 'fas fa-spinner fa-spin has-text-primary' : 'fas fa-check has-text-success' :
      jobExecution.status === statusSkipped ? 'fas fa-exclamation has-text-yellow' : 'fas fa-times has-text-danger';
  };
  const updateDates = jobExecution => {
    const localeDE = 'de-DE';
    const options = {
      year: 'numeric',
      month: '2-digit',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric'
    };

    const lastUpdatedDateFields = document.getElementsByClassName(`${id}-last-updated`);
    for (let i = 0; i < lastUpdatedDateFields.length; i++) {
      lastUpdatedDateFields[i].textContent = new Date(jobExecution.lastUpdated)
        .toLocaleDateString(localeDE, options)
        .replace(',', '');
    }

    if (jobExecution.stopped) {
      document.getElementById(`${id}-stopped`).textContent = new Date(jobExecution.stopped)
        .toLocaleDateString(localeDE, options)
        .replace(',', '');
    }
  };
  const formatMessages = rawMessages => {
    const formattedMessages = [];
    for (const rawMessage of rawMessages) {
      const formattedMessage = [];
      const rawDate = new Date(rawMessage.timestamp);
      const month = rawDate.getMonth() + 1;
      const formattedDate = [
        rawDate.getFullYear(),
        month < 10 ? `0${month}` : month,
        rawDate.getDate()
      ].join('-');
      formattedMessage.push(formattedDate, rawDate.toLocaleTimeString(), rawMessage.level, '---', rawMessage.message);
      formattedMessages.push(formattedMessage.join(' '));
    }
    return formattedMessages.join('\n');
  };
  const updateMessages = jobExecution => {
    const messageElement = document.getElementById(`${id}`);
    messageElement.textContent = formatMessages(jobExecution.messages);
    messageElement.scrollTop = messageElement.scrollHeight;
  };
  const getAndUpdateJobExecution = () => {
    fetch(`/jobExecutions/${id}`, {
      headers: {
        'Accept': 'application/json'
      }
    }).then(response => {
      response.json().then((jobExecution => {
        if (jobExecution) {
          updateMessages(jobExecution);
          updateStatus(jobExecution);
          updateDates(jobExecution);
          if (jobExecution.stopped) {
            input.checked = false;
            input.parentNode.setAttribute('disabled', '');
            input.setAttribute('disabled', '');
            clearInterval(intervalIds.get(id));
            intervalIds.delete(id);
          }
        }
      }));
    });
  };

  if (!input.disabled && (input.checked || checked)) {
    input.checked = true;
    getAndUpdateJobExecution();
    const intervalId = setInterval(getAndUpdateJobExecution, 1000);
    intervalIds.set(id, intervalId);
  } else {
    clearInterval(intervalIds.get(id));
    intervalIds.delete(id);
  }
}

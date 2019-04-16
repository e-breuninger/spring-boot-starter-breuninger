const coll = document.getElementsByClassName("collapsible");
let i;
const timermap = new Map();

for (i = 0; i < coll.length; i++) {
  coll[i].addEventListener("click", function () {
    const content = $("#" + this.value + 'content')[0];
    if (content.style.display === "flex") {
      content.style.display = "none";
    } else {
      content.style.display = "flex";
    }
  });

  function updateEventCount(input) {
    const id = input.value;

    const callJobExecutionUpdateFromServer = function () {
      $.get('messages?jobExecutionId=' + id, function (fragment) { // get from controller
        console.log(fragment);
        if (fragment) {
          const messageElement = $("#" + id)[0];
          messageElement.innerHTML = formatMessages(fragment.messages); // update snippet of page
          messageElement.scrollTop = messageElement.scrollHeight;
          const statusElement = $("#" + id + "status")[0];
          statusElement.innerHTML = fragment.status;
          const newClass = fragment.status === 'OK' ? 'green' : fragment.status === 'SKIPPED' ? 'yellow' : 'red'
          statusElement.className = newClass;

          const options = {year: 'numeric', month: '2-digit', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric' };

          const lastUpdateElement = $("#" + id + 'update')[0];
          const lastUpdatedDate = new Date(fragment.lastUpdated).toLocaleDateString('de-DE', options).replace(',','');
          lastUpdateElement.innerHTML = lastUpdatedDate;

          const stoppedElement = $("#" + id + 'stopped')[0];
          const stoppedDate = new Date(fragment.stopped).toLocaleDateString('de-DE', options).replace(',','');
          stoppedElement.innerHTML = stoppedDate;
        }
      });
    };

    if (input.checked) {
      //start interval to fetch changes every 5 seconds
      callJobExecutionUpdateFromServer();
      const intervalId = setInterval(callJobExecutionUpdateFromServer, 5000);
      timermap.set(id, intervalId);
    } else {
      //stop interval
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
        month < 10 ? '0' + month : month,
        date.getDate()
      ].join('-'), date.toLocaleTimeString(), rawMessage.level, '---', rawMessage.message);
      formattedMessages.push(formattedMessage.join(' '));
    }
    return formattedMessages.join('\n');
  }

  function startJob(button) {
    $.post('jobstart?jobId=' + button.value);
  }

  function disableJob(disable, button) {
    const data = button.previousElementSibling.value.toString();
    $.ajax({
      data: data,
      contentType: "text/plain",
      type: 'POST',
      url: 'jobsdisable?jobId=' + button.value + '&disabled=' + disable
    });
  }
}

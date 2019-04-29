/* global SBA */

import jobsEndpoint from './jobs-endpoint';
import jobExecutionsEndpoint from './jobExecutions-endpoint';
import togglzEndpoint from './togglz-endpoint';
import jobExecutionsView from './jobExecutions-view';
import togglzView from './togglz-view';
import jobsView from './jobs-view';

// togglz toplevel view
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'togglz',  //<1>
      path: '/togglz', //<2>
      component: togglzView, //<3>
      label: 'Togglz', //<4>
      order: 1000, //<5>
    });
  }
});

// jobs toplevel view
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'jobs',  //<1>
      path: '/jobs', //<2>
      component: jobsView, //<3>
      label: 'Jobs', //<4>
      order: 1000, //<5>
    });
  }
});

// jobs toplevel view
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'jobExecutions',  //<1>
      path: '/jobExecutions', //<2>
      component: jobExecutionsView, //<3>
      label: 'Job Executions', //<4>
      order: 1000, //<5>
    });
  }
});

// togglz endpoint
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/togglz',
      parent: 'instances', // <1>
      path: 'togglz',
      component: togglzEndpoint,
      label: 'Togglz',
      order: 1000,
      isEnabled: ({instance}) => instance.hasEndpoint('togglz')
    });
  }
});

// jobs endpoint
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/jobs',
      parent: 'instances', // <1>
      path: 'jobs',
      component: jobsEndpoint,
      label: 'Jobs',
      order: 1000,
    });
  }
});

// jobexecutions endpoint
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/jobExecutions',
      parent: 'instances', // <1>
      path: 'jobExecutions',
      component: jobExecutionsEndpoint,
      label: 'JobExecutions',
      order: 1000,
    });
  }
});

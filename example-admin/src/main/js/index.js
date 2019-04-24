/* global SBA */

import jobsEndpoint from './jobs-endpoint';
import jobExecutionsEndpoint from './jobExecutions-endpoint';
import togglzEndpoint from './togglz-endpoint';

// tag::customization-ui-endpoint[]
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
// end::customization-ui-endpoint[]

// tag::customization-ui-endpoint[]
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
// end::customization-ui-endpoint[]


// tag::customization-ui-endpoint[]
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
// end::customization-ui-endpoint[]

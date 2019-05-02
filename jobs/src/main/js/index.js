// TODO(BS): Endpoint and View are not allowed to have same path or the wrong menu items will be highlighted
import jobsView from './jobs-view';
import jobExecutionsView from './jobExecutions-view';
import jobsEndpoint from './jobs-endpoint';
import jobExecutionsEndpoint from './jobExecutions-endpoint';

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'jobs',
      path: '/jobs',
      component: jobsView,
      label: 'Jobs',
      order: 1000
    });
  }
});

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'jobExecutions',
      path: '/jobExecutions',
      component: jobExecutionsView,
      label: 'Job Executions',
      order: 1000
    });
  }
});

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/jobs',
      parent: 'instances',
      path: '/jobs',
      component: jobsEndpoint,
      label: 'Jobs',
      order: 1000
    });
  }
});

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/jobExecutions',
      parent: 'instances',
      path: '/jobExecutions',
      component: jobExecutionsEndpoint,
      label: 'Job Executions',
      order: 1000
    });
  }
});

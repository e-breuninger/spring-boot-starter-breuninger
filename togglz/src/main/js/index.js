/* global SBA */

import togglzEndpoint from './togglz-endpoint';
import togglzView from './togglz-view';

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

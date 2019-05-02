// TODO(BS): Endpoint and View are not allowed to have same path or the wrong menu items will be highlighted
import togglzView from './togglz-view';
import togglzEndpoint from './togglz-endpoint';

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'togglz',
      path: '/togglz',
      component: togglzView,
      label: 'Togglz',
      order: 1000
    });
  }
});

SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/togglz',
      parent: 'instances',
      path: '/togglz',
      component: togglzEndpoint,
      label: 'Togglz',
      order: 1000,
      isEnabled: ({instance}) => instance.hasEndpoint('togglz')
    });
  }
});

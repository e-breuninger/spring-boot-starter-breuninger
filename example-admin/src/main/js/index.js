/* global SBA */
import custom from './custom';
import customEndpoint from './custom-endpoint';

// tag::customization-ui-toplevel[]
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'custom',  //<1>
      path: '/custom', //<2>
      component: custom, //<3>
      label: 'Custom', //<4>
      order: 1000, //<5>
    });
  }
});
// end::customization-ui-toplevel[]

// tag::customization-ui-endpoint[]
SBA.use({
  install({viewRegistry}) {
    viewRegistry.addView({
      name: 'instances/custom',
      parent: 'instances', // <1>
      path: 'custom',
      component: customEndpoint,
      label: 'Custom',
      order: 1000,
      isEnabled: ({instance}) => instance.hasEndpoint('custom') // <2>
    });
  }
});
// end::customization-ui-endpoint[]

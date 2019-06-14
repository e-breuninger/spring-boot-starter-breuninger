<template>
  <section class="section">
    <div class="container">
      <div v-for="app in apps" :key="app.name" class="card">
        <header class="hero is-primary">
          <h1 class="title is-size-5">{{ app.name }}</h1>
        </header>
        <div class="card-content">
          <table class="table">
            <tbody>
            <tr>
              <td>
                <div v-for="instance in app.instances"
                     :key="instance.registration.serviceUrl">
                  <i
                    :class="instance.statusInfo.status === 'UP' ? checkIconClass : instance.statusInfo.status === 'OFFLINE' ? minusIconClass : timesIconClass"
                  />
                </div>
              </td>
              <td>
                <div v-for="instance in app.instances"
                     :key="instance.registration.serviceUrl"
                     class="link has-text-primary"
                     @click.stop="showDetails(instance)">{{ instance.registration.serviceUrl }}
                </div>
              </td>
              <td>
                <iframe v-if="app.instances[0].statusInfo.status !== 'OFFLINE'"
                        :src="app.instances[0].registration.serviceUrl + 'jobs'"
                        ref="iframe"
                />
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
  import iFrameResize from 'iframe-resizer';

  export default {
    props: {
      applications: {
        type: Array,
        required: true
      }
    },
    data: () => ({
      apps: '',
      checkIconClass: 'fas fa-check has-text-success',
      minusIconClass: 'fas fa-minus-circle has-text-grey',
      timesIconClass: 'fas fa-times-circle has-text-danger'
    }),
    async created() {
      this.apps = await this.applications;
    },
    methods: {
      showDetails(instance) {
        this.$router.push({
          name: 'instances/details',
          params: {instanceId: instance.id}
        });
      },
      iFrameResize
    },
    updated() {
      if (this.$refs.iframe) {
        this.$refs.iframe.forEach((iFrame) => {
          iFrame.onload = () => {
            window.iFrameResize(iFrame);
          };
        });
      }
    }
  };
</script>

<style scoped>
  @import "https://use.fontawesome.com/releases/v5.8.1/css/all.css";

  iframe {
    min-width: 100%;
    width: 100%;
  }

  .title {
    margin: .75rem 0;
  }

  td {
    padding: .5em .75em;
    position: relative;
    width: 1%;
  }

  td:last-child {
    width: 100%;
  }

  .link {
    cursor: pointer;
  }

  .link:hover {
    color: #2bb78b;
  }
</style>

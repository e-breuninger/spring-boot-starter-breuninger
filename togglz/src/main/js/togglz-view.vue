<template>
  <section class="section">
    <div class="container">
      <div
        v-for="app in apps"
        :key="app.name"
        class="card">
        <header class="hero is-primary">
          <h1 class="title is-size-5">{{ app.name }}</h1>
        </header>
        <div class="card-content">
          <table>
            <tbody>
              <tr
                v-for="instance in app.instances"
                :key="instance.registration.serviceUrl">
                <td>
                  <i :class="instance.statusInfo.status === 'UP' ? faCheckClass: faMinusCircle" />
                </td>
                <td>
                  <div class="link" @click.stop="showDetails(instance)">{{ instance.registration.serviceUrl }}</div>
                </td>
                <td>
                  <iframe
                    v-if="instance.statusInfo.status === 'UP'"
                    :src="instance.registration.serviceUrl + 'togglz'"/>
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
  export default {
    props: {
      applications: { //<1>
        type: Array,
        required: true
      }
    },
    methods: {
      stringify: JSON.stringify
    },
    data: () => ({
      apps: '',
      faCheckClass: 'fas fa-check',
      faMinusCircle: 'fas fa-minus-circle'
    }),
    async created() {
      this.apps = await this.applications;
    },
    methods: {
      showDetails(instance) {
        this.$router.push({name: 'instances/details', params: {instanceId: instance.id}});
      }
    }
  };
</script>

<style>
  @import "https://use.fontawesome.com/releases/v5.8.1/css/all.css";

  iframe {
    width: 800px;
    height: 350px !important;
  }

  .title {
    margin: .75rem 0;
  }

  td {
    padding: 0 10px;
  }

  .fa-check {
    color: #23d160;
  }

  .fa-minus-circle {
    color: #7a7a7a;
  }

  .link {
    color: #00d1b2;
    cursor: pointer;
  }
</style>

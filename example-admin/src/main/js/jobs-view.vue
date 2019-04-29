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
                  <a :href="instance.registration.serviceUrl">{{ instance.registration.serviceUrl }}</a>
                </td>
                <td>
                  <iframe
                    v-if="instance.statusInfo.status === 'UP'"
                    :src="instance.registration.serviceUrl + 'jobs'"/>
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
    }
  };
</script>

<style>
  @import "https://use.fontawesome.com/releases/v5.8.1/css/all.css";

  iframe {
    width: 800px;
    height: 350px;
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
</style>

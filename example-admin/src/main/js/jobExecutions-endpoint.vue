<template>
  <section class="jobExecutions section">
    <iframe :src="text"/>
  </section>
</template>

<script>
  export default {
    props: {
      instance: { //<1>
        type: Object,
        required: true
      }
    },
    data: () => ({
      text: ''
    }),
    async created() {
      this.text = await this.instance.registration.serviceUrl + 'jobexecutions';
    }
  };
</script>

<style>
  .jobExecutions {
    width: 100%;
    height: 100%;
  }

  iframe {
    width: 100%;
    height: 100%;
  }
</style>

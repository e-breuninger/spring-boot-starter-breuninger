<template>
  <section class="jobs section">
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
      this.text = await this.instance.registration.serviceUrl + 'jobs';
    }
  };
</script>

<style>
  .jobs {
    width: 100%;
    height: 100%;
  }

  iframe {
    width: 100%;
    height: 100%;
  }
</style>

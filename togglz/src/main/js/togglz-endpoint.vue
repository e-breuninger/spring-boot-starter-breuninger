<template>
  <section class="togglz section">
    <iframe :src="text" />
  </section>
</template>

<script>
  export default {
    props: {
      instance: {
        type: Object,
        required: true
      }
    },
    data: () => ({
      text: ''
    }),
    async created() {
      this.text = `${await this.instance.registration.serviceUrl}togglz`;
    }
  };
</script>

<style scoped>
  .togglz {
    width: 100%;
    height: 100%;
  }

  iframe {
    width: 100%;
    height: 100%;
  }
</style>

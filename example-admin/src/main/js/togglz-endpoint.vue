<template>
  <div class="togglz">
    <iframe :src="text"/>
  </div>
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
      this.text = await this.instance.registration.serviceUrl + 'togglz';
    }
  };
</script>

<style>
  .togglz {
    width: 100%;
    height: 100%;
  }

  iframe {
    width: 100%;
    height: 100%;
  }
</style>

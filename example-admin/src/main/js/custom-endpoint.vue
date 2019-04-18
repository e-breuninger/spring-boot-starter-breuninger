<template>
<div class="custom">
  <p>Instance: <span v-text="instance.id" /></p>
  <p>Output: <span v-text="text" /></p>
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
      const response = await this.instance.axios.get('actuator/custom'); //<2>
      this.text = response.data;
    }
  };
</script>

<style>
  .custom {
    font-size: 20px;
    width: 80%;
  }
</style>

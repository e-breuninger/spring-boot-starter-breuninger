<template>
  <pre v-text="stringify(applications, null, 4)"/>
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
    }
  };
</script>

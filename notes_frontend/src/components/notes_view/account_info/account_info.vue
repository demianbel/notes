<template>
  <div id="secure">
    <p> {{ this.$store.state.auth.username }} </p>
  </div>
</template>

<script>
  export default {
    name: 'AccountInfo',
    data() {
      return {};
    }
  }
</script>

<style scoped>
  #secure {
    padding: 6px 8px 6px 16px;
    background-color: #091807;
    border: 1px solid #0b0305;
    font-size: 25px;
    color: #818181;
  }
</style>

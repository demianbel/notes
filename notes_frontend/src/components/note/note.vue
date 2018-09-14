<template>
  <div id="note">
    <input v-model="this.name" placeholder="note name"/>
    <textarea v-model="this.text" placeholder="note text"></textarea>
  </div>
</template>

<script>
  export default {
    name: 'Note',
    props: ['id', 'initialName', 'initialContent'],
    data() {
      return {
        name: this.initialName,
        text: this.initialContent
      };
    }
  }
</script>

<style scoped>
  #note {
    background-color: #FFFFFF;
    border: 1px solid #CCCCCC;
    padding: 20px;
    margin-top: 10px;
  }
</style>

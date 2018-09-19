<template>
  <div id="note">
    <p>{{noteValue.name}}</p>
    <p>{{noteValue.text}}</p>
  </div>
</template>

<script>

  export default {
    props: ['note'],
    name: 'NotePreview',
    data() {
      return {
        noteValue: {
          name: this.note.name,
          text: this.note.text,
        }
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

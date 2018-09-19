<template>
  <div>
    <h1>Notes</h1>
    <p v-for="note in currentNotes"> {{note.name + note.text}}</p>
  </div>
</template>

<script>

  export default {
    props: ['notes'],
    name: 'NotesList',
    data() {
      return {
        currentNotes: this.notes
      }
    }
  }
</script>

<style scoped>
  p, h1 {
    padding: 6px 8px 6px 16px;
    text-decoration: none;
    font-size: 25px;
    color: #818181;
    display: block;
  }

  /* When you mouse over the navigation links, change their color */
  p:hover {
    color: #f1f1f1;
  }
</style>

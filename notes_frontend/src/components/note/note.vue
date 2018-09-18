<template>
  <div id="note">
    <input v-model="noteValue.name" placeholder="note name"/>
    <textarea v-model="noteValue.text" placeholder="note text"></textarea>
    <button @click="saveNote">Save</button>
  </div>
</template>

<script>
  import axios from 'axios';

  export default {
    name: 'Note',
    data() {
      return {
        noteValue: {
          name: '',
          text: '',
          nodeId: null,
          tags: []
        }
      };
    },
    methods: {
      saveNote: function () {
        axios.post(`http://localhost:8080/notes/rest/note`, this.noteValue,{
          headers: {Authorization: this.$store.state.auth.authToken}
        }).then(response => {
          this.$notify({
            group: 'general_notifications',
            title: 'Success',
            text: 'Note saved!',
            type: 'info'
          });
          this.$router.replace({name: 'Secure'})
        }).catch(e => {
          this.$notify({
            group: 'general_notifications',
            title: 'Error',
            text: 'Try again!',
            type: 'error'
          });
        });
      }
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

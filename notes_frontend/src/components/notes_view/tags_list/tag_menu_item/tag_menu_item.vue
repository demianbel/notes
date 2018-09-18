<template>
  <div id="container">
    <div id="left">
      <p>{{ itemName }}</p>
    </div>
    <div id="middle">
      <button @click="$emit('delete')">delete</button>
    </div>
    <div id="right" >
      <button @click="$emit('add')">add note</button>
    </div>
  </div>
</template>

<script>
  import axios from 'axios';

  export default {
    name: 'TagMenuItem',
    props: ['itemName'],
    data() {
      return {
        newTagName: '',
        tags: []
      }
    },
    created: function () {
      this.retrieveTags();
    },
    methods: {
      addNewTag: function () {
        axios.post(`http://localhost:8080/notes/rest/tag`, {name: this.newTagName}, {
          headers: {Authorization: this.$store.state.auth.authToken}
        }).then(response => {
          let index, len;
          let found = false;
          for (index = 0, len = this.tags.length; index < len; ++index) {
            if (this.tags[index].id === response.data.id) {
              found = true;
            }
          }
          if (!found) {
            this.tags.push(response.data);
            this.$notify({
              group: 'general_notifications',
              title: 'Success',
              text: 'Tag saved!',
              type: 'info'
            });
          } else {
            this.$notify({
              group: 'general_notifications',
              title: 'Success',
              text: 'Tag already exists!',
              type: 'info'
            });
          }
        }).catch(e => {
          this.$notify({
            group: 'general_notifications',
            title: 'Error',
            text: 'Try again!',
            type: 'error'
          });
        });
      },
      retrieveTags: function () {
        axios.get(`http://localhost:8080/notes/rest/tag/search/all`, {
          headers: {Authorization: this.$store.state.auth.authToken}
        }).then(response => {
          this.tags = response.data;
        }).catch(e => {
        });
      },
      deleteTag: function (id) {
        axios.delete(`http://localhost:8080/notes/rest/tag/?id=` + id, {
          headers: {Authorization: this.$store.state.auth.authToken}
        }).then(response => {
          let index, len;
          let foundIndex = -1;
          for (index = 0, len = this.tags.length; index < len; ++index) {
            if (this.tags[index].id === response.data.id) {
              foundIndex = index;
            }
          }
          if (foundIndex === -1){
            this.$notify({
              group: 'general_notifications',
              title: 'Success',
              text: 'Tag already deleted!',
              type: 'info'
            });
          } else {
            this.$delete(this.tags, foundIndex);
          }
        }).catch(e => {
        });
      }
    }
  }
</script>

<style scoped>
  #container {
    width:100%;
    border: #000088 solid;
  }
  #left, #middle, #right {
    display: inline-block;
    *display: inline;
    vertical-align: center;
    font-size: 11px;
    cursor: pointer;
  }
  #left {
    width: 58%;
  }
  #middle {
    width: 18%;
  }
  #right {
    width: 18%;
  }

  p {
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

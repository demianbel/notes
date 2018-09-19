<template>
  <div>
    <div>
      <side-bar class="sidebar-menu" @tagsSelected="selected = 'tags'" @dirSelected="selected = 'nodes'"></side-bar>
    </div>
    <div class="additional-sidebar" v-if="selected === 'nodes'">Notes</div>
    <tags-list class="additional-sidebar" v-if="selected === 'tags'" @select="selectTag">Tags</tags-list>
    <notes-list :notes="selectedNotes" class="additional-sidebar" v-if="selected === 'notes'"></notes-list>
    <div class="main-view" :class="{'main-view-big' : selected === 'none'}">

    </div>

  </div>
</template>

<script>
  import axios from 'axios';
  import SideBar from "./side_bar/side_bar";
  import TagsList from "./tags_list/tags_list";
  import NotesList from "./notes_list/notes_list";

  export default {
    name: 'NotesView',
    components: {NotesList, TagsList, SideBar},
    data() {
      return {
        selected: 'none',
        selectedNotes: [],
        selectedNote: null
      }
    },
    methods: {
      selectTag: function (id) {
        axios.get(`http://localhost:8080/notes/rest/note/find/tag?tagId=` + id, {
          headers: {Authorization: this.$store.state.auth.authToken}
        }).then(response => {
          this.selectedNotes = response.data;
          if (this.selectedNotes.length <= 0) {
            this.$notify({
              group: 'general_notifications',
              title: 'Warning',
              text: 'There are no notes for this tag!',
              type: 'warn'
            });
          } else {
            this.selectedNote = this.selectedNotes[0];
            this.selected = 'notes';
          }
        }).catch(e => {
          this.selectedNotes = [];
          this.$notify({
            group: 'general_notifications',
            title: 'Error',
            text: 'Try again!',
            type: 'error'
          });
          console.log(e);
        });

      },
      selectNode: function (id) {
        this.selected = 'notes';
        console.log('select node with id = ' + id);
      }
    }
  }
</script>

<style scoped>
  /* The sidebar menu */
  .sidebar-menu {
    height: 100%; /* Full-height: remove this if you want "auto" height */
    width: 20%; /* Set the width of the sidebar */
    position: fixed; /* Fixed Sidebar (stay in place on scroll) */
    z-index: 1; /* Stay on top */
    top: 0; /* Stay at the top */
    left: 0;
    background-color: #111; /* Black */
    overflow-x: hidden; /* Disable horizontal scroll */
    padding-top: 5px;
  }

  .additional-sidebar {
    height: 100%; /* Full-height: remove this if you want "auto" height */
    width: 20%; /* Set the width of the sidebar */
    position: fixed; /* Fixed Sidebar (stay in place on scroll) */
    z-index: 1; /* Stay on top */
    top: 0; /* Stay at the top */
    left: 20%;
    border-left: #004444 solid;
    background-color: #111; /* Black */
    overflow-x: hidden; /* Disable horizontal scroll */
    padding-top: 5px;
  }

  .main-view {
    height: 100%; /* Full-height: remove this if you want "auto" height */
    position: fixed; /* Fixed Sidebar (stay in place on scroll) */
    z-index: 1; /* Stay on top */
    top: 0; /* Stay at the top */
    border-left: #004444 solid;
    background-color: #111; /* Black */
    overflow-x: hidden; /* Disable horizontal scroll */
    padding-top: 5px;
    width: 60%;
    left: 40%;
  }

  .main-view-big {
    width: 80% !important;
    left: 20% !important;
  }
</style>

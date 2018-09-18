<template>
  <div>
  <div>
    <side-bar class="sidebar-menu" @notesSelected="selected = 'notes'"  @tagsSelected="selected = 'tags'"></side-bar>
  </div>
  <div class="additional-sidebar" v-if="selected === 'notes'">Notes</div>
  <tags-list class="additional-sidebar" v-if="selected === 'tags'">Tags</tags-list>
  </div>
</template>

<script>
  import SideBar from "./side_bar/side_bar";
  import TagsList from "./tags_list/tags_list";

  export default {
    name: 'NotesView',
    components: {TagsList, SideBar},
    data() {
      return {
        selected: 'none'
      }
    },
    methods: {
      doFromMenu: function () {
        console.log('hello');
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
</style>

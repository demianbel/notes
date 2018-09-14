<template>
  <div id="app">
    <router-view></router-view>
    <notifications position="top center" group="general_notifications"/>
  </div>
</template>

<script>
  import login from './components/login/login'
  export default {
    name: 'App',
    computed: {
      authorized: function () {
        return this.$store.state.auth.authorized !== 'success';
      }
    },
    methods: {
      logout() {
        this.$store.commit("auth/logout");
      }
    },
    components: {
      login
    }
  }
</script>

<style>
  body {
    background-color: #F0F0F0;
  }

  h1 {
    padding: 0;
    margin-top: 0;
  }

  #app {
    width: 1024px;
    margin: auto;
  }
</style>

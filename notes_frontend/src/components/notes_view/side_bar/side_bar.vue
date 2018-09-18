<template>
  <div>
      <account-info class="account_info"></account-info>
      <p @click="$emit('notesSelected')">Notes</p>
      <p @click="$emit('tagsSelected')">Tags</p>
      <p @click="logout">Log out</p>
  </div>
</template>

<script>
  import AccountInfo from "../account_info/account_info";
  export default {
    name: 'SideBar',
    components: {AccountInfo},
    data() {
      return {}
    },
    methods: {
      logout: function () {
        this.$store.commit('auth/logout');
        this.$router.replace({name: "Login"});
      }
    }
  }
</script>

<style scoped>
  .account_info {
    margin: 6px 8px 6px 16px;
  }

  /* The navigation menu links */
  p {
    padding: 6px 8px 6px 32px;
    text-decoration: none;
    font-size: 25px;
    color: #818181;
    display: block;
    cursor: pointer;
  }

  /* When you mouse over the navigation links, change their color */
  p:hover {
    color: #f1f1f1;
  }

</style>

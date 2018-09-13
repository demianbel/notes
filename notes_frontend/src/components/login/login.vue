<template>
  <div id="login">
    <h1>Login</h1>
    <input type="text" name="username" v-model="input.username" placeholder="Username"/>
    <input type="password" name="password" v-model="input.password" placeholder="Password"/>
    <button type="button" v-on:click="login()">Login</button>
    <button type="button" v-on:click="signUp()">Sign Up</button>
  </div>
</template>

<script>

  export default {
    name: 'Login',
    data() {
      return {
        input: {
          username: "",
          password: ""
        },
      }
    },
    methods: {
      signUp() {
        this.$router.replace({name : 'SignUp'})
      },
      login() {
        this.$store.dispatch("auth/authorize",this.input)
      }
    }
  }
</script>

<style scoped>
  #login {
    width: 500px;
    border: 1px solid #CCCCCC;
    background-color: #FFFFFF;
    margin: 200px auto auto;
    padding: 20px;
  }
</style>

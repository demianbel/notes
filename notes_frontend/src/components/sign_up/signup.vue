<template>
  <div id="signUp">
    <h1>Sign Up</h1>
    <login for="login">Name:</login>
    <input id="login" type="text" name="username" v-model="signUpBody.name" placeholder="Username"/>
    <login for="email">E-mail:</login>
    <input id="email" type="text" name="email" v-model="signUpBody.email" placeholder="Email"/>
    <login for="password">Password:</login>
    <input id="password" type="password" name="password" v-model="signUpBody.password" placeholder="Password"/>
    <button type="button" v-on:click="signUp()">Sign Up</button>
  </div>
</template>

<script>
  import axios from 'axios'

  export default {
    name: 'SignUp',
    data() {
      return {
        signUpBody: {
          name: "",
          email: "",
          password: ""
        }
      }
    },
    methods: {
      signUp() {
        axios.post(`http://localhost:8080/notes/rest/account/signup`, this.signUpBody)
          .then(response => {
            this.$notify({
              group: 'general_notifications',
              title: 'Success',
              text: 'Sign up success!',
              type: 'info'
            });
            this.$router.replace({name: "Login"});
          })
          .catch(e => {
            this.$notify({
              group: 'general_notifications',
              title: 'Error',
              text: 'Try again!',
              type: 'error'
            });
          })
      }
    }
  }
</script>

<style scoped>
  #signUp {
    width: 500px;
    border: 1px solid #CCCCCC;
    background-color: #FFFFFF;
    margin: 200px auto auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
</style>

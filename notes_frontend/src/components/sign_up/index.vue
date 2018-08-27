<template>
  <div id="signUp">
    <h1>Sign Up</h1>
    <input type="text" name="username" v-model="signUpBody.name" placeholder="Username"/>
    <input type="text" name="email" v-model="signUpBody.email" placeholder="Email"/>
    <input type="password" name="password" v-model="signUpBody.password" placeholder="Password"/>
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
        axios.post(`http://localhost:8080/notes/rest/account/signup`, {body: this.signUpBody})
          .then(response => {

          })
          .catch(e => {

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
    margin: auto;
    margin-top: 200px;
    padding: 20px;
    display: flex;
    flex-direction: column;
  }
</style>

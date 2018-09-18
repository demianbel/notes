<template>
  <div id="signUp">
    <h1>Sign Up</h1>
    <label for="login">Name:</label>
    <input id="login" type="text" name="username" v-model="signUpBody.name" placeholder="Username"/>
    <label for="email">E-mail:</label>
    <input id="email" type="text" name="email" v-model="signUpBody.email" placeholder="Email"/>
    <label for="password">Password:</label>
    <input id="password" type="password" name="password" v-model="signUpBody.password" placeholder="Password"/>
    <button type="button" @click="signUp()">Sign Up</button>
  </div>
</template>

<script>
  import axios from 'axios';

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
    computed: {
      credentials: function () {
        return {
          username: this.signUpBody.name,
          password: this.signUpBody.password,
          vue: this
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
            this.$store.dispatch("auth/authorize",this.credentials)
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

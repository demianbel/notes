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
  import axios from 'axios'

  export default {
    name: 'Login',
    data() {
      return {
        input: {
          username: "",
          password: ""
        },
        token: "",
        refresh_token: ""
      }
    },
    methods: {
      signUp() {
        this.$router.replace({name : 'SignUp'})
      },
      login() {
        let formData = new FormData();
        formData.append('username', this.input.username);
        formData.append('password', this.input.password);
        formData.append('grant_type', 'password');
        axios({
          url: `http://localhost:8080/oauth/token`,
          method: 'post',
          auth: {
            username: "notes-frontend",
            password: "notes-frontend"
          },
          data: formData,
        })
          .then(response => {
            this.token = 'Bearer ' + response.data.access_token;
            this.refresh_token = response.data.refresh_token;
            this.$router.replace({ name: "Secure" });
          })
          .catch(e => {
            this.$notify({
              group: 'general_notifications',
              title: 'Error',
              text: 'Wrong login or password!',
              type: 'error'
            });
          })
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

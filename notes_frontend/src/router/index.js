import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/login/login'
import Secure from '../components/secure/secure'
import SignUp from '../components/sign_up/signup'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: {
        name: "Login"
      }
    },
    {
      path: "/login",
      name: "Login",
      component: Login
    },
    {
      path: "/secure",
      name: "Secure",
      component: Secure
    },
    {
      path: "/signUp",
      name: "SignUp",
      component: SignUp
    }
  ]
})

import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/login'
import Secure from '../components/secure'
import SignUp from '../components/sign_up'

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

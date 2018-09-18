import Vue from 'vue';
import Router from 'vue-router';
import Login from '../components/login/login';
import Secure from '../components/notes_view/account_info/account_info';
import SignUp from '../components/sign_up/signup';
import Note from '../components/note/note'
import NotesView from '../components/notes_view/notes_view'

Vue.use(Router);

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
      component: Login,
    },
    {
      path: "/account_info",
      name: "AccountInfo",
      component: Secure
    },
    {
      path: "/signUp",
      name: "SignUp",
      component: SignUp
    },
    {
      path: "/editNote",
      name: "EditNote",
      component: Note
    },
    {
      path: "/notesView",
      name: "NotesView",
      component: NotesView
    }
  ]
});

import axios from "axios";

const state = {
  username: "",
  authToken: "",
  refresh_token: "",
  authorized: 'false'
};

const getters = {};

const actions = {
  authorize: function (context, credentials) {
    if (state.authorized !== 'in progress') {
      context.commit('setStatus', 'in progress');
      let formData = new FormData();
      formData.append('username', credentials.username);
      formData.append('password', credentials.password);
      formData.append('grant_type', 'password');
      axios({
        url: `http://localhost:8080/oauth/token`,
        method: 'post',
        auth: {
          username: "notes-frontend",
          password: "notes-frontend"
        },
        data: formData,
      }).then(response => {
        context.commit('setToken', 'Bearer ' + response.data.access_token);
        context.commit('setRefreshToken', response.data.refresh_token);
        context.commit('setStatus', 'success');
        context.commit('setUsername', credentials.username)
        credentials.vue.$router.replace({name: 'NotesView'});
      }).catch(e => {
        context.commit('setStatus', 'false');
        credentials.vue.$notify({
          group: 'general_notifications',
          title: 'Error',
          text: 'Try again!',
          type: 'error'
        });
      });
    }
  }
};

const mutations = {
  setToken(state, token) {
    state.authToken = token
  },
  setRefreshToken(state, refreshToken) {
    state.refresh_token = refreshToken
  },
  setStatus(state, status) {
    state.authorized = status;
  },
  setUsername(state, username) {
    state.username = username;
  },
  logout() {
    state.authToken = '';
    state.refresh_token = '';
    state.authorized = 'false';
    state.username = ''
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}

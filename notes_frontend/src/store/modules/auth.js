import axios from "axios";

const state = {
  authToken: "",
  refresh_token: "",
  authorized: 'false'
};

const getters = {};

const actions = {
  authorize: function(context, credentials) {
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
        credentials.vue.$router.push("/secure");
      }).catch(e => {
        context.commit('setStatus', 'false');
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
  logout() {
    state.authToken = '';
    state.refresh_token = '';
    state.authorized = 'false';
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}

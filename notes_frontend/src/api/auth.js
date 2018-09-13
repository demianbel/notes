import axios from 'axios'

export default {
  login(credentials) {
    let formData = new FormData();
    formData.append('username', credentials.username);
    formData.append('password', credentials.password);
    formData.append('grant_type', 'password');
    return axios({
      url: `http://localhost:8080/oauth/token`,
      method: 'post',
      auth: {
        username: "notes-frontend",
        password: "notes-frontend"
      },
      data: formData,
    });


  }
}

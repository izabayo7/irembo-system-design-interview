import axios from "axios";

export const API_URL =
  import.meta.env.VITE_API_URL;


class AppServices {
  login(body) {
    return axios.post(`${API_URL}/auth/signin`, body);
  }

  verifyOTP(body) {
    return axios.post(`${API_URL}/auth/verifyOTP`, body);
  }

  logout(body) {
    return axios.post(`${API_URL}/auth/signOut`);
  }

  updateUser(body, id) {
    return axios.put(`${API_URL}/users/${id}`, body);
  }

  getCurrentUser() {
    return axios.get(`${API_URL}/auth/currentUser`);
  }

  signup(body) {
    return axios.post(`${API_URL}/auth/signup`, body);
  }

  deleteUser() {
    return axios.delete(`${API_URL}/users`);
  }

  uploadIdentificationDocuments(body) {
    return axios.put(`${API_URL}/users/upload/verification`, body);
  }

  verifyAccount(body) {
    return axios.put(`${API_URL}/users/verificationStatus
`, body);
  }

  resetVerificationStatus(id) {
    return axios.put(`${API_URL}/users/resetVerificationStatus/${id}`);
  }

  setPassword(id, newPassword) {
    return axios.put(`${API_URL}/users/${id}/setPassword`, { newPassword });
  }

  forgotPassword(body) {
    return axios.post(`${API_URL}/auth/forgotPassword`, body);
  }

  verifyToken(token) {
    return axios.post(`${API_URL}/auth/verifyToken`, { token });
  }

  signInToken(body) {
    return axios.post(`${API_URL}/auth/signInToken`, body);
  }

  getUsers(currentPage, itemsPerPage, verificationStatus) {
    return axios.get(`${API_URL}/users?page=${currentPage}&limit=${itemsPerPage}${verificationStatus == null ? "" : `&verificationStatus=${verificationStatus}`}`);
  }

}

export default new AppServices();
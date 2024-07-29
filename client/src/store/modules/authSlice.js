import {
  createSlice,
  createAsyncThunk
} from "@reduxjs/toolkit";
import axios from "axios";
import AppServices from "../../services";

const initialState = {
  user: null,
  isLoggedIn: false,
};

export const loadUser = createAsyncThunk(
  'auth/loadUser',
  async () => {
    const token = localStorage.getItem("user");
    if (token) {
      const bearer = JSON.parse(token || "{}");
      console.dir({ bearer })
      axios.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${bearer?.accessToken}`;

      return AppServices.getCurrentUser().then(({
        data
      }) => {
        return data;
      }).catch((e) => {
        localStorage.removeItem('user');
      })

    } else {
      throw new Error("No token found");
    }
  }
);

export const AuthSlice = createSlice({
  name: "Auth",
  initialState,
  reducers: {
    login: (state, action) => {
      state.user = action.payload;
      state.isLoggedIn = true;
    },
    logout: (state) => {
      AppServices.logout()
      localStorage.removeItem("user");
      state.user = undefined;
      state.isLoggedIn = false;
    },
    setUser: (state, action) => {
      state.user = action.payload;
      state.isLoggedIn = true;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loadUser.pending, (state) => {
        state.user = null;
      })
      .addCase(loadUser.fulfilled, (state, action) => {
        state.user = {
          ...action.payload
        };
        if (action.payload) state.isLoggedIn = true;
      })
      .addCase(loadUser.rejected, (state) => {
        state.user = null;
        state.isLoggedIn = 0;
      });
  },
});

export const {
  login,
  logout,
  setUser
} = AuthSlice.actions;

export const selectUser = (state) => state.auth.user;
export const selectIsLoggedIn = (state) => state.auth.isLoggedIn;

export default AuthSlice.reducer;
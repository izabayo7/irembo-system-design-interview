import {
  createSlice
} from "@reduxjs/toolkit";

const initialState = {
  users: [],
  isUsersLoaded: false,
};

export const AuthSlice = createSlice({
  name: "Users",
  initialState,
  reducers: {
    setUsers: (state, action) => {
      state.users = action.payload;
      state.isUsersLoaded = true;
    },
    addUser: (state, action) => {
      state.users = [...state.users, action.payload];
    },
    updateUser: (state, action) => {
      for (const i in state.users) {
        if (state.users[i]._id === action.payload._id) {
          state.users[i] = action.payload;
        }
      }
    },
    removeUser: (state, action) => {
      state.users = state.users.filter((user) => user._id !== action.payload);
    },
  },
});

export const {
  setUsers,
  addUser,
  removeUser,
  updateUser
} = AuthSlice.actions;

export const selectUsers = (state) => state.users.users;
export const isUsersLoaded = (state) => state.users.isUsersLoaded;

export default AuthSlice.reducer;
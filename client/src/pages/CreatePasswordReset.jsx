import { useEffect, useState, useRef } from 'react'

import logo from '../assets/images/logo.png'
import '../assets/scss/login.scss'
import toast from 'react-hot-toast';
import AppServices from "../services";
import Modal from '../components/Modal';

import {
  loadUser,
  selectIsLoggedIn,
} from '../store/modules/authSlice';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';

function CreatePasswordReset() {
  const [email, SetEmail] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [password, SetPassword] = useState('')
  const [user, setUser] = useState({})
  const dispatch = useDispatch()

  const childRef = useRef(null);

  dispatch(loadUser());
  const navigate = useNavigate();
  const isLoggedIn = useSelector(selectIsLoggedIn);

  useEffect(() => {
    if (isLoggedIn) {
      navigate('/');
    } else {
      dispatch(loadUser());
    }
  }, [isLoggedIn])

  const onChangeEmail = (e) => {
    SetEmail(e.target.value);
  }

  const handleCreatePasswordReset = (e) => {
    e.preventDefault();

    if (submitted) return;
    setSubmitted(true);

    toast.promise(
      AppServices.login({ email, password }),
      {
        loading: 'Logging in ...',
        success: (response) => {
          if (response.data.token) {
            localStorage.setItem("user", JSON.stringify(response.data));
            dispatch(loadUser())
          }
          navigate('/');
          setSubmitted(false);
          return "Logged in successfully";
        },
        error: (error) => {
          const message =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
          setSubmitted(false);
          return message;
        },
      }
    );
  }
  const handleRegister = (e) => {
    e.preventDefault();

    if (user.password !== user.confirmPassword)
      return toast.error("passwords should match");

    if (submitted) return;
    setSubmitted(true);

    toast.promise(
      AppServices.register({ ...user, confirmPassword: undefined }),
      {
        loading: 'Creating password reset ...',
        success: () => {
          setSubmitted(false);
          return "Password reset successfully created";
        },
        error: (error) => {
          const message =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
          setSubmitted(false);
          if (message.includes("required pattern"))
            if (message.includes("phone")) return "invalid phone number";
            else return "invalid nationalId"
          return message;
        },
      }
    );
  }


  return (
    <div className="bg-primary h-screen flex justify-center">
      <div className="form bg-main flex max-w-md w-screen h-max justify-center p-8 m-auto">
        <form className='text-center' onSubmit={handleCreatePasswordReset}>
          <img src={logo} className="mb-9 mx-auto" alt="" />
          <div className="title mb-8">Enter your email and we will send you an <br />
            <div className="small">email with a password reset link.</div></div>
          <div className="input-container  mb-8">
            <input onChange={onChangeEmail} className='bg' placeholder="email" type="text" name="" id="" />
          </div>
          <div className="input-container  mb-8">
            <input className='submit bg-primary text-main cursor-pointer' type="submit" value="send" />
          </div>
          <div onClick={() => {
            navigate('/login')
          }} className="input-container  mb-8 text-primary cursor-pointer">
            Remembered your password?
          </div>
        </form>
      </div>
    </div>
  )
}

export default CreatePasswordReset


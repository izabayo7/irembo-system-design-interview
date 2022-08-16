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
import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';

function UpdatePasswordReset() {
  const [email, SetEmail] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [password, SetPassword] = useState('')
  const [user, setUser] = useState({})
  const dispatch = useDispatch()

  const childRef = useRef(null);

  dispatch(loadUser());
  const navigate = useNavigate();
  const isLoggedIn = useSelector(selectIsLoggedIn);
  const [pageStatus, setPageStatus] = useState("LOADING");
  const [formData, setFormData] = useState({});
  const { token } = useParams();

  useEffect(() => {
    AppServices.getPasswordReset(token).then(res => {
      if (res.data.expired) {
        setPageStatus("EXPIRED");
      } else if (!res.data.isActive) {
        setPageStatus("INACTIVE");
      } else {
        setPageStatus("LOADED");
      }
    }).catch(err => {
      setPageStatus("ERROR");
    })
  }, [])

  useEffect(() => {
    if (isLoggedIn) {
      navigate('/');
    } else {
      dispatch(loadUser());
    }
  }, [isLoggedIn])

  const handleUpdatePasswordReset = (e) => {
    e.preventDefault();

    if (submitted) return;
    setSubmitted(true);
    const { password, confirmPassword } = formData;

    if (password === "") {
      setSubmitted(false);
      return toast.error("Passwords is required");
    }

    if (confirmPassword === "") {
      setSubmitted(false);
      return toast.error("Confirm Password is required");
    }

    if (password !== confirmPassword) {
      setSubmitted(false);
      return toast.error("Passwords do not match");
    }


    toast.promise(
      AppServices.updatePasswordReset({ token, password: formData.password }),
      {
        loading: 'Resetting password ...',
        success: (response) => {
          if (response.data.token) {
            localStorage.setItem("user", JSON.stringify(response.data));
            dispatch(loadUser())
          }
          navigate('/login');
          setSubmitted(false);
          return "Password reset successful";
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

  return (
    <div className="bg-primary h-screen flex justify-center">
      <div className="form bg-main flex max-w-md w-screen h-max justify-center p-8 m-auto">
        {pageStatus === "LOADED" ? <form className='text-center' onSubmit={handleUpdatePasswordReset}>
          <img src={logo} className="mb-9 mx-auto" alt="" />
          <div className="title mb-8">Create a new password<br />
          </div>
          <div className="input-container  mb-8">
            <input onChange={(e) => { setFormData({ ...formData, password: e.target.value || "" }) }} className='bg' placeholder='password' type="password" name="" id="" />
          </div>
          <div className="input-container  mb-8">
            <input onChange={(e) => { setFormData({ ...formData, confirmPassword: e.target.value || "" }) }} placeholder='confirm password' type="password" id="confirmpassword" className='bg' />
          </div>
          <div className="input-container  mb-8">
            <input className='submit bg-primary text-main cursor-pointer' type="submit" value="submit" />
          </div>
        </form> :
          pageStatus === "LOADING" ? <div className="text-center">
            <div className="title mb-8">Loading...</div>
          </div> :
            pageStatus === "EXPIRED" ? <div className="text-center">
              <div className="title mb-8">Password reset link has expired</div>
              <div onClick={() => {
                navigate('/login')
              }} className="input-container  mb-8 text-primary cursor-pointer">
                Back to login
              </div>
            </div> :
              pageStatus === "INACTIVE" ? <div className="text-center">
                <div className="title mb-8">Password reset link has been used</div>
                <div onClick={() => {
                  navigate('/login')
                }} className="input-container  mb-8 text-primary cursor-pointer">
                  Back to login
                </div>
              </div> :
                <div className="text-center">
                  <div className="title mb-8">Invalid token</div>
                  <div onClick={() => {
                    navigate('/login')
                  }} className="input-container  mb-8 text-primary cursor-pointer">
                    Back to login
                  </div>
                </div>}
      </div>
    </div>
  )
}

export default UpdatePasswordReset


import { useEffect, useState, useRef } from "react";

import logo from "../assets/images/logo.png";
import "../assets/scss/login.scss";
import toast from "react-hot-toast";
import AppServices from "../services";

import { loadUser, selectIsLoggedIn } from "../store/modules/authSlice";
import { useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

function SigninWithEmail() {
  const [email, SetEmail] = useState("");
  const [pageStatus, setPageStatus] = useState("LOADING");
  const [submitted, setSubmitted] = useState(false);
  const dispatch = useDispatch();

  dispatch(loadUser());
  const navigate = useNavigate();
  const isLoggedIn = useSelector(selectIsLoggedIn);

  useEffect(() => {
    if (isLoggedIn) {
      navigate("/");
    } else {
      dispatch(loadUser());
    }
  }, [isLoggedIn]);

  const { token } = useParams();

  useEffect(() => {
    if (typeof token == "string") {
      AppServices.verifyToken(token)
        .then((res) => {
          localStorage.setItem("user", JSON.stringify(res.data.token));
          dispatch(loadUser());
        })
        .catch((err) => {
          setPageStatus("ERROR");
        });
    } else {
      setPageStatus("LOADED");
    }
  }, [token]);

  const onChangeEmail = (e) => {
    SetEmail(e.target.value);
  };

  const handleSigninWithEmail = (e) => {
    e.preventDefault();

    if (submitted) return;
    setSubmitted(true);

    if (email === "") {
      setSubmitted(false);
      return toast.error("Email is required");
    }

    toast.promise(AppServices.signInToken({ token: email }), {
      loading: "Logging in ...",
      success: (response) => {
        navigate("/");
        setSubmitted(false);
        return "Login successfully, check your email";
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
    });
  };

  return (
    <div className="bg-primary h-screen flex justify-center">
      <div className="form bg-main flex max-w-md w-screen h-max justify-center p-8 m-auto">
        {pageStatus === "LOADING" ? (
          <div className="text-center">
            <div className="title mb-8">Loading...</div>
          </div>
        ) : pageStatus === "LOADED" ? (
          <form className="text-center" onSubmit={handleSigninWithEmail}>
            <img
              style={{ width: "279px", height: "60px" }}
              src={logo}
              className="mb-9 mx-auto"
              alt=""
            />
            <div className="title mb-8">
              Enter your email and we'll <br /> send you login link.
            </div>
            <div className="input-container  mb-8">
              <input
                onChange={onChangeEmail}
                className="bg"
                placeholder="email"
                type="text"
                name=""
                id=""
              />
            </div>
            <div className="input-container  mb-8">
              <input
                className="submit bg-primary text-main cursor-pointer"
                type="submit"
                value="send"
              />
            </div>
            <div
              onClick={() => {
                navigate("/login");
              }}
              className="input-container  mb-8 text-primary cursor-pointer"
            >
              Want to use your password?
            </div>
          </form>
        ) : (
          <div className="text-center">
            <div className="title mb-8">Invalid login link</div>
            <div
              onClick={() => {
                navigate("/login");
              }}
              className="input-container  mb-8 text-primary cursor-pointer"
            >
              Back to login
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default SigninWithEmail;

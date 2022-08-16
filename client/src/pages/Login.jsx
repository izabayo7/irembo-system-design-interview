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

function Login() {
  const [email, SetEmail] = useState('')
  const [twofactorAuthCode, SetTwofactorAuthCode] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [password, SetPassword] = useState('')
  const [file, setFile] = useState(null)
  const [pageStatus, setPageStatus] = useState("LOGIN");
  const [user, setUser] = useState({
    gender: "MALE",
    maritalStatus: "SINGLE",
    role: "USER",
    nationality: "RWANDAN"
  })
  const dispatch = useDispatch()

  const childRef = useRef(null);

  const toggleModal = () => {
    if (childRef.current)
      childRef.current.toggleModal();

    setFile(null)
  }
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

  useEffect(() => {
    if (pageStatus === "2FA") {
      document.getElementById("code").value = "";
    }
  }, [pageStatus])

  const onChangeEmail = (e) => {
    SetEmail(e.target.value);
  }

  const onChange2FA = (e) => {
    SetTwofactorAuthCode(e.target.value);
  }

  const onChangePassword = (e) => {
    SetPassword(e.target.value);
  }

  const handleLogin = (e) => {
    e.preventDefault();

    if (submitted) return;
    setSubmitted(true);

    if (email === "") {
      setSubmitted(false);
      return toast.error("Email is required");
    }

    if (password === "") {
      setSubmitted(false);
      return toast.error("Passwords is required");
    }

    if (pageStatus === "2FA" && twofactorAuthCode === "") {
      setSubmitted(false);
      return toast.error("twofactorAuthCode is required");
    }

    toast.promise(
      AppServices.login({ email, password, twofactorAuthCode }),
      {
        loading: 'Logging in ...',
        success: (response) => {
          if (response.data.email) {
            setPageStatus("2FA");
            setSubmitted(false);
            return "Check your email to complete login";
          }
          if (response.data.accessToken) {
            localStorage.setItem("user", JSON.stringify(response.data));
            dispatch(loadUser())
          }
          // navigate('/');
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

    const formData = new FormData();
    formData.append("profilePhoto", file);

    for (const key in user) {
      if (key === "confirmPassword") continue;
      formData.append(key, user[key]);
    }

    toast.promise(
      AppServices.register(formData),
      {
        loading: 'Registering ...',
        success: () => {
          toggleModal();
          setSubmitted(false);
          return "Registered successfully";
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

  const handleFileChange = (e) => {
    e.preventDefault();
    setFile(e.target.files[0]);
  }

  return (
    <div className="bg-primary h-screen flex justify-center">
      <div className="form bg-main flex max-w-md w-screen h-max justify-center p-8 m-auto">
        {pageStatus === "LOGIN" ? <form className='text-center' onSubmit={handleLogin}>
          <img style={{ width: '279px', height: '60px' }} src={logo} className="mb-9 mx-auto" alt="" />
          <div className="title mb-8">Welcome to <br />
            <div className="small">User Account Management System</div></div>
          <div className="input-container  mb-8">
            <input onChange={onChangeEmail} className='bg' placeholder="email" type="email" id="email" />
          </div>
          <div className="input-container  mb-8">
            <input onChange={onChangePassword} className='bg' placeholder='password' type="password" id="password" />
          </div>
          <div className="input-container  mb-8">
            <input className='submit bg-primary text-main cursor-pointer' type="submit" value="submit" />
          </div>
          <div onClick={toggleModal} className="input-container  mb-8 text-primary cursor-pointer">
            Don't have an account? Signup
          </div>
          <div onClick={() => {
            navigate('/reset-password')
          }} className="input-container  mb-8 text-primary cursor-pointer">
            Forgot Password?
          </div>
        </form> :
          <form className='text-center' onSubmit={handleLogin}>
            <img style={{ width: '279px', height: '60px' }} src={logo} className="mb-9 mx-auto" alt="" />
            <div className="title mb-8">Enter the 6 digit code <br />
              <div className="small"> which was sent to your email.</div></div>
            <div className="input-container  mb-8">
              <input defaultValue={''} onChange={onChange2FA} className='bg' placeholder="code" type="text" id="code" />
            </div>
            <div className="input-container  mb-8">
              <input className='submit bg-primary text-main cursor-pointer' type="submit" value="send" />
            </div>
            <div onClick={() => {
              SetTwofactorAuthCode("");
              setPageStatus("LOGIN")
            }} className="input-container  mb-8 text-primary cursor-pointer">
              Back
            </div>
          </form>
        }
      </div>
      <Modal ref={childRef} width="767px" children={
        <div>
          <div className="modal-title text-center my-10">
            Signup
          </div>
          <div className="modal-body">
            <form>
              <div className="">
                <div className="px-4 py-5 bg-white sm:p-6">
                  <div className="grid grid-cols-6 gap-6">
                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="first-name" className="block text-sm font-medium text-gray-700">First name`</label>
                      <input onChange={(e) => { setUser({ ...user, firstName: e.target.value || "" }) }} type="text" id="first-name" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="last-name" className="block text-sm font-medium text-gray-700">Last name`</label>
                      <input onChange={(e) => { setUser({ ...user, lastName: e.target.value || "" }) }} type="text" id="last-name" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                      <input onChange={(e) => { setUser({ ...user, email: e.target.value || "" }) }} type="email" id="email" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="gender" className="block text-sm font-medium text-gray-700">Gender</label>
                      <select onChange={(e) => { setUser({ ...user, gender: e.target.value || "" }) }} id="gender" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                      </select>
                    </div>
                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="maritalStatus" className="block text-sm font-medium text-gray-700">Marital status</label>
                      <select onChange={(e) => { setUser({ ...user, maritalStatus: e.target.value || "" }) }} id="maritalStatus" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                        <option value="SINGLE">Single</option>
                        <option value="MARRIED">Married</option>
                        <option value="DIVORCED">Divorced</option>
                        <option value="WIDOWED">Widowed</option>
                      </select>
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="nationality" className="block text-sm font-medium text-gray-700">Nationality</label>
                      <select defaultValue={user.nationality} onChange={(e) => { setUser({ ...user, nationality: e.target.value || "" }) }} id="nationality" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                        <option value="AFGHAN">Afghan</option>
                        <option value="ALBANIAN">Albanian</option>
                        <option value="ALGERIAN">Algerian</option>
                        <option value="AMERICAN">American</option>
                        <option value="ANDORRAN">Andorran</option>
                        <option value="ANGOLAN">Angolan</option>
                        <option value="ANTIGUANS">Antiguans</option>
                        <option value="ARGENTINEAN">Argentinean</option>
                        <option value="ARMENIAN">Armenian</option>
                        <option value="AUSTRALIAN">Australian</option>
                        <option value="AUSTRIAN">Austrian</option>
                        <option value="AZERBAIJANI">Azerbaijani</option>
                        <option value="BAHAMIAN">Bahamian</option>
                        <option value="BAHRAINI">Bahraini</option>
                        <option value="BANGLADESHI">Bangladeshi</option>
                        <option value="BARBADIAN">Barbadian</option>
                        <option value="BARBUDANS">Barbudans</option>
                        <option value="BATSWANA">Batswana</option>
                        <option value="BELARUSIAN">Belarusian</option>
                        <option value="BELGIAN">Belgian</option>
                        <option value="BELIZEAN">Belizean</option>
                        <option value="BENINESE">Beninese</option>
                        <option value="BHUTANESE">Bhutanese</option>
                        <option value="BOLIVIAN">Bolivian</option>
                        <option value="BOSNIAN">Bosnian</option>
                        <option value="BRAZILIAN">Brazilian</option>
                        <option value="BRITISH">British</option>
                        <option value="BRUNEIAN">Bruneian</option>
                        <option value="BULGARIAN">Bulgarian</option>
                        <option value="BURKINABE">Burkinabe</option>
                        <option value="BURMESE">Burmese</option>
                        <option value="BURUNDIAN">Burundian</option>
                        <option value="CAMBODIAN">Cambodian</option>
                        <option value="CAMEROONIAN">Cameroonian</option>
                        <option value="CANADIAN">Canadian</option>
                        <option value="CAPE_VERDEAN">Cape Verdean</option>
                        <option value="CENTRAL_AFRICAN">Central African</option>
                        <option value="CHADIAN">Chadian</option>
                        <option value="CHILEAN">Chilean</option>
                        <option value="CHINESE">Chinese</option>
                        <option value="COLOMBIAN">Colombian</option>
                        <option value="COMORAN">Comoran</option>
                        <option value="CONGOLESE">Congolese</option>
                        <option value="COSTA_RICAN">Costa Rican</option>
                        <option value="CROATIAN">Croatian</option>
                        <option value="CUBAN">Cuban</option>
                        <option value="CYPRIOT">Cypriot</option>
                        <option value="CZECH">Czech</option>
                        <option value="DANISH">Danish</option>
                        <option value="DJIBOUTI">Djibouti</option>
                        <option value="DOMINICAN">Dominican</option>
                        <option value="DUTCH">Dutch</option>
                        <option value="EAST_TIMORESE">East Timorese</option>
                        <option value="ECUADOREAN">Ecuadorean</option>
                        <option value="EGYPTIAN">Egyptian</option>
                        <option value="EMIRIAN">Emirian</option>
                        <option value="EQUATORIAL_GUINEAN">Equatorial Guinean</option>
                        <option value="ERITREAN">Eritrean</option>
                        <option value="ESTONIAN">Estonian</option>
                        <option value="ETHIOPIAN">Ethiopian</option>
                        <option value="FIJIAN">Fijian</option>
                        <option value="FILIPINO">Filipino</option>
                        <option value="FINNISH">Finnish</option>
                        <option value="FRENCH">French</option>
                        <option value="GABONESE">Gabonese</option>
                        <option value="GAMBIAN">Gambian</option>
                        <option value="GEORGIAN">Georgian</option>
                        <option value="GERMAN">German</option>
                        <option value="GHANAIAN">Ghanaian</option>
                        <option value="GREEK">Greek</option>
                        <option value="GRENADIAN">Grenadian</option>
                        <option value="GUATEMALAN">Guatemalan</option>
                        <option value="GUINEA_BISSAUAN">Guinea-Bissauan</option>
                        <option value="GUINEAN">Guinean</option>
                        <option value="GUYANESE">Guyanese</option>
                        <option value="HAITIAN">Haitian</option>
                        <option value="HERZEGOVINIAN">Herzegovinian</option>
                        <option value="HONDURAN">Honduran</option>
                        <option value="HUNGARIAN">Hungarian</option>
                        <option value="ICELANDER">Icelander</option>
                        <option value="INDIAN">Indian</option>
                        <option value="INDONESIAN">Indonesian</option>
                        <option value="IRANIAN">Iranian</option>
                        <option value="IRAQI">Iraqi</option>
                        <option value="IRISH">Irish</option>
                        <option value="ISRAELI">Israeli</option>
                        <option value="ITALIAN">Italian</option>
                        <option value="IVORIAN">Ivorian</option>
                        <option value="JAMAICAN">Jamaican</option>
                        <option value="JAPANESE">Japanese</option>
                        <option value="JORDANIAN">Jordanian</option>
                        <option value="KAZAKHSTANI">Kazakhstani</option>
                        <option value="KENYAN">Kenyan</option>
                        <option value="KITTIAN_AND_NEVISIAN">Kittian and Nevisian</option>
                        <option value="KUWAITI">Kuwaiti</option>
                        <option value="KYRGYZ">Kyrgyz</option>
                        <option value="LAOTIAN">Laotian</option>
                        <option value="LATVIAN">Latvian</option>
                        <option value="LEBANESE">Lebanese</option>
                        <option value="LIBERIAN">Liberian</option>
                        <option value="LIBYAN">Libyan</option>
                        <option value="LIECHTENSTEINER">Liechtensteiner</option>
                        <option value="LITHUANIAN">Lithuanian</option>
                        <option value="LUXEMBOURGER">Luxembourger</option>
                        <option value="MACEDONIAN">Macedonian</option>
                        <option value="MALAGASY">Malagasy</option>
                        <option value="MALAWIAN">Malawian</option>
                        <option value="MALAYSIAN">Malaysian</option>
                        <option value="MALDIVAN">Maldivan</option>
                        <option value="MALIAN">Malian</option>
                        <option value="MALTESE">Maltese</option>
                        <option value="MARSHALLESE">Marshallese</option>
                        <option value="MAURITANIAN">Mauritanian</option>
                        <option value="MAURITIAN">Mauritian</option>
                        <option value="MEXICAN">Mexican</option>
                        <option value="MICRONESIAN">Micronesian</option>
                        <option value="MOLDOVAN">Moldovan</option>
                        <option value="MONACAN">Monacan</option>
                        <option value="MONGOLIAN">Mongolian</option>
                        <option value="MOROCCAN">Moroccan</option>
                        <option value="MOSOTHO">Mosotho</option>
                        <option value="MOTSWANA">Motswana</option>
                        <option value="MOZAMBICAN">Mozambican</option>
                        <option value="NAMIBIAN">Namibian</option>
                        <option value="NAURUAN">Nauruan</option>
                        <option value="NEPALESE">Nepalese</option>
                        <option value="NEW_ZEALANDER">New Zealander</option>
                        <option value="NI_VANUATU">Ni-Vanuatu</option>
                        <option value="NICARAGUAN">Nicaraguan</option>
                        <option value="NIGERIEN">Nigerien</option>
                        <option value="NORTH_KOREAN">North Korean</option>
                        <option value="NORTHERN_IRISH">Northern Irish</option>
                        <option value="NORWEGIAN">Norwegian</option>
                        <option value="OMANI">Omani</option>
                        <option value="PAKISTANI">Pakistani</option>
                        <option value="PALAUAN">Palauan</option>
                        <option value="PANAMANIAN">Panamanian</option>
                        <option value="PAPUA_NEW_GUINEAN">Papua New Guinean</option>
                        <option value="PARAGUAYAN">Paraguayan</option>
                        <option value="PERUVIAN">Peruvian</option>
                        <option value="POLISH">Polish</option>
                        <option value="PORTUGUESE">Portuguese</option>
                        <option value="QATARI">Qatari</option>
                        <option value="ROMANIAN">Romanian</option>
                        <option value="RUSSIAN">Russian</option>
                        <option selected value="RWANDAN">Rwandan</option>
                        <option value="SAINT_LUCIAN">Saint Lucian</option>
                        <option value="SALVADORAN">Salvadoran</option>
                        <option value="SAMOAN">Samoan</option>
                        <option value="SAN_MARINESE">San Marinese</option>
                        <option value="SAO_TOMEAN">Sao Tomean</option>
                        <option value="SAUDI">Saudi</option>
                        <option value="SCOTTISH">Scottish</option>
                        <option value="SENEGALESE">Senegalese</option>
                        <option value="SERBIAN">Serbian</option>
                        <option value="SEYCHELLOIS">Seychellois</option>
                        <option value="SIERRA_LEONEAN">Sierra Leonean</option>
                        <option value="SINGAPOREAN">Singaporean</option>
                        <option value="SLOVAKIAN">Slovakian</option>
                        <option value="SLOVENIAN">Slovenian</option>
                        <option value="SOLOMON_ISLANDER">Solomon Islander</option>
                        <option value="SOMALI">Somali</option>
                        <option value="SOUTH_AFRICAN">South African</option>
                        <option value="SOUTH_KOREAN">South Korean</option>
                        <option value="SPANISH">Spanish</option>
                        <option value="SRI_LANKAN">Sri Lankan</option>
                        <option value="SUDANESE">Sudanese</option>
                        <option value="SURINAMER">Surinamer</option>
                        <option value="SWAZI">Swazi</option>
                        <option value="SWEDISH">Swedish</option>
                        <option value="SWISS">Swiss</option>
                        <option value="SYRIAN">Syrian</option>
                        <option value="TAIWANESE">Taiwanese</option>
                        <option value="TAJIK">Tajik</option>
                        <option value="TANZANIAN">Tanzanian</option>
                        <option value="THAI">Thai</option>
                        <option value="TOGOLESE">Togolese</option>
                        <option value="TONGAN">Tongan</option>
                        <option value="TRINIDADIAN_OR_TOBAGONIAN">Trinidadian or Tobagonian</option>
                        <option value="TUNISIAN">Tunisian</option>
                        <option value="TURKISH">Turkish</option>
                        <option value="TUVALUAN">Tuvaluan</option>
                        <option value="UGANDAN">Ugandan</option>
                        <option value="UKRAINIAN">Ukrainian</option>
                        <option value="URUGUAYAN">Uruguayan</option>
                        <option value="UZBEKISTANI">Uzbekistani</option>
                        <option value="VENEZUELAN">Venezuelan</option>
                        <option value="VIETNAMESE">Vietnamese</option>
                        <option value="WELSH">Welsh</option>
                        <option value="YEMENITE">Yemenite</option>
                        <option value="ZAMBIAN">Zambian</option>
                        <option value="ZIMBABWEAN">Zimbabwean</option>
                      </select>
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="dob" className="block text-sm font-medium text-gray-700">Date of birth</label>
                      <input onChange={(e) => { setUser({ ...user, dateOfBirth: e.target.value || "" }) }} type="date" id="dob" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="password" className="block text-sm font-medium text-gray-700">Password</label>
                      <input onChange={(e) => { setUser({ ...user, password: e.target.value || "" }) }} type="password" id="password" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                    </div>
                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">Confirm Password</label>
                      <input onChange={(e) => { setUser({ ...user, confirmPassword: e.target.value || "" }) }} type="password" id="confirmPassword" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                    </div>
                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="role" className="block text-sm font-medium text-gray-700">Account type</label>
                      <select onChange={(e) => { setUser({ ...user, role: e.target.value || "" }) }} id="role" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
                        <option value="USER">User</option>
                        <option value="ADMIN">Admin</option>
                      </select>
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="dob" className="block text-sm font-medium text-gray-700">Profile photo</label>
                      <input onChange={handleFileChange} type="file" accept='image/*' hidden id="filePicker" />
                      <div onClick={(e) => { document.getElementById('filePicker')?.click() }} className="uploadAttachment cursor-pointer">
                        <div className='mr-2'>
                          <svg width="29" height="24" viewBox="0 0 29 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M8.38028 20.9808C6.74499 20.8759 5.19118 20.3421 3.94071 19.4556C2.69025 18.5692 1.80707 17.3753 1.41729 16.0446C1.02751 14.7139 1.15105 13.3143 1.77028 12.0457C2.38951 10.7772 3.47275 9.70445 4.86535 8.98076C5.16167 7.05085 6.28976 5.27727 8.03851 3.99192C9.78725 2.70658 12.0368 1.99756 14.3662 1.99756C16.6956 1.99756 18.9451 2.70658 20.6939 3.99192C22.4426 5.27727 23.5707 7.05085 23.867 8.98076C25.2596 9.70445 26.3429 10.7772 26.9621 12.0457C27.5813 13.3143 27.7049 14.7139 27.3151 16.0446C26.9253 17.3753 26.0421 18.5692 24.7917 19.4556C23.5412 20.3421 21.9874 20.8759 20.3521 20.9808V20.9998H8.38028V20.9808ZM15.5634 12.9998H19.1549L14.3662 7.99976L9.57746 12.9998H13.169V16.9998H15.5634V12.9998Z" fill="#1679A8" />
                          </svg>

                        </div>
                        <div>{file ? file.name : "Pick a file"}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="">
                  <button type="submit" hidden></button>
                </div>
              </div>
            </form>
          </div>
          <div className="modal-footer my-10">
            <div className="flex justify-center">
              <button className='cancel mr-9' onClick={toggleModal}>Cancel</button>
              <button onClick={handleRegister}>Submit</button>
            </div>
          </div>
        </div>
      } />
    </div>
  )
}

export default Login


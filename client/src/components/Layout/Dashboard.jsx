import { useEffect, useRef, useState } from "react";
import BG from "../../assets/images/nav-bg.svg";
import logo from "../../assets/images/logo.svg";
import activeHome from "../../assets/images/white-home-icon.svg";
import home from "../../assets/images/blued-home-icon.svg";
import "../../assets/scss/dashboardLayout.scss";
import { NavLink, useNavigate } from "react-router-dom";
import { useLocation } from "react-router-dom";
import {
  selectUser,
  logout,
  loadUser,
  selectIsLoggedIn,
  hasPrivilege,
} from "../../store/modules/authSlice";
import Modal from "../Modal";
import toast from "react-hot-toast";
import AppServices from "../../services";
import { API_URL } from "../../services";
import { useDispatch, useSelector } from "react-redux";

const DashboardLayout = ({ children }) => {
  const childRef = useRef(null);

  const toggleModal = () => {
    if (childRef.current) childRef.current.toggleModal();
  };

  const [menuStatus, setMenuStatus] = useState(false);
  const [sidebarStatus, setSidebarStatus] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [loggedInUser, setLoggedInUser] = useState({});
  const toggleMenu = () => {
    setMenuStatus(!menuStatus);
  };
  const toggleSidebar = () => {
    setSidebarStatus(!sidebarStatus);
  };
  const dispatch = useDispatch();
  const user = useSelector(selectUser);
  const isLoggedIn = useSelector(selectIsLoggedIn);
  const navigate = useNavigate();

  const [isDropDownOpen, setIsDropDownOpen] = useState(
    useLocation().pathname.includes("data-management")
  );

  const toggleDropDown = () => {
    setIsDropDownOpen(!isDropDownOpen);
  };

  const handleLogout = () => {
    dispatch(logout());
    navigate("/login");
  };

  useEffect(() => {
    if (isLoggedIn === 0) {
      navigate("/login");
    }
  }, [isLoggedIn]);

  useEffect(() => {
    if (!loaded) {
      dispatch(loadUser());
    }
  }, [loaded]);

  useEffect(() => {
    if (user) setLoaded(true);
  }, [user]);

  useEffect(() => {
    if (sidebarStatus) {
      toggleSidebar();
    }
  }, [useLocation().pathname]);

  const updateUser = () => {
    const {
      firstName,
      lastName,
      email,
      dateOfBirth,
      maritalStatus,
      gender,
      nationality,
    } = loggedInUser;
    toast.promise(
      AppServices.updateUser(
        {
          firstName,
          lastName,
          email,
          dateOfBirth,
          maritalStatus,
          gender,
          nationality,
        },
        user?.id
      ),
      {
        loading: "Updating account ...",
        success: (response) => {
          dispatch(loadUser());
          toggleModal();
          return "Account updated successfully";
        },
        error: (error) => {
          const message =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
          if (message.includes("required pattern"))
            if (message.includes("phone")) return "invalid phone number";
            else return "invalid nationalId";
          return message;
        },
      }
    );
  };

  return (
    <>
      <div className="nav-bar bg-primary">
        <img src={BG} alt="" />
        <div className="flex justify-end ml-auto place-items-center">
          <div
            onClick={toggleSidebar}
            className="toggle-sidebar lg:hidden cursor-pointer flex place-items-center justify-center"
          >
            {sidebarStatus ? (
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M21 18V20H3V18H21ZM6.596 3.90399L8.01 5.31799L4.828 8.49999L8.01 11.682L6.596 13.096L2 8.49999L6.596 3.90399ZM21 11V13H12V11H21ZM21 3.99999V5.99999H12V3.99999H21Z"
                  fill="#28A4E2"
                />
              </svg>
            ) : (
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M21 18V20H3V18H21ZM17.404 3.90399L22 8.49999L17.404 13.096L15.99 11.682L19.172 8.49999L15.99 5.31799L17.404 3.90399ZM12 11V13H3V11H12ZM12 3.99999V5.99999H3V3.99999H12Z"
                  fill="#28A4E2"
                />
              </svg>
            )}
          </div>
        </div>
      </div>
      <div className="flex">
        <div
          className={`full-height sidebar ${
            sidebarStatus ? "absolute" : "hidden"
          } md:block`}
        >
          <div id="logo" className="flex justify-center">
            <img
              alt="Coat of Arms of Rwanda logo"
              style={{ width: "146.172px", height: "81px" }}
              src={logo}
            />
          </div>
          <ul className="list-reset text-left flex flex-col h-4/6">
            <li className="dropdown" id="dropdown">
              <NavLink
                className={`link-item colored-link ${
                  useLocation().pathname === "/" ? "active" : ""
                }`}
                to="/"
              >
                <img
                  src={useLocation().pathname === "/" ? activeHome : home}
                  alt=""
                />
                <span className="menu-link">Home</span>
              </NavLink>

              {hasPrivilege(user, "INITIATE_FLOW") ? (
                <>
                  <div className="link-item" onClick={toggleDropDown}>
                    Data management
                    <svg
                      data-testid="dropdown-right-icon"
                      width="24"
                      height="24"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                      className={`${
                        isDropDownOpen ? "" : "rotate-180"
                      } transition-transform duration-300`}
                    >
                      <path
                        d="M12.3552 9.52217L12.3552 9.52218L12.3587 9.52569L16.6412 13.7582C16.684 13.8051 16.7175 13.8598 16.7398 13.9192C16.7631 13.9811 16.7739 14.0469 16.7716 14.1129C16.7692 14.1789 16.7539 14.2438 16.7263 14.3039C16.6987 14.3639 16.6596 14.4179 16.611 14.4627L16.6044 14.4688L16.5981 14.4751C16.5514 14.5214 16.496 14.5581 16.435 14.583C16.3741 14.6078 16.3089 14.6205 16.2431 14.6201L16.2423 14.6201C16.1114 14.6195 15.986 14.5677 15.8929 14.4757C15.8927 14.4755 15.8925 14.4753 15.8923 14.4751L12.3537 10.9365L12.0002 10.583L11.6466 10.9365L8.10769 14.4755C8.10754 14.4756 8.10739 14.4758 8.10724 14.4759C8.01361 14.5688 7.88708 14.6209 7.75519 14.6209C7.6231 14.6209 7.49637 14.5686 7.40269 14.4755L7.40229 14.4751C7.35543 14.4286 7.31823 14.3733 7.29285 14.3124C7.26746 14.2514 7.25439 14.1861 7.25439 14.1201C7.25439 14.0541 7.26746 13.9887 7.29285 13.9278C7.31823 13.8668 7.35543 13.8115 7.40229 13.7651L7.4023 13.7651L7.40375 13.7636L11.6437 9.52362L11.6437 9.52362L11.6452 9.52217C11.6917 9.4753 11.747 9.43811 11.8079 9.41272C11.8688 9.38734 11.9342 9.37427 12.0002 9.37427C12.0662 9.37427 12.1316 9.38734 12.1925 9.41272C12.2534 9.43811 12.3087 9.4753 12.3552 9.52217Z"
                        fill="black"
                        className={`${
                          isDropDownOpen ? "stroke-white" : "stroke-gray-400"
                        }`}
                      />
                    </svg>
                  </div>
                  {isDropDownOpen ? (
                    <ul className="border-l-2 border-gray-300 pl-3 ml-2 text-gray-400 capitalize font-medium text-sm cursor-pointer">
                      <NavLink
                        className={`link-item colored-link ${
                          useLocation().pathname.includes("users")
                            ? "active"
                            : ""
                        }`}
                        to="/data-management/users"
                      >
                        <span className="menu-link">Users</span>
                      </NavLink>
                      <NavLink
                        className={`link-item colored-link ${
                          useLocation().pathname.includes("roles")
                            ? "active"
                            : ""
                        }`}
                        to="/data-management/roles"
                      >
                        <span className="menu-link">Roles</span>
                      </NavLink>
                      <NavLink
                        className={`link-item colored-link ${
                          useLocation().pathname.includes("privileges")
                            ? "active"
                            : ""
                        }`}
                        to="/data-management/privileges"
                      >
                        <span className="menu-link">privileges</span>
                      </NavLink>
                    </ul>
                  ) : (
                    ""
                  )}{" "}
                </>
              ) : (
                ""
              )}
            </li>
          </ul>
          <div
            className="dropdown mt-auto relative flex h-1/4"
            id="dropdown"
            onClick={toggleMenu}
          >
            {menuStatus ? (
              <div className="absolute">
                <button
                  onClick={() => {
                    toggleModal();
                    if (user)
                      setLoggedInUser({
                        names: user.names,
                        email: user.emailAddress,
                        phone: user.phone,
                        nationalId: user.nationalId,
                      });
                  }}
                >
                  Settings
                </button>
                <button onClick={handleLogout}>Logout</button>
              </div>
            ) : (
              ""
            )}
            {user?.profilePicture ? (
              <div
                className="profilePhoto"
                style={{
                  backgroundImage: `url(${API_URL}/users/raw/${user.profilePicture.name})`,
                }}
              ></div>
            ) : (
              <div className="avatar">
                <div className="mt-2">
                  {user?.firstName ? user?.firstName[0] : ""}
                </div>
              </div>
            )}
            <div className="user-name ml-2 mt-1">{user?.firstName} ...</div>
          </div>
        </div>
        <div className=" full-height w-full bg-customBg">{children}</div>
      </div>
      <Modal
        ref={childRef}
        width="767px"
        children={
          <div>
            <div className="modal-title text-center my-10">Settings</div>
            <div className="modal-body">
              <form>
                <div className="">
                  <div className="px-4 py-5 bg-white sm:p-6">
                    <div className="grid grid-cols-6 gap-6">
                      <div className="col-span-6 sm:col-span-3">
                        <label
                          htmlFor="first-name"
                          className="block text-sm font-medium text-gray-700"
                        >
                          First name
                        </label>
                        <input
                          defaultValue={user?.firstName}
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              firstName: e.target.value || "",
                            });
                          }}
                          type="text"
                          id="first-name"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
                      </div>

                      <div className="col-span-6 sm:col-span-3">
                        <label
                          htmlFor="last-name"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Last name
                        </label>
                        <input
                          defaultValue={user?.lastName}
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              lastName: e.target.value || "",
                            });
                          }}
                          type="text"
                          id="last-name"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
                      </div>

                      <div className="col-span-6 sm:col-span-3">
                        <label
                          htmlFor="email"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Email
                        </label>
                        <input
                          defaultValue={user?.emailAddress}
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              email: e.target.value || "",
                            });
                          }}
                          type="email"
                          id="email"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
                      </div>

                      <div className="col-span-6 sm:col-span-3">
                        <label
                          htmlFor="gender"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Gender
                        </label>
                        <select
                          defaultValue={user?.gender}
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              gender: e.target.value || "",
                            });
                          }}
                          id="gender"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        >
                          <option value="MALE">Male</option>
                          <option value="FEMALE">Female</option>
                        </select>
                      </div>
                      <div className="col-span-6 sm:col-span-3">
                        <label
                          htmlFor="maritalStatus"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Marital status
                        </label>
                        <select
                          defaultValue={user?.maritalStatus}
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              maritalStatus: e.target.value || "",
                            });
                          }}
                          id="maritalStatus"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        >
                          <option value="SINGLE">Single</option>
                          <option value="MARRIED">Married</option>
                          <option value="DIVORCED">Divorced</option>
                          <option value="WIDOWED">Widowed</option>
                        </select>
                      </div>

                      <div className="col-span-6 sm:col-span-3">
                        <label
                          htmlFor="nationality"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Nationality
                        </label>
                        <select
                          defaultValue={user?.nationality}
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              nationality: e.target.value || "",
                            });
                          }}
                          id="nationality"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        >
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
                          <option value="CENTRAL_AFRICAN">
                            Central African
                          </option>
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
                          <option value="EQUATORIAL_GUINEAN">
                            Equatorial Guinean
                          </option>
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
                          <option value="GUINEA_BISSAUAN">
                            Guinea-Bissauan
                          </option>
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
                          <option value="KITTIAN_AND_NEVISIAN">
                            Kittian and Nevisian
                          </option>
                          <option value="KUWAITI">Kuwaiti</option>
                          <option value="KYRGYZ">Kyrgyz</option>
                          <option value="LAOTIAN">Laotian</option>
                          <option value="LATVIAN">Latvian</option>
                          <option value="LEBANESE">Lebanese</option>
                          <option value="LIBERIAN">Liberian</option>
                          <option value="LIBYAN">Libyan</option>
                          <option value="LIECHTENSTEINER">
                            Liechtensteiner
                          </option>
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
                          <option value="PAPUA_NEW_GUINEAN">
                            Papua New Guinean
                          </option>
                          <option value="PARAGUAYAN">Paraguayan</option>
                          <option value="PERUVIAN">Peruvian</option>
                          <option value="POLISH">Polish</option>
                          <option value="PORTUGUESE">Portuguese</option>
                          <option value="QATARI">Qatari</option>
                          <option value="ROMANIAN">Romanian</option>
                          <option value="RUSSIAN">Russian</option>
                          <option selected value="RWANDAN">
                            Rwandan
                          </option>
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
                          <option value="SOLOMON_ISLANDER">
                            Solomon Islander
                          </option>
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
                          <option value="TRINIDADIAN_OR_TOBAGONIAN">
                            Trinidadian or Tobagonian
                          </option>
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
                        <label
                          htmlFor="dob"
                          className="block text-sm font-medium text-gray-700"
                        >
                          Date of birth
                        </label>
                        <input
                          defaultValue={
                            user?.dateOfBirth
                              ? new Date(user.dateOfBirth)
                                  .toISOString()
                                  .substring(0, 10)
                              : ""
                          }
                          onChange={(e) => {
                            setLoggedInUser({
                              ...user,
                              dateOfBirth: e.target.value || "",
                            });
                          }}
                          type="date"
                          id="dob"
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
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
                <button className="cancel mr-9" onClick={toggleModal}>
                  Cancel
                </button>
                <button onClick={updateUser}>Submit</button>
              </div>
            </div>
          </div>
        }
      />
    </>
  );
};

export default DashboardLayout;

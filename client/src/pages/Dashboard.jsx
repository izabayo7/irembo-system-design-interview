import { useEffect, useState, useRef } from "react";
import "../assets/scss/dashboard.scss";
import "../assets/scss/modal.scss";
import verified from '../assets/images/verified.svg'
import AccountView from "../components/AccountView";
import { selectUser, selectIsLoggedIn, setUser } from "../store/modules/authSlice";
import AppServices from "../services";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { selectUsers, setUsers, updateUser } from "../store/modules/userSlice";
import Modal from '../components/Modal';

function Dashboard() {
  const isLoggedIn = useSelector(selectIsLoggedIn);
  const users = useSelector(selectUsers);
  const user = useSelector(selectUser);
  const [submitted, setSubmitted] = useState(false)
  const dispatch = useDispatch();
  const [nidOrPassport, setNidOrPassport] = useState("");
  const [file, setFile] = useState(null)
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    if (isLoggedIn) {

      AppServices.getUsers().then((response) => {
        if (response.data) {
          dispatch(setUsers(response.data));
        }
      });

    }
  }, [isLoggedIn]);

  const childRef = useRef(null);

  const toggleModal = () => {
    if (childRef.current) childRef.current.toggleModal();
  };

  const handleFileChange = (e) => {
    e.preventDefault();
    setFile(e.target.files[0]);
  }

  const verifyAccount = (e) => {
    e.preventDefault();

    toast.promise(
      AppServices.verifyAccount(selectedUser.accountVerification.id),
      {
        loading: "Verifying User ...",
        success: (response) => {
          selectedUser.accountVerification = {
            ...selectedUser.accountVerification,
            verificationStatus: "VERIFIED",
          };
          dispatch(
            updateUser(selectedUser)
          );
          toggleModal();
          return "User verified successfully";
        },
        error: (error) => {
          let message =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();
          if (message.includes("required pattern"))
            if (message.includes("chasisNumber"))
              return "invalid chasisNumber number";
            else return "invalid manufactureCompany";
          return message;
        },
      }
    );
  }

  const handleRegister = (e) => {
    e.preventDefault();

    if (nidOrPassport.length === 0) {
      return toast.error("NID of passport  is required");
    }

    if (!file) {
      return toast.error("Official Document  is required");
    }

    if (submitted) return;
    setSubmitted(true);

    const formData = new FormData();
    formData.append("officialDocument", file);
    formData.append("nidOrPassport", nidOrPassport);

    toast.promise(
      AppServices.uploadIdentificationDocuments(formData, user.accountVerification.id),
      {
        loading: 'Submitting ...',
        success: ({ data }) => {
          toggleModal();
          setSubmitted(false);
          setUser({ ...user, accountVerification: data })
          // set user with new stuff
          return "Submitted successfully";
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
    <div className="pl-10 pt-10">
      {user && <div>
        <div className="title">{user.role === "ADMIN" ? 'Users' : user.role === "USER" ? 'My account' : ''}</div>
        {user.role === "ADMIN" ? <div className="md:flex">
          <div className="w-full">
            {/* <div className="md:flex">
              <div className="flex ml-auto mr-6">
                <div className="mt-2 ml-4">
                  <input
                    onChange={(e) => {
                      setFilter({ ...filter, search: e.target.value });
                    }}
                    type="text"
                    name=""
                    id=""
                    placeholder="search"
                    className="input px-3 py-1"
                  />
                </div>
              </div>
            </div> */}
            <div className="table w-full">
              <table>
                <thead>
                  <tr
                    className="
              flex flex-col flex-no
              wrap
              table-row
              rounded-l-lg rounded-none
              mb-2 mb-0
            "
                  >
                    <th>Names</th>
                    <th>Email</th>
                    <th>Gender</th>
                    <th>Marital Status</th>
                    <th>Age</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody className="sm:flex-1 sm:flex-none">
                  {
                    users.map((user) => {
                      return (
                        <tr
                          key={user.id}
                          className="
              sm:flex
              sm:flex-col
              sm:flex-no
              sm:wrap
              sm:table-row
              sm:mb-2
              sm:mb-0
              main-header
              sm:header tr
                          "
                        >
                          <td className="flex">{user.firstName} {user.lastName} {user.accountVerification?.verificationStatus === "VERIFIED" && <img src={verified} className="ml-2" style={{ width: '15px' }} alt="verified" />}</td>
                          <td>{user.emailAddress}</td>
                          <td>{user.gender}</td>
                          <td>{user.maritalStatus}</td>
                          <td>{parseInt(new Date().getFullYear()) - parseInt(new Date(user.dateOfBirth).getFullYear())}</td>
                          <td className='pt-1 p-3'>
                            <div className="flex">
                              <div onClick={() => { setSelectedUser({ ...user }); toggleModal() }} className='status cursor-pointer rounded'>
                                View
                              </div>
                            </div>
                          </td>
                        </tr>
                      );
                    })
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div> : user.role === "USER" ?
          <div className="md:flex justify-center">
            <AccountView user={user} handleClick={toggleModal} />
          </div> : ''
        }
      </div>
      }
      <Modal ref={childRef} width="767px" children={
        <div>
          <div className="modal-title text-center my-10">
            Settings
          </div>
          {user?.role === "ADMIN" ? <div className="modal-body pt-14" style={{
            maxHeight: '60vh',
            overflowY: 'auto',
            display: 'flex',
            justifyContent: 'center'
          }}>
            {user && <AccountView user={selectedUser} role={user.role} />}
          </div> :
            <div className="modal-body pt-14">
              <form>
                <div className="">
                  <div className="px-4 py-5 bg-white sm:p-6">
                    <div className="grid grid-cols-6 gap-6">
                      <div className="col-span-6 sm:col-span-3">
                        <label htmlFor="first-name" className="block text-sm font-medium text-gray-700">NID or Passport</label>
                        <input onChange={(e) => { setNidOrPassport(e.target.value || "") }} type="text" id="first-name" className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md" />
                      </div>

                      <div className="col-span-6 sm:col-span-3">
                        <label htmlFor="dob" className="block text-sm font-medium text-gray-700">Official document</label>
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
          }
          <div className="modal-footer my-10">
            {user?.role === "ADMIN" ? <div className="flex justify-center">
              <button className='cancel mr-9' onClick={toggleModal}>Close</button>
              {(selectedUser?.accountVerification.verificationStatus !== "VERIFIED" && selectedUser?.accountVerification.nidOrPassport) && <button onClick={verifyAccount}>Verify</button>}
            </div> :
              <div className="flex justify-center">
                <button className='cancel mr-9' onClick={toggleModal}>Cancel</button>
                {user?.accountVerification?.verificationStatus !== "VERIFIED" && <button onClick={handleRegister}>Submit</button>}
              </div>
            }
          </div>
        </div>
      } />
    </div>
  );
}

export default Dashboard;

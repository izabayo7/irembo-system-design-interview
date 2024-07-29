import { useEffect, useState, useRef } from 'react'

import verified from '../assets/images/verified.svg'
import '../assets/scss/accountView.scss'

function AccountView({ user, handleClick, role }) {
  const [accountInfo, setAccountInfo] = useState({});
  const API_URL = import.meta.env.VITE_API_URL;
  const token = JSON.parse(localStorage.getItem("user") || "{}").accessToken;
  useEffect(
    () => {
      setAccountInfo(user);
    }
    , [user]
  )

  return (
    <div className="accountView">
      <div className="md:flex align-middle">
        <div
          className="profilePhoto"
          style={{
            backgroundImage: `url(${API_URL}/users/profile/${accountInfo.profilePhoto}?token=${token})`,
          }}
        ></div>
        <div className="details ml-8">
          <div className="names flex">
            {accountInfo.firstName} {accountInfo.lastName}
            {accountInfo.accountVerification?.verificationStatus ===
              "VERIFIED" && (
              <img src={verified} className="ml-2" alt="verified" />
            )}
          </div>
          <div className="other-details">{accountInfo.emailAddress}</div>
          <div className="flex">
            <div className="other-details">{accountInfo.maritalStatus}</div>
            <div className="other-details ml-auto">{accountInfo.gender}</div>
          </div>
          <div className="flex">
            <div className="other-details">
              {new Date(accountInfo.dateOfBirth).toLocaleDateString()}
            </div>
            <div className="other-details ml-auto">
              {parseInt(new Date().getFullYear()) -
                parseInt(new Date(accountInfo.dateOfBirth).getFullYear())}{" "}
              years
            </div>
          </div>
          <div className="flex">
            <div className="other-details">{accountInfo.nationality}</div>
          </div>
          <div className="other-details nid">
            {accountInfo.accountVerification?.nidOrPassport}
          </div>
        </div>
      </div>
      {accountInfo.accountVerification?.verificationStatus !== "UNVERIFIED" ? (
        <div>
          <div className="names">User Identification document</div>
          <div
            className="identificationDoc"
            style={{
              backgroundImage: `url(${API_URL}/verification/document/${accountInfo.accountVerification?.officialDocument}?token=${token})`,
            }}
          ></div>
        </div>
      ) : role !== "ADMIN" ? (
        <div>
          <button onClick={handleClick}>
            Upload identification infromation
          </button>
        </div>
      ) : (
        ""
      )}
    </div>
  );
}

export default AccountView


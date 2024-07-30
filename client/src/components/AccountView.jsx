import { useEffect, useState, useRef } from 'react'

import verified from '../assets/images/verified.svg'
import '../assets/scss/accountView.scss'

function AccountView({ user, handleClick, isOwner }) {
  const API_URL = import.meta.env.VITE_API_URL;

  return (
    <div className="accountView">
      <div className="md:flex align-middle">
        <div
          className="profilePhoto"
          style={{
            backgroundImage: `url(${API_URL}/users/raw/${user.profilePicture.name})`,
          }}
        ></div>
        <div className="details ml-8">
          <div className="names flex">
            {user.firstName} {user.lastName}
            {user.verificationStatus ===
              "VERIFIED" && (
              <img src={verified} className="ml-2" alt="verified" />
            )}
          </div>
          <div className="other-details">{user.emailAddress}</div>
          <div className="flex">
            <div className="other-details">{user.maritalStatus}</div>
            <div className="other-details ml-auto">{user.gender}</div>
          </div>
          <div className="flex">
            <div className="other-details">
              {new Date(user.dateOfBirth).toLocaleDateString()}
            </div>
            <div className="other-details ml-auto">
              {user.age + " "} 
              years 
            </div>
          </div>
          <div className="other-details nid">
            {user.nidOrPassport}
          </div>
        </div>
      </div>
      {user.verificationStatus !== "UNVERIFIED" ? (
        <div>
          <div className="names">User Identification document</div>
          <div
            className="identificationDoc"
            style={{
              backgroundImage: `url(${API_URL}/users/raw/${encodeURIComponent(user.officialDocument.name)})`,
            }}
          ></div>
        </div>
      ) : user.roles.length == 0 && isOwner ? (
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


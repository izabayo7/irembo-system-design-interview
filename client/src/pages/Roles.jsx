import { useEffect, useState, useRef } from "react";
import "../assets/scss/dashboard.scss";
import "../assets/scss/modal.scss";
import verified from "../assets/images/verified.svg";
import AccountView from "../components/AccountView";
import {
  selectUser,
  selectIsLoggedIn,
  hasPrivilege,
} from "../store/modules/authSlice";
import AppServices from "../services";
import { useDispatch, useSelector } from "react-redux";
import Pagination from "../components/Pagination";

function Roles() {
  const isLoggedIn = useSelector(selectIsLoggedIn);
  const [roles, setRoles] = useState([]);
  const user = useSelector(selectUser);
  const dispatch = useDispatch();
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const handlePageChange = (newPage) => {
    if (newPage < 1) newPage = 1;
    setCurrentPage(newPage);
    fetchRoles(newPage, pageSize);
  };

  const handlePageSizeChange = (newPageSize) => {
    if (newPageSize < 1) newPageSize = 1;
    setPageSize(newPageSize);
    setCurrentPage(1);

    fetchRoles(1, newPageSize);
  };

  const fetchRoles = (currentPage, pageSize) => {
    AppServices.getRoles(currentPage, pageSize).then((response) => {
      if (response.data) {
        setRoles(response.data.data);
        setCurrentPage(response.data.data.number);
        setTotalItems(response.data.data.totalElements);
      }
    });
  };

  useEffect(() => {
    if (isLoggedIn) {
      if (!hasPrivilege(user, "RETRIEVE_ROLE")) {
        navigate("/");
      }

      fetchRoles(currentPage + 1, pageSize);
    }
  }, [isLoggedIn]);

  return (
    <div className="pl-10 pt-10">
      {user && (
        <div>
          <div className="title">Roles</div>
          {hasPrivilege(user, "RETRIEVE_USER") ? (
            <div className="">
              <div className="md:flex">
                <div className="w-full">
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
                          <th>Name</th>
                          <th>Description</th>
                        </tr>
                      </thead>
                      <tbody className="sm:flex-1 sm:flex-none">
                        {roles.content?.map((role) => {
                          return (
                            <tr
                              key={role.id}
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
                              <td className="flex">{role.name}</td>
                              <td>{role.description}</td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                  <div className=" w-11/12 mt-4">
                    <Pagination
                      totalItems={totalItems}
                      itemsPerPage={pageSize}
                      currentPage={currentPage + 1}
                      onPageChange={(page) => handlePageChange(page)}
                      onItemsPerPageChange={(size) =>
                        handlePageSizeChange(size)
                      }
                    />
                  </div>
                </div>
              </div>
            </div>
          ) : user.roles.length == 0 ? (
            <div className="md:flex justify-center">
              <AccountView
                user={user}
                handleClick={toggleModal}
                isOwner={true}
              />
            </div>
          ) : (
            ""
          )}
        </div>
      )}
    </div>
  );
}

export default Roles;

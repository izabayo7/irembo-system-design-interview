import React, { useState, useEffect } from "react";

const PaginationButton = ({ children, onClick, active, disabled }) => (
  <button
    onClick={onClick}
    disabled={disabled}
    className={`px-3 py-2 border rounded-md ${
      active
        ? "bg-blue-500 text-white"
        : "bg-white text-gray-700 hover:bg-gray-50"
    } ${disabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer"}`}
  >
    {children}
  </button>
);

const Pagination = ({
  totalItems,
  itemsPerPage,
  currentPage,
  onPageChange,
  onItemsPerPageChange,
}) => {
  const [pages, setPages] = useState([]);

  useEffect(() => {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagesArray = getPaginationRange(currentPage, totalPages);
    setPages(pagesArray);
  }, [totalItems, itemsPerPage, currentPage]);

  const getPaginationRange = (current, total) => {
    if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);

    if (current <= 3) return [1, 2, 3, 4, 5, "...", total];
    if (current >= total - 2)
      return [1, "...", total - 4, total - 3, total - 2, total - 1, total];

    return [1, "...", current - 1, current, current + 1, "...", total];
  };

  const handlePageChange = (page) => {
      onPageChange(page);
  };

  return (
    <div className="flex flex-col sm:flex-row justify-between items-center mt-4">
      <div className="mb-4 sm:mb-0">
        <select
          value={itemsPerPage}
          onChange={(e) => onItemsPerPageChange(Number(e.target.value))}
          className="border rounded-md px-2 py-1"
        >
          {[10, 25, 50, 100].map((value) => (
            <option key={value} value={value}>
              {value} per page
            </option>
          ))}
        </select>
      </div>
      <div className="flex items-center space-x-2">
        <PaginationButton
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1}
        >
          Previous
        </PaginationButton>
        {pages.map((page, index) => (
          <PaginationButton
            key={index}
            onClick={() => typeof page === "number" && handlePageChange(page)}
            active={page === currentPage}
            disabled={typeof page !== "number"}
          >
            {page}
          </PaginationButton>
        ))}
        <PaginationButton
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={
            currentPage === Math.ceil(totalItems / itemsPerPage)
          }
        >
          Next
        </PaginationButton>
      </div>
      <div className="mt-4 sm:mt-0 text-sm text-gray-600">
        Showing {Math.min((currentPage - 1) * itemsPerPage + 1, totalItems)} to{" "}
        {Math.min(currentPage * itemsPerPage, totalItems)} of {totalItems}{" "}
        entries
      </div>
    </div>
  );
};

export default Pagination;

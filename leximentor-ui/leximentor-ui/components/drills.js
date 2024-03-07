// pages/index.js

import React from 'react';

const Drills = ({ data }) => {
  return (
    <div className="container mt-5">
      <button className="btn btn-primary mb-3">Refresh</button>
      <table className="table table-bordered">
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Name</th>
            <th scope="col">Description</th>
            <th scope="col">Quantity</th>
            <th scope="col">Price</th>
            <th scope="col">Actions</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              <th scope="row">{item.id}</th>
              <td>{item.name}</td>
              <td>{item.description}</td>
              <td>{item.quantity}</td>
              <td>{item.price}</td>
              <td>
                <input type="checkbox" />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export async function getServerSideProps() {
  // Fetch data from your API endpoint
  const res = await fetch('http://localhost:9191/api/drills'); // Replace with your API endpoint
  const data = await res.json();

  // Pass data to the component via props
  return {
    props: {
      data,
    },
  };
}

export default Drills;

// pages/[id].js

import { useRouter } from "next/router";

import Script from "next/script";
import React from "react";
import Head from "next/head";

const Challenges = ({ data }) => {
  return (
    <>
      <Head>
        <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></Script>
      </Head>
      <div className="container mt-5">
        <button className="btn btn-primary mb-3">
          Create meaning challenge
        </button>
        <button className="btn btn-warning  mb-3 ml-3">
          Create Spelling challenge
        </button>
        <button className="btn btn-success  mb-3 ml-3">
          Create Matching challenge
        </button>
        <button className="btn btn-danger mb-3 ml-3">
          Create selecting challenge
        </button>
        <table className="table table-bordered">
          <thead>
            <tr className="bg-blue-400 text-black">
              <th scope="col">#</th>
              <th scope="col">Selects</th>
              <th scope="col">RefId</th>
              <th scope="col">Name</th>
              <th scope="col">Status</th>
              <th scope="col">Overall Score</th>
              <th>Total words</th>
              <th>Total Challenges</th>
            </tr>
          </thead>
          <tbody>
            {data.data.map((item, index) => (
              <tr key={item.refId}>
                <th scope="row">{index + 1}</th>
                <td className="text-center">
                  <input type="checkbox" />
                </td>
                <td>{item.refId}</td>
                <td>{item.name}</td>
                <td>{item.status}</td>
                <td>{item.overAllScore}</td>
                <td>
                  {item.drillSetDTOList == null
                    ? 0
                    : item.drillSetDTOList.length}
                </td>
                <td>
                  {item.drillChallengeDTOList == null
                    ? 0
                    : item.drillChallengeDTOList.length}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
};

export default Challenges;
export async function getServerSideProps() {
  // Fetch data from your API endpoint
  const res = await fetch("http://192.168.1.7:9191/api/drill"); // Replace with your API endpoint
  const data = await res.json();

  // Pass data to the component via props
  return {
    props: {
      data,
    },
  };
}

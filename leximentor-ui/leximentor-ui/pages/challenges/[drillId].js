// pages/[id].js

import { useRouter } from "next/router";

import Script from "next/script";
import React from "react";
import Head from "next/head";
import Link from "next/link";

const Challenges = ({ data }) => {
  const router = useRouter();
  const { drillId } = router.query.drillId;
  return (
    <>
      <Head>
        <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></Script>
      </Head>
      <div className="container mt-5">
        <button className="btn btn-primary mb-3">Open meaning challenge</button>
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
              <th scope="col">Drill Type</th>
              <th scope="col">Drill Score</th>
              <th scope="col">Total Correct</th>
              <th scope="col">Total Wrong</th>
            </tr>
          </thead>
          <tbody>
            {data.data.map((item, index) => (
              <tr key={item.refId}>
                <th scope="row">{index + 1}</th>
                <Link href={"/drill_challenges/" + item.refId}>
                  <button className="btn btn-primary mb-3">
                    Open Challenge
                  </button>
                </Link>
                <td>{item.refId}</td>
                <td>{item.drillType}</td>
                <td>{item.drillScore}</td>
                <td>{item.totalCorrect}</td>
                <td>{item.totalWrong}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
};

export default Challenges;
export async function getServerSideProps(context) {
  // Fetch data from your API endpoint
  // const router = useRouter();
  const { drillId } = context.params;
  const res = await fetch(
    `http://192.168.1.7:9191/api/drill/challenges/${drillId}`
  ); // Replace with your API endpoint
  const data = await res.json();
  // Pass data to the component via props
  return {
    props: {
      data,
    },
  };
}

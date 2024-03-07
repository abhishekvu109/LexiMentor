import dynamic from 'next/dynamic';
import Script from "next/script";
import Head from "next/head";

const Drills = dynamic(() => import("../../components/drills"), { ssr: false });

export default function Home({data}) {
  return (
    <>
      <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></Script>
      <Head></Head>
    
      <Drills data={data}/>
    </>
  );
}
export async function getServerSideProps() {
  // Fetch data from your API endpoint
    const res = await fetch('http://192.168.1.7:9191/api/drill'); // Replace with your API endpoint
    const data = await res.json();

    // Pass data to the component via props
    return {
      props: {
        data,
      },
    };
  // try {
  //   const res = await fetch("http://192.168.1.7:9191/api/drill");
  //   const data = await res.json();

  //   return {
  //     props: {
  //       data: data.data || [], // Ensure data is an array or default to an empty array
  //     },
  //   };
  // } catch (error) {
  //   console.error("Error fetching data:", error.message);
  //   return {
  //     props: {
  //       data: [],
  //     },
  //   };
  // }
}
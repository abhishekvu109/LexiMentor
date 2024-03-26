import dynamic from 'next/dynamic';
import Script from "next/script";
import Head from "next/head";
import {API_BASE_URL} from "@/constants";

const Drills = dynamic(() => import("../../components/drills"), {ssr: false});

export default function Home({data}) {
    return (<>
        <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></Script>
        <Head></Head>

        <Drills data={data}/>
    </>);
}

export async function getServerSideProps() {
    // Fetch data from your API endpoint
    const res = await fetch(`${API_BASE_URL}/drill/metadata`); // Replace with your API endpoint
    const data = await res.json();

    // Pass data to the component via props
    return {
        props: {
            data,
        },
    };
}
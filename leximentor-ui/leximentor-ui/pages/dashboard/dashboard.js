import Script from "next/script";
import Head from "next/head";
import Drills from "@/components/drills";

export default function Home() {
  return (
    <>
      <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></Script>
      <Head></Head>
      <Drills/>
    </>
  );
}

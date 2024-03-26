// Example component using Bootstrap classes

import React from "react";
import dynamic from "next/dynamic";
import Script from "next/script";
import Head from "next/head";

const DynamicComponent = dynamic(() => import("../components/MyComponents"), {
  ssr: false,
});

const Home = () => {
  return (
    <>
    </>
  );
};

export default Home;

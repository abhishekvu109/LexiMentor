// Example component using Bootstrap classes

import React from "react";
import dynamic from "next/dynamic";

const DynamicComponent = dynamic(() => import("../components/MyComponents"), {
  ssr: false,
});

const Home = () => {
  return (
    <>
      <DynamicComponent />
    </>
  );
};

export default Home;

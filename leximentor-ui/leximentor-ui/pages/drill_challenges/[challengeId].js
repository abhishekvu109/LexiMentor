const ListChallenges = ({}) => {
  return (
    <>
      <div className="flex flex-row">
        <div className="basis-2/4 bg-gray-200">
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          <div className="flex flex-row m-2">
            <div className="basis-1/4 font-semibold">Word ID</div>
            <div className="basis-3/4"><input className="w-full form-control" type="wordId"></input></div>
          </div>
          
        </div>
        <div className="basis-2/4 bg-green-400">02</div>
      </div>
      <div classNameName="flex flex-row">
        <nav>
          <ul className="pagination">
            <li className="page-item disabled">
              <a href="#" className="page-link" tabindex="-1">
                Previous
              </a>
            </li>
            <li className="page-item active">
              <a href="#" className="page-link">
                1
              </a>
            </li>
            <li className="page-item">
              <a href="#" className="page-link">
                2
              </a>
            </li>
            <li className="page-item">
              <a href="#" className="page-link">
                3
              </a>
            </li>
            <li className="page-item">
              <a href="#" className="page-link">
                4
              </a>
            </li>
            <li className="page-item">
              <a href="#" className="page-link">
                5
              </a>
            </li>
            <li className="page-item">
              <a href="#" className="page-link">
                Next
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </>
  );
};

export default ListChallenges;

export async function getServerSideProps(context) {
  const { challengeId } = context.params;
  const res = await fetch(`http://192.168.1.7:9191/api/drill/challenges/${challengeId}`);
  const data = await res.json();
  // Pass data to the component via props
  return {
    props: {
      data,
    },
  };
}

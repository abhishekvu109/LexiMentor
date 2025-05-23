import React, {useState} from "react";
import Link from "next/link";
import {API_LEXIMENTOR_BASE_URL} from "@/constants";

const Drills = ({data}) => {
    const [isNewDrillModalOpen, setIsNewDrillModalOpen] = useState(false);
    const [drillMetadata, setDrillMetadata] = useState(data);
    const [drillData, setDrillData] = useState({
        limit: 20, isNewWords: true
    });
    const [isDrillCreationSuccessMsg, setIsDrillCreationSuccessMsg] = useState(false);
    const [isDrillCreationFailureMsg, setIsDrillCreationFailureMsg] = useState(false);
    const handleNewDrillModalOpen = () => {
        setIsNewDrillModalOpen(true);
    };

    const handleNewDrillModalClose = () => {
        setIsNewDrillModalOpen(false);
    };

    const handleDrillCreationSuccessMsgOpen = () => {
        setIsDrillCreationSuccessMsg(true);
    };

    const handleDrillCreationSuccessMsgClose = () => {
        setIsDrillCreationSuccessMsg(false);
    };

    const handleDrillCreationFailureMsgOpen = () => {
        setIsDrillCreationFailureMsg(true);
    };

    const handleDrillCreationFailureMsgClose = () => {
        setIsDrillCreationFailureMsg(false);
    };

    const handleChange = (e) => {
        // Update form data state when input fields change
        setDrillData({...drillData, [e.target.name]: e.target.value});
    };
    const submitDillCreation = async (e) => {
        e.preventDefault();
        try {
            const queryString = new URLSearchParams(drillData).toString();
            const response = await fetch(`${API_LEXIMENTOR_BASE_URL}/drill/metadata?${queryString}`, {
                method: 'POST', headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                handleDrillCreationFailureMsgOpen();
                throw new Error('Network response was not ok');
            }

            // Handle successful response
            const data = await response.json();
            console.log('Response data:', data);
            handleDrillCreationSuccessMsgOpen();
            handleNewDrillModalClose();
            await LoadTable();
        } catch (error) {
            handleDrillCreationFailureMsgOpen();
            console.error('Error:', error);
        }
    };

    const LoadTable = async () => {
        try {
            const response = await fetch(`${API_LEXIMENTOR_BASE_URL}/drill/metadata`, {
                method: 'GET', headers: {
                    'Content-Type': 'application/json',
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setDrillMetadata(data);
            console.log('Response data:', data);
        } catch (error) {
            console.error('Error:', error);
        }
    };

    const RemoveDrill = async (drillRefId) => {
        try {
            const response = await fetch(`${API_LEXIMENTOR_BASE_URL}/drill/metadata/${drillRefId}`, {
                method: 'DELETE', headers: {
                    'Content-Type': 'application/json',
                }
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            console.log('Response data:', data);
        } catch (error) {
            console.error('Error:', error);
        }
    };

    const ShowFailure = ({isVisible}) => {
        if (isVisible) {
            return <>
                <div id="alert-2"
                     className="flex items-center p-4 mb-4 text-red-800 rounded-lg bg-red-50 dark:bg-gray-800 dark:text-red-400"
                     role="alert">
                    <svg className="flex-shrink-0 w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                         fill="currentColor" viewBox="0 0 20 20">
                        <path
                            d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"/>
                    </svg>
                    <span className="sr-only">Info</span>
                    <div className="ms-3 text-sm font-medium">
                        Drill creation failed.
                    </div>
                    <button type="button" onClick={handleDrillCreationFailureMsgClose}
                            className="ms-auto -mx-1.5 -my-1.5 bg-red-50 text-red-500 rounded-lg focus:ring-2 focus:ring-red-400 p-1.5 hover:bg-red-200 inline-flex items-center justify-center h-8 w-8 dark:bg-gray-800 dark:text-red-400 dark:hover:bg-gray-700"
                            data-dismiss-target="#alert-2" aria-label="Close">
                        <span className="sr-only">Close</span>
                        <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 14 14">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                        </svg>
                    </button>
                </div>
            </>
        }
    };

    const ShowSuccess = ({isVisible}) => {
        if (isVisible) {
            return <>
                <div id="alert-3"
                     className="flex items-center p-4 mb-4 text-green-800 rounded-lg bg-green-50 dark:bg-gray-800 dark:text-green-400"
                     role="alert">
                    <svg className="flex-shrink-0 w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                         fill="currentColor" viewBox="0 0 20 20">
                        <path
                            d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"/>
                    </svg>
                    <span className="sr-only">Info</span>
                    <div className="ms-3 text-sm font-medium">
                        Drill creation is successful.
                    </div>
                    <button type="button" onClick={handleDrillCreationSuccessMsgClose}
                            className="ms-auto -mx-1.5 -my-1.5 bg-green-50 text-green-500 rounded-lg focus:ring-2 focus:ring-green-400 p-1.5 hover:bg-green-200 inline-flex items-center justify-center h-8 w-8 dark:bg-gray-800 dark:text-green-400 dark:hover:bg-gray-700"
                            data-dismiss-target="#alert-3" aria-label="Close">
                        <span className="sr-only">Close</span>
                        <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 14 14">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                        </svg>
                    </button>
                </div>
            </>
        }
    };

    const NewDrillModalComponent = ({isVisible}) => {
        if (isVisible) {
            return <>
                <div id="create-new-drill-modal-form" tabIndex="-1" aria-hidden="true" data-modal-placement="center"
                     className="overflow-y-auto overflow-x-hidden fixed inset-0 z-50 flex justify-center items-center">
                    <div className="relative p-4 w-full max-w-md max-h-full">
                        {/*Modal content*/}
                        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
                            {/*Modal header*/}
                            <div
                                className="flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600">
                                <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                                    Create New Drill
                                </h3>
                                <button type="button" onClick={handleNewDrillModalClose}
                                        className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
                                        data-modal-toggle="create-new-drill-modal-form">
                                    <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                                         fill="none"
                                         viewBox="0 0 14 14">
                                        <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round"
                                              strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                                    </svg>
                                    <span className="sr-only">Close modal</span>
                                </button>
                            </div>
                            {/*Modal body*/}
                            <form className="p-4 md:p-5" onSubmit={submitDillCreation}>
                                <div className="grid gap-4 mb-4 grid-cols-2">
                                    <div className="col-span-2 sm:col-span-1">
                                        <label htmlFor="limit"
                                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Count</label>
                                        <input type="number" name="limit" id="limit" onChange={handleChange}
                                               value={drillData.limit}
                                               className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500"
                                               placeholder="20" required=""/>
                                    </div>
                                    <div className="col-span-2 sm:col-span-1">
                                        <label htmlFor="isNewWords"
                                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Is
                                            new words</label>
                                        <select id="isNewWords" name="isNewWords" onChange={handleChange}
                                                value={drillData.isNewWords}
                                                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500">
                                            <option selected="" value="true">True</option>
                                            <option value="false">False</option>
                                        </select>
                                    </div>
                                </div>
                                <button type="submit"
                                        className="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                                    <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                                         xmlns="http://www.w3.org/2000/svg">
                                        <path fill-rule="evenodd"
                                              d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
                                              clip-rule="evenodd"></path>
                                    </svg>
                                    Create new drill
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

            </>
        }
    };

    return (<>

        <div className="container mt-5">
            {isDrillCreationSuccessMsg ? (<ShowSuccess isVisible={true}/>) : (<ShowSuccess isVisible={false}/>)}
            {isDrillCreationFailureMsg ? (<ShowFailure isVisible={true}/>) : (<ShowFailure isVisible={false}/>)}

            <button type="button" data-modal-target="create-new-drill-modal-form"
                    data-model-toggle="create-new-drill-modal-form" onClick={handleNewDrillModalOpen}
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                New drill
            </button>
            <button type="button"
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                List Challenges
            </button>
            <button type="button"
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Create Challenges
            </button>
            <button type="button" onClick={LoadTable}
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Load Table
            </button>
            <NewDrillModalComponent isVisible={isNewDrillModalOpen}/>
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
                <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-200 dark:bg-gray-700 dark:text-gray-400">
                    <tr>
                        <th scope="col" className="px-6 py-3 text-center">
                            Serial
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            ID
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Drill Name
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Status
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Overall Score
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Total words
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Total Challenges
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            LEARN
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            CHALLENGE
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Remove
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {(drillMetadata.data != null && drillMetadata.data.length > 0) ? (<>
                        {drillMetadata.data.map((item, index) => (<tr key={item.refId}
                                                                      className="odd:bg-white odd:dark:bg-gray-900 even:bg-gray-50 even:dark:bg-gray-800 border-b dark:border-gray-700">
                            <th scope="row"
                                className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white text-center">{index + 1}</th>
                            <td className="px-6 py-4 text-center">{item.refId}</td>
                            <td className="px-6 py-4 text-center">{item.name}</td>
                            <td className="px-6 py-4 text-center">{item.status}</td>
                            <td className="px-6 py-4 text-center">{item.overAllScore}</td>
                            <td className="px-6 py-4 text-center">
                                {item.drillSetDTOList == null ? 0 : item.drillSetDTOList.length}
                            </td>
                            <td className="px-6 py-4 text-center">
                                {item.drillChallengeDTOList == null ? 0 : item.drillChallengeDTOList.length}
                            </td>
                            <td className="px-6 py-4 text-center">
                                <Link href={"/drills/learning/practice/" + item.refId}
                                      className="font-medium text-blue-600 dark:text-blue-500 hover:underline">
                                    Click
                                </Link>
                            </td>
                            <td className="px-6 py-4 text-center">
                                <Link href={"/challenges/" + item.refId}
                                      className="font-medium text-blue-600 dark:text-blue-500 hover:underline">
                                    Click
                                </Link>
                            </td>
                            <td className="px-6 py-4 text-center">
                                <Link href="#" onClick={() => RemoveDrill(item.refId)}
                                      className="font-medium text-blue-600 dark:text-blue-500 hover:underline">
                                    Click
                                </Link>
                            </td>
                        </tr>))}
                    </>) : (<>
                        <tr>
                            <td className="px-6 py-4 text-center">No drills found</td>
                        </tr>
                    </>)}

                    </tbody>
                </table>
            </div>
        </div>


    </>);
};

export default Drills;

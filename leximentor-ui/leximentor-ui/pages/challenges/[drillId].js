// pages/[id].js

import React, {useEffect, useState} from "react";
import Link from "next/link";
import {API_LEXIMENTOR_BASE_URL} from "@/constants";
import {deleteData, fetchData, postData} from "@/dataService";

const Challenges = ({data, drillId}) => {
    console.log(data);
    const [challengeData, setChallengeData] = useState(data);
    const [drillRefId, setDrillRefId] = useState(drillId);
    const [isEvaluatorVisible, setIsEvaluatorVisible] = useState(false);
    const [challengeRequestData, setChallengeRequestData] = useState({drillId: drillId, drillType: ''});
    const [evaluationData, setEvaluationData] = useState({challengeId: "", evaluator: ""});
    const handleChange = (e) => {
        // Update form data state when input fields change
        setEvaluationData({...evaluationData, [e.target.name]: e.target.value});
    };
    const handleEvaluatorModel = (isOpen) => {
        setIsEvaluatorVisible(isOpen);
    };

    const getDrillTypeLink = (drillType) => {
        if (drillType == 'LEARN_MEANING') return '/drills/challenges/meaning/'; else if (drillType == 'LEARN_POS') return '/drills/challenges/pos/'; else if (drillType == 'IDENTIFY_WORD') return '/drills/challenges/identify_word/'; else if (drillType == 'GUESS_WORD') return '/drills/challenges/guess_word/'; else return '/drills/challenges/meaning/';
    };

    const handleChallengeRequestData = async (drillName) => {
        setChallengeRequestData((prevState) => {
            return {...prevState, drillType: drillName};
        });
    };

    useEffect(() => {
        // This code block will execute after the state has been updated
        if (challengeRequestData.drillType != null && challengeRequestData.drillType.length > 0) {
            createChallenge();
        }
    }, [challengeRequestData]); // Add challengeRequestData as a dependency


    const createChallenge = async () => {
        const queryString = new URLSearchParams(challengeRequestData).toString();
        const URL = `${API_LEXIMENTOR_BASE_URL}/drill/metadata/challenges/challenge?${queryString}`;
        const saveChallengeSavedData = await postData(URL);
        await LoadTable();
    };
    const deleteChallenge = async (drillRefId) => {
        const URL = `${API_LEXIMENTOR_BASE_URL}/drill/metadata/challenges/${drillRefId}`;
        const saveChallengeSavedData = await deleteData(URL);
        await LoadTable();
    };

    const Evaluate = async (e) => {
        e.preventDefault();
        const queryString = new URLSearchParams(evaluationData).toString();
        const URL = `${API_LEXIMENTOR_BASE_URL}/drill/metadata/challenges/challenge/${evaluationData.challengeId}/evaluate?${queryString}`;
        const evaluateFormData = await postData(URL);
        handleEvaluatorModel(false);
    };

    const LoadTable = async () => {
        const URL = `${API_LEXIMENTOR_BASE_URL}/drill/metadata/challenges/${drillRefId}`;
        const challengeDataFromDb = await fetchData(URL)
        setChallengeData(challengeDataFromDb);
    };

    const EvaluatorSelectorModalComponent = ({isVisible}) => {
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
                                <button type="button" onClick={() => handleEvaluatorModel(false)}
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
                            <form className="p-4 md:p-5" onSubmit={Evaluate}>
                                <div className="grid gap-4 mb-4 grid-cols-1">
                                    <div className="col-span-2 sm:col-span-1">
                                        <label htmlFor="limit"
                                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Challenge
                                            ID</label>
                                        <input type="text" name="challengeId" id="challengeId"
                                               value={evaluationData.challengeId} onChange={handleChange}
                                               className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500"
                                               placeholder="" required=""/>
                                    </div>
                                    <div className="col-span-2 sm:col-span-1">
                                        <label htmlFor="isNewWords"
                                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Evaluator</label>
                                        {/*<select id="isNewWords" name="isNewWords"*/}
                                        {/*        className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500">*/}
                                        {/*    <option selected="" value="true">True</option>*/}
                                        {/*    <option value="false">False</option>*/}
                                        {/*</select>*/}
                                        <input type="text" name="evaluator" id="evaluator"
                                               value={evaluationData.evaluator} onChange={handleChange}
                                               className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500"
                                               placeholder="" required=""/>
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
                                    Evaluate
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

            </>
        }
    };


    return (<>
        <h2 className="text-4xl  m-2 font-extrabold dark:text-white text-center">
            List of challenges for the drill</h2>
        <div className="container mt-5">
            <button type="button" data-modal-target="create-new-drill-modal-form"
                    onClick={(e) => {
                        e.preventDefault();
                        handleChallengeRequestData('MEANING')
                    }}
                    data-model-toggle="create-new-drill-modal-form"
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-violet-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Meaning
            </button>
            <button type="button" onClick={(e) => {
                e.preventDefault();
                handleChallengeRequestData('POS')
            }}
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-red-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Identify POS
            </button>
            <button type="button"
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-green-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Spell Jumble
            </button>

            <button type="button" onClick={(e) => {
                e.preventDefault();
                handleChallengeRequestData('IDENTIFY')
            }}
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-yellow-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Spell it from pronunciation.
            </button>

            <button type="button" onClick={(e) => {
                e.preventDefault();
                handleChallengeRequestData('GUESS')
            }}
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-orange-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Guess word from meaning
            </button>

            <button type="button"
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-sky-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 7.757v8.486M7.757 12h8.486M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                </svg>
                Match the right word
            </button>

            <button type="button" onClick={LoadTable}
                    className="px-6 py-3.5 m-2 text-base font-medium text-white inline-flex items-center bg-amber-400 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="w-4 h-4 text-white me-2" aria-hidden="true"
                     xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M17.651 7.65a7.131 7.131 0 0 0-12.68 3.15M18.001 4v4h-4m-7.652 8.35a7.13 7.13 0 0 0 12.68-3.15M6 20v-4h4"/>
                </svg>
                Reload
            </button>
            <EvaluatorSelectorModalComponent isVisible={isEvaluatorVisible}/>
            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-2">
                <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-200 dark:bg-gray-700 dark:text-gray-400">
                    <tr>
                        <th scope="col" className="px-6 py-3 text-center">
                            Serial
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Challenge ID
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Drill Type
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Drill Score
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Passed
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Correct
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Incorrect
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Status
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Evaluation status
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Delete
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Try
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Evaluate
                        </th>
                        <th scope="col" className="px-6 py-3 text-center">
                            Report
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {challengeData.data != null && challengeData.data.length > 0 ? (challengeData.data.map((item, index) => (
                        <tr key={item.refId}>
                            <td scope="row"
                                className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white text-center">{index + 1}</td>
                            <td className="px-6 py-4 text-center text-xs">{item.refId}</td>
                            <td className="px-6 py-4 text-center text-xs font-sans text-blue-700 text-decoration-underline">{item.drillType}</td>
                            <td className="px-6 py-4 text-center"><span
                                className="inline-flex items-center justify-center w-7 h-7 ms-2 text-xs font-semibold text-blue-800 bg-blue-200 rounded-full">
                                {item.drillScore}
                                </span>
                            </td>
                            <td className="px-6 py-4 text-center">
                                <label>{(item.evaluationStatus != 'Evaluated') ? (<>NA</>) : (<>{item.drillScore > 70 ? (<>
                                <span
                                    className="bg-green-100 text-green-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-green-400 border border-green-400">Passed</span>
                                </>) : (<><span
                                    className="bg-red-100 text-red-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-red-400 border border-red-400">Failed</span></>)}</>)}</label>
                            </td>
                            <td className="px-6 py-4 text-center">{item.totalCorrect}</td>
                            <td className="px-6 py-4 text-center">{item.totalWrong}</td>
                            <td className="px-6 py-4 text-center text-xs">{item.status}</td>
                            <td className="px-6 py-4 text-center text-xs">{item.evaluationStatus}</td>
                            <td className="px-6 py-4 text-center">
                                <Link className="font-medium text-blue-600 dark:text-blue-500 hover:underline"
                                      onClick={() => deleteChallenge(item.refId)}
                                      href="#">Delete</Link>
                            </td>
                            <td className="px-6 py-4 text-center">
                                {(item.status != 'Completed') ? (<Link
                                    className="font-medium text-blue-600 dark:text-blue-500 hover:underline"
                                    href={(getDrillTypeLink(item.drillType)) + drillRefId + "/" + item.refId}>
                                    Try
                                </Link>) : (<Link
                                    className="font-medium text-gray-300 dark:text-blue-500 hover:underline" href="#">
                                    Try
                                </Link>)}
                            </td>
                            <td className="px-6 py-4 text-center">
                                {(item.evaluationStatus == 'Evaluated' || item.status == 'Not Initiated') ? (<Link
                                    className="font-medium text-gray-300 dark:text-blue-500 hover:underline"
                                    href="#">
                                    Evaluate
                                </Link>) : (<Link
                                    className="font-medium text-blue-600 dark:text-blue-500 hover:underline"
                                    onClick={() => handleEvaluatorModel(true)}
                                    href="#">
                                    Evaluate
                                </Link>)}
                            </td>
                            <td className="px-6 py-4 text-center">
                                {(item.evaluationStatus != 'Evaluated') ? (<Link
                                    className="font-medium text-gray-300 dark:text-blue-500 hover:underline"
                                    href="#">
                                    Click
                                </Link>) : (<Link
                                    className="font-medium text-blue-600 dark:text-blue-500 hover:underline"
                                    href={"/evaluation_report/meaning_report/" + item.refId}>
                                    Click
                                </Link>)}
                            </td>
                        </tr>))) : (<tr>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                            <h6 className="text-lg font-bold dark:text-white">No data found</h6>
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                        <td scope="col" className="px-6 py-4 text-center">
                        </td>
                    </tr>)}
                    </tbody>
                </table>
            </div>
        </div>
    </>);
};

export default Challenges;

export async function getServerSideProps(context) {
    const {drillId} = context.params;
    const data = await fetchData(`${API_LEXIMENTOR_BASE_URL}/drill/metadata/challenges/${drillId}`);
    return {
        props: {
            data, drillId
        },
    };
}

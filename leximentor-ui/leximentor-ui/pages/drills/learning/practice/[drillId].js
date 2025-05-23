import {useEffect, useState} from "react";
import {API_LEXIMENTOR_BASE_URL} from "@/constants";
import Link from "next/link";
import {fetchData} from "@/dataService";
import Head from "next/head";
import Script from "next/script";

const LoadDrillSet = ({drillSetData, drillId, wordMetadata, sourcesData}) => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const [size, setSize] = useState(drillSetData.data.length);
    const [drillMetadata, setDrillMetadata] = useState(drillSetData);
    const [wordData, setWordData] = useState(wordMetadata);
    const [sources, setSources] = useState(sourcesData);
    const [source, setSource] = useState(sourcesData.data[0]);
    const [sourceDropDown, setSourceDropDown] = useState(false);

    const handleSourceDropdown = (value) => {
        setSource(value);
    };

    const fetchWordData = async (wordId, source) => {
        const wordDataResponse = await fetchData(`${API_LEXIMENTOR_BASE_URL}/inventory/words/${wordId}/sources/${source}`);
        const sourcesDataResponse = await fetchData(`${API_LEXIMENTOR_BASE_URL}/inventory/words/${wordId}/sources`);
        setWordData(wordDataResponse);
        setSources(sourcesDataResponse);
    };

    useEffect(() => {
        fetchWordData(drillMetadata.data[currentIndex].wordRefId, source);
    }, [currentIndex, source, drillMetadata]);

    const prevWord = () => {
        setCurrentIndex((currentIndex - 1 + size) % size);
    };

    const nextWord = () => {
        setCurrentIndex((currentIndex + 1) % size);
    };

    return (<>
        <Script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></Script>
        <Head>
            <Link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        </Head>
        <div className="alert alert-dark w-full font-bold text-center" role="alert">
            Practice words and their meanings.
        </div>
        <div className="container mx-auto m-5 border-3">
            <div className="flex flex-row">
                <div className="basis-1/2 text-center m-2">
                    <button onClick={prevWord} className="btn btn-warning btn-outline-dark w-full font-semibold">
                        Previous
                    </button>
                </div>
                <div className="basis-1/2 text-center m-2">
                    <button onClick={nextWord} className="btn btn-success btn-outline-dark w-full font-semibold">
                        Next
                    </button>
                </div>
            </div>
            <div className="flex flex-row m-2">
                <div className="basis-1/12">
                    <button
                        type="button"
                        className="text-white bg-gradient-to-br from-purple-600 to-blue-500 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-blue-300 dark:focus:ring-blue-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2 dropdown-toggle"
                        data-bs-toggle="dropdown"
                    >
                        Sources
                    </button>
                    <div className="dropdown-menu">
                        {sources.data != null && sources.data.length > 0 ? (sources.data.map((item, index) => (<>
                            <a href="#" onClick={() => handleSourceDropdown(item)} className="dropdown-item"
                               key={index}>
                                {item}
                            </a>
                            <div className="dropdown-divider"></div>
                        </>))) : (<>
                            <a href="#" className="dropdown-item">
                                No source found
                            </a>
                        </>)}
                    </div>
                </div>
                <div className="basis-1/12">
                    <Link href="/dashboard/dashboard">
                        <button
                            type="button"
                            className="text-white bg-gradient-to-r from-cyan-500 to-blue-500 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-cyan-300 dark:focus:ring-cyan-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2"
                        >
                            Dashboard
                        </button>
                    </Link>
                </div>
            </div>

            <div className="flex flex-row border-1 bg-gray-200 p-2">
                <div
                    className="inline-flex items-center justify-center w-10 h-10 text-sm font-bold text-white bg-purple-500 border-2 border-white rounded-full dark:border-gray-900">
                    {currentIndex + 1}
                </div>
                <div className="ml-5">
                    <h2 className="text-3xl font-extrabold dark:text-white">{wordData.data.word}</h2>
                </div>
            </div>
            {/*<div*/}
            {/*    className={currentIndex % 2 == 0 ? "flex flex-row my-2 bg-green-200 border-1" : "flex flex-row my-2 bg-cyan-200 border-1"}>*/}
            {/*    <div className="grid grid-cols-12 gap-2 m-2 w-full">*/}
            {/*        <div className="col-span-1">*/}
            {/*            <p className="text-sm font-medium text-gray-900 dark:text-white">Drill Id</p>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-11">*/}
            {/*                <span*/}
            {/*                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                    {drillId}*/}
            {/*                </span>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-1">*/}
            {/*            <p className="text-sm font-medium text-gray-900 dark:text-white">WordId</p>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-11">*/}
            {/*                <span*/}
            {/*                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                    {wordData.data.refId}*/}
            {/*                </span>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-1">*/}
            {/*            <p className="text-sm font-medium text-gray-900 dark:text-white">Word</p>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-11">*/}
            {/*                <span*/}
            {/*                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                    {wordData.data.word}*/}
            {/*                </span>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-1">*/}
            {/*            <p className="text-sm font-medium text-gray-900 dark:text-white">Source</p>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-11">*/}
            {/*                <span*/}
            {/*                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                    {wordData.data.source}*/}
            {/*                </span>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-1">*/}
            {/*            <p className="text-sm font-medium text-gray-900 dark:text-white">Language</p>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-11">*/}
            {/*                <span*/}
            {/*                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                    {wordData.data.language}*/}
            {/*                </span>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-1">*/}
            {/*            <p className="text-sm font-medium text-gray-900 dark:text-white">Status</p>*/}
            {/*        </div>*/}
            {/*        <div className="col-span-11">*/}
            {/*                <span*/}
            {/*                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                    {wordData.data.status}*/}
            {/*                </span>*/}
            {/*        </div>*/}
            {/*        {wordData.data != null && wordData.data.pronunciation ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Pronunciation</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                        <span*/}
            {/*                            className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                            {wordData.data.pronunciation}*/}
            {/*                        </span>*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.mnemonic ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Mnemonic</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                        <span*/}
            {/*                            className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                            {wordData.data.mnemonic}*/}
            {/*                        </span>*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.localMeaning ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Local Meaning</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                        <span*/}
            {/*                            className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                            {wordData.data.localMeaning}*/}
            {/*                        </span>*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.partsOfSpeeches.length > 0 ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">POS</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                {wordData.data.partsOfSpeeches.map((item, index) => (<>*/}
            {/*                                <span*/}
            {/*                                    key={index}*/}
            {/*                                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500"*/}
            {/*                                >*/}
            {/*                                    {item.pos}*/}
            {/*                                </span>*/}
            {/*                </>))}*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.meanings.length > 0 ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Meanings</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                {wordData.data.meanings.map((item, index) => (<>*/}
            {/*                                <span*/}
            {/*                                    key={index}*/}
            {/*                                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500"*/}
            {/*                                >*/}
            {/*                                    {item.meaning}*/}
            {/*                                </span>*/}
            {/*                </>))}*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.synonyms.length > 0 ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Synonyms</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                {wordData.data.synonyms.map((item, index) => (<>*/}
            {/*                                <span*/}
            {/*                                    key={index}*/}
            {/*                                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500"*/}
            {/*                                >*/}
            {/*                                    {item.synonym}*/}
            {/*                                </span>*/}
            {/*                </>))}*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.antonyms.length > 0 ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Antonyms</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                {wordData.data.antonyms.map((item, index) => (<>*/}
            {/*                                <span*/}
            {/*                                    key={index}*/}
            {/*                                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500"*/}
            {/*                                >*/}
            {/*                                    {item.antonym}*/}
            {/*                                </span>*/}
            {/*                </>))}*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.examples.length > 0 ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Examples</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                {wordData.data.examples.map((item, index) => (<>*/}
            {/*                                <span*/}
            {/*                                    key={index}*/}
            {/*                                    className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500"*/}
            {/*                                >*/}
            {/*                                    {item.example}*/}
            {/*                                </span>*/}
            {/*                </>))}*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*        {wordData.data != null && wordData.data.category ? (<>*/}
            {/*            <div className="col-span-1">*/}
            {/*                <p className="text-sm font-medium text-gray-900 dark:text-white">Category</p>*/}
            {/*            </div>*/}
            {/*            <div className="col-span-11">*/}
            {/*                        <span*/}
            {/*                            className="bg-gray-100 text-gray-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded dark:bg-gray-700 dark:text-gray-400 border border-gray-500">*/}
            {/*                            {wordData.data.category}*/}
            {/*                        </span>*/}
            {/*            </div>*/}
            {/*        </>) : (<></>)}*/}
            {/*    </div>*/}
            {/*</div>*/}
            <div
                className={currentIndex % 2 == 0 ? "flex flex-row my-2" : "flex flex-row my-2"}>
                <div className="grid grid-cols-1 gap-2 m-2 w-full">
                    <ol className="space-y-4 text-black-200 font-sans list-decimal list-inside dark:text-gray-400 text-xl">

                        {wordData.data != null && wordData.data.word ? (<>
                            <hr className="h-px my-1 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Word</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    <li>{wordData.data.word}</li>
                                </ul>
                            </li>
                        </>) : (<></>)}


                        {wordData.data != null && wordData.data.partsOfSpeeches.length > 0 ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Parts of speech</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    {wordData.data.partsOfSpeeches.map((item, index) => (<>
                                        <li key={index}>{item.pos}</li>
                                    </>))}
                                </ul>
                            </li>
                        </>) : (<></>)}
                        {wordData.data != null && wordData.data.meanings.length > 0 ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Meanings</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    {wordData.data.meanings.map((item, index) => (<>
                                        <li key={index}>{item.meaning}</li>
                                    </>))}
                                </ul>
                            </li>
                        </>) : (<></>)}

                        {wordData.data != null && wordData.data.synonyms.length > 0 ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Synonyms</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    {wordData.data.synonyms.map((item, index) => (<>
                                        <li>{item.synonym}</li>
                                    </>))}
                                </ul>
                            </li>
                        </>) : (<></>)}


                        {wordData.data != null && wordData.data.antonyms.length > 0 ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Antonyms</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    {wordData.data.antonyms.map((item, index) => (<>
                                        <li>{item.antonym}</li>
                                    </>))}
                                </ul>
                            </li>
                        </>) : (<></>)}

                        {wordData.data != null && wordData.data.pronunciation ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Pronunciation</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    <li>{wordData.data.pronunciation}</li>
                                </ul>
                            </li>
                        </>) : (<></>)}


                        {wordData.data != null && wordData.data.examples.length > 0 ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Examples</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    {wordData.data.examples.map((item, index) => (<>
                                        <li>{item.example}</li>
                                    </>))}
                                </ul>
                            </li>
                        </>) : (<></>)}

                        {wordData.data != null && wordData.data.localMeaning ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Local Meaning</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    <li>{wordData.data.localMeaning}</li>
                                </ul>
                            </li>
                        </>) : (<></>)}

                        {wordData.data != null && wordData.data.category ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Category</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    <li>{wordData.data.category}</li>
                                </ul>
                            </li>
                        </>) : (<></>)}

                        {wordData.data != null && wordData.data.mnemonic ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Mnemonic</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    <li>{wordData.data.mnemonic}</li>
                                </ul>
                            </li>
                        </>) : (<></>)}

                        {wordData.data != null && wordData.data.language ? (<>
                            <hr className="h-px my-8 bg-gray-400 border-0 dark:bg-gray-700"/>
                            <li>
                                <b>Language</b>
                                <ul className="ps-5 mt-2 space-y-1 list-disc list-inside">
                                    <li>{wordData.data.language}</li>
                                </ul>
                            </li>
                        </>) : (<></>)}
                    </ol>
                </div>
            </div>
        </div>
    </>);
};

export default LoadDrillSet;

export async function getServerSideProps(context) {
    const {drillId} = context.params;
    const drillSetData = await fetchData(`${API_LEXIMENTOR_BASE_URL}/drill/metadata/sets/${drillId}`);
    const sourcesData = await fetchData(`${API_LEXIMENTOR_BASE_URL}/inventory/words/${drillSetData.data[0].wordRefId}/sources`);
    const wordMetadata = await fetchData(`${API_LEXIMENTOR_BASE_URL}/inventory/words/${drillSetData.data[0].wordRefId}/sources/${sourcesData.data[0]}`);
    return {
        props: {
            drillSetData, drillId, wordMetadata, sourcesData,
        },
    };
}

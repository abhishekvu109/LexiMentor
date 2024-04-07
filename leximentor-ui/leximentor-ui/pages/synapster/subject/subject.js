import {useState} from "react";
import {postData} from "@/dataService";
import {API_BASE_URL, API_SYNAPSTER_BASE_URL} from "@/constants";

const Subject = () => {
    const [subjectFormData, setSubjectFormData] = useState({name: "", status: "", description: "", category: ""});
    const handleChange = (e) => {
        setSubjectFormData({...subjectFormData, [e.target.name]: e.target.value});
    };
    const handleSubjectCreate = async (e) => {
        e.preventDefault();
        console.log(JSON.stringify(subjectFormData));
        const URL = `${API_SYNAPSTER_BASE_URL}/synapster/subjects/subject`;
        const response = await postData(URL, subjectFormData);
        console.log(response);
    };

    return (<>
        <div className="container mx-auto my-10 px-4  py-4 border-1">
            <form onSubmit={handleSubjectCreate}>
                <div className="grid gap-6 mb-6 md:grid-cols-2">
                    <div>
                        <label htmlFor="subject_name"
                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Subject
                            name</label>
                        <input type="text" id="subject_name" name="name" onInput={handleChange}
                               className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                               placeholder="Subject Name"/>
                    </div>
                    <div>
                        <label htmlFor="status"
                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Status</label>
                        <select id="status" name="status" onInput={handleChange}
                                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
                            <option selected>Choose a status</option>
                            <option value="active">Active</option>
                            <option value="inactive">Inactive</option>
                        </select>
                    </div>
                    <div>
                        <label htmlFor="description"
                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Description</label>
                        <input type="text" id="description" name="description" onInput={handleChange}
                               className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                               placeholder="describe in 100 words"/>
                    </div>
                    <div>
                        <label htmlFor="category"
                               className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Category</label>
                        <select id="category" name="category" onInput={handleChange}
                                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
                            <option selected>Choose a category</option>
                            <option value="science">Science</option>
                            <option value="technology">Technology</option>
                            <option value="geopolitics">Geopolitics</option>
                            <option value="history">History</option>
                        </select>
                    </div>
                </div>
                <button type="submit"
                        className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-md text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800 mr-2 mt-2">Submit
                </button>

                <button type="reset"
                        className="text-white bg-gray-300 hover:bg-gray-500 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-md text-sm w-full sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800 mr-2 mt-2">Reset
                </button>
            </form>
        </div>

    </>)
};

export default Subject;
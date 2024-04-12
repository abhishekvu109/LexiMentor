import {API_FITMATE_BASE_URL} from "@/constants";
import {fetchData} from "@/dataService";


const FitmateDashboard = ({bodyParts}) => {

    return (<>
        <div className="container mx-auto px-4 border-1">
        </div>
    </>);
};

export default FitmateDashboard;

export async function getServerSideProps(context) {
    const bodyParts = await fetchData(`${API_FITMATE_BASE_URL}/fitmate/bodyparts`);
    return {
        props: {
            bodyParts
        },
    };
}
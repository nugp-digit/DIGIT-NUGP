// import React, { useState,useEffect,Fragment } from "react";
import React, {Fragment } from "react";
import { useParams } from "react-router-dom";
import EditForm from "./EditForm";
import { Loader } from "@egovernments/digit-ui-react-components";

const EditTender = () => {
    const { id,tenantId } = useParams();
  //   console.log("param ",id)
    const { isLoading, isError, error, data, ...rest } = Digit?.Hooks?.wms?.te?.useWmsTEGetSingleRecord(tenantId,2339);
  console.log("data tender ",data)
  // console.log("data ",data?.WMSBankDetailsApplications[0])
  // const dataDummy=
  // {bank_branch: 'Noida',
  //   bank_ifsc_code: 'SBIN00012',
  //   bank_name: 'State Bank Of India',
  //   name:'Active'}

    
  // if (isLoading) {
  //   return <Loader />;
  // }
  return <EditForm data={data?.WMSTenderEntryApplications[0]} tenantId={tenantId} />;
  // return <EditForm data={dataDummy} tenantId={tenantId} />;

  return(<>
  <div>edit</div>
  </>)

}
export default EditTender;







// csv data upload
// const EditTender = ()=>{

//   const [csvFile, setCsvFile] = useState(null);
//   const handleFileChange = (e) => {
//     const file = e.target.files[0];
//     if (file) {
//       // Validate if the selected file is a CSV file
//       if (file.name.endsWith('.csv')) {
//         setCsvFile(file);
//       } else {
//         alert('Please select a CSV file.');
//       }
//     }
//   };

//   const handleUpload = () => {
//     if (csvFile) {
//       // Here, you can process the CSV file as needed
//       // For example, you can read its contents using FileReader API
//       const reader = new FileReader();
//       reader.onload = (event) => {
//         const csvData = event.target.result;
//         // Now you can work with the CSV data
//         console.log(typeof csvData," csvData");
//       };
//       const dd=reader.readAsText(csvFile)
//       console.log(dd," reader.readAsText(csvFile)");
//     } else {
//       alert('Please select a CSV file before uploading.');
//     }
//   };


// // const handleUpload = async()=>{
// //   var formdata = new FormData();
// // formdata.append("file", csvFile);
// // var requestOptions = {
// //   method: 'POST',
// //   body: formdata,
// //   redirect: 'follow'
// // };

// // await fetch("http://10.216.36.152:8484/wms/wms-services/v1/bank/_upload", requestOptions)
// //   .then(response => response.text())
// //   .then(result => console.log('result ',result))
// //   .catch(error => console.log('error ', error));
// // }
//   return (
//     <div>
//       <input type="file" accept=".csv" onChange={handleFileChange} />
//       <button onClick={handleUpload}>Upload CSV</button>
//     </div>
//   );
  
// }
// export default EditTender;
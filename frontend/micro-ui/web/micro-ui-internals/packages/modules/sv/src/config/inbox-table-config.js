  import React from "react";
  import { Link } from "react-router-dom";

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  

  const GetSlaCell = (value) => {
    if (isNaN(value)) return <span className="sla-cell-success">0</span>;
    return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
  };

  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

  export const TableConfig = (t) => ({
    SV: {
      
      
      inboxColumns: (props) => [
      
        {
          Header: t("SV_APPLICATION_NUMBER"),
          Cell: ({ row }) => {
            return (
              <div>
                <span className="link">
                  
                  <Link to={`${props.parentRoute}/petservice/application-details/` + `${row?.original?.searchData?.["applicationNumber"]}`}>

                    {row.original?.searchData?.["applicationNumber"]}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNumber"]),
        },
        
        {
          Header: t("SV_APPLICANT_NAME"),
          Cell: ( row ) => {
          
            return GetCell(`${row?.cell?.row?.original?.searchData?.["applicantName"]}`)
            
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicantName"]),
          
        },
        {
          Header: t("SV_PET_TYPE"),
          Cell: ({ row }) => {
            return GetCell(`${row.original?.searchData?.petDetails?.["petType"]}`);
           
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.petDetails?.["petType"]),
        },

        {
          Header: t("SV_BREED_TYPE"),
          Cell: ({ row }) => {
            return GetCell(`${row.original?.searchData?.petDetails?.["breedType"]}`);
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.petDetails?.["breedType"]),
        },

        
        {
          Header: t("SV_STATUS"),
          Cell: ({ row }) => {
            
            const wf = row.original?.workflowData;
            return GetCell(t(`${row?.original?.workflowData?.state?.["applicationStatus"]}`));


          },
          mobileCell: (original) => GetMobCell(t(`ES_SV_COMMON_STATUS_${original?.workflowData?.state?.["applicationStatus"]}`)),
        

        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("SV_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,

      
    },
  });

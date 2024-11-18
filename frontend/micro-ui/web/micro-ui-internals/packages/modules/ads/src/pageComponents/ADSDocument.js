import { Loader,PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { pdfDownloadLink } from "../utils";

/* 
  ADSDocument Component

  This component is used for uploading and displaying documents.
  It fetches documents based on the provided type.
*/



function ADSDocument({ value = {}, Code, index,showFileName= false }) {
  const { t } = useTranslation();
  const { isLoading, isError, error, data } = Digit.Hooks.ads.useADSDocumentSearch(
    { value, },
    { value },
    Code,
    index
  );
  const PDFSvg = () => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect width="24" height="24" rx="4" fill="#D32F2F"/>
      <text x="0" y="16" font-family="Arial, sans-serif" font-size="12" font-weight="bold" fill="#FFFFFF">PDF</text>
    </svg>
  );

  const documents = value?.documents
    ? value.documents.documents.filter(doc => doc.documentType === Code).map(doc => ({ ...doc, documentType: doc.documentType.replace(/\./g, '_') }))
    : value.filter(doc => doc.documentType === Code).map(doc => ({ ...doc, documentType: doc.documentType.replace(/\./g, '_') }));
  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
      <React.Fragment>
        <div style={{ display: "flex", flexWrap: "wrap" }}>
          {documents.map((document, index) => {
            let documentLink = pdfDownloadLink(data.pdfFiles, document.fileStoreId);
            return (
              <a target="_" href={documentLink} style={{ minWidth: "160px" }} key={index}>
                 <PDFSvg /* width={85} height={100} style={{ background: "#f6f6f6", padding: "8px" }}  *//>
               {/*  <p style={{ marginTop: "8px" }}>{pdfDocumentName(documentLink, index)}</p> */}
               { showFileName ? <p style={{ marginTop: "8px" }}>{t(Code?.split('.').slice(0,3).join('_'))}</p> : null}
              </a>
            );
          })}
        </div>
      </React.Fragment>
    </div>
  );
}

export default ADSDocument;

import React, { useState, useEffect } from "react";
import { CardLabelError, Loader } from "@egovernments/digit-ui-react-components";
import { Dropdown, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const WmsCMVendorClass = ({ t, config, onSelect, formData = {}, userType }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { pathname: url } = useLocation();
  const editScreen = url.includes("/modify-application/");
  const { data: citizenTypes = [], isLoading } = Digit.Hooks.hrms.useHrmsMDMS(tenantId, "egov-hrms", "EmployeeType") || {};
  const [citizenType, setcitizenType] = useState(formData?.WmsCMVendorClass);
  const [isTrue, setisTrue] = useState(false);
  function SelectcitizenType(value) {
  if(!value?.name){setisTrue(true)}else{setisTrue(false);setcitizenType(value);}
  }

  useEffect(() => {
    onSelect(config.key, citizenType);
  }, [citizenType]);

  const vendorClass = [
    {
        "code": "CONTRACT_MASTER_CLASSA",
        "name": "CLASS A",
        "module": "rainmaker-tl",
        "locale": "en_IN"
    },
    {
        "code": "CONTRACT_MASTER_CLASSB",
        "name": "CLASS B",
        "module": "rainmaker-tl",
        "locale": "en_IN"
    },
    {
        "code": "CONTRACT_MASTER_CLASSC",
        "name": "CLASS C",
        "module": "rainmaker-tl",
        "locale": "en_IN"
    }
];
  const inputs = [
    {
      label: "Vendor Class",
      type: "text",
      name: "vendor_class",
      validation: {
        isRequired: true,
      },
      isMandatory: true,
    },
  ];

  if (isLoading) {
    return <Loader />;
  }

  return inputs?.map((input, index) => {
    return (
      <LabelFieldPair key={index}>
        <CardLabel className="card-label-smaller">
          {t(input.label)}
          {input.isMandatory ? " * " : null}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={citizenType}
          // option={citizenTypes?.["egov-hrms"]?.EmployeeType}
          option={vendorClass}
          onBlur={SelectcitizenType}
          select={SelectcitizenType}
          optionKey="name"
          defaultValue={undefined}
          t={t}
        />
            {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Require Field")}</CardLabelError>}

      </LabelFieldPair>
    );
  });
};

export default WmsCMVendorClass;






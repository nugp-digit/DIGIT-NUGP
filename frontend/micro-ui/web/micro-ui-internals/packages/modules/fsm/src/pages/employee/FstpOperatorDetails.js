import React, { Fragment, useState, useEffect } from "react";
import { useParams, useHistory } from "react-router-dom";
import TimePicker from "react-time-picker";
import { Dropdown } from "@egovernments/digit-ui-react-components";
import {
  Card,
  CardLabel,
  CardLabelError,
  DetailsCard,
  TextInput,
  ActionBar,
  SubmitBar,
  Loader,
  Toast,
  StatusTable,
  Row,
  LabelFieldPair,
  Menu
} from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import CustomTimePicker from "../../components/CustomTimePicker";
import ActionModal from "./ApplicationDetails/Modal/index";

const config = {
  select: (data) => {
    return data.vehicleTrip[0];
  },
};

const totalconfig = {
  select: (data) => {
    return data.vehicleTrip;
  },
};

const FstpOperatorDetails = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const queryClient = useQueryClient();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  let { id: applicationNos } = useParams();
  const [filters, setFilters] = useState({ applicationNos });
  const [isVehicleSearchCompleted, setIsVehicleSearchCompleted] = useState(false);
  const [searchParams, setSearchParams] = useState({});
  const [showToast, setShowToast] = useState(null);
  const [wasteCollected, setWasteCollected] = useState(null);
  const [errors, setErrors] = useState({});
  const [tripStartTime, setTripStartTime] = useState(null);
  const [tripTime, setTripTime] = useState(() => {
    const today = new Date();
    const hour = (today.getHours() < 10 ? "0" : "") + today.getHours();
    const minutes = (today.getMinutes() < 10 ? "0" : "") + today.getMinutes();
    return `${hour}:${minutes}`;
  });
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [tripNo, setTripNo] = useState();
  const [appId, setAppId] = useState();
  const [filterVehicle, setFilterVehicle] = useState();

  const { isLoading: totalload, isSuccess: totalsuccess, data: totalvehicle } = Digit.Hooks.fsm.useVehicleSearch({ tenantId, totalconfig });
  const { isLoading, isSuccess, data: vehicle } = Digit.Hooks.fsm.useVehicleSearch({ tenantId, filters, config });
  const { isLoading: isSearchLoading, isIdle, data: { data: { table: tripDetails } = {} } = {} } = Digit.Hooks.fsm.useSearchAll(tenantId, searchParams, null, {
    enabled: !!isVehicleSearchCompleted,
  });

  const currentTrip = filterVehicle?.length == 0 ? 1 : (tripNo - filterVehicle?.length) + 1


  const workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: applicationNos,
    moduleCode: "FSM_VEHICLE_TRIP",
    role: "FSM_EMP_FSTPO"
  });

  const mutation = Digit.Hooks.fsm.useVehicleUpdate(tenantId);

  useEffect(() => {
    if (isSuccess) {
      setWasteCollected(vehicle?.vehicle?.tankCapacity);
      const applicationNos = vehicle?.tripDetails?.map((tripData) => tripData.referenceNo).join(",");
      setSearchParams({ applicationNos });
      setIsVehicleSearchCompleted(true);
    }
  }, [isSuccess]);

  useEffect(() => {
    if (totalsuccess) {
      const temp = totalvehicle?.vehicleTrip?.filter((c, i, r) => c?.tripDetails[0]?.referenceNo === appId && c?.applicationStatus === "WAITING_FOR_DISPOSAL");
      setFilterVehicle(temp)
    }
  }, [totalsuccess]);

  useEffect(() => {
    if (!isIdle && !isSearchLoading && tripDetails) {
      setTripNo(tripDetails[0]?.noOfTrips)
      setAppId(tripDetails[0].applicationNo)
    }
  }, [isSearchLoading, isIdle]);



  useEffect(() => {
    switch (selectedAction) {
      case "DECLINEVEHICLE":
        return setShowModal(true);
      case "DISPOSE":
        setSelectedAction(null)
        return handleSubmit()
      default:
        setSelectedAction()
        console.debug("default case");
        break;
    }
  }, [selectedAction]);

  const handleSubmit = () => {
    const wasteCombined = tripDetails.reduce((acc, trip) => acc + trip.volume, 0);
    if (!wasteCollected || wasteCollected > wasteCombined || wasteCollected > vehicle.vehicle.tankCapacity) {
      setErrors({ wasteRecieved: "ES_FSTP_INVALID_WASTE_AMOUNT" });
      return;
    }
    if (tripStartTime === null) {
      setErrors({ tripStartTime: "ES_FSTP_INVALID_START_TIME" });
      return;
    }

    if (tripTime === null) {
      setErrors({ tripTime: "ES_FSTP_INVALID_TRIP_TIME" });
      return;
    }

    if (tripStartTime === tripTime || tripStartTime > tripTime) {
      setErrors({ tripTime: "ES_FSTP_INVALID_TRIP_TIME" });
      return;
    }

    setErrors({});

    const d = new Date();
    const timeStamp = Date.parse(new Date(d.toString().split(":")[0].slice(0, -2) + tripTime)) / 1000;
    const tripStartTimestamp = Date.parse(new Date(d.toString().split(":")[0].slice(0, -2) + tripStartTime)) / 1000;
    const tripDetail = { tripNo: currentTrip }
    vehicle.tripStartTime = tripStartTimestamp;
    vehicle.fstpEntryTime = tripStartTimestamp;
    vehicle.tripEndTime = timeStamp;
    vehicle.fstpExitTime = timeStamp;
    vehicle.volumeCarried = wasteCollected;
    vehicle.tripDetails[0].additionalDetails = tripDetail
    const details = {
      vehicleTrip: [vehicle],
      workflow: {
        action: "DISPOSE",
      },
    };

    mutation.mutate(details, {
      onSuccess: handleSuccess,
    });
  };

  const handleDecline = (data) => {
    vehicle.additionalDetails = {
      comments: data?.workflow?.comments,
      vehicleDeclineReason: data?.workflow?.fstpoRejectionReason
    };
    const details = {
      vehicleTrip: [vehicle],
      workflow: {
        action: "DECLINEVEHICLE",
      },
    };

    mutation.mutate(details, {
      onSuccess: handleSuccess,
    });
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const handleSuccess = () => {
    if (selectedAction === "DECLINEVEHICLE") {
      setShowModal(false)
    }
    /* Show Toast on success */
    queryClient.invalidateQueries("FSM_VEHICLE_DATA");
    setShowToast({ key: "success", action: `ES_FSM_DISPOSE_UPDATE_SUCCESS` });
    setTimeout(() => {
      closeToast();
      history.push(`/digit-ui/employee/fsm/fstp-inbox`);
    }, 5000);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    if (name === "tripTime") {
      setTripTime(value);
    } else if (name === "wasteRecieved") {
      setWasteCollected(value);
    }
  };

  if (isLoading) {
    return <Loader />;
  }

  const vehicleData = [
    {
      title: t("ES_INBOX_VEHICLE_LOG"),
      value: vehicle.applicationNo,
    },
    {
      title: t("ES_INBOX_DSO_NAME"),
      value: vehicle.tripOwner.name,
    },
    {
      title: t("ES_INBOX_VEHICLE_NO"),
      value: vehicle.vehicle?.registrationNumber,
    },
    {
      title: `${t("ES_VEHICLE CAPACITY")}`,
      value: vehicle.vehicle.tankCapacity,
    },
  ];

  const handleTimeChange = (value, cb) => {
    if (typeof value === 'string') {
      cb(value);
    }
  }

  function onActionSelect(action) {
    setSelectedAction(action);
    setDisplayMenu(false);
  }

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  return (
    <div>
      <Card>
        <StatusTable>
          {vehicleData?.map((row, index) => (
            <Row key={row.title} label={row.title} text={row.value || "N/A"} last={false} />
          ))}
          <CardLabelError>{t(errors.tripStartTime)}</CardLabelError>
          <form>
            <Row
              key={t("ES_VEHICLE_IN_TIME")}
              label={`${t("ES_VEHICLE_IN_TIME")} * `}
              rowContainerStyle={{ marginBottom: "32px" }}
              text={
                <div>
                  <CustomTimePicker name="tripStartTime" onChange={val => handleTimeChange(val, setTripStartTime)} value={tripStartTime} />
                </div>
              }
            />
            <CardLabelError>{t(errors.wasteRecieved)}</CardLabelError>
            <Row
              key={t("ES_VEHICLE_SEPTAGE_DUMPED")}
              label={`${t("ES_VEHICLE_SEPTAGE_DUMPED")} * `}
              text={
                <div>
                  <TextInput
                    type="number"
                    name="wasteRecieved"
                    value={wasteCollected}
                    onChange={handleChange}
                    style={{ width: "100%", maxWidth: "200px" }}
                  />
                </div>
              }
            />
            <CardLabelError>{t(errors.tripTime)}</CardLabelError>
            <Row
              key={t("ES_VEHICLE_OUT_TIME")}
              label={`${t("ES_VEHICLE_OUT_TIME")} * `}
              text={
                <div>
                  <CustomTimePicker name="tripTime" onChange={val => handleTimeChange(val, setTripTime)} value={tripTime} />
                </div>
              }
            />
            {!isSearchLoading && !isIdle && tripDetails ?
              <Row
                key={t("ES_VEHICLE_TRIP_NO")}
                label={`${t("ES_VEHICLE_TRIP_NO")} * `}
                text={
                  <div>
                    <Dropdown
                      disable
                      selected={{ "name": `${currentTrip} of ${tripDetails[0]?.noOfTrips}` }}
                      t={t}
                      optionKey="name"
                      style={{ maxWidth: '200px' }} />
                  </div>
                }
              >
              </Row> : null}
            {!workflowDetails?.isLoading && workflowDetails?.data?.nextActions?.length > 0 && (
              <ActionBar>
                {displayMenu && workflowDetails?.data?.nextActions ? (
                  <Menu
                    localeKeyPrefix={""}
                    options={workflowDetails?.data?.nextActions.map((action) => action.action)}
                    t={t}
                    onSelect={onActionSelect}
                  />
                ) : null}
                <SubmitBar label={t("ES_COMMON_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
              </ActionBar>
            )}
          </form>
          {showModal ? (
            <ActionModal
              t={t}
              action={selectedAction}
              tenantId={tenantId}
              state={state}
              id={applicationNos}
              closeModal={closeModal}
              submitAction={handleDecline}
              actionData={workflowDetails?.data?.timeline}
            />
          ) : null}
          {/* <LabelFieldPair>
            <CardLabel>{t("ES_VEHICLE_WASTE_RECIEVED")}</CardLabel>
            <div className="field-container">
              <TextInput name="wasteRecieved" value={wasteCollected} onChange={handleChange} />
            </div>
            {errors.wasteRecieved && <CardLabelError>{t(errors.wasteRecieved)}</CardLabelError>}
          </LabelFieldPair>
          <LabelFieldPair>
            <CardLabel>{t("ES_COMMON_TIME")}</CardLabel>
            <div>
              <TimePicker
                className="time-picker"
                name="tripTime"
                onChange={setTripTime}
                value={tripTime}
                locale="en-US"
                format="hh:mm a"
                clearIcon={null}
              />
            </div>
          </LabelFieldPair> */}
        </StatusTable>
      </Card>
      <h2 style={{ fontWeight: "bold", fontSize: "16px", marginLeft: "8px", marginTop: "16px" }}>{t("ES_FSTP_OPERATOR_DETAILS_WASTE_GENERATORS")}</h2>
      {isSearchLoading || isIdle ? (
        <Loader />
      ) : (
        <Card>
          <StatusTable>
            {tripDetails?.map((trip, index) => {
              return (
                <>
                  <Row key={index} label={t("CS_FILE_DESLUDGING_APPLICATION_NO")} text={trip.applicationNo} />
                  <Row
                    key={index}
                    label={t("ES_INBOX_LOCALITY")}
                    text={t(`${trip?.tenantId?.toUpperCase()?.split(".")?.join("_")}_REVENUE_${trip?.address?.locality?.code}`)}
                  />
                  <Row key={index} label={t("ES_USAGE")} text={t(`PROPERTYTYPE_MASTERS_${trip.propertyUsage}`)} />
                  <Row key={index} label={t("ES_WASTE_RECIEVED")} text={vehicle.tripDetails[index].volume} />
                </>
              );
            })}
          </StatusTable>
        </Card>
      )}
      {showToast && (
        <Toast
          error={showToast.key === "error" ? true : false}
          label={t(showToast.key === "success" ? showToast.action : `ES_FSM_DISPOSE_UPDATE_FAILURE`)}
          onClose={closeToast}
        />
      )}
    </div>
  );
};

export default FstpOperatorDetails;
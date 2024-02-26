import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import Hamburger from "./Hamburger";
import { NotificationBell } from "./svgindex";
import { useLocation } from "react-router-dom";
import BackButton from "./BackButton";

const TopBar = ({
  img,
  isMobile,
  logoUrl,
  onLogout,
  toggleSidebar,
  ulb,
  userDetails,
  notificationCount,
  notificationCountLoaded,
  cityOfCitizenShownBesideLogo,
  onNotificationIconClick,
  hideNotificationIconOnSomeUrlsWhenNotLoggedIn,
  changeLanguage,
}) => {
  const { pathname } = useLocation();

  // const showHaburgerorBackButton = () => {
  //   if (pathname === "/digit-ui/citizen" || pathname === "/digit-ui/citizen/" || pathname === "/digit-ui/citizen/select-language") {
  //     return <Hamburger handleClick={toggleSidebar} />;
  //   } else {
  //     return <BackButton className="top-back-btn" />;
  //   }
  // };
  console.log("hambuger-back-wrapper==",cityOfCitizenShownBesideLogo)
  return (
    <div className="navbar">
      <div className="center-container back-wrapper" style={{display:"flex",marginRight:"2rem",marginLeft:"2rem",justifyContent:"space-between"}}>
        <div className="hambuger-back-wrapper" style={{display:"flex"}}>
          {window.innerWidth <= 660  && <Hamburger handleClick={toggleSidebar} />}
          <a style={{display: 'inline-flex'}} href={window.location.href.includes("citizen")?"/digit-ui/citizen":"/digit-ui/employee"}><img
            className="city"
            id="topbar-logo"
            src={"http://216.48.176.229/static/4.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
            // {"https://in-egov-assets.s3.ap-south-1.amazonaws.com/images/Upyog-logo.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
            alt="UPYOG"
          />
           <div className='col-sm-6 header-txt' style={{marginLeft: 15}}>
              <span style={{fontSize: 16, fontWeight: 600}}>Property Tax Portal</span>
              <div style={{fontSize: 14}}>Government of Manipur</div>
            </div>
          {/* <span>Property Tax - Manipur</span> */}
          </a>
          <h3>{cityOfCitizenShownBesideLogo}</h3>
        </div>

        <div className="RightMostTopBarOptions">
          {/* {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? changeLanguage : null} */}
          {!hideNotificationIconOnSomeUrlsWhenNotLoggedIn ? (
            <div className="EventNotificationWrapper" onClick={onNotificationIconClick}>
              {notificationCountLoaded && notificationCount ? (
                <span>
                  <p>{notificationCount}</p>
                </span>
              ) : null}
              <NotificationBell />
            </div>
          ) : null}
          <h3></h3>
          <img
          className="city"
          id="topbar-logo" 
          src={"http://216.48.176.229/static/8.png" || "https://cdn.jsdelivr.net/npm/@egovernments/digit-ui-css@1.0.7/img/m_seva_white_logo.png"}
          alt="mSeva"
          style={{marginLeft:"10px"}}
        />
        </div>
      </div>
    </div>
  );
};

TopBar.propTypes = {
  img: PropTypes.string,
};

TopBar.defaultProps = {
  img: undefined,
};

export default TopBar;

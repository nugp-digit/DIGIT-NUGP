package org.egov.api.adapter;

import static org.egov.eventnotification.constants.Constants.DOUBLE_DEFAULT;
import static org.egov.eventnotification.constants.Constants.EMPTY;
import static org.egov.eventnotification.constants.Constants.EVENT_ADDRESS;
import static org.egov.eventnotification.constants.Constants.EVENT_COST;
import static org.egov.eventnotification.constants.Constants.EVENT_DESC;
import static org.egov.eventnotification.constants.Constants.EVENT_ENDDATE;
import static org.egov.eventnotification.constants.Constants.EVENT_ENDTIME;
import static org.egov.eventnotification.constants.Constants.EVENT_EVENTTYPE;
import static org.egov.eventnotification.constants.Constants.EVENT_FILENAME;
import static org.egov.eventnotification.constants.Constants.EVENT_FILESTOREID;
import static org.egov.eventnotification.constants.Constants.EVENT_HOST;
import static org.egov.eventnotification.constants.Constants.EVENT_ID;
import static org.egov.eventnotification.constants.Constants.EVENT_ISPAID;
import static org.egov.eventnotification.constants.Constants.EVENT_LOCATION;
import static org.egov.eventnotification.constants.Constants.EVENT_NAME;
import static org.egov.eventnotification.constants.Constants.EVENT_STARTDATE;
import static org.egov.eventnotification.constants.Constants.EVENT_STARTTIME;
import static org.egov.eventnotification.constants.Constants.INTERESTED_COUNT;
import static org.egov.eventnotification.constants.Constants.NO;
import static org.egov.eventnotification.constants.Constants.URL;
import static org.egov.eventnotification.constants.Constants.USER_INTERESTED;

import java.lang.reflect.Type;

import org.egov.eventnotification.entity.Event;
import org.egov.infra.utils.DateUtils;
import org.joda.time.DateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class EventAdapter extends DataAdapter<Event> {

    @Override
    public JsonElement serialize(Event event, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObjectEvent = new JsonObject();
        jsonObjectEvent.addProperty(EVENT_ID, event.getId());
        jsonObjectEvent.addProperty(EVENT_NAME, event.getName());
        jsonObjectEvent.addProperty(EVENT_DESC, event.getDescription());
        DateTime sd = new DateTime(event.getStartDate());
        jsonObjectEvent.addProperty(EVENT_STARTDATE, DateUtils.getDefaultFormattedDate(event.getStartDate()));
        jsonObjectEvent.addProperty(EVENT_STARTTIME, sd.getHourOfDay() + ":" + sd.getMinuteOfHour());
        DateTime ed = new DateTime(event.getEndDate());
        jsonObjectEvent.addProperty(EVENT_ENDDATE, DateUtils.getDefaultFormattedDate(event.getEndDate()));
        jsonObjectEvent.addProperty(EVENT_ENDTIME, ed.getHourOfDay() + ":" + ed.getMinuteOfHour());
        jsonObjectEvent.addProperty(EVENT_HOST, event.getEventhost());
        jsonObjectEvent.addProperty(EVENT_LOCATION, event.getEventlocation());
        jsonObjectEvent.addProperty(EVENT_ADDRESS, event.getAddress());
        jsonObjectEvent.addProperty(EVENT_ISPAID, event.isIspaid());
        jsonObjectEvent.addProperty(EVENT_EVENTTYPE, event.getEventType());
        jsonObjectEvent.addProperty(INTERESTED_COUNT, EMPTY);
        if (event.getFilestore() == null) {
            jsonObjectEvent.addProperty(EVENT_FILESTOREID, EMPTY);
            jsonObjectEvent.addProperty(EVENT_FILENAME, EMPTY);
        } else {
            jsonObjectEvent.addProperty(EVENT_FILESTOREID, event.getFilestore().getId());
            jsonObjectEvent.addProperty(EVENT_FILENAME, event.getFilestore().getFileName());
        }

        if (event.getCost() == null)
            jsonObjectEvent.addProperty(EVENT_COST, DOUBLE_DEFAULT);
        else
            jsonObjectEvent.addProperty(EVENT_COST, event.getCost());

        if (event.getUrl() == null)
            jsonObjectEvent.addProperty(URL, EMPTY);
        else
            jsonObjectEvent.addProperty(URL, event.getUrl());

        jsonObjectEvent.addProperty(USER_INTERESTED, NO);

        return jsonObjectEvent;
    }
}

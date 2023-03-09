package org.ksmart.birth.web.model.outsidecountry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthOutsideDetailRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<BirthOutsideApplication> newBirthDetails;

    public BirthOutsideDetailRequest addKsmartBirthDetails(BirthOutsideApplication birthDetail) {
        if (newBirthDetails == null) {
            newBirthDetails = null;
        }
        return this;
    }
}

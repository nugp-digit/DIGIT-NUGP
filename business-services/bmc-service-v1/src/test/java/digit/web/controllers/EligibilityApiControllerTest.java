package digit.web.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import digit.TestConfiguration;

/**
* API tests for EligibilityApiController
*/
@Disabled
@WebMvcTest(EligibilityApiController.class)
@Import(TestConfiguration.class)
class EligibilityApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void eligibilityCheckPostSuccess() throws Exception {
        mockMvc.perform(post("/eligibility/check").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    void eligibilityCheckPostFailure() throws Exception {
        mockMvc.perform(post("/eligibility/check").contentType(MediaType
        .APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

}
package com.websiteroute.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.websiteroute.application.PaymentActivityService;

@WebMvcTest(PaymentActivityController.class)
@Import(PaymentActivityService.class)
class PaymentActivityControllerTest {
    @Autowired MockMvc mockMvc;

    @Test
    void returnsCompletedFiftyUsdPayment() throws Exception {
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount", is(50.00)))
                .andExpect(jsonPath("$[0].currency", is("USD")))
                .andExpect(jsonPath("$[0].status", is("completed")));
    }
}
